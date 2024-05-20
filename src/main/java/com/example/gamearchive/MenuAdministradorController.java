package com.example.gamearchive;

import com.example.gamearchive.DatabaseConnection.DatabaseConnection;
import com.example.gamearchive.model.Juego;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class MenuAdministradorController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarNombresJuegos();
        cargarNombresJuegos2();

        nombreJuegos.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            buscarJuego(newValue); // Escuchar cambios en el texto del ComboBox
        });

        // Agregar evento para filtrar los resultados al hacer clic en el ComboBox
        nombreJuegos.setOnMouseClicked(event -> {
            if (!nombreJuegos.isShowing()) {
                actualizarComboBox();
            }
        });

        // Agregar un listener al campo de texto para actualizar el ComboBox automáticamente
        nombreJuegos.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!nombreJuegos.isShowing()) {
                actualizarComboBox();
            }
        });

        // Borrar juego
        cargarNombresJuegos2();
        NombreJuegos.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            buscarJuego2(newValue);
        });
        NombreJuegos.setOnMouseClicked(event -> {
            if (!NombreJuegos.isShowing()) {
                actualizarComboBox2();
            }
        });
        NombreJuegos.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            buscarJuego2(newValue);
        });
        NombreJuegos.setOnMouseClicked(event -> {
            if (!NombreJuegos.isShowing()) {
                actualizarComboBox();
            }
        });

        NombreJuegos.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.isEmpty()) {
                buscarJuego2(newValue);
            }
        });
        NombreJuegos.getEditor().textProperty().addListener((observable, oldValue, newValue) -> {
            if (!NombreJuegos.isShowing()) {
                actualizarComboBox2();
            }
        });

    }

    // Método para cargar nombres de juegos filtrados según el texto de búsqueda
    private void cargarNombresJuegosFiltrados(String textoBusqueda) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT nombre FROM Juegos WHERE nombre LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + textoBusqueda + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String nombreJuego = resultSet.getString("nombre");
                nombreJuegos.getItems().add(nombreJuego);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void buscarJuego(String nombreJuego) {
        if (nombreJuego != null && !nombreJuego.isEmpty()) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "SELECT * FROM Juegos WHERE nombre LIKE ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, "%" + nombreJuego + "%");
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    ModificarNombreJuego.setText(resultSet.getString("nombre"));
                    ModificarDescripcion.setText(resultSet.getString("descripcion"));
                    ModificarFechaDeLanzamiento.setValue(resultSet.getDate("fechaLanzamiento").toLocalDate());
                    ModificarPlataformas.setText(resultSet.getString("plataformas"));
                } else {
                    // Limpiar os campos si nin encontra xogos
                    ModificarNombreJuego.clear();
                    ModificarDescripcion.clear();
                    ModificarFechaDeLanzamiento.setValue(null);
                    ModificarPlataformas.clear();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else {
            // Limpia os campos si o texto está vacio
            ModificarNombreJuego.clear();
            ModificarDescripcion.clear();
            ModificarFechaDeLanzamiento.setValue(null);
            ModificarPlataformas.clear();
        }
    }
    private void actualizarComboBox() {
        nombreJuegos.getItems().clear();
        String textoBusqueda = nombreJuegos.getEditor().getText();
        if (!textoBusqueda.isEmpty()) {
            cargarNombresJuegosFiltrados(textoBusqueda);
        } else {
            cargarNombresJuegos();
        }
    }
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
        } else if (event.getSource() == Bienvenida) {
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
    private Label RutaImagen;
    @FXML
    private ImageView ImagenJuego;


    @FXML
    private void seleccionarCaratula(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Carátula del Juego");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        caratulaJuegoFile = fileChooser.showOpenDialog(new Stage());

        if (caratulaJuegoFile != null) {
            RutaImagen.setText(caratulaJuegoFile.getAbsolutePath());
            mostrarNotificacionExito("Éxito", "La imagen se cargó correctamente.");

            // Cargar la imagen en el ImageView
            Image image = new Image(caratulaJuegoFile.toURI().toString());
            ImagenJuego.setImage(image);
        }
    }


    @FXML
    private void HandleañadirJuego() {
        if (NombreJuego.getText().isEmpty() || Descripcion.getText().isEmpty() || FechaDeLanzamiento.getValue() == null || caratulaJuegoFile == null || Plataformas.getText().isEmpty()) {
            mostrarNotificacion("Error", "Por favor, complete todos los campos.");
            return; // Sale del método si algún campo está vacío
        }

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
                AñadirCaratulaJuego = null;
            }
            cargarNombresJuegos2();
            cargarNombresJuegos();
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
    private ComboBox nombreJuegos;
    @FXML
    private Button ModificarcaratulaJuego;
    @FXML
    private File ModificarcaratulaJuegoFile;

    @FXML
    private TextField ModificarNombreJuego;

    @FXML
    private TextField ModificarPlataformas;

    @FXML
    private DatePicker ModificarFechaDeLanzamiento;

    @FXML
    private TextField ModificarDescripcion;
    @FXML
    private Button guardar;

    @FXML
    private Label RutaImagen2;
    @FXML
    private ImageView ModificarImagenJuego;
    public static final String RUTA_POR_DEFECTO = "src\\main\\resources\\caratulas\\caratula.jpg";


    @FXML
    private void ModificarCaratula(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Carátula del Juego");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );

        caratulaJuegoFile = fileChooser.showOpenDialog(new Stage());

        if (caratulaJuegoFile != null) {
            RutaImagen2.setText(caratulaJuegoFile.getAbsolutePath());
            ModificarcaratulaJuegoFile = caratulaJuegoFile; // Asignar el archivo seleccionado
            mostrarNotificacionExito("Éxito", "La imagen se cargó correctamente.");
        }
    }


    private void cargarNombresJuegos() {
        if (!nombreJuegos.getItems().isEmpty()) {
            nombreJuegos.getItems().clear();
        }
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT nombre FROM Juegos";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String nombreJuego = resultSet.getString("nombre");
                nombreJuegos.getItems().add(nombreJuego);
            }
        } catch (SQLException e) {
            e.printStackTrace();

        }
    }

    @FXML
    private void seleccionarJuego() {
        String nombreJuegoSeleccionado = (String) nombreJuegos.getValue();
        if (nombreJuegoSeleccionado != null) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "SELECT * FROM Juegos WHERE nombre = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, nombreJuegoSeleccionado);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    ModificarNombreJuego.setText(resultSet.getString("nombre"));
                    ModificarDescripcion.setText(resultSet.getString("descripcion"));
                    ModificarFechaDeLanzamiento.setValue(resultSet.getDate("fechaLanzamiento").toLocalDate());
                    ModificarPlataformas.setText(resultSet.getString("plataformas"));

                    // Obtener la ruta de la carátula del juego seleccionado
                    String rutaCaratula = resultSet.getString("rutaCaratula");
                    if (rutaCaratula != null) {
                        // Cargar la imagen en el ImageView
                        Image imagenCaratula = new Image(new File(rutaCaratula).toURI().toString());
                        ModificarImagenJuego.setImage(imagenCaratula);
                    } else {
                        // Si no hay ruta de carátula, cargar una imagen por defecto o dejar el ImageView vacío
                        // Aquí puedes establecer una imagen por defecto o dejar el ImageView vacío, según tus preferencias
                        ModificarImagenJuego.setImage(null); // Esto deja el ImageView vacío
                        // ModificarImagenJuego.setImage(new Image(getClass().getResourceAsStream("ruta/a/imagen/por/defecto.jpg"))); // Esto establece una imagen por defecto
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        cargarNombresJuegos2();
    }


    @FXML
    private void guardarCambios() {
        String nombreJuegoSeleccionado = (String) nombreJuegos.getValue();
        if (nombreJuegoSeleccionado != null) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "UPDATE Juegos SET nombre = ?, descripcion = ?, fechaLanzamiento = ?, plataformas = ?, rutaCaratula = ? WHERE nombre = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, ModificarNombreJuego.getText());
                statement.setString(2, ModificarDescripcion.getText());
                statement.setDate(3, Date.valueOf(ModificarFechaDeLanzamiento.getValue()));
                statement.setString(4, ModificarPlataformas.getText());

                // Si se ha seleccionado una nueva carátula, actualizar la ruta de la carátula
                if (ModificarcaratulaJuegoFile != null) {
                    // Copiar la nueva carátula al directorio de recursos del proyecto
                    Path destino = Paths.get("src\\main\\resources\\caratulas\\", ModificarcaratulaJuegoFile.getName());
                    Files.copy(ModificarcaratulaJuegoFile.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);

                    // Obtener la nueva ruta de la carátula
                    String rutaNuevaCaratula = "src\\main\\resources\\caratulas\\" + ModificarcaratulaJuegoFile.getName();
                    statement.setString(5, rutaNuevaCaratula);
                } else {
                    // Si no se seleccionó una nueva carátula, mantener la ruta existente
                    statement.setString(5, obtenerRutaCaratulaActual(nombreJuegoSeleccionado));
                }

                statement.setString(6, nombreJuegoSeleccionado);
                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated > 0) {
                    mostrarNotificacionExito("Éxito", "Cambios guardados correctamente");
                }

            } catch (SQLException | IOException e) {
                e.printStackTrace();
            }
        }
        cargarNombresJuegos();
        cargarNombresJuegos2();
    }


    public static String obtenerRutaCaratulaActual(String nombreJuego) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT rutaCaratula FROM Juegos WHERE nombre = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nombreJuego);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getString("rutaCaratula");
            } else {
                // Si no se encuentra la carátula, da una por defecto. Mirar que caratula da por defecto, si no da null o en blanco
                return RUTA_POR_DEFECTO;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @FXML
    private ComboBox NombreJuegos;
    @FXML
    private Label mostrarJuego;
    @FXML
    private Button borrarJuego;

    private void cargarNombresJuegos2() {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT nombre FROM Juegos";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            // Crear una nueva lista para almacenar los nombres de los juegos
            List<String> nombresJuegos = new ArrayList<>();
            while (resultSet.next()) {
                String nombreJuego = resultSet.getString("nombre");
                nombresJuegos.add(nombreJuego);
            }

            // Actualizar la lista en el ComboBox
            Platform.runLater(() -> {
                NombreJuegos.getItems().setAll(nombresJuegos);
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void seleccionarJuegoNombreJuego() {
        String nombreJuegoSeleccionado = (String) NombreJuegos.getValue();
        if (nombreJuegoSeleccionado != null) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String query = "SELECT * FROM Juegos WHERE nombre = ?";
                PreparedStatement statement = connection.prepareStatement(query);
                statement.setString(1, nombreJuegoSeleccionado);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    // Mostrar la información del juego seleccionado en el label
                    String informacionJuego = "Nombre: " + resultSet.getString("nombre") + "\n" +
                            "Descripción: " + resultSet.getString("descripcion") + "\n" +
                            "Fecha de lanzamiento: " + resultSet.getDate("fechaLanzamiento") + "\n" +
                            "Plataformas: " + resultSet.getString("plataformas");
                    mostrarJuego.setText(informacionJuego);
                }

                // Actualizar el texto del editor del ComboBox con el nombre del juego seleccionado
                Platform.runLater(() -> {
                    NombreJuegos.getEditor().setText(nombreJuegoSeleccionado);
                });
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    private void buscarJuego2(String texto) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT nombre FROM Juegos WHERE nombre LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + texto + "%"); // Usar "%" para buscar coincidencias parciales
            ResultSet resultSet = statement.executeQuery();

            // Crear una nueva lista para almacenar los resultados de la búsqueda
            List<String> resultados = new ArrayList<>();
            while (resultSet.next()) {
                String nombreJuego = resultSet.getString("nombre");
                resultados.add(nombreJuego);
            }

            // Actualizar la lista en el ComboBox
            Platform.runLater(() -> {
                NombreJuegos.getItems().setAll(resultados);
                NombreJuegos.show(); // Mostrar el desplegable con los resultados de la búsqueda
            });
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    private void actualizarComboBox2() {
        NombreJuegos.getItems().clear(); // Limpiar los elementos del ComboBox
        String textoBusqueda = NombreJuegos.getEditor().getText();
        if (!textoBusqueda.isEmpty()) {
            cargarNombresJuegosFiltrados(textoBusqueda);
        } else {
            cargarNombresJuegos(); // Si no hay texto de búsqueda, cargar todos los juegos
        }
    }
    private void cargarNombresJuegosFiltrados2(String textoBusqueda) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT nombre FROM Juegos WHERE nombre LIKE ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, "%" + textoBusqueda + "%");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                String nombreJuego = resultSet.getString("nombre");
                NombreJuegos.getItems().add(nombreJuego);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void borrarJuego() {
        String nombreJuegoSeleccionado = (String) NombreJuegos.getValue();
        if (nombreJuegoSeleccionado != null) {
            // Mostrar un cuadro de diálogo de confirmación
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Confirmar eliminación");
            alert.setHeaderText("¿Estás seguro de que deseas eliminar este juego?");
            alert.setContentText("Esta acción eliminará el juego seleccionado junto con todos los comentarios y reseñas asociados.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try (Connection connection = DatabaseConnection.getConnection()) {
                    // Obtenemos el ID del juego
                    int idJuego = obtenerIdJuego(nombreJuegoSeleccionado);

                    // Eliminar los comentarios asociados al juego
                    String eliminarComentariosQuery = "DELETE FROM Comentarios WHERE idJuego = ?";
                    PreparedStatement eliminarComentariosStatement = connection.prepareStatement(eliminarComentariosQuery);
                    eliminarComentariosStatement.setInt(1, idJuego);
                    eliminarComentariosStatement.executeUpdate();

                    // Eliminar las reseñas asociadas al juego
                    String eliminarReseñasQuery = "DELETE FROM Reseñas WHERE idJuego = ?";
                    PreparedStatement eliminarReseñasStatement = connection.prepareStatement(eliminarReseñasQuery);
                    eliminarReseñasStatement.setInt(1, idJuego);
                    eliminarReseñasStatement.executeUpdate();

                    // Eliminar el juego
                    String eliminarJuegoQuery = "DELETE FROM Juegos WHERE nombre = ?";
                    PreparedStatement eliminarJuegoStatement = connection.prepareStatement(eliminarJuegoQuery);
                    eliminarJuegoStatement.setString(1, nombreJuegoSeleccionado);
                    int rowsDeleted = eliminarJuegoStatement.executeUpdate();
                    if (rowsDeleted > 0) {
                        mostrarNotificacionExito("Éxito", "Juego eliminado correctamente");
                        // Limpiar los campos o actualizar la lista de juegos en el ComboBox si es necesario
                    }
                    cargarNombresJuegos();
                    cargarNombresJuegos2();
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Manejo de errores
                }
            }
        }
    }



    private int obtenerIdJuego(String nombreJuego) throws SQLException {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT idJuego FROM Juegos WHERE nombre = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, nombreJuego);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getInt("idJuego");
            } else {
                return -1; // Si no se encuentra el juego
            }
        }
    }



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