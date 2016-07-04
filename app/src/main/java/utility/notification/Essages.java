package utility.notification;


import android.widget.LinearLayout;

import com.coe.c0r0vans.GameObjects.Message;
import com.coe.c0r0vans.UIElements.EssageLine;

import java.util.ArrayList;
import java.util.Date;

import utility.MainThread;

/**
 * @author Shadilan
 *         Class to show messages that system return
 */
public class Essages {
    private static LinearLayout target;

    private static ArrayList<Message> list;
    public static void clear(){
        if (list!=null && list.size()>0) {
            list.clear();
            target.removeAllViews();
        }
    }
    public static void setTarget(LinearLayout target){
        Essages.target=target;
        if (list!=null && list.size()>0){
            for (Message msg:list){
                EssageLine line=new EssageLine(target.getContext());
                line.setText(msg);
                line.setParentForm(target);
                target.addView(line, 0);
            }
            list.clear();
        }
    }



    private static class MyRunnable implements Runnable{
        private Message msg;
        public MyRunnable(Message msg){
            this.msg=msg;

        }
        @Override
        public void run() {
            if (target!=null) {
                EssageLine line = new EssageLine(target.getContext());
                line.setText(msg);
                line.setParentForm(target);
                target.addView(line, 0);
            }
        }
    }
    /**
     * Add Text to list
     *
     * @param text Text to add
     */
    public static void addEssage(String text) {
        Message msg=new Message(text);
        addEssage(msg);
    }
    public static void addEssage(Date time,String text) {
        Message msg=new Message(text,time);
        addEssage(msg);
    }
    public static void addEssage(Message msg){
        MainThread.post(new MyRunnable(msg));
        if (msg.notify) MessageNotification.notify(msg.getMessage(), msg.getType());
    }
}
