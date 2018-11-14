package cc.skyrin.autoslider.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;

import cc.skyrin.autoslider.util.L;

public class SelectLayout extends ConstraintLayout {
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint endPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);

    /**
     * 滑动范围
     */
    Rect startRect = new Rect();
    Rect endRect = new Rect();

    /**
     * 坐标文字边框
     */
    Rect ltStrBounds = new Rect();
    Rect rbStrBounds = new Rect();

    Point start = new Point();
    Point end = new Point();

    boolean startSelected = false;

    public SelectLayout(Context context) {
        super(context);
    }

    public SelectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    {
        paint.setStrokeWidth(dp2px(2));
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.parseColor("#1aad19"));

        endPaint.setStrokeWidth(dp2px(2));
        endPaint.setStyle(Paint.Style.STROKE);
        endPaint.setColor(Color.parseColor("#f45454"));

        paintText.setTextSize(dp2px(12));
        paintText.setColor(Color.parseColor("#1aad19"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制结束区域
        canvas.drawRect(endRect, endPaint);
        // 绘制起始区域
        canvas.drawRect(startRect, paint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                L.d("ACTION_DOWN");
                setStartPos((int) event.getRawX(), (int) (event.getRawY() - getStatusBarHeight()));
                break;
            case MotionEvent.ACTION_MOVE:
                L.d("ACTION_MOVE");
                setStopPos((int) event.getRawX(), (int) (event.getRawY() - getStatusBarHeight()));
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                L.d("ACTION_UP");
                // 抬起存储位置
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void setStartPos(int rawX, int rawY) {
        if (startRect.bottom != 0) {
            startSelected = true;
        }
        if (startRect.bottom != 0 && endRect.bottom != 0) {
            clear();
        }
        if (startSelected) {
            end.x = rawX;
            end.y = rawY;
        } else {
            start.x = rawX;
            start.y = rawY;
        }
    }

    private void setStopPos(int rawX, int rawY) {
        if (startSelected) {
            endRect.left = rawX < end.x ? rawX : end.x;
            endRect.right = rawX > end.x ? rawX : end.x;

            endRect.top = rawY < end.y ? rawY : end.y;
            endRect.bottom = rawY > end.y ? rawY : end.y;
        } else {
            startRect.left = rawX < start.x ? rawX : start.x;
            startRect.right = rawX > start.x ? rawX : start.x;

            startRect.top = rawY < start.y ? rawY : start.y;
            startRect.bottom = rawY > start.y ? rawY : start.y;
        }
    }

    public void clear() {
        startSelected = false;
        startRect.top = 0;
        startRect.bottom = 0;
        startRect.left = 0;
        startRect.right = 0;
        endRect.top = 0;
        endRect.bottom = 0;
        endRect.left = 0;
        endRect.right = 0;
        invalidate();
    }

    public void setStartRect(Rect rect){
        this.startRect = rect;
    }

    public Rect getStartRect() {
        Rect rstRect = startRect;
        rstRect.top += getStatusBarHeight();
        rstRect.bottom += getStatusBarHeight();
        return rstRect;
    }

    public void setEndRect(Rect rect){
        this.endRect = rect;
    }

    public Rect getEndRect() {
        Rect rstRect = endRect;
        rstRect.top += getStatusBarHeight();
        rstRect.bottom += getStatusBarHeight();
        return rstRect;
    }

    public float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return getResources().getDimensionPixelSize(resourceId);
    }
}
