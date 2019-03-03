package cc.skyrin.autoslider

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Resources
import android.graphics.PixelFormat
import android.graphics.Rect
import android.os.Build
import android.os.Handler
import android.support.design.widget.TextInputLayout
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast

import com.afollestad.materialdialogs.MaterialDialog
import com.blankj.utilcode.util.StringUtils

import cc.skyrin.autoslider.util.CommSharedUtil
import cc.skyrin.autoslider.util.CommonUtil
import cc.skyrin.autoslider.util.DialogUtil
import cc.skyrin.autoslider.util.L
import cc.skyrin.autoslider.util.SystemSetings
import cc.skyrin.autoslider.view.SelectLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    lateinit var tl_min_val: TextInputLayout
    lateinit var tl_max_val: TextInputLayout
    var edt_min_val: EditText? = null
    var edt_max_val: EditText? = null
    lateinit var btn_ok: Button

    lateinit var selectLayout: SelectLayout
    lateinit var btn_save: View
    lateinit var btn_cancel: View

    var customDialog: MaterialDialog? = null
    lateinit var dialogView: View

    lateinit var context: Context
    private var myReceiver: BroadcastReceiver? = null
    private var clickId = 0
    var layout: ViewGroup? = null

    private var wm: WindowManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        context = this
        initData()
        initListener()
        registerErrorReceiver()
    }

    private fun initListener() {
        rl_open_acc.setOnClickListener { rl_open_acc_click() }
        rl_open_ops.setOnClickListener { rl_open_ops_click() }
        rl_select_area.setOnClickListener { rl_select_area_click() }
        rl_set_duration.setOnClickListener { rl_set_duration_click() }
        rl_slide_rate.setOnClickListener { rl_slide_rate_click() }
    }

    override fun onResume() {
        super.onResume()
        MyApplication.activityResumed()
        if (SystemSetings.isAppOpsOn(this)) {
            tv_ops_status!!.text = "已开启"
            tv_ops_status!!.setTextColor(getColor(R.color.ok))
        } else {
            tv_ops_status!!.text = "未开启"
            tv_ops_status!!.setTextColor(getColor(R.color.no))
        }

        if (SystemSetings.isAccessibilitySettingsOn(this)) {
            tv_acc_status!!.text = "已开启"
            tv_acc_status!!.setTextColor(getColor(R.color.ok))
        } else {
            tv_acc_status!!.text = "未开启"
            tv_acc_status!!.setTextColor(getColor(R.color.no))
        }

        val min_start_x = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_START_X, 0)
        if (min_start_x != 0) {
            tv_start_area_val!!.text = "已存储"
        }
    }

    override fun onPause() {
        super.onPause()
        MyApplication.activityPaused()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (myReceiver != null) {
            unregisterReceiver(myReceiver)
        }
    }

    private fun initData() {
        val minDur = CommSharedUtil.getInstance(context).getInt(Constants.KEY_MIN_DUR_TIME, 100)
        val maxDur = CommSharedUtil.getInstance(context).getInt(Constants.KEY_MAX_DUR_TIME, 800)
        val minRate = CommSharedUtil.getInstance(context).getInt(Constants.KEY_MIN_RATE_TIME, 12)
        val maxRate = CommSharedUtil.getInstance(context).getInt(Constants.KEY_MAX_RATE_TIME, 27)

        tv_duration_val!!.text = String.format(resources.getString(R.string.tv_duration_val), minDur, maxDur)
        tv_rate_val!!.text = String.format(resources.getString(R.string.tv_rate_val), minRate, maxRate)

        dialogView = View.inflate(this, R.layout.dialog_setting, null)
        tl_max_val = dialogView.findViewById(R.id.tl_max_val)
        tl_min_val = dialogView.findViewById(R.id.tl_min_val)
        tl_min_val.hint = "最小时间（秒）"
        tl_max_val.hint = "最大时间（秒）"
        edt_max_val = tl_max_val.editText
        edt_min_val = tl_min_val.editText
        btn_ok = dialogView.findViewById(R.id.btn_ok)

        btn_ok.setOnClickListener { v ->

            if (StringUtils.isEmpty(edt_min_val!!.text.toString()) || StringUtils.isEmpty(edt_max_val!!.text.toString())) {
                showToast("不能为空")
                return@setOnClickListener
            }

            val min_val = Integer.parseInt(edt_min_val!!.text.toString())
            val max_val = Integer.parseInt(edt_max_val!!.text.toString())
            if (max_val <= 0 || min_val <= 0) {
                showToast("必须大于0")
                return@setOnClickListener
            }

            if (min_val > max_val) {
                showToast("最小时间不能大于最大时间")
                return@setOnClickListener
            }

            when (clickId) {
                R.id.rl_set_duration -> {
                    L.d("VID:rl_set_duration")
                    CommSharedUtil.getInstance(context).putInt(Constants.KEY_MIN_DUR_TIME, min_val)
                    CommSharedUtil.getInstance(context).putInt(Constants.KEY_MAX_DUR_TIME, max_val)
                    tv_duration_val!!.setText(String.format(resources.getString(R.string.tv_duration_val), min_val, max_val))
                }
                R.id.rl_slide_rate -> {
                    L.d("VID:rl_slide_rate")
                    CommSharedUtil.getInstance(context).putInt(Constants.KEY_MIN_RATE_TIME, min_val)
                    CommSharedUtil.getInstance(context).putInt(Constants.KEY_MAX_RATE_TIME, max_val)
                    tv_rate_val!!.setText(String.format(resources.getString(R.string.tv_rate_val), min_val, max_val))
                }
                else -> L.d("VID:default")
            }
            customDialog!!.dismiss()
            showToast("OK")
        }

        selectLayout = View.inflate(this, R.layout.dialog_select_area, null) as SelectLayout
        btn_save = selectLayout.findViewById(R.id.btn_save)
        btn_cancel = selectLayout.findViewById(R.id.btn_cancel)
        btn_save.setOnClickListener { v ->
            wm!!.removeView(selectLayout)
            val startRect = selectLayout.getStartRect()
            val endRect = selectLayout.getEndRect()

            CommSharedUtil.getInstance(context).putInt(Constants.KEY_MIN_START_X, startRect.left)
            CommSharedUtil.getInstance(context).putInt(Constants.KEY_MAX_START_X, startRect.right)
            CommSharedUtil.getInstance(context).putInt(Constants.KEY_MIN_START_Y, startRect.top)
            CommSharedUtil.getInstance(context).putInt(Constants.KEY_MAX_START_Y, startRect.bottom)

            CommSharedUtil.getInstance(context).putInt(Constants.KEY_MIN_END_X, endRect.left)
            CommSharedUtil.getInstance(context).putInt(Constants.KEY_MAX_END_X, endRect.right)
            CommSharedUtil.getInstance(context).putInt(Constants.KEY_MIN_END_Y, endRect.top)
            CommSharedUtil.getInstance(context).putInt(Constants.KEY_MAX_END_Y, endRect.bottom)
            selectLayout.clear()
            tv_start_area_val!!.text = "已存储"
        }
        btn_cancel.setOnClickListener { v ->
            hideSelectLayout()
            selectLayout.clear()
        }
    }

    fun rl_open_acc_click() {
        if (!SystemSetings.isAppOpsOn(context)) {
            showToast("请先开启悬浮窗权限")
            return
        }
        SystemSetings.openAccessibilityServiceSettings(applicationContext)
    }

    fun rl_open_ops_click() {
        SystemSetings.openOpsSettings(context)
    }

    fun rl_set_duration_click() {
        tl_min_val.hint = "最小时间（毫秒）"
        tl_max_val.hint = "最大时间（毫秒）"
        val min_dur = CommSharedUtil.getInstance(context).getInt(Constants.KEY_MIN_DUR_TIME, 100)
        val max_dur = CommSharedUtil.getInstance(context).getInt(Constants.KEY_MAX_DUR_TIME, 800)
        edt_min_val!!.setText(min_dur.toString())
        edt_max_val!!.setText(max_dur.toString())

        clickId = R.id.rl_set_duration
        customDialog = DialogUtil.getCustomDialog(this, dialogView)
        customDialog!!.show()
        Handler().postDelayed({ CommonUtil.showSoftInputFromWindow(edt_min_val!!) }, 200)
    }

    fun rl_slide_rate_click() {
        tl_min_val.hint = "最小时间（秒）"
        tl_max_val.hint = "最大时间（秒）"
        val min_rate = CommSharedUtil.getInstance(context).getInt(Constants.KEY_MIN_RATE_TIME, 12)
        val max_rate = CommSharedUtil.getInstance(context).getInt(Constants.KEY_MAX_RATE_TIME, 27)
        edt_min_val!!.setText(min_rate.toString())
        edt_max_val!!.setText(max_rate.toString())

        clickId = R.id.rl_slide_rate
        customDialog = DialogUtil.getCustomDialog(this, dialogView)
        customDialog!!.show()
        Handler().postDelayed({ CommonUtil.showSoftInputFromWindow(edt_min_val!!) }, 200)
    }

    fun rl_select_area_click() {
        showSelectLayout()
    }

    fun showToast(string: String) {
        try {
            val toast = Toast.makeText(this, string, Toast.LENGTH_SHORT)
            toast.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    fun showSelectLayout() {
        if (!SystemSetings.isAppOpsOn(context)) {
            showToast("请先开启悬浮窗权限")
            return
        }
        // 设置位置
        setRect()
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val lp = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            lp.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        lp.format = PixelFormat.TRANSLUCENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.gravity = Gravity.END or Gravity.TOP
        if (selectLayout.parent!=null){
            wm!!.removeView(selectLayout)
        }
        wm!!.addView(selectLayout, lp)
    }

    private fun setRect() {
        val min_start_x = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_START_X, 0)
        val max_start_x = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MAX_START_X, 0)
        val min_start_y = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_START_Y, 0)
        val max_start_y = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MAX_START_Y, 0)

        val min_end_x = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_END_X, 0)
        val max_end_x = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MAX_END_X, 0)
        val min_end_y = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_END_Y, 0)
        val max_end_y = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MAX_END_Y, 0)

        val startRect = Rect(min_start_x, min_start_y, max_start_x, max_start_y)
        val endRect = Rect(min_end_x, min_end_y, max_end_x, max_end_y)
        selectLayout.setStartRect(startRect)
        selectLayout.setEndRect(endRect)
        selectLayout.invalidate()
    }

    private fun hideSelectLayout() {
        wm!!.removeView(selectLayout)
    }

    fun dp2px(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().displayMetrics)
    }

    /**
     * room error receiver.
     */
    private fun registerErrorReceiver() {
        myReceiver = MyReceiver()
        val filter = IntentFilter()
        filter.addAction(Constants.ACTION_BACKPRESS)
        filter.addAction(Constants.ACTION_SWITCH_OVERLY)
        registerReceiver(myReceiver, filter)
    }

    override fun onBackPressed() {
        goHome()
    }

    internal fun goHome() {
        val home = Intent(Intent.ACTION_MAIN)
        home.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        home.addCategory(Intent.CATEGORY_HOME)
        startActivity(home)
    }

    internal inner class MyReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Constants.ACTION_BACKPRESS == intent.action) {
                goHome()
            } else if (Constants.ACTION_SWITCH_OVERLY == intent.action) {
                showHideSelectLayout()
            }
        }
    }

    internal fun showHideSelectLayout() {
        if (selectLayout.isAttachedToWindow) {
            hideSelectLayout()
        } else {
            showSelectLayout()
        }
    }
}

