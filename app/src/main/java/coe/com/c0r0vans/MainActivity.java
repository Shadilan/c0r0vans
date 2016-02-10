package coe.com.c0r0vans;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONObject;

import coe.com.c0r0vans.GameObjects.CommandButton;
import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.GameObjects.Route;
import coe.com.c0r0vans.GameObjects.SelectedObject;
import coe.com.c0r0vans.GameObjects.Upgrade;
import utility.GPSInfo;
import utility.ImageLoader;
import utility.ServerListener;
import utility.serverConnect;

public class MainActivity extends AppCompatActivity {
    Player player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main);


        player= (Player) SelectedObject.getInstance().getExecuter();

        ToggleButton playerInfo= (ToggleButton) findViewById(R.id.playerInfoButton);
        ToggleButton upgradeInfo= (ToggleButton) findViewById(R.id.upgradeInfoButton);
        ToggleButton routeInfo= (ToggleButton) findViewById(R.id.routeInfoButton);

        playerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout t= (LinearLayout) findViewById(R.id.informationLayout);
                t.setVisibility(View.VISIBLE);
                t= (LinearLayout) findViewById(R.id.upgradeLayout);
                t.setVisibility(View.INVISIBLE);
                t= (LinearLayout) findViewById(R.id.routeLayout);
                t.setVisibility(View.INVISIBLE);

                ToggleButton b= (ToggleButton) findViewById(R.id.playerInfoButton);
                b.setChecked(true);
                b= (ToggleButton) findViewById(R.id.upgradeInfoButton);
                b.setChecked(false);
                b= (ToggleButton) findViewById(R.id.routeInfoButton);
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

                ToggleButton b = (ToggleButton) findViewById(R.id.playerInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.upgradeInfoButton);
                b.setChecked(true);
                b = (ToggleButton) findViewById(R.id.routeInfoButton);
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

                ToggleButton b = (ToggleButton) findViewById(R.id.playerInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.upgradeInfoButton);
                b.setChecked(false);
                b = (ToggleButton) findViewById(R.id.routeInfoButton);
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
        });
        loadFromPlayer();
    }
    private void loadFromPlayer(){
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
        gl.setRowCount(player.getUpgrades().size()*2);

        for (Upgrade u:player.getUpgrades()){
            ImageView iv=new ImageView(getApplicationContext());
            Log.d("DebugInfo", "Upg show:" + u.getDescription());
            iv.setImageBitmap(u.getImage());
            gl.addView(iv);
            TextView info=new TextView(getApplicationContext());
            info.setSingleLine(false);
            info.setText(u.getName() + "\n" + u.getDescription());
            info.setTextColor(Color.BLACK);

            gl.addView(info);
        }

        gl=(GridLayout) findViewById(R.id.routeInfo);

        gl.removeAllViews();
        gl.setRowCount(player.getRoutes().size()*2);
        for (Route r:player.getRoutes()){
            TextView info=new TextView(getApplicationContext());
            info.setSingleLine(true);
            info.setText(r.getStartName() + " - " + r.getDistance() + " - " + r.getFinishName());
            info.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            info.setTextSize(15);
            info.setGravity(Gravity.CENTER);
            info.setTextColor(Color.BLACK);


            CommandButton remove=new CommandButton(this.getApplicationContext(),r.getAction(),r.getGUID());
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
    @Override
    protected void onResume() {
        super.onResume();
        serverConnect.getInstance().getPlayerInfo();

    }

}
