package utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import coe.com.c0r0vans.R;
import java.util.HashMap;


/**
 * @author Shadilan
 * Static image collection to use without context;
 */
public class ImageLoader {
    //private static ImageLoader instance;
    private static HashMap<String,Bitmap> images=new HashMap<>();

    /**
     * Load images on start;
     * @param context Application Context
     */
    public static void Loader(Context context){
        //Markers
        images.put("android",BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        images.put("hero",BitmapFactory.decodeResource(context.getResources(), R.mipmap.hero));
        images.put("marker",BitmapFactory.decodeResource(context.getResources(), R.mipmap.marker));
        images.put("city",BitmapFactory.decodeResource(context.getResources(), R.mipmap.city));
        images.put("ambush",BitmapFactory.decodeResource(context.getResources(), R.mipmap.ambush_img));
        images.put("caravan",BitmapFactory.decodeResource(context.getResources(), R.mipmap.caravan));
        //Actions
            //City
        images.put("start_route",BitmapFactory.decodeResource(context.getResources(), R.mipmap.start_route));
        images.put("end_route",BitmapFactory.decodeResource(context.getResources(), R.mipmap.end_route));
        images.put("buy_item",BitmapFactory.decodeResource(context.getResources(), R.mipmap.buy_item));
        images.put("set_home",BitmapFactory.decodeResource(context.getResources(), R.mipmap.set_home));
            //Player
        images.put("create_ambush",BitmapFactory.decodeResource(context.getResources(), R.mipmap.ambush));
        images.put("create_waypoint",BitmapFactory.decodeResource(context.getResources(), R.mipmap.create_waypoint));
        images.put("drop_route",BitmapFactory.decodeResource(context.getResources(), R.mipmap.drop_route));
            //Ambush
        images.put("remove_ambush",BitmapFactory.decodeResource(context.getResources(), R.mipmap.rem_ambush));
        images.put("attack_ambush",BitmapFactory.decodeResource(context.getResources(), R.mipmap.rem_ambush));
            //Route
        images.put("remove_route",BitmapFactory.decodeResource(context.getResources(), R.mipmap.remove_route));


    }

    /**
     * Get image
     * @param name ImageName
     * @return Bitmap
     */
    public static Bitmap getImage(String name){
        return images.get(name);
    }
}
