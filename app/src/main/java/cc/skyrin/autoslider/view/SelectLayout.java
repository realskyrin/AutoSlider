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

import com.blankj.utilcode.util.Utils;

import cc.skyrin.autoslider.util.L;

public class SelectLayout extends ConstraintLayout {
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    Paint paintText = new Paint(Paint.ANTI_ALIAS_FLAG);

    Rect rect = new Rect();
    Rect startStrRect = new Rect();
    Rect stopStrRect = new Rect();

    Point start = new Point();
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

        paintText.setTextSize(dp2px(12));
        paintText.setColor(Color.parseColor("#1aad19"));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(rect,paint);
        String strStartPos = "["+rect.left+","+rect.top+"]";
        String strEndPos = "["+rect.right+","+rect.bottom+"]";
        paintText.getTextBounds(strStartPos,0,strStartPos.length(),startStrRect);
        paintText.getTextBounds(strEndPos,0,strEndPos.length(),stopStrRect);
        canvas.drawText(strStartPos,rect.left+8,rect.top+startStrRect.bottom-startStrRect.top,paintText);
        canvas.drawText(strEndPos,rect.right-stopStrRect.right-10,rect.bottom-12,paintText);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                L.d("ACTION_DOWN");
                setStartPos((int)event.getRawX(),(int)(event.getRawY()-getStatusBarHeight()));
                break;
            case MotionEvent.ACTION_MOVE:
                L.d("ACTION_MOVE");
                setStopPos((int)event.getRawX(),(int)(event.getRawY()-getStatusBarHeight()));
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

    private void setStartPos(int rawX, int rawY) {
        start.x = rawX;
        start.y = rawY;
    }

    private void setStopPos(int rawX, int rawY) {
        rect.left = rawX<start.x?rawX:start.x;
        rect.right = rawX>start.x?rawX:start.x;

        rect.top = rawY<start.y?rawY:start.y;
        rect.bottom = rawY>start.y?rawY:start.y;
    }

    public void clear() {
        rect.top = 0;
        rect.bottom = 0;
        rect.left = 0;
        rect.right = 0;
        invalidate();
    }

    public Rect getRect() {
        return rect;
    }

    public float dp2px(float dp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, Resources.getSystem().getDisplayMetrics());
    }

    public int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        return getResources().getDimensionPixelSize(resourceId);
    }
}
