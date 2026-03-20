package com.example;

import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.Scene;
import java.io.IOException;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Controlador para la pantalla de inicio de sesiÃ³n y registro.
 * Gestiona la lÃ³gica de login, registro y cambio de vistas entre ambas.
 * 
 * Permite iniciar sesiÃ³n, registrar nuevos empleados y obtener el usuario actual.
 * 
 * @author Castilla
 */
public class LoginController implements Initializable {

    @FXML private Button loginTabBtn;
    @FXML private Button signupTabBtn;
    @FXML private Pane selectionIndicator;
    @FXML private VBox loginPane;
    @FXML private VBox signupPane;
    @FXML private TextField loginUsername;
    @FXML private PasswordField loginPassword;
    @FXML private Label loginInfo;
    @FXML private ProgressBar loginProgressBar;
    @FXML private TextField signupUsername;
    @FXML private TextField signupEmail;
    @FXML private PasswordField signupPassword;
    @FXML private Label signupInfo;
    @FXML private ProgressBar signupProgressBar;

    private boolean isLoginView = true;

    /**
     * Inicializa la vista, mostrando por defecto la pantalla de login.
     * @param url URL de inicializaciÃ³n.
     * @param resourceBundle Recursos de inicializaciÃ³n.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // ConfiguraciÃ³n inicial - por defecto muestra la pantalla de login
        selectionIndicator.setTranslateX(0);
        loginPane.setVisible(true);
        signupPane.setVisible(false);
        updateTabButtonsStyle();
    }

    /**
     * Cambia la vista a la pestaÃ±a de login.
     * @param event Evento de acciÃ³n.
     */
    @FXML
    public void switchToLogin(ActionEvent event) {
        if (!isLoginView) {
            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.4), selectionIndicator);
            transition.setToX(0);
            transition.play();

