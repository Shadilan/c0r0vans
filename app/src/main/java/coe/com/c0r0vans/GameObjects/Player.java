package coe.com.c0r0vans.GameObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import coe.com.c0r0vans.OnGameObjectChange;
import coe.com.c0r0vans.R;
import coe.com.c0r0vans.UIElements.ActionView;
import utility.GPSInfo;
import utility.GameSound;
import utility.ImageLoader;
import utility.internet.serverConnect;
import utility.notification.Essages;
import utility.settings.GameSettings;

/**
 * @author Shadilan
 */
public class Player extends GameObject {
    private static Player player;
    private int race=0;
    private String currentRouteGuid="";
    private Route currentR;
    private int trade;

    public static void instance(){
        player=new Player();
    }
    public static Player getPlayer(){
        return player;
    }

    //Fields
    private int Caravans=0;
    private int AmbushesMax=100;
    private int AmbushesLeft=100;
    private int Level=0;
    private int TNL=0;
    private int Exp=0;
    private int MostIn=0;
    private boolean routeStart=false;
    private int Gold=0;

    private int AmbushRadius=30;
    private int ActionDistance=50;

    //Arrays
    private ArrayList<Upgrade> Upgrades;
    private HashMap<String,Upgrade> NextUpgrades;
    private ArrayList<Route> Routes;
    private ArrayList<AmbushItem> Ambushes;

    private String currentRoute="";

