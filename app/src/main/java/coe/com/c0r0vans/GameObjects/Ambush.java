package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import coe.com.c0r0vans.GameSound;
import coe.com.c0r0vans.R;
import utility.GameSettings;
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
    private int radius=30;
    private Circle zone;

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
            LatLng latlng=new LatLng(Lat / 1e6, Lng / 1e6);
            if (obj.has("Owner")) isOwner=obj.getBoolean("Owner");
            if (obj.has("Radius")) radius=obj.getInt("Radius");

            if (mark==null) {
                setMarker(map.addMarker(new MarkerOptions().position(new LatLng(Lat / 1e6, Lng / 1e6))));
                changeMarkerSize((int) map.getCameraPosition().zoom);
            } else {
                mark.setPosition(latlng);
            }
            if (zone==null){
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latlng);
                circleOptions.radius(radius);
                circleOptions.strokeColor(Color.RED);
                circleOptions.strokeWidth(1);
                zone = map.addCircle(circleOptions);
            } else
            {
                zone.setCenter(latlng);
                zone.setRadius(radius);
            }
            showRadius();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void RemoveObject() {
        mark.remove();zone.remove();
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
            if (isOwner)
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
                    return "CancelAmbush";
                }

                @Override
                public void preAction() {
                    GameSound.playSound(GameSound.KILL_SOUND);
                    owner.getMarker().setVisible(false);zone.setVisible(false);
                }

                @Override
                public void postAction() {
                    owner.RemoveObject();
                }

                @Override
                public void postError() {
                    owner.getMarker().setVisible(true);zone.setVisible(true);
                }
            };
            else
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
                        GameSound.playSound(GameSound.KILL_SOUND);
                        owner.getMarker().setVisible(false);zone.setVisible(false);
                    }

                    @Override
                    public void postAction() {
                        owner.RemoveObject();
                    }

                    @Override
                    public void postError() {
                        owner.getMarker().setVisible(true);zone.setVisible(true);
                    }
                };

        }
        if (removeAmbush.isEnabled())Actions.add(removeAmbush);
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
    public void showRadius(){
        String opt= GameSettings.getInstance().get("SHOW_AMBUSH_RADIUS");
        if (opt.equals("Y")){
            zone.setVisible(true);
        } else
        {
            zone.setVisible(false);
        }
    }
}
