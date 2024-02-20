package com.example.gamearchive;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MenuInicialController {

    @FXML
    private Button Volver;
    @FXML
    private ImageView Agregadosrecientemente1;
    @FXML
    private ImageView Agregadosrecientemente2;
    @FXML
    private ImageView Agregadosrecientemente3;
    @FXML
    private ImageView Ramdom1;
    @FXML
    private ImageView Ramdom2;
    @FXML
    private ImageView Ramdom3;
    @FXML
    private ImageView publicidad1;
    @FXML
    private ImageView publicidad2;
    @FXML
    private TextField TextFieldBuscar;
    @FXML
    private Button ButtonBuscar;
    @FXML
    private Label Indice;


    String rutaImagen ="C:\\Users\\Xian\\IdeaProjects\\GameArchive\\src\\main\\resources\\caratulas\\dragonballcaratula.png";

    public void cargarImagen() {

        Image imagen = new Image(new File(rutaImagen).toURI().toString());

        Agregadosrecientemente1.setImage(imagen);
    }


    private static final String URL = "jdbc:mysql://localhost:3306/gamearchive?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "abc123";
    private static Connection connection;

    // Método para cargar juegos agregados recientemente
    private void cargarJuegosAgregadosRecientemente() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Consulta SQL para obtener los últimos juegos agregados
            String query = "SELECT rutaCaratula FROM Juegos ORDER BY idJuego DESC LIMIT 3";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            List<String> rutasCaratulas = new ArrayList<>();
            while (resultSet.next()) {
                String rutaCaratula = resultSet.getString("rutaCaratula");
                rutasCaratulas.add(rutaCaratula);
            }

            // Asignar las imágenes a los ImageView de los juegos agregados recientemente
            asignarImagenesAImageView(rutasCaratulas, Agregadosrecientemente1, Agregadosrecientemente2, Agregadosrecientemente3);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para cargar juegos aleatorios
    private void cargarJuegosAleatorios() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

            // Consulta SQL para obtener juegos aleatorios
            String query = "SELECT rutaCaratula FROM Juegos ORDER BY RAND() LIMIT 3";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            List<String> rutasCaratulas = new ArrayList<>();
            while (resultSet.next()) {
                String rutaCaratula = resultSet.getString("rutaCaratula");
                rutasCaratulas.add(rutaCaratula);
            }

            // Asignar las imágenes a los ImageView de los juegos aleatorios
            asignarImagenesAImageView(rutasCaratulas, Ramdom1, Ramdom2, Ramdom3);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Método para asignar las imágenes a los ImageView
    private void asignarImagenesAImageView(List<String> rutasCaratulas, ImageView... imageViews) {
        ClassLoader classLoader = getClass().getClassLoader();
        for (int i = 0; i < rutasCaratulas.size(); i++) {
            try {
                String rutaCaratula = rutasCaratulas.get(i);
                Image imagen = new Image(classLoader.getResourceAsStream(rutaCaratula));
                imageViews[i].setImage(imagen);
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleVolverPantallaPrincipal(ActionEvent event) throws IOException {
        Stage ventana = (Stage) Volver.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("MenuPrincipal.fxml"));
        Scene scene = new Scene(root);
        ventana.setScene(scene);
        ventana.show();
    }
}
