package com.coe.c0r0vans.UIElements;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.coe.c0r0vans.GameObjects.GameObject;
import com.coe.c0r0vans.GameObjects.GameObjects;
import com.coe.c0r0vans.GameObjects.Player;
import com.coe.c0r0vans.MyGoogleMap;
import com.coe.c0r0vans.OnGameObjectChange;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.UIElements.InfoLayout.InfoLayout;

import org.json.JSONObject;

import utility.GATracker;
import utility.StringUtils;
import utility.internet.ServerListener;
import utility.internet.serverConnect;
import utility.notification.Essages;
import utility.settings.GameSettings;
import utility.settings.SettingsListener;

/**
 * Элементы интерфейса
 */
public class ButtonLayout extends RelativeLayout {
    private InfoLayout infoLayout;
    private ScrollView scrollView;
    //События должны приходить на основной слой
    private Handler handler;
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
            handler=new Handler();
            afterInit();
        } catch (Exception e){
            GATracker.trackException("ButtonLayout",e);
        }
    }

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
        TextView am;
        if (Player.getPlayer() != null) {
            am = (TextView) findViewById(R.id.levelAmount);
            am.setText(String.valueOf(Player.getPlayer().getLevel()));
            am = (TextView) findViewById(R.id.expAmount);
            am.setText(String.valueOf(StringUtils.intToStr(Player.getPlayer().getExp())));
            am = (TextView) findViewById(R.id.goldAmount);
            am.setText(String.valueOf(StringUtils.intToStr(Player.getPlayer().getGold())));
            if ("".equals(Player.getPlayer().getCurrentRouteGUID()))
                PlayerInfo.setImageResource(R.mipmap.info);
            else PlayerInfo.setImageResource(R.mipmap.info_route);

            am = (TextView) findViewById(R.id.foundedAmount);
            am.setText(String.format("%d/%d", Player.getPlayer().getFoundedCities(), Player.getPlayer().getCityMax()));
            ((TextView) findViewById(R.id.hirelingsAmount)).setText(String.format("%s(%s)", StringUtils.intToStr(Player.getPlayer().getHirelings()), StringUtils.intToStr(Player.getPlayer().getLeftToHire())));
            infoLayout.loadFromPlayer();
        }
        if ("Y".equals(GameSettings.getValue("SHOW_BUILD_AREA"))) {
            findViewById(R.id.foundedTitle).setVisibility(VISIBLE);
            findViewById(R.id.foundedAmount).setVisibility(VISIBLE);
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
            }
        });
        Settings.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    Essages.addEssage("Принудительная загрузка.");
                    serverConnect.getInstance().clearQueue();
                    //Run Refresh
                    serverConnect.getInstance().RefreshCurrent();
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

        ImageView zoomButton= (ImageView) findViewById(R.id.zoomButton);
        zoomButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MyGoogleMap.switchZoom();
                    for (GameObject obj : GameObjects.getInstance().values())
                        if (obj.getMarker() != null) obj.changeMarkerSize();
                    Player.getPlayer().changeMarkerSize();
                } catch (Exception e) {
                    GATracker.trackException("ZoomClick",e);
                }

            }
        });
        Player.getPlayer().addOnChange(new OnGameObjectChange() {
            @Override
            public void change(int ChangeType) {
                try {
                    //if (ChangeType != OnGameObjectChange.EXTERNAL) return;
                    TextView am = (TextView) findViewById(R.id.levelAmount);
                    am.setText(String.valueOf(Player.getPlayer().getLevel()));
                    am = (TextView) findViewById(R.id.expAmount);
                    am.setText(String.valueOf(StringUtils.intToStr(Player.getPlayer().getExp())));
                    am = (TextView) findViewById(R.id.goldAmount);
                    am.setText(String.valueOf(StringUtils.intToStr(Player.getPlayer().getGold())));
                    ImageView btn = (ImageView) findViewById(R.id.infoview);
                    if ("".equals(Player.getPlayer().getCurrentRouteGUID()))
                        btn.setImageResource(R.mipmap.info);
                    else btn.setImageResource(R.mipmap.info_route);

                    am = (TextView) findViewById(R.id.foundedAmount);
                    am.setText(String.format("%d/%d", Player.getPlayer().getFoundedCities(), Player.getPlayer().getCityMax()));
                    ((TextView) findViewById(R.id.hirelingsAmount)).setText(String.format("%s(%s)", StringUtils.intToStr(Player.getPlayer().getHirelings()), StringUtils.intToStr(Player.getPlayer().getLeftToHire())));

                    infoLayout.loadFromPlayer();
                } catch (Exception e){
                    GATracker.trackException("PlayerChange",e);
                }
            }
        });
        LinearLayout logView = (LinearLayout) findViewById(R.id.chatBox);
        scrollView= (ScrollView) findViewById(R.id.scrollView);
        if (scrollView!=null) scrollView.getLayoutParams().height=60;
        ImageView logButton = (ImageView) findViewById(R.id.showButton);
        if (logButton !=null) logButton.setOnClickListener(new View.OnClickListener() {
            private boolean show = false;

            @Override
            public void onClick(View v) {
                try {
                    WindowManager windowManager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

                    if (show) {
                        show = false;
                        DisplayMetrics dm = new DisplayMetrics();
                        windowManager.getDefaultDisplay().getMetrics(dm);
                        scrollView.getLayoutParams().height = dm.heightPixels / 2;
                        scrollView.requestLayout();

                    } else {
                        show = true;
                        scrollView.getLayoutParams().height = 60;
                        scrollView.requestLayout();
                    }
                }catch (Exception e){
                    GATracker.trackException("OpenMessages",e);
                }
            }
        });
        Essages.setTarget(logView);
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
                handler.post(new Runnable() {
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
    public void showConnectImage(){
        ImageView ci= (ImageView) findViewById(R.id.server_connect);
        ci.setVisibility(VISIBLE);
    }
    public void hideConnectImage(){
        ImageView ci= (ImageView) findViewById(R.id.server_connect);
        ci.setVisibility(GONE);
    }


}
