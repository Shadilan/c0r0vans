package coe.com.c0r0vans;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

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


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import utility.ServerListener;
import utility.serverConnect;

public class MainWindow extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Player player;
    private ArrayList<GameObject> Objects;
    //private int SetupDone=0;
    private Handler myHandler = new Handler();
    private Timer refreshTimer;
    private ImageView connect_img;
    private int SendedRequest = 0;
    private int clientZoom = 17;

    private TextView LogView;
    private ImageView LogButton;

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

        LogView = (TextView) findViewById(R.id.chatBox);
        LogView.setHeight((int) (LogView.getTextSize() * 2));
        LogButton = (ImageView) findViewById(R.id.showButton);

        connect_img = (ImageView) findViewById(R.id.server_connect);
        ResourceString.getInstance(getApplicationContext());

        mapFragment.getMapAsync(this);
        init();

        if (!serverConnect.getInstance().isLogin()) {
            Intent i = new Intent(getApplicationContext(), Login.class);
            startActivity(i);
        }
    }



    /**
     * Make initialization.
     */

    private void init() {
        //init fields
        Objects = new ArrayList<>();

        ImageLoader.Loader(getApplicationContext());
        GPSInfo.getInstance(getApplicationContext());
        serverConnect.getInstance().connect(getResources().getString(R.string.serveradress), this.getApplicationContext());
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
        player = new Player(mMap);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(player.getMarker().getPosition(), 18));
        createListeners();
    }

    /**
     * Setup map view
     */
    private DateFormat df = new SimpleDateFormat("dd.mm.yyyy HH:mm:ss");
    private void setupMap() {

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
    }


    private void createListeners() {
        ImageView PlayerInfo = (ImageView) findViewById(R.id.infoview);
        PlayerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                SelectedObject.getInstance().setExecuter(player);
                startActivity(i);
            }
        });


        LogButton.setOnClickListener(new View.OnClickListener() {
            private boolean show = false;

            @Override
            public void onClick(View v) {
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                if (show) {
                    show = false;
                    //LogView.setBackgroundColor(Color.WHITE);
                    LogView.setHeight(dm.heightPixels / 2);
                } else {
                    show = true;
                    //LogView.setBackgroundColor(Color.TRANSPARENT);
                    LogView.setHeight((int) (LogView.getTextSize() * 2));
                }
            }
        });


        GPSInfo.getInstance().AddLocationListener(new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                LatLng target = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.animateCamera(CameraUpdateFactory.newLatLng(target));

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
        serverConnect.getInstance().addListener(new ServerListener() {
            @Override
            public void onLogin(JSONObject response) {

            }

            @Override
            public void onRefresh(JSONObject response) {
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

                                remObjects.remove(robj);
                                robj.loadJSON(JObj.getJSONObject(i));
                            } else {
                                if (JObj.getJSONObject(i).getString("Type").equals("Player")) {

                                    player.loadJSON(JObj.getJSONObject(i));

                                } else if (JObj.getJSONObject(i).getString("Type").equals("City")) {

                                    City city = new City(mMap, JObj.getJSONObject(i));
                                    Objects.add(city);
                                } else if (JObj.getJSONObject(i).getString("Type").equals("Ambush")) {

                                    Ambush ambush = new Ambush(mMap, JObj.getJSONObject(i));

                                    Objects.add(ambush);
                                } else if (JObj.getJSONObject(i).getString("Type").equals("Caravan")) {

                                    Caravan caravan = new Caravan(mMap, JObj.getJSONObject(i));
                                    Objects.add(caravan);
                                } else if (JObj.getJSONObject(i).getString("Type").equals("Sign")) {
                                    Log.d("Debug info", "Sign Load:" + JObj.getJSONObject(i).toString());
                                    Log.d("Game Warning", "Sign object");
                                } else {
                                    Log.d("Game Warning", "Unknown object");
                                }
                            }

                        }
                        for (GameObject obj : remObjects) {
                            obj.getMarker().remove();
                        }
                        Objects.removeAll(remObjects);
                    }
                    SendedRequest = 0;
                    connect_img.setVisibility(View.INVISIBLE);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAction(JSONObject response) {
                Log.d("Debug info", "action Done:" + response.toString());
                if (response.has("Result")) {
                    try {
                        LogView.append("\n" + df.format(new Date()) + ":" + response.getString("Message"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else
                    LogView.append("\n" + df.format(new Date()) + ":" + "Ok.");

            }

            @Override
            public void onPlayerInfo(JSONObject response) {
                player.loadJSON(response);
                TextView am = (TextView) findViewById(R.id.levelAmount);
                am.setText(String.valueOf(player.getLevel()));
                am = (TextView) findViewById(R.id.expAmount);
                am.setText(String.valueOf(player.getExp()));
                am = (TextView) findViewById(R.id.goldAmount);
                am.setText(String.valueOf(player.getGold()));

            }

            @Override
            public void onError(JSONObject response) {
                try {
                    Log.d("Debug info", "Error Listener;");
                    String errorText = "";
                    if (response.has("Error")) {
                        errorText = response.getString("Error");
                    }
                    String errorMsg = "";
                    if (response.has("Message")) {
                        errorMsg = response.getString("Message");
                    }
                    if (errorMsg.equals("")) errorMsg = errorText;
                    LogView.append("\n" + df.format(new Date()) + ":" + errorMsg);
                    Log.d("Debug info", LogView.getText().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                float[] distances = new float[1];
                Location.distanceBetween(latLng.latitude, latLng.longitude, player.getMarker().getPosition().latitude, player.getMarker().getPosition().longitude, distances);
                if (distances.length > 0 && distances[0] < player.getActionDistance()) {

                    SelectedObject.getInstance().setExecuter(player);
                    SelectedObject.getInstance().setTarget(player);
                    SelectedObject.getInstance().setPoint(latLng);

                    ActionView actionView = (ActionView) findViewById(R.id.actionView);
                    if (actionView.clickpos!=null){
                        actionView.clickpos.setCenter(latLng);
                        actionView.clickpos.setRadius(player.getAmbushRad());
                    } else{
                        CircleOptions circleOptions = new CircleOptions();
                        circleOptions.center(latLng);
                        circleOptions.radius(player.getAmbushRad());
                        circleOptions.strokeColor(Color.RED);
                        circleOptions.strokeWidth(1);

                        actionView.clickpos = mMap.addCircle(circleOptions);

                    }


                    actionView.ShowView();
                }
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                GameObject target = findObjectByMarker(marker);
                if (target != null) {
                    SelectedObject.getInstance().setExecuter(player);
                    SelectedObject.getInstance().setTarget(target);
                    SelectedObject.getInstance().setPoint(marker.getPosition());
                    ((ActionView) findViewById(R.id.actionView)).ShowView();
                }
                //}
                return true;
            }
        });

        ImageView zoomButton= (ImageView) findViewById(R.id.zoomButton);
        zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (clientZoom){
                    case 16:clientZoom=17;
                        break;
                    case 17:clientZoom=18;
                        break;
                    case 18:clientZoom=16;

                }
                for (GameObject obj:Objects) obj.changeMarkerSize(clientZoom);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(GPSInfo.getInstance().GetLat() / 1e6, GPSInfo.getInstance().GetLng() / 1e6), clientZoom));
            }
        });

    }

    /**
     * Return object by marker
     *
     * @param m Marker
     * @return GameObject
     */
    private GameObject findObjectByMarker(Marker m) {
        if (player.getMarker().equals(m)) {
            return player;
        } else
            for (GameObject obj : Objects) {
                if (obj.getMarker().equals(m)) {
                    Log.d("Debug info", "Selected object:" + obj.getClass().toString());
                    return obj;
                }
            }
        Log.d("Debug info", "Object not found.");
        return null;
    }

    /**
     * Start Timer to load data and other needs.
     */
    private void StartTickTimer() {
        int delay = 1000;

        if (serverConnect.getInstance().isLogin() && !firstRun) {
            Log.d("Debug info","Speed:"+GPSInfo.getInstance().getSpeed());
            if (GPSInfo.getInstance().getSpeed() < 30) delay = 40000;
            else if (GPSInfo.getInstance().getSpeed() > 30) delay = 20000;

        }
        Log.d("DebugCall", "postDelayed");
        Log.d("Debug info","Delay:"+delay);
        myHandler.postDelayed(myRunable, delay);

    }
    Runnable myRunable=new Runnable() {
        @Override
        public void run() {
            Tick();
        }
    };
    /**
     * Timed action on tick;
     */
    private boolean firstRun = true;

    private void Tick() {
        Log.d("Debug info","Time to refresh");
        if (serverConnect.getInstance().isLogin() && this.hasWindowFocus())
            if (firstRun) {
                serverConnect.getInstance().RefreshData(GPSInfo.getInstance().GetLat(), GPSInfo.getInstance().GetLng());
                serverConnect.getInstance().getPlayerInfo();
                firstRun = false;
            } else {
                SendedRequest++;
                if (SendedRequest > 1) connect_img.setVisibility(View.VISIBLE);
                serverConnect.getInstance().RefreshData(GPSInfo.getInstance().GetLat(), GPSInfo.getInstance().GetLng());
            }
        if (job) StartTickTimer();
    }
    private boolean job=true;



    @Override
    protected void onPause(){
        super.onPause();
        myHandler.removeCallbacks(myRunable);
        job=false;

    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DebugCall", "ResumeCall");
        job=true;
        StartTickTimer();

    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.actionView).getVisibility()==View.VISIBLE)
        {
            findViewById(R.id.actionView).setVisibility(View.GONE);
        } else
        {
            super.onBackPressed();
        }
    }
}
