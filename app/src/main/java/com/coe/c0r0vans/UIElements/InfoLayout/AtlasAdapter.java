package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.coe.c0r0vans.Logic.City;
import com.coe.c0r0vans.ShowHideForm;

import java.util.ArrayList;

/**
 * Created by Shadilan on 09.09.2016.
 */
public class AtlasAdapter extends BaseAdapter {
    ArrayList<City> list;
    ShowHideForm parent;
    Context context;
    public AtlasAdapter(Context context,ArrayList<City> list,ShowHideForm parent){
        this.context=context;
        this.list=list;
        this.parent=parent;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        AtlasLine l=new AtlasLine(context,list.get(i));
        l.setParentForm(parent);
        return l;
    }
}
