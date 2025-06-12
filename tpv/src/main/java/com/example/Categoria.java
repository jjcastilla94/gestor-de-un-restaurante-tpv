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
 * Clase que representa una categoría de productos.
 * Permite la gestión de categorías en la base de datos.
 * 
 * @author Castilla
 */
public class Categoria {
    private IntegerProperty id;
    private StringProperty nombre;

    /**
     * Crea una nueva categoría.
     * @param id Identificador de la categoría.
     * @param nombre Nombre de la categoría.
     */
    public Categoria(int id, String nombre) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
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
     * Obtiene el id de la categoría.
     * @return id de la categoría.
     */
    public int getId() {
        return id.get();
    }

    /**
     * Establece el id de la categoría.
     * @param id Nuevo id.
     */
    public void setId(int id) {
        this.id.set(id);
    }

    /**
     * Obtiene el nombre de la categoría.
     * @return nombre de la categoría.
     */
    public String getNombre() {
        return nombre.get();
    }

    /**
     * Establece el nombre de la categoría.
     * @param nombre Nuevo nombre.
     */
    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    /**
     * Obtiene el nombre de la categoría a partir de su id.
     * @param id Id de la categoría.
     * @return Nombre de la categoría o null si no existe.
     */
    public static String getNombreById(int id) {
        String nombre = null;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT nombre FROM categoria WHERE id = " + id);

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
     * Obtiene todas las categorías de la base de datos y las añade a la lista proporcionada.
     * @param listaCategorias Lista donde se añadirán las categorías.
     */
    public static void getAll(ObservableList<Categoria> listaCategorias) {
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM categoria");

            while (rs.next()) {
                Categoria categoria = new Categoria(rs.getInt("id"), rs.getString("nombre"));
                listaCategorias.add(categoria);
            }
            conn.close();
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
        }
    }

    /**
     * Obtiene una categoría por su id.
     * @param id Id de la categoría.
     * @return Objeto Categoria o null si no existe.
     */
    public static Categoria get(int id) {
        Categoria categoria = null;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM categoria WHERE id = " + id);

            if (rs.next()) {
                categoria = new Categoria(rs.getInt("id"), rs.getString("nombre"));
            }
            conn.close();
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
        }

        return categoria;
    }

    /**
     * Obtiene el último id registrado en la tabla categoria.
     * @return Último id o 0 si no hay registros.
     */
    public static int getLastId() {
        int lastId = 0;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(id) AS last_id FROM categoria");

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
     * Guarda la categoría en la base de datos.
     * Si existe, la actualiza; si no, la inserta.
     * @return Número de filas afectadas.
     */
    public int save() {
        int filasAfectadas = 0;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM categoria WHERE id=" + this.getId());
            if (rs.next()) {
                // Si la categoría ya existe, la modificamos
                filasAfectadas = st.executeUpdate("UPDATE categoria SET nombre='" + this.getNombre() + "' WHERE id=" + this.getId());
            } else {
                // Si la categoría no existe, la añadimos
                filasAfectadas = st.executeUpdate("INSERT INTO categoria (nombre, descripcion) VALUES ('" + this.getNombre() + "')");
            }

            conn.close();
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
        }
        return filasAfectadas;
    }

    /**
     * Elimina la categoría de la base de datos.
     * @return Número de filas afectadas.
     */
    public int delete() {
        int filasAfectadas = 0;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            filasAfectadas = st.executeUpdate("DELETE FROM categoria WHERE id=" + this.getId());
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