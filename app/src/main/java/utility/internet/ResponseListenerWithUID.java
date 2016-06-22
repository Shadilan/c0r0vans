package utility.internet;

import com.android.volley.Response;

import org.json.JSONObject;

/**
 * Обработка запроса к серверу
 */
public abstract class ResponseListenerWithUID implements Response.Listener<JSONObject> {
    public static int UNKNOWN=-1;
    public static final int LOGIN=0;
    public static final int REFRESH=1;
    public static final int ACTION=2;
    public static final int PLAYER = 3;
    public static final int MESSAGE = 4;
    public static final int RATING = 5;
    public static final int FASTSCAN=6;

    public static final int SETRACE = 16;
    public static final int AUTHORIZE=17;
    public static final int REGISTER=18;






    private String UID;
    private String request;
    private int type;

    ResponseListenerWithUID(String UID,String request,int type){
        this.UID=UID;
        this.request=request;
        this.type=type;
    }

    public String getUID() {
        return UID;
    }



    public String getRequest() {
        return request;
    }


    public int getType() {
        return type;
    }
}
