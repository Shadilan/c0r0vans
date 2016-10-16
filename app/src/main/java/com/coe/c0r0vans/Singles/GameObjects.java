package com.coe.c0r0vans.Singles;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.coe.c0r0vans.GameObject.ActiveObject;
import com.coe.c0r0vans.GameObject.GameObject;
import com.coe.c0r0vans.Logic.Ambush;
import com.coe.c0r0vans.Logic.Caravan;
import com.coe.c0r0vans.Logic.Chest;
import com.coe.c0r0vans.Logic.City;
import com.coe.c0r0vans.Logic.Player;
import com.coe.c0r0vans.UIElements.ChooseFaction;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import utility.GATracker;
import utility.GPSInfo;
import utility.internet.ServerListener;
import utility.internet.serverConnect;
import utility.notification.Essages;
import utility.settings.GameSettings;
import utility.settings.SettingsListener;


/**
 * Перечень игровых объектов на карте
 */
public class GameObjects{
    private static HashMap<String,GameObject> objects;
    private static Player player;

    private static HashMap<String,Chest> chests;
    public static void updateZoom(){
        for (GameObject obj : getInstance().values())
            if (obj.getMarker() != null) obj.changeMarkerSize();
        for (Chest chest:chests.values()) chest.changeMarkerSize();
        player.changeMarkerSize();
    }
    public static ActiveObject getClosestObject(LatLng latLng){
        float closest=1000;
        ActiveObject closestObject=null;
        for (Chest o:chests.values()){
            if (o.getMarker()!=null && o.isVisible() && o.getMarker().isVisible() && o.getPosition()!=null){
                float dist = GPSInfo.getDistance(latLng, o.getPosition());
                float pdist = GPSInfo.getDistance(player.getPosition(),o.getPosition());
                if (dist <  o.getRadius() && pdist<=player.getActionDistance()) {
                    closestObject = o;
                    break;
                }
            }
        }
        if (closestObject==null) {
            for (GameObject o : activeObjects.values()) {
                if (o instanceof ActiveObject && o.getMarker() != null && o.getMarker().isVisible()) {

                    if (o.getPosition() != null) {

                        float dist = GPSInfo.getDistance(latLng, o.getPosition());
                        if (o instanceof Chest && dist < ((ActiveObject) o).getRadius()) {
                            closestObject = (ActiveObject) o;
                            break;
                        } else if (dist < closest && dist < ((ActiveObject) o).getRadius()) {
                            closest = dist;
                            closestObject = (ActiveObject) o;
                            if (o instanceof Chest) break;
                        }
                    }
                }
            }
        }

        return closestObject;
    }

