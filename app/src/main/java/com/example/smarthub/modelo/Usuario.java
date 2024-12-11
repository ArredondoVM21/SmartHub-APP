package com.example.smarthub.modelo;

public class Usuario {
     String _id;
     String nombre;
     String apellido;

    public Usuario(String _id, String nombre, String apellido) {
        this._id = _id;
        this.nombre = nombre;
        this.apellido = apellido;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
}
