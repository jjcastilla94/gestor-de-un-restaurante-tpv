package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Clase principal de la aplicación JavaFX.
 * Gestiona el ciclo de vida de la aplicación y permite cambiar de vista (FXML) en cualquier momento.
 * 
 * @author Castilla
 */
public class App extends Application {

    /** Escena principal de la aplicación. */
    private static Scene scene;
    /** Escenario principal de la aplicación. */
    private static Stage primaryStage;

    /**
     * Método de inicio de la aplicación JavaFX.
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
        stage.setTitle("Proyecto Final TPV"); // Título de la ventana
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }

    /**
     * Cambia la vista de la aplicación cargando un nuevo archivo FXML.
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
     * @return Nodo raíz del archivo FXML cargado.
     * @throws IOException Si ocurre un error al cargar el archivo FXML.
     */
    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml));
        return fxmlLoader.load();
    }

    /**
     * Método main. Lanza la aplicación JavaFX.
     * 
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        launch();
    }

    /**
     * Devuelve el escenario principal de la aplicación.
     * 
     * @return Stage principal.
     */
    public static Stage getStage() {
        return primaryStage;
    }
}