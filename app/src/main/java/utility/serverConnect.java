package utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * ������ �������������� ���������� � �������� � �������������� � ��������. Singleton.
 * @author Shadilan
 */
public class serverConnect {

    private static serverConnect instance;

    /**
     * �������� ������
     * @return ������ ��������
     */
    public static serverConnect getInstance(){
        if (instance ==null){
            instance=new serverConnect();
        }
        return instance;
    }

    private String ServerAddres;//������ �������
    private Context context;    //�������� ����������
    private RequestQueue reqq;  //������� ��������
    private String Token;       //�����

    /**
     * Constructor
     */
    private serverConnect(){

    }

    /**
     * ��������� ���������� ��������. � ������ �������.
     * @param serverAddres Address of server
     * @param ctx Application context
     */
    public void connect(String serverAddres,Context ctx){
        Log.d("Debug info!",serverAddres);
            ServerAddres = serverAddres;
            Log.d("Debug info!!",ServerAddres);
            context = ctx;
            Log.d("Debug info!",ctx.toString());
            reqq = Volley.newRequestQueue(context);
            Log.d("Debug info!","TTT");

    }

    /**
     * �������� ������� ������� � ��������.
     * @return Return true if connection exists
     */
    private boolean checkConnection(){
        Log.d("Debug info","c1");
        if (context==null) return false;
        Log.d("Debug info","c2");
        ConnectivityManager connMgr = (ConnectivityManager)
                instance.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Log.d("Debug info","c3");
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        Log.d("Debug info","c4");
        return networkInfo != null && networkInfo.isConnected();
    }

    //Listeners
    private ArrayList<Response.Listener<JSONObject>> loginListeners;    //������������ �������
    private ArrayList<Response.Listener<JSONObject>> remloginListeners; //�������� �������� �� ������
    /**
     * Add Login Listener to object
     * @param listener Listener to add
     */
    public void addLoginListener(Response.Listener<JSONObject> listener){
        if (loginListeners==null){
            loginListeners=new ArrayList<>();
        }
        loginListeners.add(listener);
    }

    /**
     * Remove LoginListener from object
     * @param listener Listener to remove
     */
    public void removeLoginListener(Response.Listener<JSONObject> listener){
        if (remloginListeners==null){
            remloginListeners=new ArrayList<>();
        }
        remloginListeners.add(listener);
    }

    /**
     * Exec Listeners on event
     * @param response Response from server
     */
    private void doLoginListeners(JSONObject response){
        if (remloginListeners!=null && remloginListeners.size()>0) loginListeners.removeAll(remloginListeners);
        if (loginListeners !=null)
            for (Response.Listener<JSONObject> listener:loginListeners){
                listener.onResponse(response);
            }
    }

    private ArrayList<Response.Listener<JSONObject>> getDataListeners;
    private ArrayList<Response.Listener<JSONObject>> remgetDataListeners;

    /**
     * Add getData Listener to object
     * @param listener Listener to add
     */
    public void addDataListener(Response.Listener<JSONObject> listener){
        if (getDataListeners==null){
            getDataListeners=new ArrayList<>();
        }
        getDataListeners.add(listener);
    }

    /**
     * Remove getData Listener from object
     * @param listener Listener to remove
     */
    public void removeDataListener(Response.Listener<JSONObject> listener){
        if (remgetDataListeners==null){
            remgetDataListeners=new ArrayList<>();
        }
        remgetDataListeners.add(listener);
    }

    /**
     * Exec Listeners on event
     * @param response Response from server
     */
    private void doDataListeners(JSONObject response){
        try {
            if (response.getString("Result").equalsIgnoreCase("Error"))
                if (response.getString("Code").equalsIgnoreCase("AccessDenied"))
                {
                    SharedPreferences sp = context.getSharedPreferences("SpiritProto", AppCompatActivity.MODE_PRIVATE);

                    String login=sp.getString("Login", "");

                    String passw =sp.getString("Password", "");
                    ExecLogin(login,passw);
                } else Essages.instance.AddEssage(response.getString("Message"));
            else
            if (remgetDataListeners!=null && remgetDataListeners.size()>0) getDataListeners.removeAll(remgetDataListeners);
        } catch (JSONException e) {
            Essages.instance.AddEssage(e.toString());
        }
        for (Response.Listener<JSONObject> listener:getDataListeners){
            listener.onResponse(response);
        }
    }

