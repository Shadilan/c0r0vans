package com.coe.c0r0vans.UIElements.MessageLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ListView;
import android.widget.ToggleButton;

import com.coe.c0r0vans.GameObjects.Message;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.UIElements.UIControler;

import utility.SwipeDetectLayout.OnSwipeListener;
import utility.SwipeDetectLayout.SwipeDetectLayout;
import utility.notification.Essages;
import utility.notification.OnEssageListener;
import utility.settings.GameSettings;

/**
 * @author Shadilan
 *
 * Created for show messages
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
        if ("Y".equals(GameSettings.getValue("NIGHT_MODE"))) this.setBackgroundResource(R.drawable.layouts_night);
        else  this.setBackgroundResource(R.drawable.layouts);
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
        this.setOnSwipeListener(new OnSwipeListener() {
            @Override
            public void onSwipeRight() {
                HorizontalScrollView tabs= (HorizontalScrollView) findViewById(R.id.Tabs);
                if (toggleAlert.isChecked()) {
                    toggleAlert.setChecked(false);
                    toggleSystem.setChecked(true);
                    listAlert.setVisibility(GONE);
                    listSystem.setVisibility(VISIBLE);
                }

            }

            @Override
            public void onSwipeLeft() {
                if (toggleSystem.isChecked()){
                    toggleAlert.setChecked(true);
                    toggleSystem.setChecked(false);
                    listAlert.setVisibility(VISIBLE);
                    listSystem.setVisibility(GONE);
                }
            }

            @Override
            public void onSwipeUp() {

            }

            @Override
            public void onSwipeDown() {

            }
        });

        Essages.addListener(new OnEssageListener() {
            @Override
            public void onAdd(int type, Message msg) {
                refresh();
            }

            @Override
            public void onClear() {
                refresh();
            }

            @Override
            public void onRemove(Message msg) {
                refresh();
            }
        });
    }

    @Override
    public void Show() {
        UIControler.getWindowLayout().removeAllViews();
        UIControler.getWindowLayout().addView(this);
        this.setBackgroundResource(0);
        if ("Y".equals(GameSettings.getValue("NIGHT_MODE"))) this.setBackgroundResource(R.drawable.layouts_night);
        else  this.setBackgroundResource(R.drawable.layouts);
    }

    @Override
    public void Hide() {
        UIControler.getWindowLayout().removeAllViews();

    }

}
