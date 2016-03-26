package coe.com.c0r0vans.GameObjects;

import coe.com.c0r0vans.ActionView;

/**
 * @author Shadilan
 */
public interface GameObjectView {
    void updateInZone(boolean inZone);
    void close();
    void setContainer(ActionView av);
}
