package com.coe.c0r0vans.Logic;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import com.coe.c0r0vans.UIElements.ActionView;
import com.coe.c0r0vans.UIElements.ConfirmWindow;
import com.coe.c0r0vans.UIElements.GameObjectView;

import com.coe.c0r0vans.UIElements.TextWindow;
import com.coe.c0r0vans.UIElements.UIControler;
import com.google.android.gms.maps.GoogleMap;
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

import static com.coe.c0r0vans.R.mipmap.founder;

/**
 * Башня игрока
 */

public class Tower extends GameObject implements ActiveObject {
    private LatLng latlng;
    private Date updated;
    private int race=0;
    private int level=1;
    private String description;
    private String currentMarkName;
    private int obsidian;

    @Override
    public LatLng getPosition() {
        return latlng;
    }

    public Tower(){
        super();
    }
    public Tower(GoogleMap map,JSONObject object){
        super();
        this.map=map;
        try {
            loadJSON(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public Tower(JSONObject object){
        super();
        try {
            loadJSON(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public void loadJSON(JSONObject obj) throws JSONException {

        GUID = obj.getString("GUID");

        if (obj.has("Lat") && obj.has("Lng")) {
            int lat;
            lat = obj.getInt("Lat");
            int lng;
            lng = obj.getInt("Lng");
            latlng = new LatLng(lat / 1e6, lng / 1e6);
        }
        updated = new Date();
        if (obj.has("Name")) Name = obj.getString("Name"); else Name="";
        if (obj.has("Race")) {
            race = obj.getInt("Race");

        }
        if (race == 0) {
            owner = true;
            race= GameObjects.getPlayer().getRace();

        } else if (GUID.equals(GameObjects.getPlayer().getTower())){
            owner=true;
        } else if (Name.equals(GameObjects.getPlayer().getName())) owner=true;

        else owner = false;
        if (obj.has("Level")) {
            level = obj.getInt("Level");

        }
        if (obj.has("Text")) {
            description = obj.getString("Text");
        }
        if (mark == null) {
            setMarker(map.addMarker(new MarkerOptions().position(latlng)));

        } else {
            mark.setPosition(latlng);
        }
        changeMarkerSize();
        update();
        //TODO Нормальная обработка Итемов
        if (obj.has("Storage") && obj.get("Storage") instanceof JSONArray){
            JSONArray ar=obj.getJSONArray("Storage");
            for (int i=1;i<ar.length();i++){
                JSONObject item=ar.getJSONObject(i);
                if (item.has("Type") && item.has("Quanity")){
                    if ("Obsidian".equals(item.get("Type"))) obsidian=item.getInt("Quinity");
                }
            }
        }
    }

    @Override
    public void setMarker(Marker m) {
        super.setMarker(m);
    }

    @Override
    public void changeMarkerSize() {
        String markname;
        switch (race){
            case 1:
                markname="tower_1";
                break;
            case 2:
                markname="tower_2";
                break;
            case 3:
                markname="tower_3";
                break;
            default:
                markname="tower_1";
                break;
        }
        markname=markname+"_"+level;
        markname =markname + GameObject.zoomToPostfix(MyGoogleMap.getClientZoom());
        if (!markname.equals(currentMarkName)) {
            mark.setIcon(ImageLoader.getDescritor(markname));
            if ("Y".equals(GameSettings.getInstance().get("USE_TILT")))
                mark.setAnchor(0.5f, 1f);
            else mark.setAnchor(0.5f, 1f);
            currentMarkName=markname;
        }
    }
    private boolean visibility=true;
    @Override
    public void setVisibility(boolean visibility) {
        this.visibility=visibility;
        if (mark!=null) mark.setVisible(visibility);
    }
    ObjectAction setText=new ObjectAction(this) {
        @Override
        public Bitmap getImage() {
            return null;
        }

        @Override
        public String getCommand() {
            return "SetTowerText";
        }

        @Override
        public void preAction() {
        }

        @Override
        public void postAction(JSONObject response) {

        }

        @Override
        public void postError(JSONObject response) {

            try {
                setDescription("");
                String err;
                if (response.has("Error")) err = response.getString("Error");
                else if (response.has("Result")) err = response.getString("Result");
                else err = "U0000";
                switch (err) {
                    case "DB001":
                        Essages.addEssage(Essages.SYSTEM, "Ошибка сервера.");
                        break;
                    case "L0001":
                        Essages.addEssage(Essages.SYSTEM, "Соединение потеряно.");
                        GameObjects.getPlayer().setRouteStart(true);
                        break;
                    case "O1601":
                        Essages.addEssage(Essages.SYSTEM, "У вас нет Башни.");
                        break;
                    default:
                        GameObjects.getPlayer().setRouteStart(true);
                        if (response.has("Message"))
                            Essages.addEssage(Essages.SYSTEM, response.getString("Message"));
                        else Essages.addEssage(Essages.SYSTEM, "Непредвиденная ошибка.");

                }
            } catch (JSONException e) {
                GATracker.trackException("TowerSetText", e);
            }

        }
    };
    ObjectAction destroyTower=new ObjectAction(this) {
        @Override
        public Bitmap getImage() {
            return ImageLoader.getImage("destroy_tower");
        }

        @Override
        public String getCommand() {
            return "DestroyTower";
        }

        @Override
        public void preAction() {
            owner.setVisibility(false);
        }

        @Override
        public void postAction(JSONObject response) {
            owner.RemoveObject();
        }

        @Override
        public void postError(JSONObject response) {
            owner.setVisibility(true);
            try {
                String err;
                if (response.has("Error")) err=response.getString("Error");
                else if (response.has("Result")) err=response.getString("Result");
                else err="U0000";
                switch (err){
                    case "DB001":
                        Essages.addEssage(Essages.SYSTEM,"Ошибка сервера.");
                        break;
                    case "L0001":
                        Essages.addEssage(Essages.SYSTEM,"Соединение потеряно.");
                        break;
                    case "O1801":
                        Essages.addEssage(Essages.SYSTEM,"У вас нет башни.");
                        break;
                    default:
                        if (response.has("Message")) Essages.addEssage(Essages.SYSTEM,response.getString("Message"));
                        else Essages.addEssage(Essages.SYSTEM,"Непредвиденная ошибка.");

                }
            } catch (JSONException e) {
                GATracker.trackException("DestroyTower",e);
            }
        }
    };
    private ObjectAction upgradeTower=new ObjectAction(this) {
        @Override
        public Bitmap getImage() {
            return ImageLoader.getImage("upgrade_tower");
        }

        @Override
        public String getCommand() {
            return "UpgradeTower";
        }

        @Override
        public void preAction() {

        }

        @Override
        public void postAction(JSONObject response) {
            level=level+1;
            update();
            changeMarkerSize();
        }

        @Override
        public void postError(JSONObject response) {

            try {
                String err;
                if (response.has("Error")) err=response.getString("Error");
                else if (response.has("Result")) err=response.getString("Result");
                else err="U0000";
                switch (err){
                    case "DB001":
                        Essages.addEssage(Essages.SYSTEM,"Ошибка сервера.");
                        break;
                    case "L0001":
                        Essages.addEssage(Essages.SYSTEM,"Соединение потеряно.");
                        break;
                    case "O1901":
                        Essages.addEssage(Essages.SYSTEM,"У вас нет башни.");
                        break;
                    case "O1902":
                        Essages.addEssage(Essages.SYSTEM,"Не хватает обисидиана.");
                        break;
                    default:
                        if (response.has("Message")) Essages.addEssage(Essages.SYSTEM,response.getString("Message"));
                        else Essages.addEssage(Essages.SYSTEM,"Непредвиденная ошибка.");

                }
            } catch (JSONException e) {
                GATracker.trackException("DestroyTower",e);
            }
        }
    };

    private void setDescription(String description) {
        this.description = description;
    }

    public int getObsidian() {
        //TODO Obsidian
        return obsidian;
    }

    @Override
    public int getActionRadius() {
        return 30;
    }

    @Override
    public int getRadius() {
        return 30;
    }

    @Override
    public void useObject() {

        SelectedObject.getInstance().setTarget(this);
        SelectedObject.getInstance().setPoint(this.getPosition());
        UIControler.getActionLayout().ShowView();

    }

    @Override
    public RelativeLayout getObjectView(Context context){
        TowerWindow result=new TowerWindow(context);
        result.setTower(this);
        return result;
    }

    public void setObsidian(int obsidian) {
        this.obsidian = obsidian;
    }

    private class TowerWindow extends SwipeDetectLayout implements GameObjectView,ShowHideForm {
        Tower.TowerWindow self;
        Tower tower;
        private int currentCount=1;

        public TowerWindow(Context context) {
            super(context);

            init();

        }

        public TowerWindow(Context context, AttributeSet attrs) {
            super(context, attrs);
            init();
        }

        public TowerWindow(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            init();
        }
        public void init(){
            self=this;
            inflate(this.getContext(), R.layout.tower_layout, this);

            if ("Y".equals(GameSettings.getValue("NIGHT_MODE"))) {
                this.setBackgroundResource(R.drawable.layouts_night);
                //findViewById(R.id.scrollView6).setBackgroundResource(R.drawable.layouts_night);

            }
            else  {
                this.setBackgroundResource(R.drawable.layouts);
                //findViewById(R.id.scrollView6).setBackgroundResource(R.drawable.layouts);
            }
        }

        boolean loaded=true;


        public void setTower(Tower tower){
            this.tower=tower;
            if (loaded) apply();

        }



        private void apply() {
            update();

            TextView name=(TextView) findViewById(R.id.owner);
            name.setText(getName());
            //Obsidian
            findViewById(R.id.count_plus).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    count(currentCount+1);
                }
            });
            findViewById(R.id.count_minus).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    count(currentCount-1);
                }
            });
            SeekBar seekBar= (SeekBar) findViewById(R.id.count);
            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (!fromUser) {
                        return;
                    }
                    count(progress);

                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            findViewById(R.id.put_take).setOnClickListener(new OnClickListener() {
                LatLng position;
                int count;
                @Override
                public void onClick(View v) {
                    position=new LatLng(GameObjects.getPlayer().getPosition().latitude,GameObjects.getPlayer().getPosition().longitude);
                    count=currentCount;
                    if (!owner && race==GameObjects.getPlayer().getRace()) {
                        ConfirmWindow confirmWindow = new ConfirmWindow(getContext());
                        confirmWindow.setText(getContext().getString(R.string.confirm_put));
                        confirmWindow.setConfirmAction(new Runnable() {
                            @Override
                            public void run() {
                                ObjectAction put = new ObjectAction(tower) {

                                    @Override
                                    public Bitmap getImage() {
                                        return ImageLoader.getImage("obsidian");
                                    }

                                    @Override
                                    public String getCommand() {
                                        return "PutItems";
                                    }

                                    @Override
                                    public void preAction() {
                                        GameObjects.getPlayer().setObsidian(GameObjects.getPlayer().getObsidian() - count);
                                    }

                                    @Override
                                    public void postAction(JSONObject response) {
                                        Essages.addEssage(Essages.SYSTEM, "Обсидиан оставлен в Башне.");
                                    }

                                    @Override
                                    public void postError(JSONObject response) {
                                        try {

                                            String err;
                                            if (response.has("Error"))
                                                err = response.getString("Error");
                                            else if (response.has("Result"))
                                                err = response.getString("Result");
                                            else err = "U0000";
                                            switch (err) {
                                                case "DB001":
                                                    Essages.addEssage(Essages.SYSTEM, "Ошибка сервера.");
                                                    break;
                                                case "L0001":
                                                    Essages.addEssage(Essages.SYSTEM, "Соединение потеряно.");
                                                    break;
                                                case "O1701":
                                                    Essages.addEssage(Essages.SYSTEM, "Это не башня вашей Фракции.");
                                                    break;
                                                case "O1702":
                                                    Essages.addEssage(Essages.SYSTEM, "Башня далеко.");
                                                    break;
                                                case "O1703":
                                                    Essages.addEssage(Essages.SYSTEM, "Башни не существует.");
                                                    break;
                                                case "O1704":
                                                    Essages.addEssage(Essages.SYSTEM, "Не хватает обсидиана.");
                                                    break;
                                                default:
                                                    if (response.has("Message"))
                                                        Essages.addEssage(Essages.SYSTEM, response.getString("Message"));
                                                    else
                                                        Essages.addEssage(Essages.SYSTEM, "Непредвиденная ошибка.");

                                            }
                                        } catch (JSONException e) {
                                            GATracker.trackException("PutItems", e);
                                        }
                                    }
                                };
                                serverConnect.getInstance().putItems(put, (int) (position.latitude * 1e6), (int) (position.longitude * 1e6), tower.getGUID(), "Obsidian", currentCount);
                                update();
                            }
                        });
                        updateInZone(false);
                        confirmWindow.show();
                    } else
                        if(owner){
                            ObjectAction take = new ObjectAction(tower) {
                                int gCount = 0;

                                @Override
                                public Bitmap getImage() {
                                    return ImageLoader.getImage("obsidian");
                                }

                                @Override
                                public String getCommand() {
                                    return "TakeItems";
                                }

                                @Override
                                public void preAction() {
                                    gCount=tower.getObsidian();
                                    tower.setObsidian(0);
                                    update();
                                }

                                @Override
                                public void postAction(JSONObject response) {
                                    Essages.addEssage(Essages.SYSTEM, "Обсидиан получен из башни в Башне.");
                                    GameObjects.getPlayer().setObsidian(GameObjects.getPlayer().getObsidian() + gCount);

                                }

                                @Override
                                public void postError(JSONObject response) {
                                    try {
                                        tower.setObsidian(gCount);

                                        String err;
                                        if (response.has("Error"))
                                            err = response.getString("Error");
                                        else if (response.has("Result"))
                                            err = response.getString("Result");
                                        else err = "U0000";
                                        switch (err) {
                                            case "DB001":
                                                Essages.addEssage(Essages.SYSTEM, "Ошибка сервера.");
                                                break;
                                            case "L0001":
                                                Essages.addEssage(Essages.SYSTEM, "Соединение потеряно.");
                                                break;
                                            case "O1701":
                                                Essages.addEssage(Essages.SYSTEM, "Это не башня вашей Фракции.");
                                                break;
                                            case "O1702":
                                                Essages.addEssage(Essages.SYSTEM, "Башня далеко.");
                                                break;
                                            case "O1703":
                                                Essages.addEssage(Essages.SYSTEM, "Башни не существует.");
                                                break;
                                            case "O1704":
                                                Essages.addEssage(Essages.SYSTEM, "Не хватает обсидиана.");
                                                break;
                                            default:
                                                if (response.has("Message"))
                                                    Essages.addEssage(Essages.SYSTEM, response.getString("Message"));
                                                else
                                                    Essages.addEssage(Essages.SYSTEM, "Непредвиденная ошибка.");

                                        }
                                    } catch (JSONException e) {
                                        GATracker.trackException("PutItems", e);
                                    }
                                }
                            };
                            serverConnect.getInstance().takeItems(take, (int) (position.latitude * 1e6), (int) (position.longitude * 1e6), tower.getGUID(), "Obsidian");
                            update();
                        }
                }
            });

            //UpgradeTower
            findViewById(R.id.upgdadeTower).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmWindow confirmWindow=new ConfirmWindow(getContext());
                    confirmWindow.setText("Вы уверены что хотите улучшить башню? Это будет стоить 10 обсидиана.");
                    confirmWindow.setConfirmAction(new Runnable() {
                        @Override
                        public void run() {
                            int lat=(int)(GPSInfo.getInstance().getLatLng().latitude*1e6);
                            int lng=(int)(GPSInfo.getInstance().getLatLng().longitude*1e6);
                            serverConnect.getInstance().upgradeTower(upgradeTower,lat,lng,tower.getGUID());
                            update();
                        }
                    });
                    confirmWindow.show();

                }
            });
            //DestroyTower
            findViewById(R.id.destroyTower).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConfirmWindow confirmWindow=new ConfirmWindow(getContext());
                    confirmWindow.setText("Вы уверены что хотите уничтожить свою башню?");
                    confirmWindow.setConfirmAction(new Runnable() {
                        @Override
                        public void run() {
                            int lat=(int)(GPSInfo.getInstance().getLatLng().latitude*1e6);
                            int lng=(int)(GPSInfo.getInstance().getLatLng().longitude*1e6);
                            serverConnect.getInstance().destroyTower(destroyTower,lat,lng,tower.getGUID());
                            update();
                        }
                    });
                    confirmWindow.show();

                }
            });
            //DestroyTower
            findViewById(R.id.setDescription).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    final TextWindow confirmWindow=new TextWindow(getContext());
                    confirmWindow.setText("Укажите описание башни:");
                    confirmWindow.setConfirmAction(new Runnable() {
                        @Override
                        public void run() {
                            int lat=(int)(GPSInfo.getInstance().getLatLng().latitude*1e6);
                            int lng=(int)(GPSInfo.getInstance().getLatLng().longitude*1e6);
                            serverConnect.getInstance().setTowerText(setText,lat,lng,tower.getGUID(),confirmWindow.getText());
                            update();
                        }
                    });
                    confirmWindow.show();

                }
            });



            findViewById(R.id.toggleDesc).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToggleButton b = (ToggleButton) findViewById(R.id.toggleDesc);
                    b.setChecked(true);
                    b = (ToggleButton) findViewById(R.id.toggleWare);
                    b.setChecked(false);
                    findViewById(R.id.descriptionPanel).setVisibility(VISIBLE);
                    findViewById(R.id.warehouse).setVisibility(GONE);
                }
            });
            findViewById(R.id.toggleWare).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToggleButton b = (ToggleButton) findViewById(R.id.toggleDesc);
                    b.setChecked(false);
                    b = (ToggleButton) findViewById(R.id.toggleWare);
                    b.setChecked(true);
                    findViewById(R.id.descriptionPanel).setVisibility(GONE);
                    findViewById(R.id.warehouse).setVisibility(VISIBLE);
                }
            });
            this.setOnSwipeListener(new OnSwipeListener() {
                @Override
                public void onSwipeLeft() {
                    ToggleButton toggleDesc = (ToggleButton) findViewById(R.id.toggleDesc);
                    ToggleButton toggleWare = (ToggleButton) findViewById(R.id.toggleWare);
                    if (toggleWare.isChecked())
                    {
                        toggleWare.setChecked(false);
                        toggleDesc.setChecked(true);
                        findViewById(R.id.descriptionPanel).setVisibility(VISIBLE);
                        findViewById(R.id.warehouse).setVisibility(GONE);
                    }
                }

                @Override
                public void onSwipeRight() {
                    ToggleButton toggleDesc = (ToggleButton) findViewById(R.id.toggleDesc);
                    ToggleButton toggleWare = (ToggleButton) findViewById(R.id.toggleWare);
                    if (toggleDesc.isChecked() && toggleWare.getVisibility()==VISIBLE)
                    {
                        toggleDesc.setChecked(false);
                        toggleWare.setChecked(true);
                        findViewById(R.id.warehouse).setVisibility(VISIBLE);
                        findViewById(R.id.descriptionPanel).setVisibility(GONE);

                    }
                }

                @Override
                public void onSwipeUp() {

                }

                @Override
                public void onSwipeDown() {

                }
            });
            findViewById(R.id.closeActionButton).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    close();
                }
            });
            count(1);


        }
        private void update(){
            TextView level=(TextView) findViewById(R.id.level);
            level.setText("Башня "+tower.level+" уровня");
            TextView desc= (TextView) findViewById(R.id.description);
            if (!desc.isFocused()) desc.setText(description);
            if (tower.owner)
                ((TextView)findViewById(R.id.obsidian_count)).setText(String.format(getContext().getString(R.string.take_obsidian), tower.getObsidian()));
        }
        private boolean count(int newValue){
            if (tower.owner){
                ((TextView)findViewById(R.id.obsidian_count)).setText(String.format(getContext().getString(R.string.take_obsidian), tower.getObsidian()));
                return true;
            } else
            {
                if (newValue<=GameObjects.getPlayer().getObsidian() && newValue>=1) {
                    currentCount = newValue;

                } else if (newValue<1){
                    currentCount=0;
                } else if (newValue>GameObjects.getPlayer().getObsidian()) {
                    currentCount = GameObjects.getPlayer().getObsidian();
                }
                ((TextView) findViewById(R.id.obsidian_count)).setText(String.format(getContext().getString(R.string.put_obsidian), tower.getObsidian(), GameObjects.getPlayer().getObsidian()));
                SeekBar seekBar = (SeekBar) findViewById(R.id.count);
                seekBar.setProgress(currentCount);
                seekBar.setMax(GameObjects.getPlayer().getObsidian());
                return currentCount==newValue;
            }
        }

        public void updateInZone(boolean inZone){

            ToggleButton toggleWare = (ToggleButton) findViewById(R.id.toggleWare);
            Button obs_plus = (Button) findViewById(R.id.count_plus);
            Button obs_minus = (Button) findViewById(R.id.count_minus);
            SeekBar obs_count = (SeekBar) findViewById(R.id.count);
            ImageButton destroyTower = (ImageButton) findViewById(R.id.destroyTower);
            ImageButton setText = (ImageButton) findViewById(R.id.setDescription);
            ImageButton upgradeTower = (ImageButton) findViewById(R.id.upgdadeTower);
            ImageButton putTake = (ImageButton) findViewById(R.id.put_take);
            //Если владелец башни показать кнопки
            if (tower.owner) {
                destroyTower.setVisibility(VISIBLE);
                setText.setVisibility(VISIBLE);
                upgradeTower.setVisibility(VISIBLE);
                toggleWare.setVisibility(VISIBLE);
                obs_plus.setVisibility(INVISIBLE);
                obs_minus.setVisibility(INVISIBLE);
                obs_count.setVisibility(INVISIBLE);
                putTake.setVisibility(INVISIBLE);
            } else if (race == GameObjects.getPlayer().getRace()) {
                destroyTower.setVisibility(GONE);
                setText.setVisibility(GONE);
                upgradeTower.setVisibility(GONE);
                toggleWare.setVisibility(VISIBLE);
                obs_plus.setVisibility(VISIBLE);
                obs_minus.setVisibility(VISIBLE);
                obs_count.setVisibility(VISIBLE);
                putTake.setVisibility(VISIBLE);
            } else {
                destroyTower.setVisibility(INVISIBLE);
                setText.setVisibility(INVISIBLE);
                upgradeTower.setVisibility(INVISIBLE);
                toggleWare.setVisibility(INVISIBLE);
                putTake.setVisibility(INVISIBLE);
            }
            if (inZone) {
                destroyTower.setEnabled(true);
                setText.setEnabled(true);
                upgradeTower.setEnabled(true);
                putTake.setEnabled(true);

            } else
            {
                destroyTower.setEnabled(false);
                setText.setEnabled(false);
                upgradeTower.setEnabled(false);
                putTake.setEnabled(false);
            }
        }
        public void close(){
            this.setVisibility(GONE);
            tower=null;
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
            update();
        }

        @Override
        public void Hide() {
            close();
        }
    }
}
