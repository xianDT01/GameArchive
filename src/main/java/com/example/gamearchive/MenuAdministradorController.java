package com.example.gamearchive;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuAdministradorController {


    @FXML
    private Button AñadirJuego;
    @FXML
    private void handleRegistarUsuarios(ActionEvent event) throws IOException {
        Stage ventana = (Stage) AñadirJuego.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("AñadirJuego.fxml"));
        Scene scene = new Scene(root);
        ventana.setScene(scene);
        ventana.show();
    }
}
