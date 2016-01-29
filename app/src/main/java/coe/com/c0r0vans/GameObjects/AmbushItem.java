package coe.com.c0r0vans.GameObjects;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author Shadilan
 * Элемент списка засад
 */
public class AmbushItem {
    private String GUID;
    private String Name;
    private int Lat;
    private int Lng;
    public AmbushItem(){

    }
    public AmbushItem(JSONObject obj){
        try {
            LoadJSON(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void LoadJSON(JSONObject object) throws JSONException {
        if (object.has("GUID")) GUID=object.getString("GUID");
        if (object.has("Name")) Name=object.getString("Name");
        if (object.has("Lat")) Lat=object.getInt("Lat");
        if (object.has("Lng")) Lng=object.getInt("Lng");
    }
    public String getName(){return Name;}
    public String getGUID(){return GUID;}
}
