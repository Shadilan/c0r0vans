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
    CheckBox cityRad;
    CheckBox soundOn;
    CheckBox musicOn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        apply_button= (Button) findViewById(R.id.apply);
        cancel_button= (Button) findViewById(R.id.cancel);
        ambushRad= (CheckBox) findViewById(R.id.ambushRad);
        cityRad= (CheckBox) findViewById(R.id.cityRad);
        soundOn= (CheckBox) findViewById(R.id.soundOn);
        musicOn=(CheckBox) findViewById(R.id.musicOn);
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
                GameSettings.getInstance().put("MUSIC_ON", musicOn.isChecked() ? "Y" : "N");
                GameSettings.getInstance().put("SOUND_ON", soundOn.isChecked() ? "Y" : "N");
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
        ambushRad.setChecked(GameSettings.getInstance().get("SHOW_AMBUSH_RADIUS").equals("Y"));
        cityRad.setChecked(GameSettings.getInstance().get("SHOW_CITY_RADIUS").equals("Y"));
        musicOn.setChecked(GameSettings.getInstance().get("MUSIC_ON").equals("Y"));
        soundOn.setChecked(GameSettings.getInstance().get("SOUND_ON").equals("Y"));
    }
}