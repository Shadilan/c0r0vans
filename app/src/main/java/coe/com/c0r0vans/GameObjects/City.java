package coe.com.c0r0vans.GameObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

import coe.com.c0r0vans.ActionView;
import coe.com.c0r0vans.GameSound;
import coe.com.c0r0vans.MyGoogleMap;
import coe.com.c0r0vans.R;
import utility.Essages;
import utility.GPSInfo;
import utility.GameSettings;
import utility.ImageLoader;
import utility.serverConnect;

/**
 * @author Shadilan
 */
public class City extends GameObject{
    private int Level=0;
    private int radius=100;
    private String upgrade;
    private String upgradeName;
    private long influence1=0;
    private long influence2=0;
    private long influence3=0;


    private Circle zone;


    public City(GoogleMap map,JSONObject obj) throws JSONException {
        this.map=map;
        int Lat=obj.getInt("Lat");
        int Lng=obj.getInt("Lng");
        mark=map.addMarker(new MarkerOptions().position(new LatLng(Lat / 1e6, Lng / 1e6)));
        loadJSON(obj);
    }


    @Override
    public void setMarker(Marker m) {
        mark=m;
        changeMarkerSize(MyGoogleMap.getClientZoom());

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
            if (obj.has("Level")) Level=obj.getInt("Level");
            if (obj.has("Radius")) radius=obj.getInt("Radius");
            if (obj.has("Progress")) progress=obj.getInt("Progress");
            if (obj.has("Influence1")) influence1=obj.getLong("Influence1");
            if (obj.has("Influence2")) influence2=obj.getLong("Influence2");
            if (obj.has("Influence3")) influence3=obj.getLong("Influence3");
            Log.d("tttt","Influence1:"+influence1);
            if (mark==null) {
                setMarker(map.addMarker(new MarkerOptions().position(latlng)));

            } else {
                mark.setPosition(latlng);
                changeMarkerSize(MyGoogleMap.getClientZoom());
            }
            if (zone==null){
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latlng);
                circleOptions.radius(radius);
                /*if (this.influence1>this.influence2 && this.influence1>this.influence3)
                    circleOptions.fillColor(R.color.colorGuild);
                else if (this.influence2>this.influence1 && this.influence2>this.influence3)
                    circleOptions.fillColor(R.color.colorAlliance);
                else if (this.influence3>this.influence1 && this.influence3>this.influence2)
                    circleOptions.fillColor(R.color.colorLiga);
                else circleOptions.fillColor(Color.TRANSPARENT);*/
                circleOptions.strokeColor(Color.BLUE);
                circleOptions.strokeWidth(2);
                zone = map.addCircle(circleOptions);
            } else
            {
                zone.setCenter(latlng);
                zone.setRadius(radius);
                /*if (this.influence1>this.influence2 && this.influence1>this.influence3)
                    zone.setFillColor(R.color.colorGuild);
                else if (this.influence2>this.influence1 && this.influence2>this.influence3)
                    zone.setFillColor(R.color.colorAlliance);
                else if (this.influence3>this.influence1 && this.influence3>this.influence2)
                    zone.setFillColor(R.color.colorLiga);
                else zone.setFillColor(Color.TRANSPARENT);*/
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
        String tushkan="";
        if (Math.random()*1000<3) tushkan="У стен города следы непонятного зверя.";

        Upgrade up=Player.getPlayer().getNextUpgrade(upgrade);
        if (up!=null) {
            int raceBonus=0;
            long infsum=influence1+influence2+influence3;
            if (infsum>0) {
                switch (Player.getPlayer().getRace()) {
                    case 1:raceBonus=(int)(influence1/infsum);
                        break;
                    case 2:raceBonus=(int)(influence2/infsum);
                        break;
                    case 3:raceBonus=(int)(influence2/infsum);
                        break;
                }
            }

            raceBonus=((1-raceBonus/4)*(100-Player.getPlayer().getTrade())/100);
            String dop;
            if (up.getReqCityLev()>Level) dop="Требуется уровень города "+ up.getReqCityLev()+"\n";
            else if ((up.getCost()*raceBonus)>Player.getPlayer().getGold()) dop="Нужно больше золота!"+ up.getCost() +" золота!\n";
            else if (up.getLevel()>Player.getPlayer().getLevel()-1) dop="Вы недостаточно опытны!\n";
            else dop="Эффект:" + up.getDescription()+"\n";

            return "Это город " + Level + " уровня.\n В городе можно приобрести улучшение \"" + up.getName() + "\" за " +
                    (up.getCost()*raceBonus) + " золота.\n" + dop + tushkan;
        }
        else return "Это город "+ Level+" уровня.\n В городе можно приобрести улучшение \""+upgradeName+"\". "
                +tushkan;
    }
    public String getSkillInfo() {
        Upgrade up=Player.getPlayer().getNextUpgrade(upgrade);

        if (up!=null) {
            int raceBonus=0;
            long infsum=influence1+influence2+influence3;
            if (infsum>0) {
                switch (Player.getPlayer().getRace()) {
                    case 1:raceBonus=(int)(influence1/infsum);
                        break;
                    case 2:raceBonus=(int)(influence2/infsum);
                        break;
                    case 3:raceBonus=(int)(influence2/infsum);
                        break;
                }
            }

            raceBonus=((1-raceBonus/4)*(100-Player.getPlayer().getTrade())/100);
            String dop;
            if (up.getReqCityLev()>Level) dop="Требуется уровень города "+ up.getReqCityLev()+"\n";
            else if ((up.getCost()*raceBonus)>Player.getPlayer().getGold()) dop="Нужно больше золота!"+ up.getCost() +" золота!\n";
            else if (up.getLevel()>Player.getPlayer().getLevel()-1) dop="Вы недостаточно опытны!\n";
            else dop="Эффект:" + up.getDescription()+"\n";

            return up.getName() + "\" за " +
                    (up.getCost()*raceBonus) + " золота.\n" + dop;
        }
        else return upgradeName+"\". "
                ;
    }
    public boolean upgradeAvaible(){
        Upgrade up=Player.getPlayer().getNextUpgrade(upgrade);
        int raceBonus=0;
        long infsum=influence1+influence2+influence3;
        if (infsum>0) {
            switch (Player.getPlayer().getRace()) {
                case 1:raceBonus=(int)(influence1/infsum);
                    break;
                case 2:raceBonus=(int)(influence2/infsum);
                    break;
                case 3:raceBonus=(int)(influence2/infsum);
                    break;
            }
        }

        raceBonus=((1-raceBonus/4)*(100-Player.getPlayer().getTrade())/100);

        return !((up == null)
                || (up.getReqCityLev() > Level)
                || (up.getCost()*raceBonus > Player.getPlayer().getGold())
                || (up.getLevel() > Player.getPlayer().getLevel() - 1));
    }
    public String getCityName(){return (Name+" lv."+Level) ;}


