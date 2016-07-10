package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.Singles.GameObjects;

import utility.StringUtils;

/**
 * Информация об игроке
 */
public class MainInfoTable extends LinearLayout implements PlayerInfoLayout {
    private TextView level;
    private TextView exp;
    private TextView gold;
    private TextView caravans;
    private TextView ambushes;
    private ImageView faction;
    private TextView tnl;
    private TextView profit;
    private TextView hirelings;
    private TextView leftToHire;
    private TextView foundedCities;

    public MainInfoTable(Context context) {
        super(context);
        init();
    }

    public MainInfoTable(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MainInfoTable(Context context, AttributeSet attrs, int defStyleAttr) {
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
        faction= (ImageView) findViewById(R.id.factionSymbol);
        profit = (TextView) findViewById(R.id.profit);
        hirelings= (TextView) findViewById(R.id.hirelings);
        leftToHire= (TextView) findViewById(R.id.leftHirelings);
        foundedCities= (TextView) findViewById(R.id.foundedCities);
    }
    public void update(){
        if (GameObjects.getPlayer()!=null) {
            level.setText(StringUtils.intToStr(GameObjects.getPlayer().getLevel()));
            exp.setText(StringUtils.intToStr(GameObjects.getPlayer().getExp()));
            gold.setText(StringUtils.intToStr(GameObjects.getPlayer().getGold()));
            tnl.setText(StringUtils.intToStr(GameObjects.getPlayer().getTNL()));
            caravans.setText(StringUtils.intToStr(GameObjects.getPlayer().getCaravans()));
            ambushes.setText(StringUtils.intToStr(GameObjects.getPlayer().getAmbushMax() - GameObjects.getPlayer().getAmbushLeft())+"/"+StringUtils.intToStr(GameObjects.getPlayer().getAmbushMax()));
            profit.setText(StringUtils.intToStr(GameObjects.getPlayer().getProfit()));
            hirelings.setText(StringUtils.intToStr(GameObjects.getPlayer().getHirelings()));
            leftToHire.setText(StringUtils.intToStr(GameObjects.getPlayer().getLeftToHire()));
            foundedCities.setText(StringUtils.intToStr(GameObjects.getPlayer().getFoundedCities())+"/"+StringUtils.intToStr(GameObjects.getPlayer().getCityMax()));

            if (GameObjects.getPlayer().getRace() == 1) {
                faction.setImageResource(R.mipmap.guild);
            } else if (GameObjects.getPlayer().getRace() == 2) {
                faction.setImageResource(R.mipmap.alliance);
            } else if (GameObjects.getPlayer().getRace() == 3) {
                faction.setImageResource(R.mipmap.legue);
            }
        }
    }

    @Override
    public void setParent(ShowHideForm parent) {

    }

}
