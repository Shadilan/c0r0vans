package utility;

import java.util.EventListener;

/**
 * Created by Shadilan on 25.09.2016.
 */
public abstract class OnSwipeListener implements EventListener {
    public abstract void onSwipeRight();
    public abstract void onSwipeLeft();
    public abstract void onSwipeUp();
    public abstract void onSwipeDown();
}
