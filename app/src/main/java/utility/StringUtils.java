package utility;

import android.content.Context;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


/**
 * Строковые утилиты
 */
public class StringUtils {
    private static NumberFormat nf;
    public static String intToStr(int num){
        if (nf==null){
            nf=NumberFormat.getInstance();
            nf.setGroupingUsed(true);
        }
        return nf.format(num);
    }
    private static DateFormat df;
    public static String dateToStr(Date date){
        if (df==null){
            df= new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.getDefault());
        }
        return df.format(date);
    }
    public static String longToStr(long num){
        if (nf==null){
            nf=NumberFormat.getInstance();
            nf.setGroupingUsed(true);
        }
        return nf.format(num);
    }
    public static String MD5(String md5) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
            byte[] array = md.digest(md5.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte anArray : array) {
                sb.append(Integer.toHexString((anArray & 0xFF) | 0x100).substring(1, 3));
            }
            return sb.toString();
        } catch (java.security.NoSuchAlgorithmException e) {
            GATracker.trackException("MD5","GenrateMD5");
        }
        return null;
    }
    private static Context context;
    public static void init(Context ctx){
        context=ctx;
    }
    public static String getString(int id){
        if (context==null) return null;
        return context.getString(id);
    }
    public static String getTime(int time){
        String result="";
        if (Math.abs(time) == 1 ) result= "1"+ " минуту";
        else if (Math.abs(time) == 2 || Math.abs(time)  == 3) result= Math.abs(time)+ " минуты";
        else if (Math.abs(time) <=120 ) result= Math.abs(time)+ " минут";
        else if (Math.abs(time) > 120 && Math.abs(time) < 270) result= Math.abs(Math.round(time/60))+ " часа";
        else if (Math.abs(time) > 270 && Math.abs(time) < 2*60*24) result= Math.abs(Math.round(time/60))+ " часа";
        else if (Math.abs(time) >= 4*30*24 && Math.abs(time) < 9*30*24) result= (Math.round(time / 60 / 24)) + " дня";
        else if (Math.abs(time) >= 9*30*24) result= (Math.round(time / 60 / 24)) + " дней";
        return result;
    }

}
