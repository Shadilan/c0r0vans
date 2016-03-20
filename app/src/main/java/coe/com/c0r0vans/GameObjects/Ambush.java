package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import coe.com.c0r0vans.GameSound;
import coe.com.c0r0vans.MyGoogleMap;
import utility.Essages;
import utility.GameSettings;
import utility.ImageLoader;

/**
 * @author Shadilan
 */
public class Ambush extends GameObject {


    private int faction;
    private int radius=30;
    private Circle zone;

    private boolean ready=true;

    public  Ambush(GoogleMap map){
        this.map=map;

    }
    public  Ambush(GoogleMap map,JSONObject obj)
    {
        Log.d("Debug info","Ambush loaded.");
        this.map=map;
        loadJSON(obj);
        changeMarkerSize(MyGoogleMap.getClientZoom());

    }
    @Override
    public Bitmap getImage() {
        return ImageLoader.getImage("ambush");
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
            if (obj.has("Owner")) faction=obj.getInt("Owner");
            if (faction<0 ||faction>4) faction=4;
            if (obj.has("Radius")) radius=obj.getInt("Radius");
            if (obj.has("Ready")) ready=obj.getBoolean("Ready");
            if (obj.has("Progress")) progress=obj.getInt("Progress");
            if (obj.has("Name")) Name="Засада "+obj.getString("Name");
            if (mark==null) {
                setMarker(map.addMarker(new MarkerOptions().position(new LatLng(Lat / 1e6, Lng / 1e6))));
                changeMarkerSize(MyGoogleMap.getClientZoom());
            } else {
                mark.setPosition(latlng);
            }
            if (zone==null){
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latlng);
                circleOptions.radius(radius);
                switch (faction){
                    case 0:
                        circleOptions.strokeColor(Color.BLUE);
                        break;
                    case 1:
                        circleOptions.strokeColor(Color.MAGENTA);
                        break;
                    case 2:
                        circleOptions.strokeColor(Color.RED);
                        break;
                    case 3:
                        circleOptions.strokeColor(Color.YELLOW);
                        break;
                    default:
                        circleOptions.strokeColor(Color.GREEN);
                }
                circleOptions.strokeWidth(2);
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
        mark.remove();
        if (zone!=null) zone.remove();
    }

    @Override
    public String getInfo() {
        if (faction==0)
        return "Ваши верные войны ждут здесь вражеских контрабандистов в Засаде.";
        else return "Засада ожидает здесь не осторожных караванщиков.";
    }

    ObjectAction removeAmbush;


    @Override
    public ArrayList<ObjectAction> getActions(boolean inZone) {
        ArrayList<ObjectAction> Actions=new ArrayList<>();
        if (removeAmbush==null){
            if (faction==0)
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

                    owner.getMarker().setVisible(false);zone.setVisible(false);
                }

                @Override
                public void postAction() {
                    GameSound.playSound(GameSound.REMOVE_AMBUSH);
                    Essages.addEssage("Засада распущена");
                    owner.RemoveObject();
                }

                @Override
                public void postError() {
                    owner.getMarker().setVisible(true);zone.setVisible(true);
                }
            };
            else if (faction!=Player.getPlayer().getRace())
                removeAmbush = new ObjectAction(this) {
                    @Override
                    public Bitmap getImage() {
                        return ImageLoader.getImage("attack_ambush");
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

                        owner.getMarker().setVisible(false);zone.setVisible(false);
                    }

                    @Override
                    public void postAction() {
                        GameSound.playSound(GameSound.KILL_SOUND);
                        Essages.addEssage("Разбойники уничтожены.");
                        owner.RemoveObject();
                    }

                    @Override
                    public void postError() {
                        owner.getMarker().setVisible(true);zone.setVisible(true);
                    }
                };

        }
        if ((faction==0 || inZone) && removeAmbush.isEnabled()) Actions.add(removeAmbush);
        return Actions;
    }


    @Override
    public void changeMarkerSize(float Type) {
        if (mark != null) {
            String markname = "ambush";
            if (!ready) markname = markname + "_build";
            markname = markname + "_"+faction;
            markname = markname + GameObject.zoomToPostfix(Type);
            mark.setIcon(ImageLoader.getDescritor(markname));
            if ("Y".equals(GameSettings.getInstance().get("USE_TILT"))) mark.setAnchor(0.5f, 1f);
            else mark.setAnchor(0.5f, 0.5f);
        }
    }

    @Override
    public void setVisibility(boolean visibility) {
        zone.setVisible(visibility);
        mark.setVisible(visibility);
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
    public int getIsOwner(){
        return faction;
    }

}
