package com.example.gamearchive;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
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
    private Button Indice;
    public List<Integer> Aleatorios;
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarJuegosAgregadosRecientemente();
        cargarJuegosAleatorios();
        //


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
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);

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
            System.out.println(idJuego);
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
            System.out.println(idJuego);
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
            System.out.println(idJuego);
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
        Parent root = FXMLLoader.load(getClass().getResource("Indice.fxml"));
        Scene scene = new Scene(root);
        ventana.setScene(scene);
        ventana.show();
    }

    @FXML
    private void handleBuscar(ActionEvent event) {
        String busqueda = TextFieldBuscar.getText();
        if (!busqueda.isEmpty()) {
            try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
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
                stage.setScene(new Scene(root));
                stage.show();

                // Cerrar la ventana actual (MenuInicial)
                Stage ventanaActual = (Stage) TextFieldBuscar.getScene().getWindow();
                ventanaActual.close();

            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
    }


}
