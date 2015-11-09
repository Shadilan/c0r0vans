package coe.com.c0r0vans;

import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Response;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import coe.com.c0r0vans.GameObjects.Ambush;
import coe.com.c0r0vans.GameObjects.City;
import coe.com.c0r0vans.GameObjects.GameObject;
import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.GameObjects.SelectedObject;
import utility.Essages;
import utility.GPSInfo;
import utility.ImageLoader;
import utility.ResourceString;
import utility.serverConnect;

public class MainWindow extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Player player;
    private ArrayList<City> Cities;
    private ArrayList<Ambush> Ambushes;
    private TextView essegeText;
    private int SetupDone=0;
    private Handler myHandler = new Handler();
    private Timer refreshTimer;
    @Override
    /**
     * Create form;
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_window);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        ImageLoader.Loader(this.getApplicationContext());
        ImageButton routeButton= (ImageButton) findViewById(R.id.route_button);
        ResourceString.getInstance(getApplicationContext());
        Log.d("PackageInfo", getApplicationContext().getPackageName());
        Log.d("PackageInfo", getPackageName());
        routeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RouteList.class);
                startActivity(i);
            }
        });
        mapFragment.getMapAsync(this);
        essegeText=(TextView) findViewById(R.id.essageText);
        init();
        Log.d("LoginActivity","Start Login.");
        if (!serverConnect.getInstance().isLogin()) {
            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivity(i);
        }

    }
    /**
     * Make initialization.
     */
    private void init(){
        //init fields
        Cities=new ArrayList<>();
        Ambushes=new ArrayList<>();


        ImageLoader.Loader(getApplicationContext());
        GPSInfo.getInstance(getApplicationContext());
        serverConnect.getInstance().Connect(getResources().getString(R.string.serveradress), this.getApplicationContext());
        serverConnect.getInstance().RefreshData(GPSInfo.getInstance().GetLat(), GPSInfo.getInstance().GetLng());
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupMap();
        player=new Player(mMap);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(player.getMarker().getPosition(), 18));
        createListeners();
    }

    /**
     * Setup map view
     */
    private void setupMap(){

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
        serverConnect.getInstance().AddactionListener(new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Essages.instance.AddEssage("Action done.");
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

    /**
     * Return object by marker
     * @param m Marker
     * @return GameObject
     */
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

    /**
     * Start Timer to load data and other needs.
     */
    private void StartTickTimer(){
        refreshTimer = new Timer("RefreshData");

        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Tick();
                    }
                });
            }
        }, 1000 );
    }

    /**
     * Timed action on tick;
     */
    private void Tick(){
        if (Math.random()*60>58) serverConnect.getInstance().RefreshData(GPSInfo.getInstance().GetLat(), GPSInfo.getInstance().GetLng());
        Essages.instance.Tick();
        essegeText.setText(Essages.instance.getEssagesText());

    }
    @Override
    protected void onPause(){
        super.onPause();
        refreshTimer.cancel();
        refreshTimer.purge();
        refreshTimer=null;
    }
    @Override
    protected void onResume(){
        super.onResume();
        StartTickTimer();

    }
}
