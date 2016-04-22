package coe.com.c0r0vans;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Date;

import coe.com.c0r0vans.GameObjects.Ambush;
import coe.com.c0r0vans.GameObjects.Caravan;
import coe.com.c0r0vans.GameObjects.City;
import coe.com.c0r0vans.GameObjects.GameObject;
import coe.com.c0r0vans.GameObjects.GameObjects;
import coe.com.c0r0vans.GameObjects.MessageMap;
import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.GameObjects.SelectedObject;
import coe.com.c0r0vans.UIElements.ActionView;
import coe.com.c0r0vans.UIElements.ButtonLayout;
import coe.com.c0r0vans.UIElements.ChooseFaction;
import coe.com.c0r0vans.UIElements.LoginView;
import coe.com.c0r0vans.UIElements.UIControler;
import utility.GPSInfo;
import utility.GameSound;
import utility.ImageLoader;
import utility.internet.ServerListener;
import utility.internet.serverConnect;
import utility.notification.Essages;
import utility.notification.MessageNotification;
import utility.settings.GameSettings;

public class MainWindow extends FragmentActivity implements OnMapReadyCallback {
    private Handler myHandler = new Handler();
    private int SendedRequest = 0;
    private MessageMap messages;

    ButtonLayout buttonLayout;


    @Override
    /**
     * Create form;
     */
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("Timing","Start");
        super.onCreate(savedInstanceState);
        Log.d("Timing", "Super");
        GameSettings.init(getApplicationContext());
        Log.d("Timing", "GameSettings");
        buttonLayout= (ButtonLayout) findViewById(R.id.buttonLayout);
        Log.d("Timing","Button");
        Player.instance();
        SharedPreferences sp=getApplicationContext().getSharedPreferences("player",Context.MODE_PRIVATE);
        String pls=sp.getString("player","");
        if (!"".equals(pls)){
            try {
                Player.getPlayer().loadJSON(new JSONObject(pls));
            } catch (JSONException e) {
                Log.d("LoadPlayer","Error:"+pls);
            }
        }
        Log.d("Timing", "Player");
        setContentView(R.layout.activity_main_window);
        Log.d("Timing", "ContentView");
        try {
            ImageLoader.Loader(this.getApplicationContext());


        } catch (Exception e){
            Log.d("UNEXPECTED","Loading error:"+e.toString());

        }
        Log.d("Timing","ImageLoader");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        Log.d("Timing","MapFragment");
        View touchView = findViewById(R.id.touchView);
        Log.d("Timing","Touch");
        ViewGroup lay= (ViewGroup) findViewById(R.id.windowLayout);
        Log.d("Timing","window");
        UIControler.setWindowLayout(lay);
        UIControler.setButtonLayout((ButtonLayout) findViewById(R.id.buttonLayout));
        lay.removeAllViews();
        lay= (ViewGroup) findViewById(R.id.alertLayout);
        UIControler.setAlertLayout(lay);
        UIControler.setActionLayout((ActionView) findViewById(R.id.actionView));
        lay.removeAllViews();
        Log.d("Timing", "UI");
        new LoginView(getApplicationContext()).show();
        Log.d("Timing", "Login");
        touchView.setOnTouchListener(new View.OnTouchListener() {
            long tm = -1;
            Point oldPos;
            //float oldBearing=0;
            Point f1;
            Point f2;
            int firstId;
            int secondId;
            boolean closeCity = true;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        //Зафиксировать позицию и время
                        oldPos = new Point((int) event.getX(), (int) event.getY());

                        tm = new Date().getTime();
                    } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                        if (closeCity) UIControler.getActionLayout().HideView();
                        closeCity = true;
                        //Проверить лонгтап
                        if (Math.abs(oldPos.x - event.getX()) < 20 && Math.abs(oldPos.y - event.getY()) < 20 && (new Date().getTime()) - tm > 1500) {
                            tm = -1;
                        } else if (Math.abs(oldPos.x - event.getX()) < 20 && Math.abs(oldPos.y - event.getY()) < 20) {
                            int distance = 50;
                            GameObject target = null;
                            //Marker
                            for (GameObject o : GameObjects.getInstance().values()) {
                                if (o!=null && o.getMarker() != null) {
                                    Point p = MyGoogleMap.getMap().getProjection().toScreenLocation(o.getMarker().getPosition());
                                    int calc = (int) Math.sqrt(Math.pow(p.x - oldPos.x, 2) + Math.pow(p.y - oldPos.y, 2));
                                    if (!(o instanceof Player || o instanceof Caravan) && calc < distance && o.getMarker().isVisible()) {
                                        target = o;
                                        distance = calc;
                                    }
                                }
                            }
                            if (target != null) {

                                SelectedObject.getInstance().setTarget(target);
                                SelectedObject.getInstance().setPoint(target.getMarker().getPosition());
                                ((ActionView) findViewById(R.id.actionView)).ShowView();
                            } else {
                                //Ambush
                                LatLng latLng = MyGoogleMap.getMap().getProjection().fromScreenLocation(oldPos);
                                float distances = GPSInfo.getDistance(latLng, Player.getPlayer().getMarker().getPosition());
                                if (distances != -1 && distances < Player.getPlayer().getActionDistance()) {
                                    boolean setAmush = true;
                                    for (GameObject o : GameObjects.getInstance().values()) {
                                        if ((o instanceof City || o instanceof Ambush) && o.getMarker() != null && o.getMarker().isVisible()) {
                                            float d = GPSInfo.getDistance(latLng, o.getMarker().getPosition());
                                            if (d < o.getRadius()) setAmush = false;
                                        }
                                    }
                                    if (setAmush) {
                                        SelectedObject.getInstance().setTarget(Player.getPlayer());
                                        SelectedObject.getInstance().setPoint(latLng);
                                        ActionView actionView = (ActionView) findViewById(R.id.actionView);
                                        actionView.ShowView();
                                    }
                                }
                            }
                        } else {
                            SelectedObject.getInstance().hidePoint();
                        }
                        f1 = null;
                        f2 = null;


                    } else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                        closeCity = false;
                        firstId = event.getPointerId(0);
                        secondId = event.getPointerId(event.getActionIndex());

