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
 * Clase que representa un producto en el sistema.
 * Permite la gestiÃ³n de productos en la base de datos.
 * 
 * Un producto tiene un identificador, nombre, precio, imagen y categorÃ­a.
 * 
 * @author Castilla
 */
public class Producto {
    private IntegerProperty id;
    private StringProperty nombre;
    private StringProperty precio;
    private StringProperty imagen;
    private IntegerProperty id_categoria;

    /**
     * Crea un nuevo producto.
     * @param id Identificador del producto.
     * @param nombre Nombre del producto.
     * @param precio Precio del producto.
     * @param imagen Ruta o nombre de la imagen del producto.
     * @param id_categoria Identificador de la categorÃ­a del producto.
     */
    public Producto(int id, String nombre, String precio, String imagen, int id_categoria) {
        this.id = new SimpleIntegerProperty(id);
        this.nombre = new SimpleStringProperty(nombre);
        this.precio = new SimpleStringProperty(precio);
        this.imagen = new SimpleStringProperty(imagen);
        this.id_categoria = new SimpleIntegerProperty(id_categoria);
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
     * Devuelve la propiedad precio.
     * @return StringProperty del precio.
     */
    public StringProperty precioProperty() {
        return precio;
    }

    /**
     * Devuelve la propiedad imagen.
     * @return StringProperty de la imagen.
     */
    public StringProperty imagenProperty() {
        return imagen;
    }

    /**
     * Devuelve la propiedad id_categoria.
     * @return IntegerProperty del id_categoria.
     */
    public IntegerProperty id_categoriaProperty() {
        return id_categoria;
    }

    // Getters y Setters

    /**
     * Obtiene el id del producto.
     * @return id del producto.
     */
    public int getId() {
        return id.get();
    }

    /**
     * Establece el id del producto.
     * @param id Nuevo id.
     */
    public void setId(int id) {
        this.id.set(id);
    }

    /**
     * Obtiene el nombre del producto.
     * @return nombre del producto.
     */
    public String getNombre() {
        return nombre.get();
    }

    /**
     * Establece el nombre del producto.
     * @param nombre Nuevo nombre.
     */
    public void setNombre(String nombre) {
        this.nombre.set(nombre);
    }

    /**
     * Obtiene el precio del producto.
     * @return precio del producto.
     */
    public String getPrecio() {
        return precio.get();
    }

    /**
     * Establece el precio del producto.
     * @param precio Nuevo precio.
     */
    public void setPrecio(String precio) {
        this.precio.set(precio);
    }

    /**
     * Obtiene la imagen del producto.
     * @return imagen del producto.
     */
    public String getImagen() {
        return imagen.get();
    }

    /**
     * Establece la imagen del producto.
     * @param imagen Nueva imagen.
     */
    public void setImagen(String imagen) {
        this.imagen.set(imagen);
    }

    /**
     * Obtiene el id de la categorÃ­a del producto.
     * @return id de la categorÃ­a.
     */
    public int getIdCategoria() {
        return id_categoria.get();
    }

    /**
     * Establece el id de la categorÃ­a del producto.
     * @param id_categoria Nuevo id de la categorÃ­a.
     */
    public void setIdCategoria(int id_categoria) {
        this.id_categoria.set(id_categoria);
    }

    /**
     * Obtiene todos los productos de la base de datos y los aÃ±ade a la lista proporcionada.
     * @param listaProductos Lista donde se aÃ±adirÃ¡n los productos.
     */
    public static void getAll(ObservableList<Producto> listaProductos) {
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM producto");

            while (rs.next()) {
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String precio = rs.getString("precio");
                String imagen = rs.getString("imagen");
                int id_categoria = rs.getInt("id_categoria");
                listaProductos.add(new Producto(id, nombre, precio, imagen, id_categoria));
            }
            conn.close();
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
        }
    }

    /**
     * Obtiene un producto por su id.
     * @param id Id del producto.
     * @return Objeto Producto o null si no existe.
     */
    public static Producto get(int id) {
        Producto producto = null;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM producto WHERE id = " + id);

            if (rs.next()) {
                producto = new Producto(
                    rs.getInt("id"),
                    rs.getString("nombre"),
                    rs.getString("precio"),
                    rs.getString("imagen"),
                    rs.getInt("id_categoria")
                );
            }
            conn.close();
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
        }

        return producto;
    }

    /**
     * Busca productos por nombre y los aÃ±ade a la lista proporcionada.
     * @param txt Texto a buscar en el nombre.
     * @param listaProductos Lista donde se aÃ±adirÃ¡n los productos encontrados.
     */
    public static void get(String txt, ObservableList<Producto> listaProductos) {
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM producto WHERE nombre LIKE '%" + txt + "%'");

            while (rs.next()) {
                listaProductos.clear();
                int id = rs.getInt("id");
                String nombre = rs.getString("nombre");
                String precio = rs.getString("precio");
                String imagen = rs.getString("imagen");
                int id_categoria = rs.getInt("id_categoria");
                listaProductos.add(new Producto(id, nombre, precio, imagen, id_categoria));

            }
            conn.close();
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
        }

    }

    /**
     * Obtiene el Ãºltimo id registrado en la tabla producto.
     * @return Ãšltimo id o 0 si no hay registros.
     */
    public static int getLastId() {
        int lastId = 0;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT MAX(id) AS last_id FROM producto");

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
     * Guarda el producto en la base de datos.
     * Si existe, lo actualiza; si no, lo inserta.
     * @return NÃºmero de filas afectadas.
     */
    public int save() {
        int filasAfectadas = 0;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT * FROM producto WHERE id=" + this.getId());
            if (rs.next()) {
                // Si el producto ya existe, lo modificamos
                filasAfectadas = st.executeUpdate(
                    "UPDATE producto SET nombre='" + this.getNombre() +
                    "', precio='" + this.getPrecio() +
                    "', imagen='" + this.getImagen() +
                    "', id_categoria=" + this.getIdCategoria() +
                    " WHERE id=" + this.getId()
                );
            } else {
                // Si el producto no existe, lo aÃ±adimos
                filasAfectadas = st.executeUpdate(
                    "INSERT INTO producto (nombre, precio, imagen, id_categoria) VALUES ('" +
                    this.getNombre() + "', '" +
                    this.getPrecio() + "', '" +
                    this.getImagen() + "', " +
                    this.getIdCategoria() + ")"
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
     * Elimina el producto de la base de datos.
     * @return NÃºmero de filas afectadas.
     */
    public int delete() {
        int filasAfectadas = 0;
        try {
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            filasAfectadas = st.executeUpdate("DELETE FROM producto WHERE id=" + this.getId());
            conn.close();
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
        }
        return filasAfectadas;
    }

    /**
     * Muestra un mensaje de error genÃ©rico.
     */
    public static void mensajeError() {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setContentText("ERROR");
        alert.showAndWait();
    }

    /**
     * Obtiene una conexiÃ³n a la base de datos.
     * @return Objeto Connection o null si falla la conexiÃ³n.
     */
    public static Connection getConnection() {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/proyecto_final",
                                                "tpv_app", "tpv_app_123");
        } catch (Exception e) {
            mensajeError();
            e.printStackTrace();
        }
        return conn;
    }
}