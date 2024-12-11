package com.example.smarthub;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarthub.db.DBconexion;

public class ModificarEliminar extends AppCompatActivity {

    EditText id, nombre, apellido;
    Button btnModificar, btnEliminar;
    int idUsuario;
    String nombreUsuario, apellidoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_modificar_eliminar);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        id = findViewById(R.id.txtid);
        nombre = findViewById(R.id.txtnombremod);
        apellido = findViewById(R.id.txtapellidomod);
        btnModificar = findViewById(R.id.btn_modificar);
        btnEliminar = findViewById(R.id.btn_eliminar);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            idUsuario = extras.getInt("Id");
            nombreUsuario = extras.getString("Nombre");
            apellidoUsuario = extras.getString("Apellido");
        }

        id.setText(String.valueOf(idUsuario));
        nombre.setText(nombreUsuario);
        apellido.setText(apellidoUsuario);

        btnModificar.setOnClickListener(view -> {
            modificarUsuario(idUsuario, nombre.getText().toString(), apellido.getText().toString());
            onBackPressed();
        });

        btnEliminar.setOnClickListener(view -> {
            eliminarUsuario(idUsuario);
            onBackPressed();
        });
    }

    private void modificarUsuario(int id, String nuevoNombre, String nuevoApellido) {
        DBconexion dbConexion = new DBconexion(this);
        SQLiteDatabase db = dbConexion.getWritableDatabase();

        if (db != null) {
            String sql = "UPDATE usuarios SET nombre=?, apellido=? WHERE _id=?";
            db.execSQL(sql, new String[]{nuevoNombre, nuevoApellido, String.valueOf(id)});
            db.close();
            Toast.makeText(this, "Usuario modificado correctamente", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error al modificar el usuario", Toast.LENGTH_SHORT).show();
        }
    }

    private void eliminarUsuario(int id) {
        DBconexion dbConexion = new DBconexion(this);
        SQLiteDatabase db = dbConexion.getWritableDatabase();

        if (db != null) {
            int rowsDeleted = db.delete("usuarios", "_id=?", new String[]{String.valueOf(id)});
            db.close();

            if (rowsDeleted > 0) {
                Toast.makeText(this, "Usuario eliminado correctamente. Filas afectadas: " + rowsDeleted, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al eliminar el usuario", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Error al abrir la base de datos", Toast.LENGTH_SHORT).show();
        }
    }
}
