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
 * Clase que representa una mesa en el sistema.
 * Permite la gestión de mesas en la base de datos.
 * 
 * Una mesa tiene un identificador, nombre, número, capacidad y estado.
 * 
 * @author Castilla
 */
public class Mesa {

    private IntegerProperty id;
    private StringProperty nombre;
    private IntegerProperty numero;
    private IntegerProperty capacidad;
    private StringProperty estado;

    /**
     * Crea una nueva mesa.
     * @param id Identificador de la mesa.
     * @param nombre Nombre de la mesa.
     * @param numero Número de la mesa.
     * @param capacidad Capacidad de la mesa.
     * @param estado Estado de la mesa.
     */
    public Mesa(int id,String nombre, int numero, int capacidad, String estado) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.numero = new SimpleIntegerProperty(numero);
        this.capacidad = new SimpleIntegerProperty(capacidad);
        this.estado = new SimpleStringProperty(estado);
    }

    /**
     * Devuelve la propiedad id.
     * @return IntegerProperty del id.
     */
    public IntegerProperty idProperty() {
        return id;
    }

    /**
     * Devuelve la propiedad nombre.
     * @return StringProperty del nombre.
     */
    public StringProperty nombreProperty() {
        return nombre;
    }

    /**
     * Devuelve la propiedad numero.
     * @return IntegerProperty del número.
     */
    public IntegerProperty numeroProperty() {
        return numero;
    }

    /**
     * Devuelve la propiedad capacidad.
     * @return IntegerProperty de la capacidad.
     */
    public IntegerProperty capacidadProperty() {
        return capacidad;
    }

    /**
     * Devuelve la propiedad estado.
     * @return StringProperty del estado.
     */
    public StringProperty estadoProperty() {
        return estado;
    }

    /**
     * Obtiene el id de la mesa.
     * @return id de la mesa.
     */
    public int getId() {
        return id.get();
    }

    /**
     * Establece el id de la mesa.
     * @param id Nuevo id.
     */
    public void setId(int id) {
        this.id.set(id);
    }

    /**
     * Obtiene el nombre de la mesa.
     * @return nombre de la mesa.
     */
    public String getNombre() {
        return nombre.get();
    }

    /**
     * Establece el nombre de la mesa.
     * @param nombre Nuevo nombre.
     */
    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    /**
     * Obtiene el número de la mesa.
     * @return número de la mesa.
     */
    public int getNumero() {
        return numero.get();
    }

    /**
     * Establece el número de la mesa.
     * @param numero Nuevo número.
     */
    public void setNumero(int numero) {
        this.numero.set(numero);
    }

    /**
     * Obtiene la capacidad de la mesa.
     * @return capacidad de la mesa.
     */
    public int getCapacidad() {
        return capacidad.get();
    }

    /**
     * Establece la capacidad de la mesa.
     * @param capacidad Nueva capacidad.
     */
    public void setCapacidad(int capacidad) {
        this.capacidad.set(capacidad);
    }

    /**
     * Obtiene el estado de la mesa.
     * @return estado de la mesa.
     */
    public String getEstado() {
        return estado.get();
    }

    /**
     * Establece el estado de la mesa.
     * @param estado Nuevo estado.
     */
    public void setEstado(String estado) {
        this.estado.set(estado);
    }

    /**
     * Obtiene el nombre de la mesa a partir de su id.
     * @param id Id de la mesa.
     * @return Nombre de la mesa o null si no existe.
     */
    public static String getNombreById(int id) {
        String nombre = null;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT nombre FROM mesa WHERE id = " + id);

            if (rs.next()) {
                nombre = rs.getString("nombre");
            }
            conn.close();
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
        }

        return nombre;
    }

    /**
     * Obtiene todas las mesas de la base de datos y las añade a la lista proporcionada.
     * @param listaMesas Lista donde se añadirán las mesas.
     */
    public static void getAll(ObservableList<Mesa> listaMesas) {
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM mesa");

            while (rs.next()) {
                Mesa mesa = new Mesa(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getInt("numero"),
                    rs.getInt("capacidad"),
                    rs.getString("estado")
                );
                listaMesas.add(mesa);
            }
            conn.close();
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
        }
    }

    /**
     * Obtiene una mesa por su id.
     * @param id Id de la mesa.
     * @return Objeto Mesa o null si no existe.
     */
    public static Mesa get(int id) {
        Mesa mesa = null;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM mesa WHERE id = " + id);

            if (rs.next()) {
                mesa = new Mesa(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getInt("numero"),
                    rs.getInt("capacidad"),
                    rs.getString("estado")
                );
            }
            conn.close();
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
        }

        return mesa;
    }

    /**
     * Obtiene el último id registrado en la tabla mesa.
     * @return Último id o 0 si no hay registros.
     */
    public static int getLastId() {
        int lastId = 0;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(id) AS last_id FROM mesa");

            if (rs.next()) {
                lastId = rs.getInt("last_id");
            }
            conn.close();
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
        }

        return lastId;
    }

    /**
     * Guarda la mesa en la base de datos.
     * Si existe, la actualiza; si no, la inserta.
     * @return Número de filas afectadas.
     */
    public int save() {
        int filasAfectadas = 0;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM mesa WHERE id=" + this.getId());
            if (rs.next()) {
                // Si la mesa ya existe, la modificamos
                filasAfectadas = st.executeUpdate(
                    "UPDATE mesa SET nombre='" + this.getNombre() +
                    "', numero=" + this.getNumero() +
                    ", capacidad=" + this.getCapacidad() +
                    ", estado='" + this.getEstado() +
                    "' WHERE id=" + this.getId()
                );
            } else {
                // Si la mesa no existe, la añadimos
                filasAfectadas = st.executeUpdate(
                    "INSERT INTO mesa (nombre, numero, capacidad, estado) VALUES ('" +
                    this.getNombre() + "', " +
                    this.getNumero() + ", " +
                    this.getCapacidad() + ", '" +
                    this.getEstado() + "')"
                );
            }

            conn.close();
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
        }
        return filasAfectadas;
    }

    /**
     * Elimina la mesa de la base de datos.
     * @return Número de filas afectadas.
     */
    public int delete() {
        int filasAfectadas = 0;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            filasAfectadas = st.executeUpdate("DELETE FROM mesa WHERE id=" + this.getId());
            conn.close();
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
        }
        return filasAfectadas;
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