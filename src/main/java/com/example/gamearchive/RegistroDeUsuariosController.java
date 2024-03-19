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

    public void ConexionDB() {
        // URL de conexión, incluye el nombre de la base de datos y el timezone
        String url = "jdbc:mysql://localhost:3306/gamearchive?serverTimezone=UTC&user=root&password=abc123.";
        try {
            // Cargar el controlador de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establecer la conexión
            Connection connection = DriverManager.getConnection(url);
            // Si llega a este punto, la conexión se ha establecido con éxito
            System.out.println("¡Conexión exitosa a la base de datos!");
            // Puedes utilizar 'connection' para ejecutar consultas SQL, etc.
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el controlador de MySQL: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }
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
        // Verificar que los campos no estén vacíos
        String nombre = nombreUsuario.getText().trim();
        String correo = correoElectronico.getText().trim();
        String pass = contraseña.getText().trim();
        String repeatPass = RepetirContraseña.getText().trim();

        if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty() || repeatPass.isEmpty()) {
            mostrarNotificacion("Error", "Todos los campos son obligatorios.");
            return;
        }

        // Verificar que las contraseñas coincidan
        if (!pass.equals(repeatPass)) {
            mostrarNotificacion("Error", "Las contraseñas no coinciden.");
            return;
        }

        // Verificar que el correo electrónico tenga un formato válido
        if (!correo.matches("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b")) {
            mostrarNotificacion("Error", "El correo electrónico no tiene un formato válido.");
            return;
        }

        // Realizar la conexión a la base de datos y realizar la inserción
        Connection connection = null;
        try {
            // Establecer la conexión
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/gamearchive?serverTimezone=UTC", "root", "abc123.");

            // Preparar la consulta SQL
            String query = "INSERT INTO Usuarios (nombre, correo, contraseña, tipo_usuario) VALUES (?, ?, ?, 'usuario')";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nombre);
            statement.setString(2, correo);
            statement.setString(3, pass);

            // Ejecutar la consulta
            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                mostrarNotificacionExito("Éxito","Usuario registrado exitosamente.");
                // Limpiar los campos después del registro exitoso
                nombreUsuario.clear();
                correoElectronico.clear();
                contraseña.clear();
                RepetirContraseña.clear();
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            mostrarNotificacion("Error","Error al registrar el usuario. Por favor, inténtelo de nuevo.");
        } finally {
            // Cerrar la conexión
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
