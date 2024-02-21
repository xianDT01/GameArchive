package com.example.gamearchive;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
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
    private Label Indice;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarJuegosAgregadosRecientemente();
        cargarJuegosAleatorios();

    }
    private static final String URL = "jdbc:mysql://localhost:3306/gamearchive?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "abc123.";
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
        Stage ventana = (Stage) Volver.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("MenuPrincipal.fxml"));
        Scene scene = new Scene(root);
        ventana.setScene(scene);
        ventana.show();
    }

    private List<Integer> cargarJuegosAgregadosRecientemente() {
        List<Integer> idsJuegos = new ArrayList<>();
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

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
            connection = DriverManager.getConnection(URL, USER, PASSWORD);

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
            asignarImagenesAImageView(rutasCaratulas, Ramdom1, Ramdom2, Ramdom3);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return idsJuegos;
    }

    @FXML
    private void abrirMenuJuego(int idJuego) {
        try {
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            String query = "SELECT nombre, fechaLanzamiento, rutaCaratula FROM Juegos WHERE idJuego = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idJuego);
            ResultSet resultSet = statement.executeQuery();

            String nombreJuego = null;
            String fechaLanzamiento = null;
            String rutaCaratula = null;

            if (resultSet.next()) {
                nombreJuego = resultSet.getString("nombre");
                fechaLanzamiento = resultSet.getString("fechaLanzamiento");
                rutaCaratula = resultSet.getString("rutaCaratula");
            }

            connection.close();

            if (nombreJuego != null && fechaLanzamiento != null && rutaCaratula != null) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuJuego.fxml"));
                Parent root = loader.load();
                MenuJuegoController controller = loader.getController();
                controller.initData(nombreJuego, fechaLanzamiento, rutaCaratula);
                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.show();
            } else {
                System.out.println("No se encontró información para el juego con id: " + idJuego);
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void abrirMenuJuegoAgregadoRecientemente1() {
        abrirMenuJuegoPorIndice(0); // Abrir el primer juego agregado recientemente
    }

    @FXML
    private void abrirMenuJuegoAgregadoRecientemente2() {
        abrirMenuJuegoPorIndice(1); // Abrir el segundo juego agregado recientemente
    }

    @FXML
    private void abrirMenuJuegoAgregadoRecientemente3() {
        abrirMenuJuegoPorIndice(2); // Abrir el tercer juego agregado recientemente
    }

    @FXML
    private void abrirMenuJuegoAleatorio1() {
        List<Integer> idsJuegos = cargarJuegosAleatorios();
        if (!idsJuegos.isEmpty()) {
            int idJuego = idsJuegos.get(0);
            abrirMenuJuego(idJuego);
        } else {
            System.out.println("La lista de juegos aleatorios está vacía.");
        }
    }

    @FXML
    private void abrirMenuJuegoAleatorio2() {
        List<Integer> idsJuegos = cargarJuegosAleatorios();
        if (idsJuegos.size() > 1) {
            int idJuego = idsJuegos.get(1);
            abrirMenuJuego(idJuego);
        } else {
            System.out.println("La lista de juegos aleatorios no tiene suficientes juegos.");
        }
    }

    @FXML
    private void abrirMenuJuegoAleatorio3() {
        List<Integer> idsJuegos = cargarJuegosAleatorios();
        if (idsJuegos.size() > 2) {
            int idJuego = idsJuegos.get(2);
            abrirMenuJuego(idJuego);
        } else {
            System.out.println("La lista de juegos aleatorios no tiene suficientes juegos.");
        }
    }


    private void abrirMenuJuegoPorIndice(int indice) {
        List<Integer> idsJuegos = cargarJuegosAgregadosRecientemente();
        if (!idsJuegos.isEmpty() && idsJuegos.size() > indice) {
            int idJuego = idsJuegos.get(indice);
            abrirMenuJuego(idJuego);
        }
    }


    /*
    @FXML
    private void abrirMenuJuegoAgregadoRecientemente1() {
        List<Integer> idsJuegos = cargarJuegosAgregadosRecientemente();
        if (!idsJuegos.isEmpty()) {
            int idJuego = idsJuegos.get(0);
            abrirMenuJuego(idJuego);
        }
    }
*/

}
