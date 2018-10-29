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
    Rect rect = new Rect();

    Point start = new Point();
    Point stop = new Point();
    public SelectLayout(Context context) {
        super(context);
    }

    public SelectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    {
        paint.setStrokeWidth(dp2px(2));
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.RED);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(rect,paint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        L.d("getRawX:"+event.getRawX());
        L.d("getRawY:"+event.getRawY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                L.d("ACTION_DOWN");
//                rect.left = (int) event.getRawX();
//                rect.top = (int) event.getRawY();
                setStartPos(event.getRawX(),event.getRawY());
                break;
            case MotionEvent.ACTION_MOVE:
                L.d("ACTION_MOVE");
                setStopPos(event.getRawX(),event.getRawY());
//                rect.right = (int) event.getRawX();
//                rect.bottom = (int) event.getRawY();
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                L.d("ACTION_UP");
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void setStartPos(float rawX, float rawY) {
        start.x = (int) rawX;
        start.y = (int) rawY;
    }

    private void setStopPos(float rawX, float rawY) {

    }

    public float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }
}
