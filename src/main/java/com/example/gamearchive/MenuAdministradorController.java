package com.example.gamearchive;

import com.example.gamearchive.DatabaseConnection.DatabaseConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;

public class MenuAdministradorController {

    @FXML
    private Button Volver;
    @FXML
    private Button AñadirJuego;
    @FXML
    private Button AddJuego;
    @FXML
    private Button ModificarJuego;
    @FXML
    private Button BorrarJuego;
    @FXML
    private Button Bienvenida;

    @FXML
    private AnchorPane PanelBienvenida;
    @FXML
    private AnchorPane PanelAñadirJuego;
    @FXML
    private AnchorPane PanelModificarJuego;
    @FXML
    private AnchorPane PanelBorrarJuego;

    @FXML
    private void ControllerPanel(ActionEvent event) {
        if (event.getSource() == AddJuego) {
            PanelAñadirJuego.setVisible(true);
            PanelModificarJuego.setVisible(false);
            PanelBorrarJuego.setVisible(false);
            PanelBienvenida.setVisible(false);
        } else if (event.getSource() == ModificarJuego) {
            PanelModificarJuego.setVisible(true);
            PanelAñadirJuego.setVisible(false);
            PanelBorrarJuego.setVisible(false);
            PanelBienvenida.setVisible(false);
        } else if (event.getSource() == BorrarJuego) {
            PanelBorrarJuego.setVisible(true);
            PanelAñadirJuego.setVisible(false);
            PanelModificarJuego.setVisible(false);
            PanelBienvenida.setVisible(false);
        }else if (event.getSource() ==PanelBienvenida) {
                PanelBienvenida.setVisible(true);
                PanelBorrarJuego.setVisible(false);
                PanelAñadirJuego.setVisible(false);
                PanelModificarJuego.setVisible(false);
            }
    }

    /*
    Añadir Juegos
     */
    @FXML
    private Button AñadirCaratulaJuego;

    private File caratulaJuegoFile;

    @FXML
    private TextField NombreJuego;

    @FXML
    private TextField Plataformas;

    @FXML
    private DatePicker FechaDeLanzamiento;

    @FXML
    private TextField Descripcion;

    @FXML
    private Button BotonAñadirJuego;


    @FXML
    private void seleccionarCaratula(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Carátula del Juego");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        caratulaJuegoFile = fileChooser.showOpenDialog(new Stage());

        if (caratulaJuegoFile != null) {
            mostrarNotificacionExito("Éxito", "La imagen se cargó correctamente.");
        }
    }

    @FXML
    private void HandleañadirJuego() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String query = "INSERT INTO Juegos (nombre, descripcion, fechaLanzamiento, rutaCaratula, plataformas) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, NombreJuego.getText());
            statement.setString(2, Descripcion.getText());
            statement.setDate(3, Date.valueOf(FechaDeLanzamiento.getValue()));

            // Copiar la imagen seleccionada a la carpeta de caratulas
            File caratulaDestino = new File("src/main/resources/caratulas/" + caratulaJuegoFile.getName());
            Files.copy(caratulaJuegoFile.toPath(), caratulaDestino.toPath(), StandardCopyOption.REPLACE_EXISTING);

            // Obtener la ruta relativa
            String rutaRelativa = "src\\main\\resources\\caratulas\\" + caratulaJuegoFile.getName();

            // Establecer la ruta de la imagen relativa a la carpeta de caratulas
            statement.setString(4, rutaRelativa); // Guarda la ruta relativa de la imagen
            statement.setString(5, Plataformas.getText()); // Agrega las plataformas

            int rowsInserted = statement.executeUpdate();
            if (rowsInserted > 0) {
                mostrarNotificacionExito("Éxito", "Juego añadido correctamente");
                System.out.println("Juego insertado exitosamente.");
                NombreJuego.clear();
                Descripcion.clear();
                FechaDeLanzamiento.setValue(null);
                Plataformas.clear();
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            mostrarNotificacion("Error", "Error al conectar a la base de datos:" + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error al copiar la imagen de la carátula: " + e.getMessage());
            mostrarNotificacion("Error", "Error al copiar la imagen de la carátula: " + e.getMessage());
        }
    }

/*
Modificar juegos
 */




    @FXML
    private void handleVolverPantallaInicial(ActionEvent event) throws IOException {
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

}
