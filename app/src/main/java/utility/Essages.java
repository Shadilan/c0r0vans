package utility;

import android.widget.LinearLayout;

import java.text.DateFormat;
import java.util.Date;

import coe.com.c0r0vans.EssageLine;
import coe.com.c0r0vans.GameObjects.Message;
import coe.com.c0r0vans.MessageNotification;

/**
 * @author Shadilan
 *         Class to show messages that system return
 */
public class Essages {
    private static LinearLayout target;


    public static void setTarget(LinearLayout target){
        Essages.target=target;
    }
    private static DateFormat df = DateFormat.getDateTimeInstance();

    /**
     * Add Text to list
     *
     * @param text Text to add
     */
    public static void addEssage(String text) {
        if (target!=null) {

            EssageLine line=new EssageLine(target.getContext());
            line.setText(df.format(new Date()) + ":" + text);
            line.setParentForm(target);
            target.addView(line);
        }
    }
    public static void addEssage(Date time,String text) {
        if (target!=null) {
            EssageLine line=new EssageLine(target.getContext());
            line.setText(df.format(time) + ":" + text);
            line.setParentForm(target);
            target.addView(line);

            MessageNotification.notify(text, 1);

        }
    }
    public static void addEssage(Message msg){
        if (target!=null){
            EssageLine line=new EssageLine(target.getContext());
            line.setText(msg);
            line.setParentForm(target);
            target.addView(line);
            MessageNotification.notify(msg.getMessage(), 1);
        }
    }








}
