package coe.com.c0r0vans.GameObjects;

import android.graphics.Color;

import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;

import coe.com.c0r0vans.MyGoogleMap;

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
    Circle clickpos;
    Circle clickPoint;
    public void setPoint(LatLng point){
        this.point=point;
        if (clickpos != null) {
            clickpos.setCenter(point);
            clickpos.setRadius(target.getRadius());

        } else {
            CircleOptions circleOptions = new CircleOptions();
            circleOptions.center(point);
            circleOptions.radius(Player.getPlayer().getAmbushRad());
            circleOptions.strokeColor(Color.RED);
            circleOptions.strokeWidth(5);
            circleOptions.zIndex(1);
            circleOptions.radius(target.getRadius());
            clickpos = MyGoogleMap.getMap().addCircle(circleOptions);

        }
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
