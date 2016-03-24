package coe.com.c0r0vans;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import coe.com.c0r0vans.GameObjects.GameObject;
import coe.com.c0r0vans.GameObjects.Player;
import utility.GPSInfo;
import utility.GameSettings;
import utility.serverConnect;

/**
 * @author Shadilan
 */
public class MyGoogleMap{
    private static GoogleMap map;
    private static int windowHeight=800;
    private static float clientZoom = GameObject.ICON_MEDIUM;
    private static boolean moveFixed=true;
    private static Marker targetMarker;
    private static ImageButton showpointButton;
    static float bearing = 0;

    /**
     * Инициализация карты
     * @param mMap переменная карты
     * @param Height размер по вертикали экнара карты
     */
    public static void init(GoogleMap mMap,int Height){
        map=mMap;
        windowHeight=Height;
        setupMap();

    }

    /**
     * Инициализация кнопки фиксации местоположения
     * @param button
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
            float oldBearing=0f;
            @Override
            public void onLocationChanged(Location location) {
                float curBearing=map.getCameraPosition().bearing;
                boolean trackBearing="Y".equals(GameSettings.getInstance().get("TRACK_BEARING"));
                Log.d("tttt","hasBearing"+location.hasBearing());
                Log.d("tttt","hasAccuracy"+location.hasAccuracy());
                Log.d("tttt","getAccuracy"+location.getAccuracy());

                if (trackBearing && location.hasBearing() && location.hasAccuracy() && location.getAccuracy()<20 && location.getSpeed()*60*60/1000>5)
                {
                    if (Math.round(oldBearing/90)==Math.round(location.getBearing()/90)) {
                        bearing=location.getBearing();
                        curBearing=bearing;
                        Log.d("tttt","Bearing"+curBearing);
                    }
                    //ChangeBearing;
                    Log.d("tttt","oldBearing"+oldBearing);
                    oldBearing=location.getBearing();
                }
                LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
                if (moveFixed) {
                    moveCamera(target,curBearing);
                }
                Player.getPlayer().setPosition(target);
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


        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {


            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (cameraPosition.bearing != bearing) {
                    bearing = cameraPosition.bearing;
                    Log.d("Test rotate","Rotation");
                    GameSettings.setBearing(bearing);
                    if (moveFixed) moveCamera(GPSInfo.getInstance().getLatLng());
                    else moveCamera(targetPoint);

                }
            }

        });

        MarkerOptions mo=new MarkerOptions().anchor(0.5f,0.5f).icon(BitmapDescriptorFactory.fromResource(R.mipmap.closebutton)
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
     * @param target
     */
    private static void moveCamera(LatLng target){
        moveCamera(target, map.getCameraPosition().bearing);
    }

    /**
     * Изменить местоположение камеры
     * @param target
     * @param cbearing Угол поворота
     */
    private static void moveCamera(LatLng target,float cbearing){

        if (target==null) target=GPSInfo.getInstance().getLatLng();
        if ("Y".equals(GameSettings.getInstance().get("USE_TILT"))) {

            map.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(target)
                            .bearing(cbearing)
                            .tilt(60)
                            .zoom(clientZoom)
                            .build()));
        }
        else
            map.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(target)
                            .bearing(cbearing)
                            .tilt(0)
                            .zoom(clientZoom)
                            .build()));
        if ("Y".equals(GameSettings.getInstance().get("VIEW_PADDING"))){
            map.setPadding(0,windowHeight/2,0,40);
        } else map.setPadding(0, 0, 0, 40);
    }
    private static LatLng targetPoint;

    /**
     * ОФиксировать точку в координатах
     * @param point
     */
    public static void showPoint(LatLng point){
        moveFixed=false;
        targetPoint=point;
        if (targetMarker!=null){
            targetMarker.setPosition(point);
            targetMarker.setVisible(true);
        }

        if (showpointButton!=null) showpointButton.setVisibility(View.VISIBLE);
        serverConnect.getInstance().RefreshData((int)(point.latitude*1e6),(int) (point.longitude*1e6));
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
        serverConnect.getInstance().RefreshCurrent();

    }

    public static boolean isMoveFixed() {
        return moveFixed;
    }
}
