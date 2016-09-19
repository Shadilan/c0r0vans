package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import com.coe.c0r0vans.GameObject.OnGameObjectChange;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.Singles.GameObjects;
import com.coe.c0r0vans.UIElements.UIControler;

import utility.GATracker;
import utility.internet.serverConnect;

/**
 * Информация об игроке
 */
public class InfoLayout extends RelativeLayout implements ShowHideForm {
    LinearLayout informationLayout;
    PlayerInfoLayout current;
    LinearLayout gest;
    ShowHideForm form=this;


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

        informationLayout.removeAllViews();

        informationLayout.addView(l);

        current= (PlayerInfoLayout) l;

        current.setParent(this);

        current.update();

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
        ToggleButton atlas= (ToggleButton) findViewById(R.id.atlasButton);
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
                b = (ToggleButton) findViewById(R.id.atlasButton);
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
                b = (ToggleButton) findViewById(R.id.atlasButton);
                b.setChecked(false);
            }
        });
        routeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                setCurrent(new RouteInfo(getContext(),form));
                ToggleButton b = (ToggleButton) findViewById(R.id.playerInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.upgradeInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.routeInfoButton);
                b.setChecked(true);
                b = (ToggleButton) findViewById(R.id.ambushInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.atlasButton);
                b.setChecked(false);
            }
        });

        ambushInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrent(new AmbushInfo(getContext(),form));

                ToggleButton b = (ToggleButton) findViewById(R.id.playerInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.upgradeInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.routeInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.ambushInfoButton);
                b.setChecked(true);
                b = (ToggleButton) findViewById(R.id.atlasButton);
                b.setChecked(false);
            }
        });
        atlas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrent(new Atlas(getContext(),form));

                ToggleButton b = (ToggleButton) findViewById(R.id.playerInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.upgradeInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.routeInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.ambushInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.atlasButton);
                b.setChecked(true);
            }
        });
        GameObjects.getPlayer().addOnChangeListeners(new OnGameObjectChange() {
            @Override
            public void onChange(int TYPE) {
                loadFromPlayer();
            }
        });
    }

    public void loadFromPlayer() {
        if (current!=null) current.update();
    }

    public void Show(){
        serverConnect.getInstance().callGetPlayerInfo();
        UIControler.getWindowLayout().removeAllViews();
        UIControler.getWindowLayout().addView(this);

    }
    public void Hide(){
        UIControler.getWindowLayout().removeView(this);
    }
}
