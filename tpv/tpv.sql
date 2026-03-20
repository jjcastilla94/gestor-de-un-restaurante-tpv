DROP DATABASE IF EXISTS proyecto_final;
CREATE DATABASE proyecto_final CHARACTER SET utf8mb4;
USE proyecto_final;

DROP USER IF EXISTS 'tpv_app'@'%';
CREATE USER 'tpv_app'@'%' IDENTIFIED WITH caching_sha2_password BY 'tpv_app_123';
GRANT ALL PRIVILEGES ON proyecto_final.* TO 'tpv_app'@'%';
FLUSH PRIVILEGES;

-- Tabla empleado
CREATE TABLE empleado (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    pass VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL
);

CREATE TABLE usuario_actual (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL,
    pass VARCHAR(20) NOT NULL
);

-- Tabla mesa
CREATE TABLE mesa (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    numero INT NOT NULL,
    capacidad INT,
    estado ENUM('Libre', 'Ocupada') NOT NULL
);

-- Tabla categoria
CREATE TABLE categoria (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- Tabla categoria
CREATE TABLE descuento (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- Tabla producto
CREATE TABLE producto (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
    imagen VARCHAR(255) NOT NULL,
    id_categoria INT UNSIGNED NOT NULL,
    FOREIGN KEY (id_categoria) REFERENCES categoria(id)
);

-- Tabla comanda
CREATE TABLE comanda (
	id_producto INT UNSIGNED,
    FOREIGN KEY (id_producto) REFERENCES producto(id),
    id_mesa INT UNSIGNED,
    FOREIGN KEY (id_mesa) REFERENCES mesa(id),
    precio_total DECIMAL(10, 2) NOT NULL,
    cantidad INT NOT NULL
    
);

-- Tabla metodo_pago
CREATE TABLE metodo_pago (
    id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- Tabla ticket
CREATE TABLE ticket (
	id INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
    id_mesa INT UNSIGNED,
    FOREIGN KEY (id_mesa) REFERENCES mesa(id),
    fecha_hora DATETIME  NOT NULL,
    precio DECIMAL(10, 2) NOT NULL,
	id_metodo_pago INT UNSIGNED,
    FOREIGN KEY (id_metodo_pago) REFERENCES metodo_pago(id),
    id_empleado INT UNSIGNED,
    FOREIGN KEY (id_empleado) REFERENCES empleado(id)
    
);


INSERT INTO empleado (nombre, pass, email) VALUES
('anagarcia', 'clave123', 'ana@example.com'),
('luisp', 'contraseña', 'luis@example.com'),
('martal', 'marta2024', 'marta@example.com'),
('cruiz', 'ruizpass', 'carlos@example.com'),
('elenat', 'elena789', 'elena@example.com');


INSERT INTO mesa (nombre, numero, capacidad, estado) VALUES
('Mesa 1', 1, 4, 'Libre'),
('Mesa 2',2, 2, 'Ocupada'),
('Mesa 3',3, 6, 'Ocupada'),
('Mesa 4',4, 3, 'Libre'),
('Mesa 5',5, 5, 'Ocupada'),
('Terraza 1',1, 8, 'Libre'),
('Terraza 2',2, 10, 'Libre');


INSERT INTO categoria (nombre) VALUES
('Bebidas'),
('Entrantes'),
('1 Plato'),
('2 Plato'),
('Postres'),
('Cafes');

INSERT INTO descuento (nombre) VALUES
('Porcentaje'),
('Cantidad Fija');

INSERT INTO producto (id, nombre, precio, imagen, id_categoria) VALUES
(1, 'Agua', 1.50, 'agua.png', 1),
(2, 'Cocacola', 2.70, 'cocacola.png', 1),
(3, 'Jarra', 4.00, 'jarra.png', 1),
(4, 'Tubo', 3.50, 'tubo.png', 1),
(5, 'Tercio', 2.70, 'tercio.png', 1),
(6, 'Coctel', 6.00, 'coctel.png', 1),
(7, 'Limonada', 3.20, 'lemonada.png', 1),
(8, 'Whisky', 7.30, 'wiski.png', 1),
(9, 'Tacos', 9.50, 'tacos.png', 3),
(10, 'Patatas', 5.90, 'patatas.png', 2),
(11, 'Nachos', 12.20, 'nachos.png', 2),
(12, 'Sandwich de Pollo', 7.50, 'sandwich.png', 2),
(13, 'Salchipapas', 13.00, 'salchipapas.png', 4),
(14, 'Alitas de Pollo', 15.40, 'alitas.png', 3),
(15, 'Arroz con Chorizo de Messi', 22.10, 'arrozchorizo.png', 3),
(16, 'Albondigas', 11.90, 'albondigas.png', 4),
(17, 'Hamburguesa Gourmet', 18.30, 'hamburguesa.png', 4),
(18, 'Pasta al peste', 16.00, 'pasta.png', 4),
(19, 'Pizza Fitness', 12.50, 'pizza.png', 4),
(20, 'Tarta de Queso', 5.50, 'tarta.png', 5),
(21, 'Cupcake', 2.80, 'cupcake.png', 5),
(22, 'Copa de Helado', 8.20, 'copahelado.png', 5),
(23, 'Cucurucho', 4.60, 'cucurucho.png', 5),
(24, 'Macarons', 3.30, 'macarons.png', 5),
(25, 'Cortado', 1.20, 'cortado.png', 6),
(26, 'Manchada', 1.50, 'manchada.png', 6),
(27, 'Cafe Avellana', 1.70, 'avellana.png', 6),

(28, 'Batido de Caramelo', 4.60, 'batidocaramelo.png', 1),
(29, 'Batido de Fresa', 4.50, 'batidofresa.png', 1),
(30, 'Calamares', 13.00, 'calamares.png', 3),
(31, 'Chocolate', 3.00, 'chocolate.png', 6),
(32, 'Crepe', 2.80, 'crepe.png', 5),
(33, 'Croquetas de Gregoria', 8.00, 'croquetas.png', 2),
(34, 'Ensalada', 7.50, 'ensalada.png', 2),
(35, 'Ensaladilla Rusa', 6.20, 'ensaladilla.png', 2),
(36, 'Expresso', 1.50, 'expreso.png', 6),
(37, 'Gazpacho', 5.10, 'gazpacho.png', 2),
(38, 'Gofre', 4.50, 'gofre.png', 5),
(39, 'Cafe Latte', 3.50, 'late.png', 6),
(40, 'Magdalena', 1.20, 'magdalena.png', 5),
(41, 'Mocca', 3.00, 'moca.png', 6),
(42, 'Mojito', 8.50, 'mojito.png', 1),
(43, 'Paella no pa el', 25.00, 'paella.png', 3),
(44, 'Pesado Frito', 17.50, 'pescado.png', 4),
(45, 'Quesadillas', 9.50, 'quesadilla.png', 3),
(46, 'Secreto Ibérico', 40.80, 'secreto.png', 4),
(47, 'Cafe Solo', 1.10, 'solo.png', 6),
(48, 'Sopa', 10.50, 'sopa.png', 3),
(49, 'Surtido de Embutidos', 6.60, 'surtido.png', 2),
(50, 'Tiramisu', 6.00, 'tiramisu.png', 5),
(51, 'Tortilla de Patatas', 14.50, 'tortilla.png', 3);

INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (6, 2, 12.00, 2);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (14, 3, 30.80, 2);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (22, 1, 16.40, 2);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (36, 5, 3.00, 2);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (19, 6, 25.00, 2);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (50, 4, 6.00, 1);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (43, 2, 50.00, 2);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (12, 5, 22.50, 3);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (32, 3, 5.60, 2);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (1, 1, 1.50, 1);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (7, 3, 9.60, 3);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (27, 4, 5.10, 3);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (9, 6, 19.00, 2);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (35, 2, 18.60, 3);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (41, 5, 6.00, 2);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (20, 1, 11.00, 2);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (18, 3, 32.00, 2);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (28, 4, 13.80, 3);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (45, 2, 28.20, 3);
INSERT INTO comanda (id_producto, id_mesa, precio_total, cantidad) VALUES (33, 6, 24.00, 3);



INSERT INTO metodo_pago (nombre) VALUES
('Efectivo'),
('Bizum'),
('Pagaré'),
('Tarjeta');

INSERT INTO ticket (id, id_mesa, fecha_hora, precio, id_metodo_pago, id_empleado) VALUES (1, 3, '2025-05-21 14:30:00', 45.50, 2, 1);
INSERT INTO ticket (id, id_mesa, fecha_hora, precio, id_metodo_pago, id_empleado) VALUES (2, 1, '2025-05-21 15:10:00', 32.00, 1, 2);
INSERT INTO ticket (id, id_mesa, fecha_hora, precio, id_metodo_pago, id_empleado) VALUES (3, 5, '2025-05-21 16:00:00', 27.30, 3, 3);
INSERT INTO ticket (id, id_mesa, fecha_hora, precio, id_metodo_pago, id_empleado) VALUES (4, 2, '2025-05-21 16:45:00', 59.20, 4, 4);
INSERT INTO ticket (id, id_mesa, fecha_hora, precio, id_metodo_pago, id_empleado) VALUES (5, 6, '2025-05-21 17:30:00', 15.00, 2, 5);
INSERT INTO ticket (id, id_mesa, fecha_hora, precio, id_metodo_pago, id_empleado) VALUES (6, 4, '2025-05-21 18:10:00', 38.75, 1, 1);
INSERT INTO ticket (id, id_mesa, fecha_hora, precio, id_metodo_pago, id_empleado) VALUES (7, 1, '2025-05-21 18:50:00', 22.90, 3, 2);
INSERT INTO ticket (id, id_mesa, fecha_hora, precio, id_metodo_pago, id_empleado) VALUES (8, 3, '2025-05-21 19:30:00', 41.60, 4, 3);
INSERT INTO ticket (id, id_mesa, fecha_hora, precio, id_metodo_pago, id_empleado) VALUES (9, 5, '2025-05-21 20:15:00', 35.40, 1, 4);
INSERT INTO ticket (id, id_mesa, fecha_hora, precio, id_metodo_pago, id_empleado) VALUES (10, 2, '2025-05-21 21:00:00', 49.80, 2, 5);



SELECT *
From ticket;

SELECT id_producto 
FROM comanda
WHERE id_mesa = 1;

SELECT c.id, cp.cantidad, p.nombre, p.precio, (p.precio * cp.cantidad) AS total
FROM comanda c
JOIN comanda_producto cp ON c.id = cp.id_comanda
JOIN producto p ON cp.id_producto = p.id
WHERE c.id_mesa = 1;