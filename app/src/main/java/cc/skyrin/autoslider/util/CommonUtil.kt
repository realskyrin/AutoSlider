package cc.skyrin.autoslider.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText

object CommonUtil {
    /**
     * 保持当前Activity常亮
     * @param activity
     */
    fun keepScreenLongLight(activity: Activity) {
        val isOpenLight = CommSharedUtil.getInstance(activity).getBoolean(CommSharedUtil.FLAG_IS_OPEN_LONG_LIGHT, true)
        val window = activity.window
        if (isOpenLight) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }

    }

    /**
     * 复制消息到剪切板
     * @param msg
     */
    fun copyMsg2Clipboard(context: Context, msg: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("error_log", msg)
        assert(clipboard != null)
        clipboard.primaryClip = clip
    }

    /**
     * 隐藏软键盘(只适用于Activity，不适用于Fragment)
     */
    fun hideSoftKeyboard(activity: Activity) {
        val view = activity.currentFocus
        if (view != null) {
            val inputMethodManager = activity.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    fun showSoftInputFromWindow(editText: EditText) {
        editText.isFocusable = true
        editText.isFocusableInTouchMode = true
        editText.requestFocus()
        val inputManager = editText.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.showSoftInput(editText, 0)
    }
}
