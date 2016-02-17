package utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import coe.com.c0r0vans.R;
import java.util.HashMap;


/**
 * @author Shadilan
 * Static image collection to use without context;
 */
public class ImageLoader {
    //private static ImageLoader instance;
    private static HashMap<String,Bitmap> images=new HashMap<>();
    private static HashMap<String,BitmapDescriptor> descriptors = new HashMap<>();
    /**
     * Load images on start;
     * @param context Application Context
     */
    public static void Loader(Context context){
        images.put("android", BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        //Buttons
        images.put("closebutton", BitmapFactory.decodeResource(context.getResources(), R.mipmap.closebutton));
        //Markers
        createMarker(context, R.mipmap.marker, "marker");
        createMarker(context, R.mipmap.city, "city");
        createMarker(context, R.mipmap.ambush, "ambush");
        createMarker(context, R.mipmap.caravan, "caravan");
        createMarker(context, R.mipmap.caravan_e, "caravan_e");
        createMarker(context, R.mipmap.ambush_self, "ambush_self");
        createMarker(context, R.mipmap.ambushbuild, "ambushbuild");

        //Actions
            //City
        images.put("start_route",BitmapFactory.decodeResource(context.getResources(), R.mipmap.start_route));
        images.put("end_route",BitmapFactory.decodeResource(context.getResources(), R.mipmap.end_route));
        images.put("buy_item",BitmapFactory.decodeResource(context.getResources(), R.mipmap.buy_item));
            //Player
        images.put("create_ambush",BitmapFactory.decodeResource(context.getResources(), R.mipmap.ambush_img));
        images.put("drop_route",BitmapFactory.decodeResource(context.getResources(), R.mipmap.drop_route));
            //Ambush
        images.put("remove_ambush",BitmapFactory.decodeResource(context.getResources(), R.mipmap.dismiss));
        images.put("attack_ambush",BitmapFactory.decodeResource(context.getResources(), R.mipmap.rem_ambush));

            //Route
        images.put("remove_route",BitmapFactory.decodeResource(context.getResources(), R.mipmap.remove_route));

        //Skills
        images.put("speed",BitmapFactory.decodeResource(context.getResources(), R.mipmap.up_speed));
        images.put("set_ambushes",BitmapFactory.decodeResource(context.getResources(), R.mipmap.ambushbuild));
        images.put("ambushes",BitmapFactory.decodeResource(context.getResources(), R.mipmap.ambush_img));
        images.put("cargo",BitmapFactory.decodeResource(context.getResources(), R.mipmap.caravan));
        images.put("bargain",BitmapFactory.decodeResource(context.getResources(), R.mipmap.buy_item));
        images.put("paladin",BitmapFactory.decodeResource(context.getResources(), R.mipmap.rem_ambush));


    }

    /**
     * Get image
     * @param name ImageName
     * @return Bitmap
     */
    public static Bitmap getImage(String name){
        return images.get(name);
    }
    public static BitmapDescriptor getDescritor(String name) {return  descriptors.get(name);}
    private static void createMarker(Context context,int resource,String name){
        Bitmap b=BitmapFactory.decodeResource(context.getResources(), resource);
        descriptors.put(name, BitmapDescriptorFactory.fromBitmap(b));
        descriptors.put(name+"_m",BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b, (int) (b.getWidth() * 0.75), (int) (b.getHeight() * 0.75), false)));
        descriptors.put(name + "_s", BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b, (int) (b.getWidth() * 0.5), (int) (b.getHeight() * 0.5), false)));
    }
}
