package com.coe.c0r0vans;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coe.c0r0vans.UIElements.UIControler;

/**
 * Запрос подтверждения
 */
public class ConfirmWindow extends RelativeLayout {
    Runnable confirm; //Операция согласия
    Runnable reject;
    public ConfirmWindow(Context context) {
        super(context);
        init();
    }

    public ConfirmWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ConfirmWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        inflate(getContext(), R.layout.confirm_window,this);
        Button button= (Button) findViewById(R.id.confirmButton);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (confirm!=null) confirm.run();
                hide();
            }
        });
        button= (Button) findViewById(R.id.rejectButton);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reject!=null) reject.run();
                hide();
            }
        });
    }
    public void setText(String text){
        ((TextView)findViewById(R.id.question)).setText(text);

    }
    public void setConfirmAction(Runnable runnable){
        confirm=runnable;

    }
    public void setRejectAction(Runnable runnable){
        reject=runnable;

    }
    public void show(){
        UIControler.getAlertLayout().removeAllViews();
        UIControler.getAlertLayout().addView(this);
    }
    public void hide(){
        UIControler.getAlertLayout().removeView(this);
    }
}
