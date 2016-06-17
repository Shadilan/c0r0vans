package com.coe.c0r0vans.UIElements.InfoLayout;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coe.c0r0vans.GameObjects.Player;
import com.coe.c0r0vans.GameObjects.Upgrade;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;

import utility.StringUtils;

/**
 * Информация об апгрейдах
 */
public class UpgradeInfo extends LinearLayout implements PlayerInfoLayout {
    LinearLayout upgradeInfo;
    public UpgradeInfo(Context context) {
        super(context);
        init();
    }

    public UpgradeInfo(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UpgradeInfo(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        inflate(getContext(), R.layout.info_upgrade,this);
        upgradeInfo= (LinearLayout) findViewById(R.id.upgradeInfo);

    }
    @Override
    public void update() {
        upgradeInfo.removeAllViews();
        for (Upgrade u: Player.getPlayer().getUpgrades()){
            LinearLayout l=new LinearLayout(getContext());
            l.setBackgroundResource(R.drawable.layouts_bordered);
            l.setOrientation(LinearLayout.HORIZONTAL);
            upgradeInfo.addView(l);
            ImageView iv=new ImageView(getContext());
            iv.setImageBitmap(u.getImage());
            l.addView(iv);
            TextView info=new TextView(getContext());
            info.setSingleLine(false);
            String s;
            if (Player.getPlayer().getNextUpgrade(u.getType())!=null) {
                 s = StringUtils.intToStr((int) (Player.getPlayer().getNextUpgrade(u.getType()).getCost() * (100f - Player.getPlayer().getTrade() / 100f)));
            } else  s = StringUtils.intToStr((int) (u.getCost() * (100f - Player.getPlayer().getTrade()) / 100f));
            info.setText(String.format(getContext().getString(R.string.upgrade_text), u.getName(), u.getDescription(),s));
            info.setTextColor(Color.BLACK);
            l.addView(info);
            l.requestLayout();
        }
    }

    @Override
    public void setParent(ShowHideForm parent) {

    }
}
