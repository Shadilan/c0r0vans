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
    private LatLng point;
    private Circle clickpos;
    private Circle clickPoint;

    public static SelectedObject getInstance() {
        if (instance == null) {
            instance = new SelectedObject();
        }
        return instance;
    }

    public GameObject getTarget() {
        return target;
    }

    public void setTarget(GameObject target) {
        this.target = target;
    }

    public void createZone() {
        CircleOptions circleOptions = new CircleOptions()
                .center(point)
                .radius(0)
                .strokeColor(Color.RED)
                .strokeWidth(5)
                .zIndex(200)
                .visible(false);
        clickpos = MyGoogleMap.getMap().addCircle(circleOptions);
        circleOptions = new CircleOptions()
                .center(point)
                .radius(1)
                .strokeColor(Color.RED)
                .strokeWidth(5)
                .zIndex(1)
                .visible(false);
        clickPoint = MyGoogleMap.getMap().addCircle(circleOptions);
    }

    public void hidePoint() {
        if (clickpos != null) clickpos.setVisible(false);
        if (clickPoint != null) clickPoint.setVisible(false);
    }

    public LatLng getPoint() {
        return point;
    }

    public void setPoint(LatLng point){
        this.point=point;
        if (clickpos != null && clickPoint != null) {
            clickpos.setCenter(point);
            clickPoint.setCenter(point);
        } else {
            createZone();
        }
        if (target instanceof ActiveObject)
            clickpos.setRadius(((ActiveObject) target).getRadius());
        else clickpos.setRadius(GameObjects.getPlayer().getAmbushRad());

        clickPoint.setVisible(true);
        clickpos.setVisible(true);

    }

}
