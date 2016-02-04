package coe.com.c0r0vans;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import coe.com.c0r0vans.GameObjects.ObjectAction;
import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.GameObjects.SelectedObject;
import coe.com.c0r0vans.R;
import utility.GPSInfo;
import utility.serverConnect;

/**
 * Created by Shadilan on 04.02.2016.
 */
public class ActionView extends LinearLayout {

    public ActionView(Context context) {
        super(context);

    }

    public ActionView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public ActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }
    private boolean needInit=true;
    private ImageView ObjectImage;
    private TextView ObjectDesc;
    private LinearLayout ActionList;
    private ArrayList<ObjectAction> actions;
    private Button close;
    private LinearLayout cont;
    HorizontalScrollView horizontalScrollView;
    public void init(){
        this.setOrientation(VERTICAL);
        close=new Button(this.getContext());

        close.setText("X");
        close.setGravity(Gravity.RIGHT);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HideView();
            }
        });
        this.addView(close);
        this.setBackgroundColor(Color.TRANSPARENT);
        cont=new LinearLayout(this.getContext());
        cont.setOrientation(HORIZONTAL);
        this.addView(cont);
        ObjectImage=new ImageView(getContext());
        cont.addView(ObjectImage);
        ObjectDesc=new TextView(this.getContext());
        ObjectDesc.setSingleLine(false);
        ObjectDesc.setBackgroundColor(Color.WHITE);
        cont.addView(ObjectDesc);
        horizontalScrollView=new HorizontalScrollView(this.getContext());

        this.addView(horizontalScrollView);
        ActionList=new LinearLayout(this.getContext());


        horizontalScrollView.addView(ActionList);

    }
    ViewGroup.LayoutParams fullWrap=new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    ViewGroup.LayoutParams heightWrap=new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    private void setLayout(){
        close.setLayoutParams(fullWrap);
        cont.setLayoutParams(heightWrap);
        ObjectImage.setLayoutParams(fullWrap);
        horizontalScrollView.setLayoutParams(heightWrap);
        ActionList.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        ActionList.setGravity(Gravity.CENTER_HORIZONTAL);
    }
    public void HideView(){
        this.setVisibility(GONE);
    }
    public void ShowView(){
        if (needInit){
            init();
            needInit=false;
        }
        this.setVisibility(VISIBLE);
        if (SelectedObject.getInstance().getTarget() instanceof Player){

            ObjectImage.setVisibility(View.INVISIBLE);
            ObjectDesc.setVisibility(View.INVISIBLE);
        } else
        {
            ObjectImage.setVisibility(View.VISIBLE);
            ObjectDesc.setVisibility(View.VISIBLE);
        }
        ObjectImage.setImageBitmap(SelectedObject.getInstance().getTarget().getImage());
        ObjectDesc.setText(SelectedObject.getInstance().getTarget().getInfo());
        ObjectDesc.setWidth(this.getWidth() - 5 - ObjectImage.getWidth());
        ObjectDesc.setX(ObjectImage.getWidth() + 2);
        ObjectDesc.setHeight(ObjectImage.getHeight());
        reloadActions();
        setLayout();
    }
    private void reloadActions(){
        actions=SelectedObject.getInstance().getTarget().getActions();

        ActionList.removeAllViews();
        for (ObjectAction act:actions){
            CommandButton btn=new CommandButton(this.getContext(),act);
            btn.setImageBitmap(act.getImage());

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    serverConnect.getInstance().ExecCommand(((CommandButton) v).getAction(), SelectedObject.getInstance().getTarget().getGUID(), GPSInfo.getInstance().GetLat(), GPSInfo.getInstance().GetLng(),(int)(SelectedObject.getInstance().getPoint().latitude*1E6),(int)(SelectedObject.getInstance().getPoint().longitude*1E6));
                    HideView();
                }
            });

            ActionList.addView(btn);
        }
    }
    private class CommandButton extends ImageButton {

        public CommandButton(Context context) {
            super(context);
        }
        public CommandButton(Context context,ObjectAction action) {
            super(context);
            this.action=action;
        }
        private ObjectAction action;
        public ObjectAction getAction(){
            return action;
        }
    }
}
