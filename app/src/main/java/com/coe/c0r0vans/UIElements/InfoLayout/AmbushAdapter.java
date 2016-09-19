package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.coe.c0r0vans.Logic.Ambush;
import com.coe.c0r0vans.ShowHideForm;

import java.util.ArrayList;

/**
 * Created by Shadilan on 15.09.2016.
 */
public class AmbushAdapter extends BaseAdapter {
    ArrayList<Ambush> list;
    ShowHideForm parent;
    Context context;
    public AmbushAdapter(Context context, ArrayList<Ambush> list, ShowHideForm parent){
        this.context=context;
        this.list=list;
        this.parent=parent;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Ambush getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        AmbushLine l=new AmbushLine(context,list.get(i),parent);
        return l;
    }
}
