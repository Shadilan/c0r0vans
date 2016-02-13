package coe.com.c0r0vans;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import utility.GPSInfo;
import utility.ServerListener;
import utility.serverConnect;

/**
 * Created by Shadilan on 13.02.2016.
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

    private void init(){
        inflate(getContext(), R.layout.activity_login_main, this);
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
        serverConnect.getInstance().connect(getResources().getString(R.string.serveradress), this.getContext());
        LoginListener=new ServerListener() {
            @Override
            public void onLogin(JSONObject response) {
                try {
                    Log.d("LoginView", "Response:" + response.toString());
                    //Todo:Change to correct implementation.
                    //String token=response.getString("Token");
                    String token=response.getString("Token");
                    if (!(token ==null)) {
                        ConnectStatus.setVisibility(View.VISIBLE);
                        Connected=true;
                    }
                    else{
                        Log.d("Appplication Errors",response.getString("Error"));
                        Log.d("Appplication Errors", response.getString("Message"));
                    }
                    checkReadyToRun();

                } catch (JSONException e) {
                    e.printStackTrace();
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

            }

            @Override
            public void onMessage(JSONObject response) {

            }
        };
        serverConnect.getInstance().addListener(LoginListener);

        Button loginButton = (Button) findViewById(R.id.LoginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Login", LoginField.getText().toString());
                editor.putString("Password", PasswordField.getText().toString());
                editor.apply();
                Log.d("Debug info","Test login1");
                if (serverConnect.getInstance().ExecLogin(LoginField.getText().toString(), PasswordField.getText().toString()))
                    Log.d("Debug info","Login True");    else Log.d("Debug info","Login False");
            }
        });
        locationListener =new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                GPSStatus.setVisibility(View.VISIBLE);
                Positioned=true;
                checkReadyToRun();
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
        setVisibility(VISIBLE);
    }
    public void hide(){
        setVisibility(GONE);
    }
}
