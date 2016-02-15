package coe.com.c0r0vans;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONObject;

import coe.com.c0r0vans.GameObjects.AmbushItem;
import coe.com.c0r0vans.GameObjects.CommandButton;
import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.GameObjects.Route;
import coe.com.c0r0vans.GameObjects.SelectedObject;
import coe.com.c0r0vans.GameObjects.Upgrade;
import utility.ImageLoader;
import utility.ServerListener;
import utility.serverConnect;

/**
 * Информация об игроке
 */
public class InfoLayout extends RelativeLayout {
    Player player;
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
        inflate(getContext(), R.layout.content_main, this);
    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        afterInit();
    }
    private void afterInit(){
        player= (Player) SelectedObject.getInstance().getExecuter();
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
        serverConnect.getInstance().addListener(new ServerListener() {
            @Override
            public void onLogin(JSONObject response) {

            }

            @Override
            public void onRefresh(JSONObject response) {

            }

            @Override
            public void onAction(JSONObject response) {

            }

            @Override
            public void onPlayerInfo(JSONObject response) {
                loadFromPlayer();
            }

            @Override
            public void onError(JSONObject response) {

            }

            @Override
            public void onMessage(JSONObject response) {

            }
        });
    }

    private void loadFromPlayer(){
        if (player==null) player=(Player) SelectedObject.getInstance().getExecuter();
        ((TextView)findViewById(R.id.levelInfo)).setText(String.valueOf(player.getLevel()));
        ((TextView)findViewById(R.id.expInfo)).setText(String.valueOf(player.getExp()));
        ((TextView)findViewById(R.id.tnlInfo)).setText(String.valueOf(player.getTNL()));
        ((TextView)findViewById(R.id.goldInfo)).setText(String.valueOf(player.getGold()));
        ((TextView)findViewById(R.id.caravanInfo)).setText(String.valueOf(player.getCaravans()));
        ((TextView)findViewById(R.id.ambushLeftInfo)).setText(String.valueOf(player.getAmbushLeft()));
        ((TextView)findViewById(R.id.ambushSetInfo)).setText(String.valueOf(player.getAmbushMax() - player.getAmbushLeft()));
        ((TextView)findViewById(R.id.mostReachIn)).setText(String.valueOf(player.getMostReachIn()) + "км");

        GridLayout gl= (GridLayout) findViewById(R.id.upgradeInfo);
        gl.removeAllViews();
        gl.setRowCount(player.getUpgrades().size() * 2);

        for (Upgrade u:player.getUpgrades()){
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

        gl=(GridLayout) findViewById(R.id.routeInfo);

        gl.removeAllViews();
        gl.setRowCount(player.getRoutes().size() * 2 + 2);
        if (!player.getCurrentRoute().equals("")){
            TextView info=new TextView(getContext());
            info.setSingleLine(true);
            info.setText(String.valueOf(player.getCurrentRoute()) + "↝");
            info.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            info.setTextSize(15);
            info.setGravity(Gravity.CENTER);
            info.setTextColor(Color.BLACK);


            CommandButton remove=new CommandButton(this.getContext(),player.getDropRoute(),"");
            remove.setImageBitmap(ImageLoader.getImage("closebutton"));
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommandButton button = (CommandButton) v;
                    serverConnect.getInstance().ExecCommand(button.getAction(), button.getGuid(), 0, 0, 0, 0);
                    v.setVisibility(View.GONE);
                }
            });
            gl.addView(remove);
            gl.addView(info);

        }
        for (Route r:player.getRoutes()){
            TextView info=new TextView(getContext());
            info.setSingleLine(true);
            info.setText(r.getStartName() + " - " + r.getDistance() + " - " + r.getFinishName());
            info.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            info.setTextSize(15);
            info.setGravity(Gravity.CENTER);
            info.setTextColor(Color.BLACK);


            CommandButton remove=new CommandButton(this.getContext(),r.getAction(),r.getGUID());
            remove.setImageBitmap(ImageLoader.getImage("closebutton"));
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommandButton button = (CommandButton) v;
                    serverConnect.getInstance().ExecCommand(button.getAction(), button.getGuid(), 0, 0, 0, 0);
                    v.setVisibility(View.GONE);
                }
            });
            gl.addView(remove);
            gl.addView(info);
        }
        gl=(GridLayout) findViewById(R.id.ambushInfo);

        gl.removeAllViews();
        gl.setRowCount(player.getAmbushes().size() * 2);
        for (AmbushItem r:player.getAmbushes()){
            TextView info=new TextView(getContext());
            info.setSingleLine(true);
            float[] distances = new float[1];
            LatLng rLatLng=r.getLatLng();
            Location.distanceBetween(player.getMarker().getPosition().latitude, player.getMarker().getPosition().longitude, rLatLng.latitude, rLatLng.longitude, distances);

            info.setText(r.getName());
            info.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            info.setTextSize(15);
            info.setGravity(Gravity.CENTER);
            info.setTextColor(Color.BLACK);


            CommandButton remove=new CommandButton(this.getContext(),r.getAction(),r.getGUID());
            remove.setImageBitmap(ImageLoader.getImage("closebutton"));
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CommandButton  button= (CommandButton) v;
                    serverConnect.getInstance().ExecCommand(button.getAction(),button.getGuid(),0,0,0,0);
                    v.setVisibility(View.GONE);
                }
            });
            gl.addView(remove);
            gl.addView(info);
        }

    }
    public void Show(){
        serverConnect.getInstance().getPlayerInfo();
        setVisibility(VISIBLE);

    }
    public void Hide(){
        setVisibility(GONE);
    }
}
