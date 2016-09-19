package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import com.coe.c0r0vans.Logic.Caravan;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.Singles.GameObjects;
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
    ListView routeInfo;

    ArrayList<Caravan> routes; //Список Маршрутов
    ArrayList<Caravan> routesA; //Список в Листе
    ShowHideForm parent; //Parent
    int sort=0;
    public RouteInfo(Context context,ShowHideForm form) {
        super(context);
        this.parent=form;
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
        routeInfo= (ListView) findViewById(R.id.routeInfo);
        // адаптер
        ArrayList<String> lst=new ArrayList<>();
        lst.add(getContext().getString(R.string.lngth));
        lst.add(getContext().getString(R.string.byincome));
        lst.add(getContext().getString(R.string.citydistance));
        lst.add(getContext().getString(R.string.caravandistance));

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, lst);
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
        routes=new ArrayList<>();
        routesA=new ArrayList<>();
        routeInfo.setOnScrollListener(new AbsListView.OnScrollListener() {
            public int totalItemCount;
            public int currentVisibleItemCount;
            public int currentFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                GATracker.trackTimeStart("Interface","Routes.ScrollChange");
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItemCount = totalItemCount;
                final int list_size = routes.size();// Moved  list.size() call out of the loop to local variable list_size
                final int listA_size = routesA.size();// Moved  listA.size() call out of the loop to local variable listA_size
                if (this.currentVisibleItemCount > 0 && this.totalItemCount-5 <= (currentFirstVisibleItem + currentVisibleItemCount) && list_size>listA_size) {
                    GATracker.trackTimeStart("Interface","Routes.AddItemsToList");
                    routesA.addAll(routes.subList(listA_size,Math.min(list_size,listA_size+10)));
                    ((BaseAdapter)routeInfo.getAdapter()).notifyDataSetChanged();
                    GATracker.trackTimeEnd("Interface","Routes.AddItemsToList");
                }
                GATracker.trackTimeEnd("Interface","Routes.ScrollChange");
            }
        });
        RouteAdapter la =new RouteAdapter(getContext(),routesA,parent);
        routeInfo.setAdapter(la);

        ((BaseAdapter)routeInfo.getAdapter()).notifyDataSetChanged();
    }
    @Override
    public void update() {
        GATracker.trackTimeStart("InfoLayout","RoutesUpdate");
        AsyncTask<Void, Void, Void> task=new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                findViewById(R.id.loading).setVisibility(VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                routes = new ArrayList(GameObjects.getPlayer().getRoutes().values());
                switch (sort) {

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
                                LatLng pl = GPSInfo.getInstance().getLatLng();
                                LatLng c1 = lhs.getStartPoint();
                                LatLng c2 = lhs.getFinishPoint();
                                float d1 = Math.min(GPSInfo.getDistance(pl, c1), GPSInfo.getDistance(pl, c2));
                                c1 = rhs.getStartPoint();
                                c2 = rhs.getFinishPoint();
                                float d2 = Math.min(GPSInfo.getDistance(pl, c1), GPSInfo.getDistance(pl, c2));
                                return (int) (d1 - d2);
                            }
                        });
                        break;
                    case 3:
                        Collections.sort(routes, new Comparator<Caravan>() {
                            @Override
                            public int compare(Caravan lhs, Caravan rhs) {
                                LatLng pl = GPSInfo.getInstance().getLatLng();
                                LatLng c1 = lhs.getMarker().getPosition();
                                float d1 = GPSInfo.getDistance(pl, c1);
                                c1 = rhs.getStartPoint();
                                float d2 = GPSInfo.getDistance(pl, c1);
                                return (int) (d1 - d2);
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

                routesA.clear();

                routesA.addAll(routes.subList(0, Math.min(30, routes.size())));

                GATracker.trackTimeStart("InfoLayout", "RoutesUpdate ");
                return null;
            }
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                routeInfo.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        routeInfo.removeOnLayoutChangeListener(this);
                        findViewById(R.id.loading).setVisibility(INVISIBLE);
                        GATracker.trackTimeEnd("Interface","Routes.NotifyData");
                    }
                });
                ((BaseAdapter)routeInfo.getAdapter()).notifyDataSetChanged();
            }
        };
        task.execute();
    }

    @Override
    public void setParent(ShowHideForm parent) {
        this.parent=parent;
    }
}
