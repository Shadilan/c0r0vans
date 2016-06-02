package utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.coe.c0r0vans.R;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.util.HashMap;

import utility.notification.Essages;


/**
 * @author Shadilan
 * Static image collection to use without context;
 */
public class ImageLoader {
    //private static ImageLoader instance;
    private static HashMap<String,Bitmap> images=new HashMap<>();
    private static HashMap<String,Bitmap> markers=new HashMap<>();
    //private static HashMap<String,BitmapDescriptor> descriptors = new HashMap<>();
    /**
     * Load images on start;
     * @param context Application Context
     */
    public static void Loader(Context context){
        images.put("android", BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));
        //Buttons
        images.put("closebutton", BitmapFactory.decodeResource(context.getResources(), R.mipmap.closebutton));
        //Notifications
        images.put("income",BitmapFactory.decodeResource(context.getResources(),R.mipmap.ic_launcher));
        //Markers
        MapsInitializer.initialize(context);
        createMarker(context, R.mipmap.marker, "marker");
        createMarker(context, R.mipmap.city_1, "city_1");
        createMarker(context, R.mipmap.city_2, "city_2");
        createMarker(context, R.mipmap.city_3, "city_3");
        createMarker(context, R.mipmap.city_4, "city_4");
        createMarker(context, R.mipmap.city_5, "city_5");
        createMarker(context, R.mipmap.city_6, "city_6");
        createMarker(context, R.mipmap.city_7, "city_7");
        createMarker(context, R.mipmap.city_8, "city_8");
        createMarker(context, R.mipmap.city_9, "city_9");
        createMarker(context, R.mipmap.city_10, "city_10");



            createMarker(context, R.mipmap.ambush_00, "ambush_00");
            createMarker(context, R.mipmap.ambush_01, "ambush_01");
            createMarker(context, R.mipmap.ambush_02, "ambush_02");
            createMarker(context, R.mipmap.ambush_03, "ambush_03");
            createMarker(context, R.mipmap.ambush_1, "ambush_1");
            createMarker(context, R.mipmap.ambush_2, "ambush_2");
            createMarker(context, R.mipmap.ambush_3, "ambush_3");
            createMarker(context, R.mipmap.ambush_00, "ambush_4");
            createMarker(context, R.mipmap.ambush_build_00, "ambush_build_00");
            createMarker(context, R.mipmap.ambush_build_01, "ambush_build_01");
            createMarker(context, R.mipmap.ambush_build_02, "ambush_build_02");
            createMarker(context, R.mipmap.ambush_build_03, "ambush_build_03");
            createMarker(context, R.mipmap.ambush_build_1, "ambush_build_1");
            createMarker(context, R.mipmap.ambush_build_2, "ambush_build_2");
            createMarker(context, R.mipmap.ambush_build_3, "ambush_build_3");
            createMarker(context, R.mipmap.ambush_build_00, "ambush_build_4");

        createMarker(context, R.mipmap.caravan_00, "caravan_00");
        createMarker(context, R.mipmap.caravan_01, "caravan_01");
        createMarker(context, R.mipmap.caravan_02, "caravan_02");
        createMarker(context, R.mipmap.caravan_03, "caravan_03");
        createMarker(context, R.mipmap.caravan_1, "caravan_1");
        createMarker(context, R.mipmap.caravan_2, "caravan_2");
        createMarker(context, R.mipmap.caravan_3, "caravan_3");
        createMarker(context, R.mipmap.caravan_4, "caravan_4");


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
        images.put("speed_buy",BitmapFactory.decodeResource(context.getResources(), R.mipmap.up_speed_buy));
        images.put("set_ambushes",BitmapFactory.decodeResource(context.getResources(), R.mipmap.ambushbuild));
        images.put("set_ambushes_buy",BitmapFactory.decodeResource(context.getResources(), R.mipmap.ambushbuild_buy));
        images.put("ambushes",BitmapFactory.decodeResource(context.getResources(), R.mipmap.ambush));
        images.put("ambushes_buy",BitmapFactory.decodeResource(context.getResources(), R.mipmap.ambush_buy));
        images.put("cargo",BitmapFactory.decodeResource(context.getResources(), R.mipmap.cargo));
        images.put("cargo_buy",BitmapFactory.decodeResource(context.getResources(), R.mipmap.cargo_buy));
        images.put("bargain",BitmapFactory.decodeResource(context.getResources(), R.mipmap.bargain));
        images.put("bargain_buy",BitmapFactory.decodeResource(context.getResources(), R.mipmap.bargain_buy));
        images.put("paladin",BitmapFactory.decodeResource(context.getResources(), R.mipmap.paladin));
        images.put("paladin_buy",BitmapFactory.decodeResource(context.getResources(), R.mipmap.paladin_buy));
        images.put("create_city",BitmapFactory.decodeResource(context.getResources(), R.mipmap.create_city));
        images.put("founder",BitmapFactory.decodeResource(context.getResources(), R.mipmap.founder));
        images.put("unknown",BitmapFactory.decodeResource(context.getResources(),R.mipmap.unknown));

        //Workaround для решения проблемы белых квадратов
        Bitmap b=BitmapFactory.decodeResource(context.getResources(), R.mipmap.unknown);
        //descriptors.put("unknown", BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b, b.getWidth(), b.getHeight() , false)));


    }

    /**
     * Get image
     * @param name ImageName
     * @return Bitmap
     */
    public static Bitmap getImage(String name){
        Bitmap result=images.get(name);
        if (result==null) {
            Essages.addEssage("Изображение " + name + " не найдено.");
            GATracker.trackException("ImageLoader","ImageNoteFound:"+name);
            result=images.get("unknown");
        }
        return result;
    }
    public static BitmapDescriptor getDescritor(String name) {

        //BitmapDescriptor result = descriptors.get(name);
        //Workaround for https://code.google.com/p/gmaps-api-issues/issues/detail?id=9765
        //BitmapDescriptor result=BitmapDescriptorFactory.fromBitmap(markers.get(name));
        /*if (result==null) {
            GATracker.trackException("ImageLoader","ImageNoteFound:"+name);
            Essages.addEssage("Изображение объекта "+name + " не найдено.");
            result=BitmapDescriptorFactory.fromBitmap(images.get("unknown"));
        }*/
        return  BitmapDescriptorFactory.fromBitmap(markers.get(name));//result;
    }

    private static void createMarker(Context context,int resource,String name){
        Bitmap b=BitmapFactory.decodeResource(context.getResources(), resource);
        //Workaround for https://code.google.com/p/gmaps-api-issues/issues/detail?id=9765
        markers.put(name,Bitmap.createScaledBitmap(b, b.getWidth(), b.getHeight() , false));
        markers.put(name+"_m",Bitmap.createScaledBitmap(b, (int) (b.getWidth()*0.75),(int) (b.getHeight()*0.75) , false));
        markers.put(name+"_s",Bitmap.createScaledBitmap(b, (int) (b.getWidth()*0.5),(int) (b.getHeight()*0.5) , false));

        /*descriptors.put(name, BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b, b.getWidth(), b.getHeight() , false)));
        descriptors.put(name+"_m",BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b, (int) (b.getWidth() * 0.75), (int) (b.getHeight() * 0.75), false)));
        descriptors.put(name + "_s", BitmapDescriptorFactory.fromBitmap(Bitmap.createScaledBitmap(b, (int) (b.getWidth() * 0.5), (int) (b.getHeight() * 0.5), false)));*/
    }
}
