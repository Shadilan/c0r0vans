package utility;

import com.coe.c0r0vans.GameObjects.CorovanApplication;
import com.coe.c0r0vans.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

/**
 * GoogleAnalitics Tracker
 */
public class GATracker {
    private static Tracker mTracker;
    public static void initialize(CorovanApplication application){
        mTracker=application.getDefaultTracker();
        mTracker.setScreenName("Default");
        mTracker.enableExceptionReporting(true);
        mTracker.setAppVersion(application.getResources().getString(R.string.version));
    }
    public static void trackHit(String category,String action){
        mTracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel("Hit").build());
    }
    public static void trackTime(String category,String action,long time){
        mTracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel("Time").setValue(time).build());
    }
    public static void trackException(String action,Exception e){
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Exception").setAction(action).setLabel(e.toString()).set("Description", Arrays.toString(e.getStackTrace())).build());
    }
    public static void trackException(String action,String text){
        mTracker.send(new HitBuilders.EventBuilder().setCategory("Exception").setAction(action).setLabel(text).build());
    }
    private static HashMap<String,Long> timers;
    private static void checkInitTimers(){
        if (timers==null) timers=new HashMap();
    }
    public static void trackTimeStart(String category,String action){
        checkInitTimers();
        timers.put(category+action,new Date().getTime());
    }
    public static void trackTimeEnd(String category,String action){
        checkInitTimers();
        Long t=timers.get(category+action);
        if (t!=null) {
            timers.remove(category+action);
            long res = new Date().getTime() - t;
            trackTime(category, action, res);
        }
    }

}
