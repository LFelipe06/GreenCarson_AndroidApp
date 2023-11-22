package com.example.greencarson;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class CenterItem {
    private String id;
    private String nombre;
    private float latitud;
    private float longitud;
    private ArrayList<String> dias;
    private String categoria;
    private ArrayList<String> materiales;
    private String direccion;
    private String hora_cierre;
    private String hora_apertura;
    private String num_telefonico;

    private String imagen = "path/image";

    public CenterItem() {

    }

    public CenterItem(String nombre, int latitud, int longitud, ArrayList<String> dias, String direccion, String hora_cierre, String hora_apertura, String num_telefonico, String categoria, ArrayList<String> materiales, String imagen) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.dias = dias;
        this.direccion = direccion;
        this.hora_cierre = hora_cierre;
        this.hora_apertura = hora_apertura;
        this.num_telefonico = num_telefonico;
        this.materiales = materiales;
        this.categoria = categoria;
        this.imagen = imagen;
    }
    public void setId(String id){ this.id = id; }
    public String getId() { return id; }

    public String getNombre() {
        return nombre;
    }

    public float getLatitud() {
        return latitud;
    }

    public float getLongitud() {
        return longitud;
    }

    public ArrayList<String> getDias() {
        return dias;
    }

    public String getCategoria() {
        return categoria;
    }

    public ArrayList<String> getMateriales() {
        return materiales;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getHora_cierre() {
        return hora_cierre;
    }

    public String getHora_apertura() {
        return hora_apertura;
    }

    public String getNum_telefonico() {
        return num_telefonico;
    }

    public String getImagen() { return imagen;}

    @NonNull
    @Override
    public String toString() {
        return getNombre();
    }


}
