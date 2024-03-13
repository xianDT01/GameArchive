module com.example.gamearchive {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.controlsfx.controls;


    opens com.example.gamearchive to javafx.fxml;
    exports com.example.gamearchive;
}