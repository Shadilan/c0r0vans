package com.coe.c0r0vans.Singles;

import android.graphics.Color;

import com.coe.c0r0vans.GameObject.ActiveObject;
import com.coe.c0r0vans.GameObject.GameObject;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

/**
 * @author Shadilan
 */
public class SelectedObject {
    private static SelectedObject instance;
    private GameObject target;
    public GameObject getTarget(){
        return target;
    }
    private LatLng point;
    private Circle clickpos;
    private Circle clickPoint;
    public void setPoint(LatLng point){
        this.point=point;
        if (clickpos != null) {
            clickpos.setCenter(point);
        } else {
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(point);
                circleOptions.radius(0);
                circleOptions.strokeColor(Color.RED);
                circleOptions.strokeWidth(5);
                circleOptions.zIndex(200);
                clickpos = MyGoogleMap.getMap().addCircle(circleOptions);
        }

        if (target instanceof ActiveObject)
            clickpos.setRadius(((ActiveObject) target).getRadius());
        else clickpos.setRadius(GameObjects.getPlayer().getAmbushRad());
        if (clickPoint != null) {
            clickPoint.setCenter(point);
        } else {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(point);
            circleOptions.radius(1);
            circleOptions.strokeColor(Color.RED);
            circleOptions.strokeWidth(5);
            circleOptions.zIndex(1);
            clickPoint = MyGoogleMap.getMap().addCircle(circleOptions);
        }
        clickPoint.setVisible(true);
        clickpos.setVisible(true);

    }
    public void hidePoint(){
        if (clickpos!=null) clickpos.setVisible(false);
        if (clickPoint!=null) clickPoint.setVisible(false);
    }
    public LatLng getPoint(){return point;}
    public void setTarget(GameObject target){
        this.target=target;
    }
    public static SelectedObject getInstance(){
        if (instance==null){
            instance=new SelectedObject();
        }
        return instance;
    }

}
