package coe.com.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.GameObjects.Route;
import coe.com.c0r0vans.R;
import coe.com.c0r0vans.ShowHideForm;
import coe.com.c0r0vans.UIElements.CityLine;

/**
 * Список маршрутов
 */
public class RouteInfo extends LinearLayout implements PlayerInfoLayout {
    LinearLayout routeInfo;
    ShowHideForm parent;
    public RouteInfo(Context context) {
        super(context);
        init();
    }

    public RouteInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RouteInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        inflate(getContext(), R.layout.info_route,this);
        routeInfo= (LinearLayout) findViewById(R.id.routeInfo);
    }
    @Override
    public void update() {
        routeInfo=(LinearLayout) findViewById(R.id.routeInfo);

        routeInfo.removeAllViews();
        if (!Player.getPlayer().getCurrentRoute().equals("")){
            CityLine line=new CityLine(getContext());

            routeInfo.addView(line);
            line.setData(Player.getPlayer().getCurrentR());
            line.setOnRemoveClick(Player.getPlayer().getDropRoute());
            line.setTarget("");
        }
        for (Route r:Player.getPlayer().getRoutes()){
            CityLine line=new CityLine(getContext());
            routeInfo.addView(line);
            line.setData(r);
            line.setParentForm(parent);
            line.setOnRemoveClick(r.getAction(true));
            line.setTarget(r.getGUID());
        }

    }

    @Override
    public void setParent(ShowHideForm parent) {
        this.parent=parent;
    }
}
