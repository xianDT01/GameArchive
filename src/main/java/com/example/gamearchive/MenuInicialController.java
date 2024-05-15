package com.example.gamearchive;

import com.example.gamearchive.DatabaseConnection.DatabaseConnection;
import com.example.gamearchive.model.ControllerId;
import com.example.gamearchive.model.Juego;
import com.example.gamearchive.model.SesionUsuario;
import javafx.animation.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;

import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
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
    private TextField TextFieldBuscar;
    @FXML
    private Button ButtonBuscar;
    @FXML
    private Button abrirJuegosMasValorados;
    @FXML
    private Button Foro;
    @FXML
    private Button Indice;
    @FXML
    private MenuItem MenuUsuario;
    @FXML
    private ImageView ImagenDePerfil;
    public List<Integer> Aleatorios;
    @FXML
    private ImageView GIF;
    private File[] gifFiles;
    private Random random;
    private int currentIndex;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarImagenDesdeBD();
        cargarJuegosAgregadosRecientemente();
        cargarJuegosAleatorios();
        // Carga de gifs

        // Configurar la transición secuencial
        File folder = new File("src/main/resources/sprites");
        gifFiles = folder.listFiles((dir, name) -> name.toLowerCase().endsWith(".gif"));

        // Verificar si hay archivos GIF
        if (gifFiles != null && gifFiles.length > 0) {
            // Inicializar el generador de números aleatorios
            random = new Random();

            // Obtener las dimensiones de la pantalla
            Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
            double screenWidth = screenBounds.getWidth();

            // Establecer el ajuste de la imagen al ancho de la pantalla
            GIF.setPreserveRatio(true);
            GIF.setFitWidth(screenWidth);

            // Comenzar la primera animación
            playNextAnimation();
        } else {
            System.out.println("No se encontraron archivos GIF en la carpeta especificada.");
        }

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

            // Obtener los IDs de los últimos 3 registros
            String lastThreeIdsQuery = "SELECT idJuego FROM Juegos ORDER BY idJuego DESC LIMIT 3";
            PreparedStatement lastThreeIdsStatement = connection.prepareStatement(lastThreeIdsQuery);
            ResultSet lastThreeIdsResult = lastThreeIdsStatement.executeQuery();
            List<Integer> lastThreeIds = new ArrayList<>();
            while (lastThreeIdsResult.next()) {
                lastThreeIds.add(lastThreeIdsResult.getInt("idJuego"));
            }
            // Consulta SQL para obtener juegos aleatorios excluyendo los últimos 3 registros
            String query = "SELECT idJuego, rutaCaratula FROM Juegos WHERE idJuego NOT IN (?, ?, ?) ORDER BY RAND() LIMIT 3";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, lastThreeIds.get(0));
            statement.setInt(2, lastThreeIds.get(1));
            statement.setInt(3, lastThreeIds.get(2));
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
            stage.setTitle("GameArchive");
            Image icono = new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png"));
            stage.getIcons().add(icono);
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
        ventana.setTitle("GameArchive");
        Scene scene = new Scene(root);
        ventana.setScene(scene);
        ventana.show();
    }
    @FXML
    void handleMenuUsuario(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuUsuario.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((MenuItem) event.getSource()).getParentPopup().getOwnerWindow();
            Image icono = new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png"));
            stage.getIcons().add(icono);
            stage.setTitle("GameArchive Foro");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // Método para cargar la imagen de perfil desde la base de datos y mostrarla en el ImageView
    private void cargarImagenDesdeBD() {
        // Obtener la ruta de la imagen de perfil desde la base de datos
        String rutaImagenPerfil = getRutaImagenPerfilFromDB();
        if (rutaImagenPerfil != null && !rutaImagenPerfil.isEmpty()) {
            try {
                // Cargar la imagen desde la ruta obtenida y mostrarla en el ImageView
                Image image = new Image(new FileInputStream(rutaImagenPerfil));
                ImagenDePerfil.setImage(image);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String getRutaImagenPerfilFromDB() {
        String rutaImagenPerfil = null;
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT imagen_de_perfil FROM usuarios WHERE idUsuario = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            int idUsuario = SesionUsuario.getUsuario();
            statement.setInt(1, idUsuario);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                rutaImagenPerfil = resultSet.getString("imagen_de_perfil");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rutaImagenPerfil;
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

    private void playNextAnimation() {
        // Seleccionar el siguiente archivo GIF aleatorio
        currentIndex = random.nextInt(gifFiles.length);
        File randomGifFile = gifFiles[currentIndex];

        // Cargar el GIF aleatorio
        Image image = new Image(randomGifFile.toURI().toString());

        // Establecer la imagen en la ImageView
        GIF.setImage(image);

        // Configurar la transición de desplazamiento
        TranslateTransition translateTransition = new TranslateTransition(Duration.seconds(15), GIF);
        translateTransition.setFromX(-GIF.getFitWidth()); // Comienza fuera del lado izquierdo de la pantalla
        translateTransition.setToX(GIF.getFitWidth()); // Moverse hasta el final de la pantalla

        // Configurar la transición de opacidad para que aparezca gradualmente
        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(1), GIF);
        fadeTransition.setFromValue(0.0); // Comienza con opacidad 0
        fadeTransition.setToValue(1.0); // Termina con opacidad 1

        // Iniciar la animación
        translateTransition.play();
        fadeTransition.play();

        // Detectar el final de la animación y cargar la siguiente
        translateTransition.setOnFinished(event -> {
            // Reproducir la siguiente animación después de un breve retraso
            // Esto evita problemas de visualización intermitente al cambiar de GIF
            new Thread(() -> {
                try {
                    Thread.sleep(1);
                    playNextAnimation();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
    }
    }

