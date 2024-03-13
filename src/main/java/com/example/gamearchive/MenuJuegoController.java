package com.example.gamearchive;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
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
    @FXML
    private Label mostrarNotaJuego;
    @FXML
    private Button Votar;
    @FXML
    private TextField NotaJuego;

    private static final String URL = "jdbc:mysql://localhost:3306/gamearchive?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "abc123.";
    private static Connection connection;
    int IdJuego = ControllerId.getIdJuego();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        double notaPromedio = obtenerNotaPromedio();
        mostrarNotaJuego.setText(String.valueOf(notaPromedio));
    }
    public void initData(int idJuego,String nombreJuego,String descriptcion, String fechaLanzamiento, String rutaCaratula,String plataformas) {
        idJuego = idJuego;
        NombreJuego.setText(nombreJuego);
        FechaDeLanzamiento.setText(fechaLanzamiento);
        Image image = new Image(new File(rutaCaratula).toURI().toString());
        ImagenJuego.setImage(image);
        Descripcion.setText(descriptcion);
        Plataformas.setText(plataformas);
        IdJuego = idJuego;

    }

    private double obtenerNotaPromedio() {
        double promedio = 0.0;

        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            String query = "SELECT COALESCE((SELECT AVG(calificacion) FROM Reseñas WHERE idJuego = ?), 0) AS promedio";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, ControllerId.getIdJuego());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                promedio = resultSet.getDouble("promedio");

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return promedio;
    }


    @FXML
    private void guardarReseña(ActionEvent event) {
        String nota = NotaJuego.getText();
        int calificacion = Integer.parseInt(nota);

        // Asumiendo que tienes los ID de usuario y juego almacenados en variables adecuadas
        int idUsuario = SesionUsuario.getUsuario();
        int idJuego = IdJuego;

        // Verificar si el usuario ya ha votado
        if (!usuarioHaVotado(idUsuario, idJuego)) {
            // Verificar si la calificación está dentro del rango válido
            if (calificacion >= 1 && calificacion <= 10) {
                String query = "INSERT INTO reseñas (idJuego, idUsuario, calificacion) VALUES (?, ?, ?)";

                try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                     PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, idJuego);
                    statement.setInt(2, idUsuario);
                    statement.setInt(3, calificacion);
                    statement.executeUpdate();
                    mostrarAlerta("Aceptado", "Calificado", "Tu voto se guardo correctamente");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                // La calificación está fuera del rango válido
                mostrarAlerta("Error", "Calificación inválida", "La calificación debe estar entre 1 y 10.");
            }
        } else {
            // El usuario ya ha votado para este juego
            mostrarAlerta("Error", "Usuario ya ha votado", "El usuario ya ha votado para este juego.");
        }
    }

    private boolean usuarioHaVotado(int idUsuario, int idJuego) {
        String query = "SELECT COUNT(*) FROM reseñas WHERE idUsuario = ? AND idJuego = ?";
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idUsuario);
            statement.setInt(2, idJuego);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void mostrarAlerta(String titulo, String encabezado, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
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
