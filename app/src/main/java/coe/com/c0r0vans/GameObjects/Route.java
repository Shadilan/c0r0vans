package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import utility.ResourceString;

/**
 * @author Shadilan
 */
public class Route {
    private String GUID;
    private String StartName;
    private String EndName;
    private Number Gold;
    private Number TimeToGo;
    public Route(){

    }
    public Route(JSONObject obj){
        LoadJSON(obj);
    }
    public void LoadJSON(JSONObject obj){
        try {
            GUID=obj.getString("GUID");
            StartName=obj.getString("StartName");
            EndName=obj.getString("EndName");
            Gold=obj.getInt("Gold");
            TimeToGo=obj.getInt("Time");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public String getInfo(){
        return ResourceString.getInstance().getString("begin")+StartName+"\n"
                +ResourceString.getInstance().getString("end")+EndName+"\n"
                +ResourceString.getInstance().getString("gold")+Gold+"\n"
                +ResourceString.getInstance().getString("time_to_go")+TimeToGo;
    }
    public String getGUID(){
        return GUID;
    }
}
