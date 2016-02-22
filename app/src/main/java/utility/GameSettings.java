package utility;



import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;
import java.util.Map;

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

        save();
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
