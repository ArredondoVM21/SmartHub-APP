package com.example.smarthub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Principal extends AppCompatActivity {

    Button btnRegistrarCrud, btnLista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar los botones
        btnRegistrarCrud = findViewById(R.id.btn_registrarcrud);
        btnLista = findViewById(R.id.btn_lista);

        // Listener para el botón de registro CRUD
        btnRegistrarCrud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent registroIntent = new Intent(Principal.this, CRUD_Usuario_SQLite.class);
                startActivity(registroIntent);
            }
        });

        // Listener para el botón de lista
        btnLista.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent listaIntent = new Intent(Principal.this, Listado.class);
                startActivity(listaIntent);
            }
        });
    }
}
