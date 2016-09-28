package com.coe.c0r0vans.Logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.coe.c0r0vans.GameObject.GameObject;
import com.coe.c0r0vans.GameObject.OnGameObjectChange;
import com.coe.c0r0vans.GameObjects.ObjectAction;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.Singles.GameObjects;
import com.coe.c0r0vans.Singles.MyGoogleMap;
import com.coe.c0r0vans.Singles.SelectedObject;
import com.coe.c0r0vans.Singles.ToastSend;
import com.coe.c0r0vans.UIElements.ActionView;
import com.coe.c0r0vans.UIElements.GameObjectView;
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

import utility.GATracker;
import utility.GPSInfo;
import utility.GameSound;
import utility.GameVibrate;
import utility.ImageLoader;
import utility.internet.serverConnect;
import utility.notification.Essages;
import utility.settings.GameSettings;

/**
 * @author Shadilan
 */
public class Player extends GameObject {
    private int race=0;
    private String currentRouteGuid="";
    private Caravan currentR;
    private int trade;
    private int profit=0;
    private int hirelings;

    private int leftToHire;
    private int foundedCities;
    private int cityMax;

    //Fields
    private int Caravans=0;
    private int AmbushesMax=100;
    private int AmbushesLeft=100;
    private int Level=0;
    private int TNL=0;
    private int Exp=0;
    private boolean routeStart=false;
    private int Gold=0;

    private int AmbushRadius=30;
    private int ActionDistance=50;

    //Arrays
    private ArrayList<Upgrade> Upgrades;
    private HashMap<String,Upgrade> NextUpgrades;
    private HashMap<String,Caravan> Routes;
    private HashMap<String,Ambush> Ambushes;

    protected Circle zone1;


    public Player() {
        init();
    }
    public boolean checkRoute(String guid){
        if (guid==null) return false;
        if (guid.equals(currentRouteGuid)) return true;
        if ("".equals(currentRouteGuid)|| currentRouteGuid==null) return false;
        for (Caravan r:Routes.values()){
            if (r!=null && ((currentRouteGuid.equals(r.getStartGUID()) && guid.equals(r.getFinishGUID()))||
                    ((guid.equals(r.getStartGUID()) && currentRouteGuid.equals(r.getFinishGUID())))))
                return true;

        }
        return false;
    }
    public void init(){
        //image= ImageLoader.getImage("hero");
        Upgrades=new ArrayList<>();
        NextUpgrades=new HashMap<>();
        Routes=new HashMap<>();
        Ambushes=new HashMap<>();
        owner=true;
        if (dropRoute==null) dropRoute = new ObjectAction(this) {
            @Override
            public Bitmap getImage() {
                return ImageLoader.getImage("drop_route");
            }

            @Override
            public String getCommand() {
                return "DropUnfinishedRoute";
            }
            private String cancelRouteGuid;
            private Caravan cancelRoute;
            @Override
            public void preAction() {
                cancelRouteGuid=currentRouteGuid;
                cancelRoute=currentR;
                GameSound.playSound(GameSound.START_ROUTE_SOUND);
                GameObjects.getPlayer().setCurrentRouteGUID(null);
                GameObjects.getPlayer().setRouteStart(true);
                for (GameObject o: GameObjects.getInstance().values()){
                    if (o!=null && o instanceof City) ((City) o).updateColor();
                }
            }

            @Override
            public void postAction(JSONObject response) {
                Essages.addEssage("Незаконченый маршрут отменен.");
            }
            @Override
            public void postError(JSONObject response) {
                try {
                    String err;
                    if (response.has("Error")) err = response.getString("Error");
                    else if (response.has("Result")) err = response.getString("Result");
                    else err = "U0000";
                    switch (err) {
                        case "DB001":
                            Essages.addEssage("Ошибка сервера.");
                            currentRouteGuid=cancelRouteGuid;
                            currentR=cancelRoute;
                            GameObjects.getPlayer().setRouteStart(true);
                            break;
                        case "L0001":
                            Essages.addEssage("Соединение потеряно.");
                            currentRouteGuid=cancelRouteGuid;
                            currentR=cancelRoute;
                            GameObjects.getPlayer().setRouteStart(true);
                            break;
                        case "O0801":
                            Essages.addEssage("Маршрут не найден.");
                            break;
                        default:
                            currentRouteGuid=cancelRouteGuid;
                            currentR=cancelRoute;
                            GameObjects.getPlayer().setRouteStart(true);
                            if (response.has("Message"))
                                Essages.addEssage(response.getString("Message"));
                            else Essages.addEssage("Непредвиденная ошибка.");

                    }
                }catch (JSONException e)
                {
                    GATracker.trackException("DestroyAmbush",e);
                }
                String message="";

                try {
                    /*if (response.has("Error"))
                        error = response.getString("Error");*/
                    if (response.has("Message"))
                        message=response.getString("Message");
                } catch (JSONException e) {
                    GATracker.trackException("DropRoute",e);
                }
                Essages.addEssage(message);
            }
        };
        race=GameSettings.getFaction();

    }

