package com.example;

import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

/**
 * Controlador para la ventana emergente de añadir producto.
 * Permite seleccionar imagen, introducir datos y guardar un nuevo producto en la base de datos.
 * 
 * Gestiona la carga de categorías, la selección de imagen y la validación de datos antes de guardar.
 * 
 * @author Castilla
 */
public class VentanaEmergenteControllerAdd {

    private PrimaryController controladorPrincipal;

    /**
     * Establece el controlador principal para poder actualizar la vista principal tras añadir un producto.
     * @param controladorPrincipal Controlador principal.
     */
    public void setControladorPrincipal(PrimaryController controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }

    private String rutaImagenSeleccionada = ""; // Variable para almacenar la ruta de la imagen seleccionada
    @FXML private StackPane customFileUpload; // Para la imagen
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private ChoiceBox<String> choice_box_categoria;

    private Map<String, Integer> categoriaNombreToId = new HashMap<>();

    /**
     * Inicializa la ventana emergente, cargando las categorías desde la base de datos.
     */
    @FXML
    private void initialize() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final", "root", "root");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, nombre FROM categoria");

            while (rs.next()) {
                String nombreCategoria = rs.getString("nombre");
                int idCategoria = rs.getInt("id");
                choice_box_categoria.getItems().add(nombreCategoria);
                categoriaNombreToId.put(nombreCategoria, idCategoria);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre un selector de archivos para elegir una imagen y la muestra como fondo en el componente.
     * @param event Evento de ratón.
     */
    @FXML 
    private void seleccionarImagen(MouseEvent event) {
        // Obtener la ruta absoluta de la carpeta "images" dentro de resources
        File initialDirectory;
        try {
            // Busca la carpeta "images" dentro de resources, independientemente de dónde se ejecute el jar/proyecto
            initialDirectory = new File(getClass().getResource("/com/example/images").toURI());
        } catch (Exception e) {
            // Si no se encuentra, usar una ruta relativa como fallback
            initialDirectory = new File("src/main/resources/com/example/images");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Imágenes", "*.png", "*.jpg", "*.jpeg")
        );
        if (initialDirectory.exists()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }

        File selectedFile = fileChooser.showOpenDialog(customFileUpload.getScene().getWindow());
        if (selectedFile != null) {
            // Solo guardar el nombre y la extensión del archivo
            rutaImagenSeleccionada = selectedFile.getName();

            // Cambiar el estilo del componente para indicar que se ha seleccionado un archivo 
            customFileUpload.setStyle("-fx-background-image: url('" + selectedFile.toURI().toString() + "'); -fx-background-size: cover; -fx-background-position: center; -fx-background-repeat: no-repeat; -fx-border-color: #1890ff; -fx-border-width: 2; -fx-border-style: dashed; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 35, 0, 0, 48);");
        }
    }

    /**
     * Añade un nuevo producto a la base de datos tras validar los campos.
     * Muestra alertas si hay errores o si el producto ya existe.
     */
    @FXML
    private void anadirProducto() {
        String nombre = txtNombre.getText();
        Double precio = Double.parseDouble(txtPrecio.getText());
        String fotografia = rutaImagenSeleccionada;
        String nombreCategoriaSeleccionada = choice_box_categoria.getValue();

        // Obtener el id real de la categoría seleccionada
        Integer idCategoria = categoriaNombreToId.get(nombreCategoriaSeleccionada);

        try {
            String precioStr = txtPrecio.getText().trim();
            if (precioStr.isEmpty()) {
                mostrarAlerta("Error", "El campo precio no puede estar vacío.");
                return;
            }
            if (nombre == null || nombre.trim().isEmpty() || precio == null || precio <= 0 || fotografia == null || fotografia.trim().isEmpty() || idCategoria == null) {
                mostrarAlerta("Error", "Por favor, rellene todos los campos correctamente.");
                return;
            }
            
            // Comprobar si el producto ya existe
            ResultSet rs = Producto.getConnection().createStatement().executeQuery(
                "SELECT * FROM producto WHERE nombre='" + nombre + "'"
            );
            if (rs.next()) {
                mostrarAlerta("Error", "El producto ya está registrado.");
                return;
            } else {
                int id = Producto.getLastId() + 1;
                Producto producto = new Producto(id, nombre, precio.toString(), fotografia, idCategoria);
                producto.save();
                controladorPrincipal.cargarProductosDesdeBaseDeDatos();
                
            }

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar el producto. Verifique los datos ingresados.");
            e.printStackTrace();
        }

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
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

}