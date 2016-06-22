package utility.internet;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.coe.c0r0vans.GameObjects.ObjectAction;
import com.coe.c0r0vans.GameObjects.Player;
import com.coe.c0r0vans.MyGoogleMap;
import com.coe.c0r0vans.R;
import com.coe.c0r0vans.UIElements.ChooseFaction;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.UUID;

import utility.GATracker;
import utility.GPSInfo;
import utility.notification.Essages;

/**
 * Объект обеспечивающий соединение с сервером и взаимодействие с сервером. Singleton.
 * @author Shadilan
 */
//TODO Синхронизировать типы вызовов в листенере и здесь
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
    //private String login="";
    String version="";
    private ArrayList<ObjectAction> lockedActions;
    private HashMap<String,ObjectAction> listenersMap;
    private HashMap<String,ObjectAction> errorMap;

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
        version=context.getResources().getString(R.string.version);

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
     *  Login and get Secure Token

     * @return true
     */
    public boolean ExecAuthorize(String googleToken){
        if (!checkConnection()) return false;
        String url=new UrlBuilder(ServerAddres+"/authorize.jsp","Authorize",version)
                .put("GoogleToken",googleToken)
                .build();
        runRequest(UUID.randomUUID().toString(),url,ResponseListenerWithUID.AUTHORIZE);
        return true;
    }

    public boolean ExecRegister(String googleToken,String userName,String inviteCode){
        if (!checkConnection()) return false;
        String url=new UrlBuilder(ServerAddres+"/authorize.jsp","Register",version)
                .put("GoogleToken",googleToken)
                .put("UserName",userName)
                .put("InviteCode",inviteCode)
                .build();
        runRequest(UUID.randomUUID().toString(),url,ResponseListenerWithUID.REGISTER);
        return true;
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
    public boolean callScanRange(int Lat, int Lng){
        String UID= UUID.randomUUID().toString();
        if (!checkConnection()) return false;
        if (Token==null) return false;
        MyGoogleMap.setOldLatLng(Lat,Lng);
        String url=new UrlBuilder(ServerAddres+"/getdata.jsp","ScanRange",version)
                .put("Token",Token)
                .put("plat",Lat)
                .put("plng",Lng)
                .put("UUID",UID)
                .build();
        runRequest(UID, url, ResponseListenerWithUID.REFRESH);
        oldLat=Lat;
        oldLng=Lng;
        oldTime=new Date().getTime();
        return true;
    }

    public boolean callGetPlayerInfo(){
        if (!checkConnection()) return false;
        if (Token==null) return false;
        String UID=UUID.randomUUID().toString();
        String url=new UrlBuilder(ServerAddres+"/getdata.jsp","GetPlayerInfo",version)
                .put("Token",Token)
                .put("UUID",UID)
                .build();
        runRequest(UUID.randomUUID().toString(),url,ResponseListenerWithUID.PLAYER);
        return true;
    }

    public boolean callGetMessage(){
        if (!checkConnection()) return false;
        if (Token==null) return false;
        String UID=UUID.randomUUID().toString();
        String url=new UrlBuilder(ServerAddres+"/getdata.jsp","GetMessage",version)
                .put("Token",Token)
                .put("UUID",UID)
                .build();
        runRequest(UUID.randomUUID().toString(),url,ResponseListenerWithUID.MESSAGE);
        return true;
    }

    public boolean callSetAmbush(ObjectAction action, int Lat,int Lng , int TLat,int TLng){
        if (!checkConnection()) return false;
        if (Token==null) return false;
        if (lockedActions==null) lockedActions=new ArrayList<>();
        lockedActions.add(action);
        action.preAction();
        String UID=UUID.randomUUID().toString();
        String url=new UrlBuilder(ServerAddres+"/getdata.jsp",action.getCommand(),version)
                .put("Token",Token)
                .put("plat",Lat)
                .put("plng",Lng)
                .put("lat",TLat)
                .put("lng",TLng)
                .put("UUID",UID)
                .build();
        listenersMap.put(UID, action);
        errorMap.put(UID, action);
        runRequest(UID, url, ResponseListenerWithUID.ACTION);
        return true;
    }

    public boolean callDestroyAmbush(ObjectAction action, int Lat,int Lng , String target){
        if (!checkConnection()) return false;
        if (Token==null) return false;
        if (lockedActions==null) lockedActions=new ArrayList<>();
        lockedActions.add(action);
        action.preAction();
        String UID=UUID.randomUUID().toString();
        String url=new UrlBuilder(ServerAddres+"/getdata.jsp",action.getCommand(),version)
                .put("Token",Token)
                .put("plat",Lat)
                .put("plng",Lng)
                .put("TGUID",target)
                .put("UUID",UID)
                .build();
        listenersMap.put(UID, action);
        errorMap.put(UID, action);
        runRequest(UID, url, ResponseListenerWithUID.ACTION);
        return true;
    }
    public boolean callCanelAmbush(ObjectAction action, String target){
        if (!checkConnection()) return false;
        if (Token==null) return false;
        if (lockedActions==null) lockedActions=new ArrayList<>();
        lockedActions.add(action);
        action.preAction();
        String UID=UUID.randomUUID().toString();
        String url=new UrlBuilder(ServerAddres+"/getdata.jsp",action.getCommand(),version)
                .put("Token",Token)
                .put("TGUID",target)
                .put("UUID",UID)
                .build();
        listenersMap.put(UID, action);
        errorMap.put(UID, action);
        runRequest(UID, url, ResponseListenerWithUID.ACTION);
        return true;
    }
    public boolean callDropUnfinishedRoute(ObjectAction action){
        if (!checkConnection()) return false;
        if (Token==null) return false;
        if (lockedActions==null) lockedActions=new ArrayList<>();
        lockedActions.add(action);
        action.preAction();
        String UID=UUID.randomUUID().toString();
        String url=new UrlBuilder(ServerAddres+"/getdata.jsp",action.getCommand(),version)
                .put("Token",Token)
                .put("UUID",UID)
                .build();
        listenersMap.put(UID, action);
        errorMap.put(UID, action);
        runRequest(UID, url, ResponseListenerWithUID.ACTION);
        return true;
    }
    public boolean callBuyUpgrade(ObjectAction action, int Lat,int Lng , String target){
        if (!checkConnection()) return false;
        if (Token==null) return false;
        if (lockedActions==null) lockedActions=new ArrayList<>();
        lockedActions.add(action);
        action.preAction();
        String UID=UUID.randomUUID().toString();
        String url=new UrlBuilder(ServerAddres+"/getdata.jsp",action.getCommand(),version)
                .put("Token",Token)
                .put("plat",Lat)
                .put("plng",Lng)
                .put("TGUID",target)
                .put("UUID",UID)
                .build();
        listenersMap.put(UID, action);
        errorMap.put(UID, action);
        runRequest(UID, url, ResponseListenerWithUID.ACTION);
        return true;
    }
    public boolean callStartRoute(ObjectAction action,int Lat,int Lng,String target){
        if (!checkConnection()) return false;
        if (Token==null) return false;
        if (lockedActions==null) lockedActions=new ArrayList<>();
        lockedActions.add(action);
        action.preAction();
        String UID=UUID.randomUUID().toString();
        String url=new UrlBuilder(ServerAddres+"/getdata.jsp",action.getCommand(),version)
                .put("Token",Token)
                .put("plat",Lat)
                .put("plng",Lng)
                .put("TGUID",target)
                .put("UUID",UID)
                .build();
        listenersMap.put(UID, action);
        errorMap.put(UID, action);
        runRequest(UID, url, ResponseListenerWithUID.ACTION);
        return true;
    }
    public boolean callFinishRoute(ObjectAction action,int Lat,int Lng,String target){
        if (!checkConnection()) return false;
        if (Token==null) return false;
        if (lockedActions==null) lockedActions=new ArrayList<>();
        lockedActions.add(action);
        action.preAction();
        String UID=UUID.randomUUID().toString();
        String url=new UrlBuilder(ServerAddres+"/getdata.jsp",action.getCommand(),version)
                .put("Token",Token)
                .put("plat",Lat)
                .put("plng",Lng)
                .put("TGUID",target)
                .put("UUID",UID)
                .build();
        listenersMap.put(UID, action);
        errorMap.put(UID, action);
        runRequest(UID, url, ResponseListenerWithUID.ACTION);
        return true;
    }

    public boolean RefreshCurrent(){
        return callScanRange((int)(MyGoogleMap.getMap().getCameraPosition().target.latitude*1e6),(int)(MyGoogleMap.getMap().getCameraPosition().target.longitude*1e6));
    }
    public boolean checkRefresh() {
        long newTime = new Date().getTime();
        return ((GPSInfo.getDistance(new LatLng(oldLat / 1e6, oldLng / 1e6), GPSInfo.getInstance().getLatLng()) > 500) || newTime - oldTime > 5 * 1000 * 60) && RefreshCurrent();
    }

    public boolean createCity(ObjectAction action,int Lat,int Lng , int TLat,int TLng){
        if (!checkConnection()) return false;
        if (Token==null) return false;
        if (lockedActions==null) lockedActions=new ArrayList<>();
        lockedActions.add(action);
        action.preAction();
        String UID=UUID.randomUUID().toString();
        String url=new UrlBuilder(ServerAddres+"/getdata.jsp",action.getCommand(),version)
                .put("Token",Token)
                .put("plat",Lat)
                .put("plng",Lng)
                .put("lat",TLat)
                .put("lng",TLng)
                .put("UUID",UID)
                .build();
        listenersMap.put(UID, action);
        errorMap.put(UID, action);
        runRequest(UID, url, ResponseListenerWithUID.ACTION);
        return true;
    }
    public boolean hirePeople(ObjectAction action,int Lat,int Lng , String target,int amount){
        if (!checkConnection()) return false;
        if (Token==null) return false;
        if (lockedActions==null) lockedActions=new ArrayList<>();
        String version=context.getResources().getString(R.string.version);
        lockedActions.add(action);
        action.preAction();
        String UID=UUID.randomUUID().toString();
        String url=new UrlBuilder(ServerAddres+"/getdata.jsp",action.getCommand(),version)
                .put("Token",Token)
                .put("plat",Lat)
                .put("plng",Lng)
                .put("TGUID",target)
                .put("Amount",amount)
                .put("UUID",UID)
                .build();
        listenersMap.put(UID, action);
        errorMap.put(UID, action);
        runRequest(UID, url, ResponseListenerWithUID.ACTION);
        return true;
    }



    /**
     * Check if we have Token
     * @return true if we have token otherwise false;
     */
    public boolean isLogin(){
        return Token != null;
    }

    public boolean callSetRace(int race){
        if (!checkConnection()) return false;
        if (Token==null) return false;
        String UID=UUID.randomUUID().toString();
        String url=new UrlBuilder(ServerAddres+"/getdata.jsp","SetRace",version)
                .put("Token",Token)
                .put("Race",race)
                .put("UUID",UID)
                .build();
        runRequest(UUID.randomUUID().toString(), url, ResponseListenerWithUID.SETRACE);
        return true;
    }
    /*public boolean GetRating(){
        if (!checkConnection()) return false;
        if (Token==null) return false;
        String UID=UUID.randomUUID().toString();
        String url=new UrlBuilder(ServerAddres+"/getdata.jsp","GetRate",version)
                .put("Token",Token)
                .put("UUID",UID)
                .build();
        runRequest(UUID.randomUUID().toString(),url,ResponseListenerWithUID.RATING);
        return true;
    }*/
    public boolean callFastScan(){
        return callFastScan(GPSInfo.getInstance().GetLat(),GPSInfo.getInstance().GetLng());
    }
    public boolean callFastScan(int Lat,int Lng){
        if (!checkConnection()) return false;
        if (Token==null) return false;
        String UID=UUID.randomUUID().toString();
        String url=new UrlBuilder(ServerAddres+"/getdata.jsp","FastScan",version)
                .put("Token",Token)
                .put("plat",Lat)
                .put("plng",Lng)
                .put("UUID",UID)
                .build();
        runRequest(UUID.randomUUID().toString(), url, ResponseListenerWithUID.FASTSCAN);
        return true;
    }

    private void runRequest(String UID,String request,int type){
        if (busy) requestList.add(new RequestData(UID,request,type));
        else runRequest(UID, request, type, 0);

        final int requestList_size = requestList.size();// Moved  requestList.size() call out of the loop to local variable requestList_size
        for (ServerListener l:listeners){
            l.onChangeQueue(requestList_size+1);
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
        return requestList.size()+1;
    }

    private void runRequest(String UID,String request,int type, final int try_count){
        Log.d("URLRequest",request);
        busy=true;
        if (!checkConnection()) return;
        final JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, request, null, new ResponseListenerWithUID(UID,request,type){
                    @Override
                    public void onResponse(JSONObject response) {
                        try
                        {
                            Log.d("URLRequest",response.toString());
                            clearListener();

                            if (response.has("Error") || (response.has("Result") && !response.getString("Result").equals("OK"))) {
                                switch (getType()) {
                                    case AUTHORIZE:
                                    case REGISTER:
                                            for (ServerListener l : listeners) l.onError(ServerListener.LOGIN,response);
                                        break;
                                    case SETRACE:
                                        //todo:Связность кода - плохо
                                        String err="";
                                        if (response.has("Error")) err=response.getString("Error");
                                        else if (response.has("Result")) err=response.getString("Result");
                                        switch (err){
                                            case "L0001":Essages.addEssage("Потеря соединения. Перезапустите клиента.");
                                            break;
                                            case "O1101":Essages.addEssage("Вы выбрали не существующую фракцию.");
                                            break;
                                            default: if (response.has("Message")) Essages.addEssage(response.getString("Message"));
                                                else Essages.addEssage("Неизвестная ошибка");
                                        }
                                        Player.getPlayer().setRace(0);
                                        new ChooseFaction(context).show();
                                    case ACTION:
                                        if (listenersMap.get(getUID()) != null) listenersMap.get(getUID()).postError(response);
                                        break;
                                    case FASTSCAN:
                                        for (ServerListener l : listeners) l.onError(ServerListener.FASTSCAN,response);
                                        break;
                                    default:
                                        for (ServerListener l : listeners) l.onError(ServerListener.UNKNOWN,response);
                                }
                            } else {
                                switch (getType()){
                                    case LOGIN:
                                        try {
                                            Token = response.getString("Token");
                                            for (ServerListener l : listeners) l.onResponse(ServerListener.LOGIN,response);
                                        } catch (JSONException e) {
                                            for (ServerListener l:listeners) l.onError(ServerListener.LOGIN,formResponse(response.toString()));
                                        }
                                        break;
                                    case REFRESH:
                                        for (ServerListener l : listeners) l.onResponse(ServerListener.REFRESH,response);

                                        break;
                                    case ACTION: for (ServerListener l : listeners) l.onResponse(ServerListener.ACTION,response);
                                        if (listenersMap.get(getUID()) != null) listenersMap.get(getUID()).postAction(response);
                                        break;
                                    case SETRACE:
                                        break;
                                    case MESSAGE:for (ServerListener l : listeners) l.onResponse(ServerListener.MESSAGE,response);
                                        break;
                                    case PLAYER:for (ServerListener l : listeners) l.onResponse(ServerListener.PLAYER,response);
                                        break;
                                    case RATING:for (ServerListener l : listeners) l.onResponse(ServerListener.RATING,response);
                                        break;
                                    case AUTHORIZE:
                                        try {
                                            Token = response.getString("Token");
                                            for (ServerListener l : listeners) l.onResponse(ServerListener.LOGIN,response);
                                        } catch (JSONException e) {
                                            for (ServerListener l:listeners) l.onError(ServerListener.LOGIN,formResponse(response.toString()));
                                        }
                                        break;
                                    case REGISTER:
                                        try {
                                            Token = response.getString("Token");
                                            for (ServerListener l : listeners) l.onResponse(ServerListener.LOGIN,response);
                                        } catch (JSONException e) {
                                            for (ServerListener l:listeners) l.onError(ServerListener.LOGIN,formResponse(response.toString()));
                                        }
                                        break;
                                    case FASTSCAN:
                                        for (ServerListener l : listeners) l.onResponse(ServerListener.FASTSCAN,response);
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
                            GATracker.trackException("NetworkError",e);
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
                                    errorMap.get(getUID()).postError(new JSONObject().put("Error","U0000").put("Message",error.getMessage()));
                                for (ServerListener l : listeners)
                                //TODO передавать корректный тип
                                    l.onError(ServerListener.UNKNOWN,formResponse(error.toString()));
                                runNextRequest();
                            }


                        } catch (Exception e)
                        {
                            GATracker.trackException("NetworkError",e);
                        }
                    }
                });
        jsObjRequest.setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 0,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        reqq.add(jsObjRequest);

    }
    /*public void sendDebug(int type,String message){
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
    }*/
    //UserActions
    /**
     *  Login and get Secure Token
     * @param Login Login of user
     * @param Password Password of user
     * @return true
     */
    /*public boolean ExecLogin(String Login, String Password,String gmail){
        if (!checkConnection()) return false;
        String version=context.getResources().getString(R.string.version);
        String hash= StringUtils.MD5("COWBOW"+Login+Password+gmail+version+"Login");
        String url=ServerAddres+"/login.jsp"+"?Login="+Login+"&Password="+Password+"&GMail="+gmail+"&Version="+version+"&hash="+hash;
        login=Login;

        runRequest(UUID.randomUUID().toString(),url,ResponseListenerWithUID.LOGIN);
        return true;
    }*/
}
