package utility.SwipeDetectLayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.RelativeLayout;

/**
 * Created by Shadilan on 25.09.2016.
 */
public class SwipeDetectLayout extends RelativeLayout {
    public SwipeDetectLayout(Context context) {
        super(context);
    }

    public SwipeDetectLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SwipeDetectLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SwipeDetectLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    OnSwipeListener listener;
    public void setOnSwipeListener(OnSwipeListener listener){
        this.listener=listener;
    }
    private float x1,x2;
    private float y1,y2;
    static final int MIN_DISTANCE=150;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch(ev.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = ev.getX();
                y1 =ev.getY();
                Log.d("Test","TestCallDown");
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                x2 = ev.getX();
                y2 = ev.getY();
                Log.d("Test","TestCallUp");
                if (listener!=null) {
                    float deltaX = x2 - x1;
                    float deltaY = y2 - y1;
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        //swiping right to left
                        if (deltaX < 0) {
                            listener.onSwipeLeft();
                        } else if (deltaX > 0) {
                            listener.onSwipeRight();
                        }
                    } else if (Math.abs(deltaY)>MIN_DISTANCE){
                        if (deltaY < 0) {
                            listener.onSwipeUp();
                        } else if (deltaY > 0) {
                            listener.onSwipeDown();
                        }
                    }

                }
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    /*@Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean result=false;
        switch(ev.getActionMasked())
        {
            case MotionEvent.ACTION_DOWN:
                x1 = ev.getX();
                y1 =ev.getY();
                Log.d("Test","TestCallDown");
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                x2 = ev.getX();
                y2 = ev.getY();
                Log.d("Test","TestCallUp");
                if (listener!=null) {
                    result=false;
                    float deltaX = x2 - x1;
                    float deltaY = y2 - y1;
                    if (Math.abs(deltaX) > MIN_DISTANCE) {
                        //swiping right to left
                        if (deltaX < 0) {
                                listener.onSwipeLeft();
                        } else if (deltaX > 0) {
                                listener.onSwipeRight();
                        }
                    } else if (Math.abs(deltaY)>MIN_DISTANCE){
                        if (deltaY < 0) {
                            listener.onSwipeUp();
                        } else if (deltaY > 0) {
                            listener.onSwipeDown();
                        }
                    }

                }
                break;
        }
        return result;
    }*/

}
