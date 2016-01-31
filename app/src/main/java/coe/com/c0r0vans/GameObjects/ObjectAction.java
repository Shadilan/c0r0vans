package coe.com.c0r0vans.GameObjects;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * @author Shadilan
 * Информация о действиях
 */
public abstract class ObjectAction {
    public abstract Bitmap getImage();
    public abstract String getInfo();
    public abstract String getCommand();
    protected boolean enabled=true;
    public void setEnable(boolean flag){
        Log.d("DebugAction",getCommand()+":"+flag);
        enabled=flag;}
    public boolean isEnabled(){return enabled;}
}
