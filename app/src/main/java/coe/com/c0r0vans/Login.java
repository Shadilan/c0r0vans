package coe.com.c0r0vans;

import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import utility.GPSInfo;
import utility.serverConnect;

public class Login extends AppCompatActivity {
    SharedPreferences sp;
    private TextView LoginField;
    private TextView PasswordField;
    private Button LoginButton;
    private ImageView GPSStatus;
    private ImageView ConnectStatus;
    private Boolean Connected=false;
    private Boolean Positioned=false;
    private LocationListener locationListener;
    private Response.Listener<JSONObject> LoginListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        Log.d("LoginView", "Initialization");
        sp = this.getApplicationContext().getSharedPreferences("SpiritProto", AppCompatActivity.MODE_PRIVATE);
        LoginField= (TextView)  this.findViewById(R.id.LoginField);
        LoginField.setText(sp.getString("Login", ""));
        PasswordField=(TextView) this.findViewById(R.id.PasswordField);
        PasswordField.setText(sp.getString("Password", ""));
        GPSStatus= (ImageView)  this.findViewById(R.id.imgGPS);
        ConnectStatus= (ImageView)  this.findViewById(R.id.imgConnected);
        GPSInfo.getInstance(this.getApplicationContext());
        serverConnect.getInstance().Connect(getResources().getString(R.string.serveradress), this.getApplicationContext());
        LoginListener=new Response.Listener<JSONObject>(){

            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("LoginView","Response:"+response.toString());
                    String token=response.getString("Token");
                    if (!token.equals(null)) {
                        ConnectStatus.setVisibility(View.VISIBLE);
                        Connected=true;
                    }
                    if (Connected && Positioned)
                    {
                        Intent i = new Intent(getApplicationContext(), MainWindow.class);
                        startActivity(i);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        serverConnect.getInstance().AddLoginListener(LoginListener);
        LoginButton = (Button) findViewById(R.id.LoginButton);
        LoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sp.edit();
                editor.putString("Login", LoginField.getText().toString()) ;
                editor.putString("Password",PasswordField.getText().toString()) ;
                editor.commit();
                serverConnect.getInstance().ExecLogin(LoginField.getText().toString(),PasswordField.getText().toString());
            }
        });
        locationListener =new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                GPSStatus.setVisibility(View.VISIBLE);
                Positioned=true;
                if (Connected && Positioned)
                {
                    Intent i = new Intent(getApplicationContext(), MainWindow.class);
                    startActivity(i);
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
        LoginButton = (Button) this.findViewById(R.id.LoginButton);
    }
}
