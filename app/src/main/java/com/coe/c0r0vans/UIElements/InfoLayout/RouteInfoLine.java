package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coe.c0r0vans.Logic.Caravan;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.Singles.MyGoogleMap;
import com.google.android.gms.maps.model.LatLng;

import utility.GATracker;
import utility.StringUtils;


/**
 * Строка информации о городе в Атласе
 */
public class RouteInfoLine extends RelativeLayout {
    private TextView startCityView;
    private TextView endCityView;
    private TextView lengthView;
    private ImageButton showButton;

    private LatLng point;
    private ShowHideForm parentForm;
    private LatLng startCity;
    private LatLng endCity;

    public void setParentForm(ShowHideForm form){
        parentForm=form;
    }
    public void setData(Caravan r){
        if (r==null) return;
        point=r.getPosition();

        startCity=r.getStartPoint();
        endCity=r.getFinishPoint();
        if (endCity==null && startCity!=null) point=startCity;

        if (lengthView!=null)
            if (r.getDistance()>0) lengthView.setText(String.format(getContext().getString(R.string.route_info_line), r.getDistance(), StringUtils.intToStr(r.getProfit()),StringUtils.intToStr(r.getTime())));
            else lengthView.setText("↝");
        if (startCityView!=null) startCityView.setText(r.getStartName());
        if (endCityView!=null)
            if (!"null".equals(r.getFinishName())) endCityView.setText(r.getFinishName());
            else endCityView.setVisibility(GONE);
        if (point!=null && showButton!=null) showButton.setVisibility(VISIBLE);
        else if (showButton != null) {
            showButton.setVisibility(INVISIBLE);
        }

    }

    public RouteInfoLine(Context context) {
        super(context);
        init();
    }

    public RouteInfoLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RouteInfoLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.route_line, this);
        try {
            afterInit();
        } catch (Exception e){
            GATracker.trackException("CityLine","Initialization error");
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
