package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import coe.com.c0r0vans.GameSound;
import utility.Essages;
import utility.ImageLoader;
import utility.serverConnect;

/**
 * @author Shadilan
 * Элемент списка засад
 */
public class AmbushItem extends GameObject{
    private int Lat;
    private int Lng;

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


    public LatLng getLatLng(){return new LatLng(Lat/1e6,Lng/1e6);}
    public ObjectAction getAction(boolean inZone){
        return cancelAmbush;
    }
    public LatLng getPoint(){
        LatLng result=new LatLng(Lat/1e6,Lng/1e6);
        return result;
    }
}