            loginPane.setVisible(true);
            signupPane.setVisible(false);
            isLoginView = true;
            updateTabButtonsStyle();
        }
    }

    /**
     * Cambia la vista a la pestaÃ±a de registro.
     * @param event Evento de acciÃ³n.
     */
    @FXML
    public void switchToSignup(ActionEvent event) {
        if (isLoginView) {
            TranslateTransition transition = new TranslateTransition(Duration.seconds(0.4), selectionIndicator);
            transition.setToX(signupTabBtn.getLayoutX() - loginTabBtn.getLayoutX());
            transition.play();

            loginPane.setVisible(false);
            signupPane.setVisible(true);
            isLoginView = false;
            updateTabButtonsStyle();
        }
    }

    /**
     * Actualiza el estilo visual de los botones de pestaÃ±a segÃºn la vista activa.
     */
    private void updateTabButtonsStyle() {
        if (isLoginView) {
            loginTabBtn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: white; -fx-background-radius: 5;");
            signupTabBtn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-text-fill: white; -fx-background-radius: 5;");
        } else {
            loginTabBtn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.1); -fx-text-fill: white; -fx-background-radius: 5;");
            signupTabBtn.setStyle("-fx-background-color: rgba(255, 255, 255, 0.2); -fx-text-fill: white; -fx-background-radius: 5;");
        }
    }

    /**
     * Maneja el evento de inicio de sesiÃ³n.
     * Valida los campos, consulta la base de datos y realiza la transiciÃ³n a la pantalla principal si es correcto.
     * @param event Evento de acciÃ³n.
     */
    @FXML
    public void handleLogin(ActionEvent event) {
        String username = loginUsername.getText();
        String password = loginPassword.getText();

        // ValidaciÃ³n bÃ¡sica
        if (username.isEmpty() || password.isEmpty()) {
            loginInfo.setText("Por favor, completa todos los campos");
            loginUsername.clear();
            loginPassword.clear();
            return;
        }

        try {
            Connection conn = getConnection();
            if (conn == null) {
                loginInfo.setText("No se pudo conectar a la base de datos");
                return;
            }
            Statement stmt = conn.createStatement();
            String sql = "SELECT nombre, pass FROM empleado WHERE nombre = '" + username + "' AND pass = '" + password + "'";
            ResultSet rs = stmt.executeQuery(sql);

            if (rs.next()) {
                String sqlInsert = "INSERT INTO usuario_actual (nombre, pass) VALUES ('" + username + "', '" + password + "')";
                Statement stmtInsert = conn.createStatement();
                stmtInsert.executeUpdate(sqlInsert);
                stmtInsert.close();

                // Mostrar barra de progreso
                loginProgressBar.setVisible(true);
                loginProgressBar.setDisable(false);
                loginInfo.setText("Iniciando sesiÃ³n...");

                Scene scene = loginProgressBar.getScene();
                if (scene != null) {
                    scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
                }

                new Thread(() -> {
                    for (int i = 0; i <= 100; i++) {
                        final int progress = i;
                        javafx.application.Platform.runLater(() -> loginProgressBar.setProgress(progress / 100.0));
                        try {
                            Thread.sleep(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    loginProgressBar.setVisible(false);
                    try {
                        App.setRoot("primary.fxml");
                        javafx.application.Platform.runLater(() -> {
                            App.getStage().setTitle("Ventana Principal");
                            App.getStage().setResizable(true);
                            App.getStage().setMaximized(true);
                        });
                    } catch (IOException ex) {
                        ex.printStackTrace();
                        mostrarAlerta("Error", "Error al cargar la pantalla principal");
                    }
                }).start();

            } else {
                loginInfo.setText("Empleado o contraseÃ±a incorrectos");
                loginUsername.clear();
                loginPassword.clear();
            }

        } catch (Exception e) {
            loginInfo.setText("Error en la conexiÃ³n a la base de datos");
            e.printStackTrace();
            return;
        }
    }

    /**
     * Maneja el evento de registro de un nuevo empleado.
     * Valida los campos, comprueba duplicados y registra el usuario en la base de datos.
     * @param event Evento de acciÃ³n.
     */
    @FXML
    public void handleSignup(ActionEvent event) {
        String username = signupUsername.getText();
        String email = signupEmail.getText();
        String password = signupPassword.getText();

        // ValidaciÃ³n bÃ¡sica
        if (username.isEmpty() || email.isEmpty() || password.isEmpty()) {
            signupInfo.setText("Por favor, completa todos los campos");
            return;
        }

        // ValidaciÃ³n simple de email
        if (!email.contains("@") || !email.contains(".")) {
            signupInfo.setText("Por favor, ingresa un email vÃ¡lido");
            return;
        }

        // ValidaciÃ³n simple de contraseÃ±a
        if (password.length() < 6) {
            signupInfo.setText("La contraseÃ±a debe tener al menos 6 caracteres");
            return;
        }

        try {
            Connection conn = getConnection();
            if (conn == null) {
                signupInfo.setText("No se pudo conectar a la base de datos");
                return;
            }
            Statement stmt = conn.createStatement();
            String sql = "INSERT INTO empleado (nombre, pass, email) VALUES ('" + username + "', '" + password + "', '" + email + "')";

            //Si el email ya existe en la base de datos, no se permite la inserciÃ³n
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM empleado WHERE email='" + email + "'");
            if (rs.next()) {
                signupInfo.setText("El email ya estÃ¡ registrado");
                return;
            }

            // Guardar el usuario en la base de datos 
            stmt.executeUpdate(sql);
            stmt.close();

            // Mostrar barra de progreso
            signupProgressBar.setVisible(true);
            signupProgressBar.setDisable(false);
            signupInfo.setText("Registrando empleado...");

            Scene scene = signupProgressBar.getScene();
            if (scene != null) {
                scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
            }

            new Thread(() -> {
                for (int i = 0; i <= 100; i++) {
                    final int progress = i;
                    javafx.application.Platform.runLater(() -> signupProgressBar.setProgress(progress / 100.0));
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                signupProgressBar.setVisible(false);
                Platform.runLater(() -> {
                    signupInfo.setText("Â¡Empleado registrado!");
                });

                CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS).execute(() -> switchToLogin(null));

            }).start();

        } catch (Exception e) {
            signupInfo.setText("Error en el proceso de registro");
            e.printStackTrace();
        }
    }

    /**
     * Obtiene el nombre del usuario actual que ha iniciado sesiÃ³n.
     * @return Nombre del usuario actual o "Empleado no encontrado" si no existe.
     */
    @FXML
    public String getUsuarioActual() {
        String nombreUsuario = null;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            String sql = "SELECT nombre FROM usuario_actual WHERE id = (SELECT MAX(id) FROM usuario_actual)";
            ResultSet rs = st.executeQuery(sql);

            if (rs.next()) {
                nombreUsuario = rs.getString("nombre");
            } else {
                return "Empleado no encontrado";
            }
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nombreUsuario;
    }

    /**
     * Obtiene una conexiÃ³n a la base de datos.
     * @return Objeto Connection o null si falla la conexiÃ³n.
     */
    private Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/proyecto_final",
                                                "tpv_app", "tpv_app_123");
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo conectar a la base de datos");
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * Muestra una alerta de error con el tÃ­tulo y mensaje proporcionados.
     * @param titulo TÃ­tulo de la alerta.
     * @param mensaje Mensaje de la alerta.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

}