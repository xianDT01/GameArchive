package com.example.gamearchive;

import com.example.gamearchive.DatabaseConnection.DatabaseConnection;
import com.example.gamearchive.model.ControllerId;
import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
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
    private VBox contenedorJuegos;

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
                "LIMIT 50";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            int rank = 1; // Iniciamos el contador de ranking

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

                // Crear el círculo con el puesto del juego
                Label rankLabel = new Label(String.valueOf(rank));
                rankLabel.setStyle("-fx-font-size: 24px; -fx-background-color: #00ADEF; -fx-text-fill: white; -fx-background-radius: 50%; -fx-alignment: center; -fx-min-width: 50px; -fx-min-height: 50px; -fx-max-width: 50px; -fx-max-height: 50px;");
                rankLabel.setAlignment(Pos.CENTER);

                Label labelNombre = new Label(nombre);
                labelNombre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: white;");

                Label labelNotaMedia = new Label(String.format("%.2f", notaMedia));
                labelNotaMedia.setStyle("-fx-font-size: 32px; -fx-background-radius: 5; -fx-padding: 8;");
                if (notaMedia >= 0 && notaMedia < 5) {
                    labelNotaMedia.setStyle(labelNotaMedia.getStyle() + "-fx-background-color: #FF6874; -fx-text-fill: black;");
                } else if (notaMedia >= 5 && notaMedia < 8) {
                    labelNotaMedia.setStyle(labelNotaMedia.getStyle() + "-fx-background-color: #FFBD3F; -fx-text-fill: black;");
                } else if (notaMedia >= 8 && notaMedia < 9) {
                    labelNotaMedia.setStyle(labelNotaMedia.getStyle() + "-fx-background-color: #00CE7A; -fx-text-fill: black;");
                } else if (notaMedia >= 9) {
                    labelNotaMedia.setStyle(labelNotaMedia.getStyle() + "-fx-background-color: #00CE7A; -fx-text-fill: gold;");
                }

                HBox nombreNotaBox = new HBox(10, labelNombre);
                HBox notaBox = new HBox(labelNotaMedia);
                notaBox.setAlignment(Pos.TOP_RIGHT);
                HBox.setHgrow(notaBox, Priority.ALWAYS); // Asegura que la nota se alinee a la derecha

                // Dividir plataformas en líneas más cortas y asegurar que no se trunquen
                String plataformasFormatted = plataformas.replaceAll(" / ", "\n");
                Label labelPlataformas = new Label("Plataformas: " + plataformasFormatted);
                labelPlataformas.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
                labelPlataformas.setWrapText(true); // Permitir el ajuste de línea
                labelPlataformas.setMaxWidth(Double.MAX_VALUE); // Ajustar el ancho máximo
                labelPlataformas.setTextOverrun(OverrunStyle.CLIP); // Evitar truncar el texto

                VBox detallesBox = new VBox(10, nombreNotaBox, labelPlataformas);
                detallesBox.setMaxWidth(Double.MAX_VALUE); // Permitir que el VBox crezca en el HBox si es necesario
                HBox.setHgrow(detallesBox, Priority.ALWAYS); // Permitir que el VBox crezca en el HBox si es necesario

                // Crear contenedor para centrar el círculo
                VBox rankBox = new VBox(rankLabel);
                rankBox.setAlignment(Pos.CENTER); // Centra el círculo verticalmente

                HBox juegoBox = new HBox(20, rankBox, imageView, detallesBox, notaBox); // Aumentar el espacio entre elementos
                juegoBox.setStyle("-fx-padding: 10; -fx-background-color: #333; -fx-border-color: #555; -fx-border-width: 2px;");
                juegoBox.setMinWidth(600); // Ajusta el ancho mínimo del contenedor según tus necesidades
                contenedorJuegos.getChildren().add(juegoBox);

                rank++; // Incrementa el contador de ranking
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

                // Cerrar la ventana actual
                Stage currentStage = (Stage) contenedorJuegos.getScene().getWindow();
                currentStage.close();
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