package com.example;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class VentanaEmergenteControllerCobrar {
    
    private PrimaryController controladorPrincipal;

    /**
     * Establece el controlador principal para poder actualizar la vista principal tras añadir un producto.
     * @param controladorPrincipal Controlador principal.
     */
    public void setControladorPrincipal(PrimaryController controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }

    @FXML private Label lblTotal;
    @FXML private ChoiceBox<String> choice_box_metodo_pago;

     private Map<String, Integer> metodo_pagoNombreToId = new HashMap<>();

    /**
     * Inicializa la ventana emergente, cargando los tipos de descuento desde la base de datos.
     */
    @FXML
    private void initialize() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final", "root", "root");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, nombre FROM metodo_pago");

            while (rs.next()) {
                String nombrePago = rs.getString("nombre");
                int idPago = rs.getInt("id");
                choice_box_metodo_pago.getItems().add(nombrePago);
                metodo_pagoNombreToId.put(nombrePago, idPago);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Añadir listener al choice box para actualizar los datos al cambiar el valor
        choice_box_metodo_pago.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            // Aquí puedes manejar el evento de selección del choice box
            actualizarDatos();
        });

    }

    public double cuentaOriginal = 0.0;

    public void setCuentaOriginal(double cuentaOriginal) {
        this.cuentaOriginal = cuentaOriginal;
        System.out.println("Cuenta original: " + cuentaOriginal);
    }

    @FXML
    private void actualizarDatos() {
        //este metodo actualiza los datos de la ventana emergente y pone el total
        lblTotal.setText(String.valueOf(cuentaOriginal));

    }

    @FXML
    private void cobrar() {
        // Este método se llama al hacer clic en el botón "Cobrar"
        String metodoPagoSeleccionado = choice_box_metodo_pago.getSelectionModel().getSelectedItem();
        if (metodoPagoSeleccionado == null) {
            mostrarAlerta("Error", "Por favor, seleccione un método de pago.");
            return;
        }
        int idMetodoPago = metodo_pagoNombreToId.get(metodoPagoSeleccionado);
        //double total = Double.parseDouble(lblTotal.getText());

        controladorPrincipal.guardarTicket(idMetodoPago);

        // Cierra la ventana emergente
        cerrarVentana();
        
    }

    

    /**
     * Muestra una alerta de error con el título y mensaje proporcionados.
     * @param titulo Título de la alerta.
     * @param mensaje Mensaje de la alerta.
     */
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Cierra la ventana emergente.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) lblTotal.getScene().getWindow();
        stage.close();
    }
}

