package com.coe.c0r0vans.UIElements;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.coe.c0r0vans.GameObject.GameObject;
import com.coe.c0r0vans.GameObject.OnGameObjectChange;
import com.coe.c0r0vans.GameObjects.LevelTable;
import com.coe.c0r0vans.GameObjects.Message;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.Singles.GameObjects;
import com.coe.c0r0vans.Singles.MyGoogleMap;
import com.coe.c0r0vans.UIElements.InfoLayout.InfoLayout;
import com.coe.c0r0vans.UIElements.MessageLayout.EssageLine;
import com.coe.c0r0vans.UIElements.MessageLayout.MessageLayout;

import org.json.JSONObject;

import utility.GATracker;
import utility.MainThread;
import utility.StringUtils;
import utility.internet.ServerListener;
import utility.internet.serverConnect;
import utility.notification.Essages;
import utility.notification.OnEssageListener;
import utility.settings.GameSettings;
import utility.settings.SettingsListener;

/**
 * Элементы интерфейса
 */
public class ButtonLayout extends RelativeLayout {
    private InfoLayout infoLayout;
    private ScrollView scrollView;
    private MessageLayout messageLayout;
    //События должны приходить на основной слой

    public ButtonLayout(Context context) {
        super(context);
        init();
    }

    public ButtonLayout(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public ButtonLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    public void init(){
        inflate(getContext(), R.layout.main_button, this);
        try {
            afterInit();
        } catch (Exception e){
            GATracker.trackException("ButtonLayout",e);
        }
    }
    private TextView zoomCnt;
    private void afterInit() {
        MyGoogleMap.setShowpointButton((ImageButton) findViewById(R.id.showPosButton));
        infoLayout = new InfoLayout(getContext());
        infoLayout.init();
        ImageView PlayerInfo = (ImageView) findViewById(R.id.infoview);

        PlayerInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoLayout.Show();
            }
        });

        final ImageView Settings = (ImageView) findViewById(R.id.settings);
        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new utility.settings.Settings(getContext()).show();


            }
        });
        loadFromPlayer();
        if ("Y".equals(GameSettings.getValue("SHOW_BUILD_AREA"))) {
            findViewById(R.id.foundedTitle).setVisibility(VISIBLE);
            findViewById(R.id.foundedAmount).setVisibility(VISIBLE);
        }
        if ("Y".equals(GameSettings.getValue("NIGHT_MODE"))){
            TextView textView = (TextView) findViewById(R.id.ZoomCnt);
            textView.setTextColor(Color.GRAY);
            textView = (TextView) findViewById(R.id.QueueCnt);
            textView.setTextColor(Color.GRAY);
            textView = (TextView) findViewById(R.id.mailCnt);
            textView.setTextColor(Color.GRAY);
        } else
        {
            TextView textView = (TextView) findViewById(R.id.ZoomCnt);
            textView.setTextColor(Color.BLACK);
            textView = (TextView) findViewById(R.id.QueueCnt);
            textView.setTextColor(Color.BLACK);
            textView = (TextView) findViewById(R.id.mailCnt);
            textView.setTextColor(Color.BLACK);
        }
        GameSettings.addSettingsListener(new SettingsListener() {
            @Override
            public void onSettingsSave() {

            }

            @Override
            public void onSettingsLoad() {
                if ("Y".equals(GameSettings.getValue("SHOW_BUILD_AREA"))) {
                    findViewById(R.id.foundedTitle).setVisibility(VISIBLE);
                    findViewById(R.id.foundedAmount).setVisibility(VISIBLE);
                } else
                {
                    findViewById(R.id.foundedTitle).setVisibility(GONE);
                    findViewById(R.id.foundedAmount).setVisibility(GONE);
                }

            }

            @Override
            public void onSettingChange(String setting) {
                if ("SHOW_BUILD_AREA".equals(setting)){
                    if ("Y".equals(GameSettings.getValue("SHOW_BUILD_AREA"))) {
                        findViewById(R.id.foundedTitle).setVisibility(VISIBLE);
                        findViewById(R.id.foundedAmount).setVisibility(VISIBLE);
                    } else
                    {
                        findViewById(R.id.foundedTitle).setVisibility(GONE);
                        findViewById(R.id.foundedAmount).setVisibility(GONE);
                    }
                }
                else if ("NIGHT_MODE".equals(setting)){
                    if ("Y".equals(GameSettings.getValue("NIGHT_MODE"))){
                        zoomCnt.setTextColor(Color.GRAY);
                        TextView textView = (TextView) findViewById(R.id.QueueCnt);
                        textView.setTextColor(Color.GRAY);
                    } else
                    {
                        zoomCnt.setTextColor(Color.BLACK);
                        TextView textView = (TextView) findViewById(R.id.QueueCnt);
                        textView.setTextColor(Color.BLACK);
                    }

                }
            }
        });
        Settings.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    Essages.addEssage(Essages.SYSTEM,"Принудительная загрузка.");
                    serverConnect.getInstance().clearQueue();
                    //Run Refresh
                    serverConnect.getInstance().callScanRange();
                    //Run Player
                    serverConnect.getInstance().callGetPlayerInfo();
                    //RunGetMessage
                    serverConnect.getInstance().callGetMessage();
                    serverConnect.getInstance().callFastScan();
                } catch (Exception e){
                    GATracker.trackException("ForceSync",e);
                }
                return true;
            }
        });
        zoomCnt= (TextView) findViewById(R.id.ZoomCnt);
        updateZoom();
        ImageView zoomButton= (ImageView) findViewById(R.id.zoomButton);
        zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MyGoogleMap.switchZoom();
                    GameObjects.updateZoom();
                    updateZoom();
                } catch (Exception e) {
                    GATracker.trackException("ZoomClick",e);
                }

            }
        });
        zoomButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if ("Y".equals(GameSettings.getValue("NIGHT_MODE")))
                GameSettings.set("NIGHT_MODE","N");
                else GameSettings.set("NIGHT_MODE","Y");
                return true;
            }
        });
        ProgressBar expProgress= (ProgressBar) findViewById(R.id.expProgress);
        expProgress.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                CharSequence text=StringUtils.intToStr(GameObjects.getPlayer().getExp()-LevelTable.getStart(GameObjects.getPlayer().getLevel()))+"/"+StringUtils.intToStr(LevelTable.getEnd(GameObjects.getPlayer().getLevel())-LevelTable.getStart(GameObjects.getPlayer().getLevel()));
                Toast toast=Toast.makeText(getContext(),text,Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL,toast.getXOffset(),toast.getXOffset());
                toast.show();
            }
        });
        GameObjects.getPlayer().addOnChangeListeners(new OnGameObjectChange() {

            @Override
            public void onChange(int TYPE) {
                loadFromPlayer();
            }
        });

        scrollView= (ScrollView) findViewById(R.id.scrollView);
        if (scrollView!=null) scrollView.getLayoutParams().height=60;
        ImageView logButton = (ImageView) findViewById(R.id.mailButton);
        messageLayout = new MessageLayout(getContext());
        if (logButton !=null) logButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                try {
                    messageLayout.Show();
                    TextView mailCnt= (TextView) findViewById(R.id.mailCnt);
                    mailCnt.setText("");
                }catch (Exception e){
                    GATracker.trackException("OpenMessages",e);
                }
            }
        });
        Essages.addListener(new OnEssageListener() {
            @Override
            public void onAdd(int type, Message msg) {
                EssageLine line = (EssageLine) findViewById(R.id.essage);
                line.setText(msg);
                EssageLine line = (EssageLine) findViewById(R.id.essage);
            }

            @Override
            public void onClear() {

            }

            @Override
            public void onRemove(Message msg) {

            }
        });
        Essages.setTarget((EssageLine) findViewById(R.id.essage), (TextView) findViewById(R.id.mailCnt));
        serverConnect.getInstance().addListener(new ServerListener() {
            @Override
            public void onResponse(int TYPE, JSONObject response) {

            }

            @Override
            public void onError(int TYPE, JSONObject response) {

            }

            @Override
            public void onChangeQueue(int count) {
                super.onChangeQueue(count);
                MainThread.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            TextView textView = (TextView) findViewById(R.id.QueueCnt);
                            int count=serverConnect.getInstance().getQueueSize();
                            if (count < 2) {
                                textView.setText("");
                            } else textView.setText(String.valueOf(count));
                        }catch (Exception e) {
                            GATracker.trackException("ChangeQueue",e);
                        }
                    }
                });

            }
        });

    }

    private void loadFromPlayer() {
        if (GameObjects.getPlayer() == null) return;
        try {
            TextView am = (TextView) findViewById(R.id.levelAmount);
            am.setText(String.valueOf(GameObjects.getPlayer().getLevel()));
            am = (TextView) findViewById(R.id.ambush);
            am.setText(String.format(getContext().getString(R.string.d_d), GameObjects.getPlayer().getAmbushMax()-GameObjects.getPlayer().getAmbushLeft(), GameObjects.getPlayer().getAmbushMax()));
            ProgressBar expProgress= (ProgressBar) findViewById(R.id.expProgress);
            expProgress.setMax(LevelTable.getEnd(GameObjects.getPlayer().getLevel())-LevelTable.getStart(GameObjects.getPlayer().getLevel()));
            expProgress.setProgress(GameObjects.getPlayer().getExp()-LevelTable.getStart(GameObjects.getPlayer().getLevel()));

            expProgress.setClickable(true);
            am = (TextView) findViewById(R.id.goldAmount);
            am.setText(String.valueOf(StringUtils.intToStr(GameObjects.getPlayer().getGold())));
            ImageView btn = (ImageView) findViewById(R.id.infoview);
            if ("".equals(GameObjects.getPlayer().getCurrentRouteGUID()))
                btn.setImageResource(R.mipmap.info);
            else btn.setImageResource(R.mipmap.info_route);

            am = (TextView) findViewById(R.id.foundedAmount);
            am.setText(String.format(getContext().getString(R.string.d_d), GameObjects.getPlayer().getFoundedCities(), GameObjects.getPlayer().getCityMax()));

            am=(TextView) findViewById(R.id.hirelingsAmount);
            am.setText(String.format(getContext().getString(R.string.s_s), StringUtils.intToStr(GameObjects.getPlayer().getHirelings()), StringUtils.intToStr(GameObjects.getPlayer().getLeftToHire())));
            if (GameObjects.getPlayer().getHirelings()<50){
                am.setTextColor(Color.RED);
            } else am.setTextColor(Color.BLACK);

        } catch (Exception e){
            GATracker.trackException("PlayerChange",e);
        }

    }

    private void updateZoom() {
        float zoom=MyGoogleMap.getClientZoom();
        if (zoom==GameObject.ICON_SMALL)
        {
            zoomCnt.setText(R.string.x1);
        } else if (zoom==GameObject.ICON_MEDIUM)
        {
            zoomCnt.setText(R.string.x2);
        } else
        {
            zoomCnt.setText(R.string.x4);
        }
    }

    public void showConnectImage(){
        ImageView ci= (ImageView) findViewById(R.id.server_connect);
        ci.setVisibility(VISIBLE);
    }
    public void hideConnectImage(){
        ImageView ci= (ImageView) findViewById(R.id.server_connect);
        ci.setVisibility(GONE);
    }


}
