package coe.com.c0r0vans.GameObjects;

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
    private Number Level;
    private String Name;
    private String Description;
    private int nextCost=0;

    public void loadJSON(JSONObject object) throws JSONException {
        if (object.has("Type")) Type=object.getString("Type");
        if (object.has("Level")) Level=object.getInt("Level");
        if (object.has("Name")) Name=object.getString("Name");
        if (object.has("Description")) Description=object.getString("Description");

        if (object.has("NextCost")) nextCost=object.getInt("NextCost");
    }
    public Upgrade(JSONObject object){
        try {
            loadJSON(object);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public int getNextCost(){return nextCost;}
    public Bitmap getImage(){
        return ImageLoader.getImage(Type);
    }
    public String getDescription(){
        return "Уровень "+Level+"\n"+Description;
    }
    public String getName(){
        return Name;
    }

}
