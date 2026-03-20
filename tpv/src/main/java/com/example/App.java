package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Clase principal de la aplicaciÃ³n JavaFX.
 * Gestiona el ciclo de vida de la aplicaciÃ³n y permite cambiar de vista (FXML) en cualquier momento.
 * 
 * @author Castilla
 */
public class App extends Application {

    /** Escena principal de la aplicaciÃ³n. */
    private static Scene scene;
    /** Escenario principal de la aplicaciÃ³n. */
    private static Stage primaryStage;

    /**
     * MÃ©todo de inicio de la aplicaciÃ³n JavaFX.
     * Carga la vista principal desde el archivo FXML y la muestra en el escenario.
     * 
     * @param stage Escenario principal proporcionado por JavaFX.
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
     */
    @Override
    public void start(Stage stage) throws IOException {
        primaryStage = stage; // Guardamos la referencia al escenario principal
        // Creamos una Scene a partir de un archivo FXML y la mostramos en la ventana (stage)
        scene = new Scene(loadFXML("login.fxml"));
        stage.setTitle("Proyecto Final TPV"); // TÃ­tulo de la ventana
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Cambia la vista de la aplicaciÃ³n cargando un nuevo archivo FXML.
     * Puede ser llamado desde cualquier controlador.
     * 
     * @param fxml Nombre del archivo FXML a cargar.
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
     */
    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    /**
     * Carga un archivo FXML y lo devuelve como un nodo de la interfaz de usuario.
     * 
     * @param fxml Nombre del archivo FXML a cargar.
     * @return Nodo raÃ­z del archivo FXML cargado.
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        return fxmlLoader.load();
    }

    /**
     * MÃ©todo main. Lanza la aplicaciÃ³n JavaFX.
     * 
     * @param args Argumentos de la lÃ­nea de comandos.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Devuelve el escenario principal de la aplicaciÃ³n.
     * 
     * @return Stage principal.
     */
    public static Stage getStage() {
        return primaryStage;
    }
}