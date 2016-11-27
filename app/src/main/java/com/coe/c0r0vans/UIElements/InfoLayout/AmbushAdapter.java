package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.coe.c0r0vans.Logic.Ambush;
import com.coe.c0r0vans.ShowHideForm;

import java.util.ArrayList;

/**
 * AmbushAdapter
 */
class AmbushAdapter extends BaseAdapter {
    private ArrayList<Ambush> list;
    private ShowHideForm parent;
    Context context;
    AmbushAdapter(Context context, ArrayList<Ambush> list, ShowHideForm parent){
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
        if (list.size()<i) return new AmbushLine(context,list.get(list.size()-1),parent);
            return new AmbushLine(context,list.get(i),parent);

    }


}
