package com.example.gamearchive;

public class JuegoInfo {
    private String nombreJuego;
    private String fechaLanzamiento;
    private String descripcion;
    private String rutaCaratula;

    // Constructor
    public JuegoInfo(String nombreJuego, String fechaLanzamiento, String descripcion, String rutaCaratula) {
        this.nombreJuego = nombreJuego;
        this.fechaLanzamiento = fechaLanzamiento;
        this.descripcion = descripcion;
        this.rutaCaratula = rutaCaratula;
    }

    // Método getter para obtener el nombre del juego
    public String getNombreJuego() {
        return nombreJuego;
    }

    // Otros métodos getter para obtener otros atributos
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