    public int getAmbushRad(){return AmbushRadius;}


    @Override
    public void changeMarkerSize() {
        //player.mark.setIcon(BitmapDescriptorFactory.fromResource(R.mipmap.marker));
        if (zone!=null) {
            if (MyGoogleMap.getClientZoom() == GameObject.ICON_SMALL) {
                zone.setStrokeWidth(3);
                zone1.setStrokeWidth(3+2);
            } else if (MyGoogleMap.getClientZoom() == GameObject.ICON_LARGE) {
                zone.setStrokeWidth(9);
                zone1.setStrokeWidth(9+2);
            } else {
                zone.setStrokeWidth(5);
                zone1.setStrokeWidth(5+2);
            }
        }
        for (GameObject obj : this.getAmbushes().values())
            if (obj.getMarker() != null) obj.changeMarkerSize();
        for (GameObject obj : this.getRoutes().values())
            if (obj.getMarker() != null) obj.changeMarkerSize();



    }

    @Override
    public int getProgress() {
        return (Exp*100/TNL);
    }

    @Override
    public void setVisibility(boolean visibility) {
        if (mark!=null) mark.setVisible(false);
        zone.setVisible(visibility);
        zone1.setVisible(visibility);
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
        if (zone!=null) zone.setCenter(target);
        if (zone1!=null) zone1.setCenter(target);
        if (circle2!=null) circle2.setCenter(target);
        float lastRange=this.getActionDistance();
        if (lastCity==null)lastCity=new ArrayList<>();
        ArrayList<String>newLastCity=new ArrayList<>();
        boolean needSignal=false;
        int signal=0;
        //Если город ближе по дистанции чем город дистанция игрока
        //добавить город в доступные
        // если город не входит в ранее доступные пометить необходимость сигнала
        //очистить список ранее доступных
        //добавит в список ранее доступных доступные города
        //если есть пометка выполнить сигнал.
        //todo:Сделать сохранение информации о нотифицированном объекте некоторое время.
        for (GameObject o:GameObjects.getInstance().values()){
            if ((o instanceof City && o.getMarker()!=null)||
                (o instanceof Ambush && o.getMarker()!=null &&
                        ((Ambush)o).getFaction()!=0 &&
                        ((Ambush)o).getFaction()!=getRace()))
            {
                float range=GPSInfo.getDistance(target,o.getMarker().getPosition());
                if (range<=lastRange) {
                    newLastCity.add(o.getGUID());
                    boolean find=false;
                    for (String a:lastCity)if (a.equals(o.getGUID())) find=true;
                    if (!find) {
                        needSignal=true;
                        if (o instanceof City) signal=GameSound.GATE_OPEN;
                        else signal=GameSound.ROGUE_CAMP;
                    }
                }
            }
        }
        if (needSignal) {
            GameSound.playSound(signal);
            GameVibrate.vibrate();
        }
        lastCity.clear();
        lastCity.addAll(newLastCity);
        changeMarkerSize();
    }
    @Override
    public void setMarker(Marker m) {
        mark=m;
        if (zone!=null) zone.setCenter(m.getPosition());
        else
        {
            CircleOptions circleOptions=new CircleOptions();
            circleOptions.center(m.getPosition());
            circleOptions.radius(ActionDistance);
            circleOptions.zIndex(2);
            circleOptions.strokeColor(Color.parseColor("#D08D2E"));
            circleOptions.strokeWidth(5);
            zone=map.addCircle(circleOptions);

        }
        if (zone1!=null) zone1.setCenter(m.getPosition());
        else
        {
            CircleOptions circleOptions=new CircleOptions();
            circleOptions.center(m.getPosition());
            circleOptions.radius(ActionDistance);
            circleOptions.zIndex(1);
            circleOptions.strokeColor(Color.BLACK);
            circleOptions.strokeWidth(5+2);
            zone1=map.addCircle(circleOptions);
        }

        if (circle2!=null) circle2.setCenter(m.getPosition());
        else
        {
            CircleOptions circleOptions=new CircleOptions();
            circleOptions.center(m.getPosition());
            circleOptions.radius(5);
            circleOptions.strokeColor(Color.parseColor("#FF0000"));
            circleOptions.strokeWidth(5);
            circleOptions.zIndex(200);
            circle2=map.addCircle(circleOptions);
        }
        changeMarkerSize();
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
        result.put("Hirelings",hirelings);
        result.put("LeftToHire",leftToHire);
        result.put("FoundedCities",foundedCities);
        result.put("Race",race);
        JSONArray upg=new JSONArray();
        for (Upgrade u:Upgrades){
            upg.put(u.getJSON());
        }
        result.put("Upgrades",upg);
        upg=new JSONArray();
        for (Upgrade u:NextUpgrades.values()){
            upg.put(u.getJSON());
        }
        result.put("NextUpgrades",upg);

        JSONArray r=new JSONArray();
        for (Caravan u:Routes.values()){
            r.put(u.getJSON());
        }
        result.put("Routes",r);
        JSONArray a=new JSONArray();
        for (Ambush u:Ambushes.values()){
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
            if (obj.has("AmbushRadius")) AmbushRadius=obj.getInt("AmbushRadius");
            if (obj.has("AmbushRadius")) AmbushRadius=obj.getInt("AmbushRadius");
            if (obj.has("ActionDistance")) ActionDistance=obj.getInt("ActionDistance");
            if (obj.has("Race")) race=obj.getInt("Race");
            //if (race==0) Essages.addEssage("Фракция не выбрана.");
            if (obj.has("Hirelings")) hirelings=obj.getInt("Hirelings"); else hirelings=100;
            if (obj.has("LeftToHire")) leftToHire=obj.getInt("LeftToHire"); else leftToHire=100;
            if (obj.has("FoundedCities")) foundedCities=obj.getInt("FoundedCities"); else foundedCities=0;
            if (race!=0) GameSettings.setFaction(race);
            //TODO: Здесь не должно быть нула. Видимо маркер не инициализуерся в игроке.
            if (zone!=null) zone.setRadius(ActionDistance);
            if (zone1!=null) zone1.setRadius(ActionDistance);



            if (obj.has("Upgrades")){
                JSONArray upg=obj.getJSONArray("Upgrades");
                Upgrades.clear();
                final int upg_length = upg.length();// Moved  upg.length() call out of the loop to local variable upg_length
                for (int i=0;i< upg_length;i++) {
                    Upgrade upgrade=new Upgrade(upg.getJSONObject(i));
                    Upgrades.add(upgrade);
                    if (upgrade.getType().equals("bargain")) trade=upgrade.getEffect1();
                    if (upgrade.getType().equals("founder")) cityMax=upgrade.getEffect1();
                }

            }
            profit=0;
            if (obj.has("Routes")){
                currentRouteGuid="";
                currentR=null;
                setRouteStart(false);
                JSONArray jroutes=obj.getJSONArray("Routes");
                ArrayList<String> GUIDS=new ArrayList<>(Routes.keySet());



                final int route_length = jroutes.length();// Moved  route.length() call out of the loop to local variable route_length
                for (int i=0;i< route_length;i++) {
                    JSONObject jobj=jroutes.getJSONObject(i);
                    String newGUID=jobj.getString("GUID");
                    Caravan newObj=Routes.get(newGUID);
                    if (newObj!=null) {
                        newObj.loadJSON(jobj);
                        GUIDS.remove(newGUID);
                    }
                    else {
                        newObj=new Caravan(map,jobj);
                        newObj.setFaction(0);
                        if (newObj.getFinishName()==null || newObj.getFinishName().equals("null") ) {
                            currentRouteGuid=newObj.getStartGUID();
                            currentR=newObj;
                            setRouteStart(true);
                        } else Routes.put(newGUID,newObj);
                    }


                    profit+=newObj.getProfit();
                }
                for (String lGUID:GUIDS){
                    Routes.get(lGUID).RemoveObject();
                    Routes.remove(lGUID);
                }

            }
            if (obj.has("Ambushes")){
                JSONArray jambushs=obj.getJSONArray("Ambushes");
                ArrayList<String> GUIDS=new ArrayList<>(Ambushes.keySet());


                /*for (Caravan routel:Routes){
                    routel.RemoveObject();
                }
                Routes.clear();*/
                final int ambush_length = jambushs.length();// Moved  route.length() call out of the loop to local variable route_length
                for (int i=0;i< ambush_length;i++) {
                    JSONObject jobj=jambushs.getJSONObject(i);
                    String newGUID=jobj.getString("GUID");
                    Ambush newObj=Ambushes.get(newGUID);
                    if (newObj!=null) {
                        newObj.loadJSON(jobj);
                        GUIDS.remove(newGUID);
                    }
                    else {
                        newObj=new Ambush(map,jobj);
                        Ambushes.put(newGUID,newObj);
                        GameObjects.putActive(newObj);
                    }
                }
                for (String lGUID:GUIDS){
                    Ambush rem=Ambushes.get(lGUID);
                    GameObjects.removeActive(rem);
                    rem.RemoveObject();
                    Ambushes.remove(lGUID);
                }

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
            routeStart=(currentRouteGuid ==null || currentRouteGuid.equals(""));

            //TODO Слишком много одинаковых вызовов.
            for (GameObject o:GameObjects.getInstance().values()){
                if (o!=null && o instanceof City) ((City) o).updateColor();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        changeMarkerSize();
        change(OnGameObjectChange.EXTERNAL);

    }


    @Override
    public LatLng getPosition() {
        return GPSInfo.getInstance().getLatLng();
    }
    public void removeAmbush(String guid){
        Ambush sa=Ambushes.get(guid);
        setHirelings(hirelings+sa.getLife());
        setAmbushLeft(AmbushesLeft+1);
        sa.RemoveObject();
        Ambushes.remove(guid);
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



    public ArrayList<Upgrade> getUpgrades() {
        return Upgrades;
    }

    public HashMap<String,Caravan> getRoutes() {
        return Routes;
    }
    public void putObject(GameObject object){
        if (object instanceof Ambush){
            if (Ambushes.get(object.getGUID())!=null) return;
            Ambushes.put(object.getGUID(), (Ambush) object);
            GameObjects.putActive(object);
        } else if (object instanceof Caravan){
            if (Routes.get(object.getGUID())!=null) return;
            Routes.put(object.getGUID(), (Caravan) object);
        }
    }
    public HashMap<String,Ambush> getAmbushes() {return Ambushes;}
    public int getActionDistance(){return ActionDistance;}
    public ObjectAction getDropRoute(){return dropRoute;}

    public void setRouteStart(boolean routeStart) {
        this.routeStart = routeStart;
        change(OnGameObjectChange.PLAYER);
    }

    public boolean getRouteStart() {
        return routeStart;
    }
    public void showRoute(){
        if (Routes!=null){
            for (Caravan route:Routes.values()){
                route.showRoute();
            }
        }
    }


    public Upgrade getNextUpgrade(String type){


        return NextUpgrades.get(type);
    }
    public Upgrade getUpgrade(String type){
        if (Upgrades==null) return null;
        for (Upgrade u:Upgrades) if(u.getType().equals(type)) return u;
        return null;
    }

    public void setMap(GoogleMap map) {
        this.map = map;
        //Todo Дваждый описано создание объектов видимо не правильно, надо вынести
        mark=map.addMarker(new MarkerOptions().position(new LatLng(GPSInfo.getInstance().GetLat() / 1E6, GPSInfo.getInstance().GetLng() / 1E6)));
        CircleOptions circleOptions=new CircleOptions();
        circleOptions.center(new LatLng(GPSInfo.getInstance().GetLat() / 1E6, GPSInfo.getInstance().GetLng() / 1E6));
        circleOptions.radius(ActionDistance);
        circleOptions.strokeColor(Color.parseColor("#D08D2E"));
        circleOptions.strokeWidth(6);
        circleOptions.zIndex(2);
        zone=map.addCircle(circleOptions);
        circleOptions=new CircleOptions();
        circleOptions.center(new LatLng(GPSInfo.getInstance().GetLat() / 1E6, GPSInfo.getInstance().GetLng() / 1E6));
        circleOptions.radius(5);
        circleOptions.strokeColor(Color.parseColor("#FF0000"));
        circleOptions.strokeWidth(5);
        circleOptions.zIndex(200);
        circle2=map.addCircle(circleOptions);
        circleOptions.center(new LatLng(GPSInfo.getInstance().GetLat() / 1E6, GPSInfo.getInstance().GetLng() / 1E6));
        circleOptions.radius(ActionDistance);
        circleOptions.strokeColor(Color.BLACK);
        circleOptions.strokeWidth(5+2);
        circleOptions.zIndex(1);
        zone1=map.addCircle(circleOptions);

        changeMarkerSize();
        mark.setAnchor(0.5f, 0.5f);
        mark.setVisible(false);
        for (Caravan caravan:Routes.values()){
            caravan.setMap(map);
        }
        for (Ambush ambush:Ambushes.values()){
            ambush.setMap(map);
        }
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

    public Caravan getCurrentR() {
        return currentR;
    }

    public void setCurrentRouteGUID(String currentRouteGUID) {
        this.currentRouteGuid = currentRouteGUID;
    }

    public int getProfit() {
        return profit;
    }

    public int getHirelings() {
        return hirelings;
    }

    public int getLeftToHire() {
        return leftToHire;
    }

    public void setGold(int gold) {
        this.Gold = gold;
        change(OnGameObjectChange.INTERNAL);
    }

    public void setHirelings(int hirelings) {
        this.hirelings = hirelings;
        change(OnGameObjectChange.EXTERNAL);
    }

    public int getFoundedCities() {
        return foundedCities;
    }

    public void setRace(int race) {
        this.race = race;
    }

    public HashMap<String, Upgrade> getNextUpgrades() {
        return NextUpgrades;
    }

    public String getCurrentRouteGUID() {
        return currentRouteGuid;
    }

    public void setCurrentRoute(Caravan currentRoute) {
        this.currentR = currentRoute;
    }

    public void setLeftToHire(int leftToHire) {
        this.leftToHire = leftToHire;

    }

    public int getCityMax() {
        return cityMax;
    }


    class ambushCreate extends RelativeLayout implements GameObjectView {

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
            createAmbush=new ObjectAction(GameObjects.getPlayer()) {
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
                    Upgrade up=GameObjects.getPlayer().getUpgrade("ambushes");
                    if (up!=null) {
                        GameObjects.getPlayer().setHirelings(GameObjects.getPlayer().getHirelings() - up.getEffect2() * 10);
                        GameObjects.getPlayer().setAmbushLeft(GameObjects.getPlayer().getAmbushLeft() - 1);
                    }
                }

                @Override
                public void postAction(JSONObject response) {

                    GameSound.playSound(GameSound.SET_AMBUSH);


                    Essages.addEssage("Засада создана.");
                    if (response.has("Ambush")){

                        try {
                            Ambush ambush=new Ambush(MyGoogleMap.getMap(),response.getJSONObject("Ambush"));
                            GameObjects.getPlayer().getAmbushes().put(ambush.getGUID(),ambush);
                            GameObjects.putActive(ambush);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else
                    serverConnect.getInstance().callScanRange();
                }

                @Override
                public void postError(JSONObject response) {
                    //String error="";


                    try {
                        Upgrade up=GameObjects.getPlayer().getUpgrade("ambushes");
                        if (up!=null) {
                            GameObjects.getPlayer().setHirelings(GameObjects.getPlayer().getHirelings() + up.getEffect2() * 5);
                            GameObjects.getPlayer().setAmbushLeft(GameObjects.getPlayer().getAmbushLeft() + 1);
                        }
                        String err;
                        if (response.has("Error")) err=response.getString("Error");
                        else if (response.has("Result")) err=response.getString("Result");
                        else err="U0000";
                        switch (err){
                            case "DB001":
                                Essages.addEssage("Ошибка сервера.");
                                break;
                            case "L0001":
                                Essages.addEssage("Соединение потеряно.");
                                break;
                            case "O0201":
                                Essages.addEssage("Неподходящее место для засады.");
                                break;
                            case "O0202":
                                Essages.addEssage("Засада слишком далеко.");
                                break;
                            case "O0203":
                                Essages.addEssage("Все засады уже установлены.");
                                ToastSend.send("Засады расставлены.");
                                break;
                            case "O0204":
                                Essages.addEssage("Не хватает наемников.");
                                ToastSend.send("Не достаточно наемников.");
                                break;
                            default:
                                if (response.has("Message")) Essages.addEssage(response.getString("Message"));
                                else Essages.addEssage("Непредвиденная ошибка.");

                        }
                    } catch (JSONException e) {
                        GATracker.trackException("CreateAmbush",e);
                    }

                }
            };
            findViewById(R.id.createAmbushAction).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverConnect.getInstance().callSetAmbush(createAmbush,
                            GPSInfo.getInstance().GetLat(), GPSInfo.getInstance().GetLng(),
                            (int) (SelectedObject.getInstance().getPoint().latitude * 1e6),
                            (int) (SelectedObject.getInstance().getPoint().longitude * 1e6)
                    );
                    close();
                }
            });
            createCity=new ObjectAction(GameObjects.getPlayer()) {
                LatLng coord=new LatLng(SelectedObject.getInstance().getPoint().latitude,SelectedObject.getInstance().getPoint().longitude);
                @Override
                public Bitmap getImage() {
                    return ImageLoader.getImage("create_city");
                }

                @Override
                public String getCommand() {
                    return "CreateCity";
                }

                @Override
                public void preAction() {

                }

                @Override
                public void postAction(JSONObject response) {
                    int distance= (int) GPSInfo.getDistance(GPSInfo.getInstance().getLatLng(),coord);
                    foundedCities++;
                    Essages.addEssage("Поселение было создано в "+ distance+" метрах.");
                    Essages.addEssage("Вы можете создать еще "+ (cityMax-foundedCities)+" поселений.");
                    if (response.has("City")){
                        try {
                            City city=new City(MyGoogleMap.getMap(),response.getJSONObject("City"));
                            city.setFounder(GameObjects.getPlayer().getName());
                            city.setOwner(true);
                            GameObjects.put(city);
                        } catch (JSONException e) {
                            GATracker.trackException("CreateCity","PostAction Error.");
                        }
                    }
                    change(OnGameObjectChange.EXTERNAL);
                }

                @Override
                public void postError(JSONObject response) {
                    try {
                        Upgrade up=GameObjects.getPlayer().getUpgrade("ambushes");
                        if (up!=null) {
                            GameObjects.getPlayer().setHirelings(GameObjects.getPlayer().getHirelings() + up.getEffect2() * 5);
                            GameObjects.getPlayer().setAmbushLeft(GameObjects.getPlayer().getAmbushLeft() + 1);
                        }
                        String err;
                        if (response.has("Error")) err=response.getString("Error");
                        else if (response.has("Result")) err=response.getString("Result");
                        else err="U0000";
                        switch (err){
                            case "DB001":
                                Essages.addEssage("Ошибка сервера.");
                                break;
                            case "L0001":
                                Essages.addEssage("Соединение потеряно.");
                                break;
                            case "O1201":
                                Essages.addEssage("Точка слишком далеко.");
                                break;
                            case "O1202":
                                Essages.addEssage("Неподходящее место для создания поселения.");
                                break;
                            case "O1203":
                                Essages.addEssage("Нет возможности создавать поселения. (Улучшите умение \"Основание городов\")");
                                break;
                            default:
                                if (response.has("Message")) Essages.addEssage(response.getString("Message"));
                                else Essages.addEssage("Непредвиденная ошибка.");

                        }
                    } catch (JSONException e) {
                        GATracker.trackException("CreateAmbush",e);
                    }

                }


            };
            findViewById(R.id.createCity).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverConnect.getInstance().createCity(createCity,
                            GPSInfo.getInstance().GetLat(), GPSInfo.getInstance().GetLng(),
                            (int) (SelectedObject.getInstance().getPoint().latitude * 1e6),
                            (int) (SelectedObject.getInstance().getPoint().longitude * 1e6)
                    );
                    close();
                }
            });
        }
        ObjectAction createAmbush;
        ObjectAction createCity;


