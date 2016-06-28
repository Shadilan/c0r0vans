package com.coe.c0r0vans;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.coe.c0r0vans.GameObjects.Ambush;
import com.coe.c0r0vans.GameObjects.Caravan;
import com.coe.c0r0vans.GameObjects.City;
import com.coe.c0r0vans.GameObjects.GameObject;
import com.coe.c0r0vans.GameObjects.GameObjects;
import com.coe.c0r0vans.GameObjects.MessageMap;
import com.coe.c0r0vans.GameObjects.Player;
import com.coe.c0r0vans.GameObjects.SelectedObject;
import com.coe.c0r0vans.UIElements.ActionView;
import com.coe.c0r0vans.UIElements.ButtonLayout;
import com.coe.c0r0vans.UIElements.ChooseFaction;
import com.coe.c0r0vans.UIElements.UIControler;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import utility.GATracker;
import utility.GPSInfo;
import utility.GameSound;
import utility.GameVibrate;
import utility.ImageLoader;
import utility.MainThread;
import utility.internet.ServerListener;
import utility.internet.serverConnect;
import utility.notification.Essages;
import utility.notification.MessageNotification;
import utility.settings.GameSettings;

public class MainWindow extends FragmentActivity implements OnMapReadyCallback {

    private int SendedRequest = 0;
    private MessageMap messages;
    MainWindow self=this;
    private boolean ready=false;
    GoogleApiClient mGoogleApiClient;

