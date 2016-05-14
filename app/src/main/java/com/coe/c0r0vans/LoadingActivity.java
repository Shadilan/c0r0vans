package com.coe.c0r0vans;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class LoadingActivity extends AppCompatActivity {
    Handler handler;
    LoadingActivity self;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        self=this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loading);

        handler=new Handler();
        Thread thread=new Thread(new Runnable() {
            @Override
            public void run() {


                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(self,MainWindow.class));
                    }
                });
            }
        });
        thread.start();
    }

}
