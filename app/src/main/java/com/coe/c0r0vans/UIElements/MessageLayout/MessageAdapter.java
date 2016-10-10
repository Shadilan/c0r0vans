package com.coe.c0r0vans.UIElements.MessageLayout;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.coe.c0r0vans.GameObjects.Message;
import com.coe.c0r0vans.Logic.City;
import com.coe.c0r0vans.ShowHideForm;

import java.util.ArrayList;

/**
 * Created by Shadilan on 09.10.2016.
 */

public class MessageAdapter extends BaseAdapter {
    ArrayList<Message> list;
    ShowHideForm parent;
    Context context;
    public MessageAdapter(Context context,ArrayList<Message> list,ShowHideForm parent){
        this.context=context;
        this.list=list;
        this.parent=parent;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Message getItem(int position) {
        return list.get(list.size()-1-position);
    }


    @Override
    public long getItemId(int position) {
        return list.size()-1-position;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        EssageLine line=new EssageLine(context);
        line.setText(getItem(position));
        line.setParentForm(this.parent);
        return line;
    }
}
