package com.example;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.IntegerStringConverter;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * Controlador principal de la aplicación TPV.
 * Gestiona la carga y visualización de productos, comandas y tickets,
 * así como la interacción con las ventanas emergentes y la generación de PDFs.
 * Permite añadir, modificar y eliminar productos y comandas, aplicar descuentos,
 * guardar tickets y mostrar detalles de ventas.
 */
public class PrimaryController {
    
    private ObservableList<Producto> listaProductos = FXCollections.observableArrayList();
    private ObservableList<Comanda> listaComandas = FXCollections.observableArrayList();

    @FXML private Label lblCajero;
    @FXML private Label lblFechaHora;
    @FXML private TextField txtBusqueda;
    private static Producto productoSeleccionado;

    @FXML private FlowPane productosContainer; 
    @FXML private FlowPane productosContainer2;
    @FXML private FlowPane productosContainer3;
    @FXML private FlowPane productosContainer4;
    @FXML private FlowPane productosContainer5;
    @FXML private FlowPane productosContainer6;

    @FXML private ComboBox<String> comboMesas;

    @FXML private TableView<Comanda> tablaComandas;
    @FXML private TableColumn<Comanda, Integer> colCantidad;
    @FXML private TableColumn<Comanda, String> colNombre;
    @FXML private TableColumn<Comanda, Double> colPrecioUnidad;
    @FXML private TableColumn<Comanda, Double> colTotal;

    
    public static void setProductoSeleccionado(Producto producto) {
        productoSeleccionado = producto;
        
    }

    public static Producto getProductoSeleccionado() {
        return productoSeleccionado;
    }

    private Map<String, Integer> MesasNombreToId = new HashMap<>();
    
