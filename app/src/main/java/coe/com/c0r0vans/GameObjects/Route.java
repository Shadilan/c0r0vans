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
    private String FinishName;
    private String StartGUID;
    private String FinishGUID;
    private int Distance;
    private int Lat;
    private int Lng;

    public Route(){

    }
    public Route(JSONObject obj){
        try {
            LoadJSON(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void LoadJSON(JSONObject object) throws JSONException {
        if (object.has("GUID")) GUID=object.getString("GUID");
        if (object.has("StartName")) StartName=object.getString("StartName");
        if (object.has("FinishName")) FinishName=object.getString("FinishName");
        if (object.has("StartGUID")) StartGUID=object.getString("StartGUID");
        if (object.has("FinishGUID")) FinishGUID=object.getString("FinishGUID");
        if (object.has("Distance")) Distance=object.getInt("Distance");
        if (object.has("Lat")) Lat=object.getInt("Lat");
        if (object.has("Lng")) Lng=object.getInt("Lng");
    }
    public String getStartName(){return StartName;}
    public String getFinishName(){return FinishName;}
    public int getDistance(){return Distance;}
    public String getGUID(){return GUID;}
}
