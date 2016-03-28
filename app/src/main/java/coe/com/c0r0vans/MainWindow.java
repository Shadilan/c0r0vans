package coe.com.c0r0vans;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

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
    private HashMap<String,GameObject> Objects;
    private Handler myHandler = new Handler();
    private ImageView connect_img;
    private int SendedRequest = 0;
    private MessageMap messages;
    private LinearLayout LogView;
    private ScrollView scrollView;
    private ImageView LogButton;
    private View touchView;


    @Override
    /**
     * Create form;
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GameSettings.init(getApplicationContext());
        Player.instance();
        setContentView(R.layout.activity_main_window);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        ImageLoader.Loader(this.getApplicationContext());


        LogView = (LinearLayout) findViewById(R.id.chatBox);
        scrollView= (ScrollView) findViewById(R.id.scrollView);
        scrollView.getLayoutParams().height=60;
        touchView=findViewById(R.id.touchView);

        touchView.setOnTouchListener(new View.OnTouchListener() {
            long tm = -1;
            Point oldPos;
            //float oldBearing=0;
            Point f1;
            Point f2;
            int firstId;
            int secondId;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                try {
                    if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
                        //Зафиксировать позицию и время
                        oldPos = new Point((int) event.getX(), (int) event.getY());

                        tm = new Date().getTime();
                    } else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
                        //Проверить лонгтап
                        if (Math.abs(oldPos.x - event.getX()) < 20 && Math.abs(oldPos.y - event.getY()) < 20 && (new Date().getTime()) - tm > 1500) {
                            tm = -1;
                        } else if (Math.abs(oldPos.x - event.getX()) < 20 && Math.abs(oldPos.y - event.getY()) < 20) {
                            int distance = 50;
                            GameObject target = null;
                            //Marker
                            for (GameObject o : Objects.values()) {
                                Point p = MyGoogleMap.getMap().getProjection().toScreenLocation(o.getMarker().getPosition());
                                int calc = (int) Math.sqrt(Math.pow(p.x - oldPos.x, 2) + Math.pow(p.y - oldPos.y, 2));
                                if (!(o instanceof Player || o instanceof Caravan) && calc < distance && o.getMarker().isVisible()) {
                                    target = o;
                                    distance = calc;
                                }
                            }
                            if (target != null) {

                                SelectedObject.getInstance().setTarget(target);
                                SelectedObject.getInstance().setPoint(target.getMarker().getPosition());
                                ((ActionView) findViewById(R.id.actionView)).ShowView();
                            }
                        } else {
                            SelectedObject.getInstance().hidePoint();
                        }
                        f1 = null;
                        f2 = null;

                        //иначе найти маркер
                    } else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {
                        firstId = event.getPointerId(0);
                        secondId = event.getPointerId(event.getActionIndex());

                        f1 = new Point((int) event.getX(0), (int) event.getY(0));
                        f2 = new Point((int) event.getX(event.getActionIndex()), (int) event.getY(event.getActionIndex()));
                    } else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
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
                            float[] distances = new float[1];
                            Location.distanceBetween(latLng.latitude, latLng.longitude, Player.getPlayer().getMarker().getPosition().latitude, Player.getPlayer().getMarker().getPosition().longitude, distances);
                            if (distances.length > 0 && distances[0] < Player.getPlayer().getActionDistance()) {

                                SelectedObject.getInstance().setTarget(Player.getPlayer());
                                SelectedObject.getInstance().setPoint(latLng);
                                ActionView actionView = (ActionView) findViewById(R.id.actionView);
                                actionView.ShowView();
                            }
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
                    Essages.addEssage("Gesture UE:" + e.toString());
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
        Objects = new HashMap<>();

        ImageLoader.Loader(getApplicationContext());
        GPSInfo.getInstance(getApplicationContext());
        GameSound.init(getApplicationContext());
        GameSound.setVolumeControlStream(this);
        messages=new MessageMap(getApplicationContext());
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
        try{
        Point size=new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        MyGoogleMap.init(googleMap, size.y);
        MyGoogleMap.setShowpointButton((ImageButton) findViewById(R.id.showPosButton));
        Player.getPlayer().setMap(MyGoogleMap.getMap());
        createListeners();
        }
        catch (Exception e){
            Essages.addEssage("Resume UNEXPECTED:"+e.toString());
        }
    }
    boolean isListenersDone=false;
    /**
     * Setup map view
     */

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
                for (GameObject obj : Objects.values()) {
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
                    scrollView.getLayoutParams().height = dm.heightPixels / 2;
                    scrollView.requestLayout();

                } else {
                    show = true;
                    //LogView.setBackgroundColor(Color.TRANSPARENT);
                    scrollView.getLayoutParams().height = 60;
                    scrollView.requestLayout();
                }
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
                        ArrayList<GameObject> remObjects = new ArrayList<>(Objects.values());
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

                                    Player.getPlayer().loadJSON(JObj.getJSONObject(i));

                                } else if (JObj.getJSONObject(i).getString("Type").equals("City")) {

                                    City city = new City(MyGoogleMap.getMap(), JObj.getJSONObject(i));
                                    Objects.put(city.getGUID(), city);
                                } else if (JObj.getJSONObject(i).getString("Type").equals("Ambush")) {

                                    Ambush ambush = new Ambush(MyGoogleMap.getMap(), JObj.getJSONObject(i));

                                    Objects.put(ambush.getGUID(), ambush);
                                } else if (JObj.getJSONObject(i).getString("Type").equals("Caravan")) {

                                    Caravan caravan = new Caravan(MyGoogleMap.getMap(), JObj.getJSONObject(i));
                                    Objects.put(caravan.getGUID(), caravan);
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
                        for (GameObject o : remObjects) {
                            Objects.remove(o.getGUID());
                        }

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
                Player.getPlayer().loadJSON(response);
                if (Player.getPlayer().getRace() < 1 || Player.getPlayer().getRace() > 3) {
                    ((ChooseFaction) findViewById(R.id.chooseFaction)).show();
                }
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

        /*MyGoogleMap.getMap().setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                float[] distances = new float[1];
                Location.distanceBetween(latLng.latitude, latLng.longitude, Player.getPlayer().getMarker().getPosition().latitude, Player.getPlayer().getMarker().getPosition().longitude, distances);
                if (distances.length > 0 && distances[0] < Player.getPlayer().getActionDistance()) {

                    SelectedObject.getInstance().setTarget(Player.getPlayer());
                    SelectedObject.getInstance().setPoint(latLng);
                    ActionView actionView = (ActionView) findViewById(R.id.actionView);
                    actionView.ShowView();
                }
            }
        });*/
        /*MyGoogleMap.getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                GameObject target = findObjectByMarker(marker);
                if (target==null || target instanceof Player) return false;

                    SelectedObject.getInstance().setTarget(target);
                    SelectedObject.getInstance().setPoint(marker.getPosition());
                    ((ActionView) findViewById(R.id.actionView)).ShowView();
                return true;
            }
        });*/
        ImageView zoomButton= (ImageView) findViewById(R.id.zoomButton);
        zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MyGoogleMap.switchZoom();
                    for (GameObject obj : Objects.values()) if (obj.getMarker()!=null) obj.changeMarkerSize(MyGoogleMap.getClientZoom());
                } catch(Exception e){
                    Essages.addEssage("UE:"+e.toString());
                }

            }
        });
        Player.getPlayer().addOnChange(new OnGameObjectChange() {
            @Override
            public void change(int ChangeType) {
                NumberFormat nf=NumberFormat.getInstance();
                nf.setGroupingUsed(true);
                if (ChangeType != OnGameObjectChange.EXTERNAL) return;
                TextView am = (TextView) findViewById(R.id.levelAmount);
                am.setText(String.valueOf(Player.getPlayer().getLevel()));
                am = (TextView) findViewById(R.id.expAmount);
                am.setText(String.valueOf(nf.format(Player.getPlayer().getExp())));
                am = (TextView) findViewById(R.id.goldAmount);
                am.setText(String.valueOf(nf.format(Player.getPlayer().getGold())));
                ImageView btn = (ImageView) findViewById(R.id.infoview);
                if ("".equals(Player.getPlayer().getCurrentRoute())) btn.setImageResource(R.mipmap.info);
                else btn.setImageResource(R.mipmap.info_route);
                ((InfoLayout) findViewById(R.id.informationView)).loadFromPlayer();
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
    /*private GameObject findObjectByMarker(Marker m) {
        if (Player.getPlayer().getMarker().equals(m)) {
            return Player.getPlayer();
        } else
            for (GameObject obj : Objects.values()) {
                if (obj.getMarker()!=null && obj.getMarker().equals(m)) {
                    Log.d("Debug info", "Selected object:" + obj.getClass().toString());
                    return obj;
                }
            }
        Log.d("Debug info", "Object not found.");
        return null;
    }*/

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
            Essages.addEssage("TickTimer UNEXPECTED:"+e.toString());
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
                    if (SendedRequest > 1) connect_img.setVisibility(View.VISIBLE);
                    timeToPlayerRefresh--;
                    serverConnect.getInstance().RefreshData((int) (MyGoogleMap.getMap().getCameraPosition().target.latitude * 1e6), (int) (MyGoogleMap.getMap().getCameraPosition().target.longitude * 1e6));
                }
        }
        catch (Exception e){
            Essages.addEssage("Tick UNEXPECTED:"+e.toString());
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
        try {
            Log.d("DebugCall", "ResumeCall");
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
            Essages.addEssage("Resume UNEXPECTED:"+e.toString());
        }
    }

    @Override
    public void onBackPressed() {
        if (findViewById(R.id.informationView).getVisibility()==View.VISIBLE)
        {
            ((InfoLayout)(findViewById(R.id.informationView))).Hide();
        } else if (findViewById(R.id.actionView).getVisibility()==View.VISIBLE)
        {
            ((ActionView)findViewById(R.id.actionView)).HideView();
        } else if (!MyGoogleMap.isMoveFixed()){
            MyGoogleMap.stopShowPoint();
        }
        else
        {
           Essages.addEssage("Для выхода из приложения используйте кнопку Home.");
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {
                case (SETTINGS_CALL): {

                    if (resultCode == Activity.RESULT_OK) {

                        for (GameObject o : Objects.values()) {
                            if (o instanceof Ambush) {
                                ((Ambush) o).showRadius();
                            } else if (o instanceof City) {
                                ((City) o).showRadius();
                            }
                            o.changeMarkerSize(MyGoogleMap.getClientZoom());
                        }
                        Player.getPlayer().showRoute();
                        MyGoogleMap.changeSettings();


                        GameSound.updateSettings();

                    }
                    break;
                }
            }
        }
        catch (Exception e){
            Essages.addEssage("Activity Result UNEXPECTED:"+e.toString());
        }


    }

}
