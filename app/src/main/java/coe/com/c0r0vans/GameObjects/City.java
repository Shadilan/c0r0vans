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
 * @author Shadilan
 */
public class City implements GameObject{
    private Marker mark;
    private String GUID;
    private String CityName;
    private Bitmap image;
    private GoogleMap map;
    public String getGUID() {
        return GUID;
    }

    public City(GoogleMap map,JSONObject obj) throws JSONException {
        this.map=map;
        int Lat=obj.getInt("Lat");
        int Lng=obj.getInt("Lng");
        image=ImageLoader.getImage("city");

        mark=map.addMarker(new MarkerOptions().position(new LatLng(Lat / 1e6, Lng / 1e6)));
        changeMarkerSize((int) map.getCameraPosition().zoom);
        mark.setAnchor(0.5f,1);
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
        mark.setAnchor(0.5f, 1);
    }

    @Override
    public void loadJSON(JSONObject obj) {
        try {
            GUID=obj.getString("GUID");
            int Lat=obj.getInt("Lat");
            int Lng=obj.getInt("Lng");
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
        return "Name:"+CityName;
    }

    public String getCityName(){return CityName;}

    private ObjectAction startRoute;
    private ObjectAction finishRoute;
    @Override
    public ArrayList<ObjectAction> getActions() {
        ArrayList<ObjectAction> Actions=new ArrayList<>();

        if (startRoute==null)
            startRoute = new ObjectAction(this) {
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
                    return "StartRoute";
                }

                @Override
                public void preAction() {

                }

                @Override
                public void postAction() {

                }

                @Override
                public void postError() {

                }
            };
        if (startRoute.isEnabled()) Actions.add(startRoute);

        if (finishRoute==null)
        finishRoute = new ObjectAction(this) {
                @Override
                public Bitmap getImage() {
                    return ImageLoader.getImage("end_route");
                }

                @Override
                public String getInfo() {
                    return "Закончить маршрут на этом городе и запустить караван.";
                }

                @Override
                public String getCommand() {
                    return "FinishRoute";
                }

            @Override
            public void preAction() {

            }

            @Override
            public void postAction() {

            }

            @Override
            public void postError() {

            }
        };
        if (finishRoute.isEnabled()) Actions.add(finishRoute);
        return Actions;
    }
    @Override
    public void changeMarkerSize(int Type) {
        switch (Type){
            case GameObject.ICON_SMALL: mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.city_s));
                break;
            case GameObject.ICON_MEDIUM: mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.city_m));
                break;
            case GameObject.ICON_LARGE: mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.city));
                break;
        }
    }
}
