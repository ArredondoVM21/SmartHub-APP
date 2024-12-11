package com.example.smarthub;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarthub.db.DBconexion;

import java.util.regex.Pattern;

public class Login extends AppCompatActivity {

    EditText usu, cla;
    Button btning, btnreg;
    TextView recupera;
    private static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        usu = findViewById(R.id.usuario);
        cla = findViewById(R.id.clave);
        btning = findViewById(R.id.btn_ingresar);
        btnreg = findViewById(R.id.btn_registro);
        recupera = findViewById(R.id.recuperar);

        btning.setOnClickListener(view -> validateAndLogin());

        btnreg.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Registrar.class);
            startActivity(intent);
        });

        recupera.setOnClickListener(view -> {
            Intent intent = new Intent(Login.this, Recuperar.class);
            startActivity(intent);
        });
    }

    private void validateAndLogin() {
        String email = usu.getText().toString().trim();
        String password = cla.getText().toString().trim();

        if (email.isEmpty()) {
            usu.setError("Debe ingresar su correo");
            return;
        }
        if (!Pattern.matches(EMAIL_PATTERN, email)) {
            usu.setError("El formato del correo es incorrecto");
            return;
        }

        if (password.isEmpty()) {
            cla.setError("Debe ingresar su contraseña");
            return;
        }

        if (checkCredentials(email, password)) {
            Intent intent = new Intent(Login.this, Menu.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkCredentials(String email, String password) {
        DBconexion dbConexion = new DBconexion(this);
        SQLiteDatabase db = dbConexion.getReadableDatabase();
        Cursor cursor = null;
        boolean loginSuccessful = false;

        try {
            // Debug: Listar todos los usuarios antes de verificar credenciales
            cursor = db.rawQuery("SELECT * FROM usuarios", null);
            while (cursor.moveToNext()) {
                String userEmail = cursor.getString(cursor.getColumnIndex("email"));
                Log.d("DEBUG_LOGIN", "Usuario en la base de datos: " + userEmail);
            }
            cursor.close();

            // Verificar si el usuario existe con email y clave proporcionados
            String query = "SELECT * FROM usuarios WHERE email=?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor.moveToFirst()) {
                int passwordIndex = cursor.getColumnIndex("clave");
                if (passwordIndex >= 0) {
                    String storedPassword = cursor.getString(passwordIndex);
                    if (storedPassword.equals(password)) {
                        loginSuccessful = true;
                    } else {
                        Toast.makeText(this, "Contraseña incorrecta", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(this, "Error: columna 'clave' no encontrada", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return loginSuccessful;
    }
}
