package com.example.seguimientoderutas;
public class UbicacionPunto {
    private double latitud;
    private double longitud;

    // Constructor vacío requerido por Firebase
    public UbicacionPunto() {}

    public UbicacionPunto(double latitud, double longitud) {
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public double getLatitud() {
        return latitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    @Override
    public String toString() {
        return "UbicacionPunto: " +
                "latitud=" + latitud +
                ", longitud=" + longitud ;
    }
}
