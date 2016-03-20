package coe.com.c0r0vans;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import utility.serverConnect;

/**
 * Created by Shadilan on 19.03.2016.
 */
public class ChooseFaction extends RelativeLayout {
    public ChooseFaction(Context context) {
        super(context);
        init();
    }

    public ChooseFaction(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChooseFaction(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private int faction=1;
    private void init(){
        inflate(getContext(), R.layout.choose_faction, this);
        ImageButton btn= (ImageButton) findViewById(R.id.firstFaction);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton btn= (ImageButton) findViewById(R.id.firstFaction);
                btn.setBackgroundColor(Color.BLACK);
                btn= (ImageButton) findViewById(R.id.secondFaction);
                btn.setBackgroundColor(Color.WHITE);
                btn= (ImageButton) findViewById(R.id.thirdFaction);
                btn.setBackgroundColor(Color.WHITE);
                TextView txt= (TextView) findViewById(R.id.factionName);
                txt.setText(R.string.guild_name);
                txt= (TextView) findViewById(R.id.factionDesc);
                txt.setText(R.string.guild_desc);
                faction=1;
            }
        });
        btn= (ImageButton) findViewById(R.id.secondFaction);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton btn= (ImageButton) findViewById(R.id.firstFaction);
                btn.setBackgroundColor(Color.WHITE);
                btn= (ImageButton) findViewById(R.id.secondFaction);
                btn.setBackgroundColor(Color.BLACK);
                btn= (ImageButton) findViewById(R.id.thirdFaction);
                btn.setBackgroundColor(Color.WHITE);
                TextView txt= (TextView) findViewById(R.id.factionName);
                txt.setText(R.string.alliance_name);
                txt= (TextView) findViewById(R.id.factionDesc);
                txt.setText(R.string.alliance_desc);
                faction=2;
            }
        });
        btn= (ImageButton) findViewById(R.id.thirdFaction);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton btn= (ImageButton) findViewById(R.id.firstFaction);
                btn.setBackgroundColor(Color.WHITE);
                btn= (ImageButton) findViewById(R.id.secondFaction);
                btn.setBackgroundColor(Color.WHITE);
                btn= (ImageButton) findViewById(R.id.thirdFaction);
                btn.setBackgroundColor(Color.BLACK);
                TextView txt= (TextView) findViewById(R.id.factionName);
                txt.setText(R.string.legue_name);
                txt= (TextView) findViewById(R.id.factionDesc);
                txt.setText(R.string.legue_text);
                faction=3;
            }
        });
        final Button accept= (Button) findViewById(R.id.acceptFaction);
        accept.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                serverConnect.getInstance().setRace(faction);
                hide();
            }
        });


    }
    boolean showed=false;
    public void show(){
        setVisibility(VISIBLE);
        showed=true;
    }
    public void hide(){
        setVisibility(GONE);
    }


}
