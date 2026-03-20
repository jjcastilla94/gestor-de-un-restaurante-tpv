package com.example;


import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class VentanaEmergenteControllerPorcentaje {
    
    private PrimaryController controladorPrincipal;

    /**
     * Establece el controlador principal para poder actualizar la vista principal tras aÃ±adir un producto.
     * @param controladorPrincipal Controlador principal.
     */
    public void setControladorPrincipal(PrimaryController controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }

    @FXML private TextField txtCantidad;
    @FXML private Label lblCantidad;
    @FXML private Label lblTotalConDescuento;
    @FXML private ChoiceBox<String> choice_box_descuento;

     private Map<String, Integer> descuentoNombreToId = new HashMap<>();

    /**
     * Inicializa la ventana emergente, cargando los tipos de descuento desde la base de datos.
     */
    @FXML
    private void initialize() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/proyecto_final", "tpv_app", "tpv_app_123");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, nombre FROM descuento");

            while (rs.next()) {
                String nombreDescuento = rs.getString("nombre");
                int idDescuento = rs.getInt("id");
                choice_box_descuento.getItems().add(nombreDescuento);
                descuentoNombreToId.put(nombreDescuento, idDescuento);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // AÃ±adir listener al campo de texto para actualizar los datos al cambiar el valor
        txtCantidad.textProperty().addListener((observable, oldValue, newValue) -> {
            actualizarDatos();
        });

        // AÃ±adir listener al choice box para actualizar los datos al cambiar el valor
        choice_box_descuento.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            actualizarDatos();
        });

    }

    private double cuentaOriginal = 0.0;

    public void setCuentaOriginal(double cuentaOriginal) {
        this.cuentaOriginal = cuentaOriginal;
        System.out.println("Cuenta original: " + cuentaOriginal);
    }

    @FXML
    private void actualizarDatos() {
        //este metodo actualiza los datos de la ventana emergente
        String cantidadTexto = txtCantidad.getText();
        if (cantidadTexto.isEmpty() || cantidadTexto.equals("0")) {
            // Si el campo de cantidad estÃ¡ vacÃ­o, muestra 0 en los labels
            lblCantidad.setText("0.00â‚¬");
            lblTotalConDescuento.setText(cuentaOriginal + "â‚¬");
        }
        else {
           
            try {
                // Segun el tipo de descuento seleccionado, se aplicarÃ¡ un descuento diferente
                String tipoDescuento = choice_box_descuento.getValue();
                
                // Si el descuento es un porcentaje
                if (tipoDescuento != null && tipoDescuento.equals("Porcentaje")) {
                    
                    double porcentaje = Double.parseDouble(cantidadTexto);
                    if (porcentaje < 1 || porcentaje > 99) {
                        mostrarAlerta("Error", "El porcentaje debe estar entre 1 y 99.");
                        lblCantidad.setText("0.00â‚¬");
                        lblTotalConDescuento.setText(String.format("%.2fâ‚¬", cuentaOriginal));
                        return;
                    }
                    double descuento = cuentaOriginal * (porcentaje / 100);
                    double totalConDescuento = cuentaOriginal - descuento;
                    lblCantidad.setText(String.format("%.2fâ‚¬", descuento));
                    lblTotalConDescuento.setText(String.format("%.2fâ‚¬", totalConDescuento));

                } else if(tipoDescuento != null && tipoDescuento.equals("Cantidad Fija")) { 
                    // Si el descuento es un valor fijo
                    double cantidad = Double.parseDouble(cantidadTexto);
                    double totalConDescuento = cuentaOriginal - cantidad;
                    if (totalConDescuento < 0) {
                        mostrarAlerta("Error", "El descuento no puede superar el total original.");
                        lblCantidad.setText("0.00â‚¬");
                        lblTotalConDescuento.setText(String.format("%.2fâ‚¬", cuentaOriginal));
                        return;
                    }
                    lblCantidad.setText(String.format("%.2fâ‚¬", cantidad));
                    lblTotalConDescuento.setText(String.format("%.2fâ‚¬", totalConDescuento));
                }
                
            } catch (NumberFormatException e) {
                mostrarAlerta("Error", "La cantidad introducida no es vÃ¡lida.");
            } 
        }
       
    }



    @FXML
    private void aplicarDescuento() {
       // Una vez que se ha validado el descuento en el mÃ©todo actualizarDatos(), se puede aplicar el descuento
        String cantidadTexto = txtCantidad.getText();
        if (cantidadTexto.isEmpty() || cantidadTexto.equals("0")) {
            mostrarAlerta("Error", "Por favor, introduzca una cantidad vÃ¡lida.");
            return;
        }

        String tipoDescuento = choice_box_descuento.getValue();
        if (tipoDescuento == null) {
            mostrarAlerta("Error", "Por favor, seleccione un tipo de descuento.");
            return;
        }

        // AquÃ­ puedes aplicar el descuento a la cuenta original si es por porcentaje
        double cantidad = Double.parseDouble(cantidadTexto);
        double totalConDescuento = 0.0;
        if (tipoDescuento.equals("Porcentaje")) {
            double porcentaje = cantidad;
            totalConDescuento = cuentaOriginal - (cuentaOriginal * (porcentaje / 100));
        } else if(tipoDescuento.equals("Cantidad Fija")) {
            totalConDescuento = cuentaOriginal - cantidad;
        }

        

        // Actualiza la cuenta original en el controlador principal
        controladorPrincipal.setCuentaConDescuento(totalConDescuento);

        // Cierra la ventana emergente
        cerrarVentana();
        
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

    /**
     * Cierra la ventana emergente.
     */
    private void cerrarVentana() {
        Stage stage = (Stage) txtCantidad.getScene().getWindow();
        stage.close();
    }
}
