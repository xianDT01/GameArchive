package com.example.gamearchive;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.List;
import java.util.ResourceBundle;

public class ResultadosBusquedaController implements Initializable {

    @FXML
    private ListView<String> resultadosListView;

    private List<Juego> resultados;
    @FXML
    private Button VolverMenuInicial;


    @Override
    public void initialize(java.net.URL url, ResourceBundle resourceBundle) {

        resultadosListView.setOnMouseClicked(event -> {
            if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
                abrirMenuJuego();
            }
        });
    }

    public void mostrarResultados(List<Juego> resultados) {
        this.resultados = resultados;
        ObservableList<String> nombresJuegos = FXCollections.observableArrayList();
        for (Juego juego : resultados) {
            nombresJuegos.add(juego.getNombre());
        }
        resultadosListView.setItems(nombresJuegos);
    }

    @FXML
    public void abrirMenuJuego(MouseEvent event) {
        if (event.getButton().equals(MouseButton.PRIMARY) && event.getClickCount() == 2) {
            int index = resultadosListView.getSelectionModel().getSelectedIndex();
            if (index >= 0 && index < resultados.size()) {
                Juego juegoSeleccionado = resultados.get(index);
                abrirMenuJuego(juegoSeleccionado);
            }
        }
    }

    private void abrirMenuJuego(Juego juego) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuJuego.fxml"));
            Parent root = loader.load();
            MenuJuegoController controller = loader.getController();
            controller.initData(juego.getIdjuego(),juego.getNombre(), juego.getDescripcion(), juego.getFechaLanzamiento(), juego.getRutaCaratula(),juego.getPlataformas());
            int idjuego =juego.getIdjuego();
            ControllerId.setIdJuego(juego.getIdjuego());
            System.out.println("Id juego desde abrirMenuJuego buscador es:" + idjuego);
            Stage stage = new Stage();
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png")));
            stage.setScene(new Scene(root));
            stage.show();


            Stage ventanaActual = (Stage) resultadosListView.getScene().getWindow();
            ventanaActual.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private boolean abrirMenuJuego() {
        int index = resultadosListView.getSelectionModel().getSelectedIndex();
        if (index >= 0 && index < resultados.size()) {
            Juego juegoSeleccionado = resultados.get(index);
            try {
                int idjuego =juegoSeleccionado.getIdjuego();
                ControllerId.setIdJuego(juegoSeleccionado.getIdjuego());
                FXMLLoader loader = new FXMLLoader(getClass().getResource("MenuJuego.fxml"));
                Parent root = loader.load();
                MenuJuegoController controller = loader.getController();
                controller.initData(juegoSeleccionado.getIdjuego(),juegoSeleccionado.getNombre(), juegoSeleccionado.getDescripcion(), juegoSeleccionado.getFechaLanzamiento(), juegoSeleccionado.getRutaCaratula(),juegoSeleccionado.getPlataformas());
                Stage stage = new Stage();
                stage.setTitle("GameArchive");
                stage.getIcons().add(new Image(getClass().getResourceAsStream("/img/logo-GameArchive.png")));
                stage.setScene(new Scene(root));
                stage.show();


                Stage ventanaActual = (Stage) resultadosListView.getScene().getWindow();
                ventanaActual.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
    @FXML
    private void handleVolverMenuInicial(ActionEvent event) throws IOException {
        Stage ventana = (Stage) VolverMenuInicial.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource("MenuInicial.fxml"));
        Scene scene = new Scene(root);
        ventana.setTitle("GameArchive");
        ventana.setScene(scene);
        ventana.show();
    }

}