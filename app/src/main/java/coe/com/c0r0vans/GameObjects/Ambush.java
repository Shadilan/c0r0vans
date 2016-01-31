package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import utility.ImageLoader;

/**
 * @author Shadilan
 */
public class Ambush implements GameObject {
    private Marker mark;
    private String OwnerName;
    private String GUID;
    private GoogleMap map;

    public  Ambush(GoogleMap map){
        this.map=map;

    }
    public  Ambush(GoogleMap map,JSONObject obj)
    {
        Log.d("Debug info","Ambush loaded.");
        this.map=map;
        loadJSON(obj);
        mark.setIcon(BitmapDescriptorFactory.fromBitmap(getImage()));
    }
    @Override
    public Bitmap getImage() {
        return ImageLoader.getImage("ambush");
    }

    @Override
    public Marker getMarker() {
        return mark;
    }

    @Override
    public void setMarker(Marker m) {
        mark=m;
    }

    @Override
    public void loadJSON(JSONObject obj) {
        try {
            GUID=obj.getString("GUID");
            int Lat=obj.getInt("Lat");
            int Lng=obj.getInt("Lng");
            if (mark==null) {
                setMarker(map.addMarker(new MarkerOptions().position(new LatLng(Lat / 1e6, Lng / 1e6))));
                mark.setIcon(BitmapDescriptorFactory.fromBitmap(getImage()));
            } else {
                mark.setPosition(new LatLng(Lat / 1e6, Lng / 1e6));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void RemoveObject() {
        mark.remove();
    }

    @Override
    public String getInfo() {
        return "Это ЗАСАДА!!!";
    }

    ObjectAction removeAmbush;


    @Override
    public ArrayList<ObjectAction> getActions() {
        ArrayList<ObjectAction> Actions=new ArrayList<>();
        if (removeAmbush==null){
            removeAmbush = new ObjectAction() {
                @Override
                public Bitmap getImage() {
                    return ImageLoader.getImage("remove_ambush");
                }

                @Override
                public String getInfo() {
                    return "Убрать засаду.";
                }

                @Override
                public String getCommand() {
                    return "DestroyAmbush";
                }
            };

        }
        if (removeAmbush.isEnabled())Actions.add(removeAmbush);
        Log.d("DebugAction", "removeAmbush look" + ":" + removeAmbush.isEnabled());
        return Actions;
    }

    @Override
    public String getGUID() {
        return GUID;
    }
}
