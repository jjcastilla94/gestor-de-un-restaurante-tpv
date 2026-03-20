# Proyecto Final - TPV de Restaurante (JavaFX + MySQL)

## 1. Resumen del proyecto
Este repositorio contiene un **TPV (Terminal Punto de Venta)** de escritorio orientado a restauracion.
La aplicacion esta construida con JavaFX y permite gestionar el ciclo completo de venta en sala:

- autenticacion de empleados,
- gestion de catalogo de productos,
- gestion de comandas por mesa,
- aplicacion de descuentos,
- cobro y registro de tickets,
- consulta historica de tickets,
- generacion de ticket en PDF.

El proyecto esta planteado con enfoque academico-profesional, simulando un entorno real de negocio para un restaurante/bar.

## 2. Objetivos
- Digitalizar el flujo de trabajo de camareros/cajeros en un unico puesto TPV.
- Reducir errores manuales en comandas y cobros.
- Mantener trazabilidad de ventas (mesa, empleado, metodo de pago y fecha).
- Ofrecer una interfaz visual clara para operativa rapida en servicio.
- Servir como base evolucionable para un TFG/Proyecto Final de ciclo.

## 3. Funcionalidades principales
### 3.1 Login y registro de empleados
- Pantalla inicial con modo **Log In / Sign Up**.
- Validacion basica de campos.
- Alta de nuevos empleados en base de datos.
- Registro del usuario activo en tabla `usuario_actual`.

### 3.2 Gestion de productos
- Visualizacion de productos por categorias:
  - Bebidas
  - Entrantes
  - Primer Plato
  - Segundo Plato
  - Postres
  - Cafes
- Buscador por nombre.
- Alta de producto (nombre, precio, categoria e imagen).
- Modificacion de producto existente.
- Eliminacion de producto (incluyendo limpieza en comandas asociadas).

### 3.3 Gestion de comandas por mesa
- Selector de mesa con carga dinamica de su comanda.
- Alta de lineas de comanda desde catalogo.
- Acumulacion de cantidades si el producto ya existe en la mesa.
- Edicion de cantidad directamente en tabla de ticket.
- Eliminacion de linea de comanda.
- Recalculo automatico de subtotal, IVA y total.

### 3.4 Descuentos
- Aplicacion de descuento por:
  - porcentaje,
  - cantidad fija.
- Validaciones para evitar importes invalidos.
- Actualizacion del total final antes del cobro.

### 3.5 Cobro y persistencia de tickets
- Seleccion de metodo de pago (Efectivo, Bizum, Pagare, Tarjeta).
- Guardado del ticket en tabla `ticket` con:
  - mesa,
  - fecha/hora,
  - importe final,
  - metodo de pago,
  - empleado.
- Limpieza de la comanda de la mesa tras confirmar cobro.

### 3.6 Historico y detalles de tickets
- Ventana de consulta de tickets registrados.
- Visualizacion de mesa, fecha, precio, metodo de pago y empleado.
- Eliminacion de tickets desde la tabla de detalles.

### 3.7 Exportacion PDF
- Generacion de documento PDF con formato de ticket:
  - cabecera,
  - datos de mesa/fecha/cajero,
  - detalle de lineas,
  - subtotal/IVA/total,
  - pie de ticket.
- Apertura automatica del PDF si el sistema lo permite.

## 4. Tecnologias utilizadas
- **Java 11** (compilacion objetivo).
- **JavaFX 21** (`javafx-controls`, `javafx-fxml`).
- **MySQL 8** (persistencia de datos).
- **JDBC** (conexion directa desde controladores/modelos).
- **Maven** (gestion de dependencias y ejecucion).
- **iTextPDF 5.5.13.3** (generacion de tickets PDF).

## 5. Arquitectura del proyecto
Estructura general del repositorio:

```text
PROYECTO FINAL/
|-- entidad_relacion.drawio
|-- tpv/
|   |-- pom.xml
|   |-- tpv.sql
|   `-- src/main/
|       |-- java/com/example/
|       |   |-- App.java
|       |   |-- LoginController.java
|       |   |-- PrimaryController.java
|       |   |-- VentanaEmergenteController*.java
|       |   `-- modelos (Producto, Comanda, Ticket, Mesa, Categoria)
|       `-- resources/com/example/
|           |-- login.fxml
|           |-- primary.fxml
|           |-- ventanas emergentes *.fxml
|           |-- styles.css
|           `-- images/
```

