package coe.com.c0r0vans;

import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import coe.com.c0r0vans.GameObjects.Player;
import utility.GPSInfo;
import utility.GameSettings;

/**
 * @author Shadilan
 */
public class MyGoogleMap{
    private static GoogleMap map;
    private static int windowHeight=800;
    private static int clientZoom = 17;
    public static void init(GoogleMap mMap,int Height){
        map=mMap;
        windowHeight=Height;
        setupMap();
    }

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

        if ("Y".equals(GameSettings.getInstance().get("VIEW_PADDING"))){
            Point point=new Point();

            map.setPadding(0,windowHeight/2,0,40);
        } else map.setPadding(0,0,0,40);

        GPSInfo.getInstance().AddLocationListener(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
                if ("Y".equals(GameSettings.getInstance().get("USE_TILT")))
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(target)
                                    .tilt(60)
                                    .bearing(map.getCameraPosition().bearing)
                                    .zoom(clientZoom)
                                    .build()));
                else
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(target)
                                    .bearing(map.getCameraPosition().bearing)
                                    .tilt(0)
                                    .zoom(clientZoom)
                                    .build()));

                Player.getPlayer().getMarker().setPosition(target);
                Player.getPlayer().getCircle().setCenter(target);
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
            float bearing = map.getCameraPosition().bearing;

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (cameraPosition.bearing != bearing) {
                    map.moveCamera(CameraUpdateFactory.newLatLng(GPSInfo.getInstance().getLatLng()));
                    bearing = cameraPosition.bearing;
                }
                float[] distances = new float[1];
            }

        });
    }
    public static GoogleMap getMap(){
        return map;
    }

    public static void switchZoom() {
        switch (clientZoom) {
            case 16:
                clientZoom = 17;
                break;
            case 17:
                clientZoom = 18;
                break;
            case 18:
                clientZoom = 16;
                break;
            default:
                clientZoom = 16;
        }
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(GPSInfo.getInstance().GetLat() / 1e6, GPSInfo.getInstance().GetLng() / 1e6), clientZoom));
    }

    public static int getClientZoom() {
        return clientZoom;
    }
    public static void changeSettings(){
        if ("Y".equals(GameSettings.getInstance().get("USE_TILT")))
            map.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(map.getCameraPosition().target)
                            .tilt(60)
                            .zoom(clientZoom)
                            .build()));
        else
            map.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(map.getCameraPosition().target)
                            .tilt(0)
                            .zoom(clientZoom)
                            .build()));
        if ("Y".equals(GameSettings.getInstance().get("VIEW_PADDING"))){
            map.setPadding(0,windowHeight/2,0,40);
        } else map.setPadding(0, 0, 0, 40);
    }
}
