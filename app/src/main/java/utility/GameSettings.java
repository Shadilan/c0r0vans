package utility;



import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import coe.com.c0r0vans.GameObjects.GameObject;

/**
 * @author Shadilan
 * НАстройки приложения
 */
public class GameSettings extends HashMap<String,String>{
    private static GameSettings instance;
    private Context ctx;
    public static void init(Context context){
        if (instance==null){
            instance=new GameSettings();
            instance.ctx=context;
            instance.load();
        }

    }
    public static GameSettings getInstance(){
        return instance;
    }
    public void load(){
        SharedPreferences sp=ctx.getSharedPreferences("settings",Context.MODE_PRIVATE);
        this.clear();
        putAll((Map<String, String>) sp.getAll());
        if (size()==0){
            firstRun();
        }
    }
    private void firstRun(){
        put("SHOW_AMBUSH_RADIUS","Y");
        put("SHOW_CITY_RADIUS","Y");
        put("MUSIC_ON","N");
        put("SOUND_ON","N");
        put("SHOW_CARAVAN_ROUTE","N");
        put("USE_TILT","N");
        put("NET_DEBUG","N");
        put("GPS_ON_BACK","N");
        put("GPS_REATE","3");
        put("AUTO_LOGIN","N");
        put("SHOW_NETWORK_ERROR","N");
        put("VIEW_PADDING","N");
        put("TRACK_BEARING","N");
        put("BEARING","0");
        put("ZOOM","18");
        save();
    }
    public static float getBearing(){
        float result =0;
        String bearing=instance.get("BEARING");
        if (bearing!=null) result=Float.parseFloat(bearing);
        return result;
    }
    public static float getZoom(){
        float result= GameObject.ICON_MEDIUM;
        String zoom=instance.get("ZOOM");
        Log.d("Clientzoom", "Get zoom " + zoom);
        if (zoom!=null) result=Float.parseFloat(zoom);
        return result;
    }
    public static void setBearing(float bearing){
        instance.put("BEARING", String.valueOf(bearing));
        instance.save();
    }
    public static void setZoom(float zoom){
        instance.put("ZOOM", String.valueOf(zoom));
        Log.d("Clientzoom", "Set zoom " + zoom);
        instance.save();
    }

    public void save(){
        SharedPreferences sp=ctx.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed=sp.edit();
        for (String key:instance.keySet()){
            ed.putString(key,instance.get(key));
        }
        ed.apply();
        ed.commit();
    }
}
