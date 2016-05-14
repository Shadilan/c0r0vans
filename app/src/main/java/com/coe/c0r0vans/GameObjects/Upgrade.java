package com.coe.c0r0vans.GameObjects;

import android.graphics.Bitmap;

import org.json.JSONException;
import org.json.JSONObject;

import utility.ImageLoader;

/**
 * @author Shadilan
 * Элемент списка апгрейдов
 */
public class Upgrade {
    private String Type;
    private String Name;
    private String Description;
    private int effect1=0;
/*    private int effect2=0;
    private int nextCost=0;*/
    private int Cost=0;
    private int reqCityLev=0;
    private int level;
    private int effect2;

    public void loadJSON(JSONObject object) throws JSONException {

        if (object.has("Type")) Type=object.getString("Type");
        if (object.has("Level")) level=object.getInt("Level");
        if (object.has("Name")) Name=object.getString("Name");
        if (object.has("Description")) Description=object.getString("Description");
        if (object.has("ReqCityLev")) reqCityLev=object.getInt("ReqCityLev");
        //if (object.has("NextCost")) nextCost=object.getInt("NextCost");
        if (object.has("Effect1")) effect1=object.getInt("Effect1");
        if (object.has("Effect2")) effect2=object.getInt("Effect2");
        if (object.has("Cost")) Cost=object.getInt("Cost");
    }
    public Upgrade(JSONObject object){
        try {
            loadJSON(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //public int getNextCost(){return nextCost;}
    public Bitmap getImage(){
        return ImageLoader.getImage(Type);
    }
    public String getDescription(){
        return Description;
    }
    public String getName(){
        return Name+" (Уровень "+level+")";
    }
    public String getType() {return Type;}
    public int getCost(){return Cost;}
    public int getReqCityLev(){return reqCityLev;}

    public int getLevel() {
        return level;
    }

    public int getEffect1() {
        return effect1;
    }

    public JSONObject getJSON() throws JSONException {
        JSONObject result=new JSONObject();
        result.put("Type",Type);
        result.put("Level",level);
        result.put("Name",Name);
        result.put("Description",Description);
        result.put("ReqCityLev",reqCityLev);
        result.put("Effect1",effect1);
        result.put("Effect2",effect2);
        result.put("Cost",Cost);

        return result;
    }

    public int getEffect2() {
        return effect2;
    }
}
