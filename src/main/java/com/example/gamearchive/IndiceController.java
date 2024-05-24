package com.example.gamearchive;

import com.example.gamearchive.DatabaseConnection.DatabaseConnection;
import com.example.gamearchive.model.ControllerId;
import com.example.gamearchive.model.Juego;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import java.util.TreeMap;

public class IndiceController implements Initializable {
    @FXML
    private Button Volver;
    @FXML
    private VBox listaJuegosContainer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarNombresJuegos();
    }

    public void cargarNombresJuegos() {
        TreeMap<Character, VBox> juegosPorLetra = new TreeMap<>();

        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT nombre FROM Juegos ORDER BY nombre";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                String nombreJuego = resultSet.getString("nombre");
                char letraInicial = Character.toUpperCase(nombreJuego.charAt(0));

                juegosPorLetra.putIfAbsent(letraInicial, new VBox(5));
                Label juegoLabel = new Label(nombreJuego);
                juegoLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: white;");
                juegoLabel.setPadding(new Insets(5, 0, 5, 20));

                juegoLabel.setOnMouseEntered(event -> {
                    juegoLabel.setStyle("-fx-font-size: 16px; -fx-background-color: #555; -fx-text-fill: white;");
                });
                juegoLabel.setOnMouseExited(event -> {
                    juegoLabel.setStyle("-fx-font-size: 16px; -fx-background-color: transparent; -fx-text-fill: white;");
                });
                juegoLabel.setOnMouseClicked(event -> {
                    if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2) {
                        abrirMenuJuego(nombreJuego);
                    }
                });

                juegosPorLetra.get(letraInicial).getChildren().add(juegoLabel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mostrarJuegosPorLetra(juegosPorLetra);
    }

    private void mostrarJuegosPorLetra(TreeMap<Character, VBox> juegosPorLetra) {
        listaJuegosContainer.getChildren().clear();

        for (Character letra : juegosPorLetra.keySet()) {
            Label letraLabel = new Label(letra.toString());
            letraLabel.setStyle("-fx-font-size: 28px; -fx-font-weight: bold; -fx-text-fill: white;");
            letraLabel.setPadding(new Insets(10, 0, 10, 10));

            VBox seccion = new VBox(5);
            seccion.getChildren().add(letraLabel);
            seccion.getChildren().add(juegosPorLetra.get(letra));

            listaJuegosContainer.getChildren().add(seccion);
        }
    }

    private void abrirMenuJuego(String nombreJuegoSeleccionado) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT idJuego, nombre, descripcion, fechaLanzamiento, rutaCaratula, plataformas FROM Juegos WHERE nombre = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nombreJuegoSeleccionado);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int idJuego = resultSet.getInt("idJuego");
                String nombreJuego = resultSet.getString("nombre");
                String descripcion = resultSet.getString("descripcion");
                String fechaLanzamiento = resultSet.getString("fechaLanzamiento");
                String rutaCaratula = resultSet.getString("rutaCaratula");
                String plataformas = resultSet.getString("plataformas");
                ControllerId.setIdJuego(idJuego);
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuJuego.fxml"));
                Parent root = loader.load();
                MenuJuegoController controller = loader.getController();
                controller.initData(idJuego, nombreJuego, descripcion, fechaLanzamiento, rutaCaratula, plataformas);
                Stage stage = new Stage();
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png")));
                stage.setScene(new Scene(root));
                stage.show();

                Stage ventanaActual = (Stage) listaJuegosContainer.getScene().getWindow();
                ventanaActual.close();
            } else {
                System.out.println("No se encontró información para el juego seleccionado: " + nombreJuegoSeleccionado);
            }

        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleVolverPantallaPrincipal() throws IOException {
        Stage ventana = (Stage) Volver.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("MenuInicial.fxml"));
        Scene scene = new Scene(root);
        ventana.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png")));
        ventana.setTitle("GameArchive");
        ventana.setScene(scene);
        ventana.show();
    }
}