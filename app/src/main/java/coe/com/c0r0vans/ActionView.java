package coe.com.c0r0vans;

import android.content.Context;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.Circle;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import coe.com.c0r0vans.GameObjects.Ambush;
import coe.com.c0r0vans.GameObjects.Caravan;
import coe.com.c0r0vans.GameObjects.City;
import coe.com.c0r0vans.GameObjects.CommandButton;
import coe.com.c0r0vans.GameObjects.ObjectAction;
import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.GameObjects.SelectedObject;
import coe.com.c0r0vans.R;
import utility.GPSInfo;
import utility.ServerListener;
import utility.serverConnect;

/**
 * @author Shadilan
 * Компонент для отображения действий
 */
public class ActionView extends LinearLayout {

    public ActionView(Context context) {
        super(context);
        init();
    }

    public ActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private ImageView ObjectImage;
    private TextView ObjectDesc;
    private LinearLayout ActionList;
    private ArrayList<ObjectAction> actions;
    private ImageButton close;
    public Circle clickpos;

    private TextView    title;
    HorizontalScrollView horizontalScrollView;
    public void init(){
        inflate(getContext(), R.layout.actions_layout, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        title= (TextView) findViewById(R.id.actionTitle);
        close= (ImageButton) findViewById(R.id.closeButton);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HideView();
            }
        });

        ObjectImage= (ImageView) findViewById(R.id.TargetImage);
        ObjectDesc= (TextView) findViewById(R.id.TargetInfo);
        ActionList= (LinearLayout) findViewById(R.id.ActionList);

    }

    public void HideView(){
        if (clickpos!=null){
            clickpos.remove();
            clickpos=null;
        }
        this.setVisibility(GONE);
    }
    LocationListener locationListener;
    public void ShowView(){

        this.setVisibility(VISIBLE);
        if (SelectedObject.getInstance().getTarget() instanceof Player){
            title.setText("");
            ObjectImage.setVisibility(View.INVISIBLE);
            ObjectDesc.setVisibility(View.INVISIBLE);
        } else
        {
            ObjectImage.setVisibility(View.VISIBLE);
            ObjectDesc.setVisibility(View.VISIBLE);
        }
        if (SelectedObject.getInstance().getTarget() instanceof City)
        {
            title.setText(((City) SelectedObject.getInstance().getTarget()).getCityName());
            ObjectDesc.setText(SelectedObject.getInstance().getTarget().getInfo());
        } else if (SelectedObject.getInstance().getTarget() instanceof Ambush)
        {
            title.setText("Засада");
            ObjectDesc.setText(SelectedObject.getInstance().getTarget().getInfo());
        } else if (SelectedObject.getInstance().getTarget() instanceof Caravan)
        {
            title.setText("Караван");
            ObjectDesc.setText(SelectedObject.getInstance().getTarget().getInfo());
        }
        ProgressBar progressBar= (ProgressBar) findViewById(R.id.progressBar);
        if (SelectedObject.getInstance().getTarget() instanceof City) {
            progressBar.setVisibility(VISIBLE);
            progressBar.setProgress(SelectedObject.getInstance().getTarget().getProgress());

        } else
        {
            progressBar.setVisibility(INVISIBLE);
        }


        ObjectImage.setImageBitmap(SelectedObject.getInstance().getTarget().getImage());
        serverConnect.getInstance().addListener(new ServerListener() {
            @Override
            public void onLogin(JSONObject response) {

            }

            @Override
            public void onRefresh(JSONObject response) {

            }

            @Override
            public void onAction(JSONObject response) {
                if (getVisibility()==VISIBLE){
                    reloadActions();
                }
            }

            @Override
            public void onPlayerInfo(JSONObject response) {

            }

            @Override
            public void onError(JSONObject response) {

            }

            @Override
            public void onMessage(JSONObject response) {

            }
        });
        reloadActions();
        float[] distances = new float[1];
        Location.distanceBetween(SelectedObject.getInstance().getTarget().getMarker().getPosition().latitude,
                SelectedObject.getInstance().getTarget().getMarker().getPosition().longitude,
                SelectedObject.getInstance().getExecuter().getMarker().getPosition().latitude,
                SelectedObject.getInstance().getExecuter().getMarker().getPosition().longitude, distances);
        if (distances.length > 0 && distances[0] < ((Player)SelectedObject.getInstance().getExecuter()).getActionDistance()) {
            ActionList.setVisibility(VISIBLE);
        } else {
            ActionList.setVisibility(INVISIBLE);
        }
        if (locationListener==null) {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    float[] distances = new float[1];
                    Location.distanceBetween(SelectedObject.getInstance().getTarget().getMarker().getPosition().latitude,
                            SelectedObject.getInstance().getTarget().getMarker().getPosition().longitude,
                            SelectedObject.getInstance().getExecuter().getMarker().getPosition().latitude,
                            SelectedObject.getInstance().getExecuter().getMarker().getPosition().longitude, distances);
                    if (distances.length > 0 && distances[0] < ((Player)SelectedObject.getInstance().getExecuter()).getActionDistance()) {
                        ActionList.setVisibility(VISIBLE);
                    } else {
                        ActionList.setVisibility(INVISIBLE);
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            };
            GPSInfo.getInstance().AddLocationListener(locationListener);
        }

    }
    private void reloadActions(){
        actions=SelectedObject.getInstance().getTarget().getActions();

        ActionList.removeAllViews();
        for (ObjectAction act:actions){
            CommandButton btn=new CommandButton(this.getContext(),act);
            btn.setImageBitmap(act.getImage());

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverConnect.getInstance().ExecCommand(((CommandButton) v).getAction(), SelectedObject.getInstance().getTarget().getGUID(), GPSInfo.getInstance().GetLat(), GPSInfo.getInstance().GetLng(),(int)(SelectedObject.getInstance().getPoint().latitude*1E6),(int)(SelectedObject.getInstance().getPoint().longitude*1E6));
                    HideView();
                }
            });

            ActionList.addView(btn);
        }
    }

}
