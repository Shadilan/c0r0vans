package com.coe.c0r0vans.GameObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coe.c0r0vans.MyGoogleMap;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.UIElements.ActionView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import utility.GATracker;
import utility.GPSInfo;
import utility.GameSound;
import utility.ImageLoader;
import utility.internet.serverConnect;
import utility.notification.Essages;
import utility.settings.GameSettings;

/**
 * @author Shadilan
 */
public class Ambush extends GameObject {


    private int faction;
    private int radius=30;

    private int ready=0;
    private int life=1;

    public  Ambush(GoogleMap map,JSONObject obj)
    {
        this.map=map;
        loadJSON(obj);
    }
    @Override
    public Bitmap getImage() {
        return null;
    }


    @Override
    public void setMarker(Marker m) {
        mark=m;
        mark.setAnchor(0.5f, 1);
    }

    @Override
    public void loadJSON(JSONObject obj) {
        try {
            GUID=obj.getString("GUID");
            int Lat=obj.getInt("Lat");
            int Lng=obj.getInt("Lng");
            LatLng latlng=new LatLng(Lat / 1e6, Lng / 1e6);
            if (obj.has("Owner")) faction=obj.getInt("Owner");
            if (faction<0 ||faction>4) faction=4;
            if (obj.has("Radius")) radius=obj.getInt("Radius");
            if (obj.has("Ready")) ready=obj.getInt("Ready");
            if (obj.has("Name")) Name="Засада "+obj.getString("Name");
            if (obj.has("Life")) life=obj.getInt("Life");
            if (mark==null) {
                setMarker(map.addMarker(new MarkerOptions().position(new LatLng(Lat / 1e6, Lng / 1e6))));
                changeMarkerSize();
            } else {
                mark.setPosition(latlng);
                setVisibility(true);
                changeMarkerSize();
            }
            if (zone==null){
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latlng);
                circleOptions.radius(radius);
                circleOptions.zIndex(130);
                switch (faction){
                    case 0:
                        circleOptions.strokeColor(Color.BLUE);
                        break;
                    case 1:
                        circleOptions.strokeColor(Color.MAGENTA);
                        break;
                    case 2:
                        circleOptions.strokeColor(Color.RED);
                        break;
                    case 3:
                        circleOptions.strokeColor(Color.YELLOW);
                        break;
                    default:
                        circleOptions.strokeColor(Color.GREEN);
                }
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
    public JSONObject getJSON() throws JSONException {
        JSONObject object=new JSONObject();
        object.put("GUID",GUID);
        if (mark!=null){
           object.put("Lat",(int)(mark.getPosition().latitude*1e6));
            object.put("Lng",(int)(mark.getPosition().longitude*1e6));

        }
        object.put("Owner",faction);
        object.put("Radius",radius);
        object.put("Ready",ready);
        object.put("Name",Name);
        return object;
    }


    private String getInfo() {
        String count=" Здесь вы видите "+(life)+" наемников.";
        if (faction==0) {
            String dop="";
            Upgrade up=Player.getPlayer().getUpgrade("ambushes");
            if (up!=null && getRadius()<up.getEffect1()) dop="\nРазмер засады меньше чем вы можете организовать.";

            if (ready < 0)
                return "Ваши верные воины разбивают здесь засаду. Работать еще приблизительно " + (ready * -1) + " минут."+count+dop;
            else if (ready / 60 > 2)
                return "Ваши верные воины ждут здесь вражеских контрабандистов в Засаде. Стоят приблизительно " + (Math.round(ready / 60)) + " часов."+count+dop;
            else if (ready / 60 / 24 > 2)
                return "Ваши верные воины ждут здесь вражеских контрабандистов в Засаде. Стоят приблизительно " + (Math.round(ready / 60 / 24)) + " дней."+count+dop;
            else return "Ваши верные воины ждут здесь вражеских контрабандистов в Засаде."+count+dop;
        }
        else if (faction==Player.getPlayer().getRace()){
            if (ready < 0)
                return "Ваши соратники разбивают здесь засаду. Работать еще приблизительно " + (ready * -1) + " минут."+count;
            else if (ready / 60 > 2)
                return "Ваши соратники ждут здесь вражеских контрабандистов в Засаде. Cтоят приблизительно " + (Math.round(ready / 60)) + " часов."+count;
            else if (ready / 60 / 24 > 2)
                return "Ваши соратники ждут здесь вражеских контрабандистов в Засаде. Cтоят приблизительно " + (Math.round(ready / 60 / 24)) + " дней."+count;
            else return "Ваши соратники ждут здесь вражеских контрабандистов в Засаде."+count;
        } else {
            if (ready < 0)
                return "Разбойники решили разбить здесь лагерь. Пока они заняты и есть приблизительно " + (ready * -1) + " минут."+count;
            else if (ready / 60 > 2)
                return "Засада ожидает здесь неосторожных караванщиков. Cтоят приблизительно " + (Math.round(ready / 60)) + " часов."+count;
            else if (ready / 60 / 24 > 2)
                return "Засада ожидает здесь неосторожных караванщиков. Cтоят приблизительно " + (Math.round(ready / 60 / 24)) + " дней."+count;
            else return "Засада ожидает здесь неосторожных караванщиков."+count;
        }


    }

    //ObjectAction removeAmbush;

    private String currentMarkName;
    @Override
    public void changeMarkerSize() {
        if (mark != null) {
            float type= MyGoogleMap.getClientZoom();
            String mark_name = "ambush";
            if (ready<0) mark_name = mark_name + "_build";
            if (faction==0) mark_name=mark_name+"_"+faction+Player.getPlayer().getRace();
            else mark_name = mark_name + "_"+faction;
            mark_name = mark_name + GameObject.zoomToPostfix(type);
            if (!mark_name.equals(currentMarkName)) {
                mark.setIcon(ImageLoader.getDescritor(mark_name));
                if ("Y".equals(GameSettings.getInstance().get("USE_TILT")))
                    mark.setAnchor(0.5f, 1f);
                else mark.setAnchor(0.5f, 0.5f);
                currentMarkName=mark_name;
            }
        }
    }

    @Override
    public void setVisibility(boolean visibility) {
        if ("Y".equals(GameSettings.getInstance().get("SHOW_AMBUSH_RADIUS"))) {
            if (zone != null)
                zone.setVisible(visibility);
        }
        else if (zone!=null) zone.setVisible(false);
        if (mark!=null) mark.setVisible(visibility);
    }

    public void showRadius(){
        String opt= GameSettings.getInstance().get("SHOW_AMBUSH_RADIUS");
        if (opt.equals("Y") && mark!=null && mark.isVisible()){
            zone.setVisible(true);
        } else
        {
            zone.setVisible(false);
        }
    }
    public int getFaction() {
        return faction;
    }

    class AmbushLayout extends RelativeLayout implements GameObjectView{

        public AmbushLayout(Context context) {
            super(context);
            init();
        }

        public AmbushLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public AmbushLayout(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }
        private void init(){
            inflate(this.getContext(), R.layout.ambush_layout, this);
        }
        Ambush ambush;



        public void setAmbush(Ambush ambush){
            this.ambush=ambush;
            applyAmbush();
        }
        private ObjectAction removeAction;
        private void applyAmbush() {
            int f=ambush.getFaction();
            if (f==0) f=Player.getPlayer().getRace();
            if (f<1 || f>4) f=4;
            switch (f) {
                case 3:
                    ((ImageView)findViewById(R.id.ambushFaction)).setImageResource(R.mipmap.legue);
                    break;
                case 2:
                    ((ImageView)findViewById(R.id.ambushFaction)).setImageResource(R.mipmap.alliance);
                    break;
                case 1:
                    ((ImageView)findViewById(R.id.ambushFaction)).setImageResource(R.mipmap.guild);
                    break;
                default:
                    ((ImageView)findViewById(R.id.ambushFaction)).setImageResource(R.mipmap.neutral);
                    break;
            }
            ((TextView)findViewById(R.id.ambushDesc)).setText(ambush.getInfo());
            final ImageButton removeButton=(ImageButton)findViewById(R.id.ambushActionBtn);
            if (ambush.getFaction()==0)
            {
                removeButton.setImageResource(R.mipmap.dismiss);
                removeAction = new ObjectAction(ambush){
                    @Override
                    public Bitmap getImage() {
                        return ImageLoader.getImage("remove_ambush");
                    }



                    @Override
                    public String getCommand() {
                        return "CancelAmbush";
                    }

                    @Override
                    public void preAction() {
                        owner.setVisibility(false);
                    }

                    @Override
                    public void postAction(JSONObject response) {
                        GameSound.playSound(GameSound.REMOVE_AMBUSH);
                        Player.getPlayer().setAmbushLeft(Player.getPlayer().getAmbushLeft() + 1);
                        Player.getPlayer().setHirelings(Player.getPlayer().getHirelings()+owner.getLife()*5);
                        AmbushItem sa=null;
                        for (AmbushItem ai:Player.getPlayer().getAmbushes()){
                            if (ai.getGUID().equals(owner.getGUID())) {
                                sa=ai;
                                break;
                            }
                        }
                        if (sa!=null) Player.getPlayer().getAmbushes().remove(sa);
                        Essages.addEssage("Засада распущена");
                        owner.RemoveObject();
                    }

                    @Override
                    public void postError(JSONObject response) {
                        owner.setVisibility(true);
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
                                case "O0401":
                                    Essages.addEssage("Эта засада уже уничтожена.");
                                    owner.RemoveObject();
                                    break;
                                default:
                                    if (response.has("Message"))
                                        Essages.addEssage(response.getString("Message"));
                                    else Essages.addEssage("Непредвиденная ошибка.");

                            }
                        }catch (JSONException e)
                        {
                            GATracker.trackException("CancelAmbush",e);
                        }
                    }
                };
                removeButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {

                            serverConnect.getInstance().callCanelAmbush(removeAction,getGUID());


                        close();
                    }

                });
            } else if(ambush.getFaction()==Player.getPlayer().getRace()){
                removeButton.setEnabled(false);
            } else{
                removeButton.setImageResource(R.mipmap.rem_ambush);
                removeAction = new ObjectAction(ambush) {
                    @Override
                    public Bitmap getImage() {
                        return ImageLoader.getImage("attack_ambush");
                    }

                    @Override
                    public String getCommand() {
                        return "DestroyAmbush";
                    }

                    @Override

                    public void preAction() {

                        owner.setVisibility(false);
                    }

                    @Override
                    public void postAction(JSONObject response) {

                        GameSound.playSound(GameSound.KILL_SOUND);
                        if (response.has("Message")) try {
                            Essages.addEssage(response.getString("Message"));
                        } catch (JSONException e) {
                            GATracker.trackException("ObjectAction",e);
                        }
                        else Essages.addEssage("Разбойники уничтожены.");
                        owner.RemoveObject();
                    }

                    @Override
                    public void postError(JSONObject response) {
                        owner.setVisibility(true);
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
                                case "O0301":
                                    Essages.addEssage("Эта засада уже уничтожена.");
                                    owner.RemoveObject();
                                    break;
                                case "O0302":
                                    Essages.addEssage("Засада слишком далеко.");
                                    break;
                                case "O0303":
                                    Essages.addEssage("Это ваши соратники.");
                                    break;
                                case "O0304":
                                    Essages.addEssage("Не хватает наемников для уничтожения засады.");
                                    break;
                                default:
                                    if (response.has("Message"))
                                        Essages.addEssage(response.getString("Message"));
                                    else Essages.addEssage("Непредвиденная ошибка.");

                            }
                        }catch (JSONException e)
                        {
                            GATracker.trackException("DestroyAmbush",e);
                        }
                    }
                };

            }
            removeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (ambush.getMarker()!=null) {
                            serverConnect.getInstance().callDestroyAmbush(removeAction,GPSInfo.getInstance().GetLat(),
                                    GPSInfo.getInstance().GetLng(),getGUID());
                    }
                    close();
                }

            });
            removeButton.setVisibility(INVISIBLE);
            findViewById(R.id.closeActionButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    close();
                }
            });

        }

        @Override
        public void updateInZone(boolean inZone) {
            ImageButton removeButton=(ImageButton)findViewById(R.id.ambushActionBtn);
            if (ambush.getFaction()==0)
            {

                removeButton.setVisibility(VISIBLE);

            } else if(ambush.getFaction()==Player.getPlayer().getRace()){
                removeButton.setVisibility(GONE);
            } else{
                if (inZone) removeButton.setVisibility(VISIBLE);
                else removeButton.setVisibility(GONE);

            }
        }

        @Override
        public void close() {
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
        AmbushLayout result=new AmbushLayout(context);
        result.setAmbush(this);
        return result;
    }
    public int getRadius(){
        return this.radius;
    }
    public Circle getZone(){
        return zone;
    }
}
