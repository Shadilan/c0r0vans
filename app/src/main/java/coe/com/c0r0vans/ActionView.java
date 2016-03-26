package coe.com.c0r0vans;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.json.JSONObject;

import coe.com.c0r0vans.GameObjects.Ambush;
import coe.com.c0r0vans.GameObjects.City;
import coe.com.c0r0vans.GameObjects.GameObject;
import coe.com.c0r0vans.GameObjects.GameObjectView;
import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.GameObjects.SelectedObject;
import utility.Essages;
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
    public void init(){
        //inflate(getContext(), R.layout.actions_layout, this);

    }

    public void HideView(){
        SelectedObject.getInstance().hidePoint();
        this.removeAllViews();
    }
    LocationListener locationListener;
    RelativeLayout currentView=null;

    public void ShowView(){
        //Очистить вью
        //Загрузить вью
        //Обновить видимость экшенов
        Log.d("tttt","test");
        this.removeAllViews();
        GameObject target=SelectedObject.getInstance().getTarget();
        if (target instanceof Player){
            currentView=target.getObjectView(getContext());
        } else
        if (target instanceof City)
        {
            currentView=target.getObjectView(getContext());
        } else if (target instanceof Ambush)
        {
            currentView=target.getObjectView(getContext());

        }
        serverConnect.getInstance().addListener(new ServerListener() {
            @Override
            public void onLogin(JSONObject response) {

            }

            @Override
            public void onRefresh(JSONObject response) {

            }

            @Override
            public void onAction(JSONObject response) {
                if (getVisibility() == VISIBLE) {
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


        if (locationListener==null) {
            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    reloadActions();

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
        reloadActions();
        ((GameObjectView)currentView).setContainer(this);
        this.addView(currentView);

        this.requestLayout();
        Log.d("tttt", "test");
    }

    private void reloadActions(){
        try {


            if (currentView != null && currentView instanceof GameObjectView) {
                float[] distances = new float[1];
                Location.distanceBetween(SelectedObject.getInstance().getTarget().getMarker().getPosition().latitude,
                        SelectedObject.getInstance().getTarget().getMarker().getPosition().longitude,
                        Player.getPlayer().getMarker().getPosition().latitude,
                        Player.getPlayer().getMarker().getPosition().longitude, distances);
                boolean inZone = (distances.length > 0 && distances[0] < (Player.getPlayer().getActionDistance()));
                ((GameObjectView) currentView).updateInZone(inZone);
            }
        } catch (Exception e)
        {
            Essages.addEssage("UER:"+e.toString());
        }
    }

}
