package cc.skyrin.autoslider;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.blankj.utilcode.util.StringUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cc.skyrin.autoslider.util.CommSharedUtil;
import cc.skyrin.autoslider.util.CommonUtil;
import cc.skyrin.autoslider.util.DialogUtil;
import cc.skyrin.autoslider.util.FloatWindow;
import cc.skyrin.autoslider.util.L;
import cc.skyrin.autoslider.util.SystemSetings;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.rl_open_acc)
    View rl_open_acc;
    @BindView(R.id.rl_open_ops)
    View rl_open_ops;
    @BindView(R.id.rl_set_duration)
    View rl_set_duration;
    @BindView(R.id.rl_select_end_area)
    View rl_select_end_area;
    @BindView(R.id.rl_select_start_area)
    View rl_select_start_area;
    @BindView(R.id.rl_slide_rate)
    View rl_slide_rate;

    @BindView(R.id.tv_acc_status)
    TextView tv_acc_status;
    @BindView(R.id.tv_ops_status)
    TextView tv_ops_status;
    @BindView(R.id.tv_start_area_val)
    TextView tv_start_area_val;
    @BindView(R.id.tv_end_area_val)
    TextView tv_end_area_val;
    @BindView(R.id.tv_duration_val)
    TextView tv_duration_val;
    @BindView(R.id.tv_rate_val)
    TextView tv_rate_val;

    TextInputLayout tl_min_val;
    TextInputLayout tl_max_val;
    EditText edt_min_val;
    EditText edt_max_val;
    Button btn_ok;

    View dialogSelectArea;
    Button btn_save;

    MaterialDialog customDialog;
    View dialogView;

    Context context;
    BroadcastReceiver myReceiver;
    int clickId = 0;
    FloatWindow fvSelectArea;
    ViewGroup layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        context = this;
        initData();
        registerErrorReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyApplication.activityResumed();
        if (SystemSetings.isAppOpsOn(this)) {
            tv_ops_status.setText("已开启");
            tv_ops_status.setTextColor(getColor(R.color.ok));
        } else {
            tv_ops_status.setText("未开启");
            tv_ops_status.setTextColor(getColor(R.color.no));
        }

        if (SystemSetings.isAccessibilitySettingsOn(this)) {
            tv_acc_status.setText("已开启");
            tv_acc_status.setTextColor(getColor(R.color.ok));
        } else {
            tv_acc_status.setText("未开启");
            tv_acc_status.setTextColor(getColor(R.color.no));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyApplication.activityPaused();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myReceiver != null) {
            unregisterReceiver(myReceiver);
        }
    }

    private void initData() {
        int min_dur = CommSharedUtil.getInstance(context).getInt(Constants.KEY_MIN_DUR_TIME, 100);
        int max_dur = CommSharedUtil.getInstance(context).getInt(Constants.KEY_MAX_DUR_TIME, 800);
        int min_rate = CommSharedUtil.getInstance(context).getInt(Constants.KEY_MIN_RATE_TIME, 12);
        int max_rate = CommSharedUtil.getInstance(context).getInt(Constants.KEY_MAX_RATE_TIME, 27);
        tv_duration_val.setText(String.format(getResources().getString(R.string.tv_duration_val), min_dur, max_dur));
        tv_rate_val.setText(String.format(getResources().getString(R.string.tv_rate_val), min_rate, max_rate));

        dialogView = View.inflate(this, R.layout.dialog_setting, null);
        tl_max_val = dialogView.findViewById(R.id.tl_max_val);
        tl_min_val = dialogView.findViewById(R.id.tl_min_val);
        tl_min_val.setHint("最小时间（秒）");
        tl_max_val.setHint("最大时间（秒）");
        edt_max_val = tl_max_val.getEditText();
        edt_min_val = tl_min_val.getEditText();
        btn_ok = dialogView.findViewById(R.id.btn_ok);
        btn_ok.setOnClickListener(v -> {

            if (StringUtils.isEmpty(edt_min_val.getText().toString()) || StringUtils.isEmpty(edt_max_val.getText().toString())) {
                showToast("不能为空");
                return;
            }
            int min_val = Integer.parseInt(edt_min_val.getText().toString());
            int max_val = Integer.parseInt(edt_max_val.getText().toString());
            if (max_val <= 0 || min_val <= 0) {
                showToast("必须大于0");
                return;
            }
            if (min_val > max_val) {
                showToast("最小时间不能大于最大时间");
                return;
            }

            switch (clickId) {
                case R.id.rl_set_duration:
                    L.d("VID:rl_set_duration");
                    CommSharedUtil.getInstance(context).putInt(Constants.KEY_MIN_DUR_TIME, min_val);
                    CommSharedUtil.getInstance(context).putInt(Constants.KEY_MAX_DUR_TIME, max_val);
                    tv_duration_val.setText(String.format(getResources().getString(R.string.tv_duration_val), min_val, max_val));
                    break;
                case R.id.rl_slide_rate:
                    L.d("VID:rl_slide_rate");
                    CommSharedUtil.getInstance(context).putInt(Constants.KEY_MIN_RATE_TIME, min_val);
                    CommSharedUtil.getInstance(context).putInt(Constants.KEY_MAX_RATE_TIME, max_val);
                    tv_rate_val.setText(String.format(getResources().getString(R.string.tv_rate_val), min_val, max_val));
                    break;
                default:
                    L.d("VID:default");
                    break;
            }
            customDialog.dismiss();
            showToast("OK");
        });

        dialogSelectArea = View.inflate(this, R.layout.dialog_select_area, null);
        btn_save = dialogSelectArea.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(v -> {
            fvSelectArea.remove();
        });
    }

    @OnClick(R.id.rl_open_acc)
    void rl_open_acc_click() {
        if (!SystemSetings.isAppOpsOn(context)) {
            showToast("请先开启悬浮窗权限");
            return;
        }
        SystemSetings.openAccessibilityServiceSettings(getApplicationContext());
    }

    @OnClick(R.id.rl_open_ops)
    void rl_open_ops_click() {
        SystemSetings.openOpsSettings(context);
    }

    @OnClick(R.id.rl_set_duration)
    void rl_set_duration_click() {
        tl_min_val.setHint("最小时间（毫秒）");
        tl_max_val.setHint("最大时间（毫秒）");
        int min_dur = CommSharedUtil.getInstance(context).getInt(Constants.KEY_MIN_DUR_TIME, 100);
        int max_dur = CommSharedUtil.getInstance(context).getInt(Constants.KEY_MAX_DUR_TIME, 800);
        edt_min_val.setText(String.valueOf(min_dur));
        edt_max_val.setText(String.valueOf(max_dur));

        clickId = R.id.rl_set_duration;
        customDialog = DialogUtil.getCustomDialog(this, dialogView);
        customDialog.show();
        new Handler().postDelayed(() -> CommonUtil.showSoftInputFromWindow(edt_min_val), 200);
    }

    @OnClick(R.id.rl_slide_rate)
    void rl_slide_rate_click() {
        tl_min_val.setHint("最小时间（秒）");
        tl_max_val.setHint("最大时间（秒）");
        int min_rate = CommSharedUtil.getInstance(context).getInt(Constants.KEY_MIN_RATE_TIME, 12);
        int max_rate = CommSharedUtil.getInstance(context).getInt(Constants.KEY_MAX_RATE_TIME, 27);
        edt_min_val.setText(String.valueOf(min_rate));
        edt_max_val.setText(String.valueOf(max_rate));

        clickId = R.id.rl_slide_rate;
        customDialog = DialogUtil.getCustomDialog(this, dialogView);
        customDialog.show();
        new Handler().postDelayed(() -> CommonUtil.showSoftInputFromWindow(edt_min_val), 200);
    }


    @OnClick(R.id.rl_select_end_area)
    void rl_select_end_area_click() {
        clickId = R.id.rl_select_end_area;
        fvSelectArea = new FloatWindow.With(context, dialogSelectArea)
                .setWidth(LinearLayout.LayoutParams.MATCH_PARENT)
                .setHeight(LinearLayout.LayoutParams.MATCH_PARENT)
                .create();
        fvSelectArea.show();
    }

    @OnClick(R.id.rl_select_start_area)
    void rl_select_start_area_click() {
        clickId = R.id.rl_select_start_area;
    }

    public void showToast(String string) {
        try {
            Toast toast = Toast.makeText(this, string, Toast.LENGTH_SHORT);
            toast.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * room error receiver.
     */
    private void registerErrorReceiver() {
        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.ACTION_BACKPRESS);
        registerReceiver(myReceiver, filter);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Constants.ACTION_BACKPRESS.equals(intent.getAction())) {
                onBackPressed();
            }
        }
    }
}

