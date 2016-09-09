package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coe.c0r0vans.Logic.City;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.Singles.GameObjects;
import com.coe.c0r0vans.Singles.MyGoogleMap;
import com.google.android.gms.maps.model.LatLng;

import utility.GATracker;
import utility.GPSInfo;
import utility.ImageLoader;

/**
 * Created by Shadilan on 08.09.2016.
 */
public class AtlasLine extends RelativeLayout {
    ShowHideForm parentForm;
    private LatLng point;

    public void setParentForm(ShowHideForm form){
        parentForm=form;
    }
    public AtlasLine(Context context) {
        super(context);
        init();
    }

    public AtlasLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AtlasLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    City city;
    public AtlasLine(Context context,City city) {
        super(context);
        init();
        setCity(city);
    }
    private void init(){
        GATracker.trackTimeStart("Interface","AtlasLineInit");
        inflate(getContext(), R.layout.info_atlas_line,this);
        GATracker.trackTimeEnd("Interface","AtlasLineInit");
    }
    public void setCity(City city){
        GATracker.trackTimeStart("Interface","AtlasCitySet");
        this.city=city;
        ImageView img= (ImageView) findViewById(R.id.skill);
        img.setImageBitmap(ImageLoader.getImage(city.getUpgrade()));
        TextView txt= (TextView) findViewById(R.id.cityName);
        txt.setText(city.getName());
        txt= (TextView) findViewById(R.id.info);
        int dst= (int) GPSInfo.getDistance(GameObjects.getPlayer().getMarker().getPosition(),city.getMarker().getPosition());
        txt.setText(String.format(getContext().getString(R.string.city_indo), dst, city.getUpgradeCost()));
        ImageButton showButton= (ImageButton) findViewById(R.id.showPosButton);
        point=city.getMarker().getPosition();

        showButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point!=null) {
                    MyGoogleMap.showPoint(point);

                    if (parentForm!=null) parentForm.Hide();
                    else Log.d("ParentForm","ParentNULL");
                }
            }
        });
        GATracker.trackTimeEnd("Interface","AtlasCitySet");
    }




}
