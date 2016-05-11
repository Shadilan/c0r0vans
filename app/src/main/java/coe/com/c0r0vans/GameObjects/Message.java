package coe.com.c0r0vans.GameObjects;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * @author Shadilan
 * Контейнер сообщения и загрузчик
 */
public class Message {
    private String GUID;
    private String message;
    private String type;
    private String state;
    private LatLng target;
    private Date time;
    public boolean notify=true;
    private MessageMap parent;
    public void remove(){
        if (parent!=null) parent.remove(GUID);
    }
    public Message(JSONObject jsonObject) throws JSONException {
        loadJSON(jsonObject);
    }
    public Message(String text){
        GUID="";
        message=text;
        type="";
        state="";
        target=null;
        time=new Date();
        notify=false;
    }
    public Message(String text,Date date){
        GUID="";
        message=text;
        type="";
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
        if (jsonObject.has("Type")) type = jsonObject.getString("Type");
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
        if (type!=null)result.put("Type",type);
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

    public void setParent(MessageMap parent) {
        this.parent = parent;
    }
}
