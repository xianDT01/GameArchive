package com.example.gamearchive;

import com.example.gamearchive.DatabaseConnection.DatabaseConnection;
import com.example.gamearchive.model.Comentario;
import com.example.gamearchive.model.ControllerId;
import com.example.gamearchive.model.SesionUsuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ForoComentariosController implements Initializable {
    @FXML
    private TableView<Comentario> tableViewComentarios;
    @FXML
    private TableColumn<Comentario, String> usuarioColumna;
    @FXML
    private TableColumn<Comentario, String> comentarioColumna;
    @FXML
    private TextField comentarioTextField;
    @FXML
    private Label tituloLabel;
    @FXML
    private Label descripcionLabel;
    @FXML
    private Button Volver;
    private int idForoSeleccionado = ControllerId.getIdForo();
    private int idUsuarioActual = SesionUsuario.getUsuario();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar las celdas de las columnas
        usuarioColumna.setCellValueFactory(cellData -> cellData.getValue().usuarioProperty());
        comentarioColumna.setCellValueFactory(cellData -> cellData.getValue().comentarioProperty());
        cargarComentarios();
        cargarTituloYDescripcionForo();
    }

    private void cargarComentarios() {
        // Limpiar TableView antes de cargar nuevos datos
        tableViewComentarios.getItems().clear();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT c.idComentario, c.comentario, u.nombre " +
                    "FROM ForoComentarios c " +
                    "INNER JOIN Usuarios u ON c.idUsuario = u.idUsuario " +
                    "WHERE c.idForo = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idForoSeleccionado);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int idComentario = resultSet.getInt("idComentario");
                String textoComentario = resultSet.getString("comentario");
                String nombreUsuario = resultSet.getString("nombre");
                tableViewComentarios.getItems().add(new Comentario(idComentario, textoComentario, nombreUsuario));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejo de errores
        }
    }
    private void cargarTituloYDescripcionForo() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT titulo, descripcion FROM Foro WHERE idForo = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idForoSeleccionado);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String titulo = resultSet.getString("titulo");
                String descripcion = resultSet.getString("descripcion");
                tituloLabel.setText(titulo);
                descripcionLabel.setText(descripcion);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void agregarComentario() {
        String textoComentario = comentarioTextField.getText();
        if (!textoComentario.isEmpty()) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "INSERT INTO ForoComentarios (idForo, idUsuario, comentario) VALUES (?, ?, ?)";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setInt(1, idForoSeleccionado);
                statement.setInt(2, idUsuarioActual);
                statement.setString(3, textoComentario);
                statement.executeUpdate();
                cargarComentarios(); // Recargar los comentarios después de agregar uno nuevo
                comentarioTextField.clear();
            } catch (SQLException e) {
                e.printStackTrace();
                // Manejo de errores
            }
        }
    }
    @FXML
    private void handleVolverForo(ActionEvent event) throws IOException {
        Stage ventana = (Stage) Volver.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("Foro.fxml"));
        ventana.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png")));
        Scene scene = new Scene(root);
        ventana.setTitle("GameArchive");
        ventana.setScene(scene);
        ventana.show();
    }

}
