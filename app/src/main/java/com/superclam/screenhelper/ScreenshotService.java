package com.superclam.screenhelper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 截屏服务
 * 提供后台截屏功能，支持绕过检测的截屏
 */
public class ScreenshotService extends Service {
    
    private static final String TAG = "ScreenshotService";
    private static final String CHANNEL_ID = "ScreenshotServiceChannel";
    private static final int NOTIFICATION_ID = 1001;
    
    private MediaProjectionManager projectionManager;
    private MediaProjection mediaProjection;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;
    
    private int screenWidth;
    private int screenHeight;
    private int screenDensity;
    
    private Handler mainHandler;
    private VirtualDisplayHelper displayHelper;
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        mainHandler = new Handler(Looper.getMainLooper());
        displayHelper = new VirtualDisplayHelper(this);
        
        projectionManager = (MediaProjectionManager) getSystemService(MEDIA_PROJECTION_SERVICE);
        initScreenMetrics();
        createNotificationChannel();
        
        Log.d(TAG, "截屏服务已创建");
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            
            if ("TAKE_SCREENSHOT".equals(action)) {
                takeScreenshot();
            } else {
                // 启动媒体投影
                int resultCode = intent.getIntExtra("resultCode", -1);
                Intent data = intent.getParcelableExtra("data");
                
                if (resultCode != -1 && data != null) {
                    startMediaProjection(resultCode, data);
                }
            }
        }
        
        startForeground(NOTIFICATION_ID, createNotification());
        return START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopMediaProjection();
        Log.d(TAG, "截屏服务已销毁");
    }
    
    /**
     * 初始化屏幕参数
     */
    private void initScreenMetrics() {
        WindowManager windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getRealMetrics(metrics);
        
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        screenDensity = metrics.densityDpi;
        
        Log.d(TAG, String.format("屏幕参数: %dx%d, density=%d", screenWidth, screenHeight, screenDensity));
    }
    
    /**
     * 创建通知渠道
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "截屏服务",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("提供后台截屏功能");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }
    
    /**
     * 创建通知
     */
    private Notification createNotification() {
        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("ScreenHelper")
            .setContentText("截屏服务正在运行")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build();
    }
    
    /**
     * 启动媒体投影
     */
    private void startMediaProjection(int resultCode, Intent data) {
        try {
            mediaProjection = projectionManager.getMediaProjection(resultCode, data);
            
            if (mediaProjection != null) {
                // 设置回调
                mediaProjection.registerCallback(new MediaProjection.Callback() {
                    @Override
                    public void onStop() {
                        super.onStop();
                        Log.d(TAG, "MediaProjection已停止");
                        stopMediaProjection();
                    }
                }, mainHandler);
                
                // 创建虚拟显示
                createVirtualDisplay();
                
                // 设置到VirtualDisplayHelper
                displayHelper.setMediaProjection(mediaProjection);
                
                Log.d(TAG, "MediaProjection启动成功");
                showToast("截屏服务已准备就绪");
                
            } else {
                Log.e(TAG, "MediaProjection创建失败");
                showToast("截屏服务启动失败");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "启动MediaProjection异常", e);
            showToast("截屏服务启动异常");
        }
    }
    
    /**
     * 创建虚拟显示
     */
    private void createVirtualDisplay() {
        try {
            if (mediaProjection == null) {
                Log.e(TAG, "MediaProjection为空，无法创建虚拟显示");
                return;
            }
            
            // 创建ImageReader
            imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);
            
            // 设置图像可用监听器
            imageReader.setOnImageAvailableListener(new ImageReader.OnImageAvailableListener() {
                @Override
                public void onImageAvailable(ImageReader reader) {
                    // 这里可以处理捕获的图像
                    Log.d(TAG, "图像可用");
                }
            }, mainHandler);
            
            // 创建虚拟显示
            virtualDisplay = mediaProjection.createVirtualDisplay(
                "ScreenHelper_Capture",
                screenWidth,
                screenHeight,
                screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(),
                null,
                mainHandler
            );
            
            if (virtualDisplay != null) {
                Log.d(TAG, "虚拟显示创建成功");
            } else {
                Log.e(TAG, "虚拟显示创建失败");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "创建虚拟显示异常", e);
        }
    }
    
    /**
     * 执行截屏
     */
    private void takeScreenshot() {
        if (imageReader == null) {
            showToast("截屏服务未准备就绪");
            return;
        }
        
        try {
            Image image = imageReader.acquireLatestImage();
            if (image != null) {
                // 转换为Bitmap
                Bitmap bitmap = imageToBitmap(image);
                image.close();
                
                if (bitmap != null) {
                    // 保存截图
                    String fileName = saveScreenshot(bitmap);
                    if (fileName != null) {
                        showToast("截图已保存: " + fileName);
                        Log.d(TAG, "截图保存成功: " + fileName);
                    } else {
                        showToast("截图保存失败");
                        Log.e(TAG, "截图保存失败");
                    }
                    bitmap.recycle();
                } else {
                    showToast("截图转换失败");
                    Log.e(TAG, "图像转换为Bitmap失败");
                }
            } else {
                showToast("无法获取屏幕图像");
                Log.w(TAG, "无法获取屏幕图像");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "截屏异常", e);
            showToast("截屏失败: " + e.getMessage());
        }
    }
    
    /**
     * 将Image转换为Bitmap
     */
    private Bitmap imageToBitmap(Image image) {
        try {
            Image.Plane[] planes = image.getPlanes();
            ByteBuffer buffer = planes[0].getBuffer();
            int pixelStride = planes[0].getPixelStride();
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * screenWidth;
            
            Bitmap bitmap = Bitmap.createBitmap(
                screenWidth + rowPadding / pixelStride,
                screenHeight,
                Bitmap.Config.ARGB_8888
            );
            bitmap.copyPixelsFromBuffer(buffer);
            
            return bitmap;
            
        } catch (Exception e) {
            Log.e(TAG, "Image转Bitmap异常", e);
            return null;
        }
    }
    
    /**
     * 保存截图
     */
    private String saveScreenshot(Bitmap bitmap) {
        try {
            // 创建保存目录
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "ScreenHelper");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            
            // 生成文件名
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
            String fileName = "Screenshot_" + timeStamp + ".png";
            File file = new File(dir, fileName);
            
            // 保存文件
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
            
            return fileName;
            
        } catch (IOException e) {
            Log.e(TAG, "保存截图异常", e);
            return null;
        }
    }
    
    /**
     * 停止媒体投影
     */
    private void stopMediaProjection() {
        try {
            if (virtualDisplay != null) {
                virtualDisplay.release();
                virtualDisplay = null;
            }
            
            if (imageReader != null) {
                imageReader.close();
                imageReader = null;
            }
            
            if (mediaProjection != null) {
                mediaProjection.stop();
                mediaProjection = null;
            }
            
            Log.d(TAG, "MediaProjection已停止");
            
        } catch (Exception e) {
            Log.e(TAG, "停止MediaProjection异常", e);
        }
    }
    
    /**
     * 显示Toast消息
     */
    private void showToast(String message) {
        mainHandler.post(() -> Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
    }
}
