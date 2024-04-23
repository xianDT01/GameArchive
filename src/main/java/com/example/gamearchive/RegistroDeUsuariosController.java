package com.example.gamearchive;

import com.example.gamearchive.DatabaseConnection.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.sql.*;

public class RegistroDeUsuariosController {

    @FXML
    private Button VolverPantallaPrincipal;
    @FXML
    private TextField nombreUsuario;
    @FXML
    private TextField correoElectronico;
    @FXML
    private PasswordField contraseña;
    @FXML
    private PasswordField RepetirContraseña;
    @FXML
    private Button Registrar;

    @FXML
    private void handleVolverPantallaPrincipal(ActionEvent event) throws IOException {
        Stage ventana = (Stage) VolverPantallaPrincipal.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("MenuPrincipal.fxml"));
        ventana.setTitle("GameArchive");
        ventana.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png")));
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

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Verificar si el nombre de usuario ya está en uso
            String queryUsuario = "SELECT COUNT(*) AS count FROM Usuarios WHERE nombre = ?";
            PreparedStatement statementUsuario = connection.prepareStatement(queryUsuario);
            statementUsuario.setString(1, nombre);
            ResultSet resultSetUsuario = statementUsuario.executeQuery();
            if (resultSetUsuario.next() && resultSetUsuario.getInt("count") > 0) {
                mostrarNotificacion("Error", "El nombre de usuario ya está en uso. Por favor, elija otro nombre de usuario.");
                return;
            }

            // Verificar si el correo electrónico ya está en uso
            String queryCorreo = "SELECT COUNT(*) AS count FROM Usuarios WHERE correo = ?";
            PreparedStatement statementCorreo = connection.prepareStatement(queryCorreo);
            statementCorreo.setString(1, correo);
            ResultSet resultSetCorreo = statementCorreo.executeQuery();
            if (resultSetCorreo.next() && resultSetCorreo.getInt("count") > 0) {
                mostrarNotificacion("Error", "El correo electrónico ya está en uso. Por favor, utilice otro correo electrónico.");
                return;
            }

            // Insertar el nuevo usuario en la base de datos
            String queryInsert = "INSERT INTO Usuarios (nombre, correo, contraseña, tipo_usuario) VALUES (?, ?, ?, 'usuario')";
            PreparedStatement statementInsert = connection.prepareStatement(queryInsert);
            statementInsert.setString(1, nombre);
            statementInsert.setString(2, correo);
            statementInsert.setString(3, pass);
            int rowsInserted = statementInsert.executeUpdate();
            if (rowsInserted > 0) {
                mostrarNotificacionExito("Éxito", "Usuario registrado exitosamente.");

                nombreUsuario.clear();
                correoElectronico.clear();
                contraseña.clear();
                RepetirContraseña.clear();
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            mostrarNotificacion("Error", "Error al registrar el usuario. Por favor, inténtelo de nuevo.");
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
