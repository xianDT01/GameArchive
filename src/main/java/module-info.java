module com.example.gamearchive {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.controlsfx.controls;


    opens com.example.gamearchive to javafx.fxml;
    exports com.example.gamearchive;
    exports com.example.gamearchive.model;
    opens com.example.gamearchive.model to javafx.fxml;
    exports com.example.gamearchive.DatabaseConnection;
    opens com.example.gamearchive.DatabaseConnection to javafx.fxml;
}