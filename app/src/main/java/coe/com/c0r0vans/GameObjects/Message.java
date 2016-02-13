package coe.com.c0r0vans.GameObjects;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * @author Shadilan
 * Контейнер сообщения и загрузчик
 */
public class Message {
    private String GUID;
    private String message;
    private String type;
    private String state;
    private Date time;
    public Message(JSONObject jsonObject) throws JSONException {
        loadJSON(jsonObject);
    }
    public void loadJSON(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has("GUID")) GUID=jsonObject.getString("GUID");
        if (jsonObject.has("Message")) message=jsonObject.getString("Message");
        if (jsonObject.has("Type")) type=jsonObject.getString("Type");
        if (jsonObject.has("State")) state=jsonObject.getString("State");
        if (jsonObject.has("Time")) time=new Date(jsonObject.getLong("Time"));
    }

    public String getGUID() {
        return GUID;
    }

    public Date getTime() {
        return time;
    }

    public String getMessage() {
        return message;
    }
}