    @Override
    /**
     * Create form;
     */
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("ProcedureCall","onCreate");
        super.onCreate(savedInstanceState);
        GATracker.initialize((CorovanApplication) getApplication());
        MainThread.init();
        setContentView(R.layout.main_activity);
        signIn();
    }
    int signCall=0;
    private  void signIn(){
        if (signCall>3){
            ((TextView)findViewById(R.id.status)).setText(R.string.server_unavaible);
            signCall=0;
            MainThread.postDelayed(new Runnable() {
                @Override
                public void run() {
                    signIn();
                }
            },30000);
        }

        serverConnect.getInstance().connect(getResources().getString(R.string.serveradress), getApplicationContext());
        GATracker.trackTimeStart("System","SignIn");
        ((TextView)findViewById(R.id.status)).setText(R.string.enter_google_account);
        SharedPreferences sharedPreferences=getSharedPreferences("SpiritProto", MODE_PRIVATE);
        String accountName=sharedPreferences.getString("AccountName", "");
        Log.d("SignOff","Account:"+accountName);
        GoogleSignInOptions gso;

        if ("".equals(accountName)) {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("818299087088-ooq951dsv5btv7361u4obhlse0apt3al.apps.googleusercontent.com")
                    .requestEmail()
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, 123);
        }
        else {
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken("818299087088-ooq951dsv5btv7361u4obhlse0apt3al.apps.googleusercontent.com")
                    .requestEmail()
                    .setAccountName(accountName)
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            startActivityForResult(signInIntent, 123);

        }


    }

    protected void onActivityResult(final int requestCode, final int resultCode,
                                    final Intent data) {
        Log.d("ProcedureCall","onActivityResult");


        if (requestCode == 123) {
            ((TextView)findViewById(R.id.status)).setText(R.string.done_google_account);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            doResult(result);
        }
    }

    private void doResult(GoogleSignInResult result){
        if (result!=null && result.isSuccess()) {

            GoogleSignInAccount acct = result.getSignInAccount();
            // Get account information
            //String accountNAme = acct.get();
            if (acct!=null) {
                idToken = acct.getIdToken();
                String mEmail = acct.getEmail();
                SharedPreferences sharedPreferences = getSharedPreferences("SpiritProto", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("AccountName", mEmail);
                editor.apply();
                GATracker.trackTimeEnd("System","SignIn");
                //Login To Server
                loginWithToken();

                //initStart();
            } else signIn();
        } else {
            signIn();
        }
    }

    String idToken;
    private void loginWithToken(){
        Log.d("ProcedureCall","loginWithToken");
        GATracker.trackTimeStart("System","LoginToServer");
        serverConnect.getInstance().addListener(new ServerListener() {
            @Override
            public void onResponse(int TYPE, JSONObject response) {
                Log.d("InitR","Type:"+TYPE);
                if (TYPE==LOGIN){
                    serverConnect.getInstance().removeListener(this);
                    GATracker.trackTimeEnd("System","LoginToServer");
                    if (response.has("Token")) {
                        Log.d("InitR","Token");
                        ((TextView)findViewById(R.id.status)).setText("Вход выполнен1.");
                        // Выполнить дальнейшую загрузку
                        initGPS();
                    } else if (response.has("Error")){
                        Log.d("InitR","Error");
                        try {
                            String err=response.getString("Error");
                            Log.d("InitR",err);
                            switch (err){
                                case "H0101":
                                    ((TextView)findViewById(R.id.status)).setText("Сервер отказал запрос.");
                                    break;
                                //Token не распознан
                                case "L0201":
                                    //Указать на ошибку
                                    ((TextView)findViewById(R.id.status)).setText("Токен не действителен.");
                                    //Получить новый токен
                                    signIn();
                                    break;
                                //Пользователь не зарегестрирован
                                case "L0202":
                                    //Начать процесс регистрации
                                    ((TextView)findViewById(R.id.status)).setText("Регистрация пользователя.");
                                    initRegister();
                                    break;
                                //Версия не поддерживается
                                case "L0203":
                                    //Указать ошибку
                                    ((TextView)findViewById(R.id.status)).setText("Ваша версия не поддерживается.");
                                    //Остановить процесс загрузки
                                    break;
                                default:
                                    ((TextView)findViewById(R.id.status)).setText("Неопознанная ошибка.");
                                    MainThread.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loginWithToken();
                                        }
                                    },5000);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                }
            }

            @Override
            public void onError(int TYPE, JSONObject response) {
                //TODO После реализации корректной передачи ошибок сделать разделение
                serverConnect.getInstance().removeListener(this);
                GATracker.trackTimeEnd("System","RegisterToServer");
                try {
                    String err=response.getString("Error");
                    Log.d("InitR",err);
                    switch (err){
                        case "H0101":
                            ((TextView)findViewById(R.id.status)).setText("Сервер отказал запрос.");
                            break;
                        //Token не распознан
                        case "L0201":
                            //Указать на ошибку
                            ((TextView)findViewById(R.id.status)).setText("Токен не действителен.");
                            //Получить новый токен
                            signIn();
                            break;
                        //Пользователь не зарегестрирован
                        case "L0202":
                            //Начать процесс регистрации
                            ((TextView)findViewById(R.id.status)).setText("Регистрация пользователя.");
                            initRegister();
                            break;
                        //Версия не поддерживается
                        case "L0203":
                            //Указать ошибку
                            ((TextView)findViewById(R.id.status)).setText("Ваша версия не поддерживается.");
                            //Остановить процесс загрузки
                            break;
                        default:
                            ((TextView)findViewById(R.id.status)).setText("Неопознанная ошибка.");
                            MainThread.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loginWithToken();
                                }
                            },5000);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        serverConnect.getInstance().ExecAuthorize(idToken);
    }
    private void initRegister(){
        Log.d("ProcedureCall","initRegister");
        findViewById(R.id.regForm).setVisibility(View.VISIBLE);
        findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                serverConnect.getInstance().addListener(new ServerListener() {
                    @Override
                    public void onResponse(int TYPE, JSONObject response) {
                        if (TYPE==LOGIN){
                            serverConnect.getInstance().removeListener(this);
                            GATracker.trackTimeEnd("System","RegisterToServer");
                            if (response.has("Token")) {
                                ((TextView)findViewById(R.id.status)).setText("Вход выполнен2.");
                                // Выполнить дальнейшую загрузку
                                initGPS();
                            } else if (response.has("Error")){
                                try {
                                    String err=response.getString("Error");
                                    switch (err){
                                        case "H0101":
                                            ((TextView)findViewById(R.id.status)).setText("Сервер отказал запрос.");
                                            break;
                                        //Token не распознан
                                        case "L0301":
                                            //Указать на ошибку
                                            ((TextView)findViewById(R.id.status)).setText("Токен не действителен.");
                                            //Получить новый токен
                                            signIn();
                                            break;
                                        //Пользователь не зарегестрирован
                                        case "L0202":
                                            //Начать процесс регистрации
                                            ((TextView)findViewById(R.id.status)).setText("Регистрация пользователя.");
                                            initRegister();
                                            break;
                                        //Версия не поддерживается
                                        case "L0303":
                                            //Указать ошибку
                                            ((TextView)findViewById(R.id.status)).setText("Пользователь зарегестрирован ранее.");
                                            initRegister();
                                            break;
                                        //Версия не поддерживается
                                        case "L0304":
                                            //Указать ошибку
                                            ((TextView)findViewById(R.id.status)).setText("Ваша версия не поддерживается.");
                                            //Остановить процесс загрузки
                                            break;
                                        default:
                                            ((TextView)findViewById(R.id.status)).setText("Неопознанная ошибка.");
                                            MainThread.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    loginWithToken();
                                                }
                                            },5000);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(int TYPE, JSONObject response) {
                        //TODO после реализации разделения ошибок сделать здесь разделение
                        serverConnect.getInstance().removeListener(this);
                        GATracker.trackTimeEnd("System","RegisterToServer");
                        try {
                            String err=response.getString("Error");
                            switch (err){
                                case "H0101":
                                    ((TextView)findViewById(R.id.status)).setText("Сервер отказал запрос.");
                                    break;
                                //Token не распознан
                                case "L0301":
                                    //Указать на ошибку
                                    ((TextView)findViewById(R.id.status)).setText("Токен не действителен.");
                                    //Получить новый токен
                                    signIn();
                                    break;
                                //Пользователь не зарегестрирован
                                case "L0202":
                                    //Начать процесс регистрации
                                    ((TextView)findViewById(R.id.status)).setText("Регистрация пользователя.");
                                    initRegister();
                                    break;
                                //Версия не поддерживается
                                case "L0303":
                                    //Указать ошибку
                                    ((TextView)findViewById(R.id.status)).setText("Пользователь зарегестрирован ранее.");
                                    initRegister();
                                    break;
                                //Версия не поддерживается
                                case "L0304":
                                    //Указать ошибку
                                    ((TextView)findViewById(R.id.status)).setText("Ваша версия не поддерживается.");
                                    //Остановить процесс загрузки
                                    break;
                                default:
                                    ((TextView)findViewById(R.id.status)).setText("Неопознанная ошибка.");
                                    MainThread.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            loginWithToken();
                                        }
                                    },5000);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                String userName= ((TextView) findViewById(R.id.regName)).getText().toString();
                String invite= ((TextView) findViewById(R.id.inviteCode)).getText().toString();
                if (
                        !"".equals(userName) && !"".equals(invite)) {
                    ((TextView) findViewById(R.id.status)).setText("Запрос регистрации.");
                    GATracker.trackTimeEnd("System", "RegisterToServer");
                    findViewById(R.id.regForm).setVisibility(View.INVISIBLE);
                    serverConnect.getInstance().ExecRegister(idToken, userName, invite);
                }
            }
        });
        findViewById(R.id.exitAccount).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (mGoogleApiClient!=null) {
                    SharedPreferences sp = getApplicationContext().getSharedPreferences("SpiritProto", Context.MODE_PRIVATE);
                    String accountName = sp.getString("AccountName", "");
                    sp.edit().clear().apply();
                    sp = getApplicationContext().getSharedPreferences("MESSAGES", Context.MODE_PRIVATE);
                    sp.edit().clear().apply();
                    sp = getApplicationContext().getSharedPreferences("player", Context.MODE_PRIVATE);
                    sp.edit().clear().apply();
                    if (mGoogleApiClient.isConnected()){
                        Auth.GoogleSignInApi.signOut(mGoogleApiClient);

                        GATracker.trackHit("System", "ExitApplication");
                        //Todo Как корректно завершить приложение
                        System.exit(0);
                    } else
                    {
                        mGoogleApiClient.registerConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                            @Override
                            public void onConnected(@Nullable Bundle bundle) {
                                Auth.GoogleSignInApi.signOut(mGoogleApiClient);
                                GATracker.trackHit("System", "ExitApplication");
                                //Todo Как корректно завершить приложение
                                System.exit(0);
                            }

                            @Override
                            public void onConnectionSuspended(int i) {

                            }
                        });
                        mGoogleApiClient.connect();
                    }

                }

            }
        });
    }


    private void initGPS(){
        Log.d("ProcedureCall","initGPS");
        GATracker.trackTimeStart("System","LocationStart");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)){
            this.requestPermissions(
                    new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                    },31
            );

        } else {
            ((TextView) findViewById(R.id.status)).setText(R.string.get_location);
            GPSInfo.getInstance(getApplicationContext());
            GPSInfo.getInstance().AddLocationListener(new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    try {

                        if (GPSInfo.getInstance().GetLat() != -1 && GPSInfo.getInstance().GetLng() != -1) {
                            GPSInfo.getInstance().RemoveLocationListener(this);
                            ((TextView) findViewById(R.id.status)).setText(R.string.location_aquired);
                            GATracker.trackTimeEnd("System", "LocationStart");
                            initStart();
                        }
                    } catch (Exception e) {
                        GATracker.trackException("GPS", e);
                    }
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
            GPSInfo.getInstance().onGPS();
        }
    }

    private void initStart(){
        Log.d("ProcedureCall","initStart");
        GATracker.trackTimeStart("System","Init");
        ((TextView)findViewById(R.id.status)).setText(R.string.data_init);
        //Даем время интерфейсу
        MainThread.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Запускаем отдельный поток для прогрузки
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        ofThreadInit();
                        //Возвращаемся в основной поток для работы с UI.
                        MainThread.post(new Runnable() {
                            @Override
                            public void run() {
                                init();
                            }
                        });
                    }
                });
                thread.start();
            }
        }, 1000);
    }

    private void ofThreadInit(){
        Log.d("ProcedureCall","ofThreadInit");
        GameSettings.init(getApplicationContext());
        GameSettings.getInstance().mClient=mGoogleApiClient;
        ImageLoader.Loader(getApplicationContext());
        GameSound.init(getApplicationContext());
        MessageNotification.init(getApplicationContext());
        GameVibrate.init(getApplicationContext());
        Player.instance();
        SharedPreferences sp = getApplicationContext().getSharedPreferences("player", Context.MODE_PRIVATE);
        String pls = sp.getString("player", "");
        if (!"".equals(pls)) {
            try {
                Player.getPlayer().loadJSON(new JSONObject(pls));
            } catch (JSONException e) {
                GATracker.trackException("LoadPlayer",e);
            }
        }
        GameObjects.init();
    }
    /**
     * Make initialization.
     */

    private void init() {
        Log.d("ProcedureCall","init");
        //init fields
        GATracker.trackTimeStart("System","Init");
//            ((TextView)findViewById(R.id.status)).setText(R.string.init_object);
            //Загрузка GoogleMap.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
            //Компонент управления слоями
            ViewGroup lay = (ViewGroup) findViewById(R.id.windowLayout);
            UIControler.setWindowLayout(lay);
            UIControler.setButtonLayout((ButtonLayout) findViewById(R.id.buttonLayout));
            lay.removeAllViews();
            lay = (ViewGroup) findViewById(R.id.alertLayout);
            UIControler.setAlertLayout(lay);
            UIControler.setActionLayout((ActionView) findViewById(R.id.actionView));
            lay.removeAllViews();
            GameSound.setVolumeControlStream(self);
            messages=new MessageMap(getApplicationContext());

            ((ActionView)findViewById(R.id.actionView)).init();
            ((ButtonLayout)findViewById(R.id.buttonLayout)).init();


            UIControler.getWindowLayout().removeAllViews();
            ((ViewGroup) findViewById(R.id.alertLayout)).removeAllViews();
            onTrueResume();
            ready=true;
            isActive=true;
            threadFastScan=new Thread(fastScanRequest);
            threadFastScan.start();
            Thread messageThread=new Thread(messageRequest);
            messageThread.start();
            GATracker.trackTimeEnd("System","Init");
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("ProcedureCall","onMapReady");
        try{

        Point size=new Point();
        MyGoogleMap.init(googleMap, size.y);
        Player.getPlayer().setMap(MyGoogleMap.getMap());
        createListeners();
        }
        catch (Exception e){
            GATracker.trackException("LoadPlayer",e);
        }
    }
    boolean isListenersDone=false;
    /**
     * Setup map view
     */

    private void createListeners() {
        Log.d("ProcedureCall","createListener");
        if (isListenersDone) return;
        serverConnect.getInstance().addListener(new ServerListener() {
            @Override
            public void onResponse(int TYPE, JSONObject response) {
                if (TYPE==REFRESH){
                    SendedRequest = 0;
                    UIControler.getButtonLayout().hideConnectImage();
                } else if (TYPE==PLAYER){
                    try {
                        Player.getPlayer().loadJSON(response);
                        if (Player.getPlayer().getRace() < 1 || Player.getPlayer().getRace() > 3) {
                            messages.clear();
                            Essages.clear();
                            new ChooseFaction(getApplicationContext()).show();
                        }
                        SharedPreferences sp = getApplicationContext().getSharedPreferences("player", Context.MODE_PRIVATE);
                        SharedPreferences.Editor ed = sp.edit();
                        try {
                            ed.putString("player", Player.getPlayer().getJSON().toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ed.apply();

                        timeToPlayerRefresh = 6;
                    } catch (Exception e){
                        GATracker.trackException("LoadPlayer",e);
                        Essages.addEssage(e.toString());
                    }
                } else if (TYPE==MESSAGE){
                    if (response.has("Messages")) {
                        try {
                            messages.loadJSON(response);
                        } catch (Exception e){
                            GATracker.trackException("LoadMessage",e);
                        }
                    }
                }
            }

            @Override
            public void onError(int TYPE, JSONObject response) {
                //TODO Источник ошибки
                try {
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
                    GATracker.trackException("NetworkError",errorMsg);
                } catch (Exception e){
                    GATracker.trackException("NetworkError",e);
                }
            }


        });
        View touchView = findViewById(R.id.touchView);
        touchView.setOnTouchListener(new View.OnTouchListener() {
            long tm = -1;
            Point oldPos;
            //float oldBearing=0;
            Point f1;
            Point f2;
            int firstId;
            int secondId;
            boolean closeCity = true;
            //TODO: Какая то жесть надо привести в аккуратный вид.
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
                                if (o != null && o.getMarker() != null) {
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
                            } else if (Player.getPlayer() != null & Player.getPlayer().getMarker() != null) {
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
                            GATracker.trackException("MainWindow.Touch",e);
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
                    GATracker.trackException("MainWindow.Touch",e);
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
        isListenersDone=true;
    }

    /**
     * Tick Клиента
     */
    private void StartTickTimer() {
        Log.d("ProcedureCall","StartTickTimer");
        int delay = 1000;
        try {
            if (serverConnect.getInstance().isLogin() && (timeToPlayerRefresh != -1) && GPSInfo.getInstance().GetLat() != -1 && GPSInfo.getInstance().GetLng() != -1) {
                if (GPSInfo.getInstance().getSpeed() < 30) delay = 40000;
                else if (GPSInfo.getInstance().getSpeed() > 30) delay = 20000;
            }
        }
        catch (Exception e){
            GATracker.trackException("Timer",e);
        }
        MainThread.postDelayed(myRunable, delay);

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
        Log.d("ProcedureCall","Tick");
        if (ready && isActive) {
            try {
                if (serverConnect.getInstance().isLogin() && this.hasWindowFocus()
                        && GPSInfo.getInstance().GetLat() != -1 && GPSInfo.getInstance().GetLng() != -1)
                    if (timeToPlayerRefresh < 1) {
                        serverConnect.getInstance().callGetPlayerInfo();
                        serverConnect.getInstance().callScanRange();
                        serverConnect.getInstance().callFastScan();


                        timeToPlayerRefresh = 6;
                    } else {
                        SendedRequest++;
                        if (SendedRequest > 1) UIControler.getButtonLayout().showConnectImage();
                        timeToPlayerRefresh--;
                        serverConnect.getInstance().callScanRange();
                    }
            } catch (Exception e) {
                GATracker.trackException("Timer", e);
            }
        }
        StartTickTimer();
    }
    boolean run=true;
    private Runnable messageRequest=new Runnable() {
        @Override
        public void run() {
            while (run && !threadFastScan.isInterrupted()) {
                if (ready) {
                    try {
                        if (serverConnect.getInstance().isLogin())
                            serverConnect.getInstance().callGetMessage();
                    } catch (Exception e) {
                        GATracker.trackException("Timer", e);
                    }
                }
                synchronized (this) {
                    try {
                        wait(60000);
                    } catch (InterruptedException e) {
                        GATracker.trackException("Thread.Message",e.toString());
                    }
                }

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        run=false;
    }

    Thread threadFastScan;
    boolean isActive=false;
    //TODO Гасить поток
    private LatLng lastFastScan=null;
    private Runnable fastScanRequest=new Runnable() {
        @Override
        public void run() {
            while (run && !threadFastScan.isInterrupted()) {
                if (isActive) {
                    if (serverConnect.getInstance().isLogin())
                        if (lastFastScan==null ||GPSInfo.getDistance(lastFastScan,GPSInfo.getInstance().getLatLng())>20){
                            serverConnect.getInstance().callFastScan();
                            lastFastScan=GPSInfo.getInstance().getLatLng();
                        }

                    //Log.d("TestThread","Run FastScan");
                }

                synchronized (this) {
                    try {
                        wait(100);
                    } catch (InterruptedException e) {
                        GATracker.trackException("Thread.Message",e.toString());
                    }
                }
            }
        }
    };




    @Override
    protected void onStop() {
        GATracker.trackTimeEnd("System","AppActive");
        super.onStop();
        try {

            if (ready) {
                isActive=false;
                MainThread.removeCallbacks(myRunable);

                MessageNotification.appActive = false;
                GameSound.stopMusic();
                if (!"Y".equals(GameSettings.getInstance().get("GPS_ON_BACK")))
                    GPSInfo.getInstance().offGPS();
            }
        } catch (Exception e){
            GATracker.trackException("onStop",e);

        }
    }

    @Override
    protected void onRestart() {
        Log.d("ProcedureCall","onRestart");
        super.onRestart();
        if (ready) {
            isActive=true;
            try {
                onTrueResume();
            } catch (Exception e) {
                GATracker.trackException("onRestart",e);
            }
        }
    }



    private void onTrueResume(){
        Log.d("ProcedureCall","onTrueResume");
        MessageNotification.cancel();
        MessageNotification.appActive = true;
        StartTickTimer();
        GameSound.playMusic();
        GPSInfo.getInstance().onGPS();
        GATracker.trackTimeStart("System","AppActive");
    }
    @Override
    public void onBackPressed() {
        Log.d("ProcedureCall","onBackPressed");
        try {
            if (UIControler.getWindowLayout().getChildCount() > 0 && serverConnect.getInstance().isLogin() && Player.getPlayer().getRace()!=0) {
                UIControler.getWindowLayout().removeAllViews();
            } else if (findViewById(R.id.actionView).getVisibility() == View.VISIBLE) {
                ((ActionView) findViewById(R.id.actionView)).HideView();
            } else if (!MyGoogleMap.isMoveFixed()) {
                MyGoogleMap.stopShowPoint();
            } else {
                Essages.addEssage("Для выхода из приложения используйте кнопку Home.");
                GATracker.trackHit("System","BackPressed");
            }
        } catch (Exception e)
        {
            GATracker.trackException("BackPressed",e);
        }
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 31){
            initGPS();
        }

    }



}
