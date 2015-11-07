package utility;

import android.content.Context;
import android.util.Log;

import coe.com.c0r0vans.R;

/**
 * @author Shadilan
 */
public class ResourceString {
    private static ResourceString instance;
    private Context ctx;
    private ResourceString(Context ctx){
        this.ctx=ctx;
    }
    public static ResourceString getInstance(Context ctx){
        if (instance ==null){
            instance=new ResourceString(ctx);
        }
        return instance;
    }
    public static ResourceString getInstance(){
        return instance;
    }
    public String getString(String stringName){
        Log.d("PackageInfo",ctx.getPackageName());
        int id=ctx.getResources().getIdentifier(stringName,"string",ctx.getPackageName());
        if (id==0) return "";
        return ctx.getResources().getString(id);
    }
}
