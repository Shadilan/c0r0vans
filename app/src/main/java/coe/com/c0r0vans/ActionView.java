package coe.com.c0r0vans;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import coe.com.c0r0vans.GameObjects.Ambush;
import coe.com.c0r0vans.GameObjects.Caravan;
import coe.com.c0r0vans.GameObjects.City;
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
        init();
    }

    public ActionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ActionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    private ImageView ObjectImage;
    private TextView ObjectDesc;
    private LinearLayout ActionList;
    private ArrayList<ObjectAction> actions;
    private ImageButton close;


    private TextView    title;
    HorizontalScrollView horizontalScrollView;
    public void init(){
        inflate(getContext(), R.layout.actions_layout, this);

    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        title= (TextView) findViewById(R.id.actionTitle);
        close= (ImageButton) findViewById(R.id.closeButton);
        close.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                HideView();
            }
        });

        ObjectImage= (ImageView) findViewById(R.id.TargetImage);
        ObjectDesc= (TextView) findViewById(R.id.TargetInfo);
        ActionList= (LinearLayout) findViewById(R.id.ActionList);
    }

    public void HideView(){
        this.setVisibility(GONE);
    }
    public void ShowView(){

        this.setVisibility(VISIBLE);
        if (SelectedObject.getInstance().getTarget() instanceof Player){
            title.setText("");
            ObjectImage.setVisibility(View.INVISIBLE);
            ObjectDesc.setVisibility(View.INVISIBLE);
        } else
        {
            ObjectImage.setVisibility(View.VISIBLE);
            ObjectDesc.setVisibility(View.VISIBLE);
        }
        if (SelectedObject.getInstance().getTarget() instanceof City)
        {
            title.setText(((City) SelectedObject.getInstance().getTarget()).getCityName());
            ObjectDesc.setText(SelectedObject.getInstance().getTarget().getInfo());
        } else if (SelectedObject.getInstance().getTarget() instanceof Ambush)
        {
            title.setText("Засада");
            ObjectDesc.setText("Засада ожидает здесь не осторожных караванщиков.");
        } else if (SelectedObject.getInstance().getTarget() instanceof Caravan)
        {
            title.setText("Караван");
            ObjectDesc.setText(SelectedObject.getInstance().getTarget().getInfo());
        }

        ObjectImage.setImageBitmap(SelectedObject.getInstance().getTarget().getImage());

        reloadActions();

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
