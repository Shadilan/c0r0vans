package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;

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

import utility.GPSInfo;
import utility.ImageLoader;
import utility.ResourceString;

/**
 * @author Shadilan
 */
public class Player implements GameObject{

    private Bitmap image;
    private Marker mark;
    private Circle circle;
    private GoogleMap map;
    private String GUID;
    private String Name;
    private String City;
    private int Caravans=0;
    private int Ambushes=0;
    private int Route=0;
    private int AmbushRad=30;
    private int SeeDist=50;
    public int getAmbushRad(){return AmbushRad;}
    public int getRoute(){
        return Route;
    }
    private int Gold=0;


    public String getGUID(){
        return GUID;
    }
    public Player(GoogleMap mMap){
        image= ImageLoader.getImage("hero");
        Bitmap mimage= ImageLoader.getImage("marker");
        map=mMap;
        mark=mMap.addMarker(new MarkerOptions().position(new LatLng(GPSInfo.getInstance().GetLat() / 1E6, GPSInfo.getInstance().GetLng() / 1E6)));
        CircleOptions circleOptions=new CircleOptions();
        circleOptions.center(new LatLng(GPSInfo.getInstance().GetLat() / 1E6, GPSInfo.getInstance().GetLng() / 1E6));
        circleOptions.radius(SeeDist);
        circleOptions.strokeColor(Color.RED);
        circleOptions.strokeWidth(1);
        circle=mMap.addCircle(circleOptions);
        mark.setIcon(BitmapDescriptorFactory.fromBitmap(mimage));
        mark.setAnchor(0.5f, 0.5f);
    }
    public Player(GoogleMap mMap,Marker m){
        image= ImageLoader.getImage("hero");
        Bitmap mimage= ImageLoader.getImage("marker");
        map=mMap;
        mark=m;
        CircleOptions circleOptions=new CircleOptions();
        circleOptions.center(new LatLng(GPSInfo.getInstance().GetLat() / 1E6, GPSInfo.getInstance().GetLng() / 1E6));
        circleOptions.radius(100);
        circleOptions.strokeColor(Color.RED);
        circleOptions.strokeWidth(1);
        circle=mMap.addCircle(circleOptions);
        mark.setIcon(BitmapDescriptorFactory.fromBitmap(mimage));
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

    public Circle getCircle(){
        return circle;
    }
    @Override
    public void setMarker(Marker m) {
        mark=m;
        circle.setCenter(m.getPosition());
    }


    @Override
    public void loadJSON(JSONObject obj) {
        try {
            GUID=obj.getString("GUID");
            Name=obj.getString("PlayerName");
            Gold=obj.getInt("Gold");
            City=obj.getString("City");

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void RemoveObject() {

    }

    @Override
    public String getInfo() {
        String result= ResourceString.getInstance().getString("name")+this.Name+"\n"+
                "Местожительства:"+this.City+"\n"+
                ResourceString.getInstance().getString("gold")+this.Gold+"\n"+
                ResourceString.getInstance().getString("caravans")+this.Caravans+"\n"+
                ResourceString.getInstance().getString("hirelings")+this.Ambushes+"\n"
                ;
        if (this.Route>0) result+="Прокладывает маршрут.";
        return result;
    }

    @Override
    public ArrayList<ObjectAction> getActions() {
        ArrayList<ObjectAction> Actions=new ArrayList<>();
        ObjectAction act=new ObjectAction() {
            @Override
            public Bitmap getImage() {
                return ImageLoader.getImage("create_ambush");
            }

            @Override
            public String getInfo() {
                return "Если рядом с засадой будет проходить караван, то он будет перехвачен и направлен влаедльцу засады.";
            }

            @Override
            public String getCommand() {
                return "SetAmbush";
            }
        };
        Actions.add(act);
        //if (Route>0) {
            /*act = new ObjectAction() {
                @Override
                public Bitmap getImage() {
                    return ImageLoader.getImage("create_waypoint");
                }

                @Override
                public String getInfo() {
                    return "Создать точку маршрута.";
                }

                @Override
                public String getCommand() {
                    return "createWaypoint";
                }
            };
            Actions.add(act);*/

            act = new ObjectAction() {
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
            };
            Actions.add(act);
        //}

        return Actions;
    }


}
