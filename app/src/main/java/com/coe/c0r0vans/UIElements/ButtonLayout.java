package com.coe.c0r0vans.UIElements;

import android.content.Context;
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

import java.util.Arrays;

import utility.StringUtils;
import utility.internet.ServerListener;
import utility.internet.serverConnect;
import utility.notification.Essages;

/**
 * Элементы интерфейса
 */
public class ButtonLayout extends RelativeLayout {
    InfoLayout infoLayout;
    private LinearLayout LogView;
    private ScrollView scrollView;
    private ImageView LogButton;
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
        inflate(getContext(), R.layout.button_layout, this);
        try {
            afterInit();
        } catch (Exception e){
            serverConnect.getInstance().sendDebug(2, e.toString()+ Arrays.toString(e.getStackTrace()));
        }
    }

    private void afterInit(){
        MyGoogleMap.setShowpointButton((ImageButton) findViewById(R.id.showPosButton));
        infoLayout=new InfoLayout(getContext());
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
        TextView am = (TextView) findViewById(R.id.levelAmount);
        am = (TextView) findViewById(R.id.expAmount);
        am = (TextView) findViewById(R.id.goldAmount);
        ImageView btn = (ImageView) findViewById(R.id.infoview);
        if (Player.getPlayer()!=null) {
            am.setText(String.valueOf(Player.getPlayer().getLevel()));
            am.setText(String.valueOf(StringUtils.intToStr(Player.getPlayer().getExp())));
            am.setText(String.valueOf(StringUtils.intToStr(Player.getPlayer().getGold())));
            if ("".equals(Player.getPlayer().getCurrentRoute()))
                btn.setImageResource(R.mipmap.info);
            else btn.setImageResource(R.mipmap.info_route);

            infoLayout.loadFromPlayer();
        }
        Settings.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                try {
                    Essages.addEssage("Принудительная загрузка.");
                    serverConnect.getInstance().clearQueue();
                    //Run Refresh
                    serverConnect.getInstance().RefreshCurrent();
                    //Run Player
                    serverConnect.getInstance().getPlayerInfo();
                    //RunGetMessage
                    serverConnect.getInstance().getMessage();
                } catch (Exception e){
                    serverConnect.getInstance().sendDebug(2, "ForceSync UE:" + e.toString() + "\n" + Arrays.toString(e.getStackTrace()));
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
                } catch (Exception e) {
                    serverConnect.getInstance().sendDebug(2, "UE:" + e.toString()+ Arrays.toString(e.getStackTrace()));
                }

            }
        });
        Player.getPlayer().addOnChange(new OnGameObjectChange() {
            @Override
            public void change(int ChangeType) {
                try {
                    if (ChangeType != OnGameObjectChange.EXTERNAL) return;
                    TextView am = (TextView) findViewById(R.id.levelAmount);
                    am.setText(String.valueOf(Player.getPlayer().getLevel()));
                    am = (TextView) findViewById(R.id.expAmount);
                    am.setText(String.valueOf(StringUtils.intToStr(Player.getPlayer().getExp())));
                    am = (TextView) findViewById(R.id.goldAmount);
                    am.setText(String.valueOf(StringUtils.intToStr(Player.getPlayer().getGold())));
                    ImageView btn = (ImageView) findViewById(R.id.infoview);
                    if ("".equals(Player.getPlayer().getCurrentRoute()))
                        btn.setImageResource(R.mipmap.info);
                    else btn.setImageResource(R.mipmap.info_route);
                    infoLayout.loadFromPlayer();
                } catch (Exception e){
                    serverConnect.getInstance().sendDebug(2, "Player UE:" + e.toString() + "\n" + Arrays.toString(e.getStackTrace()));
                }
            }
        });
        LogView = (LinearLayout) findViewById(R.id.chatBox);
        scrollView= (ScrollView) findViewById(R.id.scrollView);
        if (scrollView!=null) scrollView.getLayoutParams().height=60;
        LogButton = (ImageView) findViewById(R.id.showButton);
        if (LogButton!=null) LogButton.setOnClickListener(new View.OnClickListener() {
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
                    serverConnect.getInstance().sendDebug(2, "Show UE:" + e.toString() + "\n" + Arrays.toString(e.getStackTrace()));
                }
            }
        });
        Essages.setTarget(LogView);
        serverConnect.getInstance().addListener(new ServerListener() {
            @Override
            public void onLogin(JSONObject response) {

            }

            @Override
            public void onRefresh(JSONObject response) {

            }

            @Override
            public void onAction(JSONObject response) {

            }

            @Override
            public void onPlayerInfo(JSONObject response) {

            }

            @Override
            public void onError(JSONObject response) {

            }

            @Override
            public void onMessage(JSONObject response) {

            }

            @Override
            public void onRating(JSONObject response) {

            }

            @Override
            public void onChangeQueue(int count) {
                super.onChangeQueue(count);
                try {
                    TextView textView = (TextView) findViewById(R.id.QueueCnt);
                    if (count == 0) {
                        textView.setText("");
                    } else textView.setText("" + count);
                }catch (Exception e) {
                    serverConnect.getInstance().sendDebug(2, "UE:" + e.toString() + "\n" + Arrays.toString(e.getStackTrace()));
                }
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
