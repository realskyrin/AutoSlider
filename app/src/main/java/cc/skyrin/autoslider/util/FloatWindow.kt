package cc.skyrin.autoslider.util

import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.provider.Settings
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout

/**
 * Created by skyrin on 2017/3/16.
 */

class FloatWindow private constructor(with: With) {

    private var mLayoutParams: WindowManager.LayoutParams? = null
    private var mWindowManager: WindowManager? = null
    private var mDisplayMetrics: DisplayMetrics? = null

    //触摸点相对于view左上角的坐标
    private var downX: Float = 0.toFloat()
    private var downY: Float = 0.toFloat()
    //触摸点相对于屏幕左上角的坐标
    private var rowX: Float = 0.toFloat()
    private var rowY: Float = 0.toFloat()
    //悬浮窗显示标记
    private var isShowing: Boolean = false

    private val mContext: Context
    //是否自动贴边
    private val autoAlign: Boolean
    //是否模态窗口
    private val modality: Boolean
    //是否可拖动
    private val moveAble: Boolean

    //透明度
    private val alpha: Float
    // 初始位置
    private val startX: Int
    private val startY: Int
    // margin
    private val left: Int
    private val top: Int
    private val right: Int
    private val bottom: Int


    /**
     * View 高度
     */
    private val height: Int
    /**
     * View 宽度
     */
    private val width: Int


    //内部定义的View，专门处理事件拦截的父View
    private var floatView: FloatView? = null
    //外部传进来的需要悬浮的View
    private val contentView: View

    init {
        this.mContext = with.context
        this.autoAlign = with.autoAlign
        this.modality = with.modality
        this.contentView = with.contentView
        this.moveAble = with.moveAble
        this.startX = with.startX
        this.startY = with.startY
        this.left = with.left
        this.top = with.top
        this.right = with.right
        this.bottom = with.bottom
        this.alpha = with.alpha
        this.height = with.height
        this.width = with.width

        initWindowManager()
        initLayoutParams()
        initFloatView()
    }

    private fun initWindowManager() {
        mWindowManager = mContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        //获取一个DisplayMetrics对象，该对象用来描述关于显示器的一些信息，例如其大小，密度和字体缩放。
        mDisplayMetrics = DisplayMetrics()
        mWindowManager!!.defaultDisplay.getMetrics(mDisplayMetrics)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initFloatView() {
        floatView = FloatView(mContext)
        if (moveAble) {
            floatView!!.setOnTouchListener(WindowTouchListener())
        }
    }

    private fun initLayoutParams() {
        mLayoutParams = WindowManager.LayoutParams()
        mLayoutParams!!.flags = (WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                or WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE)
        if (modality) {
            mLayoutParams!!.flags = mLayoutParams!!.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL.inv()
            mLayoutParams!!.flags = mLayoutParams!!.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
        }
        mLayoutParams!!.height = LinearLayout.LayoutParams.WRAP_CONTENT
        mLayoutParams!!.width = LinearLayout.LayoutParams.WRAP_CONTENT
        if (height != -666) {
            mLayoutParams!!.height = height
        }
        if (width != -666) {
            mLayoutParams!!.width = width
        }
        mLayoutParams!!.gravity = Gravity.START or Gravity.TOP
        mLayoutParams!!.format = PixelFormat.RGBA_8888
        //此处mLayoutParams.type不建议使用TYPE_TOAST，因为在一些版本较低的系统中会出现拖动异常的问题，虽然它不需要权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mLayoutParams!!.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            mLayoutParams!!.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
        }
        //悬浮窗背景明暗度0~1，数值越大背景越暗，只有在flags设置了WindowManager.LayoutParams.FLAG_DIM_BEHIND 这个属性才会生效
        mLayoutParams!!.dimAmount = 0.0f
        //悬浮窗透明度0~1，数值越大越不透明
        mLayoutParams!!.alpha = alpha
        //悬浮窗起始位置
        mLayoutParams!!.x = startX + left
        mLayoutParams!!.y = startY
    }

