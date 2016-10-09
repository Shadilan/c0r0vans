package com.coe.c0r0vans.UIElements.MessageLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.UIElements.InfoLayout.MainInfoTable;
import com.coe.c0r0vans.UIElements.UIControler;

import utility.SwipeDetectLayout.SwipeDetectLayout;
import utility.notification.Essages;

/**
 * Created by Shadilan on 09.10.2016.
 */

public class MessageLayout extends SwipeDetectLayout implements ShowHideForm {
    ToggleButton toggleSystem;
    ToggleButton toggleAlert;
    ListView listSystem;
    ListView listAlert;

    public MessageLayout(Context context) {
        super(context);
        init();
    }
    public void refresh(){
        ((BaseAdapter)listAlert.getAdapter()).notifyDataSetChanged();
        ((BaseAdapter)listSystem.getAdapter()).notifyDataSetChanged();
    }
    public MessageLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MessageLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public MessageLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }
    private void init(){
        inflate(getContext(), R.layout.message_layout,this);
        //Создать переключение
        Button backButton= (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Hide();
            }
        });
        toggleSystem= (ToggleButton) findViewById(R.id.toggleSystem);
        toggleAlert= (ToggleButton) findViewById(R.id.toggleAlert);
        listSystem= (ListView) findViewById(R.id.listSystem);
        listAlert= (ListView) findViewById(R.id.listAlert);
        toggleSystem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAlert.setChecked(false);
                toggleSystem.setChecked(true);
                listAlert.setVisibility(GONE);
                listSystem.setVisibility(VISIBLE);
            }
        });
        toggleAlert.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAlert.setChecked(true);
                toggleSystem.setChecked(false);
                listAlert.setVisibility(VISIBLE);
                listSystem.setVisibility(GONE);
            }
        });
        Button clearButton= (Button) findViewById(R.id.clear_button);
        clearButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Essages.clear();
                refresh();
            }
        });
        //Подключить списки.
        MessageAdapter listAdapter=new MessageAdapter(getContext(),Essages.getSystemList(),this);
        listSystem.setAdapter(listAdapter);
        listAdapter=new MessageAdapter(getContext(),Essages.getAlertList(),this);
        listAlert.setAdapter(listAdapter);
    }

    @Override
    public void Show() {
        UIControler.getWindowLayout().removeAllViews();
        UIControler.getWindowLayout().addView(this);
    }

    @Override
    public void Hide() {
        UIControler.getWindowLayout().removeAllViews();

    }
}
