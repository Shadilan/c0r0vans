package utility;

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

}
