package com.example.gamearchive;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class IndiceController implements Initializable {

    @FXML
    private ListView<String> listaJuegos;
    @FXML
    private Button Volver;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarNombresJuegos();

        // Agregar un listener para manejar el evento de doble clic en un elemento de la lista
        listaJuegos.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                abrirMenuJuego();
            }
        });
    }

    public void cargarNombresJuegos() {
        ObservableList<String> nombresJuegos = FXCollections.observableArrayList();

        // Conexión a la base de datos y consulta para obtener los nombres de los juegos
        try (Connection connection = DatabaseConnection.getConnection()) {
            if (connection != null) {
                String query = "SELECT nombre FROM Juegos";
                PreparedStatement statement = connection.prepareStatement(query);
                ResultSet resultSet = statement.executeQuery();

                // Iterar sobre los resultados y agregar los nombres de los juegos a la lista
                while (resultSet.next()) {
                    String nombreJuego = resultSet.getString("nombre");
                    nombresJuegos.add(nombreJuego);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Establecer la lista de nombres de juegos en el ListView
        listaJuegos.setItems(nombresJuegos);
    }

    // Método para abrir el menú del juego cuando se hace doble clic en un nombre de juego
    private void abrirMenuJuego() {
        String nombreJuegoSeleccionado = listaJuegos.getSelectionModel().getSelectedItem();
        if (nombreJuegoSeleccionado != null) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "SELECT nombre, descripcion, fechaLanzamiento, rutaCaratula FROM Juegos WHERE nombre = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, nombreJuegoSeleccionado);
                ResultSet resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    String nombreJuego = resultSet.getString("nombre");
                    String descripcion = resultSet.getString("descripcion");
                    String fechaLanzamiento = resultSet.getString("fechaLanzamiento");
                    String rutaCaratula = resultSet.getString("rutaCaratula");

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuJuego.fxml"));
                    Parent root = loader.load();
                    MenuJuegoController controller = loader.getController();
                    controller.initData(nombreJuego, descripcion, fechaLanzamiento, rutaCaratula);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();
                } else {
                    System.out.println("No se encontró información para el juego seleccionado: " + nombreJuegoSeleccionado);
                }

            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void handleVolverPantallaPrincipal(ActionEvent event) throws IOException {
        Stage ventana = (Stage) Volver.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("MenuInicial.fxml"));
        Scene scene = new Scene(root);
        ventana.setScene(scene);
        ventana.show();
    }



}
