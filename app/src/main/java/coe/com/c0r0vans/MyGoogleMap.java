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
    private static int clientZoom = 17;
    private static boolean moveFixed=true;
    private static Marker targetMarker;
    private static ImageButton showpointButton;
    static float bearing = 0;
    public static void init(GoogleMap mMap,int Height){
        map=mMap;
        windowHeight=Height;
        setupMap();

    }
    public static void setShowpointButton(ImageButton button){
        showpointButton=button;
        showpointButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopShowPoint();
            }
        });
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

        //bearing=map.getCameraPosition().bearing;

        bearing=GameSettings.getBearing();
        clientZoom=GameSettings.getZoom();
        changeSettings();


        GPSInfo.getInstance().AddLocationListener(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
                if (moveFixed) {
                    moveCamera(target);
                }
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
        GameSettings.setZoom(clientZoom);
        if (moveFixed) moveCamera(GPSInfo.getInstance().getLatLng());
        else moveCamera(targetPoint);

    }

    public static int getClientZoom() {
        return clientZoom;
    }
    public static void changeSettings(){
        stopShowPoint();
        moveCamera(map.getCameraPosition().target);
        if ("Y".equals(GameSettings.getInstance().get("VIEW_PADDING"))){
            map.setPadding(0,windowHeight/2,0,40);
        } else map.setPadding(0, 0, 0, 40);

    }
    private static void moveCamera(LatLng target){
        if (target==null) target=GPSInfo.getInstance().getLatLng();
        if ("Y".equals(GameSettings.getInstance().get("USE_TILT")))

            map.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(target)
                            .bearing(map.getCameraPosition().bearing)
                            .tilt(60)
                            .zoom(clientZoom)
                            .build()));
        else
            map.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(target)
                            .bearing(map.getCameraPosition().bearing)
                            .tilt(0)
                            .zoom(clientZoom)
                            .build()));
    }
    private static LatLng targetPoint;
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
