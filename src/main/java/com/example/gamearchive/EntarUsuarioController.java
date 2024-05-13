package com.example.gamearchive;

import com.example.gamearchive.DatabaseConnection.DatabaseConnection;
import com.example.gamearchive.model.SesionUsuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class EntarUsuarioController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        contraseña.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                try {
                    handleEntrar(new ActionEvent());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @FXML
    private TextField correoElectronico;
    @FXML
    private PasswordField contraseña;
    @FXML
    private Button Entrar;
    @FXML
    private Button RegistrarUsuario;
    @FXML
    private VBox vbox;
    private Parent fxml;

    @FXML
    private void handleEntrar(ActionEvent event) throws IOException {
        String correo = correoElectronico.getText().trim();
        String pass = contraseña.getText().trim();
        if (correo.isEmpty() || pass.isEmpty()) {
            mostrarNotificacion("Error", "Por favor, complete todos los campos.");
            return;
        }
        Connection connection = null;
        try {

            connection = DatabaseConnection.getConnection();


            String query = "SELECT idUsuario,nombre,tipo_usuario FROM Usuarios WHERE correo = ? AND contraseña = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, correo);
            statement.setString(2, pass);


            ResultSet resultSet = statement.executeQuery();


            if (resultSet.next()) {
                String tipoUsuario = resultSet.getString("tipo_usuario");
                int idUsuario = resultSet.getInt("idUsuario");
                String nombreUsuario = resultSet.getString("nombre");
                if ("usuario".equals(tipoUsuario)) {

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuInicial.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png")));
                    stage.setScene(scene);
                    stage.show();
                    // Guardamos el id de el usuario que inició sesion para saber que usuario está usando la aplicación
                    SesionUsuario.setUsuario(idUsuario);
                    SesionUsuario.setNombreUsuario(nombreUsuario);
                    Stage ventanaActual = (Stage) Entrar.getScene().getWindow();
                    ventanaActual.close();
                } else if ("administrador".equals(tipoUsuario)) {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuAdministrador.fxml"));
                    Parent root = loader.load();
                    Scene scene = new Scene(root);
                    Stage stage = new Stage();
                    stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png")));
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
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_RIGHT)
                .darkStyle()
                .showError();
    }

    private void mostrarNotificacionExito(String titulo, String mensaje) {
        Notifications.create()
                .title(titulo)
                .text(mensaje)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_RIGHT)
                .darkStyle()
                .showInformation();
    }


}
