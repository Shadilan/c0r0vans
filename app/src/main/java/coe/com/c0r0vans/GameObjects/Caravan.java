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

import coe.com.c0r0vans.R;
import utility.ImageLoader;

/**
 * Caravan Object
 */
public class Caravan implements GameObject {
    private Marker mark;
    private String GUID;
    private Bitmap image;
    private GoogleMap map;
    private boolean isOwner=false;
    public String getGUID() {
        return GUID;
    }

    public Caravan(GoogleMap map,JSONObject obj) throws JSONException {
        this.map=map;
        int Lat=obj.getInt("Lat");
        int Lng=obj.getInt("Lng");
        image= ImageLoader.getImage("caravan");

        mark=map.addMarker(new MarkerOptions().position(new LatLng(Lat / 1e6, Lng / 1e6)));
        changeMarkerSize((int) map.getCameraPosition().zoom);
        mark.setAnchor(0.5f, 0.5f);
        loadJSON(obj);


    }


    @Override
    public Bitmap getImage() {
        return image;
    }

    @Override
    public Marker getMarker() {
        return mark;
    }

    @Override
    public void setMarker(Marker m) {
        mark=m;
        changeMarkerSize((int) map.getCameraPosition().zoom);
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

        if (isOwner) return "Ваш караван направляется к цели, готовясь принести вам золото.";
            else return "Чейто караван, проезжает звеня не ВАШИМ золотом.";
    }
    private ObjectAction dropRoute;
    @Override
    public ArrayList<ObjectAction> getActions() {
        ArrayList<ObjectAction> Actions=new ArrayList<>();
        if (dropRoute==null)

        dropRoute = new ObjectAction(this) {
            @Override
            public Bitmap getImage() {
                return ImageLoader.getImage("drop_route");
            }

            @Override
            public String getInfo() {
                return "Сбросить маршрут.";
            }

            @Override
            public String getCommand() {
                return "DropUnfinishedRoute";
            }

            @Override
            public void preAction() {
                setEnable(false);
                //owner.getMarker().setVisible(false);
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

        if (dropRoute.isEnabled() && isOwner)Actions.add(dropRoute);
        return Actions;
    }
    @Override
    public void changeMarkerSize(int Type) {
        switch (Type){
            case GameObject.ICON_SMALL: mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.caravan_s));
                break;
            case GameObject.ICON_MEDIUM: mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.caravan_m));
                break;
            case GameObject.ICON_LARGE: mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.caravan));
                break;
        }
    }
}
