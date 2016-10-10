package com.coe.c0r0vans.UIElements.MessageLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coe.c0r0vans.GameObjects.Message;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.Singles.MyGoogleMap;
import com.google.android.gms.maps.model.LatLng;

import utility.GATracker;
import utility.StringUtils;
import utility.notification.Essages;

/**
 * Строка Сообщения
 */
public class EssageLine extends LinearLayout {
    private TextView text;
    private TextView time;
    private ImageButton showButton;
    private LatLng point;
    private ShowHideForm parentForm;
    private Message msg;

    public EssageLine(Context context) {
        super(context);
        init();

    }

    public EssageLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }


    public EssageLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void hide() {
        setVisibility(INVISIBLE);
    }

    public void show() {
        setVisibility(VISIBLE);
    }

    public void setParentForm(ShowHideForm form) {
        parentForm = form;

    }

    public void setText(Message text) {
        point = text.getTarget();
        if (showButton != null && point != null) showButton.setVisibility(VISIBLE);
        else if (showButton!=null) showButton.setVisibility(GONE);
        if (time != null) time.setText(StringUtils.dateToStr(text.getTime()));
        if (this.text != null) this.text.setText(text.getMessage());
        msg = text;
        setVisibility(VISIBLE);
    }

    private void init() {
        inflate(getContext(), R.layout.essage_line, this);
        try {
            afterInit();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        afterInit();
    }

    private void afterInit() {
        text = (TextView) findViewById(R.id.my_text);
        time = (TextView) findViewById(R.id.my_date);
        ImageButton removeButton = (ImageButton) findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (msg != null) Essages.remove(msg);
                    hide();
                    if (parentForm != null && parentForm instanceof MessageLayout)
                        ((MessageLayout) parentForm).refresh();

                } catch (Exception e) {
                    GATracker.trackException("RemoveMessage", "AfterInit");
                }
            }
        });
        showButton = (ImageButton) findViewById(R.id.showButton);
        showButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point != null) {
                    if (parentForm != null) parentForm.Hide();
                    MyGoogleMap.showPoint(point);
                }
            }
        });
        if (point != null) showButton.setVisibility(VISIBLE);
        removeButton.setVisibility(VISIBLE);


    }
}