    @Override
    public void changeMarkerSize(float Type) {
        if (mark!=null) {
            String markname = "city";
            int lvl=(this.Level+1)/2;
            if (lvl==0) lvl=1;
            markname = markname + "_"+lvl;
            markname = markname + GameObject.zoomToPostfix(Type);
            mark.setIcon(ImageLoader.getDescritor(markname));
            //if ("Y".equals(GameSettings.getInstance().get("USE_TILT"))) mark.setAnchor(0.5f, 1f);
            //else
            mark.setAnchor(0.5f, 0.5f);
        }
    }


    @Override
    public void setVisibility(boolean visibility) {
        mark.setVisible(visibility);
        zone.setVisible(visibility);
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

    public long getInfluence1() {
        return influence1;
    }

    public long getInfluence2() {
        return influence2;
    }

    public long getInfluence3() {
        return influence3;
    }

    private class CityWindow extends RelativeLayout implements  GameObjectView{
        City city;
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
            inflate(this.getContext(), R.layout.city_layout, this);
            if ("Y".equals(GameSettings.getInstance().get("VIEW_PADDING"))) this.setAlpha(0.7f);
            Log.d("tttt","Wind1");
        }
        boolean loaded=true;


        public void setCity(City city){
            Log.d("tttt","Wind2");
            this.city=city;
            if (loaded) applyCity();
        }
        ObjectAction buyAction;
        ObjectAction startRouteAction;
        ObjectAction endRouteAction;

        private void applyCity() {
            Log.d("tttt", "Wind4");

            findViewById(R.id.closeActionButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    close();
                }
            });
            ((TextView) findViewById(R.id.cityName)).setText(city.getCityName());

            long sum = city.getInfluence1() + city.getInfluence2() + city.getInfluence3();
            if (sum == 0) sum = 1;

