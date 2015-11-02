package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.Marker;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Shadilan
 */
public interface GameObject {

    /**
     * Return image of object
     * @return Image to draw object
     */
    public Bitmap getImage();

    /**
     * Return mapMarker seted to object
     * @return mapMarker
     */
    public Marker getMarker();

    /**
     * Set marker of object on map
     * @param m Marker of object
     */
    public void setMarker(Marker m);

    /**
     * Load object from JSON
     * @param obj JSON to Load
     */
    public void loadJSON(JSONObject obj);

    /**
     * Actions to do on remove object
     */
    public void RemoveObject();

    /**
     * Generate Info about object
     * @return info about object
     */
    public String getInfo();

    /**
     * Get Action list
     * @return ArrayList of Actions for object
     */
    public ArrayList<ObjectAction> getActions();

    public String getGUID();


}
