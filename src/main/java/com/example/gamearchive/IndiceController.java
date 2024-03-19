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
    @FXML
    private Button VolverIndice;

    private static final String URL = "jdbc:mysql://localhost:3306/gamearchive?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "abc123.";
    private static Connection connection;

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
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
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
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
                String query = "SELECT idJuego,nombre, descripcion, fechaLanzamiento, rutaCaratula, plataformas FROM Juegos WHERE nombre = ?";
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

                    FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuJuego.fxml"));
                    Parent root = loader.load();
                    MenuJuegoController controller = loader.getController();
                    controller.initData(idJuego,nombreJuego, descripcion, fechaLanzamiento, rutaCaratula, plataformas);
                    Stage stage = new Stage();
                    stage.setScene(new Scene(root));
                    stage.show();

                    // Cerrar la ventana actual (índice)
                    Stage ventanaActual = (Stage) listaJuegos.getScene().getWindow();
                    ventanaActual.close();
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
