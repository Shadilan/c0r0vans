package com.coe.c0r0vans.UIElements;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coe.c0r0vans.R;

import utility.settings.GameSettings;

/**
 * Запрос подтверждения
 */
public class TextWindow extends RelativeLayout {
    private Runnable confirm; //Операция согласия
    private Runnable reject;
    public String getText(){
        return ((EditText) findViewById(R.id.answer)).getText().toString();

    }
    public TextWindow(Context context) {
        super(context);
        init();
    }

    public TextWindow(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TextWindow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private void init(){
        inflate(getContext(), R.layout.text_window,this);
        if ("Y".equals(GameSettings.getValue("NIGHT_MODE"))){
            this.setBackgroundResource(R.drawable.layouts_night);
            findViewById(R.id.answer).setBackgroundResource(R.drawable.layouts_night);
        }

        else {
            this.setBackgroundResource(R.drawable.layouts);
            findViewById(R.id.answer).setBackgroundResource(R.drawable.layouts);
        }
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
