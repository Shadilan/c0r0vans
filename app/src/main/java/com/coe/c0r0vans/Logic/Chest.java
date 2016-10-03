package com.coe.c0r0vans.Logic;

import android.graphics.Bitmap;
import android.util.Log;

import com.coe.c0r0vans.GameObject.ActiveObject;
import com.coe.c0r0vans.GameObject.GameObject;
import com.coe.c0r0vans.GameObject.OnGameObjectRemove;
import com.coe.c0r0vans.GameObjects.ObjectAction;
import com.coe.c0r0vans.Singles.GameObjects;
import com.coe.c0r0vans.Singles.MyGoogleMap;
import com.coe.c0r0vans.Singles.ToastSend;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import utility.GATracker;
import utility.GPSInfo;
import utility.GameSound;
import utility.ImageLoader;
import utility.internet.serverConnect;
import utility.notification.Essages;
import utility.settings.GameSettings;

/**
 * Класс обеспечивающий работу с сундуками
 */
public class Chest extends GameObject implements ActiveObject {
    private String currentMarkName;
    private LatLng latlng;
    public boolean isVisible(){
        return visible;
    }
    @Override
    public void loadJSON(JSONObject obj) {

        try {
            update();
            GUID=obj.getString("GUID");
            if (obj.has("Lat") && obj.has("Lng")) {
                int lat = obj.getInt("Lat");
                int lng = obj.getInt("Lng");
                latlng = new LatLng(lat / 1e6, lng / 1e6);
                if (mark == null) {
                    if (map!=null) setMarker(map.addMarker(new MarkerOptions().position(latlng)));
                } else {
                    mark.setPosition(latlng);
                }

            }
            refresh();
        } catch (JSONException e) {
            GATracker.trackException("LoadChest", e);
        }

    }
    public void setPostion(LatLng latLng) {
        update();
        this.latlng=latLng;
        if (mark!=null){

            mark.setPosition(latLng);
        } else setMarker(map.addMarker(new MarkerOptions().position(latLng)));
    }
    public Chest(GoogleMap map, JSONObject jsonObject){
        super();
        loadJSON(jsonObject);
        setMap(map);
    }

    @Override
    public void setMap(GoogleMap map) {
        super.setMap(map);
        if (mark!=null) mark.remove();
        setMarker(map.addMarker(new MarkerOptions().position(getPosition())));
    }

    @Override
    public void setMarker(Marker m) {
        super.setMarker(m);
        changeMarkerSize();
    }
    boolean inZone=false;
    public void refresh(){

        boolean oldInZone=inZone;
        float dist=GPSInfo.getDistance(GameObjects.getPlayer().getPosition(),getPosition());
        inZone = dist < GameObjects.getPlayer().getActionDistance();
        if (inZone!=oldInZone){
            changeMarkerSize();
        }
    }
    @Override
    public void changeMarkerSize() {
        if (mark!=null) {
            String markname;
            if (inZone)   markname="openchest";
            else markname = "chest";
            markname = markname + GameObject.zoomToPostfix(MyGoogleMap.getClientZoom());
            if (!markname.equals(currentMarkName)) {
                mark.setIcon(ImageLoader.getDescritor(markname));
                if ("Y".equals(GameSettings.getInstance().get("USE_TILT")))
                    mark.setAnchor(0.5f, 1f);
                else mark.setAnchor(0.5f, 1f);
                currentMarkName=markname;
            }
            if (MyGoogleMap.getClientZoom()==ICON_SMALL){
                mark.setVisible(false);
            } else {
                mark.setVisible(visible);
            }
        }
    }
    private boolean visible=true;
    public void setVisibility(boolean visibility) {
        if (mark!=null) {
            visible=visibility;
            changeMarkerSize();
        }
    }

    @Override
    public LatLng getPosition() {
        return latlng;
    }
    @Override
    public void RemoveObject() {
        if (removeListeners!=null){
            for (OnGameObjectRemove onGameObjectRemove:removeListeners){
                onGameObjectRemove.onRemove();
            }
        }
        latlng=null;
        if (mark!=null){
            mark.remove();
            Log.d("Chests","MarkRemove");
        }
        GameObjects.removeActive(this);

    }

    @Override
    public int getRadius() {
        return 30;
    }

    @Override
    public void useObject() {

        int dist= (int) GPSInfo.getDistance(GameObjects.getPlayer().getPosition(),getPosition());
        if (this.getMarker()!=null
                && dist <GameObjects.getPlayer().getActionDistance()) {
            serverConnect.getInstance().callOpenChest(getChestAction(), GPSInfo.getInstance().GetLat(),
                    GPSInfo.getInstance().GetLng(),getGUID());
        }
    }

    private ObjectAction chestAction;
    private ObjectAction getChestAction(){
        if (chestAction==null){

                chestAction=new ObjectAction(this){
                    @Override
                    public Bitmap getImage() {
                        return ImageLoader.getImage("open_chest");
                    }
                    @Override
                    public String getCommand() {
                        return "OpenChest";
                    }

                    @Override
                    public void preAction() {
                        setVisibility(false);
                    }

                    @Override
                    public void postAction(JSONObject response) {
                        if (response.has("Gold")) {
                            try {
                                int gold=response.getInt("Gold");
                                GameObjects.getPlayer().setGold(GameObjects.getPlayer().getGold()+gold);
                                ToastSend.send("Получено "+gold+" золота.");
                                Essages.addEssage("В сундуке обнаружено "+gold+" золота.");
                                forceRemove();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                        GameSound.playSound(GameSound.OPENCHEST);

                        RemoveObject();
                    }

                    @Override
                    public void postError(JSONObject response) {
                        setVisibility(true);
                        try {
                            String err;
                            if (response.has("Error")) err = response.getString("Error");
                            else if (response.has("Result")) err = response.getString("Result");
                            else err = "U0000";
                            switch (err) {
                                case "DB001":
                                    Essages.addEssage("Ошибка сервера.");
                                    break;
                                case "L0001":
                                    Essages.addEssage("Соединение потеряно.");
                                    break;
                                case "O1401":
                                    Essages.addEssage("Сундук пуст.");
                                    forceRemove();
                                    setVisibility(false);
                                    ToastSend.send("Пусто.");
                                    break;
                                case "O1402":
                                    Essages.addEssage("Сундук слишком далеко.");
                                    break;
                                default:
                                    if (response.has("Message"))
                                        Essages.addEssage(response.getString("Message"));
                                    else Essages.addEssage("Непредвиденная ошибка.");

                            }
                        }catch (JSONException e)
                        {
                            GATracker.trackException("OpenChest",e);
                        }
                    }
                };


        }
        return chestAction;
    }

}
