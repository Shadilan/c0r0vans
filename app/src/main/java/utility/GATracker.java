package utility;

import android.util.Log;

import com.coe.c0r0vans.CorovanApplication;
import com.coe.c0r0vans.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;

import utility.internet.serverConnect;

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
        serverConnect.getInstance().sendDebug(category,action,1,0);
        Log.d(category,action);

    }
    public static void trackHit(String category,String action,int count){
        mTracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel("Hit").setValue(count).build());
        serverConnect.getInstance().sendDebug(category,action,count,0);
        Log.d(category,action+"="+count);
    }
    private static void trackTime(String category, String action, long time){
        //mTracker.send(new HitBuilders.TimingBuilder().setCategory(category).setVariable(action).setLabel("Time").setValue(time).build());
        serverConnect.getInstance().sendDebug(category,action,0,time);

    }
    public static void trackException(String action,Exception e){
        Log.d("Exception",action+":"+Arrays.toString(e.getStackTrace()));
        mTracker.send(new HitBuilders.ExceptionBuilder().setDescription(action+":"+Arrays.toString(e.getStackTrace())).build());
    }
    public static void trackException(String action,String text){

        mTracker.send(new HitBuilders.ExceptionBuilder().setDescription(action+":"+text).build());
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
