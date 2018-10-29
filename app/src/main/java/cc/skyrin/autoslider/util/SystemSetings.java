package cc.skyrin.autoslider.util;

import android.annotation.TargetApi;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import java.lang.reflect.Method;

/**
 * Created by skyrin on 2016/9/25.
 * 系统设置相关
 */

public class SystemSetings {
    /**
     * 辅助服务是否开启
     * @param context
     * @return true if Accessibility is on.
     */
    public static boolean isAccessibilitySettingsOn(Context context) {
        int i;
        try {
            i = Settings.Secure.getInt(context.getContentResolver(), "accessibility_enabled");
        } catch (Settings.SettingNotFoundException e) {
            Log.i("AccessibilitySettingsOn", e.getMessage());
            i = 0;
        }
        if (i != 1) {
            return false;
        }
        String string = Settings.Secure.getString(context.getContentResolver(), "enabled_accessibility_services");
        if (string != null) {
            return string.toLowerCase().contains(context.getPackageName().toLowerCase());
        }
        return false;
    }
    /**
     * 打开辅助服务的设置
     */
    public static boolean openAccessibilityServiceSettings(Context context) {
        boolean result = true;
        try {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } catch (Exception e) {
            result = false;
            e.printStackTrace();
        }
        return result;
    }

    /** 打开通知栏设置*/
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    public static void openNotificationServiceSettings(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 打开app权限的设置
     * @param context
     * @return
     */
    public static boolean openFloatWindowSettings(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 打开app权限的设置
     * @param context
     * @return
     */
    public static boolean openFloatWindowSettings(Context context, String pkgName) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", pkgName, null);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 打开悬浮窗设置页
     * 部分第三方ROM无法直接跳转可使用{@link #openAppSettings(Context)}跳到应用详情页
     *
     * @param context
     * @return true if it's open successful.
     */
    public static boolean openOpsSettings(Context context) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.getPackageName()));
                context.startActivity(intent);
            } else {
                return openAppSettings(context);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 打开应用详情页
     *
     * @param context
     * @return true if it's open success.
     */
    public static boolean openAppSettings(Context context) {
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", context.getPackageName(), null);
            intent.setData(uri);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 判断 悬浮窗口权限是否打开
     * 由于android未提供直接跳转到悬浮窗设置页的api，此方法使用反射去查找相关函数进行跳转
     * 部分第三方ROM可能不适用
     *
     * @param context
     * @return true 允许  false禁止
     */
    public static boolean isAppOpsOn(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context);
        }
        try {
            Object object = context.getSystemService(Context.APP_OPS_SERVICE);
            if (object == null) {
                return false;
            }
            Class localClass = object.getClass();
            Class[] arrayOfClass = new Class[3];
            arrayOfClass[0] = Integer.TYPE;
            arrayOfClass[1] = Integer.TYPE;
            arrayOfClass[2] = String.class;
            Method method = localClass.getMethod("checkOp", arrayOfClass);
            if (method == null) {
                return false;
            }
            Object[] arrayOfObject1 = new Object[3];
            arrayOfObject1[0] = 24;
            arrayOfObject1[1] = Binder.getCallingUid();
            arrayOfObject1[2] = context.getPackageName();
            int m = (Integer) method.invoke(object, arrayOfObject1);
            return m == AppOpsManager.MODE_ALLOWED;
        } catch (Exception ex) {
            ex.getStackTrace();
        }
        return false;
    }

    /**
     * 启动app
     *
     * @param context
     * @param pkgName 包名
     * @return 是否启动成功
     */
    public static boolean startApp(Context context, String pkgName) {
        try {
            PackageManager manager = context.getPackageManager();
            Intent openApp = manager.getLaunchIntentForPackage(pkgName);
            if (openApp==null){
                return false;
            }
            context.startActivity(openApp);
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