    /**
     * Inicializa la interfaz principal, carga productos y mesas,
     * y configura los listeners y la actualización de la hora.
     */
    @FXML
    public void initialize() {
        // Obtener el usuario que inició sesión desde una clase Login 
        LoginController lg = new LoginController();
        String usuarioActual = lg.getUsuarioActual(); // Método definido en LoginController para devolver el usuario actual
        lblCajero.setText(usuarioActual);
        cargarProductosDesdeBaseDeDatos();

        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final", "root", "root");
            Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery("SELECT id, nombre FROM mesa");

            while (rs.next()) {
                String nombreMesa = rs.getString("nombre");
                int idMesa = rs.getInt("id");
                comboMesas.getItems().add(nombreMesa);
                MesasNombreToId.put(nombreMesa, idMesa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Cargar las comandas desde la base de datos segun la mesa seleccionada
        comboMesas.setOnAction(event -> {
            String mesaSeleccionada = comboMesas.getValue();
            int idMesa = MesasNombreToId.get(mesaSeleccionada);
            cargarComandasDesdeBaseDeDatos(idMesa);

            
        });

        // Que se me vaya actualizando la hora cada segundo
        Thread hilo = new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(1000);
                    java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
                    String fechaHoraActual = ahora.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    javafx.application.Platform.runLater(() -> {
                        lblFechaHora.setText(fechaHoraActual);
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        hilo.setDaemon(true);
        hilo.start();


    }

    /**
     * Actualiza la vista principal recargando la escena.
     */
    @FXML
    public void actualizar(){
        try {
            // Actualiza la vista de eventos
            App.setRoot("primary.fxml");
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo actualizar la vista");
        }
    }

    /**
     * Cierra la sesión del usuario actual tras confirmación.
     */
    @FXML
    public void logOut(){
        try {
            //pregunte primero con un alert si esta seguro de cerrar sesion
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Cerrar sesión");
            alert.setHeaderText("¿Está seguro de que desea cerrar sesión?");
            alert.setContentText("Si sales, se cerrará tu sesión actual");
            alert.showAndWait();
            //si elige aceptar se cerrara la aplicacion
            if (alert.getResult().getText().equals("Aceptar")) {
                try {
                    App.setRoot("login.fxml");
                    App.getStage().setTitle("Iniciar sesión");
                    App.getStage().setResizable(false);
                    App.getStage().setMaximized(false);
                } catch (IOException e) {
                    e.printStackTrace();
                    mostrarAlerta("Error", "No se pudo cerrar sesión");
                }
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo cerrar sesión");
            e.printStackTrace();
        }  
    }

    /**
     * Devuelve el id de la mesa seleccionada en el ComboBox.
     * @return id de la mesa seleccionada o 0 si no hay ninguna seleccionada.
     */
    public int getIdMesa() {
        String mesaSeleccionada = comboMesas.getValue();
        int idMesa = 0;
        if (mesaSeleccionada != null) {
             idMesa = MesasNombreToId.get(mesaSeleccionada);
        } else {
            idMesa = 0;
        }

        return idMesa;
        
    }

    /**
     * Carga las comandas de la base de datos para la mesa indicada
     * y actualiza la tabla y los totales.
     * @param idMesa id de la mesa seleccionada
     */
    public void cargarComandasDesdeBaseDeDatos(int idMesa) {
            try {
                
                Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final", "root", "root");
                String sql = "SELECT * FROM comanda WHERE id_mesa = ?";
                PreparedStatement stmt = conexion.prepareStatement(sql);
                stmt.setInt(1, idMesa);
                ResultSet rs = stmt.executeQuery();
                listaComandas.clear();
                while (rs.next()) {
                    int id_producto = rs.getInt("id_producto");
                    String precio_total = rs.getString("precio_total");
                    int cantidad = rs.getInt("cantidad");

                    // Ajusta los parámetros según el constructor disponible en Comanda
                    Comanda comanda = new Comanda(id_producto, idMesa,  precio_total, cantidad); // ejemplo: (int idProducto, int cantidad, String precioTotal)
                    listaComandas.add(comanda);
   
                }
                
                // Configurar las columnas de la tabla
                colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
                colNombre.setCellValueFactory(cellData -> {
                    // Obtener el nombre del producto a partir del id_producto de la comanda
                    int idProducto = cellData.getValue().getId_producto();
                    String nombreProducto = obtenerNombreProductoPorId(idProducto);
                    return new javafx.beans.property.SimpleStringProperty(nombreProducto);
                });
                colPrecioUnidad.setCellValueFactory(cellData -> {
                    int idProducto = cellData.getValue().getId_producto();
                    String precioUnidadStr = obtenerPrecioProductoPorId(idProducto);
                    double precioUnidad = 0.0;
                    try {
                        precioUnidad = Double.parseDouble(precioUnidadStr);
                    } catch (NumberFormatException e) {
                        precioUnidad = 0.0;
                    }
                    return new javafx.beans.property.SimpleDoubleProperty(precioUnidad).asObject();
                });
                colTotal.setCellValueFactory(cellData -> {
                    int idProducto = cellData.getValue().getId_producto();
                    int cantidad = cellData.getValue().getCantidad();
                    double precio = 0.0;
                    try {
                        precio = Double.parseDouble(obtenerPrecioProductoPorId(idProducto));
                    } catch (NumberFormatException e) {
                        precio = 0.0;
                    }
                    double total = precio * cantidad;
                    // Redondear a dos decimales
                    total = Math.round(total * 100.0) / 100.0;
                    return new javafx.beans.property.SimpleDoubleProperty(total).asObject();
                });
                

                rs.close();
                stmt.close();
                conexion.close();
                tablaComandas.setItems(listaComandas);
                actualizarCuenta(); // Actualiza la cuenta total al cargar las comandas
                actualizarIva(); // Actualiza el IVA al cargar las comandas
                actualizarTotal(); // Actualiza el total al cargar las comandas
                

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    /**
     * Carga todos los productos desde la base de datos y los muestra en los contenedores correspondientes.
     */
    public void cargarProductosDesdeBaseDeDatos() {
        try {
            // Vacía los contenedores antes de volver a añadir productos
                productosContainer.getChildren().clear();
                productosContainer2.getChildren().clear();
                productosContainer3.getChildren().clear();
                productosContainer4.getChildren().clear();
                productosContainer5.getChildren().clear();
                productosContainer6.getChildren().clear();

            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final", "root", "root");
            String sql = "SELECT id, nombre, precio, imagen, id_Categoria FROM producto";
            PreparedStatement stmt = conexion.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
            int id = rs.getInt("id");
            String nombre = rs.getString("nombre");
            double precio = rs.getDouble("precio");
            String imagen = rs.getString("imagen");
            int categoriaId = rs.getInt("id_Categoria");

            Producto producto = new Producto(id, nombre, String.valueOf(precio), imagen, categoriaId);
            VBox productoCard = crearProductoCard(producto);




            // Añadir el producto al contenedor correspondiente según la categoría
            switch (categoriaId) {
                case 1:
                productosContainer.getChildren().add(productoCard);
                break;
                case 2:
                productosContainer2.getChildren().add(productoCard);
                break;
                case 3:
                productosContainer3.getChildren().add(productoCard);
                break;
                case 4:
                productosContainer4.getChildren().add(productoCard);
                break;
                case 5:
                productosContainer5.getChildren().add(productoCard);
                break;
                case 6:
                productosContainer6.getChildren().add(productoCard);
                break;
                default:
                // Si hay una categoría desconocida, puedes ignorarla o manejarla aquí
                mostrarAlerta("Error", "Categoría desconocida para el producto: " + nombre);
                break;
            }
            }
            rs.close();
            stmt.close();
            conexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Crea la tarjeta visual de un producto para mostrarlo en la interfaz.
     * @param producto Producto a mostrar
     * @return VBox con la información y la imagen del producto
     */
    private VBox crearProductoCard(Producto producto) {
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.TOP_CENTER);
        vbox.setSpacing(5);
        vbox.setPrefSize(150, 180);
        vbox.setStyle("-fx-background-color: white; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 5, 0, 0, 2); -fx-background-radius: 8px; -fx-padding: 15px; -fx-cursor: hand; -fx-border-color: #dee2e6; -fx-border-radius: 8px;");

        ImageView imageView = new ImageView();
        imageView.setFitWidth(80);
        imageView.setFitHeight(80);
        imageView.setPreserveRatio(true);

        String ruta = "/com/example/images/" + producto.getImagen();
        InputStream is = getClass().getResourceAsStream(ruta);
        if (is != null) {
            imageView.setImage(new Image(is));
        }

        Label nombreLabel = new Label(producto.getNombre());
        nombreLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2c3e50; -fx-padding: 5 0 0 0;");
        nombreLabel.setTextAlignment(TextAlignment.CENTER);
        nombreLabel.setWrapText(true);

        Label precioLabel = new Label(String.format("%.2f€", Double.parseDouble(producto.getPrecio())));
        precioLabel.setStyle("-fx-font-size: 13px; -fx-text-fill: #2989d8; -fx-font-weight: bold;");

        vbox.getChildren().addAll(imageView, nombreLabel, precioLabel);

        vbox.setUserData(producto);

        vbox.setOnMouseClicked(event -> {
            productoSeleccionado = (Producto) vbox.getUserData();
            
            PrimaryController.setProductoSeleccionado(productoSeleccionado);
            
        });
        return vbox;
    }

    /**
     * Busca productos por nombre y actualiza la vista con los resultados.
     */
    @FXML public void buscarProducto() {
        
        String txt = txtBusqueda.getText().toLowerCase(); // Obtener el texto de búsqueda y convertirlo a minúsculas
        if (txt.isEmpty()) {
            actualizar(); 
            return;
        }

        txtBusqueda.setText("");
        Producto.getAll(listaProductos);
        
        ObservableList<Producto> listaFiltrada = FXCollections.observableArrayList();
        for (Producto producto : listaProductos) {
            if (producto.getNombre().toLowerCase().contains(txt)) {
                listaFiltrada.add(producto);
            }
        }
        if (listaFiltrada.isEmpty()) {
            mostrarAlerta("Error", "No se encontraron productos que coincidan con la búsqueda.");
        } else {
            productosContainer.getChildren().clear();
            productosContainer2.getChildren().clear();
            productosContainer3.getChildren().clear();
            productosContainer4.getChildren().clear();
            productosContainer5.getChildren().clear();
            productosContainer6.getChildren().clear();
            // Recorre la lista filtrada y añade los productos a los contenedores correspondientes
            for (Producto producto : listaFiltrada) {
                // Suponiendo que Producto tiene un método getCategoriaId() o similar
                int categoriaId = producto.getIdCategoria();
                VBox productoCard = crearProductoCard(producto);
                if (categoriaId == 1) {
                    productosContainer.getChildren().add(productoCard); // Bebidas
                } else if (categoriaId == 2) {
                    productosContainer2.getChildren().add(productoCard); // Entrantes
                } else if (categoriaId == 3) {
                    productosContainer3.getChildren().add(productoCard); // Primer Plato
                } else if (categoriaId == 4) {
                    productosContainer4.getChildren().add(productoCard); // Segundo Plato
                } else if (categoriaId == 5) {
                    productosContainer5.getChildren().add(productoCard); // Postre
                } else if (categoriaId == 6) {
                    productosContainer6.getChildren().add(productoCard); // Café
                }
            }
           
        }
       
    }

    /**
     * Abre la ventana emergente para añadir un nuevo producto.
     */
    @FXML public void abrirVentanaEmergenteAdd() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("anadirProducto.fxml"));
            Parent root = loader.load();

            VentanaEmergenteControllerAdd controller = loader.getController();
            controller.setControladorPrincipal(this);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal hasta cerrar la emergente
            stage.setResizable(false);
            stage.setTitle("Añadir Producto");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Espera a que se cierre la ventana emergente
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la ventana emergente para modificar el producto seleccionado.
     */
    @FXML public void abrirVentanaEmergenteMod() {
        if (productoSeleccionado == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText(null);
            alert.setContentText("No puedes modificar un producto mientras no hay uno seleccionado.");
            alert.showAndWait();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("modificarProducto.fxml"));
            Parent root = loader.load();

            VentanaEmergenteControllerMod controller = loader.getController();
            controller.setControladorPrincipal(this);
            controller.setProducto(productoSeleccionado);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setResizable(false);
            stage.setTitle("Modificar Producto");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Espera a que se cierre la ventana emergente
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina el producto seleccionado tras confirmación y actualiza la vista.
     */
    @FXML public void borrarProducto() {
        if (productoSeleccionado == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText(null);
            alert.setContentText("No puedes eliminar un producto mientras no hay uno seleccionado.");
            alert.showAndWait();
            return;
        }
        try {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Eliminar Producto");
            alert.setHeaderText("¿Estás seguro de que deseas eliminar este producto?");
            alert.setContentText("Esta acción no se puede deshacer.");
            alert.showAndWait();

            if (alert.getResult().getText().equals("Aceptar")) {
                // ...existing code...
                int idProducto = productoSeleccionado.getId();
                Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final", "root", "root");
                // Primero elimina de comanda
                String sqlComanda = "DELETE FROM comanda WHERE id_producto = ?";
                PreparedStatement stmtComanda = conexion.prepareStatement(sqlComanda);
                stmtComanda.setInt(1, idProducto);
                stmtComanda.executeUpdate();
                stmtComanda.close();

                
                productoSeleccionado.delete();
                cargarComandasDesdeBaseDeDatos(getIdMesa());
                cargarProductosDesdeBaseDeDatos();
                //actualizar();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Añade el producto seleccionado a la comanda de la mesa actual.
     */
    @FXML public void anadirATicket() {
        if (productoSeleccionado == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText(null);
            alert.setContentText("No puedes añadir un producto a la comanda mientras no hay uno seleccionado.");
            alert.showAndWait();
            return;
        }
        try {
            // Obtener el id de la mesa seleccionada
            String mesaSeleccionada = comboMesas.getValue();
            if (mesaSeleccionada == null) {
                mostrarAlerta("Advertencia", "Selecciona una mesa antes de añadir productos.");
                return;
            }
            int idMesa = MesasNombreToId.get(mesaSeleccionada);
            // Obtener el id del producto seleccionado
            int idProducto = productoSeleccionado.getId();

            String sql = "SELECT cantidad FROM comanda WHERE id_mesa = ? AND id_producto = ?";
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final", "root", "root");
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, idMesa);
            stmt.setInt(2, idProducto);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                // El producto ya está en la comanda de esa mesa, actualiza cantidad y precio_total
                int cantidadActual = rs.getInt("cantidad");
                int nuevaCantidad = cantidadActual + 1;
                double precioUnitario = Double.parseDouble(productoSeleccionado.getPrecio());
                double nuevoPrecioTotal = precioUnitario * nuevaCantidad;

                String sqlUpdate = "UPDATE comanda SET cantidad = ?, precio_total = ? WHERE id_producto = ? AND id_mesa = ?";
                PreparedStatement stmtUpdate = conexion.prepareStatement(sqlUpdate);
                stmtUpdate.setInt(1, nuevaCantidad);
                stmtUpdate.setDouble(2, nuevoPrecioTotal);
                stmtUpdate.setInt(3, idProducto);
                stmtUpdate.setInt(4, idMesa);
                stmtUpdate.executeUpdate();
                stmtUpdate.close();
            } else {
                // El producto no está en la comanda de esa mesa, inserta uno nuevo
                String sqlInsert = "INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (?, ?, ?, ?)";
                PreparedStatement stmtInsert = conexion.prepareStatement(sqlInsert);
                stmtInsert.setInt(1, idProducto);
                stmtInsert.setInt(2, idMesa);
                stmtInsert.setDouble(3, Double.parseDouble(productoSeleccionado.getPrecio()));
                stmtInsert.setInt(4, 1);
                stmtInsert.executeUpdate();
                stmtInsert.close();
            }
            
            // Actualizar la tabla de comandas
            cargarComandasDesdeBaseDeDatos(idMesa);

            rs.close();
            stmt.close();
            conexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo añadir el producto a la comanda.");
        }
    }

    /**
     * Genera un PDF con el resumen de la comanda actual, mostrando productos, totales y empleado.
     * Abre el PDF automáticamente tras generarlo.
     */
    @FXML
    public void generarPDF() {
        String mesaSeleccionada = comboMesas.getValue();
        if (mesaSeleccionada == null) {
            mostrarAlerta("Advertencia", "Selecciona una mesa antes de generar el PDF.");
            return;
        }

        try {
            // Crear el documento PDF
            Document document = new Document();
            String nombreArchivo = "ticket_" + mesaSeleccionada + "_" +
                    java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".pdf";
            PdfWriter.getInstance(document, new FileOutputStream(nombreArchivo));
            document.open();

            // Añadir logo (si tienes uno en resources/com/example/images/logo.png)
            try {
                String logoPath = getClass().getResource("/com/example/images/max.png").toString();
                com.itextpdf.text.Image logo = com.itextpdf.text.Image.getInstance(logoPath);
                logo.scaleToFit(80, 80);
                logo.setAlignment(Element.ALIGN_CENTER);
                document.add(logo);
            } catch (Exception ex) {
                // Si no hay logo, no pasa nada
            }

            // Añadir título estilizado
            Font titleFont = new Font(Font.FontFamily.HELVETICA, 22, Font.BOLD, new BaseColor(41, 137, 216));
            Paragraph title = new Paragraph("TICKET DE COMANDA", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(10);
            document.add(title);

            // Línea separadora
            Paragraph linea = new Paragraph("------------------------------------------------------------");
            linea.setAlignment(Element.ALIGN_CENTER);
            document.add(linea);

            // Información de la mesa y fecha
            Font infoFont = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL, BaseColor.DARK_GRAY);
            Paragraph info = new Paragraph(
                    "Mesa : " + mesaSeleccionada + "\n" +
                    "Fecha : " + java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")) + "\n" +
                    "Atendido por : " + lblCajero.getText(),
                    infoFont
            );
            info.setAlignment(Element.ALIGN_CENTER);
            info.setSpacingAfter(10);
            document.add(info);

            // Otra línea separadora
            document.add(linea);

            // Tabla de productos con fondo de encabezado
            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.setSpacingBefore(10f);
            table.setSpacingAfter(10f);
            table.setWidths(new float[]{1.2f, 3.5f, 2f, 2f});

            // Encabezados con fondo azul
            Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD, BaseColor.WHITE);
            BaseColor headerBg = new BaseColor(41, 137, 216);
            String[] headers = {"Cantidad", "Producto", "Precio Unidad", "Total"};
            for (String header : headers) {
                PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cell.setBackgroundColor(headerBg);
                cell.setPadding(7);
                table.addCell(cell);
            }

            // Filas de productos
            Font contentFont = new Font(Font.FontFamily.HELVETICA, 11);
            for (Comanda comanda : listaComandas) {
                String nombreProducto = obtenerNombreProductoPorId(comanda.getId_producto());
                double precioUnidad = Double.parseDouble(obtenerPrecioProductoPorId(comanda.getId_producto()));
                double total = precioUnidad * comanda.getCantidad();

                PdfPCell cantidadCell = new PdfPCell(new Phrase(String.valueOf(comanda.getCantidad()), contentFont));
                cantidadCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                cantidadCell.setPadding(5);
                table.addCell(cantidadCell);

                PdfPCell nombreCell = new PdfPCell(new Phrase(nombreProducto, contentFont));
                nombreCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                nombreCell.setPadding(5);
                table.addCell(nombreCell);

                PdfPCell precioCell = new PdfPCell(new Phrase(String.format("%.2f€", precioUnidad), contentFont));
                precioCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                precioCell.setPadding(5);
                table.addCell(precioCell);

                PdfPCell totalCell = new PdfPCell(new Phrase(String.format("%.2f€", total), contentFont));
                totalCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                totalCell.setPadding(5);
                table.addCell(totalCell);
            }

            document.add(table);

            // Totales destacados
            Font totalFont = new Font(Font.FontFamily.HELVETICA, 13, Font.BOLD, new BaseColor(41, 137, 216));
            Paragraph totales = new Paragraph();
            totales.setFont(totalFont);
            totales.setSpacingBefore(10);
            totales.add("Subtotal: " + lblSubtotal.getText() + "\n");
            totales.add("IVA (21%): " + lblIva.getText() + "\n");
            totales.add("TOTAL: " + lblTotal.getText() + "\n");
            totales.setAlignment(Element.ALIGN_RIGHT);
            document.add(totales);

            // Línea separadora
            document.add(linea);

            // Pie de página elegante
            Font footerFont = new Font(Font.FontFamily.HELVETICA, 10, Font.ITALIC, BaseColor.GRAY);
            Paragraph footer = new Paragraph("¡Gracias por su visita!\nwww.tu-restaurante.com", footerFont);
            footer.setAlignment(Element.ALIGN_CENTER);
            footer.setSpacingBefore(20);
            document.add(footer);

            document.close();

            // Mostrar mensaje de éxito
            Alert alert = new Alert(AlertType.INFORMATION);
            alert.setTitle("PDF Generado");
            alert.setHeaderText(null);
            alert.setContentText("El PDF se ha generado correctamente como: " + nombreArchivo);
            alert.showAndWait();

            // Abrir el PDF automáticamente si el sistema lo permite
            try {
                java.awt.Desktop desktop = java.awt.Desktop.getDesktop();
                desktop.open(new java.io.File(nombreArchivo));
            } catch (Exception ex) {
                mostrarAlerta("Aviso", "El PDF se generó, pero no se pudo abrir automáticamente.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo generar el PDF: " + e.getMessage());
        }
    }

    /**
     * Permite modificar la cantidad de productos en la comanda desde la tabla.
     */
    @FXML
    public void modificarTicket() {
        // Obtener el producto seleccionado de la tabla
        Comanda comandaSeleccionada = tablaComandas.getSelectionModel().getSelectedItem();
        if (comandaSeleccionada != null) {
            try {
                // Permitir la edición de las celdas directamente en la tabla
                tablaComandas.setEditable(true);
                colCantidad.setEditable(true);
                colCantidad.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));

                // Validar y guardar los cambios en la base de datos al confirmar la edición
                colCantidad.setOnEditCommit(event -> {
                    Integer nuevoValor = event.getNewValue();
                    if (nuevoValor == null) {
                        mostrarAlerta("Error", "La cantidad debe ser un número.");
                        cargarComandasDesdeBaseDeDatos(getIdMesa());
                        return;
                    }
                    if (nuevoValor < 1) {
                        mostrarAlerta("Error", "La cantidad no puede ser menor que 1.");
                        cargarComandasDesdeBaseDeDatos(getIdMesa());
                        return;
                    }
                    Comanda comanda = event.getRowValue();
                    try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final", "root", "root")) {
                        double precioUnitario = Double.parseDouble(obtenerPrecioProductoPorId(comanda.getId_producto()));
                        double nuevoPrecioTotal = precioUnitario * nuevoValor;
                        String sql = "UPDATE comanda SET cantidad = ?, precio_total = ? WHERE id_producto = ? AND id_mesa = ?";
                        PreparedStatement stmt = conexion.prepareStatement(sql);
                        stmt.setInt(1, nuevoValor);
                        stmt.setDouble(2, nuevoPrecioTotal);
                        stmt.setInt(3, comanda.getId_producto());
                        stmt.setInt(4, getIdMesa());
                        stmt.executeUpdate();
                        stmt.close();
                        cargarComandasDesdeBaseDeDatos(getIdMesa());
                    } catch (SQLException e) {
                        e.printStackTrace();
                        mostrarAlerta("Error", "No se pudo actualizar la cantidad.");
                    }
                });

                // Actualizar la tabla después de modificar la cantidad
                cargarComandasDesdeBaseDeDatos(getIdMesa());
            } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Ocurrió un error al modificar el evento.");
            }
        }
    }

    /**
     * Elimina el producto seleccionado de la comanda tras confirmación.
     */
    @FXML public void eliminarDelTicket(){
        // Si no hay producto seleccionado de la tabla, muestra un mensaje de advertencia
        Comanda comandaSeleccionada = tablaComandas.getSelectionModel().getSelectedItem();
        if (comandaSeleccionada == null) {
            Alert alert = new Alert(AlertType.WARNING);
            alert.setTitle("Advertencia");
            alert.setHeaderText(null);
            alert.setContentText("No puedes eliminar un producto de la comanda mientras no hay uno seleccionado.");
            alert.showAndWait();
            return;
        }
        try {
            // Obtener el id de la mesa seleccionada
            String mesaSeleccionada = comboMesas.getValue();
            if (mesaSeleccionada == null) {
                mostrarAlerta("Advertencia", "Selecciona una mesa antes de eliminar productos.");
                return;
            }
            int idMesa = MesasNombreToId.get(mesaSeleccionada);
            // Obtener el id del producto seleccionado
            int idProducto = comandaSeleccionada.getId_producto();

            // Mostrar alerta de confirmación antes de borrar
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Eliminar producto de la comanda");
            alert.setHeaderText("¿Estás seguro de que deseas eliminar este producto de la comanda?");
            alert.setContentText("Esta acción no se puede deshacer.");
            alert.showAndWait();

            if (alert.getResult().getText().equals("Aceptar")) {
                String sql = "DELETE FROM comanda WHERE id_producto = ? AND id_mesa = ?";
                Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final", "root", "root");
                PreparedStatement stmt = conexion.prepareStatement(sql);
                stmt.setInt(1, idProducto);
                stmt.setInt(2, idMesa);
                stmt.executeUpdate();
                stmt.close();  
            }

            // Actualizar la tabla de comandas
            cargarComandasDesdeBaseDeDatos(idMesa);

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo eliminar el producto de la comanda.");
        }
    }

    @FXML private Label lblSubtotal;

    /**
     * Actualiza el subtotal de la cuenta de la mesa seleccionada.
     */
    private void actualizarCuenta() {
        String mesaSeleccionada = comboMesas.getValue();
        if (mesaSeleccionada == null) {
            lblSubtotal.setText("0.00€");
            return;
        }
        int idMesa = MesasNombreToId.get(mesaSeleccionada);
        String sql = "SELECT SUM(precio_total) AS total FROM comanda WHERE id_mesa = ?";
        try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final", "root", "root");
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idMesa);
            ResultSet rs = stmt.executeQuery();
            double total = 0.0;
            if (rs.next()) {
                total = rs.getDouble("total");
            }
            lblSubtotal.setText(String.format("%.2f€", total));
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            lblSubtotal.setText("0.00€");
        }
    }

    @FXML private Label lblIva;
    /**
     * Actualiza el IVA de la cuenta de la mesa seleccionada.
     */
    private void actualizarIva() {
        String mesaSeleccionada = comboMesas.getValue();
        if (mesaSeleccionada == null) {
            lblIva.setText("0.00€");
            return;
        }
        int idMesa = MesasNombreToId.get(mesaSeleccionada);
        String sql = "SELECT SUM(precio_total) AS total FROM comanda WHERE id_mesa = ?";
        try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final", "root", "root");
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idMesa);
            ResultSet rs = stmt.executeQuery();
            double total = 0.0;
            if (rs.next()) {
                total = rs.getDouble("total");
            }
            double iva = total * 0.21; // 21% IVA
            lblIva.setText(String.format("%.2f€", iva));
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            lblIva.setText("0.00€");
        }
    }

    @FXML private Label lblTotal;
    /**
     * Actualiza el total (con IVA) de la cuenta de la mesa seleccionada.
     */
    private void actualizarTotal() {
        String mesaSeleccionada = comboMesas.getValue();
        if (mesaSeleccionada == null) {
            lblTotal.setText("0.00€");
            return;
        }
        int idMesa = MesasNombreToId.get(mesaSeleccionada);
        String sql = "SELECT SUM(precio_total) AS total FROM comanda WHERE id_mesa = ?";
        try (Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final", "root", "root");
             PreparedStatement stmt = conexion.prepareStatement(sql)) {
            stmt.setInt(1, idMesa);
            ResultSet rs = stmt.executeQuery();
            double total = 0.0;
            if (rs.next()) {
                total = rs.getDouble("total");
            }
            double iva = total * 0.21; // 21% IVA
            double totalConIva = total + iva;
            lblTotal.setText(String.format("%.2f€", totalConIva));
            rs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            lblTotal.setText("0.00€");
        }
    }

    public double cuentaConDescuento = 0.0;
    /**
     * Aplica un descuento al total de la cuenta de la mesa seleccionada.
     * @param totalConDescuento Total con descuento aplicado
     */
    public void setCuentaConDescuento(double totalConDescuento) {
        // Solo aplica el descuento a la mesa actualmente seleccionada
        String mesaSeleccionada = comboMesas.getValue();
        if (mesaSeleccionada == null) {
            mostrarAlerta("Advertencia", "Selecciona una mesa antes de aplicar el descuento.");
            return;
        }
        this.cuentaConDescuento = totalConDescuento;
        lblTotal.setText(String.format("%.2f€", totalConDescuento));
    }
          
    /**
     * Abre la ventana emergente para aplicar un porcentaje de descuento.
     */
    @FXML public void abrirVentanaEmergentePorcentaje() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("descuento.fxml"));
            Parent root = loader.load();

            VentanaEmergenteControllerPorcentaje controller = loader.getController();
            controller.setControladorPrincipal(this);

            // Extrae el valor numérico del Label lblTotal y pásalo como double
            String totalStr = lblTotal.getText().replace("€", "").replace(",", ".").trim();
            double total = 0.0;
            try {
                total = Double.parseDouble(totalStr);
            } catch (NumberFormatException e) {
                total = 0.0;
            }
            controller.setCuentaOriginal(total); // valorCuenta es el total actual de la cuenta

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal hasta cerrar la emergente
            stage.setResizable(false);
            stage.setTitle("Añadir Descuento");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Espera a que se cierre la ventana emergente
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Guarda el ticket de la comanda actual en la base de datos.
     * @param idMetodoPago id del método de pago seleccionado
     */
    @FXML public void guardarTicket(int idMetodoPago) {
        // Este metodo guarda la comanda en la tabla ticket de la base de datos
        String mesaSeleccionada = comboMesas.getValue();
        if (mesaSeleccionada == null) {
            mostrarAlerta("Advertencia", "Selecciona una mesa antes de guardar el ticket.");
            return;
        }
        int idMesa = MesasNombreToId.get(mesaSeleccionada);
        //la consulta ticket lleva id_mesa, fecha_hora, precio, id_metodo_pago
        String sql = "INSERT INTO ticket (id, id_mesa, fecha_hora, precio, id_metodo_pago, id_empleado) VALUES (?, ?, NOW(), ?, ?, ?)";
        try{
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final", "root", "root");
            PreparedStatement stmt = conexion.prepareStatement(sql);
                //primero el id 
                int id = 0;
                String sqlId = "SELECT MAX(id) AS max_id FROM ticket";
                PreparedStatement stmtId = conexion.prepareStatement(sqlId);
                ResultSet rsId = stmtId.executeQuery();
                if (rsId.next()) {
                    id = rsId.getInt("max_id") + 1; // Incrementa el ID máximo encontrado
                }
                rsId.close();
                stmtId.close();
            // Luego el resto de los valores
            stmt.setInt(1, id);
            stmt.setInt(2, idMesa);
            // Si hay un descuento aplicado, se guarda el total con descuento
            if (cuentaConDescuento > 0) {
                stmt.setDouble(3, cuentaConDescuento);
            } else {
                stmt.setDouble(3, Double.parseDouble(lblTotal.getText().replace("€", "").replace(",", ".")));
            }
            stmt.setInt(4, idMetodoPago);
                // Obtener el id del empleado actual
                String usuarioActual = lblCajero.getText(); 
                int idEmpleado = 0;
                String sqlEmpleado = "SELECT id FROM empleado WHERE nombre = ?";
                PreparedStatement stmtEmpleado = conexion.prepareStatement(sqlEmpleado);
                stmtEmpleado.setString(1, usuarioActual);
                ResultSet rsEmpleado = stmtEmpleado.executeQuery();
                if (rsEmpleado.next()) {
                    idEmpleado = rsEmpleado.getInt("id");
                }
                rsEmpleado.close();
                stmtEmpleado.close();
            // Ahora usa idEmpleado en el insert:
            stmt.setInt(5, idEmpleado);
            stmt.executeUpdate();
            stmt.close();

            // Eliminar la comanda de la base de datos y de la tabla
            eliminarComanda();

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo guardar el ticket.");
            return;
        }

    }

    /**
     * Abre la ventana emergente con los detalles de los tickets.
     */
    @FXML public void abrirVentanaEmergenteDetalles() {
        // Este metodo abre una ventana emergente con los detalles de los tickets
        try {
            // Obtener la comanda seleccionada del combo box
            String mesaSeleccionada = comboMesas.getValue();
            

            FXMLLoader loader = new FXMLLoader(getClass().getResource("detalles.fxml"));
            Parent root = loader.load();

            VentanaEmergenteControllerDetalles controller = loader.getController();
            controller.setControladorPrincipal(this);
            controller.setMesa(mesaSeleccionada);

            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal hasta cerrar la emergente
            stage.setResizable(false);
            stage.setTitle("Detalles de los Tickets");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Espera a que se cierre la ventana emergente
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina todas las comandas de la mesa seleccionada.
     */
    @FXML public void eliminarComanda() {
        // Este metodo elimina todas las comanda de la base de datos con esa misma mesa
        String mesaSeleccionada = comboMesas.getValue();
        if (mesaSeleccionada == null) {
            mostrarAlerta("Advertencia", "Selecciona una mesa antes de eliminar la comanda.");
            return;
        }
        int idMesa = MesasNombreToId.get(mesaSeleccionada);
        String sql = "DELETE FROM comanda WHERE id_mesa = ?";
        try {
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final", "root", "root");
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, idMesa);
            stmt.executeUpdate();
            stmt.close();
            // Actualizar la tabla de comandas
            cargarComandasDesdeBaseDeDatos(idMesa);
        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "No se pudo eliminar la comanda.");
        }
    }

    /**
     * Abre la ventana emergente para cobrar la cuenta.
     */
    @FXML public void abrirVentanaEmergenteCobrar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("cobrar.fxml"));
            Parent root = loader.load();
            VentanaEmergenteControllerCobrar controller = loader.getController();
            controller.setControladorPrincipal(this);

            // Extrae el valor numérico del Label lblTotal y pásalo como double
            String totalStr = lblTotal.getText().replace("€", "").replace(",", ".").trim();
            double total = 0.0;
            try {
                total = Double.parseDouble(totalStr);
            } catch (NumberFormatException e) {
                total = 0.0;
            }
            controller.setCuentaOriginal(total); // valorCuenta es el total actual de la cuenta
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal hasta cerrar la emergente
            stage.setResizable(false);
            stage.setTitle("Añadir Descuento");
            stage.setScene(new Scene(root));
            stage.showAndWait(); // Espera a que se cierre la ventana emergente
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra una alerta de error con el título y mensaje proporcionados.
     * @param titulo Título de la alerta
     * @param mensaje Mensaje de la alerta
     */
    @FXML
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }  

    /**
     * Obtiene el nombre de un producto a partir de su id.
     * @param idProducto id del producto
     * @return nombre del producto
     */
    private String obtenerNombreProductoPorId(int idProducto) {
        String nombre = "";
        try {
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final", "root", "root");
            String sql = "SELECT nombre FROM producto WHERE id = ?";
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                nombre = rs.getString("nombre");
            }
            rs.close();
            stmt.close();
            conexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return nombre;
    }

    /**
     * Obtiene el precio de un producto a partir de su id.
     * @param idProducto id del producto
     * @return precio del producto como String
     */
    private String obtenerPrecioProductoPorId(int idProducto) {
        String precio = "0.0";
        try {
            Connection conexion = DriverManager.getConnection("jdbc:mysql://localhost:3306/proyecto_final", "root", "root");
            String sql = "SELECT precio FROM producto WHERE id = ?";
            PreparedStatement stmt = conexion.prepareStatement(sql);
            stmt.setInt(1, idProducto);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                precio = String.valueOf(rs.getDouble("precio"));
            }
            rs.close();
            stmt.close();
            conexion.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return precio;
    }
}
