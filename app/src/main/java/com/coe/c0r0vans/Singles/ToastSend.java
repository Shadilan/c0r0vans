package com.coe.c0r0vans.Singles;

import android.content.Context;
import android.widget.Toast;

/**
 * Отправка тостов без контекста
 */
public class ToastSend {
    private static Context context;
    public static void init(Context ctx){
        context=ctx;
    }
    public static void send(String text){
        Toast.makeText(context,text,Toast.LENGTH_LONG).show();
    }
}
