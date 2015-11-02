package coe.com.c0r0vans;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;

import coe.com.c0r0vans.GameObjects.ObjectAction;
import coe.com.c0r0vans.GameObjects.SelectedObject;
import utility.GPSInfo;
import utility.serverConnect;

public class ActionsActivity extends AppCompatActivity {
    ArrayList<ObjectAction> actions;
    private TextView infoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actions);
        infoView= (TextView) findViewById(R.id.infoView);

    }
    @Override
    protected void onStart(){
        super.onStart();
        if (SelectedObject.getInstance().getTarget()!=null){
            infoView.setText(SelectedObject.getInstance().getTarget().getInfo());
            actions=SelectedObject.getInstance().getTarget().getActions();
            TableLayout ActionTable= (TableLayout) findViewById(R.id.ActionTables);
            ActionTable.removeAllViews();
            for (ObjectAction act:actions){
                TableRow row=new TableRow(this);
                CommandButton btn=new CommandButton(this,act.getCommand());
                btn.setImageBitmap(act.getImage());
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        serverConnect.getInstance().ExecCommand(((CommandButton) v).getCommand(), SelectedObject.getInstance().getTarget().getGUID(), GPSInfo.getInstance().GetLat(), GPSInfo.getInstance().GetLng());
                        finish();
                    }
                });
                TextView txt=new TextView(this);
                txt.setSingleLine(false);
                txt.setHorizontallyScrolling(false);
                txt.setMinLines(2);
                txt.setText(act.getInfo());
                row.addView(btn);
                row.addView(txt);
                ActionTable.addView(row);
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
        public void SetComman(String Command){
            this.Command=Command;
        }
    }
}
