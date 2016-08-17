package utility;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import utility.settings.GameSettings;
import utility.settings.SettingsListener;

/**
 * @author Shadilan
 */

public class GPSInfo {
    private static GPSInfo instance;
    private boolean on=false;

    public static boolean checkEnabled(){

        if (instance.locationManager==null) return false;
        boolean result=false;
        for (String prov : instance.locationManager.getAllProviders()) {

            if (!"passive".equals(prov) && instance.locationManager.isProviderEnabled(prov)) {

                result=true ;
            }
        }
        return result;
    }

    public static GPSInfo getInstance(Context mContext) {
        if (instance == null) {

            instance = new GPSInfo(mContext);
        }

        return instance;
    }


    private int speed;
    public int getSpeed(){

        return speed;
    }
    public static GPSInfo getInstance() {
        return instance;
    }

    private int lat = -1;
    private int lng = -1;
    //private int request = 0;
    private LocationManager locationManager;
    private ArrayList<LocationListener> locationListeners;
    private ArrayList<LocationListener> locationListenersRem;

    public void AddLocationListener(LocationListener listener) {
        if (locationListeners == null) {
            locationListeners = new ArrayList<>();
        }
        locationListeners.add(listener);

    }

    public void RemoveLocationListener(LocationListener listener) {
        if (locationListenersRem == null) {
            locationListenersRem = new ArrayList<>();
        }
        locationListenersRem.add(listener);

    }

    Context context;
    private boolean hasAccuracy=false;
    /**
     * Constructor
     * @param mContext Application context
     */
    private GPSInfo(Context mContext) {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        context=mContext;
        //Criteria criteria = new Criteria();
        locationListener=new LocationListener() {
            float accur=1000;
            @Override
            public void onLocationChanged(Location location) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    if (location.isFromMockProvider()){
                        //Essages.addEssage("Вы используете фальшивые координаты. Информация отправлена администраторам.");
                        GATracker.trackHit("System","SpoofingWithMock");
                        return;

                    }
                }

                boolean doEvent=true;
                if (location.hasAccuracy() && location.getAccuracy()>accur+5) doEvent=false;
                if (location.hasAccuracy()) {
                    hasAccuracy=true;
                    accur=location.getAccuracy();

                }
                if (location.hasAccuracy()!=hasAccuracy) doEvent=false;



                if (doEvent) {
                    GATracker.trackHit("GPS","Accuracy",(int)accur);
                    if (location.hasSpeed()){
                        speed = (int) (location.getSpeed() * 60 *60 / 1000);
                        GATracker.trackHit("GPS","Speed",speed);
                    }

                    if (location.getLongitude() != -1 && location.getLatitude() != -1) {
                        lat = (int) (location.getLatitude() * 1000000);
                        lng = (int) (location.getLongitude() * 1000000);
                    }

                    //RequestUpdate(location.getProvider());
                    if (locationListeners != null) {
                        if (locationListenersRem != null)
                            locationListeners.removeAll(locationListenersRem);
                        for (LocationListener ll : locationListeners) {
                            ll.onLocationChanged(location);
                        }
                    }
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                if (locationListeners !=null){
                    if (locationListenersRem !=null) locationListeners.removeAll(locationListenersRem);
                    for (LocationListener ll:locationListeners){
                        ll.onStatusChanged(provider,status,extras);
                    }
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                if (locationListeners !=null){
                    if (locationListenersRem !=null) locationListeners.removeAll(locationListenersRem);
                    for (LocationListener ll:locationListeners){
                        ll.onProviderEnabled(provider);
                    }
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                GATracker.trackHit("GPSProvider",provider+"Off");
                if (locationListeners !=null){
                    if (locationListenersRem !=null) locationListeners.removeAll(locationListenersRem);
                    for (LocationListener ll:locationListeners){
                        ll.onProviderDisabled(provider);
                    }
                }
            }
        };
        onGPS();
        GameSettings.addSettingsListener(new SettingsListener() {
            @Override
            public void onSettingsSave() {

            }

            @Override
            public void onSettingsLoad() {

            }

            @Override
            public void onSettingChange(String setting) {
                if (setting.equals("GPS_RATE")) {
                    GPSInfo.getInstance().offGPS();
                    GPSInfo.getInstance().onGPS();
                }
            }
        });
    }
    public void onGPS(){

        for (String prov : locationManager.getAllProviders()) {
            RequestUpdate(prov);
        }
        on=true;
    }
    public void offGPS(){

        locationManager.removeUpdates(locationListener);
        on=false;
    }
    LocationListener locationListener;
    /**
     * Request coordinate uppdate on target provider
     * @param prov provider of GPS Data
     */

    public void RequestUpdate(String prov) {
        int refreshRate=3000;
        if (GameSettings.getInstance()==null) GameSettings.init(context);
        if (GameSettings.getInstance().get("GPS_REFRESH")!=null){
            refreshRate=Integer.getInteger(GameSettings.getInstance().get("GPS_REFRESH"))*1000;
        }
        try {
            locationManager.requestLocationUpdates(prov, refreshRate, 1, locationListener);
        } catch (SecurityException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * Get current Latitude
     * @return Latitude
     */
    public int GetLat(){
        return lat;
    }

    /**
     * Get current Longtitude
     * @return Longtitude
     */
    public int GetLng(){
        return lng;
    }
    public LatLng getLatLng(){return new LatLng(lat/1e6,lng/1e6);}
    public static float getDistance(LatLng p1,LatLng p2){
        float[] distances = new float[1];
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, distances);
        if (distances.length<1) return -1;
        else return distances[0];
    }

    public boolean isOn() {
        return on;
    }
}
