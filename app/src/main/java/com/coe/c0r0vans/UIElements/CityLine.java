package com.coe.c0r0vans.UIElements;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coe.c0r0vans.ConfirmWindow;
import com.coe.c0r0vans.GameObjects.ObjectAction;
import com.coe.c0r0vans.GameObjects.Route;
import com.coe.c0r0vans.MyGoogleMap;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.google.android.gms.maps.model.LatLng;

import utility.GATracker;
import utility.StringUtils;
import utility.internet.serverConnect;


/**
 * Строка информации о городе
 */
public class CityLine extends RelativeLayout {
    private TextView startCityView;
    private TextView endCityView;
    private TextView lengthView;
    private ImageButton removeButton;
    private ObjectAction removeAction;
    private ImageButton showButton;
    private String target;
    private LatLng point;
    private ShowHideForm parentForm;
    private LatLng startCity;
    private LatLng endCity;

    public void setParentForm(ShowHideForm form){
        parentForm=form;
    }
    public void setData(Route r){
        point=r.getPoint();
        startCity=r.getStarPoint();
        endCity=r.getEndPoint();

        if (lengthView!=null)
            if (r.getDistance()>0) lengthView.setText(String.format(getContext().getString(R.string.route_info_line), r.getDistance(), StringUtils.intToStr(r.getProfit()),StringUtils.intToStr(r.getTime())));
            else lengthView.setText("↝");
        if (startCityView!=null) startCityView.setText(r.getStartName());
        if (endCityView!=null)
            if (!r.getFinishName().equals("null")) endCityView.setText(r.getFinishName());
            else endCityView.setVisibility(GONE);
        if (point!=null && showButton!=null) showButton.setVisibility(VISIBLE);

    }

    public void setTarget(String guid){
        target=guid;
    }
    public void setOnRemoveClick(ObjectAction removeAction){
        this.removeAction=removeAction;
    }
    public CityLine(Context context) {
        super(context);
        init();
    }

    public CityLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CityLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.route_line, this);
        try {
            afterInit();
        } catch (Exception e){
            GATracker.trackException("CityLine",e);
        }

    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        afterInit();
    }

    private void afterInit() {
        startCityView= (TextView) findViewById(R.id.startCity);
        endCityView= (TextView) findViewById(R.id.endCity);
        lengthView= (TextView) findViewById(R.id.length);
        startCityView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startCity!=null){
                    MyGoogleMap.showPoint(startCity);
                    if (parentForm!=null) parentForm.Hide();
                }
            }
        });
        endCityView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(endCity!=null){
                    MyGoogleMap.showPoint(endCity);
                    if (parentForm!=null) parentForm.Hide();
                }
            }
        });
        removeButton= (ImageButton) findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmWindow confirmWindow=new ConfirmWindow(getContext());
                if ("".equals(endCityView.getText())) confirmWindow.setText("Отменить неоконченный маршрут?");
                else confirmWindow.setText("Отменить маршрут \""+startCityView.getText() + " - " + endCityView.getText() + "\"?");
                confirmWindow.setConfirmAction(new Runnable() {
                    @Override
                    public void run() {
                        serverConnect.getInstance().ExecCommand(removeAction, target, 0, 0, 0, 0);
                        removeButton.setVisibility(INVISIBLE);
                    }
                });
                confirmWindow.show();

            }
        });
        showButton= (ImageButton) findViewById(R.id.showButton);
        showButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point!=null) {
                    MyGoogleMap.showPoint(point);
                    if (parentForm!=null) parentForm.Hide();
                }
            }
        });
        if (point!=null) showButton.setVisibility(VISIBLE);

    }
}
