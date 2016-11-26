package com.coe.c0r0vans.UIElements;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coe.c0r0vans.GameObject.GameObject;
import com.coe.c0r0vans.Logic.Ambush;
import com.coe.c0r0vans.Logic.Caravan;
import com.coe.c0r0vans.Logic.City;
import com.coe.c0r0vans.Logic.Tower;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.Singles.GameObjects;
import com.coe.c0r0vans.Singles.MyGoogleMap;

import java.util.ArrayList;

/**
 * DebugInfo form
 */

public final class DebugInfo extends LinearLayout {
    public DebugInfo(Context context) {
        super(context);
        init();
    }

    public DebugInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DebugInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.debug_info,this);

        generateDebug();
    }
    private void generateDebug(){
        TextView t= (TextView) findViewById(R.id.info);
        t.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
        String res="";
        res+="GameObjects:"+ GameObjects.getInstance().size()+"\n";
        ArrayList<GameObject> list=new ArrayList<>(GameObjects.getInstance().values());
        int city=0;
        int ambush=0;
        int ambushV=0;
        int caravans=0;
        int caravansV=0;
        int towers=0;
        for (GameObject o:list){
            if (o instanceof City) city++;
            else if (o instanceof Ambush) {
                if (o.getMarker()!=null && o.getMarker().isVisible()) ambushV++;
                ambush++;
            }
            else if (o instanceof Caravan) {
                if (o.getMarker()!=null && o.getMarker().isVisible()) caravansV++;
                caravans++;
            }
            else if (o instanceof Tower) towers++;
        }
        res+="City:"+ city+"\n";
        res+="Ambush:"+ ambushV+"/"+ambush+"\n";
        res+="Caravan:"+ caravansV+"/"+caravans+"\n";
        res+="Tower:"+ towers+"\n";
        res+="Chests:"+ GameObjects.getChests().size()+"\n";
        res+="***\n";
        res+="Player Ambushes:"+ GameObjects.getPlayer().getAmbushes().size()+"\n";
        res+="Player Routes:"+ GameObjects.getPlayer().getRoutes().size()+"\n";

        t.setText(res);

    }
    private void close(){
        if (UIControler.getAlertLayout()==null) return;
        UIControler.getAlertLayout().removeAllViews();

    }
    public void show(){
        if (UIControler.getAlertLayout()==null) return;
        UIControler.getAlertLayout().removeAllViews();
        UIControler.getAlertLayout().addView(this);

    }
}
