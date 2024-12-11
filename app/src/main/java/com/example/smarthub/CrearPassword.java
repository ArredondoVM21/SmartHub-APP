package com.example.smarthub;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthub.db.DBconexion;

import java.util.regex.Pattern;

public class CrearPassword extends AppCompatActivity {

    EditText txtclav1, txtclav2;
    Button btn_guardar;
    private static final String PASSWORD_PATTERN = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_password);

        txtclav1 = findViewById(R.id.txtclav1);
        txtclav2 = findViewById(R.id.txtclav2);
        btn_guardar = findViewById(R.id.btn_guardar);

        btn_guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateAndSavePassword();
            }
        });
    }

    private void validateAndSavePassword() {
        String password = txtclav1.getText().toString().trim();
        String confirmPassword = txtclav2.getText().toString().trim();

        if (TextUtils.isEmpty(password)) {
            txtclav1.setError("Debe ingresar una contraseña");
            return;
        }

        if (!isValidPassword(password)) {
            txtclav1.setError("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial");
            return;
        }

        if (!password.equals(confirmPassword)) {
            txtclav2.setError("Las contraseñas no coinciden");
            return;
        }

        if (actualizarClaveEnBD(password)) {
            Toast.makeText(CrearPassword.this, "Contraseña creada correctamente", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CrearPassword.this, Menu.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isValidPassword(String password) {
        Pattern pattern = Pattern.compile(PASSWORD_PATTERN);
        return pattern.matcher(password).matches();
    }

    private boolean actualizarClaveEnBD(String nuevaClave) {
        DBconexion dbConexion = new DBconexion(this);
        SQLiteDatabase db = dbConexion.getWritableDatabase();
        String email = getIntent().getStringExtra("email");

        ContentValues values = new ContentValues();
        values.put("clave", nuevaClave);

        int rowsAffected = db.update("usuarios", values, "email=?", new String[]{email});
        db.close();

        return rowsAffected > 0;
    }
}
