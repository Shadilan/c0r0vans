package utility.settings;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.coe.c0r0vans.Logic.Player;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.Singles.GameObjects;
import com.coe.c0r0vans.UIElements.AboutWindow;
import com.coe.c0r0vans.UIElements.DebugInfo;
import com.coe.c0r0vans.UIElements.UIControler;

import utility.sign.SignIn;

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
    CheckBox netErrorLog;
    CheckBox usePadding;
    CheckBox trackBearing;
    CheckBox closeWindow;
    CheckBox buildArea;
    CheckBox notifySound;
    CheckBox screenOff;
    CheckBox nightMode;
    CheckBox gpsSmooth;
    private CheckBox vibrateOn;

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

        inflate(getContext(), R.layout.settings_layout, this);
        if ("Y".equals(GameSettings.getValue("NIGHT_MODE"))) this.setBackgroundResource(R.drawable.layouts_night);
        else  this.setBackgroundResource(R.drawable.layouts);
        apply_button= (Button) findViewById(R.id.apply);
        cancel_button= (Button) findViewById(R.id.cancel);
        ambushRad= (CheckBox) findViewById(R.id.ambushRad);
        caravanRoute= (CheckBox) findViewById(R.id.routeShow);
        cityRad= (CheckBox) findViewById(R.id.cityRad);
        buildArea= (CheckBox) findViewById(R.id.buildArea);
        screenOff= (CheckBox) findViewById(R.id.screenOff);
        nightMode= (CheckBox) findViewById(R.id.nightMode);

        soundOn= (CheckBox) findViewById(R.id.soundOn);
        musicOn=(CheckBox) findViewById(R.id.musicOn);
        vibrateOn=(CheckBox) findViewById(R.id.vibrateOn);
        notifySound= (CheckBox) findViewById(R.id.notifySound);

        useTilt=(CheckBox) findViewById(R.id.useTilt);
        gpsOn=(CheckBox) findViewById(R.id.gpsOn);
        gpsSmooth=(CheckBox) findViewById(R.id.gpsSmooth);
        gpsRate=(SeekBar) findViewById(R.id.gpsRate);

        netErrorLog= (CheckBox) findViewById(R.id.netErrorLogOn);
        usePadding= (CheckBox) findViewById(R.id.usePadding);
        trackBearing= (CheckBox) findViewById(R.id.trackBearing);
        closeWindow= (CheckBox) findViewById(R.id.closeWindow);


        SharedPreferences sp=getContext().getSharedPreferences("SpiritProto",Context.MODE_PRIVATE);
        ((TextView)findViewById(R.id.accountName)).setText(sp.getString("AccountName",""));
        findViewById(R.id.exitAccount).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                SignIn.doSignOut();

            }
        });

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
                GameSettings.set("SHOW_BUILD_AREA", buildArea.isChecked() ? "Y" : "N");
                GameSettings.set("SCREEN_OFF", screenOff.isChecked() ? "Y" : "N");
                GameSettings.set("NIGHT_MODE", nightMode.isChecked() ? "Y" : "N");

                GameSettings.set("MUSIC_ON", musicOn.isChecked() ? "Y" : "N");
                GameSettings.set("SOUND_ON", soundOn.isChecked() ? "Y" : "N");
                GameSettings.set("VIBRATE_ON", vibrateOn.isChecked() ? "Y" : "N");
                GameSettings.set("NOTIFY_SOUND", notifySound.isChecked() ? "Y" : "N");

                GameSettings.set("USE_TILT", useTilt.isChecked() ? "Y" : "N");
                GameSettings.set("VIEW_PADDING", usePadding.isChecked() ? "Y" : "N");
                GameSettings.set("CLOSE_WINDOW", closeWindow.isChecked() ? "Y" : "N");

                GameSettings.set("GPS_ON_BACK", gpsOn.isChecked() ? "Y" : "N");
                GameSettings.set("GPS_SMOOTH", gpsSmooth.isChecked() ? "Y" : "N");
                GameSettings.set("GPS_RATE", String.valueOf(5-(gpsRate.getProgress() + 1)));
                GameSettings.set("TRACK_BEARING", trackBearing.isChecked() ? "Y" : "N");

                GameSettings.set("SHOW_NETWORK_ERROR", netErrorLog.isChecked() ? "Y" : "N");

                GameSettings.getInstance().save();
                hide();
            }
        });
        findViewById(R.id.about_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AboutWindow about = new AboutWindow(getContext());
                about.show();
            }
        });
        findViewById(R.id.debug_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                DebugInfo debug = new DebugInfo(getContext());
                debug.show();
            }
        });

        onResume();
    }


    protected void onResume() {
        if ("Shadilan".equals(GameObjects.getPlayer().getName())
                || "Kami".equals(GameObjects.getPlayer().getName())
                || "Zlodiak".equals(GameObjects.getPlayer().getName())
                ) findViewById(R.id.debug_button).setVisibility(VISIBLE);
        else findViewById(R.id.debug_button).setVisibility(INVISIBLE);

        ambushRad.setChecked("Y".equals(GameSettings.getInstance().get("SHOW_AMBUSH_RADIUS")));
        cityRad.setChecked("Y".equals(GameSettings.getInstance().get("SHOW_CITY_RADIUS")));
        caravanRoute.setChecked("Y".equals(GameSettings.getInstance().get("SHOW_CARAVAN_ROUTE")));
        buildArea.setChecked("Y".equals(GameSettings.getInstance().get("SHOW_BUILD_AREA")));
        screenOff.setChecked(!"N".equals(GameSettings.getInstance().get("SCREEN_OFF")));
        nightMode.setChecked("Y".equals(GameSettings.getInstance().get("NIGHT_MODE")));

        musicOn.setChecked("Y".equals(GameSettings.getInstance().get("MUSIC_ON")));
        soundOn.setChecked("Y".equals(GameSettings.getInstance().get("SOUND_ON")));
        vibrateOn.setChecked("Y".equals(GameSettings.getInstance().get("VIBRATE_ON")));
        notifySound.setChecked(!"N".equals(GameSettings.getInstance().get("NOTIFY_SOUND")));

        useTilt.setChecked("Y".equals(GameSettings.getInstance().get("USE_TILT")));
        gpsOn.setChecked("Y".equals(GameSettings.getInstance().get("GPS_ON_BACK")));
        gpsSmooth.setChecked("Y".equals(GameSettings.getInstance().get("GPS_SMOOTH")));

        netErrorLog.setChecked("Y".equals(GameSettings.getInstance().get("SHOW_NETWORK_ERROR")));
        usePadding.setChecked("Y".equals(GameSettings.getInstance().get("VIEW_PADDING")));
        trackBearing.setChecked("Y".equals(GameSettings.getInstance().get("TRACK_BEARING")));
        closeWindow.setChecked("Y".equals(GameSettings.getInstance().get("CLOSE_WINDOW")));

        int refreshRate=3;
        if (GameSettings.getInstance().get("GPS_RATE")!=null){
            String strRate=GameSettings.getInstance().get("GPS_RATE");
            refreshRate=5-(Integer.parseInt(strRate)+1);
        }
        gpsRate.setProgress(refreshRate);
    }

    public void show() {
        if (UIControler.getWindowLayout() == null) return;
        UIControler.getWindowLayout().removeAllViews();
        UIControler.getWindowLayout().addView(this);
        this.setBackgroundResource(0);
        if ("Y".equals(GameSettings.getValue("NIGHT_MODE"))) this.setBackgroundResource(R.drawable.layouts_night);
        else  this.setBackgroundResource(R.drawable.layouts);
    }
    public void hide(){
        if (UIControler.getWindowLayout() == null) return;
        UIControler.getWindowLayout().removeView(this);
    }
}
