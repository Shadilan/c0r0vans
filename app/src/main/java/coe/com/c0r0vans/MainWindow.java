package coe.com.c0r0vans;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;

import android.os.Bundle;
import android.os.Handler;

import android.support.v4.app.FragmentActivity;

import android.util.DisplayMetrics;
import android.util.Log;


import android.view.View;


import android.widget.ImageView;
import android.widget.TextView;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



import java.util.ArrayList;



import coe.com.c0r0vans.GameObjects.Ambush;
import coe.com.c0r0vans.GameObjects.Caravan;
import coe.com.c0r0vans.GameObjects.City;
import coe.com.c0r0vans.GameObjects.GameObject;
import coe.com.c0r0vans.GameObjects.MessageMap;
import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.GameObjects.SelectedObject;

import utility.Essages;
import utility.GPSInfo;
import utility.GameSettings;
import utility.ImageLoader;
import utility.ResourceString;
import utility.ServerListener;
import utility.serverConnect;

public class MainWindow extends FragmentActivity implements OnMapReadyCallback {
    private static final int SETTINGS_CALL = 393;
    private GoogleMap mMap;
    private Player player;
    private ArrayList<GameObject> Objects;
    private Handler myHandler = new Handler();
    private ImageView connect_img;
    private int SendedRequest = 0;
    private int clientZoom = 17;
    private MessageMap messages;
    private TextView LogView;
    private ImageView LogButton;



    @Override
    /**
     * Create form;
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        player=new Player();
        SelectedObject.getInstance().setExecuter(player);
        setContentView(R.layout.activity_main_window);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        ImageLoader.Loader(this.getApplicationContext());
        GameSettings.init(getApplicationContext());
        LogView = (TextView) findViewById(R.id.chatBox);
        LogView.setHeight((int) (LogView.getTextSize() * 2));

        Essages.setTarget(LogView);
        LogButton = (ImageView) findViewById(R.id.showButton);

        connect_img = (ImageView) findViewById(R.id.server_connect);
        ResourceString.getInstance(getApplicationContext());

        mapFragment.getMapAsync(this);
        init();

        if (serverConnect.getInstance().isLogin()) {
            LoginView lv= (LoginView) findViewById(R.id.loginView);
            lv.hide();
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
        GameSound.init(getApplicationContext());
        GameSound.setVolumeControlStream(this);
        messages=new MessageMap();
        MessageNotification.init(getApplicationContext());
        messageRequest.run();
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
        player.setMap(mMap);// = new Player(mMap);


        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(player.getMarker().getPosition(), clientZoom));
        if ("Y".equals(GameSettings.getInstance().get("USE_TILT")))
            mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(player.getMarker().getPosition())
                            .tilt(60)
                            .zoom(clientZoom)
                            .build()));
        else
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                new CameraPosition.Builder()
                        .target(player.getMarker().getPosition())
                        .tilt(0)
                        .zoom(clientZoom)
                        .build()));
        createListeners();
    }
    boolean isListenersDone=false;
    /**
     * Setup map view
     */

