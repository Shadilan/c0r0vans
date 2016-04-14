package coe.com.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import coe.com.c0r0vans.GameObjects.AmbushItem;
import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.R;
import coe.com.c0r0vans.ShowHideForm;
import coe.com.c0r0vans.UIElements.InfoLine;

/**
 * Информация о засадах
 */
public class AmbushInfo extends LinearLayout implements PlayerInfoLayout {
    LinearLayout ambushInfo;
    ShowHideForm parent;
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
        inflate(getContext(), R.layout.info_route,this);
        ambushInfo= (LinearLayout) findViewById(R.id.routeInfo);
    }

    @Override
    public void update() {

        ambushInfo.removeAllViews();
        for (AmbushItem r: Player.getPlayer().getAmbushes()){
            InfoLine line=new InfoLine(getContext());
            //float[] distances = new float[1];

            line.setLabelText(r.getName());
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
