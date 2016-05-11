package coe.com.c0r0vans;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import org.json.JSONException;
import org.json.JSONObject;

import coe.com.c0r0vans.GameObjects.GameObjects;
import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.R;
import utility.GPSInfo;
import utility.GameSound;
import utility.ImageLoader;
import utility.internet.serverConnect;
import utility.notification.MessageNotification;
import utility.settings.GameSettings;

public class LoadingActivity extends AppCompatActivity {
    Handler handler;
    LoadingActivity self;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        self=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);
        GPSInfo.getInstance(getApplicationContext());
        handler=new Handler();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {
                GameSettings.init(getApplicationContext());
                ImageLoader.Loader(getApplicationContext());
                GameSound.init(getApplicationContext());
                MessageNotification.init(getApplicationContext());
                serverConnect.getInstance().connect(getResources().getString(R.string.serveradress), getApplicationContext());
                Player.instance();
                SharedPreferences sp = getApplicationContext().getSharedPreferences("player", Context.MODE_PRIVATE);
                String pls = sp.getString("player", "");
                if (!"".equals(pls)) {
                    try {
                        Player.getPlayer().loadJSON(new JSONObject(pls));
                    } catch (JSONException e) {
                        Log.d("LoadPlayer", "Error:" + pls);
                    }
                }
                GameObjects.init();
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(self,MainWindow.class));
                    }
                });
            }
        });
        thread.start();
    }

}
