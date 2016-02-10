package coe.com.c0r0vans;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;

import com.google.android.gms.games.Game;

import utility.GameSettings;

public class Settings extends AppCompatActivity {

    Button apply_button;
    Button cancel_button;
    CheckBox ambushRad;
    CheckBox caravanRoute;
    CheckBox cityRad;
    CheckBox soundOn;
    CheckBox musicOn;
    CheckBox netDebug;
    CheckBox useTilt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        apply_button= (Button) findViewById(R.id.apply);
        cancel_button= (Button) findViewById(R.id.cancel);
        ambushRad= (CheckBox) findViewById(R.id.ambushRad);
        caravanRoute= (CheckBox) findViewById(R.id.routeShow);
        cityRad= (CheckBox) findViewById(R.id.cityRad);
        soundOn= (CheckBox) findViewById(R.id.soundOn);
        musicOn=(CheckBox) findViewById(R.id.musicOn);
        netDebug=(CheckBox) findViewById(R.id.netLogOn);
        useTilt=(CheckBox) findViewById(R.id.useTilt);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_CANCELED, resultIntent);
                finish();
            }
        });
        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameSettings.getInstance().put("SHOW_AMBUSH_RADIUS",ambushRad.isChecked()? "Y" : "N");
                GameSettings.getInstance().put("SHOW_CITY_RADIUS", cityRad.isChecked() ? "Y" : "N");
                GameSettings.getInstance().put("SHOW_CARAVAN_ROUTE", caravanRoute.isChecked() ? "Y" : "N");
                GameSettings.getInstance().put("MUSIC_ON", musicOn.isChecked() ? "Y" : "N");
                GameSettings.getInstance().put("SOUND_ON", soundOn.isChecked() ? "Y" : "N");
                GameSettings.getInstance().put("USE_TILT", useTilt.isChecked() ? "Y" : "N");
                GameSettings.getInstance().put("NET_DEBUG", netDebug.isChecked() ? "Y" : "N");
                GameSettings.getInstance().save();
                Intent resultIntent = new Intent();
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        ambushRad.setChecked("Y".equals(GameSettings.getInstance().get("SHOW_AMBUSH_RADIUS")));
        cityRad.setChecked("Y".equals(GameSettings.getInstance().get("SHOW_CITY_RADIUS")));
        caravanRoute.setChecked("Y".equals(GameSettings.getInstance().get("SHOW_CARAVAN_ROUTE")));
        musicOn.setChecked("Y".equals(GameSettings.getInstance().get("MUSIC_ON")));
        soundOn.setChecked("Y".equals(GameSettings.getInstance().get("SOUND_ON")));
        useTilt.setChecked("Y".equals(GameSettings.getInstance().get("USE_TILT")));
        netDebug.setChecked("Y".equals(GameSettings.getInstance().get("NET_DEBUG")));
    }
}
