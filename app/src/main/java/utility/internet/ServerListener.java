package utility.internet;

import org.json.JSONObject;

/**
 * Класс описывающий вызовы для ServerConnect по событиям
 * @author Shadilan
 *
 */
public abstract class ServerListener {
    public abstract void onLogin(JSONObject response);
    public abstract void onRefresh(JSONObject response);
    public abstract void onAction(JSONObject response);
    public abstract void onPlayerInfo(JSONObject response);
    public abstract void onError(JSONObject response);
    public abstract void onMessage(JSONObject response);
    public abstract void onRating(JSONObject response);
}
