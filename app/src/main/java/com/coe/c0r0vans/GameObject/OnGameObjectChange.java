package com.coe.c0r0vans.GameObject;

import java.util.EventListener;

/**
 * ChangeEvent
 */
public abstract class OnGameObjectChange implements EventListener {
    public static final int EXTERNAL=0;
    public static final int INTERNAL=1;
    public static final int PLAYER=2;

    public abstract void onChange(int TYPE);

}
