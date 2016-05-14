package com.coe.c0r0vans.GameObjects;

import com.coe.c0r0vans.UIElements.ActionView;

/**
 * @author Shadilan
 */
public interface GameObjectView {
    void updateInZone(boolean inZone);
    void close();
    void setContainer(ActionView av);
    void setDistance(int distance);
}
