package utility;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import coe.com.c0r0vans.GameObjects.ObjectAction;
import coe.com.c0r0vans.GameObjects.Route;
import coe.com.c0r0vans.MyGoogleMap;
import coe.com.c0r0vans.R;
import coe.com.c0r0vans.ShowHideForm;


/**
 * Created by Shadilan on 22.02.2016.
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

    private Route route;

    public void setParentForm(ShowHideForm form){
        parentForm=form;
    }
    public void setData(Route r){
        point=r.getPoint();
        startCity=r.getStarPoint();
        endCity=r.getEndPoint();
        route=r;

        if (lengthView!=null && r.getDistance()>0) lengthView.setText(r.getDistance()+" м "); else
            lengthView.setText("↝");
        if (startCityView!=null) startCityView.setText(r.getStartName());
        if (endCityView!=null && !r.getFinishName().equals("null")) endCityView.setText(r.getFinishName());
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
        inflate(getContext(), R.layout.route_line_layout, this);
        try {
            afterInit();
        } catch (Exception e){
            Essages.addEssage(e.toString());
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
                serverConnect.getInstance().ExecCommand(removeAction, target, 0, 0, 0, 0);
                removeButton.setVisibility(INVISIBLE);

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
