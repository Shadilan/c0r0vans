package com.coe.c0r0vans.UIElements;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;

import com.coe.c0r0vans.R;

import utility.settings.GameSettings;

/**
 * About Layout
 */
public class AboutWindow extends RelativeLayout {
    public AboutWindow(Context context) {
        super(context);
        init();
    }

    public AboutWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AboutWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        inflate(getContext(), R.layout.about_layout,this);
        if ("Y".equals(GameSettings.getValue("NIGHT_MODE"))) this.setBackgroundResource(R.drawable.layouts_night);
        else  this.setBackgroundResource(R.drawable.layouts);
        findViewById(R.id.closeWindow).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                close();
            }
        });
    }
    private void close(){
        if (UIControler.getAlertLayout()==null) return;
        UIControler.getAlertLayout().removeAllViews();

    }
    public void show(){
        if (UIControler.getAlertLayout()==null) return;
        UIControler.getAlertLayout().removeAllViews();
        UIControler.getAlertLayout().addView(this);
        //((TextView) findViewById(R.id.textView30)).setText(serverConnect.getInstance().getCurrentRequest());
    }
}
