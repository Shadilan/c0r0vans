package com.coe.c0r0vans.GameObjects;

import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import utility.GATracker;

/**
 * @author Shadilan
 * Информация о действиях
 */
public abstract class ObjectAction {
    public ObjectAction(GameObject owner){
        this.owner=owner;
    }
    protected GameObject owner;
    public abstract Bitmap getImage();
    public abstract String getCommand();
    public abstract void preAction();
    public abstract void postAction(JSONObject response);
    public abstract void postError(JSONObject response);
    public void serverError(){
        try {
            postError(new JSONObject().put("Error","S0000").put("Message","Server Error."));
        } catch (JSONException e) {
            GATracker.trackException("ObjectAction",e);
        }
    }
    protected boolean enabled=true;
    public void setEnable(boolean flag){
        Log.d("DebugAction",getCommand()+":"+flag);
        enabled=flag;}
    public boolean isEnabled(){return enabled;}
}
