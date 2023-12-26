package com.example.seguimientoderutas;
import java.io.Serializable;
import java.util.List;

public class Ruta implements Serializable {
    private String nombre;
    private List<UbicacionPunto> puntos;
    private UbicacionPunto ubicacionInicial;
    private UbicacionPunto ubicacionFinal;

    // Constructor vacío requerido por Firebase
    public Ruta() {}

    // Constructor que incluye la ubicación inicial y final
    public Ruta(String nombre, List<UbicacionPunto> puntos, UbicacionPunto ubicacionInicial, UbicacionPunto ubicacionFinal) {
        this.nombre = nombre;
        this.puntos = puntos;
        this.ubicacionInicial = ubicacionInicial;
        this.ubicacionFinal = ubicacionFinal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public List<UbicacionPunto> getPuntos() {
        return puntos;
    }

    public void setPuntos(List<UbicacionPunto> puntos) {
        this.puntos = puntos;
    }

    public UbicacionPunto getUbicacionInicial() {
        return ubicacionInicial;
    }

    public void setUbicacionInicial(UbicacionPunto ubicacionInicial) {
        this.ubicacionInicial = ubicacionInicial;
    }

    public UbicacionPunto getUbicacionFinal() {
        return ubicacionFinal;
    }

    public void setUbicacionFinal(UbicacionPunto ubicacionFinal) {
        this.ubicacionFinal = ubicacionFinal;
    }

    @Override
    public String toString() {
        return "Ruta{" +
                "nombre='" + nombre + '\'' +
                ", ubicacionInicial=" + ubicacionInicial +
                ", ubicacionFinal=" + ubicacionFinal +
                '}';
    }
}
