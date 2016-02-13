package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import coe.com.c0r0vans.R;
import utility.Essages;
import utility.GameSettings;
import utility.ImageLoader;
import utility.serverConnect;

/**
 * Caravan Object
 */
public class Caravan implements GameObject {
    private Marker mark;
    private String GUID;
    private Bitmap image;
    private GoogleMap map;
    private LatLng start;
    private LatLng finish;
    private String startName;
    private String finishName;
    private Polyline route;
    private boolean isOwner=false;
    private double speed=20;
    public String getGUID() {
        return GUID;
    }

    public Caravan(GoogleMap map,JSONObject obj) throws JSONException {
        this.map=map;
        image= ImageLoader.getImage("caravan");
        loadJSON(obj);
        mark.setAnchor(0.5f, 0.5f);



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
            LatLng latlng=new LatLng(Lat / 1e6, Lng / 1e6);
            if (obj.has("Owner")) isOwner=obj.getBoolean("Owner");
            if (obj.has("StartName")) startName=obj.getString("StartName");
            if (obj.has("FinishName")) finishName=obj.getString("FinishName");
            if (obj.has("Speed")) speed=obj.getDouble("Speed");
            if (obj.has("StartLat") && obj.has("StartLng")){
                double lat=obj.getInt("StartLat")/1e6;
                double lng=obj.getInt("StartLng")/1e6;
                start=new LatLng(lat,lng);
            } else start=null;
            if (obj.has("FinishLat") && obj.has("FinishLng")){
                double lat=obj.getInt("FinishLat")/1e6;
                double lng=obj.getInt("FinishLng")/1e6;
                finish=new LatLng(lat,lng);
            } else finish=null;

            if (mark==null) {
                setMarker(map.addMarker(new MarkerOptions().position(latlng)));
            } else {
                mark.setPosition(new LatLng(Lat / 1e6, Lng / 1e6));
            }
            if (route==null && start!=null && finish!=null){
                PolylineOptions options=new PolylineOptions();

                if (isOwner) {
                    options.width(2);
                    options.color(Color.BLUE);

                /*else {
                    options.width(4);
                    options.color(Color.RED);
                }*/
                    options.add(start);
                    options.add(finish);
                    route = map.addPolyline(options);
                }

            }
            showRoute();

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void showRoute(){
        String opt= GameSettings.getInstance().get("SHOW_CARAVAN_ROUTE");
        if (opt==null) opt="N";
        if (opt.equals("Y") && route!=null){
            route.setVisible(true);
        } else if (route!=null)
        {
            route.setVisible(false);
        }
    }

    @Override
    public void RemoveObject() {
        mark.remove();
    }

    @Override
    public String getInfo() {
        String tushkan="";
        if (Math.random()*100<3) tushkan="На крыше видна тень кого-то невидимого.";
        if (isOwner) if (speed>0) return "Ваш караван направляется из города "+startName+" в город "+finishName +", готовясь принести вам золото."+tushkan;
        else return "Ваш караван направляется из города "+finishName+" в город "+startName +", готовясь принести вам золото."+tushkan;
            else return "Чейто караван проезжает, звеня не ВАШИМ золотом.";
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
                return "DropRoute";
            }

            @Override
            public void preAction() {
                setEnable(false);
                owner.getMarker().setVisible(false);
            }

            @Override
            public void postAction() {
                owner.RemoveObject();
                serverConnect.getInstance().RefreshCurrent();
                Essages.addEssage("Караван из распущен.");
            }

            @Override
            public void postError() {
                owner.getMarker().setVisible(true);
            }
        };

        //if (dropRoute.isEnabled() && isOwner && false)Actions.add(dropRoute);
        return Actions;
    }
    @Override
    public void changeMarkerSize(int Type) {
        if (isOwner)
        switch (Type){
            case GameObject.ICON_SMALL: mark.setIcon(ImageLoader.getDescritor("caravan_s"));
                break;
            case GameObject.ICON_MEDIUM: mark.setIcon(ImageLoader.getDescritor("caravan_m"));
                break;
            case GameObject.ICON_LARGE: mark.setIcon(ImageLoader.getDescritor("caravan"));
                break;
            default: mark.setIcon(ImageLoader.getDescritor("caravan"));
                Essages.addEssage("Ваш зум не корректен.");
        } else
            switch (Type){
                case GameObject.ICON_SMALL: mark.setIcon(ImageLoader.getDescritor("caravan_e_s"));
                    break;
                case GameObject.ICON_MEDIUM: mark.setIcon(ImageLoader.getDescritor("caravan_e_m"));
                    break;
                case GameObject.ICON_LARGE: mark.setIcon(ImageLoader.getDescritor("caravan_e"));
                    break;
                default:mark.setIcon(ImageLoader.getDescritor("caravan_e"));
                    Essages.addEssage("Ваш зум не корректен.");
            }

    }
}
