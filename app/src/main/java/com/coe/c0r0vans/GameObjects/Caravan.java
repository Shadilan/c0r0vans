package com.coe.c0r0vans.GameObjects;

import com.coe.c0r0vans.MyGoogleMap;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import utility.ImageLoader;
import utility.settings.GameSettings;

/**
 * Caravan Object
 */
public class Caravan extends GameObject {
    /*private LatLng start;
    private LatLng finish;
    private String startName;
    private String finishName;*/

    private int faction=0;
//    private double speed=20;

    public Caravan(GoogleMap map,JSONObject obj) throws JSONException {
        this.map=map;
        loadJSON(obj);

    }


    @Override
    public void setMarker(Marker m) {
        mark=m;
        changeMarkerSize();
    }

    @Override
    public void loadJSON(JSONObject obj) {
        try {
            GUID=obj.getString("GUID");
            if (obj.has("Owner")) faction=obj.getInt("Owner"); else faction=4;
            if (obj.has("Lat") && obj.has("Lng")) {

                int Lat = obj.getInt("Lat");
                int Lng = obj.getInt("Lng");
                LatLng latlng = new LatLng(Lat / 1e6, Lng / 1e6);
                if (mark == null) {
                    setMarker(map.addMarker(new MarkerOptions().position(latlng)));
                } else {
                    mark.setPosition(latlng);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private String currentMarkName="";
    @Override
    public void changeMarkerSize() {
        if (mark!=null) {
            String markname = "caravan";
            if (faction<0 || faction>4) faction=4;
            if (faction==0) markname=markname+"_"+faction+Player.getPlayer().getRace();
            else markname=markname+"_"+faction;
            markname = markname + GameObject.zoomToPostfix(MyGoogleMap.getClientZoom());
            if (!markname.equals(currentMarkName)) {
                mark.setIcon(ImageLoader.getDescritor(markname));
                if ("Y".equals(GameSettings.getInstance().get("USE_TILT")))
                    mark.setAnchor(0.5f, 1f);
                else mark.setAnchor(0.5f, 0.5f);
                currentMarkName=markname;
            }
        }
    }

    public int getFaction(){
        return faction;
    }
}
