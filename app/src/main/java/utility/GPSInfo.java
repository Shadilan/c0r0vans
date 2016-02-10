package utility;


import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

/**
 * @author Shadilan
 */

public class GPSInfo {
    private static GPSInfo instance;

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


    /**
     * Constructor
     * @param mContext Application context
     */
    private GPSInfo(Context mContext) {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        //Criteria criteria = new Criteria();
        locationListener=new LocationListener() {
            @Override

            public void onLocationChanged(Location location) {
                speed = (int) (location.getSpeed()*60/1000);
                lat = (int) (location.getLatitude() * 1000000);
                lng = (int) (location.getLongitude() * 1000000);

                //RequestUpdate(location.getProvider());
                if (locationListeners !=null){
                    if (locationListenersRem !=null) locationListeners.removeAll(locationListenersRem);
                    for (LocationListener ll:locationListeners){
                        ll.onLocationChanged(location);
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
                if (locationListeners !=null){
                    if (locationListenersRem !=null) locationListeners.removeAll(locationListenersRem);
                    for (LocationListener ll:locationListeners){
                        ll.onProviderDisabled(provider);
                    }
                }
            }
        };
        onGPS();
    }
    public void onGPS(){
        for (String prov : locationManager.getAllProviders()) {
            RequestUpdate(prov);
        }
    }
    public void offGPS(){
        locationManager.removeUpdates(locationListener);
    }
    /**
     * Request coordinate uppdate on target provider
     * @param prov provider of GPS Data
     */
    LocationListener locationListener;
    public void RequestUpdate(String prov) {

        try {

            locationManager.requestLocationUpdates(prov, 5000, 1, locationListener);
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
}
