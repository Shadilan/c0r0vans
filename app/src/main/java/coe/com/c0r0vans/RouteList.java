package coe.com.c0r0vans;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import coe.com.c0r0vans.GameObjects.Route;
import utility.Essages;
import utility.GPSInfo;
import utility.serverConnect;

public class RouteList extends AppCompatActivity {
    private LinearLayout routeItemList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_list);
        routeItemList= (LinearLayout) findViewById(R.id.route_list_items);
    }

    private View.OnTouchListener touchListener=new View.OnTouchListener() {
        float StartX;
        float StartY;
        View StartView;
        Boolean Toched=false;
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN && !Toched) {
                StartX = event.getRawX();
                StartY = event.getRawY();
                StartView=v;
                ((TextView) v).setTextColor(Color.BLACK);
                Log.d("Actions", "Action Down");
                Toched=true;
            }

            if (event.getAction() == MotionEvent.ACTION_UP
                    || event.getAction()==MotionEvent.ACTION_CANCEL) {
                Log.d("Actions","Action Up");
                ((TextView) v).setTextColor(Color.GRAY);
                Toched=false;
                float CurrentXMove = StartX - event.getRawX();
                float CurrentYMove = StartY - event.getRawY();
                if (CurrentXMove > Math.abs(CurrentYMove) && CurrentXMove > 100) {
                    //Swipe Action
                    Log.d("Actions", "SWIPE");
                    serverConnect.getInstance().ExecCommand("dropRoute",((CommandTextView) StartView).getGUID(), GPSInfo.getInstance().GetLat(),GPSInfo.getInstance().GetLat());
                    try {
                        routeItemList.removeView(v);
                    } catch (NullPointerException e){
                        Essages.instance.AddEssage(e.toString());
                    }

                }

            }
            return true;
        }
    };

    @Override
    protected void onStart(){
        super.onStart();
        routeItemList.removeAllViews();
        serverConnect.getInstance().addRouteListener(new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.has("Routes")) {
                        routeItemList.removeAllViews();
                        JSONArray routes = response.getJSONArray("Routes");
                        for (int i = 0; i < routes.length(); i++) {
                            Route route = new Route(routes.getJSONObject(i));
                            CommandTextView cmd = new CommandTextView(getApplicationContext(), route.getGUID());
                            cmd.setText(route.getInfo());
                            cmd.setTextColor(Color.GRAY);
                            cmd.setTextSize(cmd.getTextSize()+2);
                            cmd.setOnTouchListener(touchListener);
                            routeItemList.addView(cmd);

                        }
                    }
                } catch (JSONException e) {
                    Essages.instance.AddEssage("Error get routes" + e.toString());
                }
            }
        });
        serverConnect.getInstance().GetRouteList();

    }
    private class CommandTextView extends TextView{
        private String GUID;
        public String getGUID(){
            return GUID;
        }
        public CommandTextView(Context context,String guid) {
            super(context);
            GUID=guid;
        }
    }
}
