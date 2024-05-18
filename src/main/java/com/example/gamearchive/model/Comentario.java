package com.example.gamearchive.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Comentario {

    private final StringProperty usuario;
    private final StringProperty comentario;
    private final int idComentario;

    public Comentario(int idComentario, String usuario, String comentario) {
        this.idComentario = idComentario;
        this.usuario = new SimpleStringProperty(usuario);
        this.comentario = new SimpleStringProperty(comentario);
    }

    public int getIdComentario() {
        return idComentario;
    }

    public String getUsuario() {
        return usuario.get();
    }

    public StringProperty usuarioProperty() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario.set(usuario);
    }

    public String getComentario() {
        return comentario.get();
    }

    public StringProperty comentarioProperty() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario.set(comentario);
    }
}
