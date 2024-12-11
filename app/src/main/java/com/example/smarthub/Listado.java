package com.example.smarthub;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarthub.db.DBconexion;

import java.util.ArrayList;

public class Listado extends AppCompatActivity {

    ListView listado;
    EditText txtBuscaUsuario;
    ArrayList<String> listaUsuarios;

    @Override
    protected void onPostResume() {
        super.onPostResume();
        cargarLista();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_listado);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        txtBuscaUsuario = findViewById(R.id.txtbuscausuario);
        listado = findViewById(R.id.lista);

        // Cargar la lista completa de usuarios al inicio
        cargarLista();

        // Añadir el TextWatcher para la búsqueda en tiempo real
        txtBuscaUsuario.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filtrarUsuarios(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Configurar evento al hacer clic en un elemento de la lista
        listado.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String[] datosUsuario = listaUsuarios.get(i).split(" ");
                int idUsuario = Integer.parseInt(datosUsuario[0]);
                String nombre = datosUsuario[1];
                String apellido = datosUsuario[2];

                Intent intent = new Intent(Listado.this, ModificarEliminar.class);
                intent.putExtra("Id", idUsuario);
                intent.putExtra("Nombre", nombre);
                intent.putExtra("Apellido", apellido);
                startActivity(intent);
            }
        });
    }

    // Método para obtener la lista de usuarios de la base de datos
    private ArrayList<String> obtenerListaUsuarios() {
        ArrayList<String> datos = new ArrayList<>();
        DBconexion helper = new DBconexion(this); // Utiliza la clase DBconexion de tu base de datos
        SQLiteDatabase db = helper.getReadableDatabase();

        String sql = "SELECT * FROM usuarios";
        Cursor cursor = db.rawQuery(sql, null);

        if (cursor.moveToFirst()) {
            do {
                String linea = cursor.getInt(0) + " " + cursor.getString(1) + " " + cursor.getString(2);
                datos.add(linea);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return datos;
    }

    // Método para cargar la lista completa de usuarios
    private void cargarLista() {
        listaUsuarios = obtenerListaUsuarios();
        filtrarUsuarios(""); // Mostrar todos los usuarios al inicio
    }

    // Método para filtrar la lista en función de la entrada del usuario
    private void filtrarUsuarios(String query) {
        ArrayList<String> listaFiltrada = new ArrayList<>();
        String queryLowerCase = query.toLowerCase();

        for (String usuario : listaUsuarios) {
            String[] partes = usuario.split(" ");
            String nombre = partes[1].toLowerCase();
            String apellido = partes[2].toLowerCase();

            // Verificar si el nombre o apellido contiene el texto ingresado en cualquier posición
            if (nombre.contains(queryLowerCase) || apellido.contains(queryLowerCase)) {
                listaFiltrada.add(usuario);
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaFiltrada);
        listado.setAdapter(adapter);
    }
}
