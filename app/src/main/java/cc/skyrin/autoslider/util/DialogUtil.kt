package cc.skyrin.autoslider.util

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.DialogInterface
import android.support.v7.app.AlertDialog
import android.view.View

import com.afollestad.materialdialogs.MaterialDialog

import cc.skyrin.autoslider.R

object DialogUtil {
    var errorDialog: MaterialDialog? = null
        private set

    fun getErrorDialog(context: Context, errorCode: Int, msg: String): MaterialDialog? {
        errorDialog = MaterialDialog.Builder(context)
                .dividerColorRes(R.color.main)
                .positiveColorRes(R.color.main)
                .negativeColorRes(R.color.main)
                .widgetColorRes(R.color.main)
                .content("error:$errorCode\n$msg")
                .positiveText("确定")
                .negativeText("复制日志")
                .onNegative { dialog, which -> copyMsg2Clipboard(context, msg) }
                .onPositive { dialog, which -> (context as Activity).finish() }
                .build()
        return errorDialog
    }

    fun getErrorDialog(context: Context, errorCode: Int, msg: String, cancelable: Boolean): MaterialDialog? {
        errorDialog = MaterialDialog.Builder(context)
                .cancelable(cancelable)
                .dividerColorRes(R.color.main)
                .positiveColorRes(R.color.main)
                .negativeColorRes(R.color.main)
                .widgetColorRes(R.color.main)
                .content("error:$errorCode\n$msg")
                .positiveText("确定")
                .negativeText("复制日志")
                .onNegative { dialog, which -> copyMsg2Clipboard(context, msg) }
                .onPositive { dialog, which -> (context as Activity).finish() }
                .build()
        return errorDialog
    }

    fun getMsgDialogDontFinish(context: Context, msg: String): MaterialDialog? {
        errorDialog = MaterialDialog.Builder(context)
                .dividerColorRes(R.color.main)
                .positiveColorRes(R.color.main)
                .negativeColorRes(R.color.main)
                .widgetColorRes(R.color.main)
                .content(msg)
                .positiveText("确定")
                .build()
        return errorDialog
    }

    fun getMsgDialog(context: Context, msg: String): MaterialDialog? {
        errorDialog = MaterialDialog.Builder(context)
                .dividerColorRes(R.color.main)
                .positiveColorRes(R.color.main)
                .negativeColorRes(R.color.main)
                .widgetColorRes(R.color.main)
                .content(msg)
                .positiveText("确定")
                .onPositive { dialog, which -> (context as Activity).finish() }
                .build()
        return errorDialog
    }

    fun getMsgDialog(context: Context, msg: String, cancelable: Boolean): MaterialDialog? {
        errorDialog = MaterialDialog.Builder(context)
                .cancelable(cancelable)
                .dividerColorRes(R.color.main)
                .positiveColorRes(R.color.main)
                .negativeColorRes(R.color.main)
                .widgetColorRes(R.color.main)
                .content(msg)
                .positiveText("确定")
                .onPositive { dialog, which -> (context as Activity).finish() }
                .build()
        return errorDialog
    }

    fun showDialog(context: Context, content: String) {
        MaterialDialog.Builder(context)
                .content(content)
                .positiveText("复制信息")
                .onPositive { dialog, which -> copyMsg2Clipboard(context, content) }
                .show()
    }

    fun getCustomDialog(context: Context, view: View): MaterialDialog? {
        errorDialog = MaterialDialog.Builder(context)
                .customView(view, false)
                .build()
        return errorDialog
    }

    /**
     * **显示一个带确定回调的提示对话窗口
     *
     *
     * **listener 点击确定后执行的回调 DialogInterface.OnClickListener
     **** */
    fun showTipsDialog(activity: Context, msg: String, listener: DialogInterface.OnClickListener) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(msg)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", listener)
        builder.show()
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
}
