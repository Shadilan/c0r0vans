package utility.internet;

import org.json.JSONObject;

/**
 * Класс описывающий вызовы для ServerConnect по событиям
 * @author Shadilan
 *
 */
public abstract class ServerListener {
    public static int UNKNOWN=-1;
    public static int LOGIN=0;
    public static int REFRESH=1;
    public static int ACTION=2;
    public static int PLAYER=3;
    public static int MESSAGE=4;
    public static int RATING=5;

    public abstract void onResponse(int TYPE,JSONObject response);
    public abstract void onError(int TYPE,JSONObject response);
    public void onChangeQueue(int count){

    }
}
