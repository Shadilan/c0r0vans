package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Color;

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
import utility.serverConnect;

/**
 * @author Shadilan
 */
public class City extends GameObject{
    private int Level=0;
    private int radius=100;
    private String upgrade;
    private String upgradeName;

    private Circle zone;


    public City(GoogleMap map,JSONObject obj) throws JSONException {
        this.map=map;
        int Lat=obj.getInt("Lat");
        int Lng=obj.getInt("Lng");
        image=ImageLoader.getImage("city");

        mark=map.addMarker(new MarkerOptions().position(new LatLng(Lat / 1e6, Lng / 1e6)));
        changeMarkerSize(MyGoogleMap.getClientZoom());
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
            if (mark==null) {
                setMarker(map.addMarker(new MarkerOptions().position(latlng)));

            } else {
                mark.setPosition(latlng);
            }
            if (zone==null){
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latlng);
                circleOptions.radius(radius);
                circleOptions.strokeColor(Color.BLUE);
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
        mark.remove();
        if (zone!=null) zone.remove();
    }

    @Override
    public String getInfo() {
        String tushkan="";
        if (Math.random()*1000<3) tushkan="У стен города следы непонятного зверя.";

        Upgrade up=Player.getPlayer().getNextUpgrade(upgrade);
        if (up!=null) {
            String need="!Нужен уровень города:"+up.getReqCityLev();

            String dop;
            if (up.getReqCityLev()>Level) dop="Требуется уровень города "+ up.getReqCityLev()+"\n";
            else if (up.getCost()>Player.getPlayer().getGold()) dop="Нужно больше золота!"+ up.getCost() +" золота!\n";
            else if (up.getLevel()>Player.getPlayer().getLevel()-1) dop="Вы недостаточно опытны!\n";
            else dop="Эффект:" + up.getDescription()+"\n";

            return "Это город " + Level + " уровня.\n В городе можно приобрести улучшение \"" + up.getName() + "\" за " +
                    up.getCost() + " золота.\n" + dop + tushkan;
        }
        else return "Это город "+ Level+" уровня.\n В городе можно приобрести улучшение \""+upgradeName+"\". "
                +tushkan;
    }

    public String getCityName(){return (Name+" lv."+Level) ;}

    private ObjectAction startRoute;
    private ObjectAction finishRoute;
    private ObjectAction butUpgrade;
    @Override
    public ArrayList<ObjectAction> getActions(boolean inZone) {
        ArrayList<ObjectAction> Actions=new ArrayList<>();
        if (!inZone) return Actions;
        if (startRoute==null)
            startRoute = new ObjectAction(this) {
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

                }
            };

        if (startRoute.isEnabled() && Player.getPlayer().getRouteStart()) Actions.add(startRoute);

        if (finishRoute==null)
        finishRoute = new ObjectAction(this) {
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
                Essages.addEssage("Завершен маршрут в город "+Name);
                GameSound.playSound(GameSound.FINISH_ROUTE_SOUND);
                serverConnect.getInstance().getPlayerInfo();
                serverConnect.getInstance().RefreshCurrent();
            }

            @Override
            public void postError() {

            }
        };
        if (finishRoute.isEnabled()&& !Player.getPlayer().getRouteStart()) Actions.add(finishRoute);

        if (butUpgrade==null)
            butUpgrade = new ObjectAction(this) {
                @Override
                public Bitmap getImage() {
                    return ImageLoader.getImage("buy_item");
                }

                @Override
                public String getInfo() {
                    return "Купить апгрейд "+upgrade;
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

                    Upgrade up=Player.getPlayer().getNextUpgrade(upgrade);
                    if (up!=null ) Essages.addEssage("Улучшение "+up.getName()+" куплено.");
                    else Essages.addEssage("Улучшение "+upgrade+" куплено.");
                }

                @Override
                public void postError() {

                }
            };
        Upgrade up=Player.getPlayer().getNextUpgrade(upgrade);
        if (up==null || (up.getReqCityLev()<=Level && up.getCost()<=Player.getPlayer().getGold() && up.getLevel()<Player.getPlayer().getLevel() ))
            Actions.add(butUpgrade);
        return Actions;
    }
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

    public String getUpgrade() {
        return upgrade;
    }
}
