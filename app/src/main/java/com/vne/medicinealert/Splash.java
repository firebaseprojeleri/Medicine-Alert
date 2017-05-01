package com.vne.medicinealert;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.PersistableBundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

/**
 * Created by Volkan Şahin on 20.04.2017.
 */

public class Splash extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Thread mThread = new Thread(){
            @Override
            public void run(){
                try{
                    synchronized (this){
                        wait(1000);
                    }
                } catch (InterruptedException e){

                } finally {
                    Intent i = new Intent(Splash.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        };

        mThread.start();
        //startActivity(new Intent(Splash.this, MainActivity.class));
    }
}
