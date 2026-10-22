-- 03_datos.sql
-- Datos ficticios para probar todas las reglas de negocio.
--   psql -U postgres -d biblioteca -f sql/03_datos.sql

INSERT INTO socio (numero_socio, dni, nombre, apellido, activo) VALUES
('S-001', '30111222', 'Lucía',   'Fernández', TRUE),   -- sin préstamos
('S-002', '31222333', 'Martín',  'Gómez',     TRUE),   -- con 3 préstamos sin devolver (límite)
('S-003', '32333444', 'Carla',   'Ruiz',      TRUE),   -- con un préstamo vencido
('S-004', '33444555', 'Julián',  'Pérez',     FALSE),  -- socio inactivo
('S-005', '34555666', 'Sofía',   'Díaz',      TRUE);   -- con historial devuelto

INSERT INTO libro (titulo, autor, editorial, anio_publicacion, activo) VALUES
('Rayuela',                       'Julio Cortázar',      'Sudamericana', 1963, TRUE),  -- 1
('Ficciones',                     'Jorge Luis Borges',   'Emecé',        1944, TRUE),  -- 2
('El Aleph',                      'Jorge Luis Borges',   'Losada',       1949, TRUE),  -- 3
('Sobre héroes y tumbas',         'Ernesto Sabato',      'Fabril',       1961, TRUE),  -- 4
('Cien años de soledad',          'Gabriel García Márquez','Sudamericana',1967, TRUE),  -- 5
('Cien años de soledad',          'Gabriel García Márquez','Sudamericana',1967, TRUE),  -- 6 (2.º ejemplar)
('Martín Fierro',                 'José Hernández',      'Kapelusz',     1872, TRUE),  -- 7
('El túnel',                      'Ernesto Sabato',      'Seix Barral',  1948, TRUE),  -- 8
('Don Quijote de la Mancha',      'Miguel de Cervantes', 'Alfaguara',    1605, FALSE), -- 9 libro dado de baja
('La invención de Morel',         'Adolfo Bioy Casares', 'Losada',       1940, TRUE);  -- 10

-- Préstamos: S-002 llega al límite de 3 sin devolver
INSERT INTO prestamo (id_socio, id_libro, fecha_prestamo, fecha_vencimiento, fecha_devolucion) VALUES
(2, 1, CURRENT_DATE - 2, CURRENT_DATE + 5, NULL),
(2, 2, CURRENT_DATE - 1, CURRENT_DATE + 6, NULL),
(2, 3, CURRENT_DATE,     CURRENT_DATE + 7, NULL);

-- S-003 tiene un préstamo vencido (venció hace 3 días, sin devolver)
INSERT INTO prestamo (id_socio, id_libro, fecha_prestamo, fecha_vencimiento, fecha_devolucion) VALUES
(3, 4, CURRENT_DATE - 10, CURRENT_DATE - 3, NULL);

-- Historial ya devuelto (S-005 y S-004)
INSERT INTO prestamo (id_socio, id_libro, fecha_prestamo, fecha_vencimiento, fecha_devolucion) VALUES
(5, 5, CURRENT_DATE - 30, CURRENT_DATE - 23, CURRENT_DATE - 25),
(5, 7, CURRENT_DATE - 20, CURRENT_DATE - 13, CURRENT_DATE - 15),
(4, 8, CURRENT_DATE - 60, CURRENT_DATE - 53, CURRENT_DATE - 55);

-- Libros disponibles para probar préstamos: 5, 6, 7, 8, 10
-- Libro 9 está inactivo.
