package utility.notification;


import android.widget.LinearLayout;
import android.widget.TextView;

import com.coe.c0r0vans.GameObjects.Message;
import com.coe.c0r0vans.Singles.MessageMap;
import com.coe.c0r0vans.UIElements.MessageLayout.EssageLine;

import java.util.ArrayList;
import java.util.Date;

import utility.MainThread;

/**
 * @author Shadilan
 *         Class to show messages that system return
 */
public class Essages {

    private static ArrayList<Message> alert;
    private static ArrayList<Message> list;

    public static final int ALERT =1;
    public static final int SYSTEM =0;

    public static void clear(){
        if (list!=null && list.size()>0) {
            list.clear();
        }
        if (alert!=null && alert.size()>0) {
            alert.clear();
        }
        if (execListener())
            for (OnEssageListener l:listeners) l.onClear();
    }
    private static TextView essageCount;

    public static void remove(Message msg) {
        list.remove(msg);
        alert.remove(msg);
        if (execListener())
            for (OnEssageListener l:listeners) l.onRemove(msg);
    }

    public static void init() {
        if (alert==null) alert=new ArrayList<>();
        if (list==null) list=new ArrayList<>();
    }
    /**
     * Add Text to list
     *
     * @param text Text to add
     */
    public static void addEssage(int type,String text) {
        Message msg=new Message(text);
        addEssage(type,msg);
    }
    public static void addEssage(int type,Date time,String text) {
        Message msg=new Message(text,time);
        addEssage(type,msg);
    }
    public static void addEssage(int type,Message msg){
        if (msg.notify) MessageNotification.notify(msg.getMessage(), msg.getType());
        if (type==ALERT){
            if (alert==null) alert=new ArrayList<>();
            alert.add(msg);

        } else if (type==SYSTEM){
            if (list==null) list=new ArrayList<>();
            list.add(msg);
        }
        if (execListener())
            for (OnEssageListener l:listeners) l.onAdd(type,msg);

    }
    private static ArrayList<OnEssageListener> listeners;
    private static ArrayList<OnEssageListener> remListeners;
    public static void addListener(OnEssageListener listener){
        if (listeners==null) listeners=new ArrayList<>();
        listeners.add(listener);
    }
    public static void removeListener(OnEssageListener listener){
        if (remListeners==null) remListeners=new ArrayList<>();
        remListeners.add(listener);
    }
    private static boolean execListener(){
        if (listeners==null) return false;
        if (remListeners!=null && remListeners.size()>0) listeners.removeAll(remListeners);
        return true;
    }

    public static ArrayList<Message> getAlertList() {
        return alert;
    }
    public static ArrayList<Message> getSystemList() {
        return list;
    }
}
