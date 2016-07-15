package com.coe.c0r0vans.UIElements;

/**
 * @author Shadilan
 */
public interface GameObjectView {
    void updateInZone(boolean inZone);
    void close();
    void setContainer(ActionView av);
    void setDistance(int distance);
}
