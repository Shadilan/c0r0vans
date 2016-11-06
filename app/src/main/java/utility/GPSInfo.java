package utility;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Date;

import utility.settings.GameSettings;
import utility.settings.SettingsListener;

/**
 * @author Shadilan
 */

public class GPSInfo {
    private static GPSInfo instance;
    private boolean on = false;
    private Long lastTime = 0L;
    private final int stepDuration=500;

    public static boolean checkEnabled() {

        if (instance.locationManager == null) return false;
        boolean result = false;
        for (String prov : instance.locationManager.getAllProviders()) {

            if (!"passive".equals(prov) && instance.locationManager.isProviderEnabled(prov)) {

                result = true;
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

    public int getSpeed() {

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

    private Location aim;
    private Location current;
    private long lastSync = 0;
    private double stepLat = 0;
    private double stepLng = 0;
    private Context context;
    private boolean hasAccuracy = false;

    /**
     * Constructor
     * @param mContext Application context
     */
    private GPSInfo(Context mContext) {
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        context = mContext;
        //Criteria criteria = new Criteria();
        locationListener = new LocationListener() {
            float accur = 1000;

            @Override
            public void onLocationChanged(Location location) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                    if (location.isFromMockProvider()) {
                        //Essages.addEssage("Вы используете фальшивые координаты. Информация отправлена администраторам.");
                        GATracker.trackHit("GPS", "SpoofingWithMock");
                        return;

                    }
                }

                Long curTime = new Date().getTime() / 1000;
                LatLng newCord = new LatLng(location.getLatitude(), location.getLongitude());
                LatLng oldCord = getLatLng();
                if (oldCord.latitude == -1 && oldCord.longitude == -1) oldCord = newCord;
                float timespeed;
                if (location.hasSpeed() && location.getSpeed() > 0) {
                    timespeed = location.getSpeed();
                } else {
                    timespeed = Math.max(speed * 5 / 18, 1);

                    GATracker.trackHit("GPS", "NoSpeed." + location.getProvider());
                }

                if ((timespeed) * (curTime - lastTime + 1) * 10 < getDistance(oldCord, newCord) && curTime - lastTime < 30000) {
                    GATracker.trackHit("GPS", "ToFast." + location.getProvider());
                    return;
                }

                if (curTime - lastTime < 30000 && (!location.hasAccuracy() || location.getAccuracy() > 100)) {
                    GATracker.trackHit("GPS", "NotAccurate." + location.getProvider());
                    return;
                }


                if (location.hasAccuracy()) {
                    hasAccuracy = true;
                    accur = location.getAccuracy();
                    GATracker.trackHit("GPS", "Accuracy", (int) accur);

                }

                if (location.hasSpeed()) {
                    speed = (int) (timespeed * 60 * 60 / 1000);
                    GATracker.trackHit("GPS", "Speed", speed);
                }

                if (location.getLongitude() != -1 && location.getLatitude() != -1) {
                    lat = (int) (location.getLatitude() * 1000000);
                    lng = (int) (location.getLongitude() * 1000000);
                    lastTime = curTime;

                }

                aim = location;
                long t = (new Date()).getTime();

                if (lastSync == 0 || current == null) {

                    current = aim;
                    stepLat=0;
                    stepLng=0;

                } else {

                    double step = (t - lastSync)/stepDuration;
                    if (step==0) step=1;

                    stepLat = (aim.getLatitude() - current.getLatitude()) / step;
                    stepLng = (aim.getLongitude() - current.getLongitude()) / step;

                }

                lastSync = t;


                RequestUpdate(location.getProvider());
                if (locationListeners != null) {
                    if (locationListenersRem != null)
                        locationListeners.removeAll(locationListenersRem);
                    for (LocationListener ll : locationListeners) {
                        ll.onLocationChanged(current);
                    }
                }

            }



            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                if (locationListeners != null) {
                    if (locationListenersRem != null)
                        locationListeners.removeAll(locationListenersRem);
                    for (LocationListener ll : locationListeners) {
                        ll.onStatusChanged(provider, status, extras);
                    }
                }
            }

            @Override
            public void onProviderEnabled(String provider) {
                if (locationListeners != null) {
                    if (locationListenersRem != null)
                        locationListeners.removeAll(locationListenersRem);
                    for (LocationListener ll : locationListeners) {
                        ll.onProviderEnabled(provider);
                    }
                }
            }

            @Override
            public void onProviderDisabled(String provider) {
                GATracker.trackHit("GPSProvider", provider + "Off");
                if (locationListeners != null) {
                    if (locationListenersRem != null)
                        locationListeners.removeAll(locationListenersRem);
                    for (LocationListener ll : locationListeners) {
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
        MainThread.postDelayed(refreshLock,stepDuration);
    }

    public void onGPS() {

        for (String prov : locationManager.getAllProviders()) {
            RequestUpdate(prov);
        }
        on = true;
    }
    private Runnable refreshLock=new Runnable() {
        @Override
        public void run() {
            boolean needMove=true;
            if (stepLat==0 && stepLng==0) needMove=false;
            if (needMove) {
                if (aim != null) {
                    if (current == null
                            || Math.abs(current.getLatitude() - aim.getLatitude()) < Math.abs(stepLat)
                            || Math.abs(current.getLongitude() - aim.getLongitude()) < Math.abs(stepLng)
                            ) {
                        current = aim;
                        stepLat = 0;
                        stepLng = 0;
                    } else {

                        double resLat = current.getLatitude() + stepLat;
                        double resLng = current.getLongitude() + stepLng;
                        current.setLatitude(resLat);
                        current.setLongitude(resLng);
                    }

                    if (locationListeners != null && current!=null) {
                        if (locationListenersRem != null)
                            locationListeners.removeAll(locationListenersRem);
                        for (LocationListener ll : locationListeners) {
                            ll.onLocationChanged(current);
                        }
                    }
                }
            }
            MainThread.postDelayed(refreshLock,stepDuration);
        }

    };
    public void offGPS() {

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            Toast.makeText(context,"Allow GPS Access",Toast.LENGTH_LONG).show();
            return;
        }
        locationManager.removeUpdates(locationListener);
        on=false;
    }
    private LocationListener locationListener;
    /**
     * Request coordinate uppdate on target provider
     * @param prov provider of GPS Data
     */

    private void RequestUpdate(String prov) {
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
        if (current==null) return 0;
        return (int) (current.getLatitude()*1e6);
    }

    /**
     * Get current Longtitude
     * @return Longtitude
     */
    public int GetLng(){
        if (current==null) return 0;
        return (int) (current.getLongitude()*1e6);
    }
    public LatLng getLatLng(){
        if (current==null) return new LatLng(0,0);
        return new LatLng(current.getLatitude(),current.getLongitude());}
    public static float getDistance(LatLng p1,LatLng p2){
        if (p1==null ||p2==null) return 0f;
        float[] distances = new float[1];
        Location.distanceBetween(p1.latitude, p1.longitude, p2.latitude, p2.longitude, distances);
        if (distances.length<1) return -1;
        else return distances[0];
    }

    public boolean isOn() {
        return on;
    }
}

