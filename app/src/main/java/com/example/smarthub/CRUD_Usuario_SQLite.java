package com.example.smarthub;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarthub.db.DBconexion;

public class CRUD_Usuario_SQLite extends AppCompatActivity {

    EditText nombre, apellido, fechaNacimiento, email, clave;
    Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_crud_usuario_sqlite);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        nombre = findViewById(R.id.txtnombre1);
        apellido = findViewById(R.id.txtapellido);
        fechaNacimiento = findViewById(R.id.txtfnacimiento);
        email = findViewById(R.id.txtemail);
        clave = findViewById(R.id.txtclave);
        btnRegistrar = findViewById(R.id.btn_registrarusuario);

        // Configurar el botón de registrar
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                guardarUsuario(
                        nombre.getText().toString(),
                        apellido.getText().toString(),
                        fechaNacimiento.getText().toString(),
                        email.getText().toString(),
                        clave.getText().toString()
                );
            }
        });
    }

    public void guardarUsuario(String nom, String ape, String fnac, String mai, String cla) {
        DBconexion dbConexion = new DBconexion(this);
        SQLiteDatabase db = dbConexion.getWritableDatabase();
        try {
            ContentValues datos = new ContentValues();
            datos.put("nombre", nom);
            datos.put("apellido", ape);
            datos.put("fecha_nac", fnac);
            datos.put("email", mai);
            datos.put("clave", cla);

            long resultado = db.insert("usuarios", null, datos);

            if (resultado != -1) {
                Toast.makeText(this, "Datos Ingresados Correctamente", Toast.LENGTH_LONG).show();
                Intent listado = new Intent(this, Listado.class);
                startActivity(listado);
                finish();
            } else {
                Toast.makeText(this, "Error al insertar datos", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }
    }
}
