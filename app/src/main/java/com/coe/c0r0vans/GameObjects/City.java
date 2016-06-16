package com.coe.c0r0vans.GameObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.coe.c0r0vans.ConfirmWindow;
import com.coe.c0r0vans.MyGoogleMap;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.UIElements.ActionView;
import com.coe.c0r0vans.UIElements.CityLine;
import com.coe.c0r0vans.UIElements.UIControler;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import utility.GATracker;
import utility.GPSInfo;
import utility.GameSound;
import utility.ImageLoader;
import utility.StringUtils;
import utility.internet.serverConnect;
import utility.notification.Essages;
import utility.settings.GameSettings;

/**
 * @author Shadilan
 */
public class City extends GameObject{
    private int Level=1;
    private int radius=100;
    private String upgrade;
    private String upgradeName;
    private long influence1=0;
    private long influence2=0;
    private long influence3=0;
    private boolean owner;
    private Circle buildZone;
    ObjectAction buyAction;
    ObjectAction startRouteAction;
    ObjectAction endRouteAction;
    private String founder="";
    private int hirelings=100;
    private int hireprice=100;

    private void updateAction(final Context ctx){
        startRouteAction = new ObjectAction(this) {
            String oldRouteGuid;
            Route oldRoute;
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
                oldRouteGuid=Player.getPlayer().getCurrentRouteGUID();
                oldRoute=Player.getPlayer().getCurrentR();
                routeStart=Player.getPlayer().getRouteStart();
                Player.getPlayer().setRouteStart(false);
                Player.getPlayer().setCurrentRouteGUID(getGUID());
            }

            @Override
            public void postAction(JSONObject response) {
                GameSound.playSound(GameSound.START_ROUTE_SOUND);
                Essages.addEssage(String.format(ctx.getResources().getString(R.string.route_started), Name));
                if (response.has("Route")){
                    try {
                        Player.getPlayer().setCurrentRoute(new Route(response.getJSONObject("Route"),MyGoogleMap.getMap()));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                serverConnect.getInstance().callGetPlayerInfo();
                for (GameObject o:GameObjects.getInstance().values()){
                    if (o!=null && o instanceof City) ((City) o).updateColor();
                }
            }

            @Override
            public void postError(JSONObject response) {
                //todo вернуть количество наемников
                try {
                    Player.getPlayer().setCurrentRouteGUID(oldRouteGuid);
                    Player.getPlayer().setRouteStart(true);
                    Player.getPlayer().setCurrentRoute(oldRoute);
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
                            Player.getPlayer().setRouteStart(true);
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
                            Player.getPlayer().setRouteStart(true);
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
                Player.getPlayer().setRouteStart(true);
            }
        };
        endRouteAction = new ObjectAction(this) {
            String oldRouteGuid;
            Route oldRoute;
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
                oldRouteGuid=Player.getPlayer().getCurrentRouteGUID();
                oldRoute=Player.getPlayer().getCurrentR();
                routeStart=Player.getPlayer().getRouteStart();
                Player.getPlayer().setCurrentRouteGUID("");
                Player.getPlayer().setCurrentRoute(null);
                Player.getPlayer().setRouteStart(true);
            }

            @Override
            public void postAction(JSONObject response) {
                Essages.addEssage(String.format(ctx.getResources().getString(R.string.route_finish), Name));
                GameSound.playSound(GameSound.FINISH_ROUTE_SOUND);
                serverConnect.getInstance().callGetPlayerInfo();
                Player.getPlayer().setCurrentRouteGUID("");
                for (GameObject o:GameObjects.getInstance().values()){
                    if (o!=null && o instanceof City) ((City) o).updateColor();
                }
            }

            @Override
            public void postError(JSONObject response) {
                Player.getPlayer().setRouteStart(routeStart);
                Player.getPlayer().setCurrentRouteGUID(oldRouteGuid);
                Player.getPlayer().setCurrentRoute(oldRoute);
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
                            Player.getPlayer().setRouteStart(true);
                            break;
                        case "O0601":
                            Essages.addEssage("Город не найден.");
                            break;
                        case "O0602":
                            Essages.addEssage("Город далеко.");
                            break;
                        case "O0603":
                            Essages.addEssage("Маршрут не начат.");
                            Player.getPlayer().setRouteStart(false);
                            Player.getPlayer().setCurrentRouteGUID("");
                            Player.getPlayer().setCurrentRoute(null);
                            break;
                        case "O0604":
                            Essages.addEssage("Такой маршрут уже есть.");
                            break;
                        case "O0605":
                            Essages.addEssage("Маршрут начинается в этом городе.");
                            break;
                        case "O0606":
                            if (response.has("Message"))
                                Essages.addEssage(response.getString("Message"));
                            else Essages.addEssage("Не хватает наемников.");
                            Player.getPlayer().setRouteStart(routeStart);
                            Player.getPlayer().setCurrentRouteGUID(oldRouteGuid);
                            Player.getPlayer().setCurrentRoute(oldRoute);
                        default:
                            Player.getPlayer().setRouteStart(true);
                            if (response.has("Message"))
                                Essages.addEssage(response.getString("Message"));
                            else Essages.addEssage("Непредвиденная ошибка.");

                    }
                }catch (JSONException e)
                {
                    GATracker.trackException("FinishRoute",e);
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
                Upgrade up=Player.getPlayer().getNextUpgrade(upgrade);
                if (up!=null) {
                    upcost = (int) (up.getCost() * discount());
                }
                Player.getPlayer().setGold(Player.getPlayer().getGold()-upcost);
            }

            @Override
            public void postAction(JSONObject response) {

                try {
                    if ((response.has("Result") && "OK".equals(response.getString("Result"))) || (!response.has("Result")
                            && !response.has("Error"))){
                        GameSound.playSound(GameSound.BUY_SOUND);
                        Upgrade up = Player.getPlayer().getUpgrade(upgrade);
                        if (up != null) Essages.addEssage(String.format(ctx.getResources().getString(R.string.upgrade_bought),up.getName()));
                        else Essages.addEssage(String.format(ctx.getResources().getString(R.string.upgrade_bought), upgrade));
                        if (response.has("Upgrade")){
                            Upgrade n=new Upgrade(response.getJSONObject("Upgrade"));
                            Upgrade r=Player.getPlayer().getUpgrade(n.getType());
                            Player.getPlayer().getUpgrades().remove(r);
                            Player.getPlayer().getUpgrades().add(n);
                        }
                        if (response.has("NextUpgrade")){
                            Upgrade n=new Upgrade(response.getJSONObject("NextUpgrade"));
                            Player.getPlayer().getNextUpgrades().remove(n.getType());
                            Player.getPlayer().getNextUpgrades().put(n.getType(),n);
                        }



                    } else postError(response);


                } catch (JSONException e) {
                    GATracker.trackException("BuyUpgrade","JSONResult error");
                    Log.d("BuyUpgrade",e.toString()+ Arrays.toString(e.getStackTrace()));
                }

            }

            @Override
            public void postError(JSONObject response) {
                try {
                    Player.getPlayer().setGold(Player.getPlayer().getGold()-upcost);
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
                            Essages.addEssage("Не хватает золота на покупку.");
                            break;
                        case "O0704":
                            Essages.addEssage("Город слишком мал.");
                            break;
                        case "O0705":
                            Essages.addEssage("Не достаточно уровня для изучения умения.");
                            break;
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
        changeMarkerSize();

    }

    @Override
    public void loadJSON(JSONObject obj) {
        try {
            GUID=obj.getString("GUID");
            int Lat=obj.getInt("Lat");
            int Lng=obj.getInt("Lng");
            LatLng latlng=new LatLng(Lat / 1e6, Lng / 1e6);

            if (obj.has("Name")) Name=obj.getString("Name");
            if (obj.has("UpgradeType")) upgrade=obj.getString("UpgradeType");
            if (obj.has("UpgradeName")) upgradeName=obj.getString("UpgradeName");
            if (obj.has("Level")) {
                Level=obj.getInt("Level");
                hireprice=(int)(Math.sqrt(Level)*100);
            }
            if (obj.has("Radius")) radius=obj.getInt("Radius");
            if (obj.has("Progress")) progress=obj.getInt("Progress");
            if (obj.has("Influence1")) influence1=obj.getLong("Influence1");
            if (obj.has("Influence2")) influence2=obj.getLong("Influence2");
            if (obj.has("Influence3")) influence3=obj.getLong("Influence3");
            if (obj.has("Owner")) owner=obj.getBoolean("Owner"); else owner=false;
            if (obj.has("Creator")) founder=obj.getString("Creator");
            if (obj.has("Hirelings")) hirelings=obj.getInt("Hirelings");
            Log.d("tttt","Influence1:"+influence1);
            if (mark==null) {
                setMarker(map.addMarker(new MarkerOptions().position(latlng)));

            } else {
                mark.setPosition(latlng);
                changeMarkerSize();
            }
            if (zone==null){
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latlng);
                circleOptions.radius(radius);
                circleOptions.zIndex(100);
                if (Player.checkRoute(GUID)) circleOptions.strokeColor(Color.DKGRAY);
                else circleOptions.strokeColor(Color.BLUE);
                circleOptions.strokeWidth(2);
                zone = map.addCircle(circleOptions);
            } else
            {
                zone.setCenter(latlng);
                zone.setRadius(radius);
                if (Player.checkRoute(GUID)) zone.setStrokeColor(Color.DKGRAY);
                else zone.setStrokeColor(Color.BLUE);

            }
            if (buildZone==null){
                CircleOptions circleOptions=new CircleOptions();
                circleOptions.center(latlng);
                int dist=250;
                if (owner) dist=500;
                Upgrade up=Player.getPlayer().getUpgrade("founder");
                if (up!=null) dist+=up.getEffect2();
                else dist+=125;
                circleOptions.radius(dist);
                circleOptions.zIndex(0);
                circleOptions.fillColor(0x30ff0000);
                circleOptions.strokeWidth(0);
                buildZone=map.addCircle(circleOptions);
            } else
            {
                buildZone.setCenter(latlng);
                int dist=250;
                if (owner) dist=500;
                Upgrade up=Player.getPlayer().getUpgrade("founder");
                if (up!=null) dist+=up.getEffect2();
                else dist+=125;
                buildZone.setRadius(dist);
            }
            showRadius();
            showBuildZone();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private boolean upgradeAvaible(){
        Upgrade up=Player.getPlayer().getNextUpgrade(upgrade);
        if (up == null ) return true;
        float raceBonus=discount();
        int upcost= (int) (up.getCost()*raceBonus);

        return !( (up.getReqCityLev() > Level)
                || (upcost >= Player.getPlayer().getGold())
                || (up.getLevel() > Player.getPlayer().getLevel() - 1));
    }
    private float discount(){
        float raceBonus=0;
        long infsum=influence1+influence2+influence3;
        if (infsum>0) {
            switch (Player.getPlayer().getRace()) {
                case 1:raceBonus=(float)influence1/infsum;
                    break;
                case 2:raceBonus=(float)influence2/infsum;
                    break;
                case 3:raceBonus=(float)influence3/infsum;
                    break;
            }
        }
        return ((1f-raceBonus/4)*(100f-Player.getPlayer().getTrade())/100f);
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
    }


    @Override
    public void setVisibility(boolean visibility) {
        if (mark!=null) mark.setVisible(visibility);
        zone.setVisible(visibility);
    }
    public void showBuildZone(){
        String opt= GameSettings.getInstance().get("SHOW_BUILD_AREA");
        if (opt.equals("Y")){
            buildZone.setVisible(true);
        } else
        {
            buildZone.setVisible(false);
        }
    }
    public void showRadius(){
        String opt= GameSettings.getInstance().get("SHOW_CITY_RADIUS");
        if (opt.equals("Y")){
            zone.setVisible(true);
        } else
        {
            zone.setVisible(false);
        }
    }
    public void updateColor(){
            if (!Player.checkRoute(GUID)) zone.setStrokeColor(Color.BLUE);
            else zone.setStrokeColor(Color.DKGRAY);

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

    public boolean getOwner() {
        return owner;
    }

    public void setOwner(boolean owner) {
        this.owner = owner;
    }

    public void setFounder(String founder) {
        this.founder = founder;
    }

    private class CityWindow extends RelativeLayout implements  GameObjectView,ShowHideForm{
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
            Upgrade up=Player.getPlayer().getNextUpgrade(upgrade);

            if (up!=null) {
                float raceBonus=0;
                long infsum=city.influence1+city.influence2+city.influence3;
                if (infsum>0) {
                    switch (Player.getPlayer().getRace()) {
                        case 1:raceBonus=(float)city.influence1/infsum;
                            break;
                        case 2:raceBonus=(float)city.influence2/infsum;
                            break;
                        case 3:raceBonus=(float)city.influence3/infsum;
                            break;
                    }
                }
                raceBonus=((1f-raceBonus/4)*(100f-Player.getPlayer().getTrade())/100f);
                int upcost= (int) (up.getCost()*raceBonus);

                String dop;
                if (up.getReqCityLev()>Level) dop= String.format(getContext().getString(R.string.city_lvl_required), up.getReqCityLev());
                else if ((upcost)>Player.getPlayer().getGold()) dop= String.format(getContext().getString(R.string.need_more_gold), StringUtils.intToStr(upcost - Player.getPlayer().getGold()));
                else if (up.getLevel()>Player.getPlayer().getLevel()-1) dop= String.format(getContext().getString(R.string.need_higher_lvl), up.getLevel()+1);
                else dop= String.format("Эффект:%s\n", up.getDescription());

                return String.format(getContext().getString(R.string.price), up.getName(), StringUtils.intToStr(upcost),StringUtils.intToStr(up.getCost()), dop);
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
                    Upgrade up=Player.getPlayer().getNextUpgrade(upgrade);
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
                                    Upgrade up=Player.getPlayer().getNextUpgrade(upgrade);
                                    if (up!=null) {
                                        upcost = (int) (up.getCost() * discount());
                                    }
                                    Player.getPlayer().setGold(Player.getPlayer().getGold()-upcost);
                                    update();

                                }

                                @Override
                                public void postAction(JSONObject response) {

                                    try {
                                        if ((response.has("Result") && "OK".equals(response.getString("Result"))) || (!response.has("Result")
                                                && !response.has("Error"))){
                                            GameSound.playSound(GameSound.BUY_SOUND);
                                            Upgrade up = Player.getPlayer().getUpgrade(upgrade);
                                            if (up != null) Essages.addEssage(String.format(getContext().getResources().getString(R.string.upgrade_bought),up.getName()));
                                            else Essages.addEssage(String.format(getContext().getResources().getString(R.string.upgrade_bought), upgrade));

                                            if (response.has("Upgrade")){
                                                Upgrade n=new Upgrade(response.getJSONObject("Upgrade"));
                                                Upgrade r=Player.getPlayer().getUpgrade(n.getType());
                                                Player.getPlayer().getUpgrades().remove(r);
                                                Player.getPlayer().getUpgrades().add(n);
                                            }
                                            if (response.has("NextUpgrade")){
                                                Upgrade n=new Upgrade(response.getJSONObject("NextUpgrade"));
                                                Player.getPlayer().getNextUpgrades().remove(n.getType());
                                                Player.getPlayer().getNextUpgrades().put(n.getType(),n);
                                            }
                                            update();

                                        } else postError(response);


                                    } catch (JSONException e) {
                                        GATracker.trackException("BuyUpgrade","JSONResult error");
                                    }

                                }

                                @Override
                                public void postError(JSONObject response) {
                                    try {
                                        Player.getPlayer().setGold(Player.getPlayer().getGold()+upcost);
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
                                                Essages.addEssage("Не хватает золота на покупку.");
                                                break;
                                            case "O0704":
                                                Essages.addEssage("Город слишком мал.");
                                                break;
                                            case "O0705":
                                                Essages.addEssage("Не достаточно уровня для изучения умения.");
                                                break;
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
                        public int amount=currentCount;
                        public int gold=0;
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
                            Player.getPlayer().setGold(Player.getPlayer().getGold()-gold);
                            if (owner instanceof City) ((City) owner).hirelings-=amount;
                            Player.getPlayer().setLeftToHire(Player.getPlayer().getLeftToHire()-amount);
                            update();

                        }

                        @Override
                        public void postAction(JSONObject response) {
                            try {
                                if (response.has("Result") && "OK".equals(response.getString("Result"))) {
                                    GameSound.playSound(GameSound.BUY_SOUND);
                                    Player.getPlayer().setHirelings(Player.getPlayer().getHirelings()+amount);
                                    Essages.addEssage("Нанято "+amount+" чел. за "+gold+" золота.");
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
                            Player.getPlayer().setGold(Player.getPlayer().getGold()+gold);
                            if (owner instanceof City) ((City) owner).hirelings+=amount;
                            Player.getPlayer().setLeftToHire(Player.getPlayer().getLeftToHire()+amount);
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
                    ((TextView)findViewById(R.id.goldInfo)).setText(String.format(getContext().getString(R.string.gold_amount), StringUtils.intToStr(Player.getPlayer().getGold())));
                }
            });
            findViewById(R.id.restart_route).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverConnect.getInstance().callFinishRoute(endRouteAction,
                            GPSInfo.getInstance().GetLat(),
                            GPSInfo.getInstance().GetLng(),
                            city.getGUID());
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
            findViewById(R.id.drop_route).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmWindow confirmWindow=new ConfirmWindow(getContext());
                    confirmWindow.setText("Вы уверены что хотите отменить не завершенный маршрут?");
                    confirmWindow.setConfirmAction(new Runnable() {
                        @Override
                        public void run() {
                            serverConnect.getInstance().callDropUnfinishedRoute(Player.getPlayer().getDropRoute());
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
            int max=Math.min(Math.min(Player.getPlayer().getLeftToHire(),Player.getPlayer().getGold()/priceForOne),hirelings);
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
            for (Route r:Player.getPlayer().getRoutes()){
                if (r.getStartGuid().equals(city.getGUID())||r.getFinishGuid().equals(city.getGUID())){
                    CityLine line = new CityLine(getContext());
                    amount++;
                    income+=r.getProfit();
                    l.addView(line);
                    line.setData(r);
                    line.setParentForm(self);
                    line.setTarget(r.getGUID());
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
                if (Player.getPlayer().getRouteStart()) findViewById(R.id.startRoute).setVisibility(VISIBLE);
                if (!Player.getPlayer().getRouteStart() && (city!=null))
                {
                    ImageButton btn= (ImageButton) findViewById(R.id.finishRoute);
                    ImageButton btn2= (ImageButton) findViewById(R.id.restart_route);
                    btn.setVisibility(VISIBLE);
                    btn2.setVisibility(VISIBLE);
                    if (!Player.checkRoute(city.getGUID())) {
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
            ((TextView)findViewById(R.id.goldInfo)).setText(String.format(getContext().getString(R.string.gold_amount), StringUtils.intToStr(Player.getPlayer().getGold())));
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
                    Upgrade up=Player.getPlayer().getNextUpgrade(upgrade);
                    if (up!=null) {
                        float raceBonus = 0;
                        long infsum = city.influence1 + city.influence2 + city.influence3;
                        if (infsum > 0) {
                            switch (Player.getPlayer().getRace()) {
                                case 1:
                                    raceBonus = (float) city.influence1 / infsum;
                                    break;
                                case 2:
                                    raceBonus = (float) city.influence2 / infsum;
                                    break;
                                case 3:
                                    raceBonus = (float) city.influence3 / infsum;
                                    break;
                            }
                        }
                        raceBonus = ((1f - raceBonus / 4) * (100f - Player.getPlayer().getTrade()) / 100f);
                        int upcost = (int) (up.getCost() * raceBonus);
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
                        serverConnect.getInstance().callFinishRoute(endRouteAction,
                                GPSInfo.getInstance().GetLat(),
                                GPSInfo.getInstance().GetLng(),
                                city.getGUID());
                        serverConnect.getInstance().callStartRoute(startRouteAction,
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
                            serverConnect.getInstance().callDropUnfinishedRoute(Player.getPlayer().getDropRoute());
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
                if (Player.getPlayer().getRouteStart()) findViewById(R.id.start).setVisibility(VISIBLE);
                if (!Player.getPlayer().getRouteStart() && (city!=null))
                {
                    ImageButton btn= (ImageButton) findViewById(R.id.end);
                    ImageButton btn2= (ImageButton) findViewById(R.id.restart_route);
                    btn.setVisibility(VISIBLE);
                    btn2.setVisibility(VISIBLE);
                    if (!Player.checkRoute(city.getGUID())) {
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
}
