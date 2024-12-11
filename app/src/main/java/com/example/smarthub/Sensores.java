package com.example.smarthub;

import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Sensores extends AppCompatActivity {

    TextView fechahora,temp, hum;
    ImageView imagenAmpolleta;
    RequestQueue datos;
    Handler mHandler = new Handler();
    boolean isAmpolletaOn = false; // Variable para controlar el estado de la ampolleta

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sensores);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        fechahora = findViewById(R.id.txt_fecha);
        temp = findViewById(R.id.txt_temp);
        hum = findViewById(R.id.txt_humedad);
        imagenAmpolleta = findViewById(R.id.imagenAmpolletaOn);

        fechahora.setText(obtenerFechaHoraActual());
        datos = Volley.newRequestQueue(this);
        obtenerDatos();
        Refrescar.run();

        // Listener para cambiar el estado de la ampolleta al presionar la imagen
        imagenAmpolleta.setOnClickListener(v -> {
            isAmpolletaOn = !isAmpolletaOn; // Cambia el estado
            cambiarEstadoAmpolleta(); // Cambia la imagen según el nuevo estado
        });
    }

    // Método para obtener la fecha y hora actual
    public String obtenerFechaHoraActual() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy, hh:mm:ss a");
        return sdf.format(c.getTime());
    }

    // Método para obtener datos desde la API
    public void obtenerDatos() {
        String url = "https://www.pnk.cl/muestra_datos.php";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            temp.setText(response.getString("temperatura") + " °C");
                            hum.setText(response.getString("humedad") + " %");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        datos.add(request);
    }

    // Método para cambiar la imagen de la ampolleta según su estado actual
    public void cambiarEstadoAmpolleta() {
        if (isAmpolletaOn) {
            imagenAmpolleta.setImageResource(R.drawable.ampolleta_on);
        } else {
            imagenAmpolleta.setImageResource(R.drawable.ampolleta_off);
        }
    }

    // Runnable para refrescar los datos y la fecha/hora cada segundo
    public Runnable Refrescar = new Runnable() {
        @Override
        public void run() {
            fechahora.setText(obtenerFechaHoraActual());
            obtenerDatos();
            mHandler.postDelayed(this, 1000);
        }
    };
}
