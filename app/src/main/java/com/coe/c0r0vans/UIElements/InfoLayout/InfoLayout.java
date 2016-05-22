package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.UIElements.UIControler;

import java.util.Date;

import utility.GATracker;
import utility.internet.serverConnect;

/**
 * Информация об игроке
 */
public class InfoLayout extends RelativeLayout implements ShowHideForm {
    LinearLayout informationLayout;
    PlayerInfoLayout current;
    public InfoLayout(Context context) {
        super(context);
    }

    public InfoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public InfoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
    public void init(){
        inflate(getContext(), R.layout.info_layout, this);
        try {
            afterInit();
        } catch (Exception e){
            GATracker.trackException("InfoLayout",e);
        }
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        afterInit();
    }
    private void setCurrent(ViewGroup l){
        Log.d("Timing", "!Время:" + (new Date().getTime()));
        informationLayout.removeAllViews();
        Log.d("Timing", "!Время:" + (new Date().getTime()));
        informationLayout.addView(l);
        Log.d("Timing", "!Время:" + (new Date().getTime()));
        current= (PlayerInfoLayout) l;
        Log.d("Timing", "!Время:" + (new Date().getTime()));
        current.setParent(this);
        Log.d("Timing", "!Время:" + (new Date().getTime()));
        current.update();
        Log.d("Timing", "!Время:" + (new Date().getTime()));
    }
    private void afterInit(){
        informationLayout= (LinearLayout) findViewById(R.id.informationLayout);
        Button backButton= (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Hide();
            }
        });
        ToggleButton playerInfo= (ToggleButton) findViewById(R.id.playerInfoButton);
        ToggleButton upgradeInfo= (ToggleButton) findViewById(R.id.upgradeInfoButton);
        ToggleButton routeInfo= (ToggleButton) findViewById(R.id.routeInfoButton);
        ToggleButton ambushInfo= (ToggleButton) findViewById(R.id.ambushInfoButton);

        setCurrent(new MainInfoTable(getContext()));

        playerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrent(new MainInfoTable(getContext()));

                ToggleButton b = (ToggleButton) findViewById(R.id.playerInfoButton);
                b.setChecked(true);
                b = (ToggleButton) findViewById(R.id.upgradeInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.routeInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.ambushInfoButton);
                b.setChecked(false);
            }
        });

        upgradeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrent(new UpgradeInfo(getContext()));

                ToggleButton b = (ToggleButton) findViewById(R.id.playerInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.upgradeInfoButton);
                b.setChecked(true);
                b = (ToggleButton) findViewById(R.id.routeInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.ambushInfoButton);
                b.setChecked(false);
            }
        });
        routeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setCurrent(new RouteInfo(getContext()));
                ToggleButton b = (ToggleButton) findViewById(R.id.playerInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.upgradeInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.routeInfoButton);
                b.setChecked(true);
                b = (ToggleButton) findViewById(R.id.ambushInfoButton);
                b.setChecked(false);
            }
        });

        ambushInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrent(new AmbushInfo(getContext()));

                ToggleButton b = (ToggleButton) findViewById(R.id.playerInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.upgradeInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.routeInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.ambushInfoButton);
                b.setChecked(true);
            }
        });

    }

    public void loadFromPlayer() {
        if (current!=null) current.update();
    }

    public void Show(){
        serverConnect.getInstance().getPlayerInfo();
        UIControler.getWindowLayout().removeAllViews();
        UIControler.getWindowLayout().addView(this);

    }
    public void Hide(){
        UIControler.getWindowLayout().removeView(this);
    }
}
