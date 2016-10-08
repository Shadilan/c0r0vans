package com.coe.c0r0vans.GameObject;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.RelativeLayout;

import com.coe.c0r0vans.GameObjects.ObjectAction;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Shadilan
 */
public class GameObject {

    public static final float ICON_SMALL = 15;
    public static final float ICON_MEDIUM = 16;
    public static final float ICON_LARGE = 17;
    public void update(){
        updated=new Date();
    }
    private boolean forceRemove=false;
    public void forceRemove(){
        forceRemove=true;
    }
    public boolean getForceRemove(){
        return forceRemove;
    }

    public Date getUpdated() {
        return updated;
    }

    private Date updated;

    private int life;
    protected boolean owner=false;

    protected static String zoomToPostfix(float zoom){
        String result;
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
    protected Circle zone;
    protected int radius=0;

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
        update();
    }

    /**
     * Actions to do on remove object
     */



    /**
     * Get Action list
     *
     * @return ArrayList of Actions for object
     */
    public  ArrayList<ObjectAction> getActions(boolean inZone) {
        return null;
    }


    public void changeMarkerSize() {

    }

    public int getProgress() {
        return progress;
    }

    public void setVisibility(boolean visibility) {

        if (mark!=null) mark.setVisible(visibility);
    }

    public void setMap(GoogleMap map) {
        this.map = map;
    }
    public String getName(){return Name;}
    public RelativeLayout getObjectView(Context context){
        return null;
    }
    public Circle getZone(){
        return zone;
    }

    public int getLife() {
        return life;
    }

    public void setPostion(LatLng latLng) {
        update();
        if (mark!=null){

            mark.setPosition(latLng);
        }
        if (zone!=null){

            zone.setCenter(latLng);
        }
    }

    protected ArrayList<OnGameObjectChange> changeListeners;
    protected ArrayList<OnGameObjectRemove> removeListeners;

    public void addOnChangeListeners(OnGameObjectChange onGameObjectChange){
        if (onGameObjectChange==null) return;
        if (changeListeners==null) changeListeners=new ArrayList<>();
        changeListeners.add(onGameObjectChange);
    }
    public void addOnRemoveListeners(OnGameObjectRemove onGameObjectRemove){
        if (onGameObjectRemove==null) return;
        if (removeListeners==null) removeListeners=new ArrayList<>();
        removeListeners.add(onGameObjectRemove);
    }
    public void removeChangeListeners(OnGameObjectChange onGameObjectChange){
        if (removeListeners==null || onGameObjectChange==null) return;
        changeListeners.remove(onGameObjectChange);
    }

    protected void change(int type){
        if (changeListeners!=null){
            for (OnGameObjectChange onGameObjectChange:changeListeners){
                onGameObjectChange.onChange(type);
            }
        }
    }
    public void RemoveObject() {
        if (removeListeners!=null){
            for (OnGameObjectRemove onGameObjectRemove:removeListeners){
                onGameObjectRemove.onRemove();
            }
        }
        if (mark!=null){
            mark.remove();
            mark=null;
        }

        if (zone!=null) {
            zone.remove();
            zone=null;
        }



    }
    public LatLng getPosition() {return null;}
    public boolean isOwner() {
        return owner;
    }

}
