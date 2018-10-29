package cc.skyrin.autoslider.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;

import com.afollestad.materialdialogs.MaterialDialog;

import cc.skyrin.autoslider.R;

public class DialogUtil {
    private static MaterialDialog mdErrorDialog;

    public static MaterialDialog getErrorDialog(Context context, int errorCode, String msg) {
        mdErrorDialog = new MaterialDialog.Builder(context)
                .dividerColorRes(R.color.main)
                .positiveColorRes(R.color.main)
                .negativeColorRes(R.color.main)
                .widgetColorRes(R.color.main)
                .content("error:" + errorCode + "\n" + msg)
                .positiveText("确定")
                .negativeText("复制日志")
                .onNegative((dialog, which) -> copyMsg2Clipboard(context, msg))
                .onPositive((dialog, which) -> ((Activity)context).finish())
                .build();
        return mdErrorDialog;
    }

    public static MaterialDialog getErrorDialog(Context context, int errorCode, String msg, boolean cancelable) {
        mdErrorDialog = new MaterialDialog.Builder(context)
                .cancelable(cancelable)
                .dividerColorRes(R.color.main)
                .positiveColorRes(R.color.main)
                .negativeColorRes(R.color.main)
                .widgetColorRes(R.color.main)
                .content("error:" + errorCode + "\n" + msg)
                .positiveText("确定")
                .negativeText("复制日志")
                .onNegative((dialog, which) -> copyMsg2Clipboard(context, msg))
                .onPositive((dialog, which) -> ((Activity)context).finish())
                .build();
        return mdErrorDialog;
    }

    public static MaterialDialog getErrorDialog() {
        return mdErrorDialog;
    }

    public static MaterialDialog getMsgDialogDontFinish(Context context, String msg) {
        mdErrorDialog = new MaterialDialog.Builder(context)
                .dividerColorRes(R.color.main)
                .positiveColorRes(R.color.main)
                .negativeColorRes(R.color.main)
                .widgetColorRes(R.color.main)
                .content(msg)
                .positiveText("确定")
                .build();
        return mdErrorDialog;
    }

    public static MaterialDialog getMsgDialog(Context context, String msg) {
        mdErrorDialog = new MaterialDialog.Builder(context)
                .dividerColorRes(R.color.main)
                .positiveColorRes(R.color.main)
                .negativeColorRes(R.color.main)
                .widgetColorRes(R.color.main)
                .content(msg)
                .positiveText("确定")
                .onPositive((dialog, which) -> ((Activity)context).finish())
                .build();
        return mdErrorDialog;
    }

    public static MaterialDialog getMsgDialog(Context context, String msg, boolean cancelable) {
        mdErrorDialog = new MaterialDialog.Builder(context)
                .cancelable(cancelable)
                .dividerColorRes(R.color.main)
                .positiveColorRes(R.color.main)
                .negativeColorRes(R.color.main)
                .widgetColorRes(R.color.main)
                .content(msg)
                .positiveText("确定")
                .onPositive((dialog, which) -> ((Activity)context).finish())
                .build();
        return mdErrorDialog;
    }

    public static void showDialog(Context context, String content) {
        new MaterialDialog.Builder(context)
                .content(content)
                .positiveText("复制信息")
                .onPositive((dialog, which) -> copyMsg2Clipboard(context, content))
                .show();
    }

    public static MaterialDialog getCustomDialog(Context context, View view){
        mdErrorDialog = new MaterialDialog.Builder(context)
                .customView(view,false)
                .build();
        return mdErrorDialog;
    }

    /**
     * <b>显示一个带确定回调的提示对话窗口<p>
     * <b>listener 点击确定后执行的回调 DialogInterface.OnClickListener
     */
    public static void showTipsDialog(final Context activity, String msg, DialogInterface.OnClickListener listener){
        android.support.v7.app.AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage(msg)
                .setNegativeButton("取消", null)
                .setPositiveButton("确定", listener);
        builder.show();
    }

    /**
     * 复制消息到剪切板
     * @param msg
     */
    public static void copyMsg2Clipboard(Context context, String msg) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("error_log", msg);
        assert clipboard != null;
        clipboard.setPrimaryClip(clip);
    }
}
