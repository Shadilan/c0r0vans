package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.coe.c0r0vans.Logic.Caravan;
import com.coe.c0r0vans.ShowHideForm;

import java.util.ArrayList;

/**
 * Адаптер ListView для отражения информации о маршрутах
 */
class RouteAdapter extends BaseAdapter {
    private ArrayList<Caravan> list;
    private ShowHideForm parent;
    Context context;
    RouteAdapter(Context context, ArrayList<Caravan> list, ShowHideForm parent){
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
        RouteInfoLine l=new RouteInfoLine(context);
        if (i>list.size()) l.setData(getItem(list.size()-1));
        else l.setData(getItem(i));
        l.setParentForm(parent);
        return l;
    }
}
