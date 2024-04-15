package com.example.gamearchive.model;

import com.example.gamearchive.model.Comentario;

public class ComentariosForo extends Comentario {

    private int idComentario;
    private String textoComentario;

    public ComentariosForo(int idComentario, String usuario, String comentario) {
        super(idComentario, usuario, comentario);
        this.idComentario = idComentario;
        this.textoComentario = textoComentario;
    }

    public int getIdComentario() {
        return idComentario;
    }

    public void setIdComentario(int idComentario) {
        this.idComentario = idComentario;
    }

    public String getTextoComentario() {
        return textoComentario;
    }

    public void setTextoComentario(String textoComentario) {
        this.textoComentario = textoComentario;
    }
}
