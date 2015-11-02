package utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
 * @author Shadilan
 */
public class serverConnect {
    private static serverConnect instance;

    public static serverConnect getInstance(){
        if (instance ==null){
            instance=new serverConnect();
        }
        return instance;
    }

    private String ServerAddres;
    private Context context;
    private RequestQueue reqq;
    private String Token;

    /**
     * Constructor
     */
    private serverConnect(){

    }

    /**
     * Set Parameters of connection
     * @param serverAddres Address of server
     * @param ctx Application context
     */
    public void Connect(String serverAddres,Context ctx){
        ServerAddres=serverAddres;
        context =ctx;
        reqq=Volley.newRequestQueue(context);
    }

    /**
     * Check internet connection
     * @return Return true if connection exists
     */
    private boolean checkConnection(){
        if (context==null) return false;
        ConnectivityManager connMgr = (ConnectivityManager)
                instance.context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    //Listeners
    private ArrayList<Response.Listener<JSONObject>> loginListeners;
    private ArrayList<Response.Listener<JSONObject>> remloginListeners;
    /**
     * Add Login Listener to object
     * @param listener Listener to add
     */
    public void AddLoginListener(Response.Listener<JSONObject> listener){
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
    private void DoLoginListeners(JSONObject response){
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
    public void AddGetDataListener(Response.Listener<JSONObject> listener){
        if (getDataListeners==null){
            getDataListeners=new ArrayList<>();
        }
        getDataListeners.add(listener);
    }

    /**
     * Remove getData Listener from object
     * @param listener Listener to remove
     */
    public void removeAddDataListener(Response.Listener<JSONObject> listener){
        if (remgetDataListeners==null){
            remgetDataListeners=new ArrayList<>();
        }
        remgetDataListeners.add(listener);
    }

    /**
     * Exec Listeners on event
     * @param response Response from server
     */
    private void DoGetDataListeners(JSONObject response){
        if (remgetDataListeners!=null && remgetDataListeners.size()>0) getDataListeners.removeAll(remgetDataListeners);
        for (Response.Listener<JSONObject> listener:getDataListeners){
            listener.onResponse(response);
        }
    }



    private ArrayList<Response.Listener<JSONObject>> routeListeners;
    private ArrayList<Response.Listener<JSONObject>> remRouteListeners;

    /**
     * Add getData Listener to object
     * @param listener Listener to add
     */
    public void addRouteListener(Response.Listener<JSONObject> listener){
        if (routeListeners==null){
            routeListeners=new ArrayList<>();
        }
        routeListeners.add(listener);
    }

    /**
     * Remove getData Listener from object
     * @param listener Listener to remove
     */
    public void removeRouteDataListener(Response.Listener<JSONObject> listener){
        if (remRouteListeners==null){
            remRouteListeners=new ArrayList<>();
        }
        remRouteListeners.add(listener);
    }

    /**
     * Exec Listeners on event
     * @param response Response from server
     */
    private void doRouteListeners(JSONObject response){
        if (remRouteListeners!=null && remRouteListeners.size()>0) actionListeners.removeAll(remRouteListeners);
        if (routeListeners!=null) {
            for (Response.Listener<JSONObject> listener : routeListeners) {
                listener.onResponse(response);
            }
        }
        RefreshData(GPSInfo.getInstance().GetLat(),GPSInfo.getInstance().GetLng());
    }


    private ArrayList<Response.Listener<JSONObject>> actionListeners;
    private ArrayList<Response.Listener<JSONObject>> remactionListeners;

    /**
     * Add getData Listener to object
     * @param listener Listener to add
     */
    public void AddactionListener(Response.Listener<JSONObject> listener){
        if (actionListeners==null){
            actionListeners=new ArrayList<>();
        }
        actionListeners.add(listener);
    }

    /**
     * Remove getData Listener from object
     * @param listener Listener to remove
     */
    public void removeactionDataListener(Response.Listener<JSONObject> listener){
        if (remactionListeners==null){
            remactionListeners=new ArrayList<>();
        }
        remactionListeners.add(listener);
    }

    /**
     * Exec Listeners on event
     * @param response Response from server
     */
    private void DoactionListeners(JSONObject response){
        if (remgetDataListeners!=null && remgetDataListeners.size()>0) actionListeners.removeAll(remactionListeners);
        if (actionListeners!=null) {
            for (Response.Listener<JSONObject> listener : actionListeners) {
                listener.onResponse(response);
            }
        }
        RefreshData(GPSInfo.getInstance().GetLat(),GPSInfo.getInstance().GetLng());
    }
    //UserActions
    /**
     *  Login and get Secure Token
     * @param Login Login of user
     * @param Password Password of user
     * @return true
     */
    public boolean ExecLogin(String Login, String Password){
        if (!checkConnection()) return false;
        Log.d("ServeConnect","Connection start");
        String url=ServerAddres+"/login.jsp"+"?Login="+Login+"&Password="+Password;
        Log.d("ServeConnect",url);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Token=response.getString("Token");
                            DoLoginListeners(response);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO Auto-generated method stub
                        Log.d("LoginTest",error.toString());
                    }
                });
        Log.d("ServeConnect",jsObjRequest.toString());
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
        Log.d("ServeConnect","Connection start");
        String url=ServerAddres+"/getdata.jsp"+"?Token="+Token+"&Lat="+Lat+"&Lng="+Lng;
        Log.d("ServeConnect",url);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                            DoGetDataListeners(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Log.d("LoginTest",error.toString());
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
    public boolean ExecCommand(String Command, String Target, int Lat,int Lng ){
        if (!checkConnection()) return false;
        Log.d("ServeConnect","Connection start");
        String url=ServerAddres+"/simpleCommand.jsp"+"?Token="+Token+"&Command="+Command+"&Lat="+Lat+"&Lng="+Lng+"&Target="+Target;
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        DoactionListeners(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ActionTest", error.toString());

                    }
                });
        Log.d("ServeConnect",jsObjRequest.toString());
        reqq.add(jsObjRequest);
        return true;
    }
    /**
     * Exec simple action
     * @return true
     */
    public boolean GetRouteList(){
        if (!checkConnection()) return false;
        Log.d("ServeConnect","Connection start");
        String url=ServerAddres+"/routelist.jsp"+"?Token="+Token;
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        doRouteListeners(response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("ActionTest", error.toString());

                    }
                });
        Log.d("ServeConnect",jsObjRequest.toString());
        reqq.add(jsObjRequest);
        return true;
    }
}
