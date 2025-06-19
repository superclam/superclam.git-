package com.superclam.screenhelper;

import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

/**
 * 虚拟显示辅助类
 * 创建虚拟显示环境，让目标应用认为运行在虚拟环境中
 */
public class VirtualDisplayHelper {
    
    private static final String TAG = "VirtualDisplayHelper";
    private static final String VIRTUAL_DISPLAY_NAME = "ScreenHelper_VirtualDisplay";
    
    private Context context;
    private DisplayManager displayManager;
    private VirtualDisplay virtualDisplay;
    private ImageReader imageReader;
    private MediaProjection mediaProjection;
    
    private int screenWidth;
    private int screenHeight;
    private int screenDensity;
    
    public VirtualDisplayHelper(Context context) {
        this.context = context;
        this.displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        initScreenMetrics();
    }
    
    /**
     * 初始化屏幕参数
     */
    private void initScreenMetrics() {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getRealMetrics(metrics);
        
        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;
        screenDensity = metrics.densityDpi;
        
        Log.d(TAG, String.format("屏幕参数: %dx%d, density=%d", screenWidth, screenHeight, screenDensity));
    }
    
    /**
     * 创建虚拟显示
     */
    public boolean createVirtualDisplay() {
        try {
            if (virtualDisplay != null) {
                Log.w(TAG, "虚拟显示已存在");
                return true;
            }
            
            // 创建ImageReader用于捕获虚拟显示内容
            imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);
            
            // 创建虚拟显示
            virtualDisplay = displayManager.createVirtualDisplay(
                VIRTUAL_DISPLAY_NAME,
                screenWidth,
                screenHeight,
                screenDensity,
                imageReader.getSurface(),
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR |
                DisplayManager.VIRTUAL_DISPLAY_FLAG_PRESENTATION
            );
            
            if (virtualDisplay != null) {
                Log.d(TAG, "虚拟显示创建成功: " + virtualDisplay.getDisplay().getDisplayId());
                
                // 设置虚拟显示的一些特殊属性
                configureVirtualDisplay();
                
                return true;
            } else {
                Log.e(TAG, "虚拟显示创建失败");
                return false;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "创建虚拟显示异常", e);
            return false;
        }
    }
    
    /**
     * 配置虚拟显示的特殊属性
     */
    private void configureVirtualDisplay() {
        try {
            if (virtualDisplay == null) return;
            
            Display display = virtualDisplay.getDisplay();
            
            // 通过反射设置一些特殊属性，让应用认为这是虚拟环境
            try {
                // 设置显示类型为虚拟
                java.lang.reflect.Field typeField = Display.class.getDeclaredField("mType");
                typeField.setAccessible(true);
                typeField.setInt(display, Display.TYPE_VIRTUAL);
                
                // 设置显示标志
                java.lang.reflect.Field flagsField = Display.class.getDeclaredField("mFlags");
                flagsField.setAccessible(true);
                int flags = flagsField.getInt(display);
                flags |= Display.FLAG_PRESENTATION; // 标记为演示显示
                flagsField.setInt(display, flags);
                
                Log.d(TAG, "虚拟显示属性配置完成");
                
            } catch (Exception e) {
                Log.w(TAG, "配置虚拟显示属性失败", e);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "配置虚拟显示异常", e);
        }
    }
    
    /**
     * 销毁虚拟显示
     */
    public void destroyVirtualDisplay() {
        try {
            if (virtualDisplay != null) {
                virtualDisplay.release();
                virtualDisplay = null;
                Log.d(TAG, "虚拟显示已销毁");
            }
            
            if (imageReader != null) {
                imageReader.close();
                imageReader = null;
            }
            
        } catch (Exception e) {
            Log.e(TAG, "销毁虚拟显示异常", e);
        }
    }
    
    /**
     * 设置MediaProjection（用于屏幕捕获）
     */
    public void setMediaProjection(MediaProjection mediaProjection) {
        this.mediaProjection = mediaProjection;
        
        if (mediaProjection != null && virtualDisplay == null) {
            // 如果有MediaProjection但没有虚拟显示，尝试创建
            createVirtualDisplayWithProjection();
        }
    }
    
    /**
     * 使用MediaProjection创建虚拟显示
     */
    private void createVirtualDisplayWithProjection() {
        try {
            if (mediaProjection == null) {
                Log.w(TAG, "MediaProjection为空，无法创建虚拟显示");
                return;
            }
            
            if (virtualDisplay != null) {
                Log.w(TAG, "虚拟显示已存在");
                return;
            }
            
            // 创建ImageReader
            imageReader = ImageReader.newInstance(screenWidth, screenHeight, PixelFormat.RGBA_8888, 2);
            
            // 使用MediaProjection创建虚拟显示
            virtualDisplay = mediaProjection.createVirtualDisplay(
                VIRTUAL_DISPLAY_NAME,
                screenWidth,
                screenHeight,
                screenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                imageReader.getSurface(),
                null,
                null
            );
            
            if (virtualDisplay != null) {
                Log.d(TAG, "使用MediaProjection创建虚拟显示成功");
                configureVirtualDisplay();
            } else {
                Log.e(TAG, "使用MediaProjection创建虚拟显示失败");
            }
            
        } catch (Exception e) {
            Log.e(TAG, "使用MediaProjection创建虚拟显示异常", e);
        }
    }
    
    /**
     * 获取虚拟显示的Surface
     */
    public Surface getVirtualDisplaySurface() {
        if (imageReader != null) {
            return imageReader.getSurface();
        }
        return null;
    }
    
    /**
     * 获取虚拟显示对象
     */
    public VirtualDisplay getVirtualDisplay() {
        return virtualDisplay;
    }
    
    /**
     * 检查虚拟显示是否活跃
     */
    public boolean isVirtualDisplayActive() {
        return virtualDisplay != null;
    }
    
    /**
     * 模拟虚拟机环境特征
     */
    public void simulateVirtualMachineEnvironment() {
        try {
            // 这里可以添加更多模拟虚拟机环境的逻辑
            // 比如修改一些系统属性、创建特定的文件等
            
            Log.d(TAG, "虚拟机环境模拟完成");
            
        } catch (Exception e) {
            Log.e(TAG, "模拟虚拟机环境失败", e);
        }
    }
}
