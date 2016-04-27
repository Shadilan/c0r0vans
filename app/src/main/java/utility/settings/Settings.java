package utility.settings;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import coe.com.c0r0vans.R;
import coe.com.c0r0vans.UIElements.UIControler;

public class Settings extends RelativeLayout {

    Button apply_button;
    Button cancel_button;
    CheckBox ambushRad;
    CheckBox caravanRoute;
    CheckBox cityRad;
    CheckBox soundOn;
    CheckBox musicOn;
    CheckBox useTilt;
    CheckBox gpsOn;
    SeekBar gpsRate;
    CheckBox autoLogin;
    CheckBox netErrorLog;
    CheckBox usePadding;
    CheckBox trackBearing;
    CheckBox closeWindow;

    public Settings(Context context) {
        super(context);
        init();
    }

    public Settings(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Settings(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    protected void init() {

        inflate(getContext(), R.layout.activity_settings, this);
        apply_button= (Button) findViewById(R.id.apply);
        cancel_button= (Button) findViewById(R.id.cancel);
        ambushRad= (CheckBox) findViewById(R.id.ambushRad);
        caravanRoute= (CheckBox) findViewById(R.id.routeShow);
        cityRad= (CheckBox) findViewById(R.id.cityRad);
        soundOn= (CheckBox) findViewById(R.id.soundOn);
        musicOn=(CheckBox) findViewById(R.id.musicOn);

        useTilt=(CheckBox) findViewById(R.id.useTilt);
        gpsOn=(CheckBox) findViewById(R.id.gpsOn);
        gpsRate=(SeekBar) findViewById(R.id.gpsRate);
        autoLogin=(CheckBox) findViewById(R.id.autoLogin);
        netErrorLog= (CheckBox) findViewById(R.id.netErrorLogOn);
        usePadding= (CheckBox) findViewById(R.id.usePadding);
        trackBearing= (CheckBox) findViewById(R.id.trackBearing);
        closeWindow= (CheckBox) findViewById(R.id.closeWindow);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIControler.getWindowLayout().removeAllViews();
            }
        });
        apply_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GameSettings.set("SHOW_AMBUSH_RADIUS", ambushRad.isChecked() ? "Y" : "N");
                GameSettings.set("SHOW_CITY_RADIUS", cityRad.isChecked() ? "Y" : "N");
                GameSettings.set("SHOW_CARAVAN_ROUTE", caravanRoute.isChecked() ? "Y" : "N");
                GameSettings.set("MUSIC_ON", musicOn.isChecked() ? "Y" : "N");
                GameSettings.set("SOUND_ON", soundOn.isChecked() ? "Y" : "N");
                GameSettings.set("USE_TILT", useTilt.isChecked() ? "Y" : "N");
                GameSettings.set("GPS_ON_BACK", gpsOn.isChecked() ? "Y" : "N");
                GameSettings.set("GPS_RATE", String.valueOf(gpsRate.getProgress() + 1));
                GameSettings.set("AUTO_LOGIN", autoLogin.isChecked() ? "Y" : "N");
                GameSettings.set("SHOW_NETWORK_ERROR", netErrorLog.isChecked() ? "Y" : "N");
                GameSettings.set("VIEW_PADDING", usePadding.isChecked() ? "Y" : "N");
                GameSettings.set("TRACK_BEARING", trackBearing.isChecked() ? "Y" : "N");
                GameSettings.set("CLOSE_WINDOW", closeWindow.isChecked() ? "Y" : "N");
                GameSettings.getInstance().save();
                hide();
            }
        });
        onResume();
    }


    protected void onResume() {

        ambushRad.setChecked("Y".equals(GameSettings.getInstance().get("SHOW_AMBUSH_RADIUS")));
        cityRad.setChecked("Y".equals(GameSettings.getInstance().get("SHOW_CITY_RADIUS")));
        caravanRoute.setChecked("Y".equals(GameSettings.getInstance().get("SHOW_CARAVAN_ROUTE")));
        musicOn.setChecked("Y".equals(GameSettings.getInstance().get("MUSIC_ON")));
        soundOn.setChecked("Y".equals(GameSettings.getInstance().get("SOUND_ON")));
        useTilt.setChecked("Y".equals(GameSettings.getInstance().get("USE_TILT")));
        gpsOn.setChecked("Y".equals(GameSettings.getInstance().get("GPS_ON_BACK")));
        netErrorLog.setChecked("Y".equals(GameSettings.getInstance().get("SHOW_NETWORK_ERROR")));
        usePadding.setChecked("Y".equals(GameSettings.getInstance().get("VIEW_PADDING")));
        trackBearing.setChecked("Y".equals(GameSettings.getInstance().get("TRACK_BEARING")));
        closeWindow.setChecked("Y".equals(GameSettings.getInstance().get("CLOSE_WINDOW")));
        int refreshRate=3;
        if (GameSettings.getInstance().get("GPS_RATE")!=null){
            String strRate=GameSettings.getInstance().get("GPS_RATE");
            refreshRate=Integer.parseInt(strRate)-1;
        }
        gpsRate.setProgress(refreshRate);
        autoLogin.setChecked("Y".equals(GameSettings.getInstance().get("AUTO_LOGIN")));
    }

    public void show() {
        if (UIControler.getWindowLayout() == null) return;
        UIControler.getWindowLayout().removeAllViews();
        UIControler.getWindowLayout().addView(this);
    }
    public void hide(){
        if (UIControler.getWindowLayout() == null) return;
        UIControler.getWindowLayout().removeView(this);
    }
}
