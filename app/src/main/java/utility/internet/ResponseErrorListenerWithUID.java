package utility.internet;

import com.android.volley.Response;

import org.json.JSONObject;

/**
 * Обработка ошибки запроса
 */
public abstract class ResponseErrorListenerWithUID implements Response.ErrorListener {
    private String UID;
    private String request;
    private int type;
    private JSONObject body;

    ResponseErrorListenerWithUID(String UID,String request,int type,JSONObject body){
        this.UID=UID;
        this.request=request;
        this.type=type;
        this.body=body;
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
    public JSONObject getBody() {return  body;}
}
