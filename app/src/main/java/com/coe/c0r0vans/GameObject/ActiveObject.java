package com.coe.c0r0vans.GameObject;

import android.content.Context;
import android.widget.RelativeLayout;

/**
 * Define type for object for player interaction
 */
public interface ActiveObject {
    int getActionRadius();
    int getRadius();
    void useObject();
    //TODO Перенести в ActiveObject
    RelativeLayout getObjectView(Context context);
}
