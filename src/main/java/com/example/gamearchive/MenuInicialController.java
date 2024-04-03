package com.example.gamearchive;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class MenuInicialController implements Initializable {

    @FXML
    private Button Volver;
    @FXML
    private ImageView Agregadosrecientemente1;
    @FXML
    private ImageView Agregadosrecientemente2;
    @FXML
    private ImageView Agregadosrecientemente3;
    @FXML
    private ImageView Ramdom1;
    @FXML
    private ImageView Ramdom2;
    @FXML
    private ImageView Ramdom3;
    @FXML
    private ImageView publicidad1;
    @FXML
    private ImageView publicidad2;
    @FXML
    private TextField TextFieldBuscar;
    @FXML
    private Button ButtonBuscar;
    @FXML
    private Button abrirJuegosMasValorados;
    @FXML
    private Button Foro;
    @FXML
    private Button Indice;
    public List<Integer> Aleatorios;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarJuegosAgregadosRecientemente();
        cargarJuegosAleatorios();
        //
    }


    private static Connection connection;

    // Método para asignar las imágenes a los ImageView
    private void asignarImagenesAImageView(List<String> rutasCaratulas, ImageView... imageViews) {
        for (int i = 0; i < rutasCaratulas.size(); i++) {
            try {
                String rutaCaratula = rutasCaratulas.get(i);
                File file = new File(rutaCaratula);
                Image imagen = new Image(file.toURI().toString());
                imageViews[i].setImage(imagen);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleVolverPantallaPrincipal(ActionEvent event) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("MenuPrincipal.fxml"));
        Parent root = fxmlLoader.load();
        Scene scene = new Scene(root);
        Stage ventana = new Stage();
        ventana.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png")));
        ventana.initStyle(StageStyle.TRANSPARENT);
        ventana.setTitle("GameArchive");
        scene.setFill(Color.TRANSPARENT);
        ventana.setScene(scene);
        ventana.centerOnScreen();
        ventana.show();
        Stage ventanaActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
        ventanaActual.close();
    }

    private List<Integer> cargarJuegosAgregadosRecientemente() {
        List<Integer> idsJuegos = new ArrayList<>();
        try {
            connection = DatabaseConnection.getConnection();
            // Consulta SQL para obtener los últimos juegos agregados
            String query = "SELECT idJuego, rutaCaratula FROM Juegos ORDER BY idJuego DESC LIMIT 3";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            List<String> rutasCaratulas = new ArrayList<>();
            while (resultSet.next()) {
                int idJuego = resultSet.getInt("idJuego");
                String rutaCaratula = resultSet.getString("rutaCaratula");

                idsJuegos.add(idJuego);
                rutasCaratulas.add(rutaCaratula);
            }

            // Asignar las imágenes a los ImageView de los juegos agregados recientemente
            asignarImagenesAImageView(rutasCaratulas, Agregadosrecientemente1, Agregadosrecientemente2, Agregadosrecientemente3);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idsJuegos;
    }

    private List<Integer> cargarJuegosAleatorios() {
        List<Integer> idsJuegos = new ArrayList<>();
        try {
            Connection connection = DatabaseConnection.getConnection();

            // Consulta SQL para obtener juegos aleatorios
            String query = "SELECT idJuego, rutaCaratula FROM Juegos ORDER BY RAND() LIMIT 3";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            List<String> rutasCaratulas = new ArrayList<>();
            while (resultSet.next()) {
                int idJuego = resultSet.getInt("idJuego");
                String rutaCaratula = resultSet.getString("rutaCaratula");

                idsJuegos.add(idJuego);
                rutasCaratulas.add(rutaCaratula);

            }

            // Asignar las imágenes a los ImageView de los juegos aleatorios
            Aleatorios = idsJuegos;
            asignarImagenesAImageView(rutasCaratulas, Ramdom1, Ramdom2, Ramdom3);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idsJuegos;
    }


    @FXML
    private void abrirMenuJuego(int idJuego) {
        try {
            String query = "SELECT idJuego,nombre, descripcion, fechaLanzamiento, rutaCaratula, plataformas FROM Juegos WHERE idJuego = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idJuego);
            ResultSet resultSet = statement.executeQuery();
            int idxogo= 0;
            String nombreJuego = null;
            String fechaLanzamiento = null;
            String rutaCaratula = null;
            String descripcion = null;
            String plataformas = null;

            if (resultSet.next()) {
                idxogo = resultSet.getInt("idJuego");
                nombreJuego = resultSet.getString("nombre");
                fechaLanzamiento = resultSet.getString("fechaLanzamiento");
                rutaCaratula = resultSet.getString("rutaCaratula");
                descripcion = resultSet.getString("descripcion");
                plataformas = resultSet.getString("plataformas");
            }
            // Dar idJuego
            ControllerId.setIdJuego(idJuego);
            if (nombreJuego != null && descripcion != null && fechaLanzamiento != null && rutaCaratula != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuJuego.fxml"));
                Parent root = loader.load();
                MenuJuegoController controller = loader.getController();
                controller.initData(idJuego,nombreJuego, descripcion, fechaLanzamiento, rutaCaratula, plataformas);
                Stage stage = new Stage();
                stage.setTitle("GameArchive");
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png")));
                stage.setScene(new Scene(root));
                stage.show();
                Stage ventanaPrincipal = (Stage) Volver.getScene().getWindow();
                ventanaPrincipal.close();

            } else {
                System.out.println("No se encontró información para el juego con id: " + idJuego);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirMenuJuegoAgregadoRecientemente1() {
        abrirMenuJuegoPorIndice(0);
    }

    @FXML
    private void abrirMenuJuegoAgregadoRecientemente2() {
        abrirMenuJuegoPorIndice(1);
    }

    @FXML
    private void abrirMenuJuegoAgregadoRecientemente3() {
        abrirMenuJuegoPorIndice(2);
    }

    @FXML
    private void abrirMenuJuegoAleatorio1() {

        List<Integer> idsJuegos = Aleatorios;
        if (!idsJuegos.isEmpty()) {

            int idJuego = idsJuegos.get(0);
            abrirMenuJuego(idJuego);
        } else {
            System.out.println("La lista de juegos aleatorios está vacía.");
        }
    }

    @FXML
    private void abrirMenuJuegoAleatorio2() {

        List<Integer> idsJuegos = Aleatorios;
        if (!idsJuegos.isEmpty()) {

            int idJuego = idsJuegos.get(1);
            abrirMenuJuego(idJuego);
        } else {
            System.out.println("La lista de juegos aleatorios está vacía.");
        }
    }

    @FXML
    private void abrirMenuJuegoAleatorio3() {

        List<Integer> idsJuegos = Aleatorios;
        if (!idsJuegos.isEmpty()) {

            int idJuego = idsJuegos.get(2);
            abrirMenuJuego(idJuego);
        } else {
            System.out.println("La lista de juegos aleatorios está vacía.");
        }
    }


    private void abrirMenuJuegoPorIndice(int indice) {
        List<Integer> idsJuegos = cargarJuegosAgregadosRecientemente();
        if (!idsJuegos.isEmpty() && idsJuegos.size() > indice) {
            int idJuego = idsJuegos.get(indice);
            abrirMenuJuego(idJuego);
        }
    }

    @FXML
    private void handleIrAlIndice(ActionEvent event) throws IOException {
        Stage ventana = (Stage) Indice.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Indice.fxml"));
        Parent root = fxmlLoader.load();
        Image icono = new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png"));
        ventana.getIcons().add(icono);
        ventana.setTitle("GameArchive");
        Scene scene = new Scene(root);
        ventana.setScene(scene);
        ventana.show();
    }


    @FXML
    private void handleBuscar(ActionEvent event) {
        String busqueda = TextFieldBuscar.getText();
        if (!busqueda.isEmpty()) {
            try (Connection connection =DatabaseConnection.getConnection()) {
                String query = "SELECT idJuego,nombre, descripcion, fechaLanzamiento, rutaCaratula, plataformas FROM Juegos WHERE nombre LIKE ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, "%" + busqueda + "%");
                ResultSet resultSet = statement.executeQuery();

                List<Juego> juegosEncontrados = new ArrayList<>();
                while (resultSet.next()) {
                    int idJuego = resultSet.getInt("idJuego");
                    String nombreJuego = resultSet.getString("nombre");
                    String descripcion = resultSet.getString("descripcion");
                    String fechaLanzamiento = resultSet.getString("fechaLanzamiento");
                    String rutaCaratula = resultSet.getString("rutaCaratula");
                    String plataformas = resultSet.getString("plataformas");
                    juegosEncontrados.add(new Juego(idJuego,nombreJuego, descripcion, fechaLanzamiento,rutaCaratula,plataformas));
                }

                FXMLLoader loader = new FXMLLoader(getClass().getResource("ResultadosBusqueda.fxml"));
                Parent root = loader.load();
                ResultadosBusquedaController controller = loader.getController();
                controller.mostrarResultados(juegosEncontrados);
                Stage stage = new Stage();
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png")));
                stage.setScene(new Scene(root));
                stage.setTitle("GameArchive");
                stage.show();
                Stage ventanaActual = (Stage) TextFieldBuscar.getScene().getWindow();
                ventanaActual.close();

            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void abrirJuegosMasValorados(ActionEvent event) {
        try {
            Stage ventanaActual = (Stage) ((Node) event.getSource()).getScene().getWindow();
            ventanaActual.close();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("JuegosMasValorados.fxml"));
            Parent root = loader.load();
            JuegosMasValoradosController controller = loader.getController();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void abrirForo(ActionEvent event) throws IOException {
        Stage ventana = (Stage) Foro.getScene().getWindow();
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Foro.fxml"));
        Parent root = fxmlLoader.load();
        Image icono = new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png"));
        ventana.getIcons().add(icono);
        ventana.setTitle("GameArchive Foro");
        Scene scene = new Scene(root);
        ventana.setScene(scene);
        ventana.show();
    }

    @FXML
    private void onMouseEnteredAgregadosrecientemente1() {
        aplicarAnimacion(Agregadosrecientemente1);
    }

    @FXML
    private void onMouseExitedAgregadosrecientemente1() {
        quitarAnimacion(Agregadosrecientemente1);
    }

    @FXML
    private void onMouseEnteredAgregadosrecientemente2() {
        aplicarAnimacion(Agregadosrecientemente2);
    }

    @FXML
    private void onMouseExitedAgregadosrecientemente2() {
        quitarAnimacion(Agregadosrecientemente2);
    }

    @FXML
    private void onMouseEnteredAgregadosrecientemente3() {
        aplicarAnimacion(Agregadosrecientemente3);
    }

    @FXML
    private void onMouseExitedAgregadosrecientemente3() {
        quitarAnimacion(Agregadosrecientemente3);
    }

    @FXML
    private void onMouseEnteredRamdom1() {
        aplicarAnimacion(Ramdom1);
    }

    @FXML
    private void onMouseExitedRamdom1() {
        quitarAnimacion(Ramdom1);
    }

    @FXML
    private void onMouseEnteredRamdom2() {
        aplicarAnimacion(Ramdom2);
    }

    @FXML
    private void onMouseExitedRamdom2() {
        quitarAnimacion(Ramdom2);
    }

    @FXML
    private void onMouseEnteredRamdom3() {
        aplicarAnimacion(Ramdom3);
    }

    @FXML
    private void onMouseExitedRamdom3() {
        quitarAnimacion(Ramdom3);
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



}
