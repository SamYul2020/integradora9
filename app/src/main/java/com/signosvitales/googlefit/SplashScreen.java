package com.signosvitales.googlefit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreen extends AppCompatActivity {

    private static final long SPLASH_SCREEN_DELAY = 4000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        /*Se declara e inicializa la clase TimerTask, que permitirá lanzar una nueva Activity
                al finalizar el tiempo de espera, además de cerrar la Activity actual. */
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, Login.class);
                startActivity(intent);
                finish();
            }
        };
                /*Se declara e inicializa la clase Timer, que posibilita la programación de la tarea a lanzar.
                El objeto creado, invocará al método schedule(), que recibirá entre sus parámetros la tarea
                a realizar y el tiempo de espera hasta la ejecución de dicha tarea.*/
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }
}
