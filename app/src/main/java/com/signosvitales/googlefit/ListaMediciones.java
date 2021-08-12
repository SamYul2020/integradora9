package com.signosvitales.googlefit;

import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class ListaMediciones extends AppCompatActivity {

    private ListView listMediciones;
    private ArrayList<String> names;
    private String usuario;


    ListViewAdapter adapter;

    String[] titulo = new String[]{
            "Calcular ritmo cardíaco",
            "Nivel de oxígeno",
            "Nivel de temperatura",
            "Podómetro"

    };

    int[] imagenes = {
            R.drawable.corazon,
            R.drawable.oxigeno,
            R.drawable.temperatura,
            R.drawable.pasos

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista_mediciones);

        Intent intent2 = getIntent();
        usuario= intent2.getStringExtra("id");

        final ListView lista = (ListView) findViewById(R.id.lista);
        adapter = new ListViewAdapter(this, titulo, imagenes);
        lista.setAdapter(adapter);




        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView adapterView, View view, int i, long l) {
                //Toast.makeText(getApplicationContext(), "presiono " + i, Toast.LENGTH_SHORT).show();
                if(i==0)
                {
                    Bundle params = new Bundle();
                    params.putString("id", usuario);

                    //Toast.makeText(getApplicationContext(), "El usuario es; " + usuario, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ListaMediciones.this, RegistroRC.class);
                    intent.putExtras(params);
                    startActivity(intent);
                }
                else if (i==1)
                {
                    Bundle params = new Bundle();
                    params.putString("id", usuario);

                    //Toast.makeText(getApplicationContext(), "El usuario es; " + usuario, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ListaMediciones.this, RegistroO.class);
                    intent.putExtras(params);
                    startActivity(intent);
                }
                else if (i==2)
                {
                    Bundle params = new Bundle();
                    params.putString("id", usuario);

                    //Toast.makeText(getApplicationContext(), "El usuario es; " + usuario, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ListaMediciones.this, RegistroT.class);
                    intent.putExtras(params);
                    startActivity(intent);
                }
                else if (i==3)
                {
                    Bundle params = new Bundle();
                    params.putString("id", usuario);

                    //Toast.makeText(getApplicationContext(), "El usuario es; " + usuario, Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ListaMediciones.this, RegistroCP.class);
                    intent.putExtras(params);
                    startActivity(intent);

                }
                else if (i==4)
                {

                }
                else if (i==5)
                {

                }
            }
        });





    }


}
