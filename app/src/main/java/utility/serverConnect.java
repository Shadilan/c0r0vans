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
import java.util.HashMap;

import coe.com.c0r0vans.GameObjects.ObjectAction;

/**
 * Объект обеспечивающий соединение с сервером и взаимодействие с сервером. Singleton.
 * @author Shadilan
 */
public class serverConnect {

    private static serverConnect instance;

    /**
     * Получить объект
     * @return Объект коненкта
     */
    public static serverConnect getInstance(){
        if (instance ==null){
            instance=new serverConnect();
        }
        return instance;
    }

    private String ServerAddres;//Адресс сервера
    private Context context;    //Контекст приложения
    private RequestQueue reqq;  //Очередь запросов
    private String Token;       //Токен

    /**
     * Constructor
     */
    protected serverConnect(){
        listenersMap=new HashMap<>();
        errorMap=new HashMap<>();
    }

    /**
     * Установка параметров коннекта. и запуск очереди.
     * @param serverAddres Address of server
     * @param ctx Application context
     */
    public void connect(String serverAddres,Context ctx){
        ServerAddres = serverAddres;
        context = ctx;
        reqq = Volley.newRequestQueue(context);

    }

    /**
     * Проверка наличия доступа в интернет.
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
    private ArrayList<ServerListener> listeners;
    private ArrayList<ServerListener> remListeners;

    public void addListener(ServerListener listener){
        if (listeners==null) listeners=new ArrayList<>();
        listeners.add(listener);
    }
    public void removeListener(ServerListener listener){
        if (remListeners==null) remListeners=new ArrayList<>();
        remListeners.add(listener);
    }

    public void clearListener(){
        if (remListeners!=null && listeners!=null) {
            listeners.removeAll(remListeners);
            remListeners.clear();
        }
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
        String url=ServerAddres+"/login.jsp"+"?Login="+Login+"&Password="+Password;
        //if ("Y".equals(GameSettings.getInstance().get("NET_DEBUG"))) Essages.addEssage("Net:"+url);
        Log.d("Debug info", "Connect url:" + url);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            clearListener();

                            if (response.has("Error")){
                                for (ServerListener l:listeners) l.onError(response);
                            } else {
                                Token = response.getString("Token");
                                for (ServerListener l : listeners) l.onLogin(response);
                            }
                        } catch (JSONException e) {
                            for (ServerListener l:listeners) l.onError(formResponse(response.toString()));
                        } catch (Exception e)
                        {
                            Essages.addEssage("Login UE:"+e.toString());
                        }

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            for (ServerListener l : listeners)
                                l.onError(formResponse(error.toString()));
                        } catch (Exception e)
                        {
                            Essages.addEssage("Login Error UE:"+e.toString());
                        }
                    }
                });
        reqq.add(jsObjRequest);
        return true;
    }
    private JSONObject formResponse(String resp){
        JSONObject result=new JSONObject();
        try {
            result.put("Error","Unexpected Response");
            result.put("Message",resp);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return result;
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
        Log.d("Debug info", "Connect url:" + url);
        if ("Y".equals(GameSettings.getInstance().get("NET_DEBUG"))) Essages.addEssage("Net:"+url);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            clearListener();
                            if (response.has("Error")) {
                                for (ServerListener l : listeners) l.onError(response);
                            } else {
                                for (ServerListener l : listeners) l.onRefresh(response);
                            }
                            if (lockedActions != null) {
                                for (ObjectAction act : lockedActions) {
                                    act.setEnable(true);
                                }
                                lockedActions.clear();
                            }
                        }  catch (Exception e)
                        {
                            Essages.addEssage("Refresh UE:"+e.toString());
                        }
                    }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            for (ServerListener l : listeners)
                                l.onError(formResponse(error.toString()));
                        } catch (Exception e)
                        {
                            Essages.addEssage("Refresh Error UE:"+e.toString());
                        }
                    }
                });
        reqq.add(jsObjRequest);
        return true;
    }
    public boolean RefreshCurrent(){
        RefreshData(GPSInfo.getInstance().GetLat(),GPSInfo.getInstance().GetLng());
        return true;
    }

    private ArrayList<ObjectAction> lockedActions;
    private HashMap<Response.Listener<JSONObject>,ObjectAction> listenersMap;
    private HashMap<Response.ErrorListener,ObjectAction> errorMap;


    /**
     * Exec simple action
     * @param action Action
     * @param Target Target GUID
     * @param Lat Latitude of player
     * @param Lng Longtitude of player
     * @return true
     */
    public boolean ExecCommand(ObjectAction action, String Target, int Lat,int Lng , int TLat,int TLng){
        Log.d("DebugAction", "Step1");
        if (!checkConnection()) return false;
        if (Token==null) return false;
        String url=ServerAddres+"/getdata.jsp"+"?Token="+Token+"&ReqName="+action.getCommand()+"&plat="+Lat+"&plng="+Lng+"&TGUID="+Target+"&lat="+TLat+"&lng="+TLng;
        Log.d("Debug info", "Connection url:" + url);
        if ("Y".equals(GameSettings.getInstance().get("NET_DEBUG"))) Essages.addEssage("Net:"+url);
        if (lockedActions==null) lockedActions=new ArrayList<>();

        lockedActions.add(action);
        action.preAction();

        Response.Listener<JSONObject> l=new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    clearListener();

                    if (response.has("Error")) {
                        for (ServerListener l : listeners) l.onError(response);
                        if (listenersMap.get(this) != null) listenersMap.get(this).postError();
                    } else {
                        for (ServerListener l : listeners) l.onAction(response);
                        if (listenersMap.get(this) != null) listenersMap.get(this).postAction();
                    }
                } catch (Exception e)
                {
                    Essages.addEssage("Command UE:"+e.toString());
                }
            }
        };

        Response.ErrorListener le=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    for (ServerListener l : listeners) l.onError(formResponse(error.toString()));
                    if (errorMap.get(this) != null) errorMap.get(this).postError();
                } catch (Exception e)
                {
                    Essages.addEssage("Command Error UE:"+e.toString());
                }
            }
        };
        listenersMap.put(l,action);
        errorMap.put(le,action);
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, l, le);
        reqq.add(jsObjRequest);
        return true;
    }



    public boolean getPlayerInfo(){

        if (!checkConnection()) return false;
        if (Token==null) return false;

        String url=ServerAddres+"/getdata.jsp"+"?Token="+Token+"&ReqName=GetPlayerInfo";
        Log.d("Debug info", "Connection url:" + url);
        if ("Y".equals(GameSettings.getInstance().get("NET_DEBUG"))) Essages.addEssage("Net:"+url);
        Response.Listener<JSONObject> l=new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    clearListener();
                    if (response.has("Error")) {
                        for (ServerListener l : listeners) l.onError(response);
                    } else {
                        for (ServerListener l : listeners) l.onPlayerInfo(response);
                    }
                } catch (Exception e)
                {
                    Essages.addEssage("PlayerInfo UE:"+e.toString());
                }

            }
        };
        Response.ErrorListener le=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    for (ServerListener l : listeners) l.onError(formResponse(error.toString()));
                } catch (Exception e)
                {
                    Essages.addEssage("Player Info UE:"+e.toString());
                }
            }
        };


        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null,l , le);
        reqq.add(jsObjRequest);
        return true;
    }
    public boolean getMessage(){

        if (!checkConnection()) return false;
        if (Token==null) return false;

        String url=ServerAddres+"/getdata.jsp"+"?Token="+Token+"&ReqName=GetMessage";
        Log.d("Debug info", "Connection url:" + url);
        if ("Y".equals(GameSettings.getInstance().get("NET_DEBUG"))) Essages.addEssage("Net:"+url);
        Response.Listener<JSONObject> l=new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    clearListener();
                    if (response.has("Error")) {
                        for (ServerListener l : listeners) l.onError(response);
                    } else {
                        for (ServerListener l : listeners) l.onMessage(response);
                    }
                }  catch (Exception e)
                {
                    Essages.addEssage("Message UE:"+e.toString());
                }

            }
        };
        Response.ErrorListener le=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    for (ServerListener l : listeners) l.onError(formResponse(error.toString()));
                } catch (Exception e)
                {
                    Essages.addEssage("Message error UE:"+e.toString());
                }
            }
        };


        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null,l , le);
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

    public boolean setRace(int race){

        if (!checkConnection()) return false;
        if (Token==null) return false;

        String url=ServerAddres+"/getdata.jsp"+"?Token="+Token+"&ReqName=SetRace&Race="+race;
        Log.d("Debug info", "Connection url:" + url);
        if ("Y".equals(GameSettings.getInstance().get("NET_DEBUG"))) Essages.addEssage("Net:"+url);
        Response.Listener<JSONObject> l=new Response.Listener<JSONObject>(){
            @Override
            public void onResponse(JSONObject response) {
                try {
                    clearListener();
                    if (response.has("Error")) {
                        for (ServerListener l : listeners) l.onError(response);
                    } else {
                        getPlayerInfo();
                        if (response.has("Result")) try {
                            Log.d("Debug info", response.getString("Result"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }  catch (Exception e)
                {
                    Essages.addEssage("SetRace UE:"+e.toString());
                }

            }
        };
        Response.ErrorListener le=new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    for (ServerListener l : listeners) l.onError(formResponse(error.toString()));
                }  catch (Exception e)
                {
                    Essages.addEssage("SetRaceE UE:"+e.toString());
                }

            }
        };


        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null,l , le);
        reqq.add(jsObjRequest);
        return true;
    }
}
