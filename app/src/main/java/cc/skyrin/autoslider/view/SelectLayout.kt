package cc.skyrin.autoslider.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.graphics.Rect
import android.support.constraint.ConstraintLayout
import android.util.AttributeSet
import android.util.TypedValue
import android.view.MotionEvent

import cc.skyrin.autoslider.util.L

class SelectLayout : ConstraintLayout {
    private var paint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var endPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private var paintText = Paint(Paint.ANTI_ALIAS_FLAG)

    /**
     * 滑动范围
     */
    private var startRect = Rect()
    private var endRect = Rect()

    /**
     * 坐标文字边框
     */
    private var ltStrBounds = Rect()
    private var rbStrBounds = Rect()

    private var start = Point()
    private var end = Point()

    private var startSelected = false

    private val statusBarHeight: Int
        get() {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            return resources.getDimensionPixelSize(resourceId)
        }

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    init {
        paint.strokeWidth = dp2px(2f)
        paint.style = Paint.Style.STROKE
        paint.color = Color.parseColor("#1aad19")

        endPaint.strokeWidth = dp2px(2f)
        endPaint.style = Paint.Style.STROKE
        endPaint.color = Color.parseColor("#f45454")

        paintText.textSize = dp2px(12f)
        paintText.color = Color.parseColor("#1aad19")
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 绘制结束区域
        canvas.drawRect(endRect, endPaint)
        // 绘制起始区域
        canvas.drawRect(startRect, paint)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                L.d("ACTION_DOWN")
                setStartPos(event.rawX.toInt(), (event.rawY - statusBarHeight).toInt())
            }
            MotionEvent.ACTION_MOVE -> {
                L.d("ACTION_MOVE")
                setStopPos(event.rawX.toInt(), (event.rawY - statusBarHeight).toInt())
                invalidate()
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> L.d("ACTION_UP")
            else -> {
            }
        }// 抬起存储位置
        return super.onTouchEvent(event)
    }

    private fun setStartPos(rawX: Int, rawY: Int) {
        if (startRect.bottom != 0) {
            startSelected = true
        }
        if (startRect.bottom != 0 && endRect.bottom != 0) {
            clear()
        }
        if (startSelected) {
            end.x = rawX
            end.y = rawY
        } else {
            start.x = rawX
            start.y = rawY
        }
    }

    private fun setStopPos(rawX: Int, rawY: Int) {
        if (startSelected) {
            endRect.left = if (rawX < end.x) rawX else end.x
            endRect.right = if (rawX > end.x) rawX else end.x

            endRect.top = if (rawY < end.y) rawY else end.y
            endRect.bottom = if (rawY > end.y) rawY else end.y
        } else {
            startRect.left = if (rawX < start.x) rawX else start.x
            startRect.right = if (rawX > start.x) rawX else start.x

            startRect.top = if (rawY < start.y) rawY else start.y
            startRect.bottom = if (rawY > start.y) rawY else start.y
        }
    }

    fun clear() {
        startSelected = false
        startRect.top = 0
        startRect.bottom = 0
        startRect.left = 0
        startRect.right = 0
        endRect.top = 0
        endRect.bottom = 0
        endRect.left = 0
        endRect.right = 0
        invalidate()
    }

    fun setStartRect(rect: Rect) {
        this.startRect = rect
    }

    fun getStartRect(): Rect {
        val rstRect = startRect
        rstRect.top += statusBarHeight
        rstRect.bottom += statusBarHeight
        return rstRect
    }

    fun setEndRect(rect: Rect) {
        this.endRect = rect
    }

    fun getEndRect(): Rect {
        val rstRect = endRect
        rstRect.top += statusBarHeight
        rstRect.bottom += statusBarHeight
        return rstRect
    }

    fun dp2px(dp: Float): Float {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().displayMetrics)
    }
}
