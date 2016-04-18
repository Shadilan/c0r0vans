package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;
import android.util.Log;

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
    public abstract void postAction();
    public abstract void postError();
    public void serverError(){
        postError();
    }
    protected boolean enabled=true;
    public void setEnable(boolean flag){
        Log.d("DebugAction",getCommand()+":"+flag);
        enabled=flag;}
    public boolean isEnabled(){return enabled;}
}
