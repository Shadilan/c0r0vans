package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coe.c0r0vans.GameObjects.AmbushItem;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.Singles.GameObjects;
import com.coe.c0r0vans.UIElements.InfoLine;

import java.util.Collections;
import java.util.Comparator;

import utility.GPSInfo;

/**
 * Информация о засадах
 */
public class AmbushInfo extends LinearLayout implements PlayerInfoLayout {
    private LinearLayout ambushInfo;
    private ShowHideForm parent;

    public AmbushInfo(Context context) {
        super(context);
        init();
    }

    public AmbushInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AmbushInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        inflate(getContext(), R.layout.info_ambush,this);
        ambushInfo= (LinearLayout) findViewById(R.id.routeInfo);
    }

    @Override
    public void update() {
        Collections.sort(GameObjects.getPlayer().getAmbushes(), new Comparator<AmbushItem>() {
            @Override
            public int compare(AmbushItem lhs, AmbushItem rhs) {
                return (int) (GPSInfo.getDistance(lhs.getLatLng(),GameObjects.getPlayer().getMarker().getPosition())-
                                        GPSInfo.getDistance(rhs.getLatLng(),GameObjects.getPlayer().getMarker().getPosition()));
            }
        });
        ambushInfo.removeAllViews();
        for (AmbushItem r: GameObjects.getPlayer().getAmbushes()){
            InfoLine line=new InfoLine(getContext());
            //float[] distances = new float[1];
            String ready="";
            if (r.getProgress()<0) ready=" ⌛"+Math.abs(r.getProgress()) + " мин.";
            else if (r.getProgress()>0) ready=""+r.getProgress()+" мин.";
            line.setLabelText(String.format(" %s. расстояние: %sм.%s", r.getName(), (int)GPSInfo.getDistance(GameObjects.getPlayer().getMarker().getPosition(), r.getLatLng()),ready));
            line.setOnRemoveClick(r.getAction(true));
            line.setTarget(r.getGUID());
            line.setPoint(r.getPoint());
            line.setParentForm(parent);
            ambushInfo.addView(line);

            TextView info=new TextView(getContext());
            info.setSingleLine(true);

        }

    }

    @Override
    public void setParent(ShowHideForm parent) {
        this.parent=parent;
    }
}
