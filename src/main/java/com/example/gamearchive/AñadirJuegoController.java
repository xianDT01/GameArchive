package com.example.gamearchive;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.sql.*;
import java.time.LocalDate;

public class AñadirJuegoController {

    @FXML
    private Button AñadirCaratulaJuego;

    private File caratulaJuegoFile;

    @FXML
    private TextField NombreJuego;

    @FXML
    private TextField Plataformas;

    @FXML
    private DatePicker FechaDeLanzamiento;

    @FXML
    private TextField Descripcion;

    @FXML
    private Button AñadirJuego;

    @FXML
    private void seleccionarCaratula(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Carátula del Juego");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        caratulaJuegoFile = fileChooser.showOpenDialog(new Stage());
    }

    @FXML
    private void añadirJuego() {
        // Resto del código...

        try {
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/gamearchive?serverTimezone=UTC", "root", "abc123.");
            String query = "INSERT INTO Juegos (nombre, descripcion, fechaLanzamiento, rutaCaratula) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, NombreJuego.getText());
            statement.setString(2, Descripcion.getText());
            statement.setDate(3, Date.valueOf(FechaDeLanzamiento.getValue()));
            statement.setString(4, caratulaJuegoFile.getAbsolutePath()); // Guarda la ruta de la imagen
            // Resto del código...
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Juego insertado exitosamente.");
                // Restablecer los campos después de la inserción exitosa
                NombreJuego.clear();
                Descripcion.clear();
                FechaDeLanzamiento.setValue(null);
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }

}
