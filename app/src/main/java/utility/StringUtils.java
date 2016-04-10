package utility;

import java.text.NumberFormat;


/**
 * Created by Shadilan on 10.04.2016.
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
    public static String longToStr(long num){
        if (nf==null){
            nf=NumberFormat.getInstance();
            nf.setGroupingUsed(true);
        }
        return nf.format(num);
    }
}
