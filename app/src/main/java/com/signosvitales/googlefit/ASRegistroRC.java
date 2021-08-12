package com.signosvitales.googlefit;

import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.common.api.Response;
import com.google.android.gms.common.util.IOUtils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ASRegistroRC extends AsyncTask<String, Void, String> {

    public Context context;
    ProgressDialog progreso;
    int id;
    String valorFinal;
    String fecha;
    String hora;

    public ASRegistroRC.AsyncResponse delegate = null;



    public interface AsyncResponse {
        void processFinish(String output);
    }

    public ASRegistroRC(Context cxt, int id, String valorFinal, String hora, String fecha ,AsyncResponse delegate) {
        this.context = cxt;
        progreso = new ProgressDialog(context);
        this.delegate = delegate;
        this.id=id;
        this.valorFinal=valorFinal;
        this.fecha=fecha;
        this.hora=hora;
    }

    @Override
    protected void onPreExecute() {

        progreso.setMessage("Espere un momento...");
        progreso.show();

    }

    @Override
    protected String doInBackground(String... strings) {

        try{

            /*
            String respStr;
            HttpClient httpClient;
            HttpGet del;
            HttpResponse resp;
            JSONObject responseObject;
            httpClient = new DefaultHttpClient();
            //cargamos la Url en la cual esta almacenado nuestro servicio
            del = new HttpGet("https://tecnoyuul.000webhostapp.com/ws%20(9)/user/registrarRitmoCardiaco.php?idpaciente="+id+"&valorRC="+valorFinal+"&hora="+hora+"&fecha="+fecha);

            del.setHeader("content-type", "application/json");
            resp = httpClient.execute(del);
            respStr = EntityUtils.toString(resp.getEntity());

            */

            ///////////////////////

            HttpURLConnection urlConnection=null;
            StringBuilder result = new StringBuilder();

            try {
                URL url = new URL("http://smart36.xyz/ws%20(9)/user/registrarMedicion.php?idmedicion=1&idpaciente="+id+"&valorRC="+valorFinal+"&hora="+hora+"&fecha="+fecha);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

            }catch( Exception e) {
                e.printStackTrace();
            }
            finally {
                urlConnection.disconnect();
            }


            return result.toString();


        }
        catch (Exception e)
        {
            Toast.makeText(context, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    protected void onPostExecute(String result) {
        progreso.dismiss();
        delegate.processFinish(result);

    }
}

