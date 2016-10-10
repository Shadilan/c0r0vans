package utility.notification;

import com.coe.c0r0vans.GameObjects.Message;

import java.util.EventListener;

/**
 * Created by zhitnikov.bronislav on 10.10.2016.
 */

public abstract class OnEssageListener implements EventListener {
    public abstract void onAdd(int type, Message msg);
    public abstract void onClear();
    public abstract void onRemove(Message msg);

}
