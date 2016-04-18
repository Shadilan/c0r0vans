package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import utility.GameSound;
import utility.ImageLoader;
import utility.internet.serverConnect;
import utility.notification.Essages;
import utility.settings.GameSettings;

/**
 * @author Shadilan
 */
public class Route extends GameObject{
    private String StartName;
    private String FinishName;
    private String StartGUID;
    private String FinishGUID;
    private int Distance;
    private int Lat;
    private int Lng;
    private LatLng StartPoint;
    private LatLng FinishPoint;

    private Polyline line;



    /*public Route(JSONObject obj){
            loadJSON(obj);
    }*/
    public Route(JSONObject obj,GoogleMap map){
        this.map=map;
        loadJSON(obj);
    }


    public String getStartName(){return StartName;}
    public String getFinishName(){return FinishName;}
    public int getDistance(){return Distance;}


    public void showRoute(){
        if (line!=null)
        {
            line.setVisible("Y".equals(GameSettings.getInstance().get("SHOW_CARAVAN_ROUTE")));
        }
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
            if (obj.has("StartLat") && obj.has("StartLng")) {
                if (!(obj.getInt("StartLat")==0 && obj.getInt("StartLng")==0))
                    StartPoint = new LatLng(obj.getInt("StartLat")/1e6,obj.getInt("StartLng")/1e6);

            }
            if (obj.has("FinishLat") && obj.has("FinishLng")) {
                if (!(obj.getInt("FinishLat")==0 && obj.getInt("FinishLng")==0))
                    FinishPoint = new LatLng(obj.getInt("FinishLat") / 1e6, obj.getInt("FinishLng") / 1e6);

            }
            if (StartPoint!=null && FinishPoint!=null) {
                PolylineOptions options = new PolylineOptions();
                options.width(3);
                options.color(Color.BLUE);
                options.add(StartPoint);
                options.add(FinishPoint);
                if (map!=null) {
                    line = map.addPolyline(options);
                    //Log.d("RouteTest","Line draw");
                    showRoute();

                }
            }
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
            public String getCommand() {
                if (FinishName.equals("null")) return "DropUnfinishedRoute";
                else return "DropRoute";
            }

            @Override
            public void preAction() {

            }
            //Todo: Another sound;
            @Override
            public void postAction(JSONObject response) {
                GameSound.playSound(GameSound.START_ROUTE_SOUND);
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
        if (line!=null) line.remove();
    }


    private ObjectAction dropRoute;

    public LatLng getPoint(){
        if (Lat==0 && Lng==0) return null;
        else return new LatLng(Lat/1e6,Lng/1e6);
    }
    public ObjectAction getAction( boolean inZone){
        return dropRoute;
    }

    public String getStartGuid() {
        return StartGUID;
    }

    public String getFinishGuid() {
        return FinishGUID;
    }

    public LatLng getStarPoint() {
        return StartPoint;
    }

    public LatLng getEndPoint() {
        return FinishPoint;
    }
    public void fadeRoute(){
        if (line!=null)
        line.setColor(Color.LTGRAY);
    }
    public void releaseFade(){
        if (line!=null)
        line.setColor(Color.BLUE);

    }

    public JSONObject getJSON() throws JSONException {
        JSONObject result=new JSONObject();

        result.put("GUID",GUID);
        result.put("StartName",StartName);
        result.put("FinishName",FinishName);
        result.put("StartGUID",StartGUID);
        result.put("FinishGUID",FinishGUID);
        result.put("Lat",Lat);
        result.put("Lng",Lng);


        return result;
    }
}
