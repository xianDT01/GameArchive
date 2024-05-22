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
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
    private TextField comentarioTextField;
    @FXML
    private Label tituloLabel;
    @FXML
    private Label descripcionLabel;
    @FXML
    private VBox comentariosVBox;
    @FXML
    private Button Volver;

    private int idForoSeleccionado = ControllerId.getIdForo();
    private int idUsuarioActual = SesionUsuario.getUsuario();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarTituloYDescripcionForo();
        cargarComentarios();

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

    private void cargarComentarios() {
        comentariosVBox.getChildren().clear();  // Limpiar comentarios existentes
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT Usuarios.nombre, Usuarios.imagen_de_perfil, ForoComentarios.comentario " +
                    "FROM ForoComentarios " +
                    "JOIN Usuarios ON ForoComentarios.idUsuario = Usuarios.idUsuario " +
                    "WHERE ForoComentarios.idForo = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idForoSeleccionado);
            ResultSet resultSet = statement.executeQuery();

            VBox comentariosContentVBox = new VBox(); // VBox para contener los comentarios
            comentariosContentVBox.setSpacing(10);

            while (resultSet.next()) {
                String nombreUsuario = resultSet.getString("nombre");
                String imagenPerfil = resultSet.getString("imagen_de_perfil");
                String textoComentario = resultSet.getString("comentario");

                // Crear nodos para mostrar el comentario
                ImageView perfilImageView = new ImageView();
                if (imagenPerfil != null && !imagenPerfil.isEmpty()) {
                    try {
                        // Quitar "src/main/resources" de la ruta de la imagen
                        String rutaRelativaImagen = imagenPerfil.replace("src/main/resources/", "");
                        // Cargar la imagen desde los recursos
                        perfilImageView.setImage(new Image(getClass().getResourceAsStream("/" + rutaRelativaImagen)));
                    } catch (IllegalArgumentException e) {
                        // Si la URL de la imagen es inválida, usar una imagen de perfil predeterminada
                        perfilImageView.setImage(new Image(getClass().getResourceAsStream("/profiles_images/user.png")));
                    }
                } else {
                    // Usar una imagen de perfil predeterminada si no se proporciona ninguna
                    perfilImageView.setImage(new Image(getClass().getResourceAsStream("/profiles_images/user.png")));
                }
                perfilImageView.setFitHeight(80); // Ajusta la altura de la imagen
                perfilImageView.setFitWidth(80); // Ajusta el ancho de la imagen

                VBox comentarioVBox = new VBox();
                comentarioVBox.getStyleClass().add("vbox-comentario");

                Label nombreLabel = new Label(nombreUsuario);
                nombreLabel.getStyleClass().add("nombre-usuario");
                nombreLabel.setStyle("-fx-font-size: 14px;"); // Ajusta el tamaño de la fuente del nombre del usuario

                // Usar un Text para mostrar el comentario, permitiendo saltos de línea
                Text comentarioText = new Text(textoComentario);
                comentarioText.setWrappingWidth(1175); // Ancho máximo para ajustarse al contenedor
                comentarioText.setStyle("-fx-font-size: 16px;"); // Ajusta el tamaño de la fuente del comentario
                comentarioText.setFill(Color.WHITE); // Establece el color del texto a blanco

                comentarioVBox.getChildren().addAll(nombreLabel, comentarioText);

                HBox comentarioHBox = new HBox();
                comentarioHBox.setSpacing(10);
                comentarioHBox.getStyleClass().add("hbox-comentario");
                comentarioHBox.getChildren().addAll(perfilImageView, comentarioVBox);

                // Agregar el comentario al VBox de contenido
                comentariosContentVBox.getChildren().add(comentarioHBox);
            }

            // Agregar el VBox de contenido al ScrollPane
            ScrollPane scrollPane = new ScrollPane(comentariosContentVBox);
            scrollPane.setFitToWidth(true); // Permitir que el ScrollPane ajuste su ancho al contenedor

            // Agregar el ScrollPane al VBox principal
            comentariosVBox.getChildren().add(scrollPane);

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
                comentarioTextField.clear();
                cargarComentarios(); // Refresh comments
            } catch (SQLException e) {
                e.printStackTrace();
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