package utility;

import android.os.Handler;

/**
 * Singleton Mainthread
 */
public class MainThread {
    private static Handler instance;
    public static void init(){
        instance=new Handler();
    }
    public static boolean post(Runnable r){
        return instance.post(r);
    }
    public static boolean postDelayed(Runnable r,long delay){
        return instance.postDelayed(r,delay);
    }
    public static void removeCallbacks(Runnable r){
        instance.removeCallbacks(r);
    }
}