Patron aplicado (practico):
- **Vista:** FXML + CSS.
- **Controlador:** clases `*Controller` con logica de UI y casos de uso.
- **Datos:** clases de dominio (`Producto`, `Comanda`, etc.) con operaciones JDBC.

## 6. Modelo de datos (MySQL)
Script de creacion y datos semilla: `tpv/tpv.sql`.

Tablas principales:
- `empleado`: usuarios del sistema.
- `usuario_actual`: historial simple de usuario conectado.
- `mesa`: catalogo de mesas y estado.
- `categoria`: clasificacion de productos.
- `producto`: productos vendibles del TPV.
- `comanda`: lineas activas por mesa (producto, cantidad, total).
- `descuento`: tipos de descuento (porcentaje/fijo).
- `metodo_pago`: formas de cobro.
- `ticket`: registro de ventas cerradas.

## 7. Requisitos previos
Asegurate de tener instalado:
- **JDK 11** (recomendado para compatibilidad con `pom.xml`).
- **Maven 3.8+**.
- **MySQL Server 8+**.
- Cliente SQL (MySQL Workbench o terminal mysql).

## 8. Instalacion y puesta en marcha
### 8.1 Clonar el repositorio
```bash
git clone <url-del-repo>
cd "PROYECTO FINAL"
```

### 8.2 Crear base de datos
Ejecuta el script SQL:

```sql
SOURCE tpv/tpv.sql;
```

O bien copia/ejecuta manualmente el contenido de `tpv/tpv.sql` en MySQL.

### 8.3 Verificar credenciales de BD en codigo
Actualmente la conexion se realiza con:

- host: `localhost`
- puerto: `3306`
- base de datos: `proyecto_final`
- usuario: `root`
- password: `root`

Estas credenciales aparecen hardcodeadas en varios controladores/modelos.
Si tu entorno usa otro usuario/password, ajustalo antes de ejecutar.

### 8.4 Ejecutar la aplicacion
Desde la carpeta `tpv/`:

```bash
mvn clean javafx:run
```

## 9. Flujo de uso recomendado
1. Inicia sesion (o registra un empleado nuevo).
2. En pantalla principal, selecciona una mesa.
3. Navega por categorias y selecciona productos.
4. Pulsa "Nuevo" para anadir producto a la comanda.
5. Ajusta cantidades o elimina lineas si procede.
6. (Opcional) Aplica descuento.
7. Genera PDF si necesitas copia de ticket.
8. Pulsa "Cobrar" y elige metodo de pago.
9. Revisa historico en "Ver Mas".

## 10. Calidad del proyecto y decisiones tecnicas
Puntos fuertes:
- Flujo funcional completo de TPV para entorno de restauracion.
- Interfaz visual amplia y orientada a operativa real.
- Integracion de persistencia + generacion de documentos.
- Datos semilla para pruebas rapidas.

Decisiones actuales:
- JDBC directo en controladores/modelos (sin capa de servicios/repository separada).
- Validaciones basicas en capa UI.
- Generacion PDF local en el equipo cliente.

## 11. Mejoras recomendadas (roadmap)
- Parametrizar conexion a BD en archivo de configuracion.
- Usar `PreparedStatement` en todos los accesos para robustez/seguridad.
- Introducir hash de contrasenas (actualmente se guardan en texto plano).
- Separar capas (Controller -> Service -> DAO/Repository).
- Anadir pruebas unitarias e integracion.
- Implementar control de roles (camarero, encargado, admin).
- Permitir cierre de caja y reportes diarios/semanales.
- Empaquetado ejecutable (jpackage/instalador).

## 12. Riesgos y consideraciones
- No apto para produccion sin endurecimiento de seguridad.
- Credenciales y consultas SQL deben revisarse antes de despliegue real.
- Requiere que recursos (imagenes/FXML) esten bien resueltos en runtime.

## 13. Troubleshooting rapido
- Error de conexion MySQL:
  - revisa que MySQL este levantado,
  - valida usuario/password y nombre de BD,
  - ejecuta de nuevo `tpv.sql`.

- JavaFX no arranca:
  - confirma version de Java/Maven,
  - lanza desde `tpv/` con `mvn clean javafx:run`.

- No se muestran imagenes de productos:
  - verifica nombres de archivo en tabla `producto.imagen`,
  - comprueba existencia en `src/main/resources/com/example/images`.

## 14. Autor y contexto academico
Proyecto realizado como **Proyecto Final de primero de DAW**, con objetivo de demostrar competencias full-stack orientadas a software de gestion para hosteleria.