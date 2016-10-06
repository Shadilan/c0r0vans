package com.coe.c0r0vans.Logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coe.c0r0vans.GameObject.ActiveObject;
import com.coe.c0r0vans.GameObject.GameObject;
import com.coe.c0r0vans.GameObjects.ObjectAction;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.Singles.GameObjects;
import com.coe.c0r0vans.Singles.MyGoogleMap;
import com.coe.c0r0vans.Singles.SelectedObject;
import com.coe.c0r0vans.Singles.ToastSend;
import com.coe.c0r0vans.UIElements.ActionView;
import com.coe.c0r0vans.UIElements.ConfirmWindow;
import com.coe.c0r0vans.UIElements.GameObjectView;
import com.coe.c0r0vans.UIElements.InfoLayout.CityLine;
import com.coe.c0r0vans.UIElements.UIControler;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import utility.GATracker;
import utility.GPSInfo;
import utility.GameSound;
import utility.ImageLoader;
import utility.StringUtils;
import utility.SwipeDetectLayout.OnSwipeListener;
import utility.SwipeDetectLayout.SwipeDetectLayout;
import utility.internet.serverConnect;
import utility.notification.Essages;
import utility.settings.GameSettings;

/**
 * @author Shadilan
 */
public class City extends GameObject implements ActiveObject {
    private int Level=1;
    protected int radius=50;
    private String upgrade;
    private String upgradeName;
    private long influence1=0;
    private long influence2=0;
    private long influence3=0;
    private boolean owner;
    private Circle buildZone;
    private ObjectAction buyAction;
    private ObjectAction startRouteAction;
    private ObjectAction endRouteAction;
    private ObjectAction startFinishRouteAction;
    private String founder="";
    private int hirelings=100;
    private int hireprice=100;
    private Date updated;
    private Marker addMark;
    private String addMarkName="";
    private Circle zoneAdd;


