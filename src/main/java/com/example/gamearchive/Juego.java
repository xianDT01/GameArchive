package com.example.gamearchive;

public class Juego {


        private String nombre;
        private String descripcion;
        private String fechaLanzamiento;
        private String rutaCaratula;

    public Juego(String nombre, String descripcion, String fechaLanzamiento, String rutaCaratula) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.fechaLanzamiento = fechaLanzamiento;
        this.rutaCaratula = rutaCaratula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public void setFechaLanzamiento(String fechaLanzamiento) {
        this.fechaLanzamiento = fechaLanzamiento;
    }

    public String getRutaCaratula() {
        return rutaCaratula;
    }

    public void setRutaCaratula(String rutaCaratula) {
        this.rutaCaratula = rutaCaratula;
    }
}