                        f1 = new Point((int) event.getX(0), (int) event.getY(0));
                        f2 = new Point((int) event.getX(event.getActionIndex()), (int) event.getY(event.getActionIndex()));
                    } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
                        try {

                            if (f1 != null && f2 != null && event.getPointerCount() == 2) {
                                Point p1 = new Point((int) event.getX(event.findPointerIndex(firstId)), (int) event.getY(event.findPointerIndex(firstId)));
                                Point p2 = new Point((int) event.getX(event.findPointerIndex(secondId)), (int) event.getY(event.findPointerIndex(secondId)));
                                double angle = getAngle(f1, f2) - getAngle(p1, p2);
                                MyGoogleMap.rotate((float) angle);
                                //Essages.addEssage("Угол"+angle);
                                f1 = p1;
                                f2 = p2;
                            } else if (Math.abs(oldPos.x - event.getX()) < 20 && Math.abs(oldPos.y - event.getY()) < 20 && (new Date().getTime()) - tm > 800) {
                                //Ambush
                                LatLng latLng = MyGoogleMap.getMap().getProjection().fromScreenLocation(oldPos);
                                float distances = GPSInfo.getDistance(latLng, Player.getPlayer().getMarker().getPosition());
                                if (distances != -1 && distances < Player.getPlayer().getActionDistance()) {
                                    boolean setAmush = true;
                                    for (GameObject o : GameObjects.getInstance().values()) {
                                        if ((o instanceof City || o instanceof Ambush) && o.getMarker().isVisible()) {
                                            float d = GPSInfo.getDistance(latLng, o.getMarker().getPosition());
                                            if (d < o.getRadius()) setAmush = false;
                                        }
                                    }
                                    if (setAmush) {
                                        SelectedObject.getInstance().setTarget(Player.getPlayer());
                                        SelectedObject.getInstance().setPoint(latLng);
                                        ActionView actionView = (ActionView) findViewById(R.id.actionView);
                                        actionView.ShowView();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.d("Error", e.toString());
                        }
                        //Проверить поворот
                    } else if (Math.abs(oldPos.x - event.getX()) < 20 && Math.abs(oldPos.y - event.getY()) < 20 && (new Date().getTime()) - tm > 800) {
                        //Ambush
                        LatLng latLng = MyGoogleMap.getMap().getProjection().fromScreenLocation(oldPos);
                        float[] distances = new float[1];
                        Location.distanceBetween(latLng.latitude, latLng.longitude, Player.getPlayer().getMarker().getPosition().latitude, Player.getPlayer().getMarker().getPosition().longitude, distances);
                        if (distances.length > 0 && distances[0] < Player.getPlayer().getActionDistance()) {

                            SelectedObject.getInstance().setTarget(Player.getPlayer());
                            SelectedObject.getInstance().setPoint(latLng);
                            ActionView actionView = (ActionView) findViewById(R.id.actionView);
                            actionView.ShowView();
                        }
                    }
                } catch (Exception e) {
                    serverConnect.getInstance().sendDebug(2,"Gesture UE:" + e.toString()+Arrays.toString(e.getStackTrace()) );
                }
                return true;
            }

            private double getAngle(Point a, Point b) {
                double dx = b.x - a.x;
                // Minus to correct for coord re-mapping
                double dy = -(b.y - a.y);

                double inRads = Math.atan2(dy, dx);

                // We need to map to coord system when 0 degree is at 3 O'clock, 270 at 12 O'clock
                if (inRads < 0)
                    inRads = Math.abs(inRads);
                else
                    inRads = 2 * Math.PI - inRads;

                return Math.toDegrees(inRads);
            }
        });
        Log.d("Timing", "Listener");
        mapFragment.getMapAsync(this);
        Log.d("Timing", "mapFragment");

        init();
        Log.d("Timing", "Init");
        if (serverConnect.getInstance().isLogin()) {
            UIControler.getWindowLayout().removeAllViews();
        }

    }

    /**
     * Make initialization.
     */

    private void init() {
        //init fields

        GameObjects.init();
        Log.d("Timing", "GameObject");
        GPSInfo.getInstance(getApplicationContext());
        Log.d("Timing", "GPS");
        GameSound.init(getApplicationContext());
        GameSound.setVolumeControlStream(this);
        Log.d("Timing", "Sound");
        messages=new MessageMap(getApplicationContext());
        MessageNotification.init(getApplicationContext());
        messageRequest.run();
        Log.d("Timing", "Message");
        serverConnect.getInstance().connect(getResources().getString(R.string.serveradress), this.getApplicationContext());
        Log.d("Timing", "Server");
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("tttt","Map Ready");
        try{

        Point size=new Point();
        MyGoogleMap.init(googleMap, size.y);
        Player.getPlayer().setMap(MyGoogleMap.getMap());
        createListeners();
        }
        catch (Exception e){
            serverConnect.getInstance().sendDebug(2,"Resume UNEXPECTED:"+e.toString());

        }
    }
    boolean isListenersDone=false;
    /**
     * Setup map view
     */

    private void createListeners() {
        if (isListenersDone) return;
        serverConnect.getInstance().addListener(new ServerListener() {
            @Override
            public void onLogin(JSONObject response) {

            }

            @Override
            public void onRefresh(JSONObject response) {
                SendedRequest = 0;
                UIControler.getButtonLayout().hideConnectImage();

            }

            @Override
            public void onAction(JSONObject response) {

            }

            @Override
            public void onPlayerInfo(JSONObject response) {
                Player.getPlayer().loadJSON(response);
                if (Player.getPlayer().getRace() < 1 || Player.getPlayer().getRace() > 3) {
                    new ChooseFaction(getApplicationContext()).show();
                }
                SharedPreferences sp=getApplicationContext().getSharedPreferences("player", Context.MODE_PRIVATE);
                SharedPreferences.Editor ed=sp.edit();
                try {
                    ed.putString("player",Player.getPlayer().getJSON().toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ed.apply();

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
                    if (!"Unexpected Response".equals(errorText) || "Y".equals(GameSettings.getInstance().get("SHOW_NETWORK_ERROR")))
                        Essages.addEssage(errorMsg);
                        else serverConnect.getInstance().sendDebug(3, errorMsg);
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

            @Override
            public void onRating(JSONObject response) {

            }
        });
        isListenersDone=true;
    }

    /**
     * Start Timer to load data and other needs.
     */
    private void StartTickTimer() {
        int delay = 1000;
        try {
            if (serverConnect.getInstance().isLogin() && (timeToPlayerRefresh != -1) && GPSInfo.getInstance().GetLat() != -1 && GPSInfo.getInstance().GetLng() != -1) {
                Log.d("Debug info", "Speed:" + GPSInfo.getInstance().getSpeed());
                if (GPSInfo.getInstance().getSpeed() < 30) delay = 40000;
                else if (GPSInfo.getInstance().getSpeed() > 30) delay = 20000;

            }
        }
        catch (Exception e){
            serverConnect.getInstance().sendDebug(2, "TickTimer UNEXPECTED:" + e.toString());
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
        try {
            Log.d("Debug info", "Time to refresh");
            if (serverConnect.getInstance().isLogin() && this.hasWindowFocus()
                    && GPSInfo.getInstance().GetLat() != -1 && GPSInfo.getInstance().GetLng() != -1)
                if (timeToPlayerRefresh < 1) {
                    serverConnect.getInstance().getPlayerInfo();
                    serverConnect.getInstance().RefreshData((int) (MyGoogleMap.getMap().getCameraPosition().target.latitude * 1e6), (int) (MyGoogleMap.getMap().getCameraPosition().target.longitude * 1e6));


                    timeToPlayerRefresh = 6;
                } else {
                    SendedRequest++;
                    if (SendedRequest > 1) UIControler.getButtonLayout().showConnectImage();
                    timeToPlayerRefresh--;
                    serverConnect.getInstance().RefreshData((int) (MyGoogleMap.getMap().getCameraPosition().target.latitude * 1e6), (int) (MyGoogleMap.getMap().getCameraPosition().target.longitude * 1e6));
                }
        }
        catch (Exception e){
            serverConnect.getInstance().sendDebug(2, "Tick UNEXPECTED:" + e.toString());
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
        Log.d("Timing", "ResumeSuper");
        MessageNotification.cancel();
        Log.d("Timing", "MessageCancel");
        try {
            MessageNotification.appActive = true;
            StartTickTimer();
            GameSound.playMusic();
            //if (!"Y".equals(GameSettings.getInstance().get("GPS_ON_BACK")))
            GPSInfo.getInstance().onGPS();
            if (serverConnect.getInstance().isLogin() && this.hasWindowFocus()
                    && GPSInfo.getInstance().GetLat() != -1 && GPSInfo.getInstance().GetLng() != -1)
                serverConnect.getInstance().RefreshCurrent();
        }
        catch (Exception e){
            serverConnect.getInstance().sendDebug(2,"Resume UNEXPECTED:"+e.toString()+"\n");
        }
        Log.d("Timing", "RestartTick");
    }

    @Override
    public void onBackPressed() {


        if (UIControler.getWindowLayout().getChildCount()>0 && serverConnect.getInstance().isLogin())
        {
            UIControler.getWindowLayout().removeAllViews();
        } else if (findViewById(R.id.actionView).getVisibility()==View.VISIBLE)
        {
            ((ActionView)findViewById(R.id.actionView)).HideView();
        } else if (!MyGoogleMap.isMoveFixed()){
            MyGoogleMap.stopShowPoint();
        }
        else
        {
           Essages.addEssage("Для выхода из приложения используйте кнопку Home.");
            serverConnect.getInstance().sendDebug(0,"Для выхода из приложения используйте кнопку Home.");
        }
    }


}
