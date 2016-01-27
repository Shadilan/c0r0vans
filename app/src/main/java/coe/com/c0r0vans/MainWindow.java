package coe.com.c0r0vans;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceScreen;
import android.support.v4.app.FragmentActivity;

import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Response;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import coe.com.c0r0vans.GameObjects.Ambush;
import coe.com.c0r0vans.GameObjects.Caravan;
import coe.com.c0r0vans.GameObjects.City;
import coe.com.c0r0vans.GameObjects.GameObject;
import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.GameObjects.SelectedObject;

import utility.GPSInfo;
import utility.ImageLoader;
import utility.ResourceString;
import utility.serverConnect;

public class MainWindow extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Player player;
    private ArrayList<GameObject> Objects;
    //private int SetupDone=0;
    private Handler myHandler = new Handler();
    private Timer refreshTimer;
    private ImageView connect_img;
    private int SendedRequest=0;
    private int clientZoom=18;
    private Circle clickpos;
    private MainWindow main;
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
        ImageView showChat= (ImageView) findViewById(R.id.showchat);
        main=this;
        showChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplayMetrics dm=new DisplayMetrics();
                main.getWindowManager().getDefaultDisplay().getMetrics(dm);
                Log.d("Debug disain","Clicked chat"+v.getY()+" "+v.getHeight()+" "+dm.heightPixels);
                if (v.getY() > dm.heightPixels/2){
                    //Открыть
                    v.setY(getResources().getDimension(R.dimen.half_vertical));
                    EditText txt= (EditText) findViewById(R.id.chatBox);
                    txt.setVisibility(View.VISIBLE);
                    Log.d("Debug disain", "Open chat:" + txt.getWidth() + " " + txt.getHeight());
                } else
                {
                    //Закрыть
                    v.setY(dm.heightPixels-v.getHeight()/2*3);
                    EditText txt= (EditText) findViewById(R.id.chatBox);
                    txt.setVisibility(View.INVISIBLE);
                    Log.d("Debug disain", "Close chat:" + txt.getWidth()+" "+txt.getHeight());
                }

            }
        });

        ImageView PlayerInfo= (ImageView) findViewById(R.id.infoview);
        PlayerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(i);
            }
        });
        connect_img = (ImageView) findViewById(R.id.server_connect);
        ResourceString.getInstance(getApplicationContext());
        Log.d("PackageInfo", getApplicationContext().getPackageName());
        Log.d("PackageInfo", getPackageName());
        Log.d("InfoSend", "ActivityStarted");

