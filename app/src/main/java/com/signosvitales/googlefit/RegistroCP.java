package com.signosvitales.googlefit;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class RegistroCP extends WearableActivity implements SensorEventListener, ASRegistroCP.AsyncResponse, ASPeso.AsyncResponse {

    private TextView mTextViewPasos;
    private TextView mTextViewDistancia;
    private TextView mTextViewCalorias;
    private Button btnSincronizar;
    private Drawable imgStart;
    private SensorManager mSensorManager;
    private Sensor mPasosSensor;
    public int band;
    public String valorFinal;
    public String valorFinal2;
    public String valorFinal3;
    public String duracion;
    public Context v;
    public String usuario;
    public int idPaciente;
    public long steps = 0;
    public boolean isCounSensorPresent;
    int stepCount = 0;
    int var=0;
    public double peso;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_cp);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        Intent intent2 = getIntent();
        usuario= intent2.getStringExtra("id");
        idPaciente = Integer.parseInt(usuario);


        mTextViewPasos = (TextView) findViewById(R.id.pasosText);
        mTextViewDistancia = (TextView) findViewById(R.id.distanciaText);
        mTextViewCalorias = (TextView) findViewById(R.id.caloriasText);
        btnSincronizar = (Button) findViewById(R.id.btnSincronizar);
        btnSincronizar.setVisibility(View.GONE);


        btnSincronizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    long time = SystemClock.elapsedRealtime();
                    System.out.println(time);
                    long minutos=TimeUnit.MILLISECONDS.toMinutes(time);
                    long horas=minutos/60;
                    long dias=horas/24;
                    duracion=dias+" días";
                    valorFinal=mTextViewPasos.getText().toString();
                    valorFinal2=mTextViewDistancia.getText().toString();
                    valorFinal3=mTextViewCalorias.getText().toString();
                    registroCP();


            }
        });

        obtenerPeso();

        //mTextViewCalorias.setText("0.0");




        setAmbientEnabled();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        if(mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)!=null)
        {
            mPasosSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            isCounSensorPresent = true;
            startMeasure();
        }
        else
        {
            mTextViewPasos.setText("Contador de pasos no disponible");
            isCounSensorPresent = false;
        }


    }

    private void startMeasure() {
        boolean sensorRegistered = mSensorManager.registerListener(this, mPasosSensor, SensorManager.SENSOR_DELAY_FASTEST);
        Log.d("Sensor Status:", " Sensor registered: " + (sensorRegistered ? "yes" : "no"));
    }

    private void stopMeasure() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        double distancia;
        double calorias;

        if(event.sensor==mPasosSensor)
        {
            //var=var+1;
            btnSincronizar.setVisibility(View.VISIBLE);

            stepCount=(int)event.values[0];
            mTextViewPasos.setText(String.valueOf(stepCount));
            distancia=calcularDistancia(stepCount);
            calorias=calcularCalorias(peso, distancia);
            mTextViewDistancia.setText(String.valueOf(distancia));
            DecimalFormat df = new DecimalFormat("#.0");
            String var=df.format(calorias);
            mTextViewCalorias.setText(""+var);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        //Print all the sensors
//        if (mHeartRateSensor == null) {
//            List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);
//            for (Sensor sensor1 : sensors) {
//                Log.i("Sensor list", sensor1.getName() + ": " + sensor1.getType());
//            }
//        }
//    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void processFinish(String output) {

        try{

            //Toast.makeText(this, output , Toast.LENGTH_LONG).show();

            JSONObject responseObject;
            responseObject = new JSONObject(output);

            //System.out.println(responseObject);

            if (responseObject.getString("message").equals("Resultados obtenidos!")) {

                try{


                    JSONObject responseObject2;
                    JSONObject res;

                    //obtiene la respuesta en formato JSON

                    responseObject2 = new JSONObject(output);


                    // String cadena = responseObject.getString("datos");



                    if (responseObject2.getString("status").equals("true")) {

                        String cadena2 = responseObject2.getString("datos");
                        String cadenalimpia1= cadena2.replace("[", "");
                        String cadenalimpia2=cadenalimpia1.replace("]", "");
                        res = new JSONObject(cadenalimpia2);

                        peso = Double.parseDouble(res.getString("peso"));

                        //Toast.makeText(this, "El peso es de"+peso, Toast.LENGTH_LONG).show();




                    } else {

                        Toast.makeText(this, "Sin resultados", Toast.LENGTH_SHORT).show();


                    }


                }
                catch (Exception e)
                {
                    Toast.makeText(this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }



            }
            else
                {
                if (responseObject.getString("status").equals("true")) {
                    Toast.makeText(this, "Datos sincronizados correctamente", Toast.LENGTH_SHORT).show();
                    //Intent i = new Intent(RegistroActivity.this, LoginActivity.class);
                    //startActivity(i);
                } else {
                    Toast.makeText(this, "Fallo en sincronización de datos", Toast.LENGTH_SHORT).show();
                }
            }

        }catch (Exception e)
        {
            Toast.makeText(this, "Servidor no disponible", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void registroCP()
    {
        try
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                // Si hay conexión a Internet en este momento
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime = simpleDateFormat.format(new Date());


                String[] parts = currentDateandTime.split(" ");
                String fecha = parts[0]; // 123
                String hora = parts[1]; // 654321
                //String fecha = "2021-07-25"; // 123
                //String hora = "16:35:45"; // 654321
                //Toast.makeText(this, "Punto de control", Toast.LENGTH_SHORT).show();




                new ASRegistroCP(this,idPaciente, valorFinal, valorFinal2, valorFinal3, hora, fecha, duracion, this).execute();
                //Toast.makeText(this, "Si llega hasta aqui la depuracion", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "No hay conexion a Internet", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e)
        {

            Toast.makeText(this, "Servidor no disponible aqui", Toast.LENGTH_SHORT).show();
        }
    }

    public void obtenerPeso()
    {
        try
        {
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                // Si hay conexión a Internet en este momento
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateandTime = simpleDateFormat.format(new Date());


                String[] parts = currentDateandTime.split(" ");
                String fecha = parts[0]; // 123
                String hora = parts[1]; // 654321
                //String fecha = "2021-07-25"; // 123
                //String hora = "16:35:45"; // 654321
                //Toast.makeText(this, "Punto de control", Toast.LENGTH_SHORT).show();




                new ASPeso(this,idPaciente, valorFinal, hora, fecha,this).execute();
                //Toast.makeText(this, "Si llega hasta aqui la depuracion", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "No hay conexion a Internet", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e)
        {

            Toast.makeText(this, "Servidor no disponible aqui", Toast.LENGTH_SHORT).show();
        }

    }

    public double calcularDistancia(int pasos)
    {
        double res;

        res=pasos/2;

        return res;
    }

    public double calcularCalorias(double peso, double distancia)
    {
        //La distancia está en unidad de metros
        //CALORÍAS= DISTANCIA en kilómetros x PESO en kilogramos x FACTOR ANDAR (0,73)
        double res;
        double res2;
        double distanciaKM;
        distanciaKM=distancia/1000;

        res=distanciaKM*peso;
        res2=res*0.73;
        return res2;
    }


}
