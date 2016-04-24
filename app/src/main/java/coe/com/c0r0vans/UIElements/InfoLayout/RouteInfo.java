package coe.com.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Collections;
import java.util.Comparator;

import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.GameObjects.Route;
import coe.com.c0r0vans.R;
import coe.com.c0r0vans.ShowHideForm;
import coe.com.c0r0vans.UIElements.CityLine;
import utility.settings.GameSettings;

/**
 * Список маршрутов
 */
public class RouteInfo extends RelativeLayout implements PlayerInfoLayout {
    LinearLayout routeInfo;
    int page=0;
    int pageSize=15;
    int max_page=1;
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
        Button btn= (Button) findViewById(R.id.previous);
        String spage=GameSettings.getInstance().get("RoutePage");
        if (spage==null) page=0; else {
            try {
                page = Integer.parseInt(spage);
            } catch (Exception e) {
                page = 0;
            }
        }
        max_page=Math.abs((Player.getPlayer().getRoutes().size()-1)/pageSize)+1;
        if (page+1>max_page) page=Player.getPlayer().getRoutes().size()/pageSize;

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

        if (page+1>Player.getPlayer().getRoutes().size()/pageSize) {
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
        routeInfo=(LinearLayout) findViewById(R.id.routeInfo);
        Collections.sort(Player.getPlayer().getRoutes(), new Comparator<Route>() {
                    @Override
                    public int compare(Route lhs, Route rhs) {

                        return lhs.getDistance()-rhs.getDistance();
                    }
                });
                routeInfo.removeAllViews();
        if (!Player.getPlayer().getCurrentRoute().equals("")){
            CityLine line=new CityLine(getContext());

            routeInfo.addView(line);
            line.setData(Player.getPlayer().getCurrentR());
            line.setOnRemoveClick(Player.getPlayer().getDropRoute());
            line.setTarget("");
        }
        int i=0;
        for (Route r:Player.getPlayer().getRoutes()){
            if (i>page*pageSize && i<(page+1)*pageSize) {
                CityLine line = new CityLine(getContext());
                routeInfo.addView(line);
                line.setData(r);
                line.setParentForm(parent);
                line.setOnRemoveClick(r.getAction(true));
                line.setTarget(r.getGUID());
            } else if (i>(page+1)*pageSize) break;
            i++;
        }
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
        if (page+1>Player.getPlayer().getRoutes().size()/pageSize) {
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
        max_page=Math.abs((Player.getPlayer().getRoutes().size()-1)/pageSize)+1;
        tv.setText(String.valueOf(page+1)+"/"+max_page);
        GameSettings.getInstance().put("RoutePage",String.valueOf(page));
        //GameSettings.getInstance().save();

    }

    @Override
    public void setParent(ShowHideForm parent) {
        this.parent=parent;
    }
}
