package cc.skyrin.autoslider;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.GestureDescription;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityEvent;
import android.widget.CheckBox;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import cc.skyrin.autoslider.util.CommSharedUtil;
import cc.skyrin.autoslider.util.FloatWindow;
import cc.skyrin.autoslider.util.L;
import cc.skyrin.autoslider.util.SystemSetings;

public class GlobalActionBarService extends AccessibilityService {

    ViewGroup layout;
    FloatWindow floatWindow;

    Timer timer;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        L.d("onServiceConnected");
        // Create an overlay and display the action bar
        showActionBar();
        configureSwipeButton();
    }

    private void stopTimerTask() {
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }

    private void startTimerTask() {
        int min_dur = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_DUR_TIME, 100);
        int max_dur = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MAX_DUR_TIME, 800);
        int min_rate = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_RATE_TIME, 12);
        int max_rate = CommSharedUtil.getInstance(this).getInt(Constants.KEY_MAX_RATE_TIME, 27);

        int min_start_x =  CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_START_X, 0);
        int max_start_x =  CommSharedUtil.getInstance(this).getInt(Constants.KEY_MAX_START_X, 0);
        int min_start_y =  CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_START_Y, 0);
        int max_start_y =  CommSharedUtil.getInstance(this).getInt(Constants.KEY_MAX_START_Y, 0);

        int min_end_x =  CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_END_X, 0);
        int max_end_x =  CommSharedUtil.getInstance(this).getInt(Constants.KEY_MAX_END_X, 0);
        int min_end_y =  CommSharedUtil.getInstance(this).getInt(Constants.KEY_MIN_END_Y, 0);
        int max_end_y =  CommSharedUtil.getInstance(this).getInt(Constants.KEY_MAX_END_Y, 0);
        try {
            if (timer != null) {
                timer.purge();
                timer.cancel();
                timer = null;
            }
        } catch (Exception e) {
        }
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Point startPoint = new Point(randInt(min_start_x, max_start_x), randInt(min_start_y, max_start_y));
                Point endPoint = new Point(randInt(min_end_x, max_end_x), randInt(min_end_y, max_end_y));
                long duration = randInt(min_dur, max_dur);

                randSwipe(startPoint, endPoint, duration);
            }
        }, 0, randInt(min_rate, max_rate) * 1000);
    }

    private void showActionBar() {
        layout = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.action_bar, null);
        floatWindow = new FloatWindow.With(this, layout)
                .setModality(false)
                .setMoveAble(true)
                .create();
        floatWindow.show();
    }

    void randSwipe(Point startPoint, Point endPoint, long duration) {
        Path swipePath = new Path();
        swipePath.moveTo(startPoint.x, startPoint.y);
        swipePath.lineTo(endPoint.x, endPoint.y);
        GestureDescription.Builder gestureBuilder = new GestureDescription.Builder();
        gestureBuilder.addStroke(new GestureDescription.StrokeDescription(swipePath, 0, duration));
        dispatchGesture(gestureBuilder.build(), null, null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        L.d("onDestroy");
        if (floatWindow != null) {
            floatWindow.remove();
            floatWindow = null;
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

    }

    private void configureSwipeButton() {
        View btn_left = layout.findViewById(R.id.btn_left);
        btn_left.setOnClickListener(view -> {
            if (MyApplication.isActivityVisible()) {
                sendBroadcast(new Intent(Constants.ACTION_BACKPRESS));
            } else {
                SystemSetings.startApp(getApplicationContext(), getPackageName());
            }
        });
        CheckBox cbRead = layout.findViewById(R.id.cb_read);
        cbRead.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                startTimerTask();
            } else {
                stopTimerTask();
            }
        });
    }


    /**
     * 获取从 [min,max) 之间的一个随机数
     *
     * @param min
     * @param max
     * @return
     */
    int randInt(int min, int max) {
        Random rand = new Random();
        return rand.nextInt((max - min) + 1) + min;
    }

    @Override
    public void onInterrupt() {
        if (floatWindow != null) {
            floatWindow.remove();
            floatWindow = null;
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
    }
}