            int inf1 = Math.round(city.getInfluence1() * 100 / sum );
            int inf2 = Math.round(city.getInfluence2() * 100/ sum );
            int inf3 = Math.round(city.getInfluence3() * 100/ sum );
            int maxInf = Math.max(Math.max(Math.max(inf1, inf2), inf3), 1);
            NumberFormat nf=NumberFormat.getInstance();
            nf.setGroupingUsed(true);
            ((TextView)findViewById(R.id.guildInfCount)).setText(nf.format(city.getInfluence1()));
            ((TextView)findViewById(R.id.allianceInfCount)).setText(nf.format(city.getInfluence2()));
            ((TextView)findViewById(R.id.ligaInfCount)).setText(nf.format(city.getInfluence3()));
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.GuildInf);
            progressBar.setProgress(inf1);
            progressBar.setMax(100);
            progressBar = (ProgressBar) findViewById(R.id.AllianceInf);
            progressBar.setProgress(inf2);
            progressBar.setMax(100);
            progressBar = (ProgressBar) findViewById(R.id.LigaInf);
            progressBar.setProgress(inf3);
            progressBar.setMax(100);
            progressBar = (ProgressBar) findViewById(R.id.cityExp);
            progressBar.setMax(100);
            progressBar.setProgress(city.getProgress());
            ((TextView) findViewById(R.id.skillDesc)).setText(city.getSkillInfo());
            findViewById(R.id.buyUpgrade).setEnabled(city.upgradeAvaible());
            startRouteAction = new ObjectAction(city) {
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
                    return "StartRoute";
                }

                @Override
                public void preAction() {
                    Player.getPlayer().setRouteStart(false);
                }

                @Override
                public void postAction() {
                    GameSound.playSound(GameSound.START_ROUTE_SOUND);
                    Essages.addEssage("Начат маршрут в город " + Name);
                    serverConnect.getInstance().getPlayerInfo();
                }

                @Override
                public void postError() {
                    Player.getPlayer().setRouteStart(true);

                }
            };

            findViewById(R.id.startRoute).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverConnect.getInstance().ExecCommand(startRouteAction,
                            city.getGUID(),
                            GPSInfo.getInstance().GetLat(),
                            GPSInfo.getInstance().GetLng(),
                            (int) (city.getMarker().getPosition().latitude * 1e6),
                            (int) (city.getMarker().getPosition().longitude * 1e6));
                    close();
                }


            });

            endRouteAction = new ObjectAction(city) {
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
                    return "FinishRoute";
                }

                @Override
                public void preAction() {
                    Player.getPlayer().setRouteStart(true);
                }

                @Override
                public void postAction() {
                    Essages.addEssage("Завершен маршрут в город " + Name);
                    GameSound.playSound(GameSound.FINISH_ROUTE_SOUND);
                    serverConnect.getInstance().getPlayerInfo();
                    serverConnect.getInstance().RefreshCurrent();
                }

                @Override
                public void postError() {
                    Player.getPlayer().setRouteStart(false);

                }
            };
            findViewById(R.id.finishRoute).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverConnect.getInstance().ExecCommand(endRouteAction,
                            city.getGUID(),
                            GPSInfo.getInstance().GetLat(),
                            GPSInfo.getInstance().GetLng(),
                            (int) (city.getMarker().getPosition().latitude * 1e6),
                            (int) (city.getMarker().getPosition().longitude * 1e6));
                    close();
                }

            });

            buyAction = new ObjectAction(city) {
                @Override
                public Bitmap getImage() {
                    return ImageLoader.getImage("buy_item");
                }

                @Override
                public String getInfo() {
                    return "Купить апгрейд " + upgrade;
                }

                @Override
                public String getCommand() {
                    return "BuyUpgrade";
                }

                @Override
                public void preAction() {

                }

                @Override
                public void postAction() {
                    GameSound.playSound(GameSound.BUY_SOUND);
                    serverConnect.getInstance().getPlayerInfo();

                    Upgrade up = Player.getPlayer().getNextUpgrade(upgrade);
                    if (up != null) Essages.addEssage("Улучшение " + up.getName() + " куплено.");
                    else Essages.addEssage("Улучшение " + upgrade + " куплено.");
                    serverConnect.getInstance().getPlayerInfo();
                }

                @Override
                public void postError() {

                }
            };
            findViewById(R.id.buyUpgrade).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverConnect.getInstance().ExecCommand(buyAction,
                            city.getGUID(),
                            GPSInfo.getInstance().GetLat(),
                            GPSInfo.getInstance().GetLng(),
                            (int) (city.getMarker().getPosition().latitude * 1e6),
                            (int) (city.getMarker().getPosition().longitude * 1e6));
                    close();
                }
            });

        }
        public void updateInZone(boolean inZone){
            if (inZone) {
                findViewById(R.id.startRoute).setVisibility(GONE);
                findViewById(R.id.finishRoute).setVisibility(GONE);
                findViewById(R.id.buyUpgrade).setVisibility(GONE);
                if (Player.getPlayer().getRouteStart()) findViewById(R.id.startRoute).setVisibility(VISIBLE);
                if (!Player.getPlayer().getRouteStart() && (city!=null) && !Player.checkRoute(city.getGUID())) findViewById(R.id.finishRoute).setVisibility(VISIBLE);
                if (city!=null && city.upgradeAvaible()) findViewById(R.id.buyUpgrade).setVisibility(VISIBLE);
            }
            else
            {
                findViewById(R.id.startRoute).setVisibility(GONE);
                findViewById(R.id.finishRoute).setVisibility(GONE);
                findViewById(R.id.buyUpgrade).setVisibility(GONE);
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
            ((TextView) findViewById(R.id.distance)).setText(distance+"м");
        }
    }
    public RelativeLayout getObjectView(Context context){
        CityWindow result=new CityWindow(context);
        result.setCity(this);
        return result;
    }
    public int getRadius(){
        return radius;
    }
}
