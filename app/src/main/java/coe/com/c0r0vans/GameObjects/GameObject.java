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
     public static final float ICON_SMALL = 15;
    public static final float ICON_MEDIUM = 16;
    public static final float ICON_LARGE = 17;
    public static String zoomToPostfix(float zoom){
        String result="_m";
        if (zoom==GameObject.ICON_SMALL)
            result = "_s";
        else if (zoom==GameObject.ICON_MEDIUM)
            result = "_m";
        else if (zoom==GameObject.ICON_LARGE)
            result = "";
        else result = "_m";
        return result;
    }

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

    /**
     * Load object from JSON
     *
     * @param obj JSON to Load
     */
    public void loadJSON(JSONObject obj) throws JSONException {

    }

    /**
     * Actions to do on remove object
     */
    public void RemoveObject() {
        if (mark!=null)
        mark.remove();
    }

    /**
     * Generate Info about object
     *
     * @return info about object
     */
    public String getInfo() {
        return "";
    }

    /**
     * Get Action list
     *
     * @return ArrayList of Actions for object
     */
    public  ArrayList<ObjectAction> getActions(boolean inZone) {
        return null;
    }


    public void changeMarkerSize(float Type) {
        mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));
    }

    public int getProgress() {
        return progress;
    }

    public void setVisibility(boolean visibility) {
        if (mark!=null) mark.setVisible(false);
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }
    public String getName(){return Name;}
}
