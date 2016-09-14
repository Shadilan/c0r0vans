package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.coe.c0r0vans.Logic.Caravan;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.UIElements.CityLine;

import java.util.ArrayList;

/**
 * Адаптер ListView для отражения информации о маршрутах
 */
public class RouteAdapter extends BaseAdapter {
    ArrayList<Caravan> list;
    ShowHideForm parent;
    Context context;
    public RouteAdapter(Context context,ArrayList<Caravan> list,ShowHideForm parent){
        this.context=context;
        this.list=list;
        this.parent=parent;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Caravan getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        CityLine l=new CityLine(context);
        l.setData(getItem(i));
        l.setParentForm(parent);
        return l;
    }
}