    /**
     * 将窗体添加到屏幕上
     */
    @SuppressLint("NewApi")
    fun show() {
        if (!isAppOpsOn(mContext)) {
            return
        }
        if (!isShowing()) {
            mWindowManager!!.addView(floatView, mLayoutParams)
            isShowing = true
        }
    }

    /**
     * 悬浮窗是否正在显示
     *
     * @return true if it's showing.
     */
    private fun isShowing(): Boolean {
        return if (floatView != null && floatView!!.visibility == View.VISIBLE) {
            isShowing
        } else false
    }

    /**
     * 移除悬浮窗
     */
    fun remove() {
        if (isShowing()) {
            floatView!!.removeView(contentView)
            mWindowManager!!.removeView(floatView)
            isShowing = false
        }
    }

    /**
     * 用于获取系统状态栏的高度。
     *
     * @return 返回状态栏高度的像素值。
     */
    private fun getStatusBarHeight(ctx: Context): Int {
        val Identifier = ctx.resources.getIdentifier("status_bar_height",
                "dimen", "android")
        return if (Identifier > 0) {
            ctx.resources.getDimensionPixelSize(Identifier)
        } else 0
    }

    internal inner class FloatView(context: Context) : LinearLayout(context) {

        //记录按下位置
        var interceptX = 0
        var interceptY = 0

        init {
            //这里由于一个ViewGroup不能add一个已经有Parent的contentView,所以需要先判断contentView是否有Parent
            //如果有则需要将contentView先移除
            if (contentView.parent != null && contentView.parent is ViewGroup) {
                (contentView.parent as ViewGroup).removeView(contentView)
            }

            addView(contentView)
        }

        /**
         * 解决点击与拖动冲突的关键代码
         *
         * @param ev
         * @return
         */
        override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
            //此回调如果返回true则表示拦截TouchEvent由自己处理，false表示不拦截TouchEvent分发出去由子view处理
            //解决方案：如果是拖动父View则返回true调用自己的onTouch改变位置，是点击则返回false去响应子view的点击事件
            var isIntercept = false
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    interceptX = ev.x.toInt()
                    interceptY = ev.y.toInt()
                    downX = ev.x
                    downY = ev.y
                    isIntercept = false
                }
                MotionEvent.ACTION_MOVE ->
                    //在一些dpi较高的设备上点击view很容易触发 ACTION_MOVE，所以此处做一个过滤
                    isIntercept = Math.abs(ev.x - interceptX) > MINIMUM_OFFSET && Math.abs(ev.y - interceptY) > MINIMUM_OFFSET
                MotionEvent.ACTION_UP -> isIntercept = false
                else -> {
                }
            }
            return isIntercept
        }
    }

    internal inner class WindowTouchListener : View.OnTouchListener {

        @SuppressLint("ClickableViewAccessibility")
        override fun onTouch(v: View, event: MotionEvent): Boolean {

            //获取触摸点相对于屏幕左上角的坐标
            rowX = event.rawX
            rowY = event.rawY - getStatusBarHeight(mContext)

            when (event.action) {
                MotionEvent.ACTION_DOWN -> actionDown(event)
                MotionEvent.ACTION_MOVE -> actionMove(event)
                MotionEvent.ACTION_UP -> actionUp(event)
                MotionEvent.ACTION_OUTSIDE -> actionOutSide(event)
                else -> {
                }
            }
            return false
        }

        /**
         * 手指点击窗口外的事件
         *
         * @param event
         */
        private fun actionOutSide(event: MotionEvent) {
            //由于我们在layoutParams中添加了FLAG_WATCH_OUTSIDE_TOUCH标记，那么点击悬浮窗之外时此事件就会被响应
            //这里可以用来扩展点击悬浮窗外部响应事件
        }

        /**
         * 手指抬起事件
         *
         * @param event
         */
        private fun actionUp(event: MotionEvent) {
            if (autoAlign) {
                autoAlign()
            }
        }

        /**
         * 拖动事件
         *
         * @param event
         */
        private fun actionMove(event: MotionEvent) {
            //拖动事件下一直计算坐标 然后更新悬浮窗位置
            updateLocation(rowX - downX, rowY - downY)
        }

        /**
         * 更新位置
         */
        private fun updateLocation(x: Float, y: Float) {
            mLayoutParams!!.x = x.toInt()
            mLayoutParams!!.y = y.toInt()
            mWindowManager!!.updateViewLayout(floatView, mLayoutParams)
        }

        /**
         * 手指按下事件
         *
         * @param event
         */
        private fun actionDown(event: MotionEvent) {
            //            downX = event.getX();
            //            downY = event.getY();
        }

        /**
         * 自动贴边
         */
        private fun autoAlign() {
            val fromX = mLayoutParams!!.x.toFloat()

            if (rowX <= mDisplayMetrics!!.widthPixels / 2) {
                mLayoutParams!!.x = left
            } else {
                mLayoutParams!!.x = mDisplayMetrics!!.widthPixels - right
            }

            //这里使用ValueAnimator来平滑计算起始X坐标到结束X坐标之间的值，并更新悬浮窗位置
            val animator = ValueAnimator.ofFloat(fromX, mLayoutParams!!.x.toFloat())
            animator.duration = 300
            animator.addUpdateListener { animation ->
                //这里会返回fromX ~ mLayoutParams.x之间经过计算的过渡值
                val toX = animation.animatedValue as Float
                //我们直接使用这个值来更新悬浮窗位置
                updateLocation(toX, mLayoutParams!!.y.toFloat())
            }
            animator.start()
        }
    }

    class With
    /**
     * @param context     上下文环境
     * @param contentView 需要悬浮的视图
     */
    (val context: Context, val contentView: View) {
        var autoAlign: Boolean = false
        var modality: Boolean = false
        var moveAble: Boolean = false
        var alpha = 1f

        /**
         * View 高度
         */
        var height = -666
        /**
         * View 宽度
         */
        var width = -666

        // 初始位置
        var startX: Int = 0
        var startY: Int = 0
        // margin
        var left: Int = 0
        var top: Int = 0
        var right: Int = 0
        var bottom: Int = 0

        /**
         * 是否自动贴边
         *
         * @param autoAlign
         * @return
         */
        fun setAutoAlign(autoAlign: Boolean): With {
            this.autoAlign = autoAlign
            return this
        }

        /**
         * 是否模态窗口（事件是否可穿透当前窗口）
         *
         * @param modality
         * @return
         */
        fun setModality(modality: Boolean): With {
            this.modality = modality
            return this
        }

        /**
         * 是否可拖动
         *
         * @param moveAble
         * @return
         */
        fun setMoveAble(moveAble: Boolean): With {
            this.moveAble = moveAble
            return this
        }

        /**
         * 设置起始位置
         *
         * @param startX
         * @param startY
         * @return
         */
        fun setStartLocation(startX: Int, startY: Int): With {
            this.startX = startX
            this.startY = startY
            return this
        }

        fun setMargin(left: Int, top: Int, right: Int, bottom: Int): With {
            this.left = left
            this.top = top
            this.right = right
            this.bottom = bottom
            return this
        }

        fun setAlpha(alpha: Float): With {
            this.alpha = alpha
            return this
        }

        fun setHeight(height: Int): With {
            this.height = height
            return this
        }

        fun setWidth(width: Int): With {
            this.width = width
            return this
        }

        fun create(): FloatWindow {
            return FloatWindow(this)
        }
    }

    companion object {
        //拖动最小偏移量
        private val MINIMUM_OFFSET = 5

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
    }
}