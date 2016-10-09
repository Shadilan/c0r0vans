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
    private static EssageLine target;
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
        if (target!=null){
            target.hide();
        }
        MessageMap.clearAll();
    }
    private static TextView essageCount;
    public static void setTarget(EssageLine target, TextView cnt){
        Essages.target=target;
        essageCount=cnt;

    }

    public static void remove(Message msg) {
        list.remove(msg);
        alert.remove(msg);
    }

    public static void init() {
        if (alert==null) alert=new ArrayList<>();
        if (list==null) list=new ArrayList<>();
    }


    private static class MyRunnable implements Runnable{
        private Message msg;
        public MyRunnable(Message msg){
            this.msg=msg;


        }
        @Override
        public void run() {
            if (target!=null) {
                target.setText(msg);
                target.setParentForm(null);
            }

        }
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
        MainThread.post(new MyRunnable(msg));
        if (msg.notify) MessageNotification.notify(msg.getMessage(), msg.getType());
        if (type==ALERT){
            if (alert==null) alert=new ArrayList<>();
            alert.add(msg);
            if (essageCount!=null) {
                String t = essageCount.getText().toString();
                int cnt = 0;
                if ("".equals(t) || t == null) cnt = 1;
                else cnt = Integer.getInteger(t) + 1;
                essageCount.setText(String.valueOf(cnt));
            }

        } else if (type==SYSTEM){
            if (list==null) list=new ArrayList<>();
            list.add(msg);
        }

    }

    public static ArrayList<Message> getAlertList() {
        return alert;
    }
    public static ArrayList<Message> getSystemList() {
        return list;
    }
}
