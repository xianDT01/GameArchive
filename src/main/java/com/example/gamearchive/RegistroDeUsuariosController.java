package com.example.gamearchive;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RegistroDeUsuariosController {

    @FXML
    private Button VolverPantallaPrincipal;
    @FXML
    private TextField nombreUsuario;
    @FXML
    private TextField correoElectronico;
    @FXML
    private TextField contraseña;
    @FXML
    private TextField RepetirContraseña;
    @FXML
    private Button Registrar;

    private static final String URL = "jdbc:mysql://localhost:3306/gamearchive?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "abc123.";

    @FXML
    private void handleVolverPantallaPrincipal(ActionEvent event) throws IOException {
        Stage ventana = (Stage) VolverPantallaPrincipal.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("MenuPrincipal.fxml"));
        Scene scene = new Scene(root);
        ventana.setScene(scene);
        ventana.show();
    }

    @FXML
    public void handleBotonRegistarUsuario(ActionEvent event) {

        String nombre = nombreUsuario.getText().trim();
        String correo = correoElectronico.getText().trim();
        String pass = contraseña.getText().trim();
        String repeatPass = RepetirContraseña.getText().trim();

        if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty() || repeatPass.isEmpty()) {
            mostrarNotificacion("Error", "Todos los campos son obligatorios.");
            return;
        }


        if (!pass.equals(repeatPass)) {
            mostrarNotificacion("Error", "Las contraseñas no coinciden.");
            return;
        }

        if (!correo.matches("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b")) {
            mostrarNotificacion("Error", "El correo electrónico no tiene un formato válido.");
            return;
        }
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "INSERT INTO Usuarios (nombre, correo, contraseña, tipo_usuario) VALUES (?, ?, ?, 'usuario')";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nombre);
            statement.setString(2, correo);
            statement.setString(3, pass);


            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                mostrarNotificacionExito("Éxito","Usuario registrado exitosamente.");

                nombreUsuario.clear();
                correoElectronico.clear();
                contraseña.clear();
                RepetirContraseña.clear();
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            mostrarNotificacion("Error","Error al registrar el usuario. Por favor, inténtelo de nuevo.");
        } finally {

            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }



    private void mostrarNotificacion(String titulo, String mensaje) {
        Notifications.create()
                .title(titulo)
                .text(mensaje)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_RIGHT)
                .showError();
    }
    private void mostrarNotificacionExito(String titulo, String mensaje) {
        Notifications.create()
                .title(titulo)
                .text(mensaje)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_RIGHT)
                .showInformation();
    }


}
