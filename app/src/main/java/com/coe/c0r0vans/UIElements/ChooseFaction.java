package com.coe.c0r0vans.UIElements;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coe.c0r0vans.R;
import com.coe.c0r0vans.Singles.GameObjects;

import utility.internet.serverConnect;
import utility.settings.GameSettings;

/**
 * View to chose faction
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
        if ("Y".equals(GameSettings.getValue("NIGHT_MODE"))) this.setBackgroundResource(R.drawable.layouts_night);
        else  this.setBackgroundResource(R.drawable.layouts);
        ImageButton btn= (ImageButton) findViewById(R.id.firstFaction);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ImageButton btn= (ImageButton) findViewById(R.id.firstFaction);
                btn.setBackgroundResource(R.drawable.factionsel);
                btn= (ImageButton) findViewById(R.id.secondFaction);
                btn.setBackgroundResource(R.drawable.faction);
                btn= (ImageButton) findViewById(R.id.thirdFaction);
                btn.setBackgroundResource(R.drawable.faction);
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
                btn.setBackgroundResource(R.drawable.faction);
                btn= (ImageButton) findViewById(R.id.secondFaction);
                btn.setBackgroundResource(R.drawable.factionsel);
                btn= (ImageButton) findViewById(R.id.thirdFaction);
                btn.setBackgroundResource(R.drawable.faction);
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
                btn.setBackgroundResource(R.drawable.faction);
                btn= (ImageButton) findViewById(R.id.secondFaction);
                btn.setBackgroundResource(R.drawable.faction);
                btn= (ImageButton) findViewById(R.id.thirdFaction);
                btn.setBackgroundResource(R.drawable.factionsel);
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
                serverConnect.getInstance().callSetRace(faction);
                GameObjects.getPlayer().setRace(faction);
                hide();
            }
        });


    }

    private boolean showed=false;
    public void show(){
        UIControler.getWindowLayout().removeAllViews();
        UIControler.getWindowLayout().addView(this);
        showed=true;
    }
    public void hide(){
        UIControler.getWindowLayout().removeView(this);
    }


}
