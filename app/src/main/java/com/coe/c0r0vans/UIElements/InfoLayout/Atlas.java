package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.coe.c0r0vans.GameObject.GameObject;
import com.coe.c0r0vans.Logic.City;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.Singles.GameObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import utility.GATracker;
import utility.GPSInfo;
import utility.settings.GameSettings;

/**
 * Atlas
 */
public class Atlas extends LinearLayout implements PlayerInfoLayout {
    private ListView atlasInfo;
    private ShowHideForm parent;
    private int sort;
    private LinearLayout self=this;
    private ArrayList<City> list;
    public Atlas(Context context,ShowHideForm parentForm) {
        super(context);
        this.parent=parentForm;
        init();
    }

    public Atlas(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public Atlas(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        GATracker.trackTimeStart("Interface","Atlas.Init");
        inflate(getContext(), R.layout.info_atlas,this);
        list=new ArrayList<>();
        ArrayList<String> lst=new ArrayList<>();
        lst.add("Растояние");
        lst.add("Умение");
        lst.add("Уровень");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, lst);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setAdapter(adapter);
        // заголовок
        spinner.setPrompt(getContext().getString(R.string.sortby));
        // выделяем элемент
        String s= GameSettings.getValue("AtlasSort");
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
                GameSettings.set("AtlasSort",String.valueOf(sort));
                findViewById(R.id.loading).setVisibility(VISIBLE);

                update();
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });
        atlasInfo = (ListView) findViewById(R.id.cityInfo);

        AtlasAdapter la =new AtlasAdapter(getContext(),list,parent);
        atlasInfo.setAdapter(la);
        GATracker.trackTimeEnd("Interface","Atlas.Init");
    }

    @Override
    public void update() {
        GATracker.trackTimeStart("Interface","Atlas.Update");

        GATracker.trackTimeStart("Interface","Atlas.AddItems");
        for (GameObject obj:GameObjects.getInstance().values()){
            if (obj instanceof City) {
                boolean find=false;
                for (City c : list) if (c==obj) find=true;
                if (!find) list.add((City) obj);
            }
        }
        GATracker.trackTimeEnd("Interface","Atlas.AddItems");
        GATracker.trackTimeStart("Interface","Atlas.RemoveOldItems");
        ArrayList<City> rem=new ArrayList<>();
        for (City obj:list){
            boolean find=false;
            for (GameObject c : GameObjects.getInstance().values()) if (c==obj) find=true;
            if (!find) rem.add(obj);
        }
        list.removeAll(rem);
        GATracker.trackTimeEnd("Interface","Atlas.RemoveOldItems");
        GATracker.trackTimeStart("Interface","Atlas.Sort");
        switch (sort){
            case 1:
                Collections.sort(list, new Comparator<City>() {
                    @Override
                    public int compare(City lhs, City rhs) {
                        String up=lhs.getUpgrade();
                        int lcost=0;
                        switch (up){
                            case "speed":
                                lcost=100000000;
                                break;
                            case "cargo":
                                lcost=200000000;
                                break;
                            case "bargain":
                                lcost=300000000;
                                break;
                            case "ambushes":
                                lcost=400000000;
                                break;
                            case "set_ambushes":
                                lcost=500000000;
                                break;
                            case "paladin":
                                lcost=600000000;
                                break;
                            case "founder":
                                lcost=700000000;
                                break;
                            default:
                                lcost=0;
                        }
                        lcost+=lhs.getLevel()*1000000;
                        lcost+=lhs.getUpgradeCost()/10000;
                        up=rhs.getUpgrade();
                        int rcost=0;
                        switch (up){
                            case "speed":
                                rcost=100000000;
                                break;
                            case "cargo":
                                rcost=200000000;
                                break;
                            case "bargain":
                                rcost=300000000;
                                break;
                            case "ambushes":
                                rcost=400000000;
                                break;
                            case "set_ambushes":
                                rcost=500000000;
                                break;
                            case "paladin":
                                rcost=600000000;
                                break;
                            case "founder":
                                rcost=700000000;
                                break;
                            default:
                                rcost=0;
                        }
                        rcost+=rhs.getLevel()*1000000;
                        rcost+=rhs.getUpgradeCost()/10000;
                        return rcost-lcost;
                    }
                });
                break;
            case 2:
                Collections.sort(list, new Comparator<City>() {
                    @Override
                    public int compare(City lhs, City rhs) {
                        return rhs.getLevel()-lhs.getLevel();
                    }
                });
                break;
            default:
                Collections.sort(list, new Comparator<City>() {
                    @Override
                    public int compare(City lhs, City rhs) {
                        return (int) (GPSInfo.getDistance(lhs.getMarker().getPosition(),GameObjects.getPlayer().getMarker().getPosition())-
                                GPSInfo.getDistance(rhs.getMarker().getPosition(),GameObjects.getPlayer().getMarker().getPosition()));
                    }
                });
        }
        GATracker.trackTimeEnd("Interface","Atlas.Sort");
        GATracker.trackTimeStart("Interface","Atlas.NotifyData");
        ((BaseAdapter)atlasInfo.getAdapter()).notifyDataSetChanged();
        atlasInfo.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {

            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                atlasInfo.removeOnLayoutChangeListener(this);
                findViewById(R.id.loading).setVisibility(INVISIBLE);
                GATracker.trackTimeEnd("Interface","Atlas.NotifyData");
            }
        });


        GATracker.trackTimeEnd("Interface","Atlas.Update");

    }
    class CreateLine implements Runnable {
        City r;
        ShowHideForm f;
        public CreateLine(City r,ShowHideForm f){
            this.r=r;
            this.f=f;
        }

        @Override
        public void run() {
            AtlasLine line=new AtlasLine(getContext(),r);
            line.setParentForm(f);
            atlasInfo.addView(line);
        }
    }
    @Override
    public void setParent(ShowHideForm parent) {
        this.parent=parent;
    }

}
