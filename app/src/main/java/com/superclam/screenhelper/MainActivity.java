package com.superclam.screenhelper;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    
    private static final int REQUEST_CODE_PERMISSIONS = 1000;
    private static final int REQUEST_CODE_MEDIA_PROJECTION = 1001;
    private static final int REQUEST_CODE_OVERLAY = 1002;
    
    private Button btnStartService;
    private Button btnStopService;
    private Button btnTakeScreenshot;
    private Switch switchBypassDetection;
    private TextView tvStatus;
    
    private DetectionBypassHelper bypassHelper;
    private VirtualDisplayHelper displayHelper;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        initHelpers();
        checkPermissions();
    }
    
    private void initViews() {
        btnStartService = findViewById(R.id.btn_start_service);
        btnStopService = findViewById(R.id.btn_stop_service);
        btnTakeScreenshot = findViewById(R.id.btn_take_screenshot);
        switchBypassDetection = findViewById(R.id.switch_bypass_detection);
        tvStatus = findViewById(R.id.tv_status);
        
        btnStartService.setOnClickListener(v -> startScreenshotService());
        btnStopService.setOnClickListener(v -> stopScreenshotService());
        btnTakeScreenshot.setOnClickListener(v -> takeScreenshot());
        
        switchBypassDetection.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                enableBypassMode();
            } else {
                disableBypassMode();
            }
        });
    }
    
    private void initHelpers() {
        bypassHelper = new DetectionBypassHelper(this);
        displayHelper = new VirtualDisplayHelper(this);
    }
    
    private void checkPermissions() {
        String[] permissions = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        };
        
        boolean needRequest = false;
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) 
                != PackageManager.PERMISSION_GRANTED) {
                needRequest = true;
                break;
            }
        }
        
        if (needRequest) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSIONS);
        }
        
        // 检查悬浮窗权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE_OVERLAY);
            }
        }
    }
    
    private void startScreenshotService() {
        MediaProjectionManager manager = (MediaProjectionManager) 
            getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        Intent intent = manager.createScreenCaptureIntent();
        startActivityForResult(intent, REQUEST_CODE_MEDIA_PROJECTION);
    }
    
    private void stopScreenshotService() {
        Intent intent = new Intent(this, ScreenshotService.class);
        stopService(intent);
        updateStatus("服务已停止");
    }
    
    private void takeScreenshot() {
        Intent intent = new Intent(this, ScreenshotService.class);
        intent.setAction("TAKE_SCREENSHOT");
        startService(intent);
    }
    
    private void enableBypassMode() {
        if (bypassHelper.enableBypass()) {
            updateStatus("检测绕过模式已启用");
            displayHelper.createVirtualDisplay();
        } else {
            updateStatus("启用绕过模式失败");
            switchBypassDetection.setChecked(false);
        }
    }
    
    private void disableBypassMode() {
        bypassHelper.disableBypass();
        displayHelper.destroyVirtualDisplay();
        updateStatus("检测绕过模式已关闭");
    }
    
    private void updateStatus(String status) {
        tvStatus.setText("状态: " + status);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == REQUEST_CODE_MEDIA_PROJECTION) {
            if (resultCode == Activity.RESULT_OK) {
                Intent intent = new Intent(this, ScreenshotService.class);
                intent.putExtra("resultCode", resultCode);
                intent.putExtra("data", data);
                startService(intent);
                updateStatus("截屏服务已启动");
            } else {
                updateStatus("用户拒绝了截屏权限");
            }
        } else if (requestCode == REQUEST_CODE_OVERLAY) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    updateStatus("悬浮窗权限已获取");
                } else {
                    updateStatus("需要悬浮窗权限才能正常工作");
                }
            }
        }
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (allGranted) {
                updateStatus("权限已获取");
            } else {
                updateStatus("需要存储权限才能保存截图");
            }
        }
    }
}
