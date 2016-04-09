package coe.com.c0r0vans.UIElements;

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

import java.text.NumberFormat;

import coe.com.c0r0vans.GameObjects.GameObject;
import coe.com.c0r0vans.GameObjects.GameObjects;
import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.MyGoogleMap;
import coe.com.c0r0vans.OnGameObjectChange;
import coe.com.c0r0vans.R;
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
        init();
    }

    public ButtonLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        inflate(getContext(), R.layout.button_layout, this);
        try {
            afterInit();
        } catch (Exception e){
            Essages.addEssage(e.toString());
        }
    }

    private void afterInit(){
        MyGoogleMap.setShowpointButton((ImageButton) findViewById(R.id.showPosButton));
        infoLayout=new InfoLayout(getContext());
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
        Settings.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Essages.addEssage("Принудительная загрузка.");
                //Run Refresh
                serverConnect.getInstance().RefreshCurrent();
                //Run Player
                serverConnect.getInstance().getPlayerInfo();
                //RunGetMessage
                serverConnect.getInstance().getMessage();
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
                    Essages.addEssage("UE:" + e.toString());
                }

            }
        });
        Player.getPlayer().addOnChange(new OnGameObjectChange() {
            @Override
            public void change(int ChangeType) {
                NumberFormat nf = NumberFormat.getInstance();
                nf.setGroupingUsed(true);
                if (ChangeType != OnGameObjectChange.EXTERNAL) return;
                TextView am = (TextView) findViewById(R.id.levelAmount);
                am.setText(String.valueOf(Player.getPlayer().getLevel()));
                am = (TextView) findViewById(R.id.expAmount);
                am.setText(String.valueOf(nf.format(Player.getPlayer().getExp())));
                am = (TextView) findViewById(R.id.goldAmount);
                am.setText(String.valueOf(nf.format(Player.getPlayer().getGold())));
                ImageView btn = (ImageView) findViewById(R.id.infoview);
                if ("".equals(Player.getPlayer().getCurrentRoute()))
                    btn.setImageResource(R.mipmap.info);
                else btn.setImageResource(R.mipmap.info_route);
                infoLayout.loadFromPlayer();
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
                WindowManager windowManager= (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);

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
            }
        });
        Essages.setTarget(LogView);

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
