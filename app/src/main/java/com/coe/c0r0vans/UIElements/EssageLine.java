package com.coe.c0r0vans.UIElements;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coe.c0r0vans.GameObjects.Message;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.Singles.MyGoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;

import utility.GATracker;
import utility.StringUtils;

/**
 * Строка Сообщения
 */
public class EssageLine extends LinearLayout{
    private TextView text;
    private ImageButton removeButton;
    private ImageButton showButton;
    private String txt="";
    private LatLng point;
    private LinearLayout parentForm;
    private EssageLine current;
    private Message msg;



    public void setParentForm(LinearLayout form){
        parentForm=form; removeButton.setVisibility(VISIBLE);

    }
    public void setText(String text){
        txt= StringUtils.dateToStr(new Date()) + ":" + text;
        if (this.text!=null) this.text.setText(txt);
    }
    public void setText(Date date,String text){
        txt=StringUtils.dateToStr(date) + ":" +text;
        if (this.text!=null) this.text.setText(txt);

    }

    public void setText(Message text){
        point=text.getTarget();
        if (showButton!=null && point!=null) showButton.setVisibility(VISIBLE);
        txt=StringUtils.dateToStr(text.getTime()) + ":" + text.getMessage();
        if (this.text!=null) this.text.setText(txt);
        msg=text;
    }

    public void setPoint(LatLng point){
        this.point=point;
        showButton.setVisibility(VISIBLE);
    }
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

    private void init(){
        inflate(getContext(), R.layout.essage_line, this);
        current=this;
        try {
            afterInit();
        } catch (Exception e){
            e.printStackTrace();
        }


    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        afterInit();
    }

    private void afterInit() {
        text= (TextView) findViewById(R.id.my_text);
        removeButton= (ImageButton) findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    parentForm.removeView(current);
                    if (msg != null) msg.remove();
                } catch (Exception e){
                    GATracker.trackException("RemoveMessage","AfterInit");
                }
            }
        });
        showButton= (ImageButton) findViewById(R.id.showButton);
        showButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point!=null) {
                    MyGoogleMap.showPoint(point);
                }
            }
        });

        if (point!=null) showButton.setVisibility(VISIBLE);
        if (parentForm!=null) {
            removeButton.setVisibility(VISIBLE);
        }


    }
}
