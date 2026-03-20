package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;

/**
 * Controlador para la ventana emergente de detalles de tickets.
 * Permite visualizar, consultar y eliminar tickets de la base de datos.
 */
public class VentanaEmergenteControllerDetalles {
    private PrimaryController controladorPrincipal;
    private String mesa;
    private ObservableList<Ticket> listaTickets = FXCollections.observableArrayList();

    @FXML private TableView<Ticket> tablaTickets;
    @FXML private TableColumn<Ticket, String> colMesa;
    @FXML private TableColumn<Ticket, String> colFecha;
    @FXML private TableColumn<Ticket, String> colPrecio;
    @FXML private TableColumn<Ticket, String> colMetodoPago;
    @FXML private TableColumn<Ticket, String> colEmpleado;

    /**
     * Establece el controlador principal para poder actualizar la vista principal tras aÃ±adir un producto.
     * @param controladorPrincipal Controlador principal.
     */
    public void setControladorPrincipal(PrimaryController controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }

    /**
     * Establece el nombre de la mesa asociada a la ventana de detalles.
     * @param mesa Nombre de la mesa.
     */
    public void setMesa(String mesa) {
        this.mesa = mesa;
    }

    /**
     * Inicializa la tabla de tickets y carga los datos desde la base de datos.
     */
    public void initialize() {
        if (tablaTickets != null) {
            tablaTickets.setItems(listaTickets);
        }

        // Inicializa las columnas de la tabla
        colMesa.setCellValueFactory(cellData -> {
            String nombreMesa = obtenerNombreMesaPorId(cellData.getValue().getIdMesa());
            return new SimpleStringProperty(nombreMesa);
        });
        colFecha.setCellValueFactory(cellData -> cellData.getValue().fecha_horaProperty());
        colPrecio.setCellValueFactory(cellData -> cellData.getValue().precioProperty());
        colMetodoPago.setCellValueFactory(cellData -> {
            String metodoPago = obtenerNombreMetodoPagoPorId(cellData.getValue().getIdMetodoPago());
            return new SimpleStringProperty(metodoPago);
        });
        colEmpleado.setCellValueFactory(cellData -> {
            String empleado = obtenerNombreEmpleadoPorId(cellData.getValue().getIdEmpleado());
            return new SimpleStringProperty(empleado);
        });

        // Carga los datos de la base de datos en la tabla
        try {
            cargarTickets();
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la lista de tickets.");
        }
    }

    /**
     * Carga los tickets desde la base de datos y los muestra en la tabla.
     */
    @FXML
    public void cargarTickets() {
        listaTickets.clear();
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/proyecto_final", "tpv_app", "tpv_app_123");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM ticket ");

            while (rs.next()) {
                Ticket ticket = new Ticket(rs.getInt("id"), rs.getInt("id_mesa"), rs.getString("fecha_hora"), rs.getString("precio"),
                rs.getInt("id_metodo_pago"), rs.getInt("id_empleado"));
                listaTickets.add(ticket);
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo cargar la lista de tickets.");
        }
        tablaTickets.setItems(listaTickets);
    }

    /**
     * Elimina el ticket seleccionado de la tabla y de la base de datos.
     * Muestra una alerta si no hay ningÃºn ticket seleccionado.
     */
    @FXML
    public void quitarTicket() {
        Ticket ticketSeleccionado = tablaTickets.getSelectionModel().getSelectedItem();
        if (ticketSeleccionado != null) {
            try {
                Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/proyecto_final", "tpv_app", "tpv_app_123");
                String sql = "DELETE FROM ticket WHERE id = ?";
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, ticketSeleccionado.getId());
                pstmt.executeUpdate();
                conn.close();

                // Actualizar la tabla
                cargarTickets();
            } catch (SQLException e) {
                e.printStackTrace();
                mostrarAlerta("Error", "No se pudo eliminar el ticket.");
            }
        } else {
            mostrarAlerta("Error", "Por favor, selecciona un ticket para eliminar.");
        }
    }

    /**
     * Devuelve el nombre del mÃ©todo de pago dado su ID.
     * @param idMetodoPago ID del mÃ©todo de pago.
     * @return Nombre del mÃ©todo de pago.
     */
    private String obtenerNombreMetodoPagoPorId(int idMetodoPago) {
        String nombreMetodoPago = null;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/proyecto_final", "tpv_app", "tpv_app_123");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT nombre FROM metodo_pago WHERE id = " + idMetodoPago);

            if (rs.next()) {
                nombreMetodoPago = rs.getString("nombre");
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nombreMetodoPago;
    }

    /**
     * Devuelve el nombre de la mesa dado su ID.
     * @param idMesa ID de la mesa.
     * @return Nombre de la mesa.
     */
    private String obtenerNombreMesaPorId(int idMesa) {
        String nombreMesa = null;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/proyecto_final", "tpv_app", "tpv_app_123");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT nombre FROM mesa WHERE id = " + idMesa);

            if (rs.next()) {
                nombreMesa = rs.getString("nombre");
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nombreMesa;
    }

    /**
     * Devuelve el nombre del empleado dado su ID.
     * @param idEmpleado ID del empleado.
     * @return Nombre del empleado.
     */
    private String obtenerNombreEmpleadoPorId(int idEmpleado) {
        String nombreEmpleado = null;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/proyecto_final", "tpv_app", "tpv_app_123");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT nombre FROM empleado WHERE id = " + idEmpleado);

            if (rs.next()) {
                nombreEmpleado = rs.getString("nombre");
            }
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nombreEmpleado;
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