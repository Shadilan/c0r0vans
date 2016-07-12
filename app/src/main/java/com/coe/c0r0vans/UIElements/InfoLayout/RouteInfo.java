package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coe.c0r0vans.Logic.Caravan;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.Singles.GameObjects;
import com.coe.c0r0vans.UIElements.CityLine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import utility.GATracker;
import utility.settings.GameSettings;

/**
 * Список маршрутов
 */
public class RouteInfo extends RelativeLayout implements PlayerInfoLayout {
    LinearLayout routeInfo;
    int page=0;
    int pageSize=50;
    int max_page=1;
    ArrayList<Caravan> routes;
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
        inflate(getContext(), R.layout.info_route, this);
        routeInfo= (LinearLayout) findViewById(R.id.routeInfo);
        Button btn= (Button) findViewById(R.id.previous);
        String spage=GameSettings.getInstance().get("RoutePage");
        if (spage==null) page=0; else {
            try {
                page = Integer.parseInt(spage);
            } catch (Exception e) {
                page = 0;
            }
        }
        max_page=Math.abs((GameObjects.getPlayer().getRoutes().size()-1)/pageSize)+1;
        if (page+1>max_page) page=GameObjects.getPlayer().getRoutes().size()/pageSize;
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                page--;
                update();
            }
        });
        if (page<1) {
            btn.setClickable(false);
            btn.setEnabled(false);
        } else
        {
            btn.setClickable(true);
            btn.setEnabled(true);
        }
        btn= (Button) findViewById(R.id.next);
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                page++;
                update();
            }
        });

        if (page+2>max_page) {
            btn.setClickable(false);
            btn.setEnabled(false);
        } else
        {
            btn.setClickable(true);
            btn.setEnabled(true);
        }

        TextView tv= (TextView) findViewById(R.id.pageNumber);
        tv.setText(String.valueOf(page));

    }
    @Override
    public void update() {
        GATracker.trackTimeStart("InfoLayout","RoutesUpdate");

        routeInfo=(LinearLayout) findViewById(R.id.routeInfo);
        routes=new ArrayList(GameObjects.getPlayer().getRoutes().values());
        Collections.sort(routes, new Comparator<Caravan>() {
            @Override
            public int compare(Caravan lhs, Caravan rhs) {

                return lhs.getDistance() - rhs.getDistance();
            }
        });
                routeInfo.removeAllViews();
        if (!"".equals(GameObjects.getPlayer().getCurrentRouteGUID())){
            CityLine line=new CityLine(getContext());

            routeInfo.addView(line);
            line.setData(GameObjects.getPlayer().getCurrentR());
            //line.setOnRemoveClick(GameObjects.getPlayer().getDropRoute());
            line.setTarget("");
        }

        int i=0;
        for (Caravan r:routes){
            if (i>=page*pageSize && i<(page+1)*pageSize) {
                CityLine line = new CityLine(getContext());
                routeInfo.addView(line);
                line.setData(r);
                line.setParentForm(parent);
                //line.setOnRemoveClick(r.getAction(true));
                line.setTarget(r.getGUID());
            } else if (i>(page+1)*pageSize) break;
            i++;
        }

        max_page=Math.abs((GameObjects.getPlayer().getRoutes().size()-2)/pageSize)+1;
        if (page<1) {
            Button btn= (Button) findViewById(R.id.previous);
            btn.setClickable(false);
            btn.setEnabled(false);
        } else
        {
            Button btn= (Button) findViewById(R.id.previous);
            btn.setClickable(true);
            btn.setEnabled(true);
        }
        if (page+2>max_page) {
            Button btn= (Button) findViewById(R.id.next);
            btn.setClickable(false);
            btn.setEnabled(false);
        } else
        {
            Button btn= (Button) findViewById(R.id.next);
            btn.setClickable(true);
            btn.setEnabled(true);
        }
        TextView tv= (TextView) findViewById(R.id.pageNumber);

        tv.setText(String.format(getContext().getString(R.string.pages), page + 1, max_page));
        GameSettings.getInstance().put("RoutePage", String.valueOf(page));
        //GameSettings.getInstance().save();
        GATracker.trackTimeStart("InfoLayout","RoutesUpdate ");

    }

    @Override
    public void setParent(ShowHideForm parent) {
        this.parent=parent;
    }
}
