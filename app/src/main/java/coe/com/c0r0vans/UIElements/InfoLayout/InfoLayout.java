package coe.com.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ToggleButton;

import coe.com.c0r0vans.R;
import coe.com.c0r0vans.ShowHideForm;
import coe.com.c0r0vans.UIElements.UIControler;
import utility.internet.serverConnect;

/**
 * Информация об игроке
 */
public class InfoLayout extends RelativeLayout implements ShowHideForm {
    LinearLayout informationLayout;
    PlayerInfoLayout current;
    public InfoLayout(Context context) {
        super(context);
        init();
    }

    public InfoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InfoLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        inflate(getContext(), R.layout.info_layout, this);
        try {
            afterInit();
        } catch (Exception e){
            serverConnect.getInstance().sendDebug(2, e.toString());
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

        setCurrent(new MainInfo(getContext()));

        playerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setCurrent(new MainInfo(getContext()));

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
