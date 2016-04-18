package coe.com.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.R;
import coe.com.c0r0vans.ShowHideForm;
import utility.StringUtils;

/**
 * Информация об игроке
 */
public class MainInfo extends LinearLayout implements PlayerInfoLayout {
    private TextView level;
    private TextView exp;
    private TextView gold;
    private TextView caravans;
    private TextView ambushes;
    private TextView ambushes_left;
    private ImageView faction;
    private TextView tnl;
    public MainInfo(Context context) {
        super(context);
        init();
    }

    public MainInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MainInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        inflate(getContext(), R.layout.info_main,this);
        level= (TextView) findViewById(R.id.levelInfo);
        exp= (TextView) findViewById(R.id.expInfo);
        tnl= (TextView) findViewById(R.id.tnlInfo);
        gold= (TextView) findViewById(R.id.goldInfo);
        caravans= (TextView) findViewById(R.id.caravanInfo);
        ambushes= (TextView) findViewById(R.id.ambushSetInfo);
        ambushes_left= (TextView) findViewById(R.id.ambushLeftInfo);
        faction= (ImageView) findViewById(R.id.factionSymbol);
    }
    public void update(){
        level.setText(StringUtils.intToStr(Player.getPlayer().getLevel()));
        exp.setText(StringUtils.intToStr(Player.getPlayer().getExp()));
        gold.setText(StringUtils.intToStr(Player.getPlayer().getGold()));
        tnl.setText(StringUtils.intToStr(Player.getPlayer().getTNL()));
        caravans.setText(StringUtils.intToStr(Player.getPlayer().getCaravans()));
        ambushes.setText(StringUtils.intToStr(Player.getPlayer().getAmbushMax()-Player.getPlayer().getAmbushLeft()));
        ambushes_left.setText(StringUtils.intToStr(Player.getPlayer().getAmbushLeft()));

        if (Player.getPlayer().getRace()==1){
            faction.setImageResource(R.mipmap.guild);
        }else if (Player.getPlayer().getRace()==2){
            faction.setImageResource(R.mipmap.alliance);
        }else if (Player.getPlayer().getRace()==3){
            faction.setImageResource(R.mipmap.legue);
        }
    }

    @Override
    public void setParent(ShowHideForm parent) {

    }

}
