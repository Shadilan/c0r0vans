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
public class City implements GameObject{
    private Marker mark;
    private String GUID;
    private String CityName;
    private int Level=0;
    private int radius=100;
    private String upgrade;
    private String upgradeName;
    private Bitmap image;
    private GoogleMap map;
    private Circle zone;
    public String getGUID() {
        return GUID;
    }

    public City(GoogleMap map,JSONObject obj) throws JSONException {
        this.map=map;
        int Lat=obj.getInt("Lat");
        int Lng=obj.getInt("Lng");
        image=ImageLoader.getImage("city");

        mark=map.addMarker(new MarkerOptions().position(new LatLng(Lat / 1e6, Lng / 1e6)));
        changeMarkerSize((int) map.getCameraPosition().zoom);
        mark.setAnchor(0.5f,1);
        loadJSON(obj);


    }


    @Override
    public Bitmap getImage() {
        return image;
    }

    @Override
    public Marker getMarker() {
        return mark;
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
            if (obj.has("Name")) CityName=obj.getString("Name");
            if (obj.has("UpgradeType")) upgrade=obj.getString("UpgradeType");
            if (obj.has("UpgradeName")) upgradeName=obj.getString("UpgradeName");
            if (obj.has("Level")) Level=obj.getInt("Level");
            if (obj.has("Radius")) radius=obj.getInt("Radius");
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
    }

    @Override
    public String getInfo() {
        String tushkan="";
        if (Math.random()*100<5) tushkan="У стен города следы непонятного зверя.";
        return "Это город "+ Level+" уровня. В городе можно приобрести \""+upgradeName+"\"."+tushkan;
    }

    public String getCityName(){return (CityName+" lv."+Level) ;}

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
                    GameSound.playSound(GameSound.START_ROUTE_SOUND);
                }

                @Override
                public void postAction() {

                    Essages.addEssage("Начат маршрут в город " + CityName);
                    serverConnect.getInstance().getPlayerInfo();
                }

                @Override
                public void postError() {

                }
            };
        Player player= (Player) SelectedObject.getInstance().getExecuter();
        if (startRoute.isEnabled() && player.getCurrentRoute().equals("")) Actions.add(startRoute);

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
                GameSound.playSound(GameSound.FINISH_ROUTE_SOUND);
            }

            @Override
            public void postAction() {
                Essages.addEssage("Завершен маршрут в город "+CityName);
                serverConnect.getInstance().getPlayerInfo();
                serverConnect.getInstance().RefreshCurrent();
            }

            @Override
            public void postError() {

            }
        };
        if (finishRoute.isEnabled()&& !player.getCurrentRoute().equals("")) Actions.add(finishRoute);

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
                    GameSound.playSound(GameSound.BUY_SOUND);
                }

                @Override
                public void postAction() {
                    serverConnect.getInstance().getPlayerInfo();
                    Essages.addEssage("Улучшение "+upgrade+" куплено.");
                }

                @Override
                public void postError() {

                }
            };
        Actions.add(butUpgrade);
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
