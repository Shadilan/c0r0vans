package com.coe.c0r0vans.GameObjects;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import utility.settings.GameSettings;

/**
 * @author Shadilan
 */
//// TODO: 21.05.2016 Привести к объекту караван
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
    private int time=0;

    private Polyline line;
    private int profit=0;


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
            if (obj.has("profit")) profit=obj.getInt("profit");
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
                GameObject target=SelectedObject.getInstance().getTarget();
                if (target!=null && target instanceof City && !(target.getGUID().equals(StartGUID) || target.getGUID().equals(FinishGUID)))
                    options.color(Color.LTGRAY);
                else options.color(Color.BLUE);
                options.geodesic(true);
                options.add(StartPoint);
                options.add(FinishPoint);

                if (map!=null) {
                    line = map.addPolyline(options);

                    //Log.d("RouteTest","Line draw");
                    showRoute();


                }
            }
            Player.getPlayer().getUpgrade("speed");
            //time=S/v + (v*v-(1+a)*(1+a))/2/(a)/v-0.7
            Upgrade up=Player.getPlayer().getUpgrade("speed");
            if (up!=null && up.getEffect2()>0) {
                int v=up.getEffect2();
                int a=up.getEffect1();
                int S=Distance;
                int S0= (int) Math.ceil((Math.pow(v,2)-1)/2/a);
                if (S0>S) time= (int) Math.ceil(Math.sqrt(2*S/a));
                else {
                    int t0 = (int) Math.ceil(v / a);
                    time = (S - S0) / v + t0;
                }
            }
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void RemoveObject() {
        if (line!=null) line.remove();
    }


    public LatLng getPoint(){
        if (Lat==0 && Lng==0) return null;
        else return new LatLng(Lat/1e6,Lng/1e6);
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
        result.put("profit",profit);
        result.put("Lat",Lat);
        result.put("Lng",Lng);


        return result;
    }

    public int getProfit() {
        return profit;
    }

    public int getTime() {
        return time;
    }
}
