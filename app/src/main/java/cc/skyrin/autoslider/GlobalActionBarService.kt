package cc.skyrin.autoslider

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Path
import android.graphics.Point
import android.view.LayoutInflater
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.widget.CheckBox

import java.util.Random
import java.util.Timer
import java.util.TimerTask

import cc.skyrin.autoslider.util.CommSharedUtil
import cc.skyrin.autoslider.util.FloatWindow
import cc.skyrin.autoslider.util.L
import cc.skyrin.autoslider.util.SystemSetings

class GlobalActionBarService : AccessibilityService() {

    lateinit var layout: View
    private var floatWindow: FloatWindow? = null
    private var timer: Timer? = null

    override fun onServiceConnected() {
        super.onServiceConnected()
        L.d("onServiceConnected")
        // Create an overlay and display the action bar
        showActionBar()
        configureSwipeButton()
    }

    private fun stopTimerTask() {
        timer?.purge()
        timer?.cancel()
        timer = null
    }

    private fun startTimerTask() {
        val minDur = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_DUR_TIME, 100)
        val maxDur = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MAX_DUR_TIME, 800)
        val minDate = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_RATE_TIME, 12)
        val maxDate = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MAX_RATE_TIME, 27)

        val minStartX = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_START_X, 0)
        val maxStartX = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MAX_START_X, 0)
        val minStartY = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_START_Y, 0)
        val maxStartY = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MAX_START_Y, 0)

        val minEndX = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_END_X, 0)
        val maxEndX = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MAX_END_X, 0)
        val minEndY = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_END_Y, 0)
        val maxEndY = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MAX_END_Y, 0)
        try {
            stopTimerTask()
        } catch (e: Exception) {
        }

        timer = Timer()
        timer!!.schedule(object : TimerTask() {
            override fun run() {
                val startPoint = Point(randInt(minStartX, maxStartX), randInt(minStartY, maxStartY))
                val endPoint = Point(randInt(minEndX, maxEndX), randInt(minEndY, maxEndY))
                val duration = randInt(minDur, maxDur).toLong()

                randSwipe(startPoint, endPoint, duration)
            }
        }, 0, (randInt(minDate, maxDate) * 1000).toLong())
    }

    @SuppressLint("InflateParams")
    private fun showActionBar() {
        layout = LayoutInflater.from(this).inflate(R.layout.action_bar, null)

        floatWindow = FloatWindow.With(this, layout)
                .create()
        floatWindow!!.show()

        // android 7.0 要求 view 必须附加到 window 之后 view#post 才会执行
        layout.post {
            floatWindow = FloatWindow.With(this, layout)
                    .setModality(false)
                    .setMoveAble(true)
                    .setAutoAlign(true)
                    // 获取到 layout 的宽度计算右边距
                    .setMargin(20, 0, layout.width + 20, 0)
                    .create()
            floatWindow!!.remove()
            floatWindow!!.show()
        }
    }

    internal fun randSwipe(startPoint: Point, endPoint: Point, duration: Long) {
        val swipePath = Path()
        swipePath.moveTo(startPoint.x.toFloat(), startPoint.y.toFloat())
        swipePath.lineTo(endPoint.x.toFloat(), endPoint.y.toFloat())
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(GestureDescription.StrokeDescription(swipePath, 0, duration))
        dispatchGesture(gestureBuilder.build(), null, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        L.d("onDestroy")

        floatWindow?.remove()
        floatWindow = null

        stopTimerTask()
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {

    }

    private fun configureSwipeButton() {
        val btnSettings = layout.findViewById<View>(R.id.btn_settings)
        val btnHome = layout.findViewById<View>(R.id.btn_home)
        val cbRead = layout.findViewById<CheckBox>(R.id.cb_read)
        btnSettings.setOnClickListener { sendBroadcast(Intent(Constants.ACTION_SWITCH_OVERLY)) }
        btnHome.setOnClickListener {
            if (MyApplication.isActivityVisible) {
                sendBroadcast(Intent(Constants.ACTION_BACKPRESS))
            } else {
                SystemSetings.startApp(applicationContext, packageName)
            }
        }
        cbRead.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                startTimerTask()
            } else {
                stopTimerTask()
            }
        }
    }


    /**
     * 获取从 [min,max) 之间的一个随机数
     *
     * @param min
     * @param max
     * @return
     */
    internal fun randInt(min: Int, max: Int): Int {
        val rand = Random()
        return rand.nextInt(max - min + 1) + min
    }

    override fun onInterrupt() {
        floatWindow?.remove()
        floatWindow = null

        stopTimerTask()
    }
}
