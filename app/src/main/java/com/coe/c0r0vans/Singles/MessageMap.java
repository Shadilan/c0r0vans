package com.coe.c0r0vans.Singles;

import android.content.Context;
import android.content.SharedPreferences;

import com.coe.c0r0vans.GameObjects.Message;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import utility.GATracker;
import utility.notification.Essages;
import utility.notification.OnEssageListener;
import utility.settings.GameSettings;

/**
 * @author Shadilan
 * Контейнер сообщений
 */
//Do Single
public class MessageMap {
    private static Context context;
    private static boolean load=false;
    private static HashMap<String,Message> messages;
    public static void clearAll(){
        if (messages!=null) messages.clear();
        try {
            save();
        } catch (JSONException e) {
            GATracker.trackException("MessageSubsystem","Error Clear");
        }

    }

    public static void init(Context ctx){
        context=ctx;
        //todo вынести в отдельный поток после переноса визуальной части в хэндлер
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    load=true;
                    SharedPreferences sp=context.getSharedPreferences("MESSAGES",Context.MODE_PRIVATE);
                    try {
                        String sptext=sp.getString("Messages", "");
                        if (!"".equals(sptext))  loadJSON(new JSONObject(sptext));
                    } catch (JSONException e) {
                        if ("Y".equals(GameSettings.getInstance().get("SHOW_NETWORK_ERROR")))
                            Essages.addEssage(Essages.SYSTEM,"Error Loading:"+ e.toString());
                        GATracker.trackException("MessageSubsystem",e);

                    }
                    load=false;
                    Essages.addListener(new OnEssageListener() {
                        @Override
                        public void onAdd(int type, Message msg) {

                        }

                        @Override
                        public void onClear() {
                            clearAll();
                        }

                        @Override
                        public void onRemove(Message msg) {

                        }
                    });
                }
            }).start();
        } catch(Exception e){
            GATracker.trackException("MessageSubsystem",e);

        }

    }
    public static boolean put(Message message){
        if (messages==null) messages=new HashMap<>();
        if (messages.get(message.getGUID())!=null) return false;

        messages.put(message.getGUID(),message);
        return true;
    }

    public static void loadJSON(JSONObject jsonObject) throws JSONException {
        JSONArray jsonArray;
        if (jsonObject.has("Messages")){
            jsonArray=jsonObject.getJSONArray("Messages");
            if (messages==null) messages=new HashMap<>();
            for (int i=0;i<jsonArray.length();i++){
                Message message=new Message(jsonArray.getJSONObject(i));

                if (put(message)){
                    message.notify=!load;
                    Essages.addEssage(Essages.ALERT,message);
                }
            }
            if (jsonArray.length()>0){
                save();
            }
        }
    }
    private static void save() throws JSONException {
        SharedPreferences sp=context.getSharedPreferences("MESSAGES",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =sp.edit();
        String out=getJSON().toString();
        editor.putString("Messages", out);
        editor.apply();
    }
    private static JSONObject getJSON() throws JSONException {
        List<Message> msg=new LinkedList<>(messages.values());
        Collections.sort(msg, new Comparator<Message>() {
            @Override
            public int compare(Message lhs, Message rhs) {
                return lhs.getTime().compareTo(rhs.getTime());
            }
        });
        JSONObject resultM=new JSONObject();
        JSONArray result=new JSONArray();
        //todo Убрать ограничение
        int i=msg.size();
        msg=msg.subList(Math.max(i-30,0),i);
        for (Message o:msg){
            i++;
            result.put(o.getJSON());
        }
        resultM.put("Messages",result);
        return resultM;
    }

    public static Message remove(Object key) {
        if (messages==null) return null;
        Message m =messages.remove(key);
        try {
            save();
        } catch (JSONException e) {
            GATracker.trackException("MessageSubsystem","Error Remove");
        }
        return m;
    }



}
