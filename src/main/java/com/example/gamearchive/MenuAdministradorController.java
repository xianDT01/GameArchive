package com.example.gamearchive;

import com.example.gamearchive.DatabaseConnection.DatabaseConnection;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class MenuAdministradorController implements Initializable {
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        handleViewComentarios();
        handleViewForos();
        cargarNombresJuegos();

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
            if (!NombreJuegos.isShowing()) {
                actualizarComboBox2();
            }
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
    private Button AñadirAdmin;
    @FXML
    private Button AdministraUsuarios;
    @FXML
    private Button ModerarForo;

    @FXML
    private AnchorPane PanelBienvenida;
    @FXML
    private AnchorPane PanelAñadirJuego;
    @FXML
    private AnchorPane PanelModificarJuego;
    @FXML
    private AnchorPane PanelBorrarJuego;
    @FXML
    private AnchorPane PanelAñadirAdmin;
    @FXML
    private AnchorPane PanelAdministrarUsuarios;
    @FXML
    private AnchorPane PanelModerarForo;


    @FXML
    private void ControllerPanel(ActionEvent event) {
        if (event.getSource() == AddJuego) {
            PanelAñadirJuego.setVisible(true);
            PanelModificarJuego.setVisible(false);
            PanelBorrarJuego.setVisible(false);
            PanelBienvenida.setVisible(false);
            PanelAñadirAdmin.setVisible(false);
            PanelAdministrarUsuarios.setVisible(false);
            PanelModerarForo.setVisible(false);
        } else if (event.getSource() == ModificarJuego) {
            PanelModificarJuego.setVisible(true);
            PanelAñadirJuego.setVisible(false);
            PanelBorrarJuego.setVisible(false);
            PanelBienvenida.setVisible(false);
            PanelAñadirAdmin.setVisible(false);
            PanelAdministrarUsuarios.setVisible(false);
            PanelModerarForo.setVisible(false);
        } else if (event.getSource() == BorrarJuego) {
            PanelBorrarJuego.setVisible(true);
            PanelAñadirJuego.setVisible(false);
            PanelModificarJuego.setVisible(false);
            PanelBienvenida.setVisible(false);
            PanelAñadirAdmin.setVisible(false);
            PanelAdministrarUsuarios.setVisible(false);
            PanelModerarForo.setVisible(false);
        } else if (event.getSource() == Bienvenida) {
            PanelBienvenida.setVisible(true);
            PanelBorrarJuego.setVisible(false);
            PanelAñadirJuego.setVisible(false);
            PanelModificarJuego.setVisible(false);
            PanelAñadirAdmin.setVisible(false);
            PanelAdministrarUsuarios.setVisible(false);
            PanelModerarForo.setVisible(false);
        } else if (event.getSource() == AñadirAdmin) {
            PanelBienvenida.setVisible(false);
            PanelBorrarJuego.setVisible(false);
            PanelAñadirJuego.setVisible(false);
            PanelModificarJuego.setVisible(false);
            PanelAñadirAdmin.setVisible(true);
            PanelAdministrarUsuarios.setVisible(false);
            PanelModerarForo.setVisible(false);
        } else if (event.getSource() == AdministraUsuarios) {
            PanelBienvenida.setVisible(false);
            PanelBorrarJuego.setVisible(false);
            PanelAñadirJuego.setVisible(false);
            PanelModificarJuego.setVisible(false);
            PanelAñadirAdmin.setVisible(false);
            PanelAdministrarUsuarios.setVisible(true);
            PanelModerarForo.setVisible(false);
        } else if (event.getSource() == ModerarForo) {
            PanelBienvenida.setVisible(false);
            PanelBorrarJuego.setVisible(false);
            PanelAñadirJuego.setVisible(false);
            PanelModificarJuego.setVisible(false);
            PanelAñadirAdmin.setVisible(false);
            PanelAdministrarUsuarios.setVisible(false);
            PanelModerarForo.setVisible(true);
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
    private TextArea Descripcion;

    @FXML
    private Button BotonAñadirJuego;

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


                // Cargar la imagen por defecto en el ImageView
                Image defaultImage = new Image("file:src/main/resources/caratulas/default.png");
                ImagenJuego.setImage(defaultImage);

                caratulaJuegoFile = null; // Reiniciar el archivo de caratula
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
    private TextArea ModificarDescripcion;
    @FXML
    private Button guardar;

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
    private Label mostrarNombreJuego;
    @FXML
    private Label mostrarPlataformasJuego;
    @FXML
    private Label mostrarDescripcionJuego;
    @FXML
    private Label mostrarFechaLanzamientoJuego;
    @FXML
    private ImageView mostrarCatula;

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

                    String nombre = resultSet.getString("nombre");
                    String plataformas = resultSet.getString("plataformas");
                    String fechaLanzamiento = resultSet.getString("fechaLanzamiento");
                    String descripcion = resultSet.getString("descripcion");

                    mostrarNombreJuego.setText(nombre);
                    mostrarPlataformasJuego.setText(plataformas);
                    mostrarDescripcionJuego.setText(descripcion);
                    mostrarFechaLanzamientoJuego.setText(fechaLanzamiento);
                }
                String rutaCaratula = resultSet.getString("rutaCaratula");
                if (rutaCaratula != null && !rutaCaratula.isEmpty()) {
                    try {
                        Image imagenCaratula = new Image("file:" + rutaCaratula);
                        mostrarCatula.setImage(imagenCaratula);
                    } catch (Exception e) {
                        e.printStackTrace();
                        mostrarCatula.setImage(null);
                    }
                } else {
                    mostrarCatula.setImage(null);
                }

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

                        // Limpiar los campos y la imagen
                        mostrarNombreJuego.setText("");
                        mostrarPlataformasJuego.setText("");
                        mostrarDescripcionJuego.setText("");
                        mostrarFechaLanzamientoJuego.setText("");

                        // Cargar la imagen por defecto en el ImageView
                        Image defaultImage = new Image("file:src/main/resources/caratulas/default.png");
                        mostrarCatula.setImage(defaultImage);

                        cargarNombresJuegos();
                        cargarNombresJuegos2();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    mostrarNotificacion("Error", "Error al eliminar el juego: " + e.getMessage());
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
    private TextField nombreUsuario;
    @FXML
    private TextField correoElectronico;
    @FXML
    private PasswordField contraseña;
    @FXML
    private PasswordField RepetirContraseña;
    @FXML
    private Button Registrar;

    @FXML
    public void handleBotonRegistarUsuario(ActionEvent event) {
        String nombre = nombreUsuario.getText().trim();
        String correo = correoElectronico.getText().trim();
        String pass = contraseña.getText().trim();
        String repeatPass = RepetirContraseña.getText().trim();

        if (nombre.isEmpty() || correo.isEmpty() || pass.isEmpty() || repeatPass.isEmpty()) {
            mostrarNotificacion("Error", "Todos los campos son obligatorios.");
            return;
        }

        if (!pass.equals(repeatPass)) {
            mostrarNotificacion("Error", "Las contraseñas no coinciden.");
            return;
        }

        if (!correo.matches("\\b[\\w.%-]+@[-.\\w]+\\.[A-Za-z]{2,4}\\b")) {
            mostrarNotificacion("Error", "El correo electrónico no tiene un formato válido.");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Verificar si el nombre de usuario ya está en uso
            String queryUsuario = "SELECT COUNT(*) AS count FROM Usuarios WHERE nombre = ?";
            PreparedStatement statementUsuario = connection.prepareStatement(queryUsuario);
            statementUsuario.setString(1, nombre);
            ResultSet resultSetUsuario = statementUsuario.executeQuery();
            if (resultSetUsuario.next() && resultSetUsuario.getInt("count") > 0) {
                mostrarNotificacion("Error", "El nombre de usuario ya está en uso. Por favor, elija otro nombre de usuario.");
                return;
            }

            // Verificar si el correo electrónico ya está en uso
            String queryCorreo = "SELECT COUNT(*) AS count FROM Usuarios WHERE correo = ?";
            PreparedStatement statementCorreo = connection.prepareStatement(queryCorreo);
            statementCorreo.setString(1, correo);
            ResultSet resultSetCorreo = statementCorreo.executeQuery();
            if (resultSetCorreo.next() && resultSetCorreo.getInt("count") > 0) {
                mostrarNotificacion("Error", "El correo electrónico ya está en uso. Por favor, utilice otro correo electrónico.");
                return;
            }

            // Insertar el nuevo usuario en la base de datos
            String queryInsert = "INSERT INTO Usuarios (nombre, correo, contraseña, tipo_usuario) VALUES (?, ?, ?, 'administrador')";
            PreparedStatement statementInsert = connection.prepareStatement(queryInsert);
            statementInsert.setString(1, nombre);
            statementInsert.setString(2, correo);
            statementInsert.setString(3, pass);
            int rowsInserted = statementInsert.executeUpdate();
            if (rowsInserted > 0) {
                mostrarNotificacionExito("Éxito", "Usuario registrado exitosamente.");

                nombreUsuario.clear();
                correoElectronico.clear();
                contraseña.clear();
                RepetirContraseña.clear();
            }
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
            mostrarNotificacion("Error", "Error al registrar el usuario. Por favor, inténtelo de nuevo.");
        }
    }

    // Banear usuarios

    @FXML
    private TextField searchField;
    @FXML
    private VBox resultBox;

    @FXML
    public void handleSearch() {
        String searchTerm = searchField.getText();
        resultBox.getChildren().clear();

        String query = "SELECT idUsuario, nombre, correo, estaBaneado, tipo_usuario FROM Usuarios WHERE nombre LIKE ? OR correo LIKE ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, "%" + searchTerm + "%");
            statement.setString(2, "%" + searchTerm + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int userId = resultSet.getInt("idUsuario");
                String nombre = resultSet.getString("nombre");
                String correo = resultSet.getString("correo");
                AtomicBoolean estaBaneado = new AtomicBoolean(resultSet.getBoolean("estaBaneado"));
                String tipoUsuario = resultSet.getString("tipo_usuario");

                HBox userBox = new HBox(10);
                userBox.getStyleClass().add("hbox");

                Label userInfo = new Label("Nombre: " + nombre + " | Correo: " + correo + " | Baneado: " + (estaBaneado.get() ? "Sí" : "No") + " | Tipo: " + tipoUsuario);
                userInfo.getStyleClass().add("label");

                Button banButton = new Button(estaBaneado.get() ? "Desbanear" : "Banear");
                banButton.getStyleClass().add("button");

                // Disable ban button for administrators
                if ("administrador".equals(tipoUsuario)) {
                    banButton.setDisable(true);
                }

                banButton.setOnAction(event -> {
                    if (estaBaneado.get()) {
                        desbanearUsuario(userId);
                        // Update the local estaBaneado flag and UI components
                        estaBaneado.set(false);
                        banButton.setText("Banear");
                        userInfo.setText("Nombre: " + nombre + " | Correo: " + correo + " | Baneado: No" + " | Tipo: " + tipoUsuario);
                    } else {
                        String motivo = solicitarMotivo();
                        if (motivo != null) {
                            banearUsuario(userId, motivo);
                            // Update the local estaBaneado flag and UI components
                            estaBaneado.set(true);
                            banButton.setText("Desbanear");
                            userInfo.setText("Nombre: " + nombre + " | Correo: " + correo + " | Baneado: Sí" + " | Tipo: " + tipoUsuario);
                        }
                    }
                });

                userBox.getChildren().addAll(userInfo, banButton);
                resultBox.getChildren().add(userBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void banearUsuario(int userId, String motivo) {
        String updateQuery = "UPDATE Usuarios SET estaBaneado = TRUE WHERE idUsuario = ?";
        String insertQuery = "INSERT INTO Baneos (idUsuario, motivo) VALUES (?, ?)";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
             PreparedStatement insertStatement = connection.prepareStatement(insertQuery)) {

            connection.setAutoCommit(false);  // Start transaction

            // Update user status to banned
            updateStatement.setInt(1, userId);
            updateStatement.executeUpdate();

            // Insert ban reason
            insertStatement.setInt(1, userId);
            insertStatement.setString(2, motivo);
            insertStatement.executeUpdate();

            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void desbanearUsuario(int userId) {
        String query = "UPDATE Usuarios SET estaBaneado = FALSE WHERE idUsuario = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, userId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private String solicitarMotivo() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Motivo del baneo");
        dialog.setHeaderText("Ingrese el motivo del baneo");
        dialog.setContentText("Motivo:");
        Optional<String> result = dialog.showAndWait();
        return result.orElse(null);
    }

    // Administrar Foro


    @FXML
    private TextField buscarCampo;
    @FXML
    private VBox cajaResultados;

    @FXML
    public void handleSearchForoMessages() {
        String searchTerm = buscarCampo.getText();
        cajaResultados.getChildren().clear();

        String query = "SELECT fc.idComentario, fc.comentario, f.titulo " +
                "FROM ForoComentarios fc " +
                "JOIN Usuarios u ON fc.idUsuario = u.idUsuario " +
                "JOIN Foro f ON fc.idForo = f.idForo " +
                "WHERE u.nombre LIKE ? OR u.correo LIKE ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, "%" + searchTerm + "%");
            statement.setString(2, "%" + searchTerm + "%");
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int comentarioId = resultSet.getInt("idComentario");
                String comentario = resultSet.getString("comentario");
                String tituloForo = resultSet.getString("titulo");

                HBox commentBox = new HBox(10);
                Label commentInfo = new Label("Foro: " + tituloForo + " | Comentario: " + comentario);
                Button deleteButton = new Button("Eliminar");

                deleteButton.setOnAction(event -> {
                    eliminarComentarioForo(comentarioId);
                    cajaResultados.getChildren().remove(commentBox);
                });

                commentBox.getChildren().addAll(commentInfo, deleteButton);
                cajaResultados.getChildren().add(commentBox);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Administrar foro
    private void eliminarComentarioForo(int comentarioId) {
        String query = "DELETE FROM ForoComentarios WHERE idComentario = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, comentarioId);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private VBox foroListBox;


    @FXML
    private void handleViewForos() {
        // Limpia la interfaz
        cajaResultados.getChildren().clear();

        String query = "SELECT idForo, titulo, descripcion FROM Foro";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int foroId = resultSet.getInt("idForo");
                String titulo = resultSet.getString("titulo");
                String descripcion = resultSet.getString("descripcion");

                HBox foroItem = new HBox(10); // Añadir espacio entre elementos
                Text foroText = new Text("Foro: " + titulo + " - " + descripcion);
                Button deleteButton = new Button("Eliminar");
                deleteButton.setOnAction(event -> eliminarForo(foroId));

                foroItem.getChildren().addAll(foroText, deleteButton);
                foroItem.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");
                cajaResultados.getChildren().add(foroItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleViewComentarios() {
        // Limpia la interfaz
        cajaResultados.getChildren().clear();

        String query = "SELECT c.idComentario, c.comentario, c.idForo, u.nombre, u.correo " +
                "FROM forocomentarios c " +
                "JOIN usuarios u ON c.idUsuario = u.idUsuario";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                int comentarioId = resultSet.getInt("idComentario");
                String comentario = resultSet.getString("comentario");
                int foroId = resultSet.getInt("idForo");
                String nombre = resultSet.getString("nombre");
                String correo = resultSet.getString("correo");

                HBox comentarioItem = new HBox(10); // Añadir espacio entre elementos
                Text comentarioText = new Text("Comentario: " + comentario + " (Foro ID: " + foroId + ")");
                Text usuarioText = new Text("Usuario: " + nombre + " - " + correo);
                Button deleteButton = new Button("Eliminar");
                deleteButton.setOnAction(event -> eliminarComentario(comentarioId));

                VBox comentarioBox = new VBox(5); // Caja vertical para agrupar comentarios y detalles de usuario
                comentarioBox.getChildren().addAll(comentarioText, usuarioText);

                comentarioItem.getChildren().addAll(comentarioBox, deleteButton);
                comentarioItem.setStyle("-fx-padding: 10; -fx-border-color: black; -fx-border-width: 1; -fx-border-radius: 5;");
                cajaResultados.getChildren().add(comentarioItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarForo(int idForo) {
        String deleteCommentsQuery = "DELETE FROM forocomentarios WHERE idForo = ?";
        String deleteForoQuery = "DELETE FROM foro WHERE idForo = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement deleteCommentsStmt = connection.prepareStatement(deleteCommentsQuery);
             PreparedStatement deleteForoStmt = connection.prepareStatement(deleteForoQuery)) {

            // Eliminar comentarios asociados al foro
            deleteCommentsStmt.setInt(1, idForo);
            deleteCommentsStmt.executeUpdate();

            // Eliminar el foro
            deleteForoStmt.setInt(1, idForo);
            deleteForoStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void eliminarComentario(int idComentario) {
        String deleteComentarioQuery = "DELETE FROM forocomentarios WHERE idComentario = ?";

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement deleteComentarioStmt = connection.prepareStatement(deleteComentarioQuery)) {

            // Eliminar el comentario
            deleteComentarioStmt.setInt(1, idComentario);
            deleteComentarioStmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
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