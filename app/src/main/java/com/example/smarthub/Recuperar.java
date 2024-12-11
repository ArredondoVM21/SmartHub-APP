package com.example.smarthub;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.smarthub.db.DBconexion;

public class Recuperar extends AppCompatActivity {

    EditText usu, txt1, txt2, txt3, txt4, txt5, txt6;
    Button btnEnviarCodigo;
    TextView temporizador;
    private static final long START_TIME_IN_MILLIS = 5 * 60 * 1000; // 5 minutos en milisegundos
    private CountDownTimer countDownTimer;
    private boolean timerRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar);

        btnEnviarCodigo = findViewById(R.id.btn_enviarcodigo);
        usu = findViewById(R.id.usuario1);
        temporizador = findViewById(R.id.txttemporizador);

        txt1 = findViewById(R.id.txt1);
        txt2 = findViewById(R.id.txt2);
        txt3 = findViewById(R.id.txt3);
        txt4 = findViewById(R.id.txt4);
        txt5 = findViewById(R.id.txt5);
        txt6 = findViewById(R.id.txt6);

        btnEnviarCodigo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validateEmail()) {
                    String codigoRecuperacion = generarCodigoRecuperacion();
                    almacenarCodigoEnBD(usu.getText().toString(), codigoRecuperacion);
                    startTimer();
                    Toast.makeText(Recuperar.this, "Código enviado: " + codigoRecuperacion, Toast.LENGTH_SHORT).show();
                }
            }
        });

        setUpEditTextListeners();
    }

    private String generarCodigoRecuperacion() {
        int codigo = (int) (Math.random() * 900000) + 100000; // Código de 6 dígitos
        return String.valueOf(codigo);
    }

    private void almacenarCodigoEnBD(String email, String codigo) {
        DBconexion dbConexion = new DBconexion(this);
        SQLiteDatabase db = dbConexion.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("codigo_recuperacion", codigo);
        db.update("usuarios", values, "email=?", new String[]{email});
        db.close();
    }

    private boolean validateEmail() {
        String email = usu.getText().toString().trim();
        if (email.isEmpty()) {
            usu.setError("Debe ingresar su correo");
            return false;
        }
        return true;
    }

    private void validateCode() {
        String codigoIngresado = txt1.getText().toString() + txt2.getText().toString() + txt3.getText().toString() +
                txt4.getText().toString() + txt5.getText().toString() + txt6.getText().toString();

        if (verificarCodigoEnBD(usu.getText().toString(), codigoIngresado)) {
            Intent intent = new Intent(Recuperar.this, CrearPassword.class);
            intent.putExtra("email", usu.getText().toString());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Código inválido", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean verificarCodigoEnBD(String email, String codigo) {
        DBconexion dbConexion = new DBconexion(this);
        SQLiteDatabase db = dbConexion.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT codigo_recuperacion FROM usuarios WHERE email=?", new String[]{email});
        boolean isValid = false;

        if (cursor.moveToFirst()) {
            int indexCodigo = cursor.getColumnIndex("codigo_recuperacion");
            if (indexCodigo != -1) {
                String codigoBD = cursor.getString(indexCodigo);
                isValid = codigo.equals(codigoBD);
            } else {
                Toast.makeText(this, "Error: columna 'codigo_recuperacion' no encontrada", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show();
        }
        cursor.close();
        db.close();
        return isValid;
    }

    private void startTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(START_TIME_IN_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                temporizador.setText(String.format("%02d:%02d", minutes, seconds));
            }

            @Override
            public void onFinish() {
                temporizador.setText("00:00");
                timerRunning = false;
                disableCodeFields();
            }
        }.start();

        timerRunning = true;
    }

    private void disableCodeFields() {
        txt1.setEnabled(false);
        txt2.setEnabled(false);
        txt3.setEnabled(false);
        txt4.setEnabled(false);
        txt5.setEnabled(false);
        txt6.setEnabled(false);
    }

    private void setUpEditTextListeners() {
        txt1.addTextChangedListener(new CodeTextWatcher(txt1, txt2));
        txt2.addTextChangedListener(new CodeTextWatcher(txt2, txt3));
        txt3.addTextChangedListener(new CodeTextWatcher(txt3, txt4));
        txt4.addTextChangedListener(new CodeTextWatcher(txt4, txt5));
        txt5.addTextChangedListener(new CodeTextWatcher(txt5, txt6));
        txt6.addTextChangedListener(new CodeTextWatcher(txt6, null) {
            @Override
            public void afterTextChanged(android.text.Editable editable) {
                super.afterTextChanged(editable);
                if (editable.length() == 1) {
                    validateCode();
                }
            }
        });
    }

    private class CodeTextWatcher implements android.text.TextWatcher {
        private EditText currentEditText;
        private EditText nextEditText;

        CodeTextWatcher(EditText currentEditText, EditText nextEditText) {
            this.currentEditText = currentEditText;
            this.nextEditText = nextEditText;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s.length() == 1 && nextEditText != null) {
                nextEditText.requestFocus();
            }
        }

        @Override
        public void afterTextChanged(android.text.Editable s) {}
    }
}
