package coe.com.c0r0vans.GameObjects;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import coe.com.c0r0vans.MyGoogleMap;
import utility.GameSettings;
import utility.ImageLoader;

/**
 * Caravan Object
 */
public class Caravan extends GameObject {
    private LatLng start;
    private LatLng finish;
    private String startName;
    private String finishName;

    private int faction=0;
    private double speed=20;

    public Caravan(GoogleMap map,JSONObject obj) throws JSONException {
        this.map=map;
        loadJSON(obj);

    }


    @Override
    public void setMarker(Marker m) {
        mark=m;
        changeMarkerSize(MyGoogleMap.getClientZoom());
    }

    @Override
    public void loadJSON(JSONObject obj) {
        try {
            GUID=obj.getString("GUID");
            int Lat=obj.getInt("Lat");
            int Lng=obj.getInt("Lng");
            LatLng latlng=new LatLng(Lat / 1e6, Lng / 1e6);
            if (obj.has("Owner")) faction=obj.getInt("Owner");
            if (obj.has("StartName")) startName=obj.getString("StartName");
            if (obj.has("FinishName")) finishName=obj.getString("FinishName");
            if (obj.has("Speed")) speed=obj.getDouble("Speed");
            if (obj.has("StartLat") && obj.has("StartLng")){
                double lat=obj.getInt("StartLat")/1e6;
                double lng=obj.getInt("StartLng")/1e6;
                start=new LatLng(lat,lng);
            } else start=null;
            if (obj.has("FinishLat") && obj.has("FinishLng")){
                double lat=obj.getInt("FinishLat")/1e6;
                double lng=obj.getInt("FinishLng")/1e6;
                finish=new LatLng(lat,lng);
            } else finish=null;

            if (mark==null) {
                setMarker(map.addMarker(new MarkerOptions().position(latlng)));
            } else {
                mark.setPosition(latlng);
            }
            mark.setVisible(true);




        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public String getInfo() {
        String tushkan="";
        if (Math.random()*1000<3) tushkan="На крыше видна тень кого-то невидимого.";
        if (faction==0) if (speed>0) return "Ваш караван направляется из города "+startName+" в город "+finishName +", готовясь принести вам золото."+tushkan;
        else return "Ваш караван направляется из города "+finishName+" в город "+startName +", готовясь принести вам золото."+tushkan;
            else return "Чейто караван проезжает, звеня не ВАШИМ золотом.";
    }
    private ObjectAction dropRoute;

    @Override
    public void changeMarkerSize(float Type) {
        if (mark!=null) {
            String markname = "caravan";
            if (faction<0 || faction>4) faction=4;
            if (faction==0) markname=markname+"_"+faction+Player.getPlayer().getRace();
            else markname=markname+"_"+faction;
            markname = markname + GameObject.zoomToPostfix(Type);
            mark.setIcon(ImageLoader.getDescritor(markname));
            if ("Y".equals(GameSettings.getInstance().get("USE_TILT"))) mark.setAnchor(0.5f, 1f);
            else mark.setAnchor(0.5f, 0.5f);
        }
    }

    public int getIsOwner(){
        return faction;
    }
}
