package coe.com.c0r0vans;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

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
        Button playerInfo= (Button) findViewById(R.id.playerInfoButton);
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
    }

}
