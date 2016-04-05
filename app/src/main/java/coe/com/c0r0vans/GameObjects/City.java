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
        loadJSON(obj);
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
                changeMarkerSize();
            }
            if (zone==null){
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latlng);
                circleOptions.radius(radius);

                circleOptions.strokeColor(Color.BLUE);
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


    public boolean upgradeAvaible(){
        Upgrade up=Player.getPlayer().getNextUpgrade(upgrade);
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

        raceBonus=((1f-raceBonus/4)*(100f-Player.getPlayer().getTrade())/100f);
        int upcost= (int) (up.getCost()*raceBonus);

        return !( (up.getReqCityLev() > Level)
                || (upcost >= Player.getPlayer().getGold())
                || (up.getLevel() > Player.getPlayer().getLevel() - 1));
    }
    public String getName(){return (Name+" lv."+Level) ;}


    @Override
    public void changeMarkerSize() {
        if (mark!=null) {
            String markname = "city";
            int lvl=(this.Level+1)/2;
            if (lvl==0) lvl=1;
            markname = markname + "_"+lvl;
            markname = markname + GameObject.zoomToPostfix(MyGoogleMap.getClientZoom());
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
                if (up.getReqCityLev()>Level) dop= String.format("Требуется уровень города %d\n", up.getReqCityLev());
                else if ((upcost)>Player.getPlayer().getGold()) dop= String.format("Вам не хватает:%d золота!\n", upcost - Player.getPlayer().getGold());
                else if (up.getLevel()>Player.getPlayer().getLevel()-1) dop= String.format("Требуется уровень %d\n", up.getLevel());
                else dop= String.format("Эффект:%s\n", up.getDescription());

                return String.format("%s\" за %d золота(без скидки %d).\n%s", up.getName(), upcost,up.getCost() , dop);
            }
            else return upgradeName+"\". "
                    ;
        }
        private void applyCity() {


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
            ((TextView) findViewById(R.id.skillDesc)).setText(getSkillInfo());
            findViewById(R.id.buyUpgrade).setEnabled(city.upgradeAvaible());
            startRouteAction = new ObjectAction(city) {
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
                    Player.getPlayer().setRouteStart(false);
                }

                @Override
                public void postAction() {
                    GameSound.playSound(GameSound.START_ROUTE_SOUND);
                    Essages.addEssage(String.format(getResources().getString(R.string.route_started), Name));
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
                public String getCommand() {
                    return "FinishRoute";
                }

                @Override
                public void preAction() {
                    Player.getPlayer().setRouteStart(true);
                }

                @Override
                public void postAction() {
                    Essages.addEssage(String.format(getResources().getString(R.string.route_finish), Name));
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
                    if (up != null) Essages.addEssage(String.format(getResources().getString(R.string.upgrade_bought),up.getName()));
                    else Essages.addEssage(String.format(getResources().getString(R.string.upgrade_bought), upgrade));
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
            ((TextView) findViewById(R.id.distance)).setText(String.format(getResources().getString(R.string.distance), distance));
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
