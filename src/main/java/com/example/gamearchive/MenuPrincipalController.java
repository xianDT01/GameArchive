package com.example.gamearchive;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;
import java.io.IOException;
import java.sql.*;


public class MenuPrincipalController {

    @FXML
    private TextField correoElectronico;
    @FXML
    private PasswordField contraseña;
    @FXML
    private Button Entrar;
    @FXML
    private Button RegistrarUsuario;

    private static final String URL = "jdbc:mysql://localhost:3306/gamearchive?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "abc123.";
    private static Connection connection;




    @FXML
    private void handleEntrar(ActionEvent event) throws IOException {
        String correo = correoElectronico.getText().trim();
        String pass = contraseña.getText().trim();

        // Verificar que los campos no estén vacíos
        if (correo.isEmpty() || pass.isEmpty()) {
            mostrarNotificacion("Error", "Por favor, complete todos los campos.");
            return;
        }

        // Realizar la conexión a la base de datos y verificar las credenciales
        Connection connection = null;
        try {
            // Establecer la conexión
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/gamearchive?serverTimezone=UTC", "root", "abc123.");

            // Preparar la consulta SQL
            String query = "SELECT idUsuario,tipo_usuario FROM Usuarios WHERE correo = ? AND contraseña = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, correo);
            statement.setString(2, pass);

            // Ejecutar la consulta
            ResultSet resultSet = statement.executeQuery();

            // Verificar si se encontró un usuario con las credenciales proporcionadas
            if (resultSet.next()) {
                String tipoUsuario = resultSet.getString("tipo_usuario");
                int idUsuario = resultSet.getInt("idUsuario");
                if ("usuario".equals(tipoUsuario)) {
                    // Redirigir a la pantalla de usuario
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuInicial.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();
                    SesionUsuario.setUsuario(idUsuario);
                    // Cerrar la ventana actual
                    Stage ventanaActual = (Stage) Entrar.getScene().getWindow();
                    ventanaActual.close();
                } else if ("administrador".equals(tipoUsuario)) {
                    // Redirigir a la pantalla de administrador
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuAdministrador.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.setScene(scene);
                    stage.show();

                    // Cerrar la ventana actual
                    Stage ventanaActual = (Stage) Entrar.getScene().getWindow();
                    ventanaActual.close();
                }
            } else {
                mostrarNotificacion("Error", "Credenciales incorrectas. Por favor, inténtelo de nuevo.");
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());

            mostrarNotificacion("Error", "Error al iniciar sesión. Por favor, inténtelo de nuevo.");
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
                .hideAfter(Duration.seconds(5)) // Ocultar después de 5 segundos
                .position(Pos.BOTTOM_RIGHT) // Posición de la notificación en la pantalla
                .showError();
    }

    @FXML
    private void handleRegistarUsuarios(ActionEvent event) throws IOException {
        Stage ventana = (Stage) RegistrarUsuario.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("RegistroDeUsuarios.fxml"));
        Scene scene = new Scene(root);
        ventana.setScene(scene);
        ventana.show();


    }


}