package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.coe.c0r0vans.Logic.Ambush;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.Singles.GameObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import utility.GATracker;
import utility.GPSInfo;

/**
 * Информация о засадах
 */
public class AmbushInfo extends LinearLayout implements PlayerInfoLayout {
    private ListView ambushInfo;
    private ShowHideForm parent;

     ArrayList<Ambush> list;
    ArrayList<Ambush> lista;

    public AmbushInfo(Context context,ShowHideForm form) {
        super(context);
        this.parent=form;
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
        ambushInfo= (ListView) findViewById(R.id.routeInfo);
        list=new ArrayList<>();
        lista=new ArrayList<>();
        ambushInfo.setOnScrollListener(new AbsListView.OnScrollListener() {
            public int totalItemCount;
            public int currentVisibleItemCount;
            public int currentFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                GATracker.trackTimeStart("Interface","Ambushes.ScrollChange");
                this.currentFirstVisibleItem = firstVisibleItem;
                this.currentVisibleItemCount = visibleItemCount;
                this.totalItemCount = totalItemCount;
                final int list_size = list.size();// Moved  list.size() call out of the loop to local variable list_size
                final int listA_size = lista.size();// Moved  listA.size() call out of the loop to local variable listA_size
                if (this.currentVisibleItemCount > 0 && this.totalItemCount-5 <= (currentFirstVisibleItem + currentVisibleItemCount) && list_size>listA_size) {
                    GATracker.trackTimeStart("Interface","Ambushes.AddItemsToList");
                    lista.addAll(list.subList(listA_size,Math.min(list_size,listA_size+10)));
                    ((BaseAdapter)ambushInfo.getAdapter()).notifyDataSetChanged();
                    GATracker.trackTimeEnd("Interface","Ambushes.AddItemsToList");
                }
                GATracker.trackTimeEnd("Interface","Ambushes.ScrollChange");
            }
        });
        AmbushAdapter la =new AmbushAdapter(getContext(),lista,parent);
        ambushInfo.setAdapter(la);

        ((BaseAdapter)ambushInfo.getAdapter()).notifyDataSetChanged();
    }

    @Override
    public void update() {
        AsyncTask<Void, Void, Void> task=new AsyncTask<Void, Void, Void>() {
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                findViewById(R.id.loading).setVisibility(VISIBLE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    list=new ArrayList<>(GameObjects.getPlayer().getAmbushes().values());
                }catch (Exception e){

                }

        Collections.sort(list, new Comparator<Ambush>() {
            @Override
            public int compare(Ambush lhs, Ambush rhs) {
                return (int) (GPSInfo.getDistance(lhs.getPosition(),GameObjects.getPlayer().getPosition())-
                                        GPSInfo.getDistance(rhs.getPosition(),GameObjects.getPlayer().getPosition()));
            }
        });
        lista.clear();
        lista.addAll(list.subList(0,Math.min(30,list.size())));

                return null;
            }
            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                ambushInfo.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

                    @Override
                    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                        ambushInfo.removeOnLayoutChangeListener(this);
                        findViewById(R.id.loading).setVisibility(INVISIBLE);
                        GATracker.trackTimeEnd("Interface","Ambushes.NotifyData");
                    }
                });
                ((BaseAdapter)ambushInfo.getAdapter()).notifyDataSetChanged();
            }

        };
        task.execute();

    }

    @Override
    public void setParent(ShowHideForm parent) {
        this.parent=parent;
    }
}
