package com.example.gamearchive;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MenuJuegoController implements Initializable {

    @FXML
    private Button Volver;
    @FXML
    private ImageView ImagenJuego;
    @FXML
    private Label NombreJuego;

    @FXML
    private Label Plataformas;

    @FXML
    private Label FechaDeLanzamiento;

    @FXML
    private Label Descripcion;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
    public void initData(String nombreJuego, String fechaLanzamiento, String rutaCaratula) {
        NombreJuego.setText(nombreJuego);
        FechaDeLanzamiento.setText(fechaLanzamiento);
        Image image = new Image(new File(rutaCaratula).toURI().toString());
        ImagenJuego.setImage(image);
    }

    @FXML
    private void handleVolverPantallaPrincipal(ActionEvent event) throws IOException {
        Stage ventana = (Stage) Volver.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("MenuInicial.fxml"));
        Scene scene = new Scene(root);
        ventana.setScene(scene);
        ventana.show();
    }

}
