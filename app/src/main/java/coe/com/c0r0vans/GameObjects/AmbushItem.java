package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import coe.com.c0r0vans.GameSound;
import utility.Essages;
import utility.ImageLoader;
import utility.serverConnect;

/**
 * @author Shadilan
 * Элемент списка засад
 */
public class AmbushItem implements GameObject{
    private String GUID;
    private String Name;
    private int Lat;
    private int Lng;
    private int progress=0;
    public AmbushItem(){

    }
    public AmbushItem(JSONObject obj){
        try {
            loadJSON(obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    ObjectAction cancelAmbush;
    public void loadJSON(JSONObject object) throws JSONException {
        if (object.has("GUID")) GUID=object.getString("GUID");
        if (object.has("Name")) Name=object.getString("Name");
        if (object.has("Lat")) Lat=object.getInt("Lat");
        if (object.has("Lng")) Lng=object.getInt("Lng");
        if (object.has("Progress")) progress=object.getInt("Progress");
        cancelAmbush=new ObjectAction(this) {
            @Override
            public Bitmap getImage() {
                return ImageLoader.getImage("closebutton");
            }

            @Override
            public String getInfo() {
                return "Отменить засаду";
            }

            @Override
            public String getCommand() {
                return "CancelAmbush";
            }

            @Override
            public void preAction() {

            }
            //Todo: Another sound;
            @Override
            public void postAction() {
                GameSound.playSound(GameSound.START_ROUTE_SOUND);
                serverConnect.getInstance().getPlayerInfo();
                Essages.addEssage("Засада распущена");
            }

            @Override
            public void postError() {
                serverConnect.getInstance().getPlayerInfo();
            }
        };
    }
    public String getName(){return Name;}

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
    public void RemoveObject() {

    }

    @Override
    public String getInfo() {
        return null;
    }

    @Override
    public ArrayList<ObjectAction> getActions() {
        return null;
    }

    public String getGUID(){return GUID;}

    @Override
    public void changeMarkerSize(int Type) {

    }

    @Override
    public int getProgress() {
        return progress;
    }

    @Override
    public void setVisibility(boolean visibility) {

    }

    public LatLng getLatLng(){return new LatLng(Lat/1e6,Lng/1e6);}
    public ObjectAction getAction(){
        return cancelAmbush;
    }
}
