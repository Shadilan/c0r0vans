package coe.com.c0r0vans;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;

import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import java.util.ArrayList;

import coe.com.c0r0vans.GameObjects.Ambush;
import coe.com.c0r0vans.GameObjects.City;
import coe.com.c0r0vans.GameObjects.ObjectAction;
import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.GameObjects.SelectedObject;
import utility.GPSInfo;
import utility.serverConnect;

public class ActionsActivity extends AppCompatActivity {
    ArrayList<ObjectAction> actions;
    private TextView infoView;
    private ImageView targetImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);
        infoView= (TextView) findViewById(R.id.TargetInfo);
        targetImage= (ImageView) findViewById(R.id.TargetImage);


    }
    @Override
    protected void onStart(){
        super.onStart();

        if (SelectedObject.getInstance().getTarget()!=null){

            if (SelectedObject.getInstance().getTarget() instanceof Player){
                this.setTitle("");
                targetImage.setVisibility(View.INVISIBLE);
                infoView.setVisibility(View.INVISIBLE);
            } else
            {
                targetImage.setVisibility(View.VISIBLE);
                infoView.setVisibility(View.VISIBLE);
            }
            if (SelectedObject.getInstance().getTarget() instanceof Ambush){
                this.setTitle(getResources().getString(R.string.ambush));
            } else if (SelectedObject.getInstance().getTarget() instanceof City)
            {
                this.setTitle(((City)SelectedObject.getInstance().getTarget()).getCityName());
            }
            infoView.setText(SelectedObject.getInstance().getTarget().getInfo());
            targetImage.setImageBitmap(SelectedObject.getInstance().getTarget().getImage());
            actions=SelectedObject.getInstance().getTarget().getActions();
            LinearLayout ActionTable= (LinearLayout) findViewById(R.id.ActionList);
            ActionTable.removeAllViews();
            for (ObjectAction act:actions){
                CommandButton btn=new CommandButton(this,act.getCommand());
                btn.setMinimumWidth(80);
                btn.setMinimumWidth(80);
                btn.setMaxWidth(80);
                btn.setMaxHeight(80);
                btn.setImageBitmap(act.getImage());

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        serverConnect.getInstance().ExecCommand(((CommandButton) v).getCommand(), SelectedObject.getInstance().getTarget().getGUID(), GPSInfo.getInstance().GetLat(), GPSInfo.getInstance().GetLng(),(int)(SelectedObject.getInstance().getPoint().latitude*1E6),(int)(SelectedObject.getInstance().getPoint().longitude*1E6));
                        finish();
                    }
                });

                ActionTable.addView(btn);
            }

        }
    }
    private class CommandButton extends ImageButton{

        public CommandButton(Context context) {
            super(context);
        }
        public CommandButton(Context context,String Command) {
            super(context);
            this.Command=Command;
        }
        private String Command;
        public String getCommand(){
            return Command;
        }
    }
}
