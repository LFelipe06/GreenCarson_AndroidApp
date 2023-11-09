package com.example.greencarson;

import java.util.ArrayList;

public class CenterItem {
    private String nombre;
    private int latitud;
    private int longitud;
    private ArrayList<String> dias;
    private String direccion;
    private String horaCierre;
    private String horaApertura;
    private String numTelefono;

    public CenterItem() {

    }

    public CenterItem(String nombre, int latitud, int longitud, ArrayList<String> dias, String direccion, String horaCierre, String horaApertura, String numTelefono) {
        this.nombre = nombre;
        this.latitud = latitud;
        this.longitud = longitud;
        this.dias = dias;
        this.direccion = direccion;
        this.horaCierre = horaCierre;
        this.horaApertura = horaApertura;
        this.numTelefono = numTelefono;
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

    public String getDireccion() {
        return direccion;
    }

    public String getHoraCierre() {
        return horaCierre;
    }

    public String getHoraApertura() {
        return horaApertura;
    }

    public String getNumTelefono() {
        return numTelefono;
    }
}
