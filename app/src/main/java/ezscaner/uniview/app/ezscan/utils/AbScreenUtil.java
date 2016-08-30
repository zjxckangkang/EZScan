package ezscaner.uniview.app.ezscan.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;

public class AbScreenUtil {

    public static int MAX_LIGHT_NUM = 255;
    public static int MIN_LIGHT_NUM = 20;

    /**
     * 获得当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    public static int getScreenMode(Context context) {
        int screenMode = 0;
        try {
            screenMode = Settings.System.getInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE);
        } catch (Exception localException) {

        }
        return screenMode;
    }

    /**
     * 获得当前屏幕亮度值 0--255
     */
    public static int getScreenBrightness(Context context) {
        int screenBrightness = 255;
        try {
            screenBrightness = Settings.System.getInt(
                    context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception localException) {

        }
        return screenBrightness;
    }

    /**
     * 设置当前屏幕亮度的模式 SCREEN_BRIGHTNESS_MODE_AUTOMATIC=1 为自动调节屏幕亮度
     * SCREEN_BRIGHTNESS_MODE_MANUAL=0 为手动调节屏幕亮度
     */
    public static void setScreenMode(Context context, int paramInt) {
        try {
            Settings.System.putInt(context.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS_MODE, paramInt);
        } catch (Exception localException) {
            localException.printStackTrace();
        }
    }

    /**
     * 设置当前屏幕亮度值 0--255
     */
    // public static void saveScreenBrightness(Context context, int paramInt) {
    // try {
    // Settings.System.putInt(context.getContentResolver(),
    // Settings.System.SCREEN_BRIGHTNESS, paramInt);
    // } catch (Exception localException) {
    // localException.printStackTrace();
    // }
    // }

    /**
     * 保存当前的屏幕亮度值，并使之生效
     */
    public static void saveScreenBrightness(Context context, int paramInt) {
        Window localWindow = ((Activity) context).getWindow();
        WindowManager.LayoutParams localLayoutParams = localWindow
                .getAttributes();
        float f = paramInt / 255.0F;
        localLayoutParams.screenBrightness = f;
        localWindow.setAttributes(localLayoutParams);
    }

    /**
     * @param activity
     */
    public static void setScreenKeepOn(Activity activity, boolean isKeepOn) {
        if (activity != null) {
            Window window = activity.getWindow();
            if (isKeepOn) {
                window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON); // 保持屏幕处于点亮状态
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                window.addFlags(WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON); // 可以锁屏幕
            }
        }
    }


    /**
     * 获取手机屏幕分辨率
     *
     * @param context
     * @return 返回格式为 屏幕宽x屏幕高 的字符串，例如：480x800
     */
    public static int getScreenHeight(Context context) {
        if (context != null) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            return dm.heightPixels;
        } else {
            return 0;
        }

    }

    /**
     * 获取手机屏幕分辨率
     *
     * @param context
     * @return 返回格式为 屏幕宽x屏幕高 的字符串，例如：480x800
     */
    public static int getScreenWidth(Context context) {
        if (context != null) {
            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            return dm.widthPixels;
        } else {
            return 0;
        }
    }

    /**
     * 设置为横屏
     */
    public static void setLandscape(Activity activity) {
        if (activity != null) {
            int requestedOrientation = getOrientation(activity);
            if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
        }
    }

    /**
     * 设置默认屏
     *
     * @param activity
     */
    public static void setDefaultScreen(Activity activity) {
        if (activity != null) {
            activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
        }
    }

    public static int SCREEN_ORIENTATION_LANDSCAPE = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
    public static int SCREEN_ORIENTATION_PORTRAIT = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;

    /**
     * 设置为竖屏
     */
    public static void setPortrait(Activity activity) {
        if (activity != null) {
            int requestedOrientation = getOrientation(activity);
            if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
                activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    public static int getOrientation(Activity activity) {
        return activity.getRequestedOrientation();
    }

    public static void setFullScreen(Activity activity) {
        if (activity != null) {
            activity.getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
        }
    }

    public static void setFeatureNoTitle(Activity activity) {
        if (activity != null) {
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }
    }

    public static void clearFullScreen(Activity activity) {
        if (activity != null) {
            activity.getWindow().clearFlags(
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);// 全屏
        }
    }

}
