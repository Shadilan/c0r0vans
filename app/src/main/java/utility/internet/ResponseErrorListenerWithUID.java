package utility.internet;

import com.android.volley.Response;

/**
 * Обработка ошибки запроса
 */
public abstract class ResponseErrorListenerWithUID implements Response.ErrorListener {
    private String UID;
    private String request;
    private int type;

    ResponseErrorListenerWithUID(String UID,String request,int type){
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
