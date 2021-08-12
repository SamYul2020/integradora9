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
import android.support.wearable.activity.WearableActivity;
import android.support.wearable.view.WatchViewStub;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewStub;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RegistroRC extends WearableActivity implements SensorEventListener, ASRegistroRC.AsyncResponse {

    private TextView mTextView;
    private ImageButton btnStart;
    private ImageButton btnPause;
    private Drawable imgStart;
    private SensorManager mSensorManager;
    private Sensor mHeartRateSensor;
    public int band;
    public String valorFinal;
    public Context v;
    public String usuario;
    public int idPaciente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent2 = getIntent();
        usuario= intent2.getStringExtra("id");
        idPaciente = Integer.parseInt(usuario);

                mTextView = (TextView) findViewById(R.id.heartRateText);
                btnStart = (ImageButton) findViewById(R.id.btnStart);
                btnPause = (ImageButton) findViewById(R.id.btnPause);


                btnStart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnStart.setVisibility(ImageButton.GONE);
                        btnPause.setVisibility(ImageButton.VISIBLE);
                        mTextView.setText("Por favor espere...");
                        startMeasure();
                    }
                });

                btnPause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        btnPause.setVisibility(ImageButton.GONE);
                        btnStart.setVisibility(ImageButton.VISIBLE);
                        mTextView.setText("--");
                        stopMeasure();
                    }
                });

                mTextView.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                        band=band+1;

                        //Context context = getApplicationContext();
                        //CharSequence text = "El texto se ha actualizado"+band;
                        //int duration = Toast.LENGTH_SHORT;
                        //Toast toast = Toast.makeText(context, text, duration);
                        //toast.show();

                        if(band==4)
                        {
                            valorFinal=mTextView.getText().toString();
                            stopMeasure();

                            mTextView.setText("Tu ritmo cardiaco es de: "+valorFinal);
                            btnPause.setVisibility(ImageButton.GONE);
                            btnStart.setVisibility(ImageButton.VISIBLE);

                            registroRC();
                        }



                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });




        setAmbientEnabled();

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mHeartRateSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);

    }

    private void startMeasure() {
        boolean sensorRegistered = mSensorManager.registerListener(this, mHeartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);
        Log.d("Sensor Status:", " Sensor registered: " + (sensorRegistered ? "yes" : "no"));
    }

    private void stopMeasure() {
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float mHeartRateFloat = event.values[0];

        int mHeartRate = Math.round(mHeartRateFloat);

        mTextView.setText(Integer.toString(mHeartRate));
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


            if (responseObject.getString("status").equals("true")) {
                Toast.makeText(this, "Datos sincronizados correctamente", Toast.LENGTH_SHORT).show();
                //Intent i = new Intent(RegistroActivity.this, LoginActivity.class);
                //startActivity(i);
            } else {
                Toast.makeText(this, "Fallo en sincronización de datos", Toast.LENGTH_SHORT).show();
            }


        }catch (Exception e)
        {
            Toast.makeText(this, "Servidor no disponible", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void registroRC()
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




                new ASRegistroRC(this,idPaciente, valorFinal, hora, fecha,this).execute();
                //Toast.makeText(this, "Si llega hasta aqui la depuracion", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "No hay conexion a Internet", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e)
        {

            Toast.makeText(this, "Servidor no disponible aqui", Toast.LENGTH_SHORT).show();
        }
    }


}
