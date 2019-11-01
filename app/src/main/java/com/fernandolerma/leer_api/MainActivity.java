package com.fernandolerma.leer_api;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
public class MainActivity extends AppCompatActivity {

    TextView txtGson;
    ProgressDialog pd;

    String enlace= "http://157.245.118.222:8000/api/api-token-auth/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtGson = findViewById(R.id.txtGSON);

        JSONObject postData = new JSONObject();
        try {
            postData.put("email", "menona@gmail.com");
            postData.put("password", "menona123");
            //txtGson.setText(enlace + postData.toString());
            //sendPostRequest(enlace, postData.toString());
            new EnviarJSON().execute(enlace, postData.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //new JsonTask().execute("http://157.245.118.222:8000/api/api-token-auth/");

    }

    private class JsonTask extends AsyncTask<String, String, String> {

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(MainActivity.this);
            pd.setMessage("Cargando datos");
            pd.setCancelable(false);
            pd.show();
        }

        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream, "UTF-8"));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                return buffer.toString();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (pd.isShowing()) {
                pd.dismiss();
            }
            obtenerArray_JSON(result);
            txtGson.setMovementMethod(new ScrollingMovementMethod());
        }
    }

    public void obtenerArray_JSON(String JSON) {
        try {
            JSONArray jsonArray= new JSONArray(JSON);
            for (int indice_objeto = 0; indice_objeto < jsonArray.length(); indice_objeto++) {
                JSONObject datos = jsonArray.getJSONObject(indice_objeto);
                for (int indice_nombre = 0; indice_nombre < datos.length(); indice_nombre++) {
                    String id= datos.names().get(indice_nombre).toString();
                    txtGson.append(id + ": " + datos.get(id) + "\n");
                }
                txtGson.append("\n");
            }
        } catch (JSONException e) {
            Toast.makeText(MainActivity.this, "ERROR\n" + e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private class EnviarJSON extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Accept", "application/json");
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream(), "UTF-8");
                writer.write(params[1]);
                writer.close();
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuffer jsonString = new StringBuffer();
                String line;
                while ((line = br.readLine()) != null) {
                    jsonString.append(line);
                }
                br.close();
                connection.disconnect();
                return jsonString.toString();
            } catch (Exception e) {
                //throw new RuntimeException(e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            txtGson.append(result + "\n"); // this is expecting a response code to be sent from your server upon receiving the POST data
        }
    }
}
