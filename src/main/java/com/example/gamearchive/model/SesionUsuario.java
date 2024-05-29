package com.example.gamearchive.model;

public class SesionUsuario {
    private static int usuario;
    private static String NombreUsuario;

    public static String getNombreUsuario() {
        return NombreUsuario;
    }

    public static void setNombreUsuario(String nombreUsuario) {
        SesionUsuario.NombreUsuario = nombreUsuario;
    }

    public static int getUsuario() {
        return usuario;
    }

    public static void setUsuario(int usuario) {
        SesionUsuario.usuario = usuario;
    }


}



