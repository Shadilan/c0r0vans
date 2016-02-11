package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import coe.com.c0r0vans.GameSound;
import utility.Essages;
import utility.ImageLoader;
import utility.ResourceString;
import utility.serverConnect;

/**
 * @author Shadilan
 */
public class Route implements GameObject{
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
            loadJSON(obj);
    }


    public String getStartName(){return StartName;}
    public String getFinishName(){return FinishName;}
    public int getDistance(){return Distance;}

    @Override
    public Bitmap getImage() {
        return null;
    }

    @Override
    public Marker getMarker() {
        return null;
    }

    @Override
    public void setMarker(Marker m) {

    }

    @Override
    public void loadJSON(JSONObject obj) {
        try {
            if (obj.has("GUID")) GUID = obj.getString("GUID");
            if (obj.has("StartName")) StartName = obj.getString("StartName");
            if (obj.has("FinishName")) FinishName = obj.getString("FinishName");
            if (obj.has("StartGUID")) StartGUID = obj.getString("StartGUID");
            if (obj.has("FinishGUID")) FinishGUID = obj.getString("FinishGUID");
            if (obj.has("Distance")) Distance = obj.getInt("Distance");
            if (obj.has("Lat")) Lat = obj.getInt("Lat");
            if (obj.has("Lng")) Lng = obj.getInt("Lng");
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        dropRoute=new ObjectAction(this) {
            @Override
            public Bitmap getImage() {
                return ImageLoader.getImage("closebutton");
            }

            @Override
            public String getInfo() {
                return "удалить караван";
            }

            @Override
            public String getCommand() {
                if (FinishName.equals("null")) return "DropUnfinishedRoute";
                else return "DropRoute";
            }

            @Override
            public void preAction() {
                GameSound.playSound(GameSound.START_ROUTE_SOUND);
            }
            //Todo: Another sound;
            @Override
            public void postAction() {
                serverConnect.getInstance().getPlayerInfo();
                Essages.addEssage("Караван из "+getStartName()+" в "+getFinishName()+" отменен.");
            }

            @Override
            public void postError() {
                serverConnect.getInstance().getPlayerInfo();
            }
        };
    }

    @Override
    public void RemoveObject() {

    }

    @Override
    public String getInfo() {
        return "Маршрут";
    }

    @Override
    public ArrayList<ObjectAction> getActions() {
        return null;
    }

    public String getGUID(){return GUID;}

    @Override
    public void changeMarkerSize(int Type) {

    }

    private ObjectAction dropRoute;
    public ObjectAction getAction(){
      return dropRoute;
    }
}
