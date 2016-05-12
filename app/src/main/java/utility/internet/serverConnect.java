package utility.internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import coe.com.c0r0vans.GameObjects.ObjectAction;
import coe.com.c0r0vans.GameObjects.Player;
import coe.com.c0r0vans.MyGoogleMap;
import coe.com.c0r0vans.R;
import utility.GPSInfo;
import utility.StringUtils;

/**
 * Объект обеспечивающий соединение с сервером и взаимодействие с сервером. Singleton.
 * @author Shadilan
 */
public class serverConnect {
    private final int max_retry=3;
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
    private String login="";

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
        login=Login;
        runRequest(UUID.randomUUID().toString(),url,ResponseListenerWithUID.LOGIN);
        sendDebug(1,"Login try.");
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

    int oldLat=0;
    int oldLng=0;
    long oldTime=0;

    /**
     * Get new data
     * @param Lat Latitude of position to get data
     * @param Lng Longtitude of position to get data
     * @return true
     */
    public boolean RefreshData(int Lat,int Lng){
        String UID= UUID.randomUUID().toString();
        if (!checkConnection()) return false;
        if (Token==null) return false;
        //TODO: Придумать другой вариант с меньше связностью кода
        MyGoogleMap.setOldLatLng(Lat,Lng);
        String hash= StringUtils.MD5("COWBOW"+Token+Lat+Lng+"ScanRange"+UID);
        String url=ServerAddres+"/getdata.jsp"+"?ReqName=ScanRange&Token="+Token+"&plat="+Lat+"&plng="+Lng+"&UUID="+UID+"&hash="+hash;
        runRequest(UID, url, ResponseListenerWithUID.REFRESH);
        oldLat=Lat;
        oldLng=Lng;
        oldTime=new Date().getTime();
        return true;
    }
    public boolean RefreshCurrent(){
        return RefreshData((int)(MyGoogleMap.getMap().getCameraPosition().target.latitude*1e6),(int)(MyGoogleMap.getMap().getCameraPosition().target.longitude*1e6));
    }
    public boolean checkRefresh() {
        long newTime = new Date().getTime();
        return ((GPSInfo.getDistance(new LatLng(oldLat / 1e6, oldLng / 1e6), GPSInfo.getInstance().getLatLng()) > 500) || newTime - oldTime > 5 * 1000 * 60) && RefreshCurrent();
    }

