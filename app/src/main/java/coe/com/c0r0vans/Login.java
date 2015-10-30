package coe.com.c0r0vans;

import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import utility.GPSInfo;
import utility.serverConnect;

public class Login extends AppCompatActivity {
    SharedPreferences sp;
    private TextView LoginField;
    private TextView PasswordField;
    private Button LoginButton;
    private TextView GPSStatus;
    private LocationListener locationListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_main);
        Log.d("LoginView", "Initialization");
        sp = this.getApplicationContext().getSharedPreferences("SpiritProto", AppCompatActivity.MODE_PRIVATE);
        LoginField= (TextView)  this.findViewById(R.id.LoginField);
        LoginField.setText(sp.getString("Login",""));
        PasswordField=(TextView) this.findViewById(R.id.PasswordField);
        PasswordField.setText(sp.getString("Password", ""));
        GPSStatus= (TextView)  this.findViewById(R.id.GPSStatus);
        GPSInfo.getInstance(this.getApplicationContext());
        serverConnect.instance
        LoginButton = (Button) findViewById(R.id.LoginButton);
        LoginButton.setOnClickListener();
        locationListener =new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                GPSStatus.setText("GPS Located");
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
