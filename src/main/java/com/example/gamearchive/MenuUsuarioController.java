package com.example.gamearchive;

import com.example.gamearchive.DatabaseConnection.DatabaseConnection;
import com.example.gamearchive.model.SesionUsuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.controlsfx.control.Notifications;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.sql.*;
import java.util.ResourceBundle;
import java.nio.file.Paths;


public class MenuUsuarioController implements Initializable {


    @FXML
    private ImageView ImagenDePerfil;
    @FXML
    private ImageView ImagenDePerfil2;
    @FXML
    private Button Volver;
    @FXML
    private Label nombre;
    @FXML
    private Label correo;
    @FXML
    private Label TotalComentarios;
    @FXML
    private  Label TotalNotas;
    @FXML
    private Button Atecptar;
    @FXML
    private Button PerfilUsuaruio;
    @FXML
    private Button ModificarUsuaruio;
    @FXML
    private Button Bienbenida;
    @FXML
    private AnchorPane PanelBienvenida;
    @FXML
    private AnchorPane PanelPerfilUsuario;
    @FXML
    private AnchorPane PanelModificarUsuario;
    private String rutaDeLaImagen;


    @FXML
    private void ControllerPanel(ActionEvent event) {
        if (event.getSource() == PerfilUsuaruio) {
            PanelPerfilUsuario.setVisible(true);
            PanelModificarUsuario.setVisible(false);
            PanelBienvenida.setVisible(false);
        } else if (event.getSource() ==Bienbenida) {
            PanelBienvenida.setVisible(true);
            PanelPerfilUsuario.setVisible(false);
            PanelModificarUsuario.setVisible(false);
        } else if ( event.getSource() == ModificarUsuaruio) {
            PanelModificarUsuario.setVisible(true);
            PanelBienvenida.setVisible(false);
            PanelPerfilUsuario.setVisible(false);
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        cargarImagenDesdeBD();
        int idUsuario = SesionUsuario.getUsuario();
        mostrarInformacionUsuario(idUsuario);
        //
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
            // Manejar el error adecuadamente
        }
        return rutaImagenPerfil;
    }

    @FXML
    private void aceptarCambios() {
        // Verifica si se ha seleccionado una imagen antes de guardarla
        if (rutaDeLaImagen != null && !rutaDeLaImagen.isEmpty()) {
            guardarRutaImagenPerfilEnBD(rutaDeLaImagen);
            System.out.println("Guardada imagen de perfil");
            mostrarNotificacionExito("Éxito","La imagen de perfil se guardo correctamente");
        } else {
            System.out.println("Error al guardar imagen");
            mostrarNotificacion("Error", "Error al guardar la imagen");
        }
    }