    private void updateAction(final Context ctx){
        startRouteAction = new ObjectAction(this) {
            String oldRouteGuid;
            Caravan oldRoute;
            boolean routeStart;

            @Override
            public Bitmap getImage() {
                return ImageLoader.getImage("start_route");
            }

            @Override
            public String getCommand() {
                return "StartRoute";
            }

            @Override
            public void preAction() {
                //Todo уменьшить количество наемников
                oldRouteGuid=GameObjects.getPlayer().getCurrentRouteGUID();
                oldRoute=GameObjects.getPlayer().getCurrentR();
                routeStart=GameObjects.getPlayer().getRouteStart();
                GameObjects.getPlayer().setRouteStart(false);
                GameObjects.getPlayer().setCurrentRouteGUID(getGUID());
            }

            @Override
            public void postAction(JSONObject response) {
                GameSound.playSound(GameSound.START_ROUTE_SOUND);
                Essages.addEssage(String.format(ctx.getResources().getString(R.string.route_started), Name));
                if (response.has("Route")){
                    try {
                        GameObjects.getPlayer().setCurrentRoute(new Caravan(MyGoogleMap.getMap(),response.getJSONObject("Route")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                serverConnect.getInstance().callGetPlayerInfo();
                for (GameObject o: GameObjects.getInstance().values()){
                    if (o!=null && o instanceof City) ((City) o).updateColor();
                }
            }

            @Override
            public void postError(JSONObject response) {
                //todo вернуть количество наемников
                try {
                    GameObjects.getPlayer().setCurrentRouteGUID(oldRouteGuid);
                    GameObjects.getPlayer().setRouteStart(true);
                    GameObjects.getPlayer().setCurrentRoute(oldRoute);
                    String err;
                    if (response.has("Error")) err = response.getString("Error");
                    else if (response.has("Result")) err = response.getString("Result");
                    else err = "U0000";
                    switch (err) {
                        case "DB001":
                            Essages.addEssage("Ошибка сервера.");
                            break;
                        case "L0001":
                            Essages.addEssage("Соединение потеряно.");
                            GameObjects.getPlayer().setRouteStart(true);
                            break;
                        case "O0501":
                            Essages.addEssage("Город не найден.");
                            break;
                        case "O0502":
                            Essages.addEssage("Город далеко.");
                            break;
                        case "O0503":
                            Essages.addEssage("Маршрут незакончен.");
                            break;
                        default:
                            GameObjects.getPlayer().setRouteStart(true);
                            if (response.has("Message"))
                                Essages.addEssage(response.getString("Message"));
                            else Essages.addEssage("Непредвиденная ошибка.");

                    }
                }catch (JSONException e)
                {
                    GATracker.trackException("StartRoute",e);
                }

            }
            @Override
            public void serverError(){
                GameObjects.getPlayer().setRouteStart(true);
            }
        };
        endRouteAction = new ObjectAction(this) {
            String oldRouteGuid;
            Caravan oldRoute;
            boolean routeStart;

            @Override
            public Bitmap getImage() {
                return ImageLoader.getImage("end_route");
            }

            @Override
            public String getCommand() {
                return "FinishRoute";
            }

            @Override
            public void preAction() {
                oldRouteGuid=GameObjects.getPlayer().getCurrentRouteGUID();
                oldRoute=GameObjects.getPlayer().getCurrentR();
                routeStart=GameObjects.getPlayer().getRouteStart();
                GameObjects.getPlayer().setCurrentRouteGUID("");
                GameObjects.getPlayer().setCurrentRoute(null);
                GameObjects.getPlayer().setRouteStart(true);
                updateColor();
            }

            @Override
            public void postAction(JSONObject response) {
                if (oldRoute !=null )
                Essages.addEssage(String.format(ctx.getResources().getString(R.string.route_finish), oldRoute.getStartName(),Name));
                else Essages.addEssage(String.format(ctx.getResources().getString(R.string.route_finish), "",Name));

                GameSound.playSound(GameSound.FINISH_ROUTE_SOUND);
                serverConnect.getInstance().callGetPlayerInfo();

                for (GameObject o:GameObjects.getInstance().values()){
                    if (o!=null && o instanceof City) ((City) o).updateColor();
                }
            }

            @Override
            public void postError(JSONObject response) {
                GameObjects.getPlayer().setRouteStart(routeStart);
                GameObjects.getPlayer().setCurrentRouteGUID(oldRouteGuid);
                GameObjects.getPlayer().setCurrentRoute(oldRoute);
                GameObjects.getPlayer().setRouteStart(false);
                try {

                    String err;
                    if (response.has("Error")) err = response.getString("Error");
                    else if (response.has("Result")) err = response.getString("Result");
                    else err = "U0000";
                    switch (err) {
                        case "DB001":
                            Essages.addEssage("Ошибка сервера.");
                            break;
                        case "L0001":
                            Essages.addEssage("Соединение потеряно.");
                            break;
                        case "O0601":
                            Essages.addEssage("Город не найден.");
                            break;
                        case "O0602":
                            Essages.addEssage("Город далеко.");
                            break;
                        case "O0603":
                            Essages.addEssage("Маршрут не начат.");
                            GameObjects.getPlayer().setRouteStart(true);
                            GameObjects.getPlayer().setCurrentRouteGUID("");
                            GameObjects.getPlayer().setCurrentRoute(null);
                            break;
                        case "O0604":
                            Essages.addEssage("Такой маршрут уже есть.");
                            break;
                        case "O0605":
                            Essages.addEssage("Маршрут начинается в этом городе.");
                            break;
                        case "O0606":
                            String msg;
                            if (response.has("Message")) msg=response.getString("Message");
                            else msg="Не достаточно наемников";
                            Essages.addEssage(msg);
                            ToastSend.send(msg);
                            break;
                        default:
                            if (response.has("Message"))
                                Essages.addEssage(response.getString("Message"));
                            else Essages.addEssage("Непредвиденная ошибка.");

                    }
                    updateColor();
                }catch (JSONException e)
                {
                    GATracker.trackException("FinishRoute",e);
                }

            }
        };
        startFinishRouteAction = new ObjectAction(this) {
            String oldRouteGuid;
            Caravan oldRoute;
            boolean routeStart;
            //TODO: Зачем это?
            @Override
            public Bitmap getImage() {
                return null;
            }

            @Override
            public String getCommand() {
                return "FinishStartRoute";
            }

            @Override
            public void preAction() {
                oldRouteGuid=GameObjects.getPlayer().getCurrentRouteGUID();
                oldRoute=GameObjects.getPlayer().getCurrentR();
                routeStart=GameObjects.getPlayer().getRouteStart();
                GameObjects.getPlayer().setRouteStart(false);
                GameObjects.getPlayer().setCurrentRouteGUID(getGUID());
                updateColor();
            }

            @Override
            public void postAction(JSONObject response) {
                if (oldRoute !=null )
                    Essages.addEssage(String.format(ctx.getResources().getString(R.string.route_finish), oldRoute.getStartName(),Name));
                else Essages.addEssage(String.format(ctx.getResources().getString(R.string.route_finish), "",Name));
                GameSound.playSound(GameSound.START_ROUTE_SOUND);
                Essages.addEssage(String.format(ctx.getResources().getString(R.string.route_started), Name));
                serverConnect.getInstance().callGetPlayerInfo();

                for (GameObject o:GameObjects.getInstance().values()){
                    if (o!=null && o instanceof City) ((City) o).updateColor();
                }
                try {
                    if (response.has("Route")) {
                        JSONArray array=response.getJSONArray("Route");
                        final int array_length = array.length();// Moved  array.length() call out of the loop to local variable array_length
                        for (int i = 0; i< array_length; i++){
                            JSONObject obj=array.getJSONObject(i);
                            String guid="";
                            if (obj.has("GUID")) guid=obj.getString("GUID");
                            if (GameObjects.getPlayer().getCurrentR().getGUID().equals(guid)){
                                GameObjects.getPlayer().setCurrentRoute(new Caravan(MyGoogleMap.getMap(),obj));
                            } else {
                                Caravan caravan=GameObjects.getPlayer().getRoutes().get(guid);
                                if (caravan!=null) caravan.loadJSON(obj);
                                else GameObjects.getPlayer().getRoutes().put(guid,new Caravan(MyGoogleMap.getMap(),obj));
                            }
                        }

                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void postError(JSONObject response) {

                GameObjects.getPlayer().setRouteStart(routeStart);
                GameObjects.getPlayer().setCurrentRouteGUID(oldRouteGuid);
                GameObjects.getPlayer().setCurrentRoute(oldRoute);

                try {

                    String err;
                    if (response.has("Error")) err = response.getString("Error");
                    else if (response.has("Result")) err = response.getString("Result");
                    else err = "U0000";
                    switch (err) {
                        case "DB001":
                            Essages.addEssage("Ошибка сервера.");
                            break;
                        case "L0001":
                            //TODO: Опасное место может надо всетаки отдавать управление при потере токена
                            Essages.addEssage("Соединение потеряно.");
                            break;
                        case "O0601":
                            Essages.addEssage("Город не найден.");
                            break;
                        case "O0602":
                            Essages.addEssage("Город далеко.");
                            break;
                        case "O0604":
                            Essages.addEssage("Такой маршрут уже есть.");
                            break;
                        case "O0605":
                            Essages.addEssage("Маршрут начинается в этом городе.");
                            break;
                        case "O0606":
                            String msg;
                            if (response.has("Message")) msg=response.getString("Message");
                            else msg="Не достаточно наемников";
                            Essages.addEssage(msg);
                            ToastSend.send(msg);
                            break;
                        default:
                            if (response.has("Message"))
                                Essages.addEssage(response.getString("Message"));
                            else Essages.addEssage("Непредвиденная ошибка.");

                    }
                    updateColor();
                }catch (JSONException e)
                {
                    GATracker.trackException("StarFinishRoute",e);
                }


            }
        };
        buyAction = new ObjectAction(this) {
            @Override
            public Bitmap getImage() {
                return ImageLoader.getImage("buy_item");
            }

            int upcost;
            @Override
            public String getCommand() {
                return "BuyUpgrade";
            }

            @Override
            public void preAction() {
                Upgrade up=GameObjects.getPlayer().getNextUpgrade(upgrade);
                if (up!=null) {
                    upcost = (int) (up.getCost() * discount());
                }
                GameObjects.getPlayer().setGold(GameObjects.getPlayer().getGold()-upcost);
            }

            @Override
            public void postAction(JSONObject response) {

                try {
                    if ((response.has("Result") && "OK".equals(response.getString("Result"))) || (!response.has("Result")
                            && !response.has("Error"))){
                        GameSound.playSound(GameSound.BUY_SOUND);

                        if (response.has("Upgrade")){
                            Upgrade n=new Upgrade(response.getJSONObject("Upgrade"));
                            Upgrade r=GameObjects.getPlayer().getUpgrade(n.getType());
                            GameObjects.getPlayer().getUpgrades().remove(r);
                            GameObjects.getPlayer().getUpgrades().add(n);
                        }
                        if (response.has("NextUpgrade")){
                            Upgrade n=new Upgrade(response.getJSONObject("NextUpgrade"));
                            GameObjects.getPlayer().getNextUpgrades().remove(n.getType());
                            GameObjects.getPlayer().getNextUpgrades().put(n.getType(),n);
                        }
                        Upgrade up = GameObjects.getPlayer().getUpgrade(upgrade);
                        if (up != null) Essages.addEssage(String.format(ctx.getResources().getString(R.string.upgrade_bought),up.getName()));
                        else Essages.addEssage(String.format(ctx.getResources().getString(R.string.upgrade_bought), upgrade));
                        serverConnect.getInstance().callScanRange();
                    } else postError(response);


                } catch (JSONException e) {
                    GATracker.trackException("BuyUpgrade","JSONResult error");

                }

            }

            @Override
            public void postError(JSONObject response) {
                try {
                    GameObjects.getPlayer().setGold(GameObjects.getPlayer().getGold()-upcost);
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
                        case "O0701":
                            Essages.addEssage("Город не найден.");
                            break;
                        case "O0702":
                            Essages.addEssage("Город слишком далеко.");
                            break;
                        case "O0703":
                            ToastSend.send("Не хватает золота на оплату обучения.");
                            Essages.addEssage("Не хватает золота на оплату обучения.");
                            break;
                        case "O0704":
                            ToastSend.send("Город слишком мал.");
                            Essages.addEssage("Город слишком мал.");

                            break;
                        case "O0705":
                            ToastSend.send("Не достаточно уровня для изучения умения.");
                            Essages.addEssage("Не достаточно уровня для изучения умения.");
                            break;
                        case "O0706":
                            ToastSend.send("Вы уже обучились максимальному навыку.");
                            Essages.addEssage("Вы уже обучились максимальному навыку.");
                        default:
                            if (response.has("Message")) Essages.addEssage(response.getString("Message"));
                            else Essages.addEssage("Непредвиденная ошибка.");

                    }
                } catch (JSONException e) {
                    GATracker.trackException("BuyUpgrade",e);
                }

            }
        };
    }
    public City(GoogleMap map,JSONObject obj) throws JSONException {
        this.map=map;
        loadJSON(obj);
    }

    @Override
    public void RemoveObject() {
        super.RemoveObject();
        if (buildZone!=null){
            buildZone.remove();
            buildZone=null;
        }
    }

    @Override
    public void setMarker(Marker m) {
        mark=m;
        if (addMark==null){
            addMark=map.addMarker(new MarkerOptions().position(m.getPosition()).icon(ImageLoader.getDescritor("route_finish")).visible(false).anchor(0.5f,1f));
            updateColor();
        }
        changeMarkerSize();

    }
    private LatLng latlng;

    @Override
    public LatLng getPosition() {
        return latlng;
    }
    @Override
    public void loadJSON(JSONObject obj) {
        try {
            update();
            GUID = obj.getString("GUID");
            int Lat = obj.getInt("Lat");
            int Lng = obj.getInt("Lng");
            updated=new Date();
            latlng = new LatLng(Lat / 1e6, Lng / 1e6);

            if (obj.has("Name")) Name = obj.getString("Name");
            if (obj.has("UpgradeType")) upgrade = obj.getString("UpgradeType");
            if (obj.has("UpgradeName")) upgradeName = obj.getString("UpgradeName");
            if (obj.has("Level")) {
                Level = obj.getInt("Level");
                hireprice = (int) (Math.sqrt(Level) * 100);
            }
            if (obj.has("Radius")) radius = obj.getInt("Radius");
            if (obj.has("Progress")) progress = obj.getInt("Progress");
            if (obj.has("Influence1")) influence1 = obj.getLong("Influence1");
            if (obj.has("Influence2")) influence2 = obj.getLong("Influence2");
            if (obj.has("Influence3")) influence3 = obj.getLong("Influence3");
            owner = obj.has("Owner") && obj.getBoolean("Owner");
            if (obj.has("Creator")) founder = obj.getString("Creator");
            if ("null".equals(founder)) founder = "";
            if (obj.has("Hirelings")) hirelings = obj.getInt("Hirelings");


            if (mark == null) {
                setMarker(map.addMarker(new MarkerOptions().position(latlng)));

            } else {
                mark.setPosition(latlng);
                addMark.setPosition(latlng);

            }
            if (zoneAdd==null){
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latlng);
                circleOptions.radius(radius);
                //circleOptions.zIndex(100);
                //circleOptions.visible(false);
                circleOptions.strokeColor(Color.BLACK);
                circleOptions.strokeWidth(2*GameSettings.getMetric()+2);
                zoneAdd = map.addCircle(circleOptions);
            } else {
                zoneAdd.setCenter(latlng);
                zoneAdd.setRadius(radius);
            }

            if (zone == null) {
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latlng);
                circleOptions.radius(radius);
                //circleOptions.zIndex(100);
                //circleOptions.visible(false);
                circleOptions.strokeColor(Color.DKGRAY);

                circleOptions.strokeWidth(2*GameSettings.getMetric());
                zone = map.addCircle(circleOptions);
            } else {
                zone.setCenter(latlng);
                zone.setRadius(radius);
            }


            if (buildZone == null) {
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latlng);
                //circleOptions.visible(false);
                int dist = 125;
                if (owner) dist = 250;
                Upgrade up = GameObjects.getPlayer().getUpgrade("founder");
                if (up != null) dist += up.getEffect2();
                else dist += 75;
                circleOptions.radius(dist);
                //circleOptions.zIndex(0);
                circleOptions.fillColor(0x30ff0000);
                circleOptions.strokeWidth(0);
                buildZone = map.addCircle(circleOptions);
            } else {
                buildZone.setCenter(latlng);
                int dist = 125;
                if (owner) dist = 250;
                Upgrade up = GameObjects.getPlayer().getUpgrade("founder");
                if (up != null) dist += up.getEffect2();
                else dist += 75;
                buildZone.setRadius(dist);
            }
            changeMarkerSize();
            updateColor();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean upgradeAvaible(){
        Upgrade up=GameObjects.getPlayer().getNextUpgrade(upgrade);
        int upcost= getUpgradeCost();

        return !( (up.getReqCityLev() > Level)
                || (upcost >= GameObjects.getPlayer().getGold())
                || (up.getLevel() > GameObjects.getPlayer().getLevel() - 1));
    }
    private float discount(){

        float raceBonus=0;
        long infsum=influence1+influence2+influence3;
        long maxInfluence = Math.max(Math.max(influence1,influence2),influence3);
        float conc=0;

        if (infsum>0) {
                 conc= (3f*maxInfluence-infsum)/(4f*infsum);

            switch (GameObjects.getPlayer().getRace()) {
                case 1:raceBonus=(float)influence1/infsum;
                    break;
                case 2:raceBonus=(float)influence2/infsum;
                    break;
                case 3:raceBonus=(float)influence3/infsum;
                    break;
            }
        }
        return (1f+conc)*((1f-raceBonus/4)*(100f-GameObjects.getPlayer().getTrade())/100f);
    }
    public String getName(){return (Name+" ур."+Level) ;}

    private String currentMarkName;
    @Override
    public void changeMarkerSize() {
        if (mark!=null) {
            String markname = "city";
            int lvl=(this.Level+1)/2;
            if (lvl==0) lvl=1;
            markname = markname + "_"+lvl;
            markname = markname + GameObject.zoomToPostfix(MyGoogleMap.getClientZoom());
            if (!markname.equals(currentMarkName)) {
                mark.setIcon(ImageLoader.getDescritor(markname));
                mark.setAnchor(0.5f, 0.5f);
                currentMarkName=markname;
            }
        }
        if (addMark!=null){
            String markName="route_finish";
            markName=markName+GameObject.zoomToPostfix(MyGoogleMap.getClientZoom());
            if (!markName.equals(addMarkName)){
                addMark.setIcon(ImageLoader.getDescritor(markName));
                /*if ("Y".equals(GameSettings.getInstance().get("USE_TILT")))
                    addMark.setAnchor(0.5f, 0.5f);
                else addMark.setAnchor(0.5f, 0.5f);*/
                addMark.setAnchor(0.5f, 0.5f);
                addMarkName=markName;
            }
        }
        showBuildZone();
        showRadius();
    }

    private boolean visibility=true;
    @Override
    public void setVisibility(boolean visibility) {
        this.visibility=visibility;
        if (mark!=null) mark.setVisible(visibility);
        zone.setVisible(visibility);
        zoneAdd.setVisible(visibility);
    }
    public void showBuildZone(){
        if (buildZone==null) return;
        String opt= GameSettings.getInstance().get("SHOW_BUILD_AREA");
        if (opt.equals("Y")){
            buildZone.setVisible(true);
        } else
        {
            buildZone.setVisible(false);
        }
    }
    public void showRadius(){
        if (zone==null || zoneAdd==null) return;

        String opt= GameSettings.getInstance().get("SHOW_CITY_RADIUS");
        if (opt.equals("Y") && MyGoogleMap.getClientZoom()!=ICON_SMALL){
            zone.setVisible(visibility);
            zoneAdd.setVisible(visibility);
        } else
        {
            zone.setVisible(false);
            zoneAdd.setVisible(false);
        }
    }
    void updateColor(){
        if (!GameObjects.getPlayer().checkRoute(GUID) && !GameObjects.getPlayer().getRouteStart()) addMark.setVisible(visibility);
        else addMark.setVisible(false);
    }
    private long getInfluence1() {
        return influence1;
    }

    private long getInfluence2() {
        return influence2;
    }

    private long getInfluence3() {
        return influence3;
    }

    boolean getOwner() {
        return owner;
    }

    void setOwner(boolean owner) {
        this.owner = owner;
    }

    void setFounder(String founder) {
        this.founder = founder;
    }


    public String getUpgrade() {
        return upgrade;
    }

    public int getLevel() {
        return Level;
    }

    public int getUpgradeCost() {
        int res=0;
        Upgrade up=GameObjects.getPlayer().getNextUpgrade(upgrade);

        if (up!=null) {
            res = (int) (up.getCost() * discount());
        }
        return res;
    }

    private class CityWindow extends SwipeDetectLayout implements GameObjectView,ShowHideForm{
        City city;
        CityWindow self;
        private int currentCount=1;

        public CityWindow(Context context) {
            super(context);
            init();
        }

        public CityWindow(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public CityWindow(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }
        public void init(){
            self=this;
            inflate(this.getContext(), R.layout.city_layout, this);

        }
        boolean loaded=true;


        public void setCity(City city){
            this.city=city;
            if (loaded) applyCity();
        }


        public String getSkillInfo() {
            Upgrade up=GameObjects.getPlayer().getNextUpgrade(upgrade);

            if (up!=null) {
                int upcost= city.getUpgradeCost();

                String dop;
                if (up.getReqCityLev()>Level) dop= String.format(getContext().getString(R.string.city_lvl_required), up.getReqCityLev());
                else if ((upcost)>GameObjects.getPlayer().getGold()) dop= String.format(getContext().getString(R.string.need_more_gold), StringUtils.intToStr(upcost - GameObjects.getPlayer().getGold()));
                else if (up.getLevel()>GameObjects.getPlayer().getLevel()-1) dop= String.format(getContext().getString(R.string.need_higher_lvl), up.getLevel()+1);
                else dop= String.format(getContext().getString(R.string.effect), up.getDescription());

                return String.format(getContext().getString(R.string.price), up.getName(), StringUtils.intToStr(upcost),StringUtils.intToStr((int)(up.getCost()/up.getOUC())),String.valueOf(up.getOUC()), dop);
            }
            else return (String.format(getContext().getString(R.string.unknown_upgrade), upgradeName));
        }
        private void applyCity() {
            ImageButton btn= (ImageButton) findViewById(R.id.buyUpgrade);
            btn.setEnabled(city.upgradeAvaible());
            btn.setImageBitmap(ImageLoader.getImage(city.upgrade + "_buy"));
            findViewById(R.id.closeActionButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    close();
                }
            });
            ((TextView) findViewById(R.id.cityName)).setText(city.getName());

            long sum = city.getInfluence1() + city.getInfluence2() + city.getInfluence3();
            if (sum == 0) sum = 1;

            int inf1 = Math.round(city.getInfluence1() * 100 / sum );
            int inf2 = Math.round(city.getInfluence2() * 100/ sum );
            int inf3 = Math.round(city.getInfluence3() * 100 / sum);
            ((TextView)findViewById(R.id.guildInfCount)).setText(StringUtils.longToStr(city.getInfluence1()));
            ((TextView)findViewById(R.id.allianceInfCount)).setText(StringUtils.longToStr(city.getInfluence2()));
            ((TextView)findViewById(R.id.ligaInfCount)).setText(StringUtils.longToStr(city.getInfluence3()));
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.GuildInf);
            progressBar.setProgress(inf1);
            progressBar.setMax(100);
            progressBar = (ProgressBar) findViewById(R.id.AllianceInf);
            progressBar.setProgress(inf2);
            progressBar.setMax(100);
            progressBar = (ProgressBar) findViewById(R.id.LigaInf);
            progressBar.setProgress(inf3);
            progressBar.setMax(100);

            update();
            ((TextView) findViewById(R.id.cityFounder)).setText(founder);
            updateAction(getContext());
            findViewById(R.id.startRoute).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverConnect.getInstance().callStartRoute(startRouteAction,
                            GPSInfo.getInstance().GetLat(),
                            GPSInfo.getInstance().GetLng(),
                            city.getGUID());
                    if ("Y".equals(GameSettings.getInstance().get("CLOSE_WINDOW")))
                        close();
                    else {
                        updateInZone(true);
                    }
                }


            });


            findViewById(R.id.finishRoute).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverConnect.getInstance().callFinishRoute(endRouteAction,
                            GPSInfo.getInstance().GetLat(),
                            GPSInfo.getInstance().GetLng(),
                            city.getGUID());
                    if ("Y".equals(GameSettings.getInstance().get("CLOSE_WINDOW")))
                        close();
                    else {
                        updateInZone(true);
                    }
                }

            });


            findViewById(R.id.buyUpgrade).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmWindow confirmWindow=new ConfirmWindow(getContext());
                    Upgrade up=GameObjects.getPlayer().getNextUpgrade(upgrade);
                    if (up!=null) {
                        int upcost = (int) (up.getCost() * discount());
                        confirmWindow.setText(String.format(getContext().getString(R.string.accept_buy_upgrade), up.getName(), up.getLevel(), StringUtils.intToStr(upcost)));
                    } else confirmWindow.setText(String.format(getContext().getString(R.string.accept_buy_upgrade_unknown), upgradeName));
                    confirmWindow.setConfirmAction(new Runnable() {
                        @Override
                        public void run() {
                            ObjectAction nbuy=new ObjectAction(city) {
                                @Override
                                public Bitmap getImage() {
                                    return ImageLoader.getImage("buy_item");
                                }

                                int upcost;
                                @Override
                                public String getCommand() {
                                    return "BuyUpgrade";
                                }

                                @Override
                                public void preAction() {
                                    Upgrade up=GameObjects.getPlayer().getNextUpgrade(upgrade);
                                    if (up!=null) {
                                        upcost = (int) (up.getCost() * discount());
                                    }
                                    GameObjects.getPlayer().setGold(GameObjects.getPlayer().getGold()-upcost);
                                    update();

                                }

                                @Override
                                public void postAction(JSONObject response) {

                                    try {
                                        if ((response.has("Result") && "OK".equals(response.getString("Result"))) || (!response.has("Result")
                                                && !response.has("Error"))){
                                            GameSound.playSound(GameSound.BUY_SOUND);


                                            if (response.has("Upgrade")){
                                                Upgrade n=new Upgrade(response.getJSONObject("Upgrade"));
                                                Upgrade r=GameObjects.getPlayer().getUpgrade(n.getType());
                                                GameObjects.getPlayer().getUpgrades().remove(r);
                                                GameObjects.getPlayer().getUpgrades().add(n);
                                            }
                                            if (response.has("NextUpgrade")){
                                                Upgrade n=new Upgrade(response.getJSONObject("NextUpgrade"));
                                                GameObjects.getPlayer().getNextUpgrades().remove(n.getType());
                                                GameObjects.getPlayer().getNextUpgrades().put(n.getType(),n);
                                            }
                                            Upgrade up = GameObjects.getPlayer().getUpgrade(upgrade);
                                            if (up != null) Essages.addEssage(String.format(getContext().getResources().getString(R.string.upgrade_bought),up.getName()));
                                            else Essages.addEssage(String.format(getContext().getResources().getString(R.string.upgrade_bought), upgrade));
                                            update();
                                            serverConnect.getInstance().callScanRange();
                                        } else postError(response);


                                    } catch (JSONException e) {
                                        GATracker.trackException("BuyUpgrade","JSONResult error");
                                    }

                                }

                                @Override
                                public void postError(JSONObject response) {
                                    try {
                                        GameObjects.getPlayer().setGold(GameObjects.getPlayer().getGold()+upcost);
                                        update();
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
                                            case "O0701":
                                                Essages.addEssage("Город не найден.");
                                                break;
                                            case "O0702":
                                                Essages.addEssage("Город слишком далеко.");
                                                break;
                                            case "O0703":
                                                ToastSend.send("Не хватает золота на оплату обучения.");
                                                Essages.addEssage("Не хватает золота на оплату обучения.");
                                                break;
                                            case "O0704":
                                                ToastSend.send("Город слишком мал.");
                                                Essages.addEssage("Город слишком мал.");

                                                break;
                                            case "O0705":
                                                ToastSend.send("Не достаточно уровня для изучения умения.");
                                                Essages.addEssage("Не достаточно уровня для изучения умения.");
                                                break;
                                            case "O0706":
                                                ToastSend.send("Вы уже обучились максимальному навыку.");
                                                Essages.addEssage("Вы уже обучились максимальному навыку.");
                                            default:
                                                if (response.has("Message")) Essages.addEssage(response.getString("Message"));
                                                else Essages.addEssage("Непредвиденная ошибка.");

                                        }
                                    } catch (JSONException e) {
                                        GATracker.trackException("BuyUpgrade",e);
                                    }

                                }
                            };

                            serverConnect.getInstance().callBuyUpgrade(nbuy,
                                    GPSInfo.getInstance().GetLat(),
                                    GPSInfo.getInstance().GetLng(),
                                    city.getGUID()
                                    );
                            if ("Y".equals(GameSettings.getInstance().get("CLOSE_WINDOW")))
                                close();
                            else {
                                updateInZone(true);
                            }
                        }
                    });
                    confirmWindow.show();
                }
            });
            findViewById(R.id.hire_plus).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Проверить что хватает денег
                    countHire(currentCount+1);
                }
            });
            findViewById(R.id.hire_minus).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Проверить что хватает денег
                    countHire(currentCount-1);
                }
            });
            SeekBar seekBar= (SeekBar) findViewById(R.id.hireCount);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (!fromUser) {
                        return;
                    }
                   countHire(progress);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            findViewById(R.id.hirePeople).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ObjectAction hire=new ObjectAction(city) {
                        int amount=currentCount;
                        int gold=0;
                        @Override
                        public Bitmap getImage() {
                            return null;
                        }

                        @Override
                        public String getCommand() {
                            return "HirePeople";
                        }

                        @Override
                        public void preAction() {
                            int priceForOne= (int) (hireprice*discount());
                            gold= (amount)*priceForOne;
                            GameObjects.getPlayer().setGold(GameObjects.getPlayer().getGold()-gold);
                            if (owner instanceof City) ((City) owner).hirelings-=amount;
                            GameObjects.getPlayer().setLeftToHire(GameObjects.getPlayer().getLeftToHire()-amount);
                            update();

                        }

                        @Override
                        public void postAction(JSONObject response) {
                            try {
                                if (response.has("Result") && "OK".equals(response.getString("Result"))) {
                                    GameSound.playSound(GameSound.BUY_SOUND);
                                    GameObjects.getPlayer().setHirelings(GameObjects.getPlayer().getHirelings()+amount);
                                    Essages.addEssage("Нанято "+amount+" чел. за "+gold+" золота.");
                                    serverConnect.getInstance().callScanRange();
                                } else
                                {
                                    postError(response);
                                }
                            } catch (JSONException e) {
                                GATracker.trackException("Hire","PostAction Error JSON");
                            }
                        }

                        @Override
                        public void postError(JSONObject response) {
                            GameObjects.getPlayer().setGold(GameObjects.getPlayer().getGold()+gold);
                            if (owner instanceof City) ((City) owner).hirelings+=amount;
                            GameObjects.getPlayer().setLeftToHire(GameObjects.getPlayer().getLeftToHire()+amount);
                            try {
                            if (response.has("Result")){
                                String err=response.getString("Result");
                                switch (err){
                                    case "L0001":
                                        //todo:Сделать вызов логина;
                                        Essages.addEssage("Связь с сервером потеряна. Перезапустите приложение.");
                                        break;
                                    case "O1301":
                                        Essages.addEssage("Действие не выполнено. Такого города не существует.");
                                        break;
                                    case "O1302":
                                        Essages.addEssage("Вы находитесь слишком далеко от города.");
                                        break;
                                    case "O1303":
                                        Essages.addEssage("Недостаточно денег для покупки.");
                                        break;
                                    case "O1304":
                                        Essages.addEssage("Не хватает лидерства для покупки.");
                                        break;
                                    default:
                                        if (response.has("Message")) Essages.addEssage(response.getString("Message"));
                                        else Essages.addEssage("Неопределенная ошибка");

                                }
                            } else
                            {
                                Essages.addEssage("Неопределенная ошибка");
                            }
                            } catch (JSONException e) {
                                GATracker.trackException("Hire","ErrorAction Error JSON");
                            }
                            update();
                        }
                    };
                    serverConnect.getInstance().hirePeople(hire,GPSInfo.getInstance().GetLat(),GPSInfo.getInstance().GetLng(),city.getGUID(),currentCount);
                    countHire(1);
                    ((TextView)findViewById(R.id.goldInfo)).setText(String.format(getContext().getString(R.string.gold_amount), StringUtils.intToStr(GameObjects.getPlayer().getGold())));
                }
            });
            findViewById(R.id.restart_route).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverConnect.getInstance().callStartFinishRoute(startFinishRouteAction,
                            GPSInfo.getInstance().GetLat(),
                            GPSInfo.getInstance().GetLng(),
                            city.getGUID());
                    if ("Y".equals(GameSettings.getInstance().get("CLOSE_WINDOW")))
                        close();
                    else {
                        updateInZone(true);
                    }
                }
            });
            findViewById(R.id.drop_route).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmWindow confirmWindow=new ConfirmWindow(getContext());
                    confirmWindow.setText(getContext().getString(R.string.approve_route_remove));
                    confirmWindow.setConfirmAction(new Runnable() {
                        @Override
                        public void run() {
                            serverConnect.getInstance().callDropUnfinishedRoute(GameObjects.getPlayer().getDropRoute());
                            updateInZone(true);
                        }
                    });
                    confirmWindow.show();

                }
            });
            findViewById(R.id.marketToggle).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToggleButton b = (ToggleButton) findViewById(R.id.infoToggle);
                    b.setChecked(false);
                    b = (ToggleButton) findViewById(R.id.marketToggle);
                    b.setChecked(true);
                    b = (ToggleButton) findViewById(R.id.routeToggle);
                    b.setChecked(false);
                    findViewById(R.id.buyPanel).setVisibility(VISIBLE);
                    findViewById(R.id.infoPanel).setVisibility(GONE);
                    findViewById(R.id.routePanel).setVisibility(GONE);
                    GameSettings.set("CityTab","Market");
                }
            });
            findViewById(R.id.infoToggle).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToggleButton b = (ToggleButton) findViewById(R.id.infoToggle);
                    b.setChecked(true);
                    b = (ToggleButton) findViewById(R.id.marketToggle);
                    b.setChecked(false);
                    b = (ToggleButton) findViewById(R.id.routeToggle);
                    b.setChecked(false);
                    findViewById(R.id.buyPanel).setVisibility(GONE);
                    findViewById(R.id.infoPanel).setVisibility(VISIBLE);
                    findViewById(R.id.routePanel).setVisibility(GONE);
                    GameSettings.set("CityTab","Info");
                }
            });
            findViewById(R.id.routeToggle).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToggleButton b = (ToggleButton) findViewById(R.id.infoToggle);
                    b.setChecked(false);
                    b = (ToggleButton) findViewById(R.id.marketToggle);
                    b.setChecked(false);
                    b = (ToggleButton) findViewById(R.id.routeToggle);
                    b.setChecked(true);
                    findViewById(R.id.buyPanel).setVisibility(GONE);
                    findViewById(R.id.infoPanel).setVisibility(GONE);
                    findViewById(R.id.routePanel).setVisibility(VISIBLE);
                    GameSettings.set("CityTab","Route");
                }
            });
            this.setOnSwipeListener(new OnSwipeListener() {
                @Override
                public void onSwipeRight() {
                    ToggleButton info = (ToggleButton) findViewById(R.id.infoToggle);
                    ToggleButton market = (ToggleButton) findViewById(R.id.marketToggle);
                    ToggleButton route = (ToggleButton) findViewById(R.id.routeToggle);
                    if (info.isChecked())
                    {
                        info.setChecked(false);
                        market.setChecked(true);
                        findViewById(R.id.buyPanel).setVisibility(VISIBLE);
                        findViewById(R.id.infoPanel).setVisibility(GONE);
                        GameSettings.set("CityTab","Market");
                    } else if (route.isChecked())
                    {
                        route.setChecked(false);
                        info.setChecked(true);
                        findViewById(R.id.infoPanel).setVisibility(VISIBLE);
                        findViewById(R.id.routePanel).setVisibility(GONE);
                        GameSettings.set("CityTab","Info");
                    }
                }

                @Override
                public void onSwipeLeft() {
                    ToggleButton info = (ToggleButton) findViewById(R.id.infoToggle);
                    ToggleButton market = (ToggleButton) findViewById(R.id.marketToggle);
                    ToggleButton route = (ToggleButton) findViewById(R.id.routeToggle);
                    if (info.isChecked())
                    {
                        info.setChecked(false);
                        route.setChecked(true);
                        findViewById(R.id.routePanel).setVisibility(VISIBLE);
                        findViewById(R.id.infoPanel).setVisibility(GONE);
                        GameSettings.set("CityTab","Route");
                    } else if (market.isChecked())
                    {
                        market.setChecked(false);
                        info.setChecked(true);
                        findViewById(R.id.infoPanel).setVisibility(VISIBLE);
                        findViewById(R.id.buyPanel).setVisibility(GONE);
                        GameSettings.set("CityTab","Info");
                    }
                }

                @Override
                public void onSwipeUp() {

                }

                @Override
                public void onSwipeDown() {

                }
            });

            countRoutes();
            ToggleButton b;
            String a=GameSettings.getValue("CityTab");
            if (a==null) a="Info";
            switch (a){
                case "Market": b = (ToggleButton) findViewById(R.id.marketToggle);
                    b.setChecked(true);
                    findViewById(R.id.buyPanel).setVisibility(VISIBLE);
                    break;
                case "Route": b = (ToggleButton) findViewById(R.id.routeToggle);
                    b.setChecked(true);
                    findViewById(R.id.routePanel).setVisibility(VISIBLE);
                    break;
                default:   b = (ToggleButton) findViewById(R.id.infoToggle);
                    b.setChecked(true);
                    findViewById(R.id.infoPanel).setVisibility(VISIBLE);
            }
            countHire(1);
        }
        private void update(){
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.cityExp);
            progressBar.setMax(100);
            progressBar.setProgress(city.getProgress());
            findViewById(R.id.buyUpgrade).setEnabled(city.upgradeAvaible());
            ((TextView) findViewById(R.id.skillDesc)).setText(getSkillInfo());
            countHire(currentCount);

        }
        private boolean countHire(int newValue){

            int priceForOne= (int) (hireprice*discount());
            int max=Math.min(Math.min(GameObjects.getPlayer().getLeftToHire(),GameObjects.getPlayer().getGold()/priceForOne),hirelings);
            int current=newValue;
            if (current<1) current=1;
            if (current>max) current=currentCount;
            if (current>max) current=max;
            currentCount=current;
            int price=(currentCount)*priceForOne;
            ((TextView)findViewById(R.id.hire_price)).setText(String.format(getContext().getString(R.string.hire), currentCount, max,StringUtils.intToStr(price)));
            SeekBar seekBar= (SeekBar) findViewById(R.id.hireCount);
            seekBar.setProgress(currentCount);
            seekBar.setMax(max);
            return currentCount==newValue;
        }
        private void countRoutes(){
            int amount=0;
            int income=0;
            LinearLayout l= (LinearLayout) findViewById(R.id.routePanel);
            l.removeAllViews();
            for (Caravan r: GameObjects.getPlayer().getRoutes().values()){
                if (r.getStartGUID().equals(city.getGUID())||r.getFinishGUID().equals(city.getGUID())){
                    CityLine line = new CityLine(getContext());
                    amount++;
                    income+=r.getProfit();
                    l.addView(line);
                    line.setData(r);
                    line.setParentForm(self);

                }
                ((TextView)findViewById(R.id.routeCount)).setText(String.valueOf(amount));
                ((TextView)findViewById(R.id.routeIncome)).setText(String.valueOf(income));
            }
        }
        public void updateInZone(boolean inZone){
            if (inZone) {
                findViewById(R.id.startRoute).setVisibility(GONE);
                findViewById(R.id.finishRoute).setVisibility(GONE);
                {
                    ImageButton btn= (ImageButton) findViewById(R.id.buyUpgrade);
                    btn.setClickable(false);
                    btn.setEnabled(false);
                    btn.setAlpha(0.5f);
                }
                findViewById(R.id.restart_route).setVisibility(GONE);
                findViewById(R.id.drop_route).setVisibility(GONE);
                if (GameObjects.getPlayer().getRouteStart()) findViewById(R.id.startRoute).setVisibility(VISIBLE);
                if (!GameObjects.getPlayer().getRouteStart() && (city!=null))
                {
                    ImageButton btn= (ImageButton) findViewById(R.id.finishRoute);
                    ImageButton btn2= (ImageButton) findViewById(R.id.restart_route);
                    btn.setVisibility(VISIBLE);
                    btn2.setVisibility(VISIBLE);
                    if (!GameObjects.getPlayer().checkRoute(city.getGUID())) {
                        btn.setClickable(true);
                        btn.setEnabled(true);
                        btn.setAlpha(1f);
                        btn2.setClickable(true);
                        btn2.setEnabled(true);
                        btn2.setAlpha(1f);
                    } else
                    {
                        btn.setClickable(false);
                        btn.setEnabled(false);
                        btn.setAlpha(0.5f);
                        btn2.setClickable(false);
                        btn2.setEnabled(false);
                        btn2.setAlpha(0.5f);
                    }
                    btn= (ImageButton) findViewById(R.id.drop_route);
                    btn.setVisibility(VISIBLE);


                }
                if (city!=null) {
                    ImageButton btn= (ImageButton) findViewById(R.id.buyUpgrade);

                    if (city.upgradeAvaible()) {
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
                ImageButton btn= (ImageButton) findViewById(R.id.hirePeople);
                if (currentCount>0) {
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
            else
            {
                findViewById(R.id.startRoute).setVisibility(GONE);
                findViewById(R.id.finishRoute).setVisibility(GONE);
                {
                    ImageButton btn= (ImageButton) findViewById(R.id.buyUpgrade);
                    btn.setClickable(false);
                    btn.setEnabled(false);
                    btn.setAlpha(0.5f);
                }
                {
                    ImageButton btn= (ImageButton) findViewById(R.id.hirePeople);
                    btn.setClickable(false);
                    btn.setEnabled(false);
                    btn.setAlpha(0.5f);
                }
                findViewById(R.id.restart_route).setVisibility(GONE);
                findViewById(R.id.drop_route).setVisibility(GONE);
            }
            ((TextView)findViewById(R.id.goldInfo)).setText(String.format(getContext().getString(R.string.gold_amount), StringUtils.intToStr(GameObjects.getPlayer().getGold())));
        }
        public void close(){
            this.setVisibility(GONE);
            city=null;
            actionView.HideView();
        }
        ActionView actionView;
        @Override
        public void setContainer(ActionView av) {
            actionView=av;
        }

        @Override
        public void setDistance(int distance) {
            ((TextView) findViewById(R.id.distance)).setText(String.format(getResources().getString(R.string.distance), distance));
        }

        @Override
        public void Show() {

        }

        @Override
        public void Hide() {
            close();
        }
    }
    private class CityAction extends RelativeLayout implements  GameObjectView{
        City city;
        public CityAction(Context context) {
            super(context);
            init();
        }

        public CityAction(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public CityAction(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }
        public void init(){
            inflate(this.getContext(), R.layout.city_actions, this);
        }
        boolean loaded=true;


        public void setCity(City city){
            this.city=city;
            if (loaded) applyCity();
        }

        private void applyCity() {
            findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    close();
                }
            });
            ImageButton btn= (ImageButton) findViewById(R.id.buy);
            btn.setEnabled(city.upgradeAvaible());
            btn.setImageBitmap(ImageLoader.getImage(city.upgrade + "_buy"));
            updateAction(getContext());

            findViewById(R.id.start).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverConnect.getInstance().callStartRoute(startRouteAction,
                            GPSInfo.getInstance().GetLat(),
                            GPSInfo.getInstance().GetLng(),
                            city.getGUID());
                    if ("Y".equals(GameSettings.getInstance().get("CLOSE_WINDOW")))
                        close();
                    else {
                        updateInZone(true);
                    }
                }
            });
            findViewById(R.id.end).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverConnect.getInstance().callFinishRoute(endRouteAction,
                            GPSInfo.getInstance().GetLat(),
                            GPSInfo.getInstance().GetLng(),
                            city.getGUID());
                    if ("Y".equals(GameSettings.getInstance().get("CLOSE_WINDOW")))
                        close();
                    else {
                        updateInZone(true);
                    }
                }

            });


            findViewById(R.id.buy).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmWindow confirmWindow=new ConfirmWindow(getContext());
                    Upgrade up=GameObjects.getPlayer().getNextUpgrade(upgrade);
                    if (up!=null) {

                        int upcost = getUpgradeCost();
                        confirmWindow.setText(String.format(getContext().getString(R.string.accept_buy_upgrade), up.getName(), up.getLevel(), StringUtils.intToStr(upcost)));
                    } else confirmWindow.setText(String.format(getContext().getString(R.string.accept_buy_upgrade_unknown), upgradeName));
                    confirmWindow.setConfirmAction(new Runnable() {
                        @Override
                        public void run() {

                            serverConnect.getInstance().callBuyUpgrade(buyAction,
                                    GPSInfo.getInstance().GetLat(),
                                    GPSInfo.getInstance().GetLng(),city.getGUID()
                                    );
                            if ("Y".equals(GameSettings.getInstance().get("CLOSE_WINDOW")))
                                close();
                            else {
                                updateInZone(true);
                            }
                        }
                    });
                    confirmWindow.show();
                }
            });
            findViewById(R.id.info).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    UIControler.getActionLayout().removeAllViews();
                    CityWindow cityWindow=new CityWindow(getContext());
                    cityWindow.setCity(city);
                    UIControler.getActionLayout().setCurrentView(cityWindow);

                }
            });
            findViewById(R.id.restart_route).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (city.getMarker()!=null) {
                        serverConnect.getInstance().callStartFinishRoute(startFinishRouteAction,
                                GPSInfo.getInstance().GetLat(),
                                GPSInfo.getInstance().GetLng(),
                                city.getGUID());

                        if ("Y".equals(GameSettings.getInstance().get("CLOSE_WINDOW")))
                            close();
                        else {
                            updateInZone(true);
                        }
                    } else close();
                }
            });
            findViewById(R.id.drop_route).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmWindow confirmWindow=new ConfirmWindow(getContext());
                    confirmWindow.setText("Вы уверены что хотите отменить не завершенный маршрут?");
                    confirmWindow.setConfirmAction(new Runnable() {
                        @Override
                        public void run() {
                            serverConnect.getInstance().callDropUnfinishedRoute(GameObjects.getPlayer().getDropRoute());
                            updateInZone(true);
                        }
                    });
                    confirmWindow.show();
                }
            });

        }
        public void updateInZone(boolean inZone){
            if (inZone) {

                findViewById(R.id.start).setVisibility(INVISIBLE);
                findViewById(R.id.end).setVisibility(INVISIBLE);
                findViewById(R.id.buy).setVisibility(INVISIBLE);
                findViewById(R.id.restart_route).setVisibility(INVISIBLE);
                findViewById(R.id.drop_route).setVisibility(INVISIBLE);
                if (GameObjects.getPlayer().getRouteStart()) findViewById(R.id.start).setVisibility(VISIBLE);
                if (!GameObjects.getPlayer().getRouteStart() && (city!=null))
                {
                    ImageButton btn= (ImageButton) findViewById(R.id.end);
                    ImageButton btn2= (ImageButton) findViewById(R.id.restart_route);
                    btn.setVisibility(VISIBLE);
                    btn2.setVisibility(VISIBLE);
                    if (!GameObjects.getPlayer().checkRoute(city.getGUID())) {
                        btn.setClickable(true);
                        btn.setEnabled(true);
                        btn.setAlpha(1f);
                        btn2.setClickable(true);
                        btn2.setEnabled(true);
                        btn2.setAlpha(1f);
                    } else
                    {
                        btn.setClickable(false);
                        btn.setEnabled(false);
                        btn.setAlpha(0.5f);
                        btn2.setClickable(false);
                        btn2.setEnabled(false);
                        btn2.setAlpha(0.5f);
                    }
                    btn= (ImageButton) findViewById(R.id.drop_route);
                    btn.setVisibility(VISIBLE);

                }
                if (city!=null) {
                    ImageButton btn= (ImageButton) findViewById(R.id.buy);
                    btn.setVisibility(VISIBLE);
                    if (city.upgradeAvaible()) {
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
            }
            else
            {
                findViewById(R.id.start).setVisibility(INVISIBLE);
                findViewById(R.id.end).setVisibility(INVISIBLE);
                findViewById(R.id.buy).setVisibility(INVISIBLE);
                findViewById(R.id.restart_route).setVisibility(INVISIBLE);
                findViewById(R.id.drop_route).setVisibility(INVISIBLE);
            }
        }
        public void close(){
            this.setVisibility(GONE);
            city=null;
            actionView.HideView();
        }
        ActionView actionView;
        @Override
        public void setContainer(ActionView av) {
            actionView=av;
        }

        @Override
        public void setDistance(int distance) {
            ((TextView) findViewById(R.id.distance)).setText(String.format(getContext().getString(R.string.distance_mesure), distance));
        }
    }
    public RelativeLayout getObjectView(Context context){
        if ("Y".equals(GameSettings.getInstance().get("VIEW_PADDING"))) {
            CityAction result = new CityAction(context);
            result.setCity(this);
            return result;
        } else
        {
            CityWindow result = new CityWindow(context);
            result.setCity(this);
            return result;
        }
    }
    public int getRadius(){
        return radius;
    }

    @Override
    public void useObject() {
        SelectedObject.getInstance().setTarget(this);
        SelectedObject.getInstance().setPoint(this.getPosition());
        UIControler.getActionLayout().ShowView();
    }
}
