package cc.skyrin.autoslider.util

import android.annotation.TargetApi
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.provider.Settings
import android.util.Log

import java.lang.reflect.Method

/**
 * Created by skyrin on 2016/9/25.
 * 系统设置相关
 */

object SystemSetings {
    /**
     * 辅助服务是否开启
     * @param context
     * @return true if Accessibility is on.
     */
    fun isAccessibilitySettingsOn(context: Context): Boolean {
        var i: Int
        try {
            i = Settings.Secure.getInt(context.contentResolver, "accessibility_enabled")
        } catch (e: Settings.SettingNotFoundException) {
            Log.i("AccessibilitySettingsOn", e.message)
            i = 0
        }

        if (i != 1) {
            return false
        }
        val string = Settings.Secure.getString(context.contentResolver, "enabled_accessibility_services")
        return string?.toLowerCase()?.contains(context.packageName.toLowerCase()) ?: false
    }

    /**
     * 打开辅助服务的设置
     */
    fun openAccessibilityServiceSettings(context: Context): Boolean {
        var result = true
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            result = false
            e.printStackTrace()
        }

        return result
    }

    /** 打开通知栏设置 */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    fun openNotificationServiceSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    /**
     * 打开app权限的设置
     * @param context
     * @return
     */
    fun openFloatWindowSettings(context: Context): Boolean {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

    /**
     * 打开app权限的设置
     * @param context
     * @return
     */
    fun openFloatWindowSettings(context: Context, pkgName: String): Boolean {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", pkgName, null)
            intent.data = uri
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

    /**
     * 打开悬浮窗设置页
     * 部分第三方ROM无法直接跳转可使用[.openAppSettings]跳到应用详情页
     *
     * @param context
     * @return true if it's open successful.
     */
    fun openOpsSettings(context: Context): Boolean {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                val intent = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + context.packageName))
                context.startActivity(intent)
            } else {
                return openAppSettings(context)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

    /**
     * 打开应用详情页
     *
     * @param context
     * @return true if it's open success.
     */
    fun openAppSettings(context: Context): Boolean {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", context.packageName, null)
            intent.data = uri
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }

        return true
    }

    /**
     * 判断 悬浮窗口权限是否打开
     * 由于android未提供直接跳转到悬浮窗设置页的api，此方法使用反射去查找相关函数进行跳转
     * 部分第三方ROM可能不适用
     *
     * @param context
     * @return true 允许  false禁止
     */
    fun isAppOpsOn(context: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Settings.canDrawOverlays(context)
        }
        try {
            val `object` = context.getSystemService(Context.APP_OPS_SERVICE) ?: return false
            val localClass = `object`.javaClass
            val arrayOfClass = arrayOfNulls<Class<*>>(3)
            arrayOfClass[0] = Integer.TYPE
            arrayOfClass[1] = Integer.TYPE
            arrayOfClass[2] = String::class.java
            val method = localClass.getMethod("checkOp", *arrayOfClass) ?: return false
            val arrayOfObject1 = arrayOfNulls<Any>(3)
            arrayOfObject1[0] = 24
            arrayOfObject1[1] = Binder.getCallingUid()
            arrayOfObject1[2] = context.packageName
            val m = method.invoke(`object`, *arrayOfObject1) as Int
            return m == AppOpsManager.MODE_ALLOWED
        } catch (ex: Exception) {
            ex.stackTrace
        }

        return false
    }

    /**
     * 启动app
     *
     * @param context
     * @param pkgName 包名
     * @return 是否启动成功
     */
    fun startApp(context: Context, pkgName: String): Boolean {
        try {
            val manager = context.packageManager
            val openApp = manager.getLaunchIntentForPackage(pkgName) ?: return false
            openApp.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
            context.startActivity(openApp)
        } catch (e: Exception) {
            return false
        }
        return true
    }
}
