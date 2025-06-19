package com.superclam.screenhelper;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 检测绕过核心类
 * 通过多种技术手段绕过截屏和分屏检测
 */
public class DetectionBypassHelper {
    
    private static final String TAG = "DetectionBypassHelper";
    
    private Context context;
    private WindowManager windowManager;
    private View overlayView;
    private boolean bypassEnabled = false;
    
    public DetectionBypassHelper(Context context) {
        this.context = context;
        this.windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
    }
    
    /**
     * 启用检测绕过模式
     */
    public boolean enableBypass() {
        try {
            // 方法1: 修改窗口标志，模拟虚拟环境
            modifyWindowFlags();
            
            // 方法2: 创建透明覆盖层，干扰检测机制
            createOverlayLayer();
            
            // 方法3: 修改系统属性，模拟虚拟机环境
            modifySystemProperties();
            
            // 方法4: Hook相关检测方法
            hookDetectionMethods();
            
            bypassEnabled = true;
            Log.d(TAG, "检测绕过模式已启用");
            return true;
            
        } catch (Exception e) {
            Log.e(TAG, "启用绕过模式失败", e);
            return false;
        }
    }
    
    /**
     * 禁用检测绕过模式
     */
    public void disableBypass() {
        try {
            removeOverlayLayer();
            restoreSystemProperties();
            bypassEnabled = false;
            Log.d(TAG, "检测绕过模式已禁用");
        } catch (Exception e) {
            Log.e(TAG, "禁用绕过模式失败", e);
        }
    }
    
    /**
     * 修改窗口标志，移除FLAG_SECURE等安全标志
     */
    private void modifyWindowFlags() {
        try {
            if (context instanceof Activity) {
                Activity activity = (Activity) context;
                
                // 通过反射获取Window对象
                Field windowField = Activity.class.getDeclaredField("mWindow");
                windowField.setAccessible(true);
                Object window = windowField.get(activity);
                
                // 获取WindowManager.LayoutParams
                Method getAttributesMethod = window.getClass().getMethod("getAttributes");
                WindowManager.LayoutParams params = (WindowManager.LayoutParams) getAttributesMethod.invoke(window);
                
                // 清除FLAG_SECURE标志
                params.flags &= ~WindowManager.LayoutParams.FLAG_SECURE;
                
                // 添加一些标志来模拟虚拟环境
                params.flags |= WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
                
                // 应用修改
                Method setAttributesMethod = window.getClass().getMethod("setAttributes", WindowManager.LayoutParams.class);
                setAttributesMethod.invoke(window, params);
                
                Log.d(TAG, "窗口标志修改成功");
            }
        } catch (Exception e) {
            Log.e(TAG, "修改窗口标志失败", e);
        }
    }
    
    /**
     * 创建透明覆盖层
     */
    private void createOverlayLayer() {
        try {
            if (overlayView != null) {
                return;
            }
            
            overlayView = new View(context);
            overlayView.setBackgroundColor(0x00000000); // 完全透明
            
            WindowManager.LayoutParams params = new WindowManager.LayoutParams(
                1, 1, // 最小尺寸
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.O 
                    ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                    : WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE 
                    | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                    | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                PixelFormat.TRANSLUCENT
            );
            
            params.gravity = Gravity.TOP | Gravity.LEFT;
            params.x = -1000; // 移到屏幕外
            params.y = -1000;
            
            windowManager.addView(overlayView, params);
            Log.d(TAG, "覆盖层创建成功");
            
        } catch (Exception e) {
            Log.e(TAG, "创建覆盖层失败", e);
        }
    }
    
    /**
     * 移除覆盖层
     */
    private void removeOverlayLayer() {
        try {
            if (overlayView != null && windowManager != null) {
                windowManager.removeView(overlayView);
                overlayView = null;
                Log.d(TAG, "覆盖层已移除");
            }
        } catch (Exception e) {
            Log.e(TAG, "移除覆盖层失败", e);
        }
    }
    
    /**
     * 修改系统属性，模拟虚拟机环境
     */
    private void modifySystemProperties() {
        try {
            // 通过反射修改一些系统属性，让应用认为运行在虚拟环境中
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method setMethod = systemPropertiesClass.getMethod("set", String.class, String.class);
            
            // 设置一些虚拟机相关的属性
            setMethod.invoke(null, "ro.kernel.qemu", "1");
            setMethod.invoke(null, "ro.hardware", "goldfish");
            setMethod.invoke(null, "ro.product.model", "sdk");
            
            Log.d(TAG, "系统属性修改成功");
        } catch (Exception e) {
            Log.e(TAG, "修改系统属性失败", e);
        }
    }
    
    /**
     * 恢复系统属性
     */
    private void restoreSystemProperties() {
        try {
            Class<?> systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method setMethod = systemPropertiesClass.getMethod("set", String.class, String.class);
            
            // 恢复原始属性（这里简单设置为空）
            setMethod.invoke(null, "ro.kernel.qemu", "");
            setMethod.invoke(null, "ro.hardware", "");
            setMethod.invoke(null, "ro.product.model", "");
            
            Log.d(TAG, "系统属性已恢复");
        } catch (Exception e) {
            Log.e(TAG, "恢复系统属性失败", e);
        }
    }
    
    /**
     * Hook检测相关方法
     */
    private void hookDetectionMethods() {
        try {
            // 这里可以使用Xposed框架或其他Hook技术
            // 由于复杂性，这里只是示例框架
            
            // Hook截屏检测方法
            hookScreenshotDetection();
            
            // Hook分屏检测方法
            hookSplitScreenDetection();
            
            Log.d(TAG, "检测方法Hook成功");
        } catch (Exception e) {
            Log.e(TAG, "Hook检测方法失败", e);
        }
    }
    
    private void hookScreenshotDetection() {
        // 实现截屏检测的Hook逻辑
        // 可以拦截相关的系统调用或API
    }
    
    private void hookSplitScreenDetection() {
        // 实现分屏检测的Hook逻辑
        // 可以修改多窗口模式的检测结果
    }
    
    public boolean isBypassEnabled() {
        return bypassEnabled;
    }
}
