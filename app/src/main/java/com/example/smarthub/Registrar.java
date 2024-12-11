package com.example.smarthub;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
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

import java.util.regex.Pattern;

public class Registrar extends AppCompatActivity {

    EditText nombre, apellido, fechaNacimiento, email, clave;
    Button btnRegistrar;

    private static final String PASSWORD_PATTERN =
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"; // Contraseña robusta

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_registrar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        nombre = findViewById(R.id.txtnombre);
        apellido = findViewById(R.id.txtapellido);
        fechaNacimiento = findViewById(R.id.txtfnacimiento);
        email = findViewById(R.id.txtemail);
        clave = findViewById(R.id.txtclave);
        btnRegistrar = findViewById(R.id.btn_registrarusuario);

        // Configurar el botón de registrar
        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateFields()) {
                    guardarUsuario(
                            nombre.getText().toString().trim(),
                            apellido.getText().toString().trim(),
                            fechaNacimiento.getText().toString().trim(),
                            email.getText().toString().trim(),
                            clave.getText().toString().trim()
                    );
                }
            }
        });
    }

    private boolean validateFields() {
        // Validar campos en blanco
        if (TextUtils.isEmpty(nombre.getText().toString().trim())) {
            nombre.setError("Este campo es obligatorio");
            return false;
        }
        if (TextUtils.isEmpty(apellido.getText().toString().trim())) {
            apellido.setError("Este campo es obligatorio");
            return false;
        }
        if (TextUtils.isEmpty(fechaNacimiento.getText().toString().trim())) {
            fechaNacimiento.setError("Este campo es obligatorio");
            return false;
        }
        if (TextUtils.isEmpty(email.getText().toString().trim())) {
            email.setError("Debe ingresar un correo electrónico");
            return false;
        }
        if (TextUtils.isEmpty(clave.getText().toString().trim())) {
            clave.setError("Debe ingresar una contraseña");
            return false;
        }

        // Validación de correo electrónico
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches()) {
            email.setError("Formato de correo inválido");
            return false;
        }

        // Validación de contraseña robusta
        if (!Pattern.matches(PASSWORD_PATTERN, clave.getText().toString().trim())) {
            clave.setError("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial");
            return false;
        }

        return true; // Todos los campos son válidos
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
                Toast.makeText(this, "Usuario registrado exitosamente", Toast.LENGTH_LONG).show();
                // Redirigir a otra actividad después del registro exitoso
                Intent intent = new Intent(this, Menu.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Error al registrar usuario", Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            db.close();
        }
    }
}
