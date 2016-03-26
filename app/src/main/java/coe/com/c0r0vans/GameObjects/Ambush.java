package coe.com.c0r0vans.GameObjects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
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
public class Ambush extends GameObject {


    private int faction;
    private int radius=30;
    private Circle zone;

    private int ready=0;

    public  Ambush(GoogleMap map,JSONObject obj)
    {
        Log.d("Debug info","Ambush loaded.");
        this.map=map;
        loadJSON(obj);
        changeMarkerSize(MyGoogleMap.getClientZoom());

    }
    @Override
    public Bitmap getImage() {
        return ImageLoader.getImage("ambush");
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
            if (obj.has("Progress")) progress=obj.getInt("Progress");
            if (obj.has("Name")) Name="Засада "+obj.getString("Name");
            if (mark==null) {
                setMarker(map.addMarker(new MarkerOptions().position(new LatLng(Lat / 1e6, Lng / 1e6))));
                changeMarkerSize(MyGoogleMap.getClientZoom());
            } else {
                mark.setPosition(latlng);
            }
            if (zone==null){
                CircleOptions circleOptions = new CircleOptions();
                circleOptions.center(latlng);
                circleOptions.radius(radius);
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

    @Override
    public void RemoveObject() {
        mark.remove();
        if (zone!=null) zone.remove();
    }

    @Override
    public String getInfo() {
        if (faction==0) {
            if (ready < 0)
                return "Ваши верные войны разбивают здесь засаду. Работать еще " + (ready * -1) + " минут.";
            else if (ready / 60 > 2)
                return "Ваши верные войны ждут здесь вражеских контрабандистов в Засаде. Стоят уже" + (Math.round(ready / 60)) + " часов.";
            else if (ready / 60 / 24 > 2)
                return "Ваши верные войны ждут здесь вражеских контрабандистов в Засаде. Стоят уже" + (Math.round(ready / 60 / 24)) + " дней.";
            else return "Ваши верные войны ждут здесь вражеских контрабандистов в Засаде.";
        }
        else if (faction==Player.getPlayer().getRace()){
            if (ready < 0)
                return "Ваши соратники разбивают здесь засаду. Работать еще " + (ready * -1) + " минут.";
            else if (ready / 60 > 2)
                return "Ваши соратники ждут здесь вражеских контрабандистов в Засаде. По виду стоят уже " + (Math.round(ready / 60)) + " часов.";
            else if (ready / 60 / 24 > 2)
                return "Ваши соратники ждут здесь вражеских контрабандистов в Засаде. По виду стоят уже " + (Math.round(ready / 60 / 24)) + " дней.";
            else return "Ваши соратники ждут здесь вражеских контрабандистов в Засаде.";
        } else {
            if (ready < 0)
                return "Разбойники решили разбить здесь лагерь. Пока они заняты и есть еще " + (ready * -1) + " минут.";
            else if (ready / 60 > 2)
                return "Засада ожидает здесь не осторожных караванщиков. По виду стоят уже " + (Math.round(ready / 60)) + " часов.";
            else if (ready / 60 / 24 > 2)
                return "Засада ожидает здесь не осторожных караванщиков. По виду стоят уже " + (Math.round(ready / 60 / 24)) + " дней.";
            else return "Засада ожидает здесь не осторожных караванщиков.";
        }


    }

    ObjectAction removeAmbush;


    @Override
    public void changeMarkerSize(float Type) {
        if (mark != null) {
            String markname = "ambush";
            if (ready<0) markname = markname + "_build";
            if (faction==0) markname=markname+"_"+faction+Player.getPlayer().getRace();
            else markname = markname + "_"+faction;
            markname = markname + GameObject.zoomToPostfix(Type);
            mark.setIcon(ImageLoader.getDescritor(markname));
            if ("Y".equals(GameSettings.getInstance().get("USE_TILT"))) mark.setAnchor(0.5f, 1f);
            else mark.setAnchor(0.5f, 0.5f);
        }
    }

    @Override
    public void setVisibility(boolean visibility) {
        zone.setVisible(visibility);
        mark.setVisible(visibility);
    }

    public void showRadius(){
        String opt= GameSettings.getInstance().get("SHOW_AMBUSH_RADIUS");
        if (opt.equals("Y")){
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
            inflate(this.getContext(),R.layout.ambush_layout,this);
        }
        Ambush ambush;
        boolean loaded=true;



        public void setAmbush(Ambush ambush){
            this.ambush=ambush;
            if (loaded){
                applyAmbush();
            }
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
            ImageButton removeButton=(ImageButton)findViewById(R.id.ambushActionBtn);
            if (ambush.getFaction()==0)
            {
                removeButton.setImageResource(R.mipmap.dismiss);
                removeAction = new ObjectAction(ambush) {
                    @Override
                    public Bitmap getImage() {
                        return ImageLoader.getImage("remove_ambush");
                    }

                    @Override
                    public String getInfo() {
                        return "Убрать засаду.";
                    }

                    @Override
                    public String getCommand() {
                        return "CancelAmbush";
                    }

                    @Override
                    public void preAction() {

                        owner.getMarker().setVisible(false);
                        zone.setVisible(false);
                    }

                    @Override
                    public void postAction() {
                        GameSound.playSound(GameSound.REMOVE_AMBUSH);
                        Essages.addEssage("Засада распущена");
                        owner.RemoveObject();
                    }

                    @Override
                    public void postError() {
                        owner.getMarker().setVisible(true);
                        zone.setVisible(true);
                    }
                };
                removeButton.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        serverConnect.getInstance().ExecCommand(removeAction,
                                ambush.getGUID(),
                                GPSInfo.getInstance().GetLat(),
                                GPSInfo.getInstance().GetLng(),
                                (int)(ambush.getMarker().getPosition().latitude*1e6),
                                (int)(ambush.getMarker().getPosition().longitude*1e6));
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
                    public String getInfo() {
                        return "Убрать засаду.";
                    }

                    @Override
                    public String getCommand() {
                        return "DestroyAmbush";
                    }

                    @Override

                    public void preAction() {

                        owner.getMarker().setVisible(false);
                        zone.setVisible(false);
                    }

                    @Override
                    public void postAction() {
                        GameSound.playSound(GameSound.KILL_SOUND);
                        Essages.addEssage("Разбойники уничтожены.");
                        owner.RemoveObject();
                    }

                    @Override
                    public void postError() {
                        owner.getMarker().setVisible(true);
                        zone.setVisible(true);
                    }
                };

            }
            removeButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverConnect.getInstance().ExecCommand(removeAction,
                            ambush.getGUID(),
                            GPSInfo.getInstance().GetLat(),
                            GPSInfo.getInstance().GetLng(),
                            (int)(ambush.getMarker().getPosition().latitude*1e6),
                            (int)(ambush.getMarker().getPosition().longitude*1e6));
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
    }
    public RelativeLayout getObjectView(Context context){
        AmbushLayout result=new AmbushLayout(context);
        result.setAmbush(this);
        return result;
    }
}
