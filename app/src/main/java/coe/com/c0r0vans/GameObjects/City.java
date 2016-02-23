package coe.com.c0r0vans.GameObjects;

import android.graphics.Bitmap;
import android.graphics.Color;

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

import coe.com.c0r0vans.GameSound;
import coe.com.c0r0vans.R;
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
        changeMarkerSize((int) map.getCameraPosition().zoom);
        mark.setAnchor(0.5f, 1);
        loadJSON(obj);


    }


    @Override
    public void setMarker(Marker m) {
        mark=m;
        changeMarkerSize((int) map.getCameraPosition().zoom);
        mark.setAnchor(0.5f, 1);
    }

    @Override
    public void loadJSON(JSONObject obj) {
        try {
            GUID=obj.getString("GUID");
            int Lat=obj.getInt("Lat");
            int Lng=obj.getInt("Lng");
            LatLng latlng=new LatLng(Lat / 1e6, Lng / 1e6);
            if (mark==null) {
                setMarker(map.addMarker(new MarkerOptions().position(latlng)));

            } else {
                mark.setPosition(latlng);
            }
            if (obj.has("Name")) Name=obj.getString("Name");
            if (obj.has("UpgradeType")) upgrade=obj.getString("UpgradeType");
            if (obj.has("UpgradeName")) upgradeName=obj.getString("UpgradeName");
            if (obj.has("Level")) Level=obj.getInt("Level");
            if (obj.has("Radius")) radius=obj.getInt("Radius");
            if (obj.has("Progress")) progress=obj.getInt("Progress");
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
        Upgrade up=((Player) SelectedObject.getInstance().getExecuter()).getNextUpgrade(upgrade);
        if (up!=null) {
            String need="!Нужен уровень города:"+up.getReqCityLev();
            if (up.getReqCityLev()>Level) return "Это город " + Level + " уровня.\n В городе можно приобрести улучшение \"" + up.getName() + "\" за " +
                    up.getCost() + " золота.\n" + need;
            else
            return "Это город " + Level + " уровня.\n В городе можно приобрести улучшение \"" + up.getName() + "\" за " +
                    up.getCost() + " золота.\n" + "Эффект:" + up.getDescription() + tushkan;
        }
        else return "Это город "+ Level+" уровня.\n В городе можно приобрести улучшение \""+upgradeName+"\". "
                +tushkan;
    }

    public String getCityName(){return (Name+" lv."+Level) ;}

    private ObjectAction startRoute;
    private ObjectAction finishRoute;
    private ObjectAction butUpgrade;
    @Override
    public ArrayList<ObjectAction> getActions() {
        ArrayList<ObjectAction> Actions=new ArrayList<>();

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
                    Player player= (Player) SelectedObject.getInstance().getExecuter();
                    player.setRouteStart(false);
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
        Player player= (Player) SelectedObject.getInstance().getExecuter();
        if (startRoute.isEnabled() && player.getRouteStart()) Actions.add(startRoute);

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
                Player player= (Player) SelectedObject.getInstance().getExecuter();
                player.setRouteStart(true);
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
        if (finishRoute.isEnabled()&& !player.getRouteStart()) Actions.add(finishRoute);

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
                    Essages.addEssage("Улучшение "+upgrade+" куплено.");
                }

                @Override
                public void postError() {

                }
            };
        Upgrade up=((Player) SelectedObject.getInstance().getExecuter()).getNextUpgrade(upgrade);
        if (up==null || (up!=null && up.getReqCityLev()<=Level)) Actions.add(butUpgrade);
        return Actions;
    }
    @Override
    public void changeMarkerSize(int Type) {
        switch (Type){
            case GameObject.ICON_SMALL: mark.setIcon(ImageLoader.getDescritor("city_s"));
                break;
            case GameObject.ICON_MEDIUM: mark.setIcon(ImageLoader.getDescritor("city_m"));
                break;
            case GameObject.ICON_LARGE: mark.setIcon(ImageLoader.getDescritor("city"));
                break;
            default:mark.setIcon(ImageLoader.getDescritor("city"));
                Essages.addEssage("Ваш зум не корректен.");
                break;
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