        @Override
        public void updateInZone(boolean inZone) {
            if (inZone){
                Upgrade up=getUpgrade("ambushes");
                int cnt=10;
                if (up!=null) cnt=up.getEffect2();
                ImageButton btn= (ImageButton) findViewById(R.id.createAmbushAction);
                btn.setVisibility(VISIBLE);
                if (GameObjects.getPlayer().getAmbushLeft()>0 && GameObjects.getPlayer().getHirelings()>=cnt){
                    btn.setClickable(true);
                    btn.setEnabled(true);
                    btn.setAlpha(1f);
                } else
                {
                    btn.setClickable(false);
                    btn.setEnabled(false);
                    btn.setAlpha(0.5f);
                }
                btn= (ImageButton) findViewById(R.id.createCity);
                btn.setVisibility(VISIBLE);
                boolean allow=GameObjects.getPlayer().getCityMax()>GameObjects.getPlayer().getFoundedCities();
                if (allow) {
                    for (GameObject o : GameObjects.getInstance().values()) {
                        if (o instanceof City && o.getMarker() != null) {
                            float dist = 125;
                            //float mapper = 0;
                            if (((City) o).getOwner()) dist = 250;
                            up = GameObjects.getPlayer().getUpgrade("founder");
                            if (up != null) dist += up.getEffect2();
                            else dist += 125;
                        /*double tlat=SelectedObject.getInstance().getPoint().latitude;
                        double tlng=SelectedObject.getInstance().getPoint().longitude;
                        double lat=o.getMarker().getPosition().latitude;
                        double lng=o.getMarker().getPosition().longitude;
                        double delta_lat=Math.asin((180/3.1415926)*(dist-mapper)/(6378137)); //это 375 минус апгрейд картографера метров
                        double delta_lng=Math.asin((180/3.1415926)*(375-mapper)/(6378137*Math.cos((tlat/1000000)*3.1415926/180)));
                        if ((tlat>lat-delta_lat && tlat<lat+delta_lat)
                                && (tlng>lng-delta_lng && tlng<lng+delta_lng))
                        {
                            allow=false;
                        }*/
                            if (o.getMarker() != null && GPSInfo.getDistance(o.getMarker().getPosition(), SelectedObject.getInstance().getPoint()) < dist) {
                                allow = false;
                            }
                        }
                    }
                }
                if (allow){
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
            else {
                findViewById(R.id.createAmbushAction).setVisibility(INVISIBLE);
                findViewById(R.id.createCity).setVisibility(INVISIBLE);
            }
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
        if (city==null) for (Caravan r:Routes.values()){
             r.releaseFade();
        }
            else
        for (Caravan r:Routes.values()){
            if (!(city.equals(r.getStartGUID()) || city.equals(r.getFinishGUID()))) r.fadeRoute();
        }
    }

}
