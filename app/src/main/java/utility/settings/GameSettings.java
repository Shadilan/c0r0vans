package utility.settings;


import android.content.Context;
import android.content.SharedPreferences;

import com.coe.c0r0vans.GameObjects.GameObject;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import utility.GATracker;

/**
 * @author Shadilan
 * НАстройки приложения
 */
public class GameSettings extends HashMap<String,String>{
    private static GameSettings instance;
    public GoogleApiClient mClient;
    private Context ctx;
    private ArrayList<SettingsListener> settingsListeners;
    private ArrayList<SettingsListener> removedSettingsListeners;
    public static void addSettingsListener(SettingsListener listener){
        if (instance.settingsListeners==null){
            instance.settingsListeners=new ArrayList<>();
        }
        instance.settingsListeners.add(listener);
    }
    public static void removeSettingListener(SettingsListener listener){
        if (instance.removedSettingsListeners==null){
            instance.removedSettingsListeners=new ArrayList<>();
        }
        instance.removedSettingsListeners.add(listener);
    }

    private void checkListenerCount(){
        if (removedSettingsListeners ==null || settingsListeners == null) return;
        settingsListeners.removeAll(removedSettingsListeners);
        removedSettingsListeners.clear();

    }
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
    private void load(){
        SharedPreferences sp=ctx.getSharedPreferences("settings",Context.MODE_PRIVATE);
        this.clear();
        putAll((Map<String, String>) sp.getAll());
        if (size()==0){
            firstRun();
        }
        if (settingsListeners!=null) {
            checkListenerCount();
            for (SettingsListener listener : settingsListeners) {
                listener.onSettingsLoad();
            }
        }
    }
    public static String getValue(String key){
     return instance.get(key);
    }

    private void firstRun(){
        //Отображение
        put("SHOW_AMBUSH_RADIUS","Y");
        put("SHOW_CITY_RADIUS","Y");
        put("SHOW_CARAVAN_ROUTE","Y");
        put("SHOW_BUILD_AREA","N");
        put("SCREEN_OFF","Y");
        //Медия
        put("MUSIC_ON","N");
        put("SOUND_ON","N");
        put("VIBRATE_ON","N");
        put("NOTIFY_SOUND","Y");
        //
        put("USE_TILT","N");
        put("VIEW_PADDING","Y");//Быстрые действия
        put("CLOSE_WINDOW","Y");

        put("GPS_ON_BACK","N");
        put("GPS_REATE","3");
        put("TRACK_BEARING","N");

        put("SHOW_NETWORK_ERROR","N");



        put("BEARING","0");
        put("ZOOM","18");
        put("PLAYER_FACTION","0");
        put("AUTOCLOSE_WINDOW","N");
        save();
    }
    public static void set(String property,String value){
        String old_value=instance.get(property);
        if (!value.equals(old_value)) {
            instance.put(property, value);
            if (instance.settingsListeners != null) {
                instance.checkListenerCount();
                for (SettingsListener listener : instance.settingsListeners) {
                    listener.onSettingChange(property);
                }
            }
        }
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
        if (zoom!=null) result=Float.parseFloat(zoom);
        return result;
    }
    public static void setBearing(float bearing){
        set("BEARING", String.valueOf(bearing));
        instance.save();

    }
    public static void setZoom(float zoom){
        set("ZOOM", String.valueOf(zoom));
        instance.save();
    }
    public static void setFaction(int faction){
        String oldFaction=instance.get("PLAYER_FACTION");
        if (!String.valueOf(faction).equals(oldFaction)) {
            set("PLAYER_FACTION", String.valueOf(faction));
            instance.save();
        }
    }
    public static int getFaction(){
        String oldFaction=instance.get("PLAYER_FACTION");
        if ("".equals(oldFaction)||oldFaction ==null) oldFaction="0";
        int result=0;
        try{
            result=Integer.parseInt(oldFaction);
        } catch (Exception e){
            GATracker.trackException("GetFaction","ParsInt in Faction.");
        }
        return result;
    }

    public void save(){
        SharedPreferences sp=ctx.getSharedPreferences("settings", Context.MODE_PRIVATE);
        SharedPreferences.Editor ed=sp.edit();
        for (String key:instance.keySet()){
            ed.putString(key,instance.get(key));
        }
        ed.apply();
        if (settingsListeners!=null) {
            checkListenerCount();
            for (SettingsListener listener : settingsListeners) {
                listener.onSettingsSave();
            }
        }

    }
}
