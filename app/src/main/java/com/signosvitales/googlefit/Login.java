package com.signosvitales.googlefit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class Login extends AppCompatActivity implements ASLogin.AsyncResponse{

    private EditText usuario2;
    private EditText password2;
    private Button btnEntrar;
    public Context v;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        usuario2 = (EditText) findViewById(R.id.usuario2);
        password2 = (EditText) findViewById(R.id.password2);
        btnEntrar = (Button) findViewById(R.id.btnEntrar);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                validarUsuario();

            }
        });
    }

    public void validarUsuario()
    {
        try
        {

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                // Si hay conexión a Internet en este momento


                if(usuario2.getText().toString().equals("") || password2.getText().toString().equals("")) {
                    Toast.makeText(this, "Te faltan campos por rellenar", Toast.LENGTH_SHORT).show();
                }
                else
                {

                   new ASLogin(this,usuario2.getText().toString(),password2.getText().toString(),this).execute();

/////////////////////////////////////////////////////////////////////////////////////////

                }

            } else {
                Toast.makeText(this, "No hay conexion a Internet", Toast.LENGTH_SHORT).show();
            }

        }catch (Exception e)
        {
            Toast.makeText(this, "Servidor no disponible", Toast.LENGTH_SHORT).show();
            //e.printStackTrace();
        }


    }

    @Override
    public void processFinish(String output){

        try
        {
            //Here you will receive the result fired from async class
            //of onPostExecute(result) method.
            ///Toast.makeText(this, output, Toast.LENGTH_SHORT).show();

            JSONObject responseObject;
            JSONObject res;

            //obtiene la respuesta en formato JSON

            responseObject = new JSONObject(output);


            String cadena = responseObject.getString("datos");



            char[] cadena_div = cadena.toCharArray();
            String n = "";
            for (int i = 0; i < cadena.length(); i++) {
                if (Character.isDigit(cadena_div[i])) {
                    n += cadena_div[i];
                }
            }

            Bundle params = new Bundle();
            params.putString("id", n);

            if (responseObject.getString("status").equals("true")) {

                String cadena2 = responseObject.getString("datos");
                String cadenalimpia1= cadena2.replace("[", "");
                String cadenalimpia2=cadenalimpia1.replace("]", "");
                res = new JSONObject(cadenalimpia2);

                String nombre = res.getString("nombre");
                String apepaterno = res.getString("apepaterno");

                Intent i = new Intent(Login.this, ListaMediciones.class);
                i.putExtras(params);

                startActivity(i);
                Toast.makeText(this, "Bienvenido(a): "+nombre+" "+apepaterno, Toast.LENGTH_LONG).show();
                //Toast.makeText(this, "Acceso concedido", Toast.LENGTH_SHORT).show();

            } else {

                Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();


            }
        }catch (Exception e)
        {
            Toast.makeText(this, "Servidor no disponible ahora", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

}