/*        PlayerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), RouteList.class);
                startActivity(i);
            }
        });*/
        mapFragment.getMapAsync(this);
        init();
        Log.d("LoginActivity","Start Login.");
        if (!serverConnect.getInstance().isLogin()) {
            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivity(i);
        }

    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && clickpos!=null){
            clickpos.remove();
            clickpos=null;
        }
    }

    /**
     * Make initialization.
     */

    private void init(){
        //init fields
        Objects=new ArrayList<>();

        ImageLoader.Loader(getApplicationContext());
        GPSInfo.getInstance(getApplicationContext());
        serverConnect.getInstance().connect(getResources().getString(R.string.serveradress), this.getApplicationContext());
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
            mMap.getUiSettings().setZoomGesturesEnabled(true);
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(target, clientZoom));
                serverConnect.getInstance().RefreshData((int)(location.getLatitude()*1e6),(int)(location.getLongitude()*1e6));
                Log.d("MapViewTest", "Coord:" + location.getLatitude() + "x" + location.getLongitude());
                player.getMarker().setPosition(target);
                player.getCircle().setCenter(target);
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
        serverConnect.getInstance().addDataListener(new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    //Проверить наличие массива JSON. Objects
                    if (response.has("Objects")) {
                        //Скопировать данные в массив для удаления
                        ArrayList<GameObject> remObjects = new ArrayList<>(Objects);
                        JSONArray JObj = response.getJSONArray("Objects");
                        int leng = JObj.length();

                        for (int i = 0; i < leng; i++) {
                            GameObject robj = null;
                            for (GameObject obj : remObjects) {
                                if (obj.getGUID().equals(JObj.getJSONObject(i).getString("GUID"))) {
                                    robj = obj;
                                    break;
                                }
                            }
                            if (robj != null) {
                                Log.d("Debug info", "Exciting object:" + robj.getClass().toString() + " text:" + JObj.getJSONObject(i).toString());
                                remObjects.remove(robj);
                                robj.loadJSON(JObj.getJSONObject(i));
                            } else {
                                if (JObj.getJSONObject(i).getString("Type").equals("Player")) {
                                    Log.d("Debug info","Player Load:"+JObj.getJSONObject(i).toString());
                                    player.loadJSON(JObj.getJSONObject(i));

                                } else if (JObj.getJSONObject(i).getString("Type").equals("City")) {
                                    Log.d("Debug info","City Load:"+JObj.getJSONObject(i).toString());
                                    City city = new City(mMap, JObj.getJSONObject(i));
                                    Objects.add(city);
                                } else if (JObj.getJSONObject(i).getString("Type").equals("Ambush")) {
                                    Log.d("Debug info", "Ambush Load:" + JObj.getJSONObject(i).toString());
                                    Ambush ambush = new Ambush(mMap,JObj.getJSONObject(i));

                                    Objects.add(ambush);
                                } else if (JObj.getJSONObject(i).getString("Type").equals("Caravan")) {
                                    Log.d("Debug info","Caravan Load:"+JObj.getJSONObject(i).toString());
                                    Caravan caravan = new Caravan(mMap,JObj.getJSONObject(i));
                                    Objects.add(caravan);
                                } else if (JObj.getJSONObject(i).getString("Type").equals("Sign")) {
                                    Log.d("Debug info","Sign Load:"+JObj.getJSONObject(i).toString());
                                    Log.d("Game Warning", "Sign object");
                                } else {
                                    Log.d("Game Warning", "Unknown object");
                                }
                            }

                        }
                        for (GameObject obj:remObjects){
                            obj.getMarker().remove();
                            Log.d("Debug info", "Remove object:" + obj.getClass().toString() + " GUID:" + obj.getGUID() );
                        }
                        Objects.removeAll(remObjects);
                    }
                    SendedRequest = 0;
                    connect_img.setVisibility(View.INVISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        serverConnect.getInstance().addActionListener(new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (!response.getString("Result").equalsIgnoreCase("Success")) {
                        Log.d("Game Error:", response.getString("Error"));
                        Log.d("Game Error:", response.getString("Message"));
                    }
                } catch (JSONException e) {
                    Log.d("Unexped Error:", e.toString());
                }

            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                float[] distances=new float[1];
                Location.distanceBetween(latLng.latitude, latLng.longitude, player.getMarker().getPosition().latitude, player.getMarker().getPosition().longitude, distances);
                if (distances.length>0 && distances[0]<50) {
                    Intent myIntent = new Intent(getApplicationContext(), ActionsActivity.class);
                    SelectedObject.getInstance().setExecuter(player);
                    SelectedObject.getInstance().setTarget(player);
                    SelectedObject.getInstance().setPoint(latLng);
                    CircleOptions circleOptions = new CircleOptions();
                    circleOptions.center(latLng);
                    circleOptions.radius(player.getAmbushRad());
                    circleOptions.strokeColor(Color.RED);
                    circleOptions.strokeWidth(2);
                    clickpos = mMap.addCircle(circleOptions);
                    startActivity(myIntent);
                }
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                GameObject target = findObjectByMarker(marker);
                if (target!=null) {
                    //float[] distances=new float[1];
                    //Location.distanceBetween(marker.getPosition().latitude, marker.getPosition().longitude, player.getMarker().getPosition().latitude, player.getMarker().getPosition().longitude, distances);
                    //if (distances!=null && distances.length>0 && distances[0]<100) {
                    Intent myIntent = new Intent(getApplicationContext(), ActionsActivity.class);
                    SelectedObject.getInstance().setExecuter(player);
                    SelectedObject.getInstance().setTarget(target);
                    SelectedObject.getInstance().setPoint(marker.getPosition());
                    startActivity(myIntent);
                }
                //}
                return false;
            }
        });
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                clientZoom= (int) cameraPosition.zoom;
                if (clientZoom<16) {
                    clientZoom=16;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(GPSInfo.getInstance().GetLat()/1e6,GPSInfo.getInstance().GetLng()/1e6), clientZoom));
                }
                if (clientZoom>20) {
                    clientZoom=20;
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(GPSInfo.getInstance().GetLat()/1e6,GPSInfo.getInstance().GetLng()/1e6), clientZoom));
                }
                if (GPSInfo.getInstance().GetLat()!=cameraPosition.target.latitude*1e6||GPSInfo.getInstance().GetLng()!=cameraPosition.target.longitude*1e6){
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(GPSInfo.getInstance().GetLat()/1e6,GPSInfo.getInstance().GetLng()/1e6), clientZoom));
                }

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
        } else
        for (GameObject obj:Objects){
            if (obj.getMarker().equals(m)){
                Log.d("Debug info","Selected object:"+obj.getClass().toString());
                return obj;
            }
        }
        Log.d("Debug info","Object not found.");
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
        if (Math.random()*60>56) {
            SendedRequest++;
            if (SendedRequest>1) connect_img.setVisibility(View.VISIBLE);
            serverConnect.getInstance().RefreshData(GPSInfo.getInstance().GetLat(), GPSInfo.getInstance().GetLng());

        }
        StartTickTimer();

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
