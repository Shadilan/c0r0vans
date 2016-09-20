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
        int val=Math.abs(time);
        if (val<=120) {
            if (String.valueOf(val).endsWith("1")) result = val + " минуту";
            else if (String.valueOf(val).endsWith("2") || String.valueOf(val).endsWith("3")
                    || String.valueOf(val).endsWith("4"))
                result = val + " минуты";
            else result=val + " минут";
        }
        else if (val > 120 && val < 2*60*24){
            val=time/60;
            if (String.valueOf(val).endsWith("1")) result = val + " час";
            else if (String.valueOf(val).endsWith("2") || String.valueOf(val).endsWith("3")
                    || String.valueOf(val).endsWith("4"))
                result = val + " часа";
            else result=val + " часов";
        } else if (val>=2*60*24){
            val=time/60/24;
            if (String.valueOf(val).endsWith("1")) result = val + " день";
            else if (String.valueOf(val).endsWith("2") || String.valueOf(val).endsWith("3")
                    || String.valueOf(val).endsWith("4"))
                result = val + " дня";
            else result=val + " дней";
        }
        return result;
    }

}
