package coe.com.c0r0vans;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONObject;

import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.GameObjects.Route;
import coe.com.c0r0vans.GameObjects.SelectedObject;
import coe.com.c0r0vans.GameObjects.Upgrade;
import utility.ServerListener;
import utility.serverConnect;

public class MainActivity extends AppCompatActivity {
    Player player;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        player= (Player) SelectedObject.getInstance().getExecuter();

        final Button playerInfo= (Button) findViewById(R.id.playerInfoButton);
        Button upgradeInfo= (Button) findViewById(R.id.upgradeInfoButton);
        Button routeInfo= (Button) findViewById(R.id.routeInfoButton);

        playerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout t= (LinearLayout) findViewById(R.id.informationLayout);
                t.setVisibility(View.VISIBLE);
                t= (LinearLayout) findViewById(R.id.upgradeLayout);
                t.setVisibility(View.INVISIBLE);
                t= (LinearLayout) findViewById(R.id.routeLayout);
                t.setVisibility(View.INVISIBLE);

                Button b= (Button) findViewById(R.id.playerInfoButton);
                b.setSelected(true);
                b= (Button) findViewById(R.id.upgradeInfoButton);
                b.setSelected(false);
                b= (Button) findViewById(R.id.routeInfoButton);
                b.setSelected(false);
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

                Button b = (Button) findViewById(R.id.playerInfoButton);
                b.setSelected(false);
                b = (Button) findViewById(R.id.upgradeInfoButton);
                b.setSelected(true);
                b = (Button) findViewById(R.id.routeInfoButton);
                b.setSelected(false);
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

                Button b = (Button) findViewById(R.id.playerInfoButton);
                b.setSelected(false);
                b = (Button) findViewById(R.id.upgradeInfoButton);
                b.setSelected(false);
                b = (Button) findViewById(R.id.routeInfoButton);
                b.setSelected(true);
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
        gl.setRowCount(player.getUpgrades().size());
        for (Upgrade u:player.getUpgrades()){
            ImageView iv=new ImageView(getApplicationContext());
            iv.setImageBitmap(u.getImage());
            gl.addView(iv);
            TextView info=new TextView(getApplicationContext());
            info.setSingleLine(false);
            info.setText(u.getDescription());
            info.setTextColor(Color.BLACK);

            gl.addView(info);
        }

        gl=(GridLayout) findViewById(R.id.routeInfo);

        gl.removeAllViews();
        gl.setRowCount(player.getRoutes().size());
        for (Route r:player.getRoutes()){
            TextView info=new TextView(getApplicationContext());
            info.setSingleLine(true);
            info.setText(r.getStartName()+" - "+r.getDistance()+" - "+r.getFinishName());
            info.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            info.setTextSize(15);
            info.setGravity(Gravity.CENTER);
            info.setTextColor(Color.BLACK);

            gl.addView(info);
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        serverConnect.getInstance().getPlayerInfo();

    }
}