    private void setupMap() {

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setBuildingsEnabled(false);
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setTiltGesturesEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);

    }


    private void createListeners() {
        if (isListenersDone) return;

        ImageView PlayerInfo = (ImageView) findViewById(R.id.infoview);
        PlayerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoLayout info = (InfoLayout) findViewById(R.id.informationView);
                info.Show();


            }
        });

        ImageView Settings = (ImageView) findViewById(R.id.settings);
        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Settings.class);
                startActivityForResult(i, SETTINGS_CALL);

            }
        });
        Settings.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                //ForceSync
                //Remove all Objects
                for (GameObject obj : Objects) {
                    obj.RemoveObject();
                }
                Objects.clear();
                //Run Refresh
                serverConnect.getInstance().RefreshCurrent();
                //Run Player
                serverConnect.getInstance().getPlayerInfo();
                //RunGetMessage
                serverConnect.getInstance().getMessage();
                return true;
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
                if ("Y".equals(GameSettings.getInstance().get("USE_TILT")))
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(target)
                                    .tilt(60)
                                    .bearing(mMap.getCameraPosition().bearing)
                                    .zoom(clientZoom)
                                    .build()));
                else
                    mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                            new CameraPosition.Builder()
                                    .target(target)
                                    .bearing(mMap.getCameraPosition().bearing)
                                    .tilt(0)
                                    .zoom(clientZoom)
                                    .build()));

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
                            obj.RemoveObject();
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

            }

            @Override
            public void onPlayerInfo(JSONObject response) {
                player.loadJSON(response);
                timeToPlayerRefresh = 6;
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
                    if (!"Unexpected Response".equals(errorText)||"Y".equals(GameSettings.getInstance().get("SHOW_NETWORK_ERROR")))
                        Essages.addEssage(errorMsg);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(JSONObject response) {
                if (response.has("Messages")) {
                    try {
                        messages.loadJSON(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                float[] distances = new float[1];
                Location.distanceBetween(latLng.latitude, latLng.longitude, player.getMarker().getPosition().latitude, player.getMarker().getPosition().longitude, distances);
                if (distances.length > 0 && distances[0] < player.getActionDistance()) {

                    SelectedObject.getInstance().setTarget(player);
                    SelectedObject.getInstance().setPoint(latLng);

                    ActionView actionView = (ActionView) findViewById(R.id.actionView);
                    if (actionView.clickpos != null) {
                        actionView.clickpos.setCenter(latLng);
                        actionView.clickpos.setRadius(player.getAmbushRad());

                    } else {
                        CircleOptions circleOptions = new CircleOptions();
                        circleOptions.center(latLng);
                        circleOptions.radius(player.getAmbushRad());
                        circleOptions.strokeColor(Color.RED);
                        circleOptions.strokeWidth(1);

                        actionView.clickpos = mMap.addCircle(circleOptions);

                    }
                    if (actionView.clickPoint != null) {
                        actionView.clickPoint.setCenter(latLng);
                    } else {
                        CircleOptions circleOptions = new CircleOptions();
                        circleOptions.center(latLng);
                        circleOptions.radius(1);
                        circleOptions.strokeColor(Color.RED);
                        circleOptions.strokeWidth(5);

                        actionView.clickPoint = mMap.addCircle(circleOptions);
                    }


                    actionView.ShowView();
                }
            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                GameObject target = findObjectByMarker(marker);
                if (target instanceof Player) return false;
                if (target != null) {
                    SelectedObject.getInstance().setTarget(target);
                    SelectedObject.getInstance().setPoint(marker.getPosition());
                    ((ActionView) findViewById(R.id.actionView)).ShowView();
                }
                //}
                return true;
            }
        });
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            float bearing = mMap.getCameraPosition().bearing;

            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (cameraPosition.bearing != bearing) {
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(player.getMarker().getPosition()));
                    bearing = cameraPosition.bearing;
                }
                float[] distances = new float[1];
            }

            });

        ImageView zoomButton= (ImageView) findViewById(R.id.zoomButton);
        zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                for (GameObject obj : Objects) obj.changeMarkerSize(clientZoom);
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(GPSInfo.getInstance().GetLat() / 1e6, GPSInfo.getInstance().GetLng() / 1e6), clientZoom));
            }
        });
        player.addOnChange(new OnGameObjectChange() {
            @Override
            public void change(int ChangeType) {

                if (ChangeType != OnGameObjectChange.EXTERNAL) return;
                TextView am = (TextView) findViewById(R.id.levelAmount);
                am.setText(String.valueOf(player.getLevel()));
                am = (TextView) findViewById(R.id.expAmount);
                am.setText(String.valueOf(player.getExp()));
                am = (TextView) findViewById(R.id.goldAmount);
                am.setText(String.valueOf(player.getGold()));
                ImageView btn= (ImageView) findViewById(R.id.infoview);
                if ("".equals(player.getCurrentRoute())) btn.setImageResource(R.mipmap.info);
                else btn.setImageResource(R.mipmap.info_route);
                ((InfoLayout)findViewById(R.id.informationView)).loadFromPlayer();
            }
        });
        isListenersDone=true;

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

        if (serverConnect.getInstance().isLogin() && (timeToPlayerRefresh!=-1) && GPSInfo.getInstance().GetLat()!=-1 && GPSInfo.getInstance().GetLng()!=-1) {
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

    int timeToPlayerRefresh=-1;
    private void Tick() {
        Log.d("Debug info","Time to refresh");
        if (serverConnect.getInstance().isLogin() && this.hasWindowFocus()
                && GPSInfo.getInstance().GetLat()!=-1 && GPSInfo.getInstance().GetLng()!=-1)
            if (timeToPlayerRefresh<1) {
                serverConnect.getInstance().RefreshData(GPSInfo.getInstance().GetLat(), GPSInfo.getInstance().GetLng());
                serverConnect.getInstance().getPlayerInfo();

                timeToPlayerRefresh = 6;
            } else {
                SendedRequest++;
                if (SendedRequest > 1) connect_img.setVisibility(View.VISIBLE);
                timeToPlayerRefresh--;
                serverConnect.getInstance().RefreshData(GPSInfo.getInstance().GetLat(), GPSInfo.getInstance().GetLng());
            }
        StartTickTimer();
    }
    private Runnable messageRequest=new Runnable() {
        @Override
        public void run() {
            if (serverConnect.getInstance().isLogin()) serverConnect.getInstance().getMessage();
            myHandler.removeCallbacks(messageRequest);
            myHandler.postDelayed(messageRequest, 60000);

        }
    };


    @Override
    protected void onPause(){
        super.onPause();
        myHandler.removeCallbacks(myRunable);

        MessageNotification.appActive=false;
        GameSound.stopMusic();
        if (!"Y".equals(GameSettings.getInstance().get("GPS_ON_BACK"))) GPSInfo.getInstance().offGPS();

    }
    @Override
    protected void onResume() {
        super.onResume();
        Log.d("DebugCall", "ResumeCall");
        MessageNotification.appActive=true;
        StartTickTimer();
        GameSound.playMusic();
        //if (!"Y".equals(GameSettings.getInstance().get("GPS_ON_BACK")))
            GPSInfo.getInstance().onGPS();
        if (serverConnect.getInstance().isLogin() && this.hasWindowFocus()
                && GPSInfo.getInstance().GetLat()!=-1 && GPSInfo.getInstance().GetLng()!=-1)
            serverConnect.getInstance().RefreshCurrent();
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.informationView).getVisibility()==View.VISIBLE)
        {
            ((InfoLayout)(findViewById(R.id.informationView))).Hide();
        } else if (findViewById(R.id.actionView).getVisibility()==View.VISIBLE)
        {
            findViewById(R.id.actionView).setVisibility(View.GONE);
        } else
        {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case (SETTINGS_CALL): {

                if (resultCode == Activity.RESULT_OK) {

                    for (GameObject o:Objects){
                        if (o instanceof Ambush){
                            ((Ambush) o).showRadius();
                        } else if (o instanceof City)
                        {
                            ((City) o).showRadius();
                        }
                    }
                    player.showRoute();

                    if ("Y".equals(GameSettings.getInstance().get("USE_TILT")))
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(player.getMarker().getPosition())
                                        .tilt(60)
                                        .zoom(clientZoom)
                                        .build()));
                    else
                        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(
                                new CameraPosition.Builder()
                                        .target(player.getMarker().getPosition())
                                        .tilt(0)
                                        .zoom(clientZoom)
                                        .build()));

                    GameSound.updateSettings();

                }
                break;
            }
        }


    }
}
