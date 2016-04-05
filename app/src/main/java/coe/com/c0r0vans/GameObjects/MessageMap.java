package coe.com.c0r0vans.GameObjects;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

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
            Log.d("MessageLoad",sptext);
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
        String out=getJSON().toString();
        editor.putString("Messages", out);
        Log.d("MessageSave", out);
        editor.apply();

    }
    private JSONObject getJSON() throws JSONException {
        List<Message> msg=new LinkedList<>(this.values());
        Collections.sort(msg, new Comparator<Message>() {
            @Override
            public int compare(Message lhs, Message rhs) {
                return lhs.getTime().compareTo(rhs.getTime());
            }
        });
        JSONObject resultM=new JSONObject();
        JSONArray result=new JSONArray();
        for (Message o:msg){
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
