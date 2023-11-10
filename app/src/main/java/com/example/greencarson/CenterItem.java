package com.example.greencarson;

import java.util.ArrayList;

public class CenterItem {
    private String nombre;
    private int latitud;
    private int longitud;
    private ArrayList<String> dias;
    private ArrayList<String> categorias;
    private ArrayList<String> materiales;
    private String direccion;
    private String hora_cierre;
    private String hora_apertura;
    private String num_telefonico;

    public CenterItem() {

    }

    public CenterItem(String nombre, int latitud, int longitud, ArrayList<String> dias, String direccion, String hora_cierre, String hora_apertura, String num_telefonico, ArrayList<String> categorias, ArrayList<String> materiales) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.dias = dias;
        this.direccion = direccion;
        this.hora_cierre = hora_cierre;
        this.hora_apertura = hora_apertura;
        this.num_telefonico = num_telefonico;
        this.materiales = materiales;
        this.categorias = categorias;
    }

    public String getNombre() {
        return nombre;
    }

    public int getLatitud() {
        return latitud;
    }

    public int getLongitud() {
        return longitud;
    }

    public ArrayList<String> getDias() {
        return dias;
    }

    public ArrayList<String> getCategorias() {
        return categorias;
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

}