    private ArrayList<ObjectAction> lockedActions;
    private HashMap<String,ObjectAction> listenersMap;
    private HashMap<String,ObjectAction> errorMap;


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
        sendDebug(5,"Action execute:"+action.getCommand());
        if (lockedActions==null) lockedActions=new ArrayList<>();
        lockedActions.add(action);
        action.preAction();
        String UID=UUID.randomUUID().toString();
        String hash= StringUtils.MD5("COWBOW" + Token + Lat + Lng + action.getCommand() + UID+Target+TLat+TLng);
        String url=ServerAddres+"/getdata.jsp"+"?Token="+Token+"&ReqName="+action.getCommand()+"&plat="+Lat+"&plng="+Lng+"&TGUID="+Target+"&lat="+TLat+"&lng="+TLng+"&UID="+UID+"&hash="+hash;
        listenersMap.put(UID, action);
        errorMap.put(UID, action);
        runRequest(UID, url, ResponseListenerWithUID.ACTION);
        return true;
    }

    public boolean getPlayerInfo(){
        if (!checkConnection()) return false;
        if (Token==null) return false;
        String url=ServerAddres+"/getdata.jsp"+"?Token="+Token+"&ReqName=GetPlayerInfo";
        runRequest(UUID.randomUUID().toString(),url,ResponseListenerWithUID.PLAYER);
        return true;
    }

    public boolean getMessage(){
        if (!checkConnection()) return false;
        if (Token==null) return false;
        String url=ServerAddres+"/getdata.jsp"+"?Token="+Token+"&ReqName=GetMessage";
        runRequest(UUID.randomUUID().toString(),url,ResponseListenerWithUID.MESSAGE);
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
        runRequest(UUID.randomUUID().toString(), url, ResponseListenerWithUID.SETRACE);
        return true;
    }
    public boolean GetRating(){
        if (!checkConnection()) return false;
        if (Token==null) return false;

        String url=ServerAddres+"/getdata.jsp"+"?Token="+Token+"&ReqName=GetRate";
        runRequest(UUID.randomUUID().toString(),url,ResponseListenerWithUID.RATING);
        return true;
    }

    private void runRequest(String UID,String request,int type){
        if (busy) requestList.add(new RequestData(UID,request,type));
        else runRequest(UID, request, type, 0);
        int queuesize=requestList.size()+1;
        if (listeners!=null)
            for (ServerListener l:listeners){

                l.onChangeQueue(queuesize);
            }
    }
    boolean busy=false;
    private class RequestData{
        public RequestData(String UID,String request, int type){
            this.request=request;
            this.UID=UID;
            this.type=type;
        }
        String UID;
        String request;
        int type;
    }
    LinkedList<RequestData> requestList=new LinkedList<>();
    private void runNextRequest(){
        if (requestList.isEmpty()) {
            busy=false;
            if (listeners!=null)
                for (ServerListener l:listeners){

                    l.onChangeQueue(0);
                }
            return;
        }
        RequestData requestData=requestList.poll();
        runRequest(requestData.UID,requestData.request,requestData.type,0);
        int queuesize=requestList.size()+1;
        if (listeners!=null)
            for (ServerListener l:listeners){
                l.onChangeQueue(queuesize);
            }
    }
    public void clearQueue(){
        requestList.clear();
        busy=false;
        if (listeners!=null)
            for (ServerListener l:listeners){

                l.onChangeQueue(0);
            }
    }
    public int getQueueSize(){
        return requestList.size();
    }
    private void runRequest(String UID,String request,int type, final int try_count){
        busy=true;
        if (!checkConnection()) return;
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, request, null, new ResponseListenerWithUID(UID,request,type){
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {

                            clearListener();

                            if (response.has("Error")) {
                                for (ServerListener l : listeners) l.onError(response);
                            } else {
                                switch (getType()){
                                    case LOGIN:
                                        try {
                                            Token = response.getString("Token");
                                            for (ServerListener l : listeners) l.onLogin(response);
                                        } catch (JSONException e) {
                                            for (ServerListener l:listeners) l.onError(formResponse(response.toString()));
                                        }
                                        break;
                                    case REFRESH:
                                        for (ServerListener l : listeners) l.onRefresh(response);

                                        break;
                                    case ACTION: for (ServerListener l : listeners) l.onAction(response);
                                        if (listenersMap.get(getUID()) != null) listenersMap.get(getUID()).postAction(response);
                                        break;
                                    case SETRACE:getPlayerInfo();
                                        break;
                                    case MESSAGE:for (ServerListener l : listeners) l.onMessage(response);
                                        break;
                                    case PLAYER:for (ServerListener l : listeners) l.onPlayerInfo(response);
                                        break;
                                    case RATING:for (ServerListener l : listeners) l.onRating(response);
                                        break;
                                }
                            }
                            if (lockedActions != null) {
                                for (ObjectAction act : lockedActions) {
                                    act.setEnable(true);
                                }
                                lockedActions.clear();
                            }

                        }
                        catch (Exception e){
                            sendDebug(2,e.toString()+"\n"+ Arrays.toString(e.getStackTrace()));
                        }
                        runNextRequest();


                    }

                }, new ResponseErrorListenerWithUID(UID,request,type) {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {

                            if (error.networkResponse == null && error.getClass().equals(TimeoutError.class) && try_count<max_retry) runRequest(getUID(), getRequest(), getType(),try_count+1);
                            else {
                                if (getType() == 2) if (errorMap.get(getUID()) != null)
                                    errorMap.get(getUID()).postError();
                                for (ServerListener l : listeners)
                                    l.onError(formResponse(error.toString()));
                                runNextRequest();
                            }


                        } catch (Exception e)
                        {
                            serverConnect.getInstance().sendDebug(3, "Net UE:" + e.toString()+ Arrays.toString(e.getStackTrace()));
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        reqq.add(jsObjRequest);

    }
    public void sendDebug(int type,String message){
        //
        Log.d("SendDebug",message);
        if (!checkConnection()) return;
        String request="https://support-merchantarg.rhcloud.com/addLog.jsp";

        JSONObject reqTest= null;
        String android_id;
        try {
            android_id = Settings.Secure.getString(context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } catch(Exception e){
            android_id="";
        }
        String user="";
        if (Player.getPlayer()!=null) user=Player.getPlayer().getName();
        String version=context.getResources().getString(R.string.version);
        String hash= StringUtils.MD5("COWBOW"+user+android_id+message+version);
        try {
            reqTest = new JSONObject().put("Type",type)
                    .put("User", login)
                    .put("Device",android_id)
                    .put("Version", version)
                    .put("Data", message)
                    .put("Lat",GPSInfo.getInstance().getLatLng().latitude)
                    .put("Lng",GPSInfo.getInstance().getLatLng().longitude)
                    .put("hash", hash).put("Version", version);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.POST, request, reqTest, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(20 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        reqq.add(jsObjRequest);
    }
}
