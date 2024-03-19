package com.example.gamearchive;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class InicioApp extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(InicioApp.class.getResource("MenuPrincipal.fxml"));
        Parent root = fxmlLoader.load();
        // Crear la escena con las dimensiones de la pantalla
        Scene scene = new Scene(root);
        // Configurar el título de la ventana
        stage.setTitle("GameArchive");
        // Establecer el fondo de la escena como transparente
        scene.setFill(Color.TRANSPARENT);
        // Configurar el estilo del escenario como transparente
        stage.initStyle(StageStyle.TRANSPARENT);
        // Establecer la escena en el escenario
        stage.setScene(scene);
        // Centrar la ventana en la pantalla
        stage.centerOnScreen();
        // Mostrar la ventana
        stage.show();
    }


    /*
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(InicioApp.class.getResource("MenuPrincipal.fxml"));
        Parent root = fxmlLoader.load();
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
        stage.setTitle("GameArchive");
        stage.setScene(scene);
        stage.show();
    }

     */

    public static void main(String[] args) {
        launch();
    }
}