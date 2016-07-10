package com.coe.c0r0vans.Singles;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.coe.c0r0vans.GameObject.GameObject;
import com.coe.c0r0vans.Logic.Player;
import com.coe.c0r0vans.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Date;

import utility.GPSInfo;
import utility.internet.serverConnect;
import utility.settings.GameSettings;
import utility.settings.SettingsListener;

/**
 * @author Shadilan
 */
public class MyGoogleMap{
    private static GoogleMap map;
    private static float clientZoom = GameSettings.getZoom();
    private static boolean moveFixed=true;
    private static Marker targetMarker;
    private static ImageButton showpointButton;
    static float bearing = 0;
    private static long lastRotateTime=0;

    /**
     * Инициализация карты
     * @param mMap переменная карты
     * @param Height размер по вертикали экнара карты
     */
    public static void init(GoogleMap mMap,int Height){
        map=mMap;
        setupMap();
        GameSettings.addSettingsListener(new SettingsListener() {
            @Override
            public void onSettingsSave() {

            }

            @Override
            public void onSettingsLoad() {

            }

            @Override
            public void onSettingChange(String setting) {
                if (setting.equals("USE_TILT") ||
                        setting.equals("TRACK_BEARING")
                        ) MyGoogleMap.changeSettings();
            }
        });
    }

    /**
     * Инициализация кнопки фиксации местоположения
     * @param button Кнопка для возврата.
     */
    public static void setShowpointButton(ImageButton button){
        showpointButton=button;
        showpointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopShowPoint();
            }
        });
    }

    /**
     * Настройка карты
     */
    private static  void setupMap(){
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setBuildingsEnabled(false);
        map.getUiSettings().setScrollGesturesEnabled(false);
        map.getUiSettings().setTiltGesturesEnabled(false);
        map.getUiSettings().setMyLocationButtonEnabled(false);
        map.getUiSettings().setZoomControlsEnabled(false);
        map.getUiSettings().setZoomGesturesEnabled(false);
        map.getUiSettings().setRotateGesturesEnabled(true);
        map.getUiSettings().setCompassEnabled(false);
        map.getUiSettings().setMapToolbarEnabled(false);
        map.getUiSettings().setIndoorLevelPickerEnabled(false);

        //bearing=map.getCameraPosition().bearing;

        bearing=GameSettings.getBearing();
        clientZoom=GameSettings.getZoom();
        changeSettings();


        GPSInfo.getInstance().AddLocationListener(new LocationListener() {
            float oldBearing = 0f;

            @Override
            public void onLocationChanged(Location location) {
                float curBearing = map.getCameraPosition().bearing;
                boolean trackBearing = "Y".equals(GameSettings.getInstance().get("TRACK_BEARING"));

                if (trackBearing && location.hasBearing() && location.hasAccuracy() && location.getAccuracy() < 20 && location.getSpeed() * 60 * 60 / 1000 > 5 && new Date().getTime()-lastRotateTime>30000) {
                    if (Math.round(oldBearing / 90) == Math.round(location.getBearing() / 90)) {
                        bearing = location.getBearing();
                        curBearing = bearing;
                    }
                    //ChangeBearing;
                    oldBearing = location.getBearing();
                }
                LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
                if (moveFixed) {
                    moveCamera(target, curBearing);
                }
                Player.getPlayer().setPosition(target);
                serverConnect.getInstance().checkRefresh();
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

        MarkerOptions mo=new MarkerOptions().anchor(0.5f,0.5f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.showbutton)
        ).position(new LatLng(0,0)).visible(false);
        targetMarker=map.addMarker(mo);

    }

    /**
     * Получить объект карты
     * @return объект карты
     */
    public static GoogleMap getMap(){
        return map;
    }

    /**
     * Изменить зум карты
     */
    public static void switchZoom() {
        if (clientZoom==GameObject.ICON_SMALL)
                clientZoom = GameObject.ICON_MEDIUM;
        else if (clientZoom==GameObject.ICON_MEDIUM)
            clientZoom = GameObject.ICON_LARGE;
        else if (clientZoom==GameObject.ICON_LARGE)
            clientZoom = GameObject.ICON_SMALL;
        else clientZoom = GameObject.ICON_SMALL;
        GameSettings.setZoom(clientZoom);
        if (moveFixed) moveCamera(GPSInfo.getInstance().getLatLng());
        else moveCamera(targetPoint);

    }

    /**
     *
     * @return Текущий зум
     */
    public static float getClientZoom() {
        return clientZoom;
    }

    /**
     * Перечитать настройки
     */
    public static void changeSettings(){
        stopShowPoint();
        moveCamera(map.getCameraPosition().target);

    }

    /**
     * Изменить местоположение камеры
     * @param target точка цели
     */
    private static void moveCamera(LatLng target){
        moveCamera(target, map.getCameraPosition().bearing);
    }
    private static LatLng oldLatLng=null;
    /**
     * Изменить местоположение камеры
     * @param target точка цели
     * @param cbearing Угол поворота
     */

    private static void moveCamera(LatLng target,float cbearing){

        bearing = cbearing;
        GameSettings.setBearing(bearing);

        if (target==null) target=GPSInfo.getInstance().getLatLng();
        if ("Y".equals(GameSettings.getInstance().get("USE_TILT"))) {

            map.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .bearing(cbearing)
                            .tilt(60)
                            .zoom(clientZoom)
                            .target(target)
                            .build()));
        }
        else
            map.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .bearing(cbearing)
                            .tilt(0)
                            .zoom(clientZoom)
                            .target(target)
                            .build()));

        if (oldLatLng==null || GPSInfo.getDistance(oldLatLng,target)>300){
            serverConnect.getInstance().callScanRange();
        }
    }
    private static LatLng targetPoint;

    /**
     * ОФиксировать точку в координатах
     * @param point точка цели
     */
    public static void showPoint(LatLng point){
        moveFixed=false;
        targetPoint=point;
        if (targetMarker!=null){
            targetMarker.setPosition(point);
            targetMarker.setVisible(true);
        }

        if (showpointButton!=null) showpointButton.setVisibility(View.VISIBLE);
        moveCamera(point);
    }

    /**
     * Отменить фиксациюточки в координатах
     */
    public static void stopShowPoint() {
        moveFixed=true;
        moveCamera(GPSInfo.getInstance().getLatLng());
        if (targetMarker!=null) targetMarker.setVisible(false);
        if (showpointButton!=null) showpointButton.setVisibility(View.INVISIBLE);
        serverConnect.getInstance().callScanRange();

    }

    public static boolean isMoveFixed() {
        return moveFixed;
    }

    public static void rotate(float angle) {
        /*if (!moveFixed) moveCamera(targetMarker.getPosition(),map.getCameraPosition().bearing+angle);
        else moveCamera(Player.getPlayer().getMarker().getPosition(), map.getCameraPosition().bearing+angle);*/
        moveCamera(map.getCameraPosition().target, map.getCameraPosition().bearing+angle);
        lastRotateTime=new Date().getTime();

    }

    public static void setOldLatLng(int lat, int lng) {
        oldLatLng=new LatLng((float)lat/1e6,(float)lng/1e6);
    }
}