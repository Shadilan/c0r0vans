package utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import coe.com.c0r0vans.R;

import java.util.HashMap;

import coe.com.c0r0vans.R;

/**
 * @author Shadilan
 * Static image collection to use without context;
 */
public class ImageLoader {
    private static ImageLoader instance;
    private static HashMap<String,Bitmap> images=new HashMap<>();

    /**
     * Load images on start;
     * @param context Application Context
     */
    public static void Loader(Context context){
        images.put("android",BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

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
