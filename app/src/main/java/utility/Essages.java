package utility;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * @author Shadilan
 *         Class to show messages that system return
 */
public class Essages {
    private static TextView target;


    public static void setTarget(TextView target){
        Essages.target=target;
    }
    private static DateFormat df = DateFormat.getDateTimeInstance();

    /**
     * Add Text to list
     *
     * @param text Text to add
     */
    public static void addEssage(String text) {
        target.append("\n" + df.format(new Date()) + ":" + text);
    }








}