    // Método para manejar el evento de carga de imagen de perfil
    @FXML
    private void cargarImagenPerfil(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen de Perfil");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.gif", "*.bmp"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            mostrarNotificacionExito("Éxito", "Se cargo la imagen correctamente");
            FileInputStream fis = null;
            try {
                // Obtener la ruta relativa del archivo seleccionado
                String nombreArchivo = file.getName();
                String rutaDestino = "src/main/resources/profiles_images/" + nombreArchivo;
                // Eliminar la imagen anterior si existe
                if (rutaDeLaImagen != null && !rutaDeLaImagen.isEmpty()) {
                    Files.deleteIfExists(Paths.get(rutaDeLaImagen));
                }
                // Verificar si el directorio de destino existe, si no, crearlo
                Path directorioDestino = Paths.get("src/main/resources/profiles_images");
                if (!Files.exists(directorioDestino)) {
                    Files.createDirectories(directorioDestino);
                }
                // Copiar el archivo seleccionado a la nueva ubicación
                Files.copy(file.toPath(), Paths.get(rutaDestino), StandardCopyOption.REPLACE_EXISTING);
                fis = new FileInputStream(rutaDestino);
                Image image = new Image(fis);
                ImagenDePerfil.setImage(image);
                // Almacenar la ruta de la imagen seleccionada en la variable de instancia
                rutaDeLaImagen = rutaDestino;
                aceptarCambios();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    // Método para guardar la imagen de perfil en el directorio correspondiente
    private String getRutaImagenPerfil() {
        return "src/main/resources/profiles_images/default_profile.png";
    }

    // Método para guardar la ruta de la imagen de perfil en la base de datos
    private void guardarRutaImagenPerfilEnBD(String rutaImagenPerfil) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "UPDATE Usuarios SET imagen_de_perfil = ? WHERE idUsuario = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            int idUsuario = SesionUsuario.getUsuario();
            statement.setString(1, rutaImagenPerfil);
            statement.setInt(2, idUsuario);
            int filasAfectadas = statement.executeUpdate();
            if (filasAfectadas > 0) {
                System.out.println("Ruta de imagen de perfil actualizada en la base de datos.");
                mostrarNotificacionExito("Éxito", "Se guardo la imagen correctamente");
            } else {
                System.out.println("No se pudo actualizar la ruta de imagen de perfil en la base de datos.");
                mostrarNotificacion("Erorr","No se pudo guardar la imagen");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    // Metodo para mostrar la información al usuario y ponerla en le label, se muestra el nombre de usuario, correo, total de notas cualificadas y el total de comentarios
    private void mostrarInformacionUsuario(int idUsuario) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            // Consulta para obtener el nombre y correo del usuario
            String queryUsuario = "SELECT nombre, correo FROM Usuarios WHERE idUsuario = ?";
            PreparedStatement statementUsuario = connection.prepareStatement(queryUsuario);
            statementUsuario.setInt(1, idUsuario);
            ResultSet resultSetUsuario = statementUsuario.executeQuery();
            if (resultSetUsuario.next()) {
                String nombreUsuario = resultSetUsuario.getString("nombre");
                String correoUsuario = resultSetUsuario.getString("correo");
                // Mostrar nombre y correo del usuario en los Labels
                nombre.setText(nombreUsuario);
                correo.setText(correoUsuario);
            }

            // Consulta para obtener el número de comentarios del usuario
            String queryComentarios = "SELECT COUNT(*) AS totalComentarios FROM Comentarios WHERE idUsuario = ?";
            PreparedStatement statementComentarios = connection.prepareStatement(queryComentarios);
            statementComentarios.setInt(1, idUsuario);
            ResultSet resultSetComentarios = statementComentarios.executeQuery();
            if (resultSetComentarios.next()) {
                int totalComentarios = resultSetComentarios.getInt("totalComentarios");
                TotalComentarios.setText(String.valueOf(totalComentarios));
            }

            // Consulta para obtener el número de juegos del usuario
            String queryJuegos = "SELECT COUNT(*) AS totalJuegos FROM Reseñas WHERE idUsuario = ?";
            PreparedStatement statementJuegos = connection.prepareStatement(queryJuegos);
            statementJuegos.setInt(1, idUsuario);
            ResultSet resultSetJuegos = statementJuegos.executeQuery();
            if (resultSetJuegos.next()) {
                int totalJuegos = resultSetJuegos.getInt("totalJuegos");
                // Mostrar el número de juegos en el Label correspondiente
                TotalNotas.setText(String.valueOf(totalJuegos));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TextField ModificarCorreoElectronico;
    @FXML
    private Button ActualizarDatos;
    @FXML
    private PasswordField ModificarContraseña;
    @FXML
    private PasswordField RepetirContraseña;
    @FXML
    private Button ModificarContraseñas;

    private void cargarDatosUsuario(int idUsuario) {
        try (Connection connection = DatabaseConnection.getConnection()) {
            String query = "SELECT correo FROM Usuarios WHERE idUsuario = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setInt(1, idUsuario);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String correo = resultSet.getString("correo");
                // Mostrar el correo electrónico actual del usuario en el campo correspondiente
                ModificarCorreoElectronico.setText(correo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void actualizarDatosUsuario() {
        int idUsuario = SesionUsuario.getUsuario();

        // Obtener el nuevo correo electrónico ingresado por el usuario
        String nuevoCorreoElectronico = ModificarCorreoElectronico.getText();

        if (nuevoCorreoElectronico.isEmpty()) {
            mostrarNotificacion("Error", "Por favor, complete el campo de correo electrónico");
            return;
        }

        // Verificar si el correo electrónico tiene un formato válido
        if (!validarFormatoCorreo(nuevoCorreoElectronico)) {
            mostrarNotificacion("Error", "Por favor, ingrese un correo electrónico válido");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Verificar si el nuevo correo electrónico ya existe
            if (!validarCorreoElectronico(nuevoCorreoElectronico, idUsuario, connection)) {
                return;
            }

            // Actualizar el correo electrónico del usuario en la base de datos
            String queryActualizar = "UPDATE Usuarios SET correo = ? WHERE idUsuario = ?";
            PreparedStatement statementActualizar = connection.prepareStatement(queryActualizar);
            statementActualizar.setString(1, nuevoCorreoElectronico);
            statementActualizar.setInt(2, idUsuario);
            statementActualizar.executeUpdate();

            mostrarNotificacionExito("Éxito", "Correo electrónico actualizado correctamente");
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar la excepción adecuadamente
        }
    }

    private boolean validarFormatoCorreo(String correoElectronico) {
        // validar el formato del correo electrónico
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return correoElectronico.matches(regex);
    }


    private boolean validarNombreUsuario(String nuevoNombreUsuario, int idUsuario, Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM Usuarios WHERE nombre = ? AND idUsuario != ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, nuevoNombreUsuario);
        statement.setInt(2, idUsuario);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            int total = resultSet.getInt("total");
            if (total > 0) {
                mostrarNotificacion("Error", "El nombre de usuario ya está en uso");
                return false;
            }
        }
        return true;
    }

    private boolean validarCorreoElectronico(String nuevoCorreoElectronico, int idUsuario, Connection connection) throws SQLException {
        String query = "SELECT COUNT(*) AS total FROM Usuarios WHERE correo = ? AND idUsuario != ?";
        PreparedStatement statement = connection.prepareStatement(query);
        statement.setString(1, nuevoCorreoElectronico);
        statement.setInt(2, idUsuario);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            int total = resultSet.getInt("total");
            if (total > 0) {
                mostrarNotificacion("Error", "El correo electrónico ya está en uso");
                return false;
            }
        }
        return true;
    }

    @FXML
    private void actualizarContraseña() {
        // Obtener el ID del usuario
        int idUsuario = SesionUsuario.getUsuario();

        // Obtener la nueva contraseña ingresada por el usuario
        String nuevaContraseña = ModificarContraseña.getText();
        String repetirContraseña = RepetirContraseña.getText();

        // Verificar si las contraseñas coinciden
        if (!nuevaContraseña.equals(repetirContraseña)) {
            mostrarNotificacion("Error", "Las contraseñas no coinciden");
            return;
        }

        // Verificar si la nueva contraseña tiene al menos 8 caracteres
        if (nuevaContraseña.length() < 8) {
            mostrarNotificacion("Error", "La contraseña debe tener al menos 8 caracteres");
            return;
        }

        try (Connection connection = DatabaseConnection.getConnection()) {
            // Actualizar la contraseña del usuario en la base de datos
            String queryActualizar = "UPDATE Usuarios SET contraseña = ? WHERE idUsuario = ?";
            PreparedStatement statementActualizar = connection.prepareStatement(queryActualizar);
            statementActualizar.setString(1, nuevaContraseña);
            statementActualizar.setInt(2, idUsuario);
            statementActualizar.executeUpdate();

            mostrarNotificacionExito("Éxito", "Contraseña actualizada correctamente");
        } catch (SQLException e) {
            e.printStackTrace();
            // Manejar la excepción adecuadamente
        }
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
    @FXML
    private void handleVolverPantallaPrincipal(ActionEvent event) throws IOException {
        Stage ventana = (Stage) Volver.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("MenuInicial.fxml"));
        Scene scene = new Scene(root);
        ventana.setTitle("GameArchive");
        ventana.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png")));
        ventana.setScene(scene);
        ventana.show();
    }

}
