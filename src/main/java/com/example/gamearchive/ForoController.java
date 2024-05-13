package com.example.gamearchive;

import com.example.gamearchive.DatabaseConnection.DatabaseConnection;
import com.example.gamearchive.model.ControllerId;
import com.example.gamearchive.model.SesionUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.IOException;
import java.sql.*;

public class ForoController {
    @FXML
    private ListView<String> temasListView;
    @FXML
    private TextField nuevoTituloField;
    @FXML
    private TextField nuevaDescripcionField;
    @FXML
    private Button Volver;
    @FXML
    private void initialize() {
        cargarTemas();
        // Configurar el evento de doble clic en el ListView
        temasListView.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                abrirForoComentarios();
            }
        });
    }

    @FXML
    private void crearNuevoTema() {
        // Obtener los valores ingresados por el usuario
        String nuevoTitulo = nuevoTituloField.getText();
        String nuevaDescripcion = nuevaDescripcionField.getText();

        // Validar que se haya ingresado un título y una descripción
        if (nuevoTitulo.isEmpty() || nuevaDescripcion.isEmpty()) {
            mostrarMensajeError("Por favor, ingresa un título y una descripción para el nuevo tema.");
            return;
        }

        // Guardar el nuevo tema en la base de datos
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO Foro (titulo, descripcion, autor) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nuevoTitulo);
            statement.setString(2, nuevaDescripcion);
            statement.setString(3, SesionUsuario.getNombreUsuario());
            statement.executeUpdate();
            nuevoTituloField.clear();
            nuevaDescripcionField.clear();

            // Mostrar notificación de éxito
            mostrarNotificacion("Éxito", "El nuevo tema se ha creado correctamente.");

            // Actualizar la lista de temas
            cargarTemas();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarMensajeError("Error al guardar el nuevo tema en la base de datos.");
        }
    }


    private void abrirForoComentarios() {
        String temaSeleccionado = temasListView.getSelectionModel().getSelectedItem();
        if (temaSeleccionado != null) {
            // Obtener el ID del tema seleccionado
            int idForo = obtenerIdForo(temaSeleccionado);
            if (idForo != -1) {
                // Cerrar la ventana actual
                Stage currentStage = (Stage) temasListView.getScene().getWindow();
                currentStage.close();

                // Establecer el ID del tema en el controlador de ForoComentarios
                ControllerId.setIdForo(idForo);
                try {
                    // Cargar la vista de ForoComentarios.fxml
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("ForoComentarios.fxml"));
                    Parent root = loader.load();
                    // Obtener el controlador de ForoComentarios
                    ForoComentariosController controller = loader.getController();
                    // Configurar cualquier dato adicional que ForoComentariosController pueda necesitar
                    // Mostrar la escena en un nuevo Stage
                    Stage stage = new Stage();
                    stage.setTitle("Comentarios del Foro");
                    stage.setScene(new Scene(root));
                    stage.show();
                } catch (IOException e) {
                    e.printStackTrace();
                    mostrarMensajeError("Error al abrir la pantalla de comentarios del foro.");
                }
            } else {
                mostrarMensajeError("No se pudo obtener el ID del tema seleccionado.");
            }
        } else {
            mostrarMensajeError("Por favor, selecciona un tema para ver los comentarios.");
        }
    }


    // Método para obtener el ID del tema seleccionado
    private int obtenerIdForo(String temaSeleccionado) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT idForo FROM Foro WHERE titulo = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, temaSeleccionado);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("idForo");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

// Cargamos todos los temas del foro en el ListView
    private void cargarTemas() {
        ObservableList<String> temas = FXCollections.observableArrayList();
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT titulo FROM Foro";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                temas.add(resultSet.getString("titulo"));
            }
            temasListView.setItems(temas);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarMensajeError("Error al cargar los temas desde la base de datos.");
        }
    }
    @FXML
    private void handleVolverPantallaPrincipal(ActionEvent event) throws IOException {
        Stage ventana = (Stage) Volver.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("MenuInicial.fxml"));
        Scene scene = new Scene(root);
        ventana.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png")));
        ventana.setTitle("GameArchive");
        ventana.setScene(scene);
        ventana.show();
    }
    private void mostrarNotificacion(String titulo, String mensaje) {
        Notifications.create()
                .title(titulo)
                .text(mensaje)
                .hideAfter(Duration.seconds(5))
                .position(Pos.BOTTOM_RIGHT)
                .showError();
    }
    private void mostrarMensajeError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}