package com.example.gamearchive;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.sql.*;

public class MenuAdministradorController {


    @FXML
    private Button AñadirJuego;
    @FXML
    private Button AddJuego;
    @FXML
    private Button ModificarJuego;
    @FXML
    private Button BorrarJuego;
    @FXML
    private Button Bienvenida;

    @FXML
    private AnchorPane PanelBienvenida;
    @FXML
    private AnchorPane PanelAñadirJuego;
    @FXML
    private AnchorPane PanelModificarJuego;
    @FXML
    private AnchorPane PanelBorrarJuego;

    @FXML
    private void ControllerPanel(ActionEvent event) {
        if (event.getSource() == AddJuego) {
            PanelAñadirJuego.setVisible(true);
            PanelModificarJuego.setVisible(false);
            PanelBorrarJuego.setVisible(false);
            PanelBienvenida.setVisible(false);
        } else if (event.getSource() == ModificarJuego) {
            PanelModificarJuego.setVisible(true);
            PanelAñadirJuego.setVisible(false);
            PanelBorrarJuego.setVisible(false);
            PanelBienvenida.setVisible(false);
        } else if (event.getSource() == BorrarJuego) {
            PanelBorrarJuego.setVisible(true);
            PanelAñadirJuego.setVisible(false);
            PanelModificarJuego.setVisible(false);
            PanelBienvenida.setVisible(false);
        }else if (event.getSource() ==PanelBienvenida) {
                PanelBienvenida.setVisible(true);
                PanelBorrarJuego.setVisible(false);
                PanelAñadirJuego.setVisible(false);
                PanelModificarJuego.setVisible(false);
            }
    }


    /*
    Añadir Juegos
     */
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
    private Button BotonAñadirJuego;

    private static final String URL = "jdbc:mysql://localhost:3306/gamearchive?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "abc123.";
    private static Connection connection;

    @FXML
    private void seleccionarCaratula(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Carátula del Juego");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        caratulaJuegoFile = fileChooser.showOpenDialog(new Stage());
    }
    public void ConexionDB() throws SQLException {
        // Obtener la conexión a la base de datos utilizando el método getConnection() de DatabaseConnection
        Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

        if (connection != null) {
            // Si llega a este punto, la conexión se ha establecido con éxito
            System.out.println("¡Conexión exitosa a la base de datos!");
            // Puedes utilizar 'connection' para ejecutar consultas SQL, etc.
        } else {
            System.err.println("Error al conectar a la base de datos.");
        }
    }

    @FXML
    private void HandleañadirJuego() {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "INSERT INTO Juegos (nombre, descripcion, fechaLanzamiento, rutaCaratula, plataformas) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, NombreJuego.getText());
            statement.setString(2, Descripcion.getText());
            statement.setDate(3, Date.valueOf(FechaDeLanzamiento.getValue()));
            statement.setString(4, caratulaJuegoFile.getAbsolutePath()); // Guarda la ruta de la imagen
            statement.setString(5, Plataformas.getText()); // Agrega las plataformas

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                System.out.println("Juego insertado exitosamente.");
                // Restablecer los campos después de la inserción exitosa
                NombreJuego.clear();
                Descripcion.clear();
                FechaDeLanzamiento.setValue(null);
                Plataformas.clear();
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }
/*
Modificar juegos
 */





}
