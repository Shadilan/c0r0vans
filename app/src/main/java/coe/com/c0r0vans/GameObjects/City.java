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

    public City(GoogleMap map,JSONObject obj){
        this.map=map;
        image=ImageLoader.getImage("city");
        //mark=map.addMarker(new MarkerOptions());
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
        m.setIcon(BitmapDescriptorFactory.fromBitmap(image));
    }

    @Override
    public void loadJSON(JSONObject obj) {
        try {
            GUID=obj.getString("GUID");
            int Lat=obj.getInt("Lat");
            int Lng=obj.getInt("Lng");
            CityName=obj.getString("CityName");
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

    @Override
    public ArrayList<ObjectAction> getActions() {
        ArrayList<ObjectAction> Actions=new ArrayList<>();
        ObjectAction act;
        if (((Player)SelectedObject.getInstance().getExecuter()).getRoute()==0) {
            act = new ObjectAction() {
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
                    return "create_route";
                }
            };
            Actions.add(act);
        } else {
            act = new ObjectAction() {
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
                    return "end_route";
                }
            };
            Actions.add(act);
        }
        return Actions;
    }
}
