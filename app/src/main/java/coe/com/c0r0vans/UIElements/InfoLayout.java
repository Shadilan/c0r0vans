package coe.com.c0r0vans.UIElements;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import coe.com.c0r0vans.GameObjects.AmbushItem;
import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.GameObjects.Route;
import coe.com.c0r0vans.GameObjects.Upgrade;
import coe.com.c0r0vans.R;
import coe.com.c0r0vans.ShowHideForm;
import utility.StringUtils;
import utility.internet.serverConnect;
import utility.notification.Essages;

/**
 * Информация об игроке
 */
public class InfoLayout extends RelativeLayout implements ShowHideForm {

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
            Essages.addEssage(e.toString());
        }
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        afterInit();
    }
    private void afterInit(){

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
        playerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout t= (LinearLayout) findViewById(R.id.informationLayout);
                t.setVisibility(View.VISIBLE);
                t= (LinearLayout) findViewById(R.id.upgradeLayout);
                t.setVisibility(View.INVISIBLE);
                t= (LinearLayout) findViewById(R.id.routeLayout);
                t.setVisibility(View.INVISIBLE);
                t = (LinearLayout) findViewById(R.id.ambushLayout);
                t.setVisibility(View.INVISIBLE);

                ToggleButton b= (ToggleButton) findViewById(R.id.playerInfoButton);
                b.setChecked(true);
                b= (ToggleButton) findViewById(R.id.upgradeInfoButton);
                b.setChecked(false);
                b= (ToggleButton) findViewById(R.id.routeInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.ambushInfoButton);
                b.setChecked(false);
            }
        });

        upgradeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout t = (LinearLayout) findViewById(R.id.informationLayout);
                t.setVisibility(View.INVISIBLE);
                t = (LinearLayout) findViewById(R.id.upgradeLayout);
                t.setVisibility(View.VISIBLE);
                t = (LinearLayout) findViewById(R.id.routeLayout);
                t.setVisibility(View.INVISIBLE);
                t = (LinearLayout) findViewById(R.id.ambushLayout);
                t.setVisibility(View.INVISIBLE);

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
                LinearLayout t = (LinearLayout) findViewById(R.id.informationLayout);
                t.setVisibility(View.INVISIBLE);
                t = (LinearLayout) findViewById(R.id.upgradeLayout);
                t.setVisibility(View.INVISIBLE);
                t = (LinearLayout) findViewById(R.id.routeLayout);
                t.setVisibility(View.VISIBLE);
                t = (LinearLayout) findViewById(R.id.ambushLayout);
                t.setVisibility(View.INVISIBLE);

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
                LinearLayout t = (LinearLayout) findViewById(R.id.informationLayout);
                t.setVisibility(View.INVISIBLE);
                t = (LinearLayout) findViewById(R.id.upgradeLayout);
                t.setVisibility(View.INVISIBLE);
                t = (LinearLayout) findViewById(R.id.routeLayout);
                t.setVisibility(View.INVISIBLE);
                t = (LinearLayout) findViewById(R.id.ambushLayout);
                t.setVisibility(View.VISIBLE);

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

    public void loadFromPlayer(){

        ((TextView)findViewById(R.id.levelInfo)).setText(String.valueOf(Player.getPlayer().getLevel()));
        ((TextView)findViewById(R.id.expInfo)).setText(StringUtils.intToStr(Player.getPlayer().getExp()));
        ((TextView)findViewById(R.id.tnlInfo)).setText(StringUtils.intToStr(Player.getPlayer().getTNL()));
        ((TextView)findViewById(R.id.goldInfo)).setText(StringUtils.intToStr(Player.getPlayer().getGold()));
        ((TextView)findViewById(R.id.caravanInfo)).setText(String.valueOf(Player.getPlayer().getCaravans()));
        ((TextView)findViewById(R.id.ambushLeftInfo)).setText(String.valueOf(Player.getPlayer().getAmbushLeft()));
        ((TextView)findViewById(R.id.ambushSetInfo)).setText(String.valueOf(Player.getPlayer().getAmbushMax() - Player.getPlayer().getAmbushLeft()));
        ((TextView)findViewById(R.id.mostReachIn)).setText(String.valueOf(Player.getPlayer().getMostReachIn()) + "км");

        GridLayout gl= (GridLayout) findViewById(R.id.upgradeInfo);
        gl.removeAllViews();

        gl.setRowCount(Player.getPlayer().getUpgrades().size()*2);

        for (Upgrade u:Player.getPlayer().getUpgrades()){
            ImageView iv=new ImageView(getContext());
            Log.d("DebugInfo", "Upg show:" + u.getDescription());
            iv.setImageBitmap(u.getImage());
            gl.addView(iv);
            TextView info=new TextView(getContext());
            info.setSingleLine(false);
            info.setText(u.getName() + "\n" + u.getDescription());
            info.setTextColor(Color.BLACK);

            gl.addView(info);
        }

        LinearLayout ll=(LinearLayout) findViewById(R.id.routeInfo);

        ll.removeAllViews();
        if (!Player.getPlayer().getCurrentRoute().equals("")){
            CityLine line=new CityLine(getContext());

            ll.addView(line);
            line.setData(Player.getPlayer().getCurrentR());
            //line.setLabelText(String.valueOf(Player.getPlayer().getCurrentRoute()) + "↝");

            line.setOnRemoveClick(Player.getPlayer().getDropRoute());
            line.setTarget("");
        }
        for (Route r:Player.getPlayer().getRoutes()){
            CityLine line=new CityLine(getContext());
            ll.addView(line);
            line.setData(r);
            line.setParentForm(this);
            line.setOnRemoveClick(r.getAction(true));
            line.setTarget(r.getGUID());
            //line.setPoint(r.getPoint());

        }
        ll=(LinearLayout) findViewById(R.id.ambushInfo);

        ll.removeAllViews();
        for (AmbushItem r:Player.getPlayer().getAmbushes()){
            InfoLine line=new InfoLine(getContext());
            //float[] distances = new float[1];

            line.setLabelText(r.getName());
            line.setOnRemoveClick(r.getAction(true));
            line.setTarget(r.getGUID());
            line.setPoint(r.getPoint());
            line.setParentForm(this);
            ll.addView(line);

            TextView info=new TextView(getContext());
            info.setSingleLine(true);

        }
        if (Player.getPlayer().getRace()==1){
            ImageView img= (ImageView) findViewById(R.id.factionSymbol);
            img.setImageResource(R.mipmap.guild);
        }else if (Player.getPlayer().getRace()==2){
            ImageView img= (ImageView) findViewById(R.id.factionSymbol);
            img.setImageResource(R.mipmap.alliance);
        }else if (Player.getPlayer().getRace()==3){
            ImageView img= (ImageView) findViewById(R.id.factionSymbol);
            img.setImageResource(R.mipmap.legue);
        }

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
