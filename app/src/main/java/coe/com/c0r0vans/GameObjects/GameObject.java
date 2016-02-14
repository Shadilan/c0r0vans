package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * @author Shadilan
 */
public interface GameObject {
      int ICON_SMALL=16;
      int ICON_MEDIUM=17;
      int ICON_LARGE=18;
    /**
     * Return image of object
     * @return Image to draw object
     */
     Bitmap getImage();

    /**
     * Return mapMarker seted to object
     * @return mapMarker
     */
     Marker getMarker();

    /**
     * Set marker of object on map
     * @param m Marker of object
     */
     void setMarker(Marker m);

    /**
     * Load object from JSON
     * @param obj JSON to Load
     */
     void loadJSON(JSONObject obj) throws JSONException;

    /**
     * Actions to do on remove object
     */
     void RemoveObject();

    /**
     * Generate Info about object
     * @return info about object
     */
     String getInfo();

    /**
     * Get Action list
     * @return ArrayList of Actions for object
     */
     ArrayList<ObjectAction> getActions();

     String getGUID();

     void changeMarkerSize(int Type);
    int getProgress();

}
