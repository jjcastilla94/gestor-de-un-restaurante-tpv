package com.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javafx.beans.property.StringProperty;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.SimpleIntegerProperty;

/**
 * Clase que representa una comanda (los tickets) en el sistema.
 * Permite la gestión de comandas en la base de datos.
 * 
 * Una comanda está asociada a un producto, una mesa, un precio total y una cantidad.
 * 
 * @author Castilla
 */
public class Comanda {
    private IntegerProperty id_producto;
    private IntegerProperty id_mesa;
    private StringProperty precio_total;
    private IntegerProperty cantidad;

    /**
     * Crea una nueva comanda.
     * @param id_producto Identificador del producto.
     * @param id_mesa Identificador de la mesa.
     * @param precio_total Precio total de la comanda.
     * @param cantidad Cantidad de productos en la comanda.
     */
    public Comanda(int id_producto,int id_mesa, String precio_total, int cantidad ) {
        this.id_producto = new SimpleIntegerProperty(id_producto);
        this.id_mesa = new SimpleIntegerProperty(id_mesa);
        this.precio_total = new SimpleStringProperty(precio_total);
        this.cantidad = new SimpleIntegerProperty(cantidad);
    }

    /**
     * Devuelve la propiedad id_producto.
     * @return IntegerProperty del id_producto.
     */
    public IntegerProperty id_productoProperty() {
        return id_producto;
    }

    /**
     * Devuelve la propiedad id_mesa.
     * @return IntegerProperty del id_mesa.
     */
    public IntegerProperty id_mesaProperty() {
        return id_mesa;
    }

    /**
     * Devuelve la propiedad precio_total.
     * @return StringProperty del precio_total.
     */
    public StringProperty precio_totalProperty() {
        return precio_total;
    }

    /**
     * Devuelve la propiedad cantidad.
     * @return IntegerProperty de la cantidad.
     */
    public IntegerProperty cantidadProperty() {
        return cantidad;
    }

    /**
     * Obtiene el id del producto.
     * @return id del producto.
     */
    public int getId_producto() {
        return id_producto.get();
    }

    /**
     * Establece el id del producto.
     * @param id_producto Nuevo id del producto.
     */
    public void setId_producto(int id_producto) {
        this.id_producto.set(id_producto);
    }

    /**
     * Obtiene el id de la mesa.
     * @return id de la mesa.
     */
    public int getId_mesa() {
        return id_mesa.get();
    }

    /**
     * Establece el id de la mesa.
     * @param id_mesa Nuevo id de la mesa.
     */
    public void setId_mesa(int id_mesa) {
        this.id_mesa.set(id_mesa);
    }

    /**
     * Obtiene el precio total de la comanda.
     * @return precio total.
     */
    public String getPrecio_total() {
        return precio_total.get();
    }

    /**
     * Establece el precio total de la comanda.
     * @param precio_total Nuevo precio total.
     */
    public void setPrecio_total(String precio_total) {
        this.precio_total.set(precio_total);
    }

    /**
     * Obtiene la cantidad de productos en la comanda.
     * @return cantidad.
     */
    public int getCantidad() {
        return cantidad.get();
    }

    /**
     * Establece la cantidad de productos en la comanda.
     * @param cantidad Nueva cantidad.
     */
    public void setCantidad(int cantidad) {
        this.cantidad.set(cantidad);
    }

    /**
     * Obtiene todas las comandas de la base de datos y las añade a la lista proporcionada.
     * @param listaComandas Lista donde se añadirán las comandas.
     */
    public static void getAll(ObservableList<Comanda> listaComandas) {
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM comanda");

            while (rs.next()) {
                Comanda comanda = new Comanda(
                        rs.getInt("id_producto"),
                        rs.getInt("id_mesa"),
                        rs.getString("precio_total"),
                        rs.getInt("cantidad")
                );
                listaComandas.add(comanda);
            }
            conn.close();
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
        }
    }

    /**
     * Guarda la comanda en la base de datos.
     * @return true si la operación fue exitosa, false en caso contrario.
     */
    public boolean save() {
        boolean exito = true;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            String sql = "INSERT INTO comanda (id_producto, precio_total, cantidad, id_mesa) VALUES (" +
                         getId_producto() + ", '" + getPrecio_total() + "', '" + getCantidad() + "', " + getId_mesa() + ")";
            st.executeUpdate(sql);
            conn.close();
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
            exito = false;
        }
        return exito;
    }

    /**
     * Elimina la comanda de la base de datos según el id del producto.
     * @param id id del producto de la comanda a eliminar.
     * @return true si la operación fue exitosa, false en caso contrario.
     */
    public boolean delete(int id) {
        boolean exito = true;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            String sql = "DELETE FROM comanda WHERE id_producto = " + id;
            st.executeUpdate(sql);
            conn.close();
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
            exito = false;
        }
        return exito;
    }

    /**
     * Muestra un mensaje de error genérico.
     */
    public static void mensajeError() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText("ERROR");
        alert.showAndWait();
    }

    /**
     * Obtiene una conexión a la base de datos.
     * @return Objeto Connection o null si falla la conexión.
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final",
                                                "root", "root");
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
        }
        return conn;
    }
}