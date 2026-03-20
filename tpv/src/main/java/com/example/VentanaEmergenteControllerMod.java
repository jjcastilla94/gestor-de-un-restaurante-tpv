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
 * Controlador para la ventana emergente de modificaciÃ³n de producto.
 * Permite modificar los datos de un producto existente en la base de datos.
 * 
 * Gestiona la carga de categorÃ­as, la selecciÃ³n de imagen, la validaciÃ³n de datos y la actualizaciÃ³n del producto.
 * 
 * @author Castilla
 */
public class VentanaEmergenteControllerMod {

    private PrimaryController controladorPrincipal;

    /**
     * Establece el controlador principal para poder actualizar la vista principal tras modificar un producto.
     * @param controladorPrincipal Controlador principal.
     */
    public void setControladorPrincipal(PrimaryController controladorPrincipal) {
        this.controladorPrincipal = controladorPrincipal;
    }

    private Producto producto;

    /**
     * Establece el producto a modificar y rellena los campos con sus datos actuales.
     * @param producto Producto a modificar.
     */
    public void setProducto(Producto producto) {
        this.producto = producto;

        // Rellenar los campos con los datos actuales del producto
        txtNombre.setText(producto.getNombre());
        txtPrecio.setText(producto.getPrecio());
        rutaImagenSeleccionada = producto.getImagen();

        // Mostrar la imagen actual en el customFileUpload (opcional)
        if (producto.getImagen() != null && !producto.getImagen().isEmpty()) {
            File imagenFile = new File("src/main/resources/com/example/images/" + producto.getImagen());
            if (imagenFile.exists()) {
                customFileUpload.setStyle("-fx-background-image: url('file:///" + imagenFile.getAbsolutePath().replace("\\", "/") + "'); -fx-background-size: cover; -fx-background-position: center; -fx-background-repeat: no-repeat; -fx-border-color: #1890ff; -fx-border-width: 2; -fx-border-style: dashed; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 35, 0, 0, 48);");
            }
        }

        // Seleccionar la categorÃ­a en el ChoiceBox
        if (producto.getIdCategoria() != 0 && choice_box_categoria != null) {
            for (Map.Entry<String, Integer> entry : categoriaNombreToId.entrySet()) {
                if (entry.getValue().equals(producto.getIdCategoria())) {
                    choice_box_categoria.setValue(entry.getKey());
                }
            }
        }
    }

    private String rutaImagenSeleccionada = ""; // Variable para almacenar la ruta de la imagen seleccionada
    @FXML private StackPane customFileUpload; // Para la imagen
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrecio;
    @FXML private ChoiceBox<String> choice_box_categoria;

    private Map<String, Integer> categoriaNombreToId = new HashMap<>();

    /**
     * Inicializa la ventana emergente, cargando las categorÃ­as desde la base de datos.
     */
    @FXML
    private void initialize() {
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/proyecto_final", "tpv_app", "tpv_app_123");
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
     * @param event Evento de ratÃ³n.
     */
    @FXML 
    private void seleccionarImagen(MouseEvent event) {
        // Obtener la ruta absoluta de la carpeta "images" dentro de resources
        File initialDirectory;
        try {
            // Busca la carpeta "images" dentro de resources, independientemente de dÃ³nde se ejecute el jar/proyecto
            initialDirectory = new File(getClass().getResource("/com/example/images").toURI());
        } catch (Exception e) {
            // Si no se encuentra, usar una ruta relativa como fallback
            initialDirectory = new File("src/main/resources/com/example/images");
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Seleccionar Imagen");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("ImÃ¡genes", "*.png", "*.jpg", "*.jpeg")
        );
        if (initialDirectory.exists()) {
            fileChooser.setInitialDirectory(initialDirectory);
        }

        File selectedFile = fileChooser.showOpenDialog(customFileUpload.getScene().getWindow());
        if (selectedFile != null) {
            // Solo guardar el nombre y la extensiÃ³n del archivo
            rutaImagenSeleccionada = selectedFile.getName();

            // Cambiar el estilo del componente para indicar que se ha seleccionado un archivo 
            customFileUpload.setStyle("-fx-background-image: url('" + selectedFile.toURI().toString() + "'); -fx-background-size: cover; -fx-background-position: center; -fx-background-repeat: no-repeat; -fx-border-color: #1890ff; -fx-border-width: 2; -fx-border-style: dashed; -fx-border-radius: 10; -fx-background-radius: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 35, 0, 0, 48);");
        }
    }

    /**
     * Modifica el producto existente en la base de datos tras validar los campos.
     * Muestra alertas si hay errores o si la modificaciÃ³n es exitosa.
     */
    @FXML
    private void modificarProducto() {
        String nombre = txtNombre.getText();
        Double precio = Double.parseDouble(txtPrecio.getText());
        String fotografia = rutaImagenSeleccionada;
        String nombreCategoriaSeleccionada = choice_box_categoria.getValue();

        // Obtener el id real de la categorÃ­a seleccionada
        Integer idCategoria = categoriaNombreToId.get(nombreCategoriaSeleccionada);

        try {
            if (nombre.isEmpty() || precio == null || precio <= 0 || fotografia.isEmpty() || idCategoria == null) {
                mostrarAlerta("Error", "Por favor, rellene todos los campos correctamente.");
                return;
            }
            
            // Actualizar el producto existente
            int id = producto.getId();
            Producto productoModificado = new Producto(id, nombre, precio.toString(), fotografia, idCategoria);
            productoModificado.save();
            controladorPrincipal.cargarProductosDesdeBaseDeDatos();
            int idMesa = controladorPrincipal.getIdMesa(); 
            controladorPrincipal.cargarComandasDesdeBaseDeDatos(idMesa);

            // Mostrar un mensaje de Ã©xito
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Ã‰xito");
            alert.setHeaderText(null);
            alert.setContentText("Producto modificado correctamente.");
            alert.showAndWait();

            cerrarVentana();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo guardar el producto. Verifique los datos ingresados.");
            e.printStackTrace();
        }

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
        Stage stage = (Stage) txtNombre.getScene().getWindow();
        stage.close();
    }

}