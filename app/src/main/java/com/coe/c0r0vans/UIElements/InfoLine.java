package com.coe.c0r0vans.UIElements;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.coe.c0r0vans.GameObjects.ObjectAction;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.ShowHideForm;
import com.coe.c0r0vans.Singles.MyGoogleMap;
import com.google.android.gms.maps.model.LatLng;

import utility.GATracker;
import utility.internet.serverConnect;


/**
 * Информационная строка для засад
 */
public class InfoLine extends RelativeLayout {
    private TextView labelText;
    private ImageButton removeButton;
    private ObjectAction removeAction;
    private ImageButton showButton;
    private String target;
    private String labelString;
    private LatLng point;
    private ShowHideForm parentForm;
    InfoLine self=this;

    public void setParentForm(ShowHideForm form){
        parentForm=form;
    }
    public void setLabelText(String text){
        labelString=text;

        if (labelText!=null) {
            labelText.setText(text);

        }


    }
    public void setPoint(LatLng point){
        this.point=point;
        showButton.setVisibility(VISIBLE);
    }
    public void setTarget(String guid){
        target=guid;
    }
    public void setOnRemoveClick(ObjectAction removeAction){
        this.removeAction=removeAction;
    }
    public InfoLine(Context context) {
        super(context);
        init();
    }

    public InfoLine(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InfoLine(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.ambush_line, this);
        try {
            afterInit();
        } catch (Exception e){
            GATracker.trackException("InfoLine",e);
        }

    }
    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        afterInit();
    }

    private void afterInit() {

        labelText= (TextView) findViewById(R.id.infoLineText);
        labelText.setText(labelString);
        removeButton= (ImageButton) findViewById(R.id.removeButton);
        removeButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ConfirmWindow confirmWindow=new ConfirmWindow(getContext());
                confirmWindow.setText("Распустить засаду?");
                confirmWindow.setConfirmAction(new Runnable() {
                    @Override
                    public void run() {
                        serverConnect.getInstance().callCanelAmbush(removeAction, target);
                        removeButton.setVisibility(INVISIBLE);
                        self.setVisibility(GONE);

                    }
                });
                confirmWindow.show();
            }
        });
        showButton= (ImageButton) findViewById(R.id.showButton);
        showButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (point!=null) {
                    MyGoogleMap.showPoint(point);
                    if (parentForm!=null) parentForm.Hide();
                }
            }
        });

    }
}
