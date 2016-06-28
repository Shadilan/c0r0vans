package com.coe.c0r0vans.GameObjects;

import android.util.Log;

import com.coe.c0r0vans.MyGoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import utility.internet.ServerListener;
import utility.internet.serverConnect;
import utility.notification.Essages;
import utility.settings.GameSettings;
import utility.settings.SettingsListener;


/**
 * Перечень игровых объектов на карте
 */
public class GameObjects extends HashMap<String,GameObject> {
    private static GameObjects instance = new GameObjects();
    public static void init(){
        serverConnect.getInstance().addListener(new ServerListener() {
            @Override
            public void onResponse(int TYPE, JSONObject response) {
                if (TYPE==REFRESH){
                    try {
                        //Проверить наличие массива JSON. Objects
                        if (response.has("Objects")) {
                            //Скопировать данные в массив для удаления
                            ArrayList<GameObject> remObjects = new ArrayList<>(instance.values());
                            JSONArray JObj = response.getJSONArray("Objects");
                            int leng = JObj.length();

                            for (int i = 0; i < leng; i++) {
                                GameObject robj = null;
                                for (GameObject obj : remObjects) {
                                    if (obj.getGUID().equals(JObj.getJSONObject(i).getString("GUID"))) {
                                        robj = obj;
                                        break;
                                    }
                                }
                                if (robj != null) {

                                    remObjects.remove(robj);
                                    robj.loadJSON(JObj.getJSONObject(i));
                                } else {
                                    if (JObj.getJSONObject(i).getString("Type").equalsIgnoreCase("Player")) {

                                        Player.getPlayer().loadJSON(JObj.getJSONObject(i));

                                    } else if (JObj.getJSONObject(i).getString("Type").equalsIgnoreCase("City")) {

                                        City city = new City(MyGoogleMap.getMap(), JObj.getJSONObject(i));
                                        instance.put(city.getGUID(), city);
                                    } else if (JObj.getJSONObject(i).getString("Type").equalsIgnoreCase("Ambush")) {

                                        Ambush ambush = new Ambush(MyGoogleMap.getMap(), JObj.getJSONObject(i));

                                        instance.put(ambush.getGUID(), ambush);
                                    } else if (JObj.getJSONObject(i).getString("Type").equalsIgnoreCase("Caravan")) {

                                        Caravan caravan = new Caravan(MyGoogleMap.getMap(), JObj.getJSONObject(i));
                                        instance.put(caravan.getGUID(), caravan);
                                    } else if (JObj.getJSONObject(i).getString("Type").equalsIgnoreCase("Sign")) {
                                        Log.d("Debug info", "Sign Load:" + JObj.getJSONObject(i).toString());
                                        Log.d("Game Warning", "Sign object");
                                    } else {
                                        Log.d("Game Warning", "Unknown object");
                                    }
                                }

                            }
                            for (GameObject obj : remObjects) {
                                obj.RemoveObject();
                            }
                            for (GameObject o : remObjects) {
                                instance.remove(o.getGUID());
                            }

                        }

                    } catch (JSONException e) {
                        Essages.addEssage(e.toString());
                    }
                } else
                if (TYPE==FASTSCAN){
                    try {
                        //Проверить наличие массива JSON. Objects
                        Log.d("FastScanTest","ListenerOK");
                        if (response.has("FastScan")) {
                            //Для каждого объекта в GameObjects
                            Log.d("FastScanTest","JSONOk");
                            JSONArray lst=response.getJSONArray("FastScan");
                            for (GameObject o:instance.values()) {
                               //Если это Засада или Караван
                                if (o instanceof Ambush || o instanceof Caravan) {

                                    //Если он есть в JSON обновить данные
                                    final int lst_length = lst.length();// Moved  lst.length() call out of the loop to local variable lst_length
                                    boolean isChanged=false;
                                    for (int i = 0; i< lst_length; i++){
                                        JSONObject obj=lst.getJSONObject(i);
                                        String guid="";
                                        int lat=0;
                                        int lng=0;
                                        //String type="";
                                        if (obj.has("GUID"))guid=obj.getString("GUID");
                                        if (obj.has("Lat"))lat=obj.getInt("Lat");
                                        if (obj.has("Lng"))lng=obj.getInt("Lng");
                                        //if (obj.has("Type")) type=obj.getString("Type");
                                        if (o.getGUID().equals(guid)){
                                            Log.d("FastScanTest","ObjectFound");
                                            o.setPostion(new LatLng(lat/1e6,lng/1e6));
                                            o.setVisibility(true);
                                            //isChanged=true;
                                        }

                                    }
                                    //Пока не очищать типа запомнил ?
                                    /*if (!isChanged && ((o instanceof Ambush && ((Ambush)o).getFaction()!=0)||(o instanceof Caravan && ((Caravan)o).getFaction()!=0))) {
                                        //TODO Если нет очистить данные.
                                        o.setVisibility(false);
                                    }*/
                                }
                            }

                        }

                    } catch (JSONException e) {
                        Essages.addEssage(e.toString());
                    }
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
                if (instance == null) return;
                switch (setting) {
                    case "SHOW_AMBUSH_RADIUS":
                        for (GameObject o : instance.values()) {
                            if (o instanceof Ambush) {
                                ((Ambush) o).showRadius();
                            }
                            o.changeMarkerSize();
                        }
                        break;
                    case "SHOW_CITY_RADIUS":
                        for (GameObject o : instance.values()) {
                            if (o instanceof City) {
                                ((City) o).showRadius();
                            }
                            o.changeMarkerSize();
                        }
                        break;
                    case "SHOW_BUILD_AREA":
                        for (GameObject o:instance.values()){
                            if (o instanceof  City) ((City) o).showBuildZone();
                        }
                    case "SHOW_CARAVAN_ROUTE":
                        Player.getPlayer().showRoute();
                        break;
                }
            }
        });
    }

    public HashMap put(GameObject object){
        if (this.get(object.getGUID())!=null) return this;
        put(object.getGUID(),object);
        return this;
    }
    public void updateView(){
        for (GameObject o:this.values()){
            if (o.getMarker()!=null){
                o.changeMarkerSize();
            }
        }
    }
    public static GameObjects getInstance(){
        return instance;
    }
}
