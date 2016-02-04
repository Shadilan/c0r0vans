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

import coe.com.c0r0vans.R;
import utility.ImageLoader;

/**
 * @author Shadilan
 */
public class Ambush implements GameObject {
    private Marker mark;
    private String OwnerName;
    private String GUID;
    private boolean isOwner;
    private GoogleMap map;

    public  Ambush(GoogleMap map){
        this.map=map;

    }
    public  Ambush(GoogleMap map,JSONObject obj)
    {
        Log.d("Debug info","Ambush loaded.");
        this.map=map;
        loadJSON(obj);
        changeMarkerSize((int) map.getCameraPosition().zoom);
        mark.setAnchor(0.5f, 1);
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
        mark.setAnchor(0.5f,1);
    }

    @Override
    public void loadJSON(JSONObject obj) {
        try {
            GUID=obj.getString("GUID");
            int Lat=obj.getInt("Lat");
            int Lng=obj.getInt("Lng");
            if (obj.has("Owner")) isOwner=obj.getBoolean("Owner");
            if (mark==null) {
                setMarker(map.addMarker(new MarkerOptions().position(new LatLng(Lat / 1e6, Lng / 1e6))));
                changeMarkerSize((int) map.getCameraPosition().zoom);
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
        if (isOwner)
        return "Ваша верные войны ждут тут вражеских контрабандистов в Засаде.";
        else return "Засада ожидает здесь не осторожных караванщиков.";
    }

    ObjectAction removeAmbush;


    @Override
    public ArrayList<ObjectAction> getActions() {
        ArrayList<ObjectAction> Actions=new ArrayList<>();
        if (removeAmbush==null){
            removeAmbush = new ObjectAction(this) {
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

                @Override
                public void preAction() {
                    owner.getMarker().setVisible(false);
                }

                @Override
                public void postAction() {
                    owner.getMarker().remove();
                }

                @Override
                public void postError() {
                    owner.getMarker().setVisible(true);
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

    @Override
    public void changeMarkerSize(int Type) {
        if (isOwner)
        {
            switch (Type){
                case GameObject.ICON_SMALL: mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ambush_self_s));
                    break;
                case GameObject.ICON_MEDIUM: mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ambush_self_m));
                    break;
                case GameObject.ICON_LARGE: mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ambush_self));
                    break;
            }
        } else {
            switch (Type) {
                case GameObject.ICON_SMALL:
                    mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ambush_s));
                    break;
                case GameObject.ICON_MEDIUM:
                    mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ambush_m));
                    break;
                case GameObject.ICON_LARGE:
                    mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ambush));
                    break;
            }
        }
    }
}
