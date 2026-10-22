-- 02_tablas.sql
-- Ejecutar conectado a la base "biblioteca":
--   psql -U postgres -d biblioteca -f sql/02_tablas.sql

DROP TABLE IF EXISTS prestamo;
DROP TABLE IF EXISTS libro;
DROP TABLE IF EXISTS socio;

-- RN-10: las bajas son lógicas (columna activo)
-- RN-11: DNI y número de socio no se repiten (UNIQUE)
CREATE TABLE socio (
    id            SERIAL PRIMARY KEY,
    numero_socio  VARCHAR(20)  NOT NULL UNIQUE,
    dni           VARCHAR(15)  NOT NULL UNIQUE,
    nombre        VARCHAR(80)  NOT NULL,
    apellido      VARCHAR(80)  NOT NULL,
    activo        BOOLEAN      NOT NULL DEFAULT TRUE
);

-- Cada registro es un ejemplar físico (dos copias = dos ids distintos)
CREATE TABLE libro (
    id                SERIAL PRIMARY KEY,
    titulo            VARCHAR(150) NOT NULL,
    autor             VARCHAR(100) NOT NULL,
    editorial         VARCHAR(100),
    anio_publicacion  INTEGER CHECK (anio_publicacion BETWEEN 1000 AND 2100),
    activo            BOOLEAN NOT NULL DEFAULT TRUE
);

-- RN-04: cada préstamo = un socio y un ejemplar
-- fecha_devolucion NULL = préstamo sin devolver
CREATE TABLE prestamo (
    id                 SERIAL PRIMARY KEY,
    id_socio           INTEGER NOT NULL REFERENCES socio(id),
    id_libro           INTEGER NOT NULL REFERENCES libro(id),
    fecha_prestamo     DATE NOT NULL,
    fecha_vencimiento  DATE NOT NULL,
    fecha_devolucion   DATE,
    CONSTRAINT ck_fechas CHECK (
        fecha_vencimiento >= fecha_prestamo
        AND (fecha_devolucion IS NULL OR fecha_devolucion >= fecha_prestamo)
    )
);

-- Un ejemplar no puede tener dos préstamos sin devolver a la vez
-- (garantiza la coherencia de disponibilidad a nivel de base de datos)
CREATE UNIQUE INDEX ux_prestamo_libro_abierto
    ON prestamo (id_libro) WHERE fecha_devolucion IS NULL;

CREATE INDEX ix_prestamo_socio ON prestamo (id_socio);
CREATE INDEX ix_libro_titulo   ON libro (LOWER(titulo));
CREATE INDEX ix_libro_autor    ON libro (LOWER(autor));
