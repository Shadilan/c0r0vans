package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.coe.c0r0vans.Logic.Caravan;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.Singles.GameObjects;
import com.coe.c0r0vans.UIElements.CityLine;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import utility.GATracker;
import utility.GPSInfo;
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
    int sort=0;
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
        // адаптер
        ArrayList<String> lst=new ArrayList<>();
        lst.add(getContext().getString(R.string.lngth));
        lst.add(getContext().getString(R.string.byincome));
        lst.add(getContext().getString(R.string.citydistance));
        lst.add(getContext().getString(R.string.caravandistance));

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, lst);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt(getContext().getString(R.string.sortby));
        // выделяем элемент
        String s=GameSettings.getValue("RouteSort");
        if (s ==null) sort=0;
        else sort = Integer.valueOf(s);
        spinner.setSelection(sort);
        // устанавливаем обработчик нажатия
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                // показываем позиция нажатого элемента
                sort=position;
                GameSettings.set("RouteSort",String.valueOf(sort));
                update();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
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
        switch (sort){

            case 1:
                Collections.sort(routes, new Comparator<Caravan>() {
                    @Override
                    public int compare(Caravan lhs, Caravan rhs) {

                        return lhs.getProfit() - rhs.getProfit();
                    }
                });
                break;
            case 2:
                Collections.sort(routes, new Comparator<Caravan>() {
                    @Override
                    public int compare(Caravan lhs, Caravan rhs) {
                        LatLng pl= GPSInfo.getInstance().getLatLng();
                        LatLng c1=lhs.getStartPoint();
                        LatLng c2=lhs.getFinishPoint();
                        float d1=Math.min(GPSInfo.getDistance(pl,c1),GPSInfo.getDistance(pl,c2));
                        c1=rhs.getStartPoint();
                        c2=rhs.getFinishPoint();
                        float d2=Math.min(GPSInfo.getDistance(pl,c1),GPSInfo.getDistance(pl,c2));
                        return (int)(d1-d2);
                    }
                });
                break;
            case 3:
                Collections.sort(routes, new Comparator<Caravan>() {
                    @Override
                    public int compare(Caravan lhs, Caravan rhs) {
                        LatLng pl= GPSInfo.getInstance().getLatLng();
                        LatLng c1=lhs.getMarker().getPosition();
                        float d1=GPSInfo.getDistance(pl,c1);
                        c1=rhs.getStartPoint();
                        float d2=GPSInfo.getDistance(pl,c1);
                        return (int)(d1-d2);
                    }
                });
                break;
            default:
                Collections.sort(routes, new Comparator<Caravan>() {
                    @Override
                    public int compare(Caravan lhs, Caravan rhs) {

                        return lhs.getDistance() - rhs.getDistance();
                    }
                });
        }

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
