package com.coe.c0r0vans.GameObjects;

import android.content.Context;
import android.widget.ImageButton;

/**
 * @author Shadilan
 * Кнопка запуска экшенов.
 */
public class CommandButton  extends ImageButton {
        public CommandButton(Context context) {
            super(context);
        }
        public CommandButton(Context context,ObjectAction action) {
            super(context);
            this.action=action;
        }
    public CommandButton(Context context,ObjectAction action,String GUID) {
        super(context);
        this.guid=GUID;
        this.action=action;
    }
        private ObjectAction action;
        private String guid;
        public ObjectAction getAction(){
            return action;
        }

    public String getGuid() {
        return guid;
    }
}
