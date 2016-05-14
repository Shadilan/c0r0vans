package com.coe.c0r0vans;

/**
 * Listener on Change
 */
public abstract class OnGameObjectChange {
    public static final int EXTERNAL=0;
    public static final int PLAYER=1;
    public static final int GAME=2;
    public static final int UNKNOWN=3;

    public abstract void change(int ChangeType);

}
