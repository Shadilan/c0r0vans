package coe.com.c0r0vans.GameObjects;

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
    public Message(JSONObject jsonObject) throws JSONException {
        loadJSON(jsonObject);
    }
    public void loadJSON(JSONObject jsonObject) throws JSONException {
        int lat=0;
        int lng=0;
        if (jsonObject.has("GUID")) GUID=jsonObject.getString("GUID");
        if (jsonObject.has("Message")) message=jsonObject.getString("Message");
        if (jsonObject.has("Type")) type=jsonObject.getString("Type");
        if (jsonObject.has("State")) state=jsonObject.getString("State");
        if (jsonObject.has("Time")) time=new Date(jsonObject.getLong("Time"));
        if (jsonObject.has("Lat")) lat=jsonObject.getInt("Lat");
        if (jsonObject.has("Lng")) lng=jsonObject.getInt("Lat");
        if (lat!=0 && lng!=0) target=new LatLng((int)(lat/1e6),(int)(lng/1e6));

    }
    public JSONObject getJSON() throws JSONException {
        JSONObject result=new JSONObject();
        if (GUID!=null) result.put("GUID",GUID);
        if (GUID!=null)result.put("Message",message);
        if (type!=null)result.put("Type",type);
        if (state!=null)result.put("State",state);
        if (time!=null)result.put("Time",time.getTime());
        if (target!=null) {
            result.put("Lat", (int) (target.latitude * 1e6));
            result.put("Lng", (int) (target.longitude * 1e6));
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
}
