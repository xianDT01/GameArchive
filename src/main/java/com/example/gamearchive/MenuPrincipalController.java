package com.example.gamearchive;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class MenuPrincipalController {
    @FXML
    private Label welcomeText;
    @FXML
    private TextField correoElectronico;
    @FXML
    private PasswordField contraseña;

    public void ConexionDB() {
        // URL de conexión, incluye el nombre de la base de datos y el timezone
      //  String url = "jdbc:mysql://localhost:3306/gamearchive?serverTimezone=UTC&user=root&allowPublicKeyRetrieval=true";
        String url = "jdbc:mysql://localhost:3306/gamearchive?serverTimezone=UTC&user=root&password=abc123.";

        try {
            // Cargar el controlador de MySQL
            Class.forName("com.mysql.cj.jdbc.Driver");
            // Establecer la conexión
            Connection connection = DriverManager.getConnection(url);
            // Si llega a este punto, la conexión se ha establecido con éxito
            System.out.println("¡Conexión exitosa a la base de datos!");
            // Puedes utilizar 'connection' para ejecutar consultas SQL, etc.
        } catch (ClassNotFoundException e) {
            System.err.println("Error al cargar el controlador de MySQL: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error al conectar a la base de datos: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        MenuPrincipalController menuPrincipalController = new MenuPrincipalController();
        menuPrincipalController.ConexionDB(); // Llama al método ConexionDB para probar la conexión a la base de datos

    }

}