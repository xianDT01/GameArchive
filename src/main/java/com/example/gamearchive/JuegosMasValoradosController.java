package com.example.gamearchive;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
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
    private ImageView Top1;
    @FXML
    private ImageView Top2;
    @FXML
    private ImageView Top3;
    @FXML
    private ImageView Top4;
    @FXML
    private ImageView Top5;
    @FXML
    private ImageView Top6;
    @FXML
    private ImageView Top7;
    @FXML
    private ImageView Top8;
    @FXML
    private ImageView Top9;
    @FXML
    private ImageView Top10;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT idJuego, rutaCaratula FROM Juegos " +
                    "ORDER BY (SELECT AVG(calificacion) FROM Reseñas WHERE Reseñas.idJuego = Juegos.idJuego) DESC " +
                    "LIMIT 10";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            Map<ImageView, Integer> imageViewMap = new HashMap<>();
            imageViewMap.put(Top1, null);
            imageViewMap.put(Top2, null);
            imageViewMap.put(Top3, null);
            imageViewMap.put(Top4, null);
            imageViewMap.put(Top5, null);
            imageViewMap.put(Top6, null);
            imageViewMap.put(Top7, null);
            imageViewMap.put(Top8, null);
            imageViewMap.put(Top9, null);
            imageViewMap.put(Top10, null);

            int index = 0;
            while (resultSet.next() && index < 10) {
                int idJuego = resultSet.getInt("idJuego");
                String rutaCaratula = resultSet.getString("rutaCaratula");
                ImageView imageView = getTopImageViewByIndex(index);
                imageViewMap.put(imageView, idJuego);
                // Cargar la imagen del juego desde la rutaCaratula y asignarla al ImageView
                File file = new File(rutaCaratula);
                Image image = new Image(file.toURI().toString());
                imageView.setImage(image);
                // Asignar evento de clic para abrir la pantalla del juego correspondiente
                asignarEventoClic(imageView, idJuego);

                index++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private ImageView getTopImageViewByIndex(int index) {
        switch (index) {
            case 0:
                return Top1;
            case 1:
                return Top2;
            case 2:
                return Top3;
            case 3:
                return Top4;
            case 4:
                return Top5;
            case 5:
                return Top6;
            case 6:
                return Top7;
            case 7:
                return Top8;
            case 8:
                return Top9;
            case 9:
                return Top10;
            default:
                return null;
        }
    }
    private void asignarEventoClic(ImageView imageView, int idJuego) {
        imageView.setOnMouseClicked(event -> {
            // Abrir la pantalla del juego cuando se hace clic en la imagen
            abrirMenuJuego(idJuego);
        });
    }
    private void abrirMenuJuego(int idJuego) {
        try (Connection connection =DatabaseConnection.getConnection()) {
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
                Stage ventanaActual = (Stage) Top1.getScene().getWindow();
                ventanaActual.close();

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
    private void onMouseTop1() {
        aplicarAnimacion(Top1);
    }
    @FXML
    private void onMouseTop2() {
        aplicarAnimacion(Top2);
    }
    @FXML
    private void onMouseTop3() {
        aplicarAnimacion(Top3);
    }
    @FXML
    private void onMouseTop4() {
        aplicarAnimacion(Top4);
    }
    @FXML
    private void onMouseTop5() {
        aplicarAnimacion(Top5);
    }
    @FXML
    private void onMouseTop6() {
        aplicarAnimacion(Top6);
    }
    @FXML
    private void onMouseTop7() {
        aplicarAnimacion(Top7);
    }
    @FXML
    private void onMouseTop8() {
        aplicarAnimacion(Top8);
    }
    @FXML
    private void onMouseTop9() {
        aplicarAnimacion(Top9);
    }
    @FXML
    private void onMouseTop10() {
        aplicarAnimacion(Top10);
    }
    @FXML
    private void onMouseExitedTop1() {
        quitarAnimacion(Top1);
    }
    @FXML
    private void onMouseExitedTop2() {
        quitarAnimacion(Top2);
    }
    @FXML
    private void onMouseExitedTop3() {
        quitarAnimacion(Top3);
    }
    @FXML
    private void onMouseExitedTop4() {
        quitarAnimacion(Top4);
    }
    @FXML
    private void onMouseExitedTop5() {
        quitarAnimacion(Top5);
    }
    @FXML
    private void onMouseExitedTop6() {
        quitarAnimacion(Top6);
    }
    @FXML
    private void onMouseExitedTop7() {
        quitarAnimacion(Top7);
    }
    @FXML
    private void onMouseExitedTop8() {
        quitarAnimacion(Top8);
    }
    @FXML
    private void onMouseExitedTop9() {
        quitarAnimacion(Top9);
    }
    @FXML
    private void onMouseExitedTop10() {
        quitarAnimacion(Top10);
    }
    private void aplicarAnimacion(ImageView imageView) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), imageView);
        scaleTransition.setToX(1.2);
        scaleTransition.setToY(1.2);
        scaleTransition.play();
    }

    private void quitarAnimacion(ImageView imageView) {
        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), imageView);
        scaleTransition.setToX(1.0);
        scaleTransition.setToY(1.0);
        scaleTransition.play();
    }

    @FXML
    private void handleVolverPantallaPrincipal(ActionEvent event) throws IOException {
        Stage ventana = (Stage) Volver.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("MenuInicial.fxml"));
        Scene scene = new Scene(root);
        ventana.setTitle("GameArchive");
        ventana.setScene(scene);
        ventana.show();
    }

}
