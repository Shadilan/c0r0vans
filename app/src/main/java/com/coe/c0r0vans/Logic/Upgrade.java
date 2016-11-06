package com.coe.c0r0vans.Logic;

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
    private int Cost=0;
    private int reqCityLev=0;
    private int level;
    private int effect2;
    private double OUC=1;


    public void loadJSON(JSONObject object) throws JSONException {

        if (object.has("Type")) Type=object.getString("Type");
        if (object.has("Level")) level=object.getInt("Level");
        if (object.has("Effect1")) effect1=object.getInt("Effect1");
        if (object.has("Effect2")) effect2=object.getInt("Effect2");
        if (object.has("OUC")) OUC=object.getDouble("OUC");
        /*switch (Type){
            case "speed":
                Name= StringUtils.getString(R.string.sk_name_speed);
                break;
            case "cargo":
                Name=StringUtils.getString(R.string.sk_name_cargo);
                break;
            case "bargain":
                Name=StringUtils.getString(R.string.sk_name_bargain);
                break;
            case "ambushes":
                Name=StringUtils.getString(R.string.sk_name_ambushes);
                break;
            case "set_ambushes":
                Name=StringUtils.getString(R.string.sk_name_set_ambushes);
                break;
            case "paladin":
                Name=StringUtils.getString(R.string.sk_name_paladin);
                break;
            case "founder":
                Name=StringUtils.getString(R.string.sk_name_founder);
                break;
            default:*/
                if (object.has("Name")) Name=object.getString("Name");

        //}
        if (object.has("Description")) Description=object.getString("Description");
        if (object.has("ReqCityLev")) reqCityLev=object.getInt("ReqCityLev");
        if (object.has("Cost")) Cost=object.getInt("Cost");

    }
    Upgrade(JSONObject object){
        try {
            loadJSON(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public int getTypeNum(){
        switch (getType()){
            case "speed": return 1;
            case "set_ambushes":
                return 2;
            case "ambushes":
                return 3;
            case "cargo":
                return 4;
            case "bargain":
                return 5;
            case "paladin":
                return 6;
            case "founder":
                return 7;
            case "leadership":
                return 8;
            default:
                return 0;
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
    int getReqCityLev(){return reqCityLev;}

    public int getLevel() {
        return level;
    }

    int getEffect1() {
        return effect1;
    }

    JSONObject getJSON() throws JSONException {
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

    int getEffect2() {
        return effect2;
    }

    public double getOUC() {
        return OUC;
    }
}
