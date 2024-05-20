package com.example.gamearchive;

import com.example.gamearchive.DatabaseConnection.DatabaseConnection;
import com.example.gamearchive.model.Comentario;
import com.example.gamearchive.model.ComentariosForo;
import com.example.gamearchive.model.ControllerId;
import com.example.gamearchive.model.SesionUsuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MenuJuegoController implements Initializable {
    @FXML
    private Button Volver;
    @FXML
    private ImageView ImagenJuego;
    @FXML
    private Label NombreJuego;
    @FXML
    private Label Plataformas;
    @FXML
    private Label FechaDeLanzamiento;
    @FXML
    private Label Descripcion;
    @FXML
    private Label mostrarNotaJuego;
    @FXML
    private Button Votar;
    @FXML
    private TextField NotaJuego;
    int IdJuego = ControllerId.getIdJuego();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        double notaPromedio = obtenerNotaPromedio();
        String notaFormateada = String.format("%.1f", notaPromedio);
        mostrarNotaJuego.setText(String.valueOf(notaFormateada));
        BotonComentar.setOnAction(this::agregarComentario);
        cargarComentarios(); // Llamar al método para cargar los comentarios

    }
    public void initData(int idJuego,String nombreJuego,String descriptcion, String fechaLanzamiento, String rutaCaratula,String plataformas) {
        idJuego = idJuego;
        NombreJuego.setText(nombreJuego);
        FechaDeLanzamiento.setText(fechaLanzamiento);
        Image image = new Image(new File(rutaCaratula).toURI().toString());
        ImagenJuego.setImage(image);
        Descripcion.setText(descriptcion);
        Plataformas.setText(plataformas);
        IdJuego = idJuego;

    }
    private double obtenerNotaPromedio() {
        double promedio = 0.0;

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT COALESCE((SELECT AVG(calificacion) FROM Reseñas WHERE idJuego = ?), 0) AS promedio";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, ControllerId.getIdJuego());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                promedio = resultSet.getDouble("promedio");
                if (promedio >= 0 && promedio < 5) {
                    mostrarNotaJuego.setStyle("-fx-background-color: #FF6874; -fx-text-fill: black; -fx-background-radius: 5;");
                } else if (promedio >= 5 && promedio < 8) {
                    mostrarNotaJuego.setStyle("-fx-background-color: #FFBD3F; -fx-text-fill: black; -fx-background-radius: 5;");
                } else if (promedio >= 8 && promedio < 9) {
                    mostrarNotaJuego.setStyle("-fx-background-color: #00CE7A; -fx-text-fill: black; -fx-background-radius: 5;");
                } else if (promedio >= 9) {
                    mostrarNotaJuego.setStyle("-fx-background-color: #00CE7A; -fx-text-fill: gold; -fx-background-radius: 5;");
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return promedio;
    }
    @FXML
    private void guardarReseña(ActionEvent event) {
        String nota = NotaJuego.getText();
        int calificacion = Integer.parseInt(nota);

        int idUsuario = SesionUsuario.getUsuario();
        int idJuego = IdJuego;

        // Verificar si el usuario ya ha votado
        if (!usuarioHaVotado(idUsuario, idJuego)) {
            // Verificar si la calificación está dentro del rango válido
            if (calificacion >= 1 && calificacion <= 10) {
                String query = "INSERT INTO reseñas (idJuego, idUsuario, calificacion) VALUES (?, ?, ?)";

                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement statement = connection.prepareStatement(query)) {
                    statement.setInt(1, idJuego);
                    statement.setInt(2, idUsuario);
                    statement.setInt(3, calificacion);
                    statement.executeUpdate();
                    mostrarNotificacionExito("Éxito","Tu voto se guardo correctamente");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {

                mostrarAlerta("Error", "Calificación inválida", "La calificación debe estar entre 1 y 10.");
            }
        } else {

            mostrarAlerta("Error", "Usuario ya ha votado", "El usuario ya ha votado para este juego.");
        }
        double notaPromedioAntes = obtenerNotaPromedio();
        double notaPromedioDespues = obtenerNotaPromedio();
        String notaFormateada = String.format("%.1f", notaPromedioDespues);
        mostrarNotaJuego.setText(String.valueOf(notaFormateada));

        obtenerNotaPromedio();
    }
    private boolean usuarioHaVotado(int idUsuario, int idJuego) {
        String query = "SELECT COUNT(*) FROM reseñas WHERE idUsuario = ? AND idJuego = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idUsuario);
            statement.setInt(2, idJuego);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    int count = resultSet.getInt(1);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    @FXML
    private TextField comentario;

    @FXML
    private Button BotonComentar;


    // Método que se llama cuando se presiona el botón de comentar
    private void agregarComentario(ActionEvent event) {
        String textoComentario = comentario.getText();

        if (textoComentario.trim().isEmpty()) {
            mostrarNotificacion("Error", "Tienes que escribir algo antes de comentar");
            return;
        }

        if (usuarioYaComento()) {
            mostrarNotificacion("Error", "El usuario ya comentó en este Juego, no se pueden comentar dos veces por juego");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "INSERT INTO Comentarios (idUsuario, idJuego, comentario) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, SesionUsuario.getUsuario());
            statement.setInt(2, IdJuego);
            statement.setString(3, textoComentario);
            statement.executeUpdate();
            comentario.clear();
            cargarComentarios(); // Llamar al método para actualizar los comentarios
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    private boolean usuarioYaComento() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT COUNT(*) AS numComentarios FROM Comentarios WHERE idUsuario = ? AND idJuego = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, SesionUsuario.getUsuario());
            statement.setInt(2, IdJuego);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int numComentarios = resultSet.getInt("numComentarios");
                return numComentarios > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
        return false;
    }

    @FXML
    private VBox contenedorComentarios;
    @FXML
    private ScrollPane scrollPaneComentarios;

    @FXML
    private void cargarComentarios() {
        contenedorComentarios.getChildren().clear();
        int idJuego = IdJuego;

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT r.calificacion, u.nombre, c.comentario " +
                    "FROM Comentarios c " +
                    "JOIN Usuarios u ON c.idUsuario = u.idUsuario " +
                    "JOIN Reseñas r ON r.idUsuario = u.idUsuario AND r.idJuego = c.idJuego " +
                    "WHERE c.idJuego = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idJuego);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int calificacion = resultSet.getInt("calificacion");
                String nombreUsuario = resultSet.getString("nombre");
                String comentarioTexto = resultSet.getString("comentario");

                // Contenedor principal para cada comentario
                VBox comentarioBox = new VBox();
                comentarioBox.setSpacing(5);
                comentarioBox.setStyle("-fx-padding: 10; -fx-background-radius: 5; -fx-background-color: #333;");
                comentarioBox.setMargin(comentarioBox, new Insets(0, 0, 10, 0));

                // Etiqueta para la calificación
                Label calificacionLabel = new Label(String.valueOf(calificacion));
                calificacionLabel.setStyle("-fx-font-size: 30; -fx-font-weight: bold; -fx-padding: 8;");

                // Aplicar color según la calificación
                if (calificacion >= 0 && calificacion < 5) {
                    calificacionLabel.setStyle(calificacionLabel.getStyle() + "-fx-background-color: #FF6874; -fx-text-fill: black; -fx-background-radius: 10;");
                } else if (calificacion >= 5 && calificacion < 8) {
                    calificacionLabel.setStyle(calificacionLabel.getStyle() + "-fx-background-color: #FFBD3F; -fx-text-fill: black; -fx-background-radius: 10;");
                } else if (calificacion >= 8 && calificacion < 9) {
                    calificacionLabel.setStyle(calificacionLabel.getStyle() + "-fx-background-color: #00CE7A; -fx-text-fill: black; -fx-background-radius: 10;");
                } else if (calificacion >= 9) {
                    calificacionLabel.setStyle(calificacionLabel.getStyle() + "-fx-background-color: #00CE7A; -fx-text-fill: gold; -fx-background-radius: 10;");
                }

                // Etiqueta para el nombre del usuario
                Label usuarioLabel = new Label(nombreUsuario);
                usuarioLabel.setStyle("-fx-font-size: 24; -fx-text-fill: white;");

                // Etiqueta para el comentario
                Text comentarioText = new Text(comentarioTexto);
                comentarioText.setStyle("-fx-font-size: 14; -fx-fill: white;");
                comentarioText.setWrappingWidth(1200); // Ajusta el ancho máximo del texto

                // Añadir etiquetas al contenedor del comentario
                HBox headerBox = new HBox();
                headerBox.setSpacing(10);
                headerBox.getChildren().addAll(calificacionLabel, usuarioLabel);

                comentarioBox.getChildren().addAll(headerBox, comentarioText);
                contenedorComentarios.getChildren().add(comentarioBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
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
    private void mostrarAlerta(String titulo, String encabezado, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(encabezado);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    @FXML
    private void handleVolverPantallaPrincipal(ActionEvent event) throws IOException {
        Stage ventana = (Stage) Volver.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MenuInicial.fxml"));
        Parent root = fxmlLoader.load();
        Image icono = new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png"));
        ventana.getIcons().add(icono);
        ventana.setTitle("GameArchive");
        Scene scene = new Scene(root);
        ventana.setScene(scene);
        ventana.show();
    }

}