    public static void init(final Context context){
        player=new Player();
        objects=new HashMap<>();
        activeObjects=new HashMap<>();
        chests =new HashMap<>();
        SharedPreferences sp = context.getSharedPreferences("player", Context.MODE_PRIVATE);
        String pls = sp.getString("player", "");
        if (!"".equals(pls)) {
            try {
                GameObjects.getPlayer().loadJSON(new JSONObject(pls));
            } catch (JSONException e) {
                GATracker.trackException("LoadPlayer",e);
            }
        }

        serverConnect.getInstance().addListener(new ServerListener() {
            @Override
            public void onResponse(int TYPE, JSONObject response) {
                if (TYPE==REFRESH){
                    try {
                        //Проверить наличие массива JSON. Objects
                        if (response.has("Objects")) {
                            //Скопировать данные в массив для удаления
                            GATracker.trackTimeStart("DonwloadData","ReadyMassive");
                            ArrayList<GameObject> remObjects = new ArrayList<>(objects.values());
                            JSONArray JObj = response.getJSONArray("Objects");
                            int leng = JObj.length();

                            GATracker.trackTimeEnd("DonwloadData","ReadyMassive");
                            for (int i = 0; i < leng; i++) {

                                GameObject robj;
                                String GUID=JObj.getJSONObject(i).getString("GUID");
                                robj=objects.get(GUID);
                                if (robj==null) robj=player.getAmbushes().get(GUID);
                                if (robj==null) robj=player.getRoutes().get(GUID);
                                if (robj != null) {

                                    remObjects.remove(robj);
                                    robj.loadJSON(JObj.getJSONObject(i));

                                } else {
                                    if (JObj.getJSONObject(i).getString("Type").equalsIgnoreCase("Player")) {

                                        GameObjects.getPlayer().loadJSON(JObj.getJSONObject(i));


                                    } else if (JObj.getJSONObject(i).getString("Type").equalsIgnoreCase("City")) {
                                        put(new City(MyGoogleMap.getMap(), JObj.getJSONObject(i)));


                                    } else if (JObj.getJSONObject(i).getString("Type").equalsIgnoreCase("Ambush")) {

                                        Ambush ambush = new Ambush(MyGoogleMap.getMap(), JObj.getJSONObject(i));
                                        /*if (ambush.isOwner()) player.getAmbushes().put(ambush.getGUID(),ambush);
                                        else objects.put(ambush.getGUID(), ambush);*/
                                        put(ambush);

                                    } else if (JObj.getJSONObject(i).getString("Type").equalsIgnoreCase("Caravan")) {

                                        Caravan caravan = new Caravan(MyGoogleMap.getMap(), JObj.getJSONObject(i));
                                        /*if (caravan.isOwner()) player.getRoutes().put(caravan.getGUID(), caravan);
                                        else objects.put(caravan.getGUID(), caravan);*/
                                        put(caravan);

                                    }
                                }

                            }
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());
                            calendar.add(Calendar.MINUTE,-20);
                            for (GameObject obj : remObjects) {
                                //Если город то не удаляем в течении 20 минут.
                                if (!(obj instanceof City &&  obj.getUpdated().after(calendar.getTime())) && !(obj instanceof Chest)) {
                                    obj.RemoveObject();
                                    removeActive(obj);
                                    objects.remove(obj.getGUID());
                                }
                            }
                        }

                    } catch (JSONException e) {
                       GATracker.trackException("ScanRange",e);
                    }
                } else
                if (TYPE==FASTSCAN){
                    try {
                        //Проверить наличие массива JSON. Objects

                        if (response.has("FastScan")) {
                            //Для каждого объекта в GameObjects
                            //Собрать список гуидов
                            ArrayList<String> guids=new ArrayList<>();

                            JSONArray lst=response.getJSONArray("FastScan");

                            //Если он есть в JSON обновить данные
                            final int lst_length = lst.length();// Moved  lst.length() call out of the loop to local variable lst_length
                            for (int i = 0; i< lst_length; i++){
                                JSONObject obj=lst.getJSONObject(i);
                                String guid="";
                                int lat=0;
                                int lng=0;
                                String type="";
                                if (obj.has("GUID"))guid=obj.getString("GUID");
                                if (obj.has("Lat"))lat=obj.getInt("Lat");
                                if (obj.has("Lng"))lng=obj.getInt("Lng");
                                if (obj.has("Type")) type=obj.getString("Type");
                                if (type.equalsIgnoreCase("Chest")){
                                    Chest chest=chests.get(guid);
                                    if (chest!=null){
                                        chest.setPostion(new LatLng(lat/1e6,lng/1e6));


                                    } else
                                    {
                                        chests.put(guid,new Chest(MyGoogleMap.getMap(),obj));
                                    }
                                } else
                                {
                                    GameObject loaded=objects.get(guid);
                                    if (loaded!=null){
                                        loaded.setPostion(new LatLng(lat/1e6,lng/1e6));
                                        loaded.setVisibility(true);

                                    } else
                                    {
                                        if (type.equalsIgnoreCase("Caravan")){
                                            loaded=player.getRoutes().get(guid);
                                            if (loaded!=null){
                                                loaded.setPostion(new LatLng(lat/1e6,lng/1e6));
                                                loaded.setVisibility(true);

                                            }
                                            else {
                                                serverConnect.getInstance().callScanRange();

                                            }
                                        }
                                    }

                                }
                                guids.add(guid);

                            }

                            Set<String> remove=chests.keySet();
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(new Date());
                            calendar.add(Calendar.SECOND,-15);
                            for (String guid:remove){

                                Chest chest=chests.get(guid);
                                if (chest.getForceRemove() || (chest.getUpdated().before(calendar.getTime()))){
                                    chest.RemoveObject();
                                    chests.remove(guid);
                                }

                            }
                            remove=objects.keySet();

                            for (String guid:remove){
                                GameObject rem=objects.get(guid);

                                if (rem instanceof Ambush || rem instanceof Caravan) {
                                    if (rem.getForceRemove() || (rem.getUpdated().before(calendar.getTime()))){
                                        rem.setVisibility(false);
                                        rem.setPostion(null);
                                    }
                                }

                            }

                        }

                    } catch (JSONException e) {
                        Log.d("FastScanMS", "Exception");
                        GATracker.trackException("FastScan",e);
                    }
                } else if (TYPE==PLAYER){
                    GameObjects.getPlayer().loadJSON(response);
                    if (GameObjects.getPlayer().getRace() < 1 || GameObjects.getPlayer().getRace() > 3) {
                        MessageMap.clearAll();
                        Essages.clear();
                        new ChooseFaction(context).show();
                    }
                    SharedPreferences sp = context.getSharedPreferences("player", Context.MODE_PRIVATE);
                    SharedPreferences.Editor ed = sp.edit();
                    try {
                        ed.putString("player", GameObjects.getPlayer().getJSON().toString());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ed.apply();
                }
            }

            @Override
            public void onError(int TYPE, JSONObject response) {

            }
        });

        GameSettings.addSettingsListener(new SettingsListener() {
            @Override
            public void onSettingsSave() {

            }

            @Override
            public void onSettingsLoad() {

            }

            @Override
            public void onSettingChange(String setting) {
                if (objects == null) return;
                switch (setting) {
                    case "SHOW_AMBUSH_RADIUS":
                        for (GameObject o : objects.values()) {
                            if (o instanceof Ambush) {
                                ((Ambush) o).showRadius();
                                o.changeMarkerSize();
                            }
                        }
                        break;
                    case "SHOW_CITY_RADIUS":
                        for (GameObject o : objects.values()) {
                            if (o instanceof City) {
                                ((City) o).showRadius();
                                o.changeMarkerSize();
                            }

                        }
                        break;
                    case "SHOW_BUILD_AREA":
                        for (GameObject o:objects.values()){
                            if (o instanceof  City) ((City) o).showBuildZone();
                        }
                    case "SHOW_CARAVAN_ROUTE":
                    case "NIGHT_MODE":
                        GameObjects.getPlayer().showRoute();
                        break;
                    case "USE_TILT":
                        getPlayer().changeMarkerSize();
                        for (GameObject o:getPlayer().getAmbushes().values()) o.changeMarkerSize();
                        for (GameObject o:getPlayer().getRoutes().values()) o.changeMarkerSize();
                        for (GameObject o:objects.values()) o.changeMarkerSize();
                        for (GameObject o:chests.values()) o.changeMarkerSize();

                }
            }
        });
        GPSInfo.getInstance().AddLocationListener(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
                GameObjects.getPlayer().setPosition(target);
                try {
                    for (Chest o:chests.values()) o.refresh();
                } catch (Exception e){
                    GATracker.trackException("ChestRefresh",e);
                }

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    public static HashMap put(GameObject object){
        if (objects.get(object.getGUID())!=null) return objects;
        if (!(object instanceof City) && object.isOwner()){
            player.putObject(object);
            return objects;
        }
        objects.put(object.getGUID(),object);
        if (object instanceof  ActiveObject) {
            //Добавить в перечень активных объектов.
            putActive(object);
        }
        return objects;
    }
    private static HashMap<String,GameObject> activeObjects;
    public static HashMap<String,GameObject> putActive(GameObject object){
        if (!(object instanceof ActiveObject)) return null;
        if (activeObjects.get(object.getGUID())!=null) return activeObjects;
        activeObjects.put(object.getGUID(),object);
        return activeObjects;
    }
    public static HashMap<String,GameObject> removeActive(GameObject object){
        if (!(object instanceof ActiveObject)) return null;
        if (object instanceof Chest){
            chests.remove(object.getGUID());
            return activeObjects;
        }
        activeObjects.remove(object.getGUID());
        return activeObjects;
    }
    public static HashMap<String, GameObject> getInstance(){
        return objects;
    }
    public static Player getPlayer(){
        return player;
    }

    public static void setMap(GoogleMap map) {
        if (player!=null) player.setMap(map);
        if (objects!=null) for (GameObject object:objects.values()){
            object.setMap(map);
        }
    }
}
