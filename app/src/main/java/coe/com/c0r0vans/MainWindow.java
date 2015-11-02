package coe.com.c0r0vans;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.android.volley.Response;
import com.google.android.gms.games.Game;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import coe.com.c0r0vans.GameObjects.Ambush;
import coe.com.c0r0vans.GameObjects.City;
import coe.com.c0r0vans.GameObjects.GameObject;
import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.GameObjects.SelectedObject;
import utility.GPSInfo;
import utility.ImageLoader;
import utility.serverConnect;

public class MainWindow extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Player player;
    private ArrayList<City> Cities;
    private ArrayList<Ambush> Ambushes;
    private Timer RefreshTimer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        ImageLoader.Loader(this.getApplicationContext());
        mapFragment.getMapAsync(this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupMap();
        init();
    }
    private void setupMap(){
        LatLng target = new LatLng(GPSInfo.getInstance().GetLat()/1E6, GPSInfo.getInstance().GetLng()/1E6);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 18));
        mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);

    }
    private void init(){
        //init fields
        Cities=new ArrayList<>();
        Ambushes=new ArrayList<>();
        player=new Player(mMap);

        createListeners();
        serverConnect.getInstance().RefreshData(GPSInfo.getInstance().GetLat(), GPSInfo.getInstance().GetLng());
        RefreshTimer=new Timer("RefreshData");
        RefreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                serverConnect.getInstance().RefreshData(GPSInfo.getInstance().GetLat(),GPSInfo.getInstance().GetLng());
            }
        },1000*60);




    }
    private void createListeners()
    {
        GPSInfo.getInstance().AddLocationListener(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, 18));
                Log.d("MapViewTest", "Coord:" + location.getLatitude() + "x" + location.getLongitude());
                player.getMarker().setPosition(target);
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
        serverConnect.getInstance().AddGetDataListener(new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    player.loadJSON(response.getJSONObject("Player"));
                    //Cities
                    ArrayList<City> remCity = new ArrayList<>(Cities);
                    JSONArray arr;
                    if (response.has("Cities")) {
                        arr = response.getJSONArray("Cities");
                        for (int i = 0; i < arr.length(); i++) {
                            String GUID = arr.getJSONObject(i).getString("GUID");
                            City notchange = null;
                            for (City obj : remCity) {
                                if (GUID.equals(obj.getGUID())) {
                                    notchange = obj;
                                    break;
                                }
                            }
                            if (notchange == null) {
                                Cities.add(new City(mMap, arr.getJSONObject(i)));
                            } else remCity.remove(notchange);
                        }
                    }
                    for (City obj : remCity) {
                        obj.RemoveObject();
                    }
                    Cities.removeAll(remCity);
                    //Ambushes
                    ArrayList<Ambush> remAmbushes = new ArrayList<>(Ambushes);
                    if (response.has("Ambushes")) {
                        arr = response.getJSONArray("Ambushes");

                        for (int i = 0; i < arr.length(); i++) {
                            String GUID = arr.getJSONObject(i).getString("GUID");
                            Ambush notchange = null;
                            for (Ambush obj : remAmbushes) {
                                if (GUID.equals(obj.getGUID())) {
                                    notchange = obj;
                                    break;
                                }
                            }
                            if (notchange == null) {
                                Ambushes.add(new Ambush(mMap, arr.getJSONObject(i)));
                            } else remAmbushes.remove(notchange);
                        }
                    }
                    for (Ambush obj : remAmbushes) {
                        obj.RemoveObject();
                    }
                    Ambushes.removeAll(remAmbushes);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                GameObject target = findObjectByMarker(marker);
                Intent myIntent = new Intent(getApplicationContext(), ActionsActivity.class);
                SelectedObject.getInstance().setExecuter(player);
                SelectedObject.getInstance().setTarget(target);
                startActivity(myIntent);
                return false;
            }
        });

    }
    private GameObject findObjectByMarker(Marker m){
        if (player.getMarker().equals(m)){
            return player;
        }
        for (City obj:Cities){
            if (obj.getMarker().equals(m)){
                return obj;
            }
        }
        return  null;
    }
}