    public Player() {
        init();
    }
    public static boolean checkRoute(String guid){
        if (player.currentRouteGuid.equals(guid)) return true;
        if (player.currentRouteGuid.equals("")) return true;
        for (Route r:player.Routes){
            if ((r.getStartGuid().equals(player.currentRouteGuid) && r.getFinishGuid().equals(guid))||
                    ((r.getStartGuid().equals(guid) && r.getFinishGuid().equals(player.currentRouteGuid))))
                return true;

        }
        return false;
    }
    public void init(){
        image= ImageLoader.getImage("hero");
        Upgrades=new ArrayList<>();
        NextUpgrades=new HashMap<>();
        Routes=new ArrayList<>();
        Ambushes=new ArrayList<>();
        if (dropRoute==null) dropRoute = new ObjectAction(this) {
            @Override
            public Bitmap getImage() {
                return ImageLoader.getImage("drop_route");
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
        race=GameSettings.getFaction();

    }

    public int getAmbushRad(){return player.AmbushRadius;}


    @Override
    public void changeMarkerSize() {
        //player.mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));
    }

    @Override
    public int getProgress() {
        return (player.Exp*100/player.TNL);
    }

    @Override
    public void setVisibility(boolean visibility) {
        if (mark!=null) mark.setVisible(false);
        circle.setVisible(visibility);
        circle2.setVisible(visibility);
    }






    @Override
    public Marker getMarker() {
        return mark;
    }
    private Circle circle2;
    /*public Circle getCircle(){
        return circle;
    }*/
    ArrayList<String> lastCity;
    public void setPosition(LatLng target){
        if (mark!=null) mark.setPosition(target);
        if (circle!=null) circle.setCenter(target);
        if (circle2!=null) circle2.setCenter(target);
        float lastRange=this.getActionDistance();
        if (lastCity==null)lastCity=new ArrayList<>();
        ArrayList<String>newLastCity=new ArrayList<>();
        boolean needSignal=false;
        //Если город ближе по дистанции чем город дистанция игрока
        //добавить город в доступные
        // если город не входит в ранее доступные пометить необходимость сигнала
        //очистить список ранее доступных
        //добавит в список ранее доступных доступные города
        //если есть пометка выполнить сигнал.
        for (GameObject o:GameObjects.getInstance().values()){
            if (o instanceof City && o.getMarker()!=null){
                float range=GPSInfo.getDistance(target,o.getMarker().getPosition());
                if (range<=lastRange) {
                    newLastCity.add(o.getGUID());
                    boolean find=false;
                    for (String a:lastCity)if (a.equals(o.getGUID())) find=true;
                    if (!find) needSignal=true;
                }
            }
        }
        if (needSignal) GameSound.playSound(GameSound.GATE_OPEN);
        lastCity.clear();
        lastCity.addAll(newLastCity);
    }
    @Override
    public void setMarker(Marker m) {
        mark=m;
        if (circle!=null) circle.setCenter(m.getPosition());
        else
        {
            CircleOptions circleOptions=new CircleOptions();
            circleOptions.center(m.getPosition());
            circleOptions.radius(ActionDistance);
            circleOptions.zIndex(2);
            circleOptions.strokeColor(Color.parseColor("#D08D2E"));
            circleOptions.strokeWidth(5);
            circle=map.addCircle(circleOptions);

        }
        if (circle2!=null) circle2.setCenter(m.getPosition());
        else
        {
            CircleOptions circleOptions=new CircleOptions();
            circleOptions.center(m.getPosition());
            circleOptions.radius(5);
            circleOptions.strokeColor(Color.parseColor("#FF0000"));
            circleOptions.strokeWidth(5);
            circleOptions.zIndex(2);
            circle2=map.addCircle(circleOptions);
        }
    }

    public JSONObject getJSON() throws JSONException {
        JSONObject result=new JSONObject();
        result.put("GUID",GUID);
        result.put("PlayerName",Name);
        result.put("Level", Level);
        result.put("TNL",TNL);
        result.put("Exp",Exp);
        result.put("Gold",Gold);
        result.put("Caravans",Caravans);
        result.put("AmbushesMax",AmbushesMax);
        result.put("AmbushesLeft",AmbushesLeft);
        result.put("AmbushRadius",AmbushRadius);
        result.put("ActionDistance",ActionDistance);
        result.put("Race",race);
        JSONArray upg=new JSONArray();
        for (Upgrade u:Upgrades){
            upg.put(u.getJSON());
        }
        result.put("Upgrades",upg);
        JSONArray r=new JSONArray();
        for (Route u:Routes){
            r.put(u.getJSON());
        }
        result.put("Routes",r);
        JSONArray a=new JSONArray();
        for (AmbushItem u:Ambushes){
            a.put(u.getJSON());
        }
        result.put("Ambushes", a);
        return result;
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
            if (obj.has("Race")) race=obj.getInt("Race");
            if (race!=0) GameSettings.setFaction(race);
            //TODO: Здесь не должно быть нула. Видимо маркер не инициализуерся в игроке.
            if (circle!=null) circle.setRadius(ActionDistance);
            if (obj.has("Upgrades")){
                JSONArray upg=obj.getJSONArray("Upgrades");
                Upgrades.clear();
                final int upg_length = upg.length();// Moved  upg.length() call out of the loop to local variable upg_length
                for (int i=0;i< upg_length;i++) {
                    Upgrade upgrade=new Upgrade(upg.getJSONObject(i));
                    Upgrades.add(upgrade);
                    if (upgrade.getType().equals("bargain")) trade=upgrade.getEffect1();
                }

            }
            if (obj.has("Routes")){
                currentRoute="";
                currentRouteGuid="";
                JSONArray route=obj.getJSONArray("Routes");
                for (Route routel:Routes){
                    routel.RemoveObject();
                }
                Routes.clear();
                final int route_length = route.length();// Moved  route.length() call out of the loop to local variable route_length
                for (int i=0;i< route_length;i++) {
                    Route routeObj=new Route(route.getJSONObject(i),map);
                    if (routeObj.getFinishName().equals("null") || routeObj.getFinishName()==null) {
                        currentRoute=routeObj.getStartName();
                        currentRouteGuid=routeObj.getStartGuid();
                        currentR=routeObj;
                    }
                    else Routes.add(routeObj);

                }
            }
            if (obj.has("Ambushes")){
                JSONArray ambush=obj.getJSONArray("Ambushes");
                Ambushes.clear();
                final int ambush_length = ambush.length();// Moved  ambush.length() call out of the loop to local variable ambush_length
                for (int i=0;i< ambush_length;i++) Ambushes.add(new AmbushItem(ambush.getJSONObject(i)));
            }
            if (obj.has("NextUpgrades")){
                JSONArray nextUpgrade=obj.getJSONArray("NextUpgrades");
                NextUpgrades.clear();

                final int nextUpgrade_length = nextUpgrade.length();// Moved  nextUpgrade.length() call out of the loop to local variable nextUpgrade_length
                for (int i=0;i< nextUpgrade_length;i++) {
                    Upgrade nUp=new Upgrade(nextUpgrade.getJSONObject(i));
                    NextUpgrades.put(nUp.getType(), nUp);

                }
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



    //private  ObjectAction createAmbush;
    private ObjectAction dropRoute;
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
    public void setAmbushLeft(int AmbushLeft){
        AmbushesLeft=AmbushLeft;
        change(OnGameObjectChange.PLAYER);
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

    /*public void setCurrentRoute(String currentRoute) {
        this.currentRoute = currentRoute;
        change(OnGameObjectChange.PLAYER);
    }*/

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
    //private ArrayList<OnGameObjectChange> removeOnChangeList;
    public void addOnChange(OnGameObjectChange onGameObjectChange){
        if (onChangeList==null) onChangeList=new ArrayList<>();
        onChangeList.add(onGameObjectChange);
    }
/*    public void removeOnChange(OnGameObjectChange onGameObjectChange){
        if (removeOnChangeList==null) removeOnChangeList=new ArrayList<>();
        removeOnChangeList.add(onGameObjectChange);
    }*/
    public void change(int type){
        if (onChangeList==null) return;
        /*if (removeOnChangeList!=null && removeOnChangeList.size()>0){
            onChangeList.removeAll(removeOnChangeList);
            removeOnChangeList.clear();
        }*/
        for (OnGameObjectChange ev:onChangeList){
            ev.change(type);
        }
    }
    public Upgrade getNextUpgrade(String type){
        Log.d("NextUp",type+" "+NextUpgrades.size());

        return NextUpgrades.get(type);
    }

    public void setMap(GoogleMap map) {
        this.map = map;

        mark=map.addMarker(new MarkerOptions().position(new LatLng(GPSInfo.getInstance().GetLat() / 1E6, GPSInfo.getInstance().GetLng() / 1E6)));
        CircleOptions circleOptions=new CircleOptions();
        circleOptions.center(new LatLng(GPSInfo.getInstance().GetLat() / 1E6, GPSInfo.getInstance().GetLng() / 1E6));
        circleOptions.radius(ActionDistance);
        circleOptions.strokeColor(Color.parseColor("#D08D2E"));
        circleOptions.strokeWidth(5);
        circleOptions.zIndex(1);
        circle=map.addCircle(circleOptions);
        circleOptions=new CircleOptions();
        circleOptions.center(new LatLng(GPSInfo.getInstance().GetLat() / 1E6, GPSInfo.getInstance().GetLng() / 1E6));
        circleOptions.radius(5);
        circleOptions.strokeColor(Color.parseColor("#FF0000"));
        circleOptions.strokeWidth(5);
        circle2=map.addCircle(circleOptions);
        changeMarkerSize();
        mark.setAnchor(0.5f, 0.5f);
        mark.setVisible(false);
    }

    public int getRace() {
                return race;
    }

    @Override
    public RelativeLayout getObjectView(Context context) {
        return new ambushCreate(context);

    }


    public int getTrade() {
        return trade;
    }

    public Route getCurrentR() {
        return currentR;
    }

    public void setCurrentRouteGUID(String currentRouteGUID) {
        this.currentRouteGuid = currentRouteGUID;
    }


    class ambushCreate extends RelativeLayout implements GameObjectView{

        public ambushCreate(Context context) {
            super(context);
            init();
        }

        public ambushCreate(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public ambushCreate(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }
        private void init(){
            inflate(this.getContext(),R.layout.longtap_layout,this);
            findViewById(R.id.longtapClose).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    close();
                }
            });
            createAmbush=new ObjectAction(Player.getPlayer()) {
                @Override
                public Bitmap getImage() {
                    return ImageLoader.getImage("create_ambush");
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
                    serverConnect.getInstance().RefreshCurrent();
                    GameSound.playSound(GameSound.SET_AMBUSH);
                    Player.getPlayer().setAmbushLeft(Player.getPlayer().getAmbushLeft() - 1);
                    Essages.addEssage("Засада создана.");

                }

                @Override
                public void postError() {

                }
            };
            findViewById(R.id.createAmbushAction).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverConnect.getInstance().ExecCommand(createAmbush, Player.getPlayer().getGUID(),
                            GPSInfo.getInstance().GetLat(), GPSInfo.getInstance().GetLng(),
                            (int) (SelectedObject.getInstance().getPoint().latitude * 1e6),
                            (int) (SelectedObject.getInstance().getPoint().longitude * 1e6)
                    );
                    close();
                }
            });
        }
        ObjectAction createAmbush;


        @Override
        public void updateInZone(boolean inZone) {
            if (inZone){
                ImageButton btn= (ImageButton) findViewById(R.id.createAmbushAction);
                btn.setVisibility(VISIBLE);
                if (Player.getPlayer().getAmbushLeft()>0){
                    btn.setClickable(true);
                    btn.setEnabled(true);
                    btn.setAlpha(1f);
                } else
                {
                    btn.setClickable(false);
                    btn.setEnabled(false);
                    btn.setAlpha(0.5f);
                }
            }
            else findViewById(R.id.createAmbushAction).setVisibility(INVISIBLE);
        }

        @Override
        public void close() {
            this.setVisibility(GONE);
            actionView.HideView();

        }
        ActionView actionView;
        @Override
        public void setContainer(ActionView av) {
            actionView=av;
        }

        @Override
        public void setDistance(int distance) {

        }
    }
    public int getRadius(){
        return AmbushRadius;

    }
    public void higlight(String city){
        if (city==null) for (Route r:Routes){
             r.releaseFade();
        }
            else
        for (Route r:Routes){
            if (!(city.equals(r.getStartGuid()) || city.equals(r.getFinishGuid()))) r.fadeRoute();
        }
    }
}
