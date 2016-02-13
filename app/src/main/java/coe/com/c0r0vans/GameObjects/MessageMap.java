package coe.com.c0r0vans.GameObjects;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import utility.Essages;

/**
 * @author Shadilan
 * Контейнер сообщений
 */
public class MessageMap extends HashMap<String,Message>{
    public boolean put(Message message){
        if (this.get(message.getGUID())!=null) return false;
        else put(message.getGUID(),message);
        return true;
    }
    public void loadJSON(JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray;
        if (jsonObject.has("Messages")){
            jsonArray=jsonObject.getJSONArray("Messages");
            for (int i=0;i<jsonArray.length();i++){
                Message message=new Message(jsonArray.getJSONObject(i));
                if (put(message)){
                    Essages.addEssage(message.getTime(),message.getMessage());
                }
            }
        }
    }
}
