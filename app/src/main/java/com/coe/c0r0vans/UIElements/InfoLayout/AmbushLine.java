package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coe.c0r0vans.Logic.Ambush;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.Singles.GameObjects;
import com.coe.c0r0vans.Singles.MyGoogleMap;
import com.coe.c0r0vans.UIElements.ConfirmWindow;
import com.google.android.gms.maps.model.LatLng;

import utility.GATracker;
import utility.GPSInfo;
import utility.internet.serverConnect;


/**
 * Информационная строка для засад
 */
public class AmbushLine extends RelativeLayout {
    private final Ambush ambush;

    private ImageButton removeButton;

    private ImageButton showButton;
    private LatLng point;
    private ShowHideForm parentForm;

    public void setPoint(LatLng point){
        this.point=point;
        showButton.setVisibility(VISIBLE);
    }

    public AmbushLine(Context context, Ambush ambush,ShowHideForm parentForm) {
        super(context);
        this.ambush=ambush;
        this.parentForm=parentForm;
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.ambush_line, this);
        try {
            afterInit();
        } catch (Exception e){
            GATracker.trackException("InfoLine",e);
        }

    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        afterInit();
    }

    private void afterInit() {

        TextView labelText= (TextView) findViewById(R.id.infoLineText);
        labelText.setText(ambush.getName());
        labelText= (TextView) findViewById(R.id.infoLineText2);
        String t="";
        if (ambush.getReady()<0) t="⌛";
        else t="";

        labelText.setText((int) GPSInfo.getDistance(GameObjects.getPlayer().getPosition(), ambush.getPosition())+
                "m " +t+ (ambush.getLife())+"⚔");
        removeButton= (ImageButton) findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmWindow confirmWindow=new ConfirmWindow(getContext());
                confirmWindow.setText("Распустить засаду?");
                confirmWindow.setConfirmAction(new Runnable() {
                    @Override
                    public void run() {
                        serverConnect.getInstance().callCanelAmbush(ambush.getRemoveAction(), ambush.getGUID());
                        removeButton.setVisibility(INVISIBLE);
                        //self.setVisibility(GONE);

                    }
                });
                confirmWindow.show();
            }
        });
        showButton= (ImageButton) findViewById(R.id.showButton);
        showButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ambush.getPosition()!=null) {
                    MyGoogleMap.showPoint(ambush.getPosition());
                    if (parentForm!=null) parentForm.Hide();
                }
            }
        });

    }
}
