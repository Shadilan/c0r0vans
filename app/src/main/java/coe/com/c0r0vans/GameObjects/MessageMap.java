package coe.com.c0r0vans.GameObjects;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import utility.Essages;
import utility.GameSettings;

/**
 * @author Shadilan
 * Контейнер сообщений
 */
public class MessageMap extends HashMap<String,Message>{
    Context ctx;
    Boolean load=false;
    public MessageMap(Context ctx){
        this.ctx=ctx;
        load=true;
        SharedPreferences sp=ctx.getSharedPreferences("MESSAGES",Context.MODE_PRIVATE);
        try {
            String sptext=sp.getString("Messages", "");
            loadJSON(new JSONObject(sptext));
        } catch (JSONException e) {
            if ("Y".equals(GameSettings.getInstance().get("SHOW_NETWORK_ERROR")))
                Essages.addEssage("Error Loading:"+ e.toString());

        }
        load=false;
    }
    public boolean put(Message message){
        if (this.get(message.getGUID())!=null) return false;
        message.setParent(this);
        put(message.getGUID(),message);
        return true;
    }
    public void loadJSON(JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray;
        if (jsonObject.has("Messages")){
            jsonArray=jsonObject.getJSONArray("Messages");
            for (int i=0;i<jsonArray.length();i++){
                Message message=new Message(jsonArray.getJSONObject(i));
                if (put(message)){
                    Essages.addEssage(message,load);
                }
            }
            if (jsonArray.length()>0){
                save();
            }
        }
    }
    private void save() throws JSONException {
        SharedPreferences sp=ctx.getSharedPreferences("MESSAGES",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =sp.edit();
        editor.putString("Messages", getJSON().toString());
        editor.apply();
        editor.commit();
    }
    private JSONObject getJSON() throws JSONException {
        JSONObject resultM=new JSONObject();
        JSONArray result=new JSONArray();
        for (Message o:this.values()){
            result.put(o.getJSON());
        }
        resultM.put("Messages",result);
        return resultM;
    }

    @Override
    public Message remove(Object key) {
        Message m =super.remove(key);
        try {
            save();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return m;
    }
}
