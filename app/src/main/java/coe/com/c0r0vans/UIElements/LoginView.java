package coe.com.c0r0vans.UIElements;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import coe.com.c0r0vans.R;
import utility.GPSInfo;
import utility.internet.ServerListener;
import utility.internet.serverConnect;
import utility.settings.GameSettings;

/**
 * View to enter login
 */
public class LoginView extends RelativeLayout {
    SharedPreferences sp;
    private TextView LoginField;
    private TextView PasswordField;
    private ImageView GPSStatus;
    private ImageView ConnectStatus;
    private Boolean Connected=false;
    private Boolean Positioned=false;
    private LocationListener locationListener;
    private ServerListener LoginListener;

    public LoginView(Context context) {
        super(context);
        init();
    }

    public LoginView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LoginView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();

    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        afterInit();
    }
    Button loginButton;
    private void init(){
        inflate(getContext(), R.layout.activity_login_main, this);
        try {
            afterInit();
        } catch (Exception e){
            serverConnect.getInstance().sendDebug(2, e.toString()+ Arrays.toString(e.getStackTrace()));
        }
    }
    private void afterInit(){
        sp = getContext().getSharedPreferences("SpiritProto", AppCompatActivity.MODE_PRIVATE);
        LoginField= (TextView)  this.findViewById(R.id.LoginField);
        LoginField.setText(sp.getString("Login", ""));
        PasswordField=(TextView) this.findViewById(R.id.PasswordField);
        PasswordField.setText(sp.getString("Password", ""));
        GPSStatus= (ImageView)  this.findViewById(R.id.imgGPS);
        ConnectStatus= (ImageView)  this.findViewById(R.id.imgConnected);
        GPSInfo.getInstance(this.getContext());
        LoginListener=new ServerListener() {
            @Override
            public void onLogin(JSONObject response) {
                try {
                    Log.d("LoginView", "Response:" + response.toString());
                    //Todo:Change to correct implementation.
                    //String token=response.getString("Token");
                    String token=null;
                    if (response.has("Token")) token=response.getString("Token");
                    TextView errorText= (TextView) findViewById(R.id.errorText);
                    if (!(token ==null)) {
                        ConnectStatus.setImageResource(R.mipmap.server_connect);
                        Connected=true;
                        loginButton.setVisibility(INVISIBLE);
                        errorText.setText(R.string.LoginComplete);
                    }
                    else{

                        errorText.setText(String.format("%s:%s", response.getString("Error"), response.getString("Message")));
                    }

                    checkReadyToRun();
                    loginButton.setText(R.string.login_button);


                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (Exception e)
                {
                    TextView errorText= (TextView) findViewById(R.id.errorText);
                    errorText.setText(e.toString());
                    serverConnect.getInstance().sendDebug(2, "Login UE:" + e.toString() + "\n" + Arrays.toString(e.getStackTrace()));
                }

            }

            @Override
            public void onRefresh(JSONObject response) {

            }

            @Override
            public void onAction(JSONObject response) {

            }

            @Override
            public void onPlayerInfo(JSONObject response) {

            }

            @Override
            public void onError(JSONObject response) {
                TextView errorText= (TextView) findViewById(R.id.errorText);
                try {

                    String result="";

                    if (response.has("Error")) result=response.getString("Error");
                    if (response.has("Message")) result=result+" "+response.getString("Message");
                    loginButton.setText(R.string.login_button);
                    errorText.setText(result);
                } catch (JSONException e) {
                    errorText.setText(response.toString());

                } catch (Exception e){
                    errorText.setText(e.toString());
                    serverConnect.getInstance().sendDebug(2, "Login UE:" + e.toString() + "\n" + Arrays.toString(e.getStackTrace()));
                }
            }

            @Override
            public void onMessage(JSONObject response) {

            }

            @Override
            public void onRating(JSONObject response) {

            }
        };
        serverConnect.getInstance().addListener(LoginListener);

        loginButton = (Button) findViewById(R.id.LoginButton);
        OnFocusChangeListener focusChangeListener=new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                try {
                    if (!hasFocus) {
                        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                } catch (Exception e)
                {
                    serverConnect.getInstance().sendDebug(2, "Login UE:" + e.toString() + "\n" + Arrays.toString(e.getStackTrace()));
                }
            }
        };
        LoginField.setOnFocusChangeListener(focusChangeListener);
        PasswordField.setOnFocusChangeListener(focusChangeListener);
        loginButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    SharedPreferences.Editor editor = sp.edit();

                    editor.putString("Login", LoginField.getText().toString());
                    editor.putString("Password", PasswordField.getText().toString());

                    editor.apply();
                    TextView errorText = (TextView) findViewById(R.id.errorText);
                    errorText.setText("");
                    loginButton.setText(R.string.login_button_run);

                    if (!serverConnect.getInstance().ExecLogin(LoginField.getText().toString(), PasswordField.getText().toString())) {
                        errorText.setText("Отсутствует подключение к интернету.");
                        loginButton.setText(R.string.login_button);

                    }
                } catch (Exception e)
                {
                    serverConnect.getInstance().sendDebug(2,"Login UE:"+e.toString()+"\n"+ Arrays.toString(e.getStackTrace()));
                }
            }
        });
        //Если настройки не инициализированы инициализируем их.
        if (GameSettings.getInstance()==null) GameSettings.init(getContext());
        try {
            if ("Y".equals(GameSettings.getInstance().get("AUTO_LOGIN")) && !LoginField.getText().equals("")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                    loginButton.callOnClick();
                }
            }
        } catch (Exception e)
        {
            TextView errorText= (TextView) findViewById(R.id.errorText);
            errorText.setText(e.toString());
        }
        locationListener =new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                try {


                    if (GPSInfo.getInstance().GetLat() != -1 && GPSInfo.getInstance().GetLng() != -1) {
                        GPSStatus.setImageResource(R.mipmap.gps_connect);
                        Positioned = true;
                        checkReadyToRun();
                    }
                } catch (Exception e)
                {
                    serverConnect.getInstance().sendDebug(2, "Lovation Get UE:" + e.toString() + "\n" + Arrays.toString(e.getStackTrace()));
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
        };
        GPSInfo.getInstance().AddLocationListener(locationListener);
        findViewById(R.id.about_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutWindow about=new AboutWindow(getContext());
                about.show();
            }
        });
    }

    private void checkReadyToRun(){
        if (Connected && Positioned)
        {
            GPSInfo.getInstance().RemoveLocationListener(locationListener);
            serverConnect.getInstance().removeListener(LoginListener);
            hide();
        }
    }
    public void show(){
        if (UIControler.getWindowLayout()==null) return;
        UIControler.getWindowLayout().removeAllViews();
        UIControler.getWindowLayout().addView(this);
    }
    public void hide(){
        if (UIControler.getWindowLayout()==null) return;
        UIControler.getWindowLayout().removeView(this);
    }
}