    private ArrayList<Response.Listener<JSONObject>> actionListeners;
    private ArrayList<Response.Listener<JSONObject>> remactionListeners;

    /**
     * Add action Listener to object
     * @param listener Listener to add
     */
    public void addActionListener(Response.Listener<JSONObject> listener){
        if (actionListeners==null){
            actionListeners=new ArrayList<>();
        }
        actionListeners.add(listener);
    }

    /**
     * Remove getData Listener from object
     * @param listener Listener to remove
     */
    public void removeActionDataListener(Response.Listener<JSONObject> listener){
        if (remactionListeners==null){
            remactionListeners=new ArrayList<>();
        }
        remactionListeners.add(listener);
    }

    /**
     * Exec Listeners on event
     * @param response Response from server
     */
    private void doActionListeners(JSONObject response){
        if (remgetDataListeners!=null && remgetDataListeners.size()>0) actionListeners.removeAll(remactionListeners);
        if (actionListeners!=null) {
            for (Response.Listener<JSONObject> listener : actionListeners) {
                listener.onResponse(response);
            }
        }
        RefreshData(GPSInfo.getInstance().GetLat(), GPSInfo.getInstance().GetLng());
    }
    //UserActions
    /**
     *  Login and get Secure Token
     * @param Login Login of user
     * @param Password Password of user
     * @return true
     */
    public boolean ExecLogin(String Login, String Password){
        Log.d("Debug�info","el1");
        if (!checkConnection()) return false;
        Log.d("Debug�info","el2");
        String url=ServerAddres+"/login.jsp"+"?Login="+Login+"&Password="+Password;
        Log.d("Debug�info","Connect url:"+url);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Token=response.getString("Token");
                            doLoginListeners(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Ubnexpected Error",error.toString());
                    }
                });
        reqq.add(jsObjRequest);
        return true;
    }

    /**
     * Get new data
     * @param Lat Latitude of position to get data
     * @param Lng Longtitude of position to get data
     * @return true
     */
    public boolean RefreshData(int Lat,int Lng){

        if (!checkConnection()) return false;
        if (Token==null) return false;
        String url=ServerAddres+"/getdata.jsp"+"?ReqName=ScanRange&Token="+Token+"&plat="+Lat+"&plng="+Lng;
        Log.d("Debug�info","Connect url:"+url);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                            //Todo ��������� ��� ��� ������ ������ ���� ������ ���� �� �������� �������� ������
                            doDataListeners(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Unexpected Error:", error.toString());
                    }
                });
        reqq.add(jsObjRequest);
        return true;
    }

    /**
     * Exec simple action
     * @param Command Action
     * @param Target Target GUID
     * @param Lat Latitude of player
     * @param Lng Longtitude of player
     * @return true
     */
    public boolean ExecCommand(String Command, String Target, int Lat,int Lng , int TLat,int TLng){
        if (!checkConnection()) return false;
        if (Token==null) return false;

        String url=ServerAddres+"/getdata.jsp"+"?Token="+Token+"&ReqName="+Command+"&plat="+Lat+"&plng="+Lng+"&TGUID="+Target+"&lat="+TLat+"&lng="+TLng;
        Log.d("Debug info","Connection url:"+url);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        //TODO Add check token error if not login remove token
                        doActionListeners(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Unexpected Error:", error.toString());
                    }
                });
        reqq.add(jsObjRequest);
        return true;
    }
    /**
     * Check if we have Token
     * @return true if we have token otherwise false;
     */
    public boolean isLogin(){
        return Token != null;
    }
}
