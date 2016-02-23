package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import coe.com.c0r0vans.R;

/**
 * @author Shadilan
 */
public class GameObject {
     public static final int ICON_SMALL = 16;
    public static final int ICON_MEDIUM = 17;
    public static final int ICON_LARGE = 18;


    protected Bitmap image;
    protected Marker mark;
    protected Circle circle;
    protected GoogleMap map;


    protected String GUID="";
    protected String Name="";

    protected int progress=0;

    public String getGUID() {
        return GUID;
    }

    /**
     * Return image of object
     *
     * @return Image to draw object
     */
    public Bitmap getImage() {
        return image;
    }

    /**
     * Return mapMarker seted to object
     *
     * @return mapMarker
     */
    public Marker getMarker() {
        return mark;
    }

    /**
     * Set marker of object on map
     *
     * @param m Marker of object
     */
    public void setMarker(Marker m) {
        mark = m;
    }

    ;

    /**
     * Load object from JSON
     *
     * @param obj JSON to Load
     */
    public void loadJSON(JSONObject obj) throws JSONException {

    }

    ;

    /**
     * Actions to do on remove object
     */
    public void RemoveObject() {
        if (mark!=null)
        mark.remove();
    }

    ;

    /**
     * Generate Info about object
     *
     * @return info about object
     */
    public String getInfo() {
        return "";
    }

    ;

    /**
     * Get Action list
     *
     * @return ArrayList of Actions for object
     */
    public  ArrayList<ObjectAction> getActions() {
        return null;
    }

    ;


    public void changeMarkerSize(int Type) {
        mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));
    }

    public int getProgress() {
        return progress;
    }

    ;

    public void setVisibility(boolean visibility) {
        if (mark!=null) mark.setVisible(false);
    }

    ;

    public void setMap(GoogleMap map) {
        this.map = map;
    }
    public String getName(){return Name;}
}
