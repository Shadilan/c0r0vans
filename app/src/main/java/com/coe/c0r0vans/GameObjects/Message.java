package com.coe.c0r0vans.GameObjects;

import com.coe.c0r0vans.Singles.MessageMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * @author Shadilan
 * Контейнер сообщения и загрузчик
 */
public class Message {
    public static final int AMBUSH_DESTROYED=2;
    public static final int CARAVAN_DESTROYED=1;
    public static final int CARAVAN_CATCHED=3;
    public static final int UNKNOWN=0;
    public static final int LORE=10;
    private String GUID;
    private String message;
    private int type;
    private String state;
    private LatLng target;
    private Date time;
    public boolean notify=true;
    public void remove(){
        MessageMap.remove(GUID);
    }
    public Message(JSONObject jsonObject) throws JSONException {
        loadJSON(jsonObject);
    }
    public Message(String text){
        GUID="";
        message=text;
        type=0;
        state="";
        target=null;
        time=new Date();
        notify=false;
    }
    public Message(String text,Date date){
        GUID="";
        message=text;
        type=0;
        state="";
        target=null;
        time=date;
        notify=false;
    }
    public void loadJSON(JSONObject jsonObject) throws JSONException {
        int lat = 0;
        int lng = 0;
        if (jsonObject.has("GUID")) GUID = jsonObject.getString("GUID");
        if (jsonObject.has("Message")) message = jsonObject.getString("Message");
        try {
            if (jsonObject.has("Type")) type = jsonObject.getInt("Type"); else type=0;
        } catch (Exception e){
            type=0;
        }
        if (jsonObject.has("State")) state = jsonObject.getString("State");
        if (jsonObject.has("Time")) time = new Date(jsonObject.getLong("Time"));
        if (jsonObject.has("TargetLat")) lat = jsonObject.getInt("TargetLat");
        if (jsonObject.has("TargetLng")) lng = jsonObject.getInt("TargetLng");
        notify = !jsonObject.has("notify") || jsonObject.getBoolean("notify");

        if (lat != 0 && lng != 0) {
            target = new LatLng((double) lat / 1e6, (double) lng / 1e6);
        }

    }
    public JSONObject getJSON() throws JSONException {
        JSONObject result=new JSONObject();
        if (GUID!=null) result.put("GUID",GUID);
        if (GUID!=null)result.put("Message",message);
        if (type!=0)result.put("Type",type);
        if (state!=null)result.put("State",state);
        if (time!=null)result.put("Time",time.getTime());
        result.put("notify",false);
        if (target!=null) {
            result.put("TargetLat", (int) (target.latitude * 1e6));
            result.put("TargetLng", (int) (target.longitude * 1e6));
        }
        return result;
    }
    public String getGUID() {
        return GUID;
    }

    public Date getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }

    public LatLng getTarget(){ return target;}



    public int getType() {
        return type;
    }
}
