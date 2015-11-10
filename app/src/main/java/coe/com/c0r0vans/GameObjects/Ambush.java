package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;

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
 * Created by Shadilan on 02.11.2015.
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
            OwnerName=obj.getString("Owner");
            if (mark==null) {
                setMarker(map.addMarker(new MarkerOptions().position(new LatLng(Lat / 1e6, Lng / 1e6))));
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
        return "Владелец:"+OwnerName;
    }

    @Override
    public ArrayList<ObjectAction> getActions() {
        ArrayList<ObjectAction> Actions=new ArrayList<>();
        ObjectAction act=new ObjectAction() {
            @Override
            public Bitmap getImage() {
                return ImageLoader.getImage("start_route");
            }

            @Override
            public String getInfo() {
                return "Начать маршрут из этого города.";
            }

            @Override
            public String getCommand() {
                return "createRoute";
            }
        };
        Actions.add(act);
        return Actions;
    }

    @Override
    public String getGUID() {
        return GUID;
    }
}
