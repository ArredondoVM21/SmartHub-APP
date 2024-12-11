package com.example.smarthub.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBmanager {
    // Tabla Usuario
    public static final String USUARIO_ID = "_id";
    public static final String USUARIO_NOMBRE = "nombre";
    public static final String USUARIO_APELLIDO = "apellido";
    public static final String USUARIO_FECHA_NACIMIENTO = "fecha_nac";
    public static final String USUARIO_EMAIL = "email";
    public static final String USUARIO_CLAVE = "clave";

    // Sentencia SQL para la creación de la tabla
    public static final String TABLA_USUARIOS_CREATE = "CREATE TABLE usuarios (" +
            USUARIO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            USUARIO_NOMBRE + " TEXT NOT NULL, " +
            USUARIO_APELLIDO + " TEXT NOT NULL, " +
            USUARIO_FECHA_NACIMIENTO + " TEXT, " +
            USUARIO_EMAIL + " TEXT UNIQUE NOT NULL, " +
            USUARIO_CLAVE + " TEXT NOT NULL" +
            ");";

    private DBconexion _conexion;
    private SQLiteDatabase _basededatos;

    public DBmanager(Context context) {
        _conexion = new DBconexion(context);
    }

    public DBmanager open() throws SQLException {
        _basededatos = _conexion.getWritableDatabase(); // Cambiado a getWritableDatabase
        return this;
    }

    public void close() {
        _conexion.close();
    }

    public void insertarUsuario(String nombre, String apellido, String fechaNacimiento, String email, String clave) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(USUARIO_NOMBRE, nombre);
        contentValues.put(USUARIO_APELLIDO, apellido);
        contentValues.put(USUARIO_FECHA_NACIMIENTO, fechaNacimiento);
        contentValues.put(USUARIO_EMAIL, email);
        contentValues.put(USUARIO_CLAVE, clave);
        long resultado = this._basededatos.insert("usuarios", null, contentValues);
        if (resultado == -1) {
            Log.d("insercion", "Incorrecta");
        } else {
            Log.d("insercion", "Correcta");
        }
    }

    public boolean registrarUsuario(String nombre, String apellido, String fechaNacimiento, String email, String clave) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(USUARIO_NOMBRE, nombre);  // Cambiado para usar el valor directamente
        contentValues.put(USUARIO_APELLIDO, apellido);
        contentValues.put(USUARIO_FECHA_NACIMIENTO, fechaNacimiento);
        contentValues.put(USUARIO_EMAIL, email);
        contentValues.put(USUARIO_CLAVE, clave);
        long resultado = _basededatos.insert("usuarios", null, contentValues);
        if (resultado == -1) {
            Log.d("insercion", "Incorrecta");
            return false;
        } else {
            Log.d("insercion", "Correcta");
            return true;
        }
    }
}
