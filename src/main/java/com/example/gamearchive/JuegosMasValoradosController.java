package com.example.gamearchive;

import com.example.gamearchive.DatabaseConnection.DatabaseConnection;
import com.example.gamearchive.model.ControllerId;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class JuegosMasValoradosController implements Initializable {

    @FXML
    private Button Volver;

    @FXML
    private VBox contenedorJuegos; // Contenedor para agregar los elementos de los juegos

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            mostrarJuegosMasValorados();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void mostrarJuegosMasValorados() throws SQLException {
        String query = "SELECT J.idJuego, J.nombre, J.descripcion, J.rutaCaratula, J.plataformas, " +
                "(SELECT AVG(R.calificacion) FROM Reseñas R WHERE R.idJuego = J.idJuego) AS notaMedia " +
                "FROM Juegos J " +
                "ORDER BY notaMedia DESC " +
                "LIMIT 10";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int idJuego = resultSet.getInt("idJuego");
                String nombre = resultSet.getString("nombre");
                String descripcion = resultSet.getString("descripcion");
                String rutaCaratula = resultSet.getString("rutaCaratula");
                String plataformas = resultSet.getString("plataformas");
                double notaMedia = resultSet.getDouble("notaMedia");

                // Crear elementos para mostrar el juego
                ImageView imageView = new ImageView(new Image(new File(rutaCaratula).toURI().toString()));
                imageView.setFitHeight(150);
                imageView.setFitWidth(100);
                asignarEventoClic(imageView, idJuego);

                Label labelNombre = new Label(nombre);
                labelNombre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

                Label labelNotaMedia = new Label(String.format("%.2f", notaMedia));
                labelNotaMedia.setStyle("-fx-font-size: 18px; -fx-background-radius: 5; -fx-padding: 5;");
                if (notaMedia >= 0 && notaMedia < 5) {
                    labelNotaMedia.setStyle(labelNotaMedia.getStyle() + "-fx-background-color: #FF6874; -fx-text-fill: black;");
                } else if (notaMedia >= 5 && notaMedia < 8) {
                    labelNotaMedia.setStyle(labelNotaMedia.getStyle() + "-fx-background-color: #FFBD3F; -fx-text-fill: black;");
                } else if (notaMedia >= 8 && notaMedia < 9) {
                    labelNotaMedia.setStyle(labelNotaMedia.getStyle() + "-fx-background-color: #00CE7A; -fx-text-fill: black;");
                } else if (notaMedia >= 9) {
                    labelNotaMedia.setStyle(labelNotaMedia.getStyle() + "-fx-background-color: #00CE7A; -fx-text-fill: gold;");
                }

                HBox nombreNotaBox = new HBox(10, labelNombre, labelNotaMedia);

                Label labelPlataformas = new Label("Plataformas: " + plataformas);
                labelPlataformas.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
                Label labelDescripcion = new Label(descripcion);
                labelDescripcion.setWrapText(true);
                labelDescripcion.setMaxWidth(Double.MAX_VALUE); // Permite que la descripción ocupe todo el ancho disponible
                labelDescripcion.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");

                VBox.setVgrow(labelDescripcion, Priority.ALWAYS); // Permite que la descripción crezca en el VBox si es necesario

                VBox detallesBox = new VBox(10, nombreNotaBox, labelPlataformas, labelDescripcion);
                HBox.setHgrow(detallesBox, Priority.ALWAYS); // Permite que el VBox crezca en el HBox si es necesario

                HBox juegoBox = new HBox(10, imageView, detallesBox);
                juegoBox.setStyle("-fx-padding: 10; -fx-background-color: #333; -fx-border-color: #555; -fx-border-width: 2px;");
                juegoBox.setMinWidth(600); // Ajusta el ancho mínimo del contenedor según tus necesidades
                contenedorJuegos.getChildren().add(juegoBox);
            }
        }
    }




    private void asignarEventoClic(ImageView imageView, int idJuego) {
        imageView.setOnMouseClicked(event -> {
            abrirMenuJuego(idJuego);
        });

        // Añadir animación al pasar el ratón
        imageView.setOnMouseEntered(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), imageView);
            st.setToX(1.1);
            st.setToY(1.1);
            st.play();
        });

        imageView.setOnMouseExited(event -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(200), imageView);
            st.setToX(1.0);
            st.setToY(1.0);
            st.play();
        });
    }



    private void abrirMenuJuego(int idJuego) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT idJuego, nombre, descripcion, fechaLanzamiento, rutaCaratula, plataformas " +
                    "FROM Juegos " +
                    "WHERE idJuego = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idJuego);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int idJuegoResultado = resultSet.getInt("idJuego");
                String nombreJuego = resultSet.getString("nombre");
                String descripcion = resultSet.getString("descripcion");
                String fechaLanzamiento = resultSet.getString("fechaLanzamiento");
                String rutaCaratula = resultSet.getString("rutaCaratula");
                String plataformas = resultSet.getString("plataformas");
                ControllerId.setIdJuego(idJuego);
                // Consultar y mostrar la nota media del juego por consola
                double notaMedia = obtenerNotaMediaJuego(idJuegoResultado);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuJuego.fxml"));
                Parent root = loader.load();
                MenuJuegoController controller = loader.getController();
                controller.initData(idJuegoResultado, nombreJuego, descripcion, fechaLanzamiento, rutaCaratula, plataformas);
                Stage stage = new Stage();
                stage.setTitle("GameArchive");
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png")));
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                System.out.println("No se encontró información para el juego con ID: " + idJuego);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    private double obtenerNotaMediaJuego(int idJuego) throws SQLException {
        double notaMedia = 0;
        int totalCalificaciones = 0;
        int sumaCalificaciones = 0;

        String query = "SELECT calificacion FROM Reseñas WHERE idJuego = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, idJuego);
            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int calificacion = resultSet.getInt("calificacion");
                    sumaCalificaciones += calificacion;
                    totalCalificaciones++;
                }
            }
        }
        if (totalCalificaciones > 0) {
            notaMedia = (double) sumaCalificaciones / totalCalificaciones;
        }

        return notaMedia;
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
}