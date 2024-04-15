package com.example.gamearchive.model;

public class JuegoInfo {
    private String nombreJuego;
    private String fechaLanzamiento;
    private String descripcion;
    private String rutaCaratula;

    public JuegoInfo(String nombreJuego, String fechaLanzamiento, String descripcion, String rutaCaratula) {
        this.nombreJuego = nombreJuego;
        this.fechaLanzamiento = fechaLanzamiento;
        this.descripcion = descripcion;
        this.rutaCaratula = rutaCaratula;
    }

    public String getNombreJuego() {
        return nombreJuego;
    }

    public String getFechaLanzamiento() {
        return fechaLanzamiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getRutaCaratula() {
        return rutaCaratula;
    }
}

