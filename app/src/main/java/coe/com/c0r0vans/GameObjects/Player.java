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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import coe.com.c0r0vans.GameSound;
import coe.com.c0r0vans.R;
import utility.Essages;
import utility.GPSInfo;
import utility.ImageLoader;
import utility.ResourceString;
import utility.serverConnect;

/**
 * @author Shadilan
 */
public class Player extends GameObject {


    //Fields
    private int Caravans=0;
    private int AmbushesMax=100;
    private int AmbushesLeft=100;
    private int Level=0;
    private int TNL=0;
    private int Exp=0;
    private int MostIn=0;
    private boolean routeStart=false;

    private int AmbushRadius=30;
    private int ActionDistance=50;

    //Arrays
    private ArrayList<Upgrade> Upgrades;
    private ArrayList<Route> Routes;
    private ArrayList<AmbushItem> Ambushes;

    private String currentRoute="";

    public Player() {
        init();
    }

    public Player(GoogleMap mMap){
        setMap(mMap);
        init();
    }

    public void init(){
        image= ImageLoader.getImage("hero");
        Upgrades=new ArrayList<>();
        Routes=new ArrayList<>();
        Ambushes=new ArrayList<>();
        if (dropRoute==null) dropRoute = new ObjectAction(this) {
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
                GameSound.playSound(GameSound.START_ROUTE_SOUND);
            }

            @Override
            public void postAction() {
                serverConnect.getInstance().getPlayerInfo();
                Essages.addEssage("Незаконченый маршрут отменен.");
            }

            @Override
            public void postError() {

            }
        };
    }

    public int getAmbushRad(){return AmbushRadius;}

    private int Gold=0;
    @Override
    public void changeMarkerSize(int Type) {
        mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));
    }

    @Override
    public int getProgress() {
        return (Exp*100/TNL);
    }

    @Override
    public void setVisibility(boolean visibility) {
        mark.setVisible(visibility);
        circle.setVisible(visibility);
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
            if (obj.has("GUID")) GUID=obj.getString("GUID");
            if (obj.has("PlayerName")) Name=obj.getString("PlayerName");
            if (obj.has("Level")) Level=obj.getInt("Level");
            if (obj.has("TNL")) TNL=obj.getInt("TNL");
            if (obj.has("Exp")) Exp=obj.getInt("Exp");
            if (obj.has("Gold")) Gold=obj.getInt("Gold");
            if (obj.has("Caravans")) Caravans=obj.getInt("Caravans");
            if (obj.has("AmbushesMax")) AmbushesMax=obj.getInt("AmbushesMax");
            if (obj.has("AmbushesLeft")) AmbushesLeft=obj.getInt("AmbushesLeft");
            if (obj.has("MostIn")) MostIn=obj.getInt("MostIn");
            if (obj.has("AmbushRadius")) AmbushRadius=obj.getInt("AmbushRadius");
            if (obj.has("ActionDistance")) ActionDistance=obj.getInt("ActionDistance");
            circle.setRadius(ActionDistance);
            if (obj.has("Upgrades")){
                JSONArray upg=obj.getJSONArray("Upgrades");
                Upgrades.clear();
                for (int i=0;i<upg.length();i++) {
                    Upgrades.add(new Upgrade(upg.getJSONObject(i)));

                }
                Log.d("DebugInfo", "ULength" + Upgrades.size());

            }
            if (obj.has("Routes")){
                currentRoute="";
                JSONArray route=obj.getJSONArray("Routes");
                for (Route routel:Routes){
                    routel.RemoveObject();
                }
                Routes.clear();
                for (int i=0;i<route.length();i++) {
                    Route routeObj=new Route(route.getJSONObject(i),map);
                    if (routeObj.getFinishName().equals("null")) currentRoute=routeObj.getStartName();
                    else Routes.add(routeObj);

                }
            }
            if (obj.has("Ambushes")){
                JSONArray ambush=obj.getJSONArray("Ambushes");
                Ambushes.clear();
                for (int i=0;i<ambush.length();i++) Ambushes.add(new AmbushItem(ambush.getJSONObject(i)));
            }
            routeStart = currentRoute.equals("");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        change(OnGameObjectChange.EXTERNAL);
    }

    @Override
    public void RemoveObject() {

    }

    @Override
    public String getInfo() {
        return ResourceString.getInstance().getString("name")+this.Name+"\n"+
                ResourceString.getInstance().getString("gold")+this.Gold+"\n"+
                ResourceString.getInstance().getString("caravans")+this.Caravans+"\n";
    }

    private  ObjectAction createAmbush;
    private ObjectAction dropRoute;
    @Override
    public ArrayList<ObjectAction> getActions() {
        ArrayList<ObjectAction> Actions=new ArrayList<>();
        if (createAmbush==null) createAmbush=new ObjectAction(this) {
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

            @Override
            public void preAction() {

            }

            @Override
            public void postAction() {
                GameSound.playSound(GameSound.SET_AMBUSH);
                serverConnect.getInstance().RefreshCurrent();
                Essages.addEssage("Засада установлена.");
            }

            @Override
            public void postError() {

            }
        };
        if (createAmbush.isEnabled()) Actions.add(createAmbush);


        if (dropRoute==null) dropRoute = new ObjectAction(this) {
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

            }

            @Override
            public void postAction() {
                GameSound.playSound(GameSound.START_ROUTE_SOUND);
                serverConnect.getInstance().getPlayerInfo();
                Essages.addEssage("Незаконченый маршрут отменен.");
            }

            @Override
            public void postError() {

            }
        };
        //if (false) Actions.add(dropRoute);


        return Actions;
    }


    public int getLevel() {
        return Level;
    }

    public int getExp() {
        return Exp;
    }

    public int getTNL() {
        return TNL;
    }

    public int getGold() {
        return Gold;
    }


    public int getCaravans() {
        return Caravans;
    }

    public int getAmbushLeft() {
        return AmbushesLeft;
    }

    public int getAmbushMax() {
        return AmbushesMax;
    }

    public int getMostReachIn() {
        return MostIn;
    }

    public ArrayList<Upgrade> getUpgrades() {
        return Upgrades;
    }

    public ArrayList<Route> getRoutes() {
        return Routes;
    }
    public ArrayList<AmbushItem> getAmbushes() {return Ambushes;}
    public int getActionDistance(){return ActionDistance;}
    public String getCurrentRoute(){return currentRoute;}
    public ObjectAction getDropRoute(){return dropRoute;}

    public void setCurrentRoute(String currentRoute) {
        this.currentRoute = currentRoute;
        change(OnGameObjectChange.PLAYER);
    }

    public void setRouteStart(boolean routeStart) {
        this.routeStart = routeStart;
        change(OnGameObjectChange.PLAYER);
    }

    public boolean getRouteStart() {
        return routeStart;
    }
    public void showRoute(){
        if (Routes!=null){
            for (Route route:Routes){
                route.showRoute();
            }
        }
    }

    private ArrayList<OnGameObjectChange> onChangeList;
    private ArrayList<OnGameObjectChange> removeOnChangeList;
    public void addOnChange(OnGameObjectChange onGameObjectChange){
        if (onChangeList==null) onChangeList=new ArrayList<>();
        onChangeList.add(onGameObjectChange);
    }
    public void removeOnChange(OnGameObjectChange onGameObjectChange){
        if (removeOnChangeList==null) removeOnChangeList=new ArrayList<>();
        removeOnChangeList.add(onGameObjectChange);
    }
    public void change(int type){
        if (onChangeList==null) return;
        if (removeOnChangeList!=null && removeOnChangeList.size()>0){
            onChangeList.removeAll(removeOnChangeList);
            removeOnChangeList.clear();
        }
        for (OnGameObjectChange ev:onChangeList){
            ev.change(type);
        }
    }

    public void setMap(GoogleMap map) {
        this.map = map;

        mark=map.addMarker(new MarkerOptions().position(new LatLng(GPSInfo.getInstance().GetLat() / 1E6, GPSInfo.getInstance().GetLng() / 1E6)));
        CircleOptions circleOptions=new CircleOptions();
        circleOptions.center(new LatLng(GPSInfo.getInstance().GetLat() / 1E6, GPSInfo.getInstance().GetLng() / 1E6));
        circleOptions.radius(ActionDistance);
        circleOptions.strokeColor(Color.parseColor("#D08D2E"));
        circleOptions.strokeWidth(5);
        circle=map.addCircle(circleOptions);
        changeMarkerSize((int) map.getCameraPosition().zoom);
        mark.setAnchor(0.5f, 0.5f);
    }
}
