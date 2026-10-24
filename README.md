# BibliotecaApp - Sistema de Gestión de Biblioteca

Aplicación de consola desarrollada en Java para gestionar los socios, los libros y los préstamos de una biblioteca.
Trabajo Práctico Nº 2 - Ingeniería de Software.

## Integrantes

- Moron Valentin
- Della Savia Federico
- Lombardo Enzo
- Filippini Benjamin
- Sampietro Augusto

## Tecnologías

- Java 17
- Maven 3.9
- PostgreSQL (acceso con JDBC)
- Arquitectura en capas

## Requisitos previos

- Java 17 o superior (`java -version`)
- Maven (`mvn -version`)
- PostgreSQL con pgAdmin
- Git

## Configuración

### 1. Crear la base de datos

1. En pgAdmin, clic derecho sobre **Databases**, **Create**, **Database...** y crear una base llamada `biblioteca`.
2. Abrir el **Query Tool** sobre la base `biblioteca` y ejecutar, en este orden:
   1. `sql/02_tablas.sql` (crea las tablas `socio`, `libro` y `prestamo`).
   2. `sql/03_datos.sql` (carga datos ficticios para probar).
3. Comprobar con `SELECT * FROM socio;`: deben aparecer 5 socios.

El script `sql/01_crear_bd.sql` es una alternativa para crear la base desde la línea de comandos con `psql -U postgres -f sql/01_crear_bd.sql`. Atención: borra la base `biblioteca` si ya existe.

### 2. Configurar la conexión

Crear el archivo `src/main/resources/db.properties` con los datos de PostgreSQL de cada computadora.
Este archivo no se sube a Git (está en `.gitignore`) porque contiene la contraseña.

```
db.url=jdbc:postgresql://localhost:5432/biblioteca
db.user=postgres
db.password=TU_CONTRASEÑA
```

## Ejecución

Desde la carpeta del proyecto:

```
mvn clean compile exec:java
```

## Perfiles

Al iniciar se elige el perfil desde el menú principal:

| Perfil | Cómo se ingresa | Qué puede hacer |
|---|---|---|
| Socio | Opción 1, indicando el número de un socio existente (ej. `S-001`) | Consultar libros y su disponibilidad, y ver únicamente sus propios préstamos |
| Bibliotecario | Opción 2 | Gestionar socios, consultar libros, registrar préstamos y devoluciones, y listar préstamos |
| Administrador | Opción 3 | Gestionar libros y consultar socios y préstamos |

## Operaciones disponibles

**Socio**
- Consultar libros por título o autor, o listarlos todos, con su disponibilidad.
- Ver mis préstamos.

**Bibliotecario**
- Registrar, modificar, consultar y dar de baja socios.
- Consultar libros por título o autor.
- Registrar préstamos (muestra el comprobante).
- Registrar devoluciones (muestra el comprobante).
- Ver el historial de préstamos de un socio.
- Listar préstamos vigentes y vencidos.

**Administrador**
- Registrar, modificar, consultar y dar de baja libros.
- Consultar socios y el historial de préstamos de un socio.
- Listar préstamos vigentes y vencidos.

## Datos de prueba

| Socio | Situación | Sirve para probar |
|---|---|---|
| S-001 Lucía Fernández | Activa, sin préstamos | Préstamo exitoso |
| S-002 Martín Gómez | 3 préstamos sin devolver | Límite de 3 préstamos (RN-03) |
| S-003 Carla Ruiz | Un préstamo vencido | Bloqueo por préstamo vencido (RN-06) |
| S-004 Julián Pérez | Inactivo | Socio inactivo (RN-01) |
| S-005 Sofía Díaz | Historial devuelto | Historial de préstamos |

Libros: el 9 está dado de baja, los ids 5 y 6 son dos ejemplares del mismo título, y los ids 1, 2, 3 y 4 están prestados.

## Reglas de negocio

Las reglas RN-01 a RN-12 del enunciado se aplican en la capa de negocio (`negocio`), sobre todo en `PrestamoService`, `SocioService` y `LibroService`:

- Solo se prestan libros activos y disponibles a socios activos.
- Máximo 3 préstamos sin devolver por socio.
- Plazo de 7 días corridos.
- Un socio con préstamos vencidos no puede pedir nuevos préstamos.
- Cada devolución se registra una sola vez.
- No se puede dar de baja un socio o un libro con préstamos sin devolver.
- Las bajas son lógicas: el registro queda inactivo y conserva su historial.
- El DNI y el número de socio no se repiten.
- Los préstamos y las devoluciones usan la fecha actual del sistema.

## Arquitectura

Paquete base: `com.IngSoftware.BibliotecaApp`

| Capa | Paquete | Responsabilidad |
|---|---|---|
| Presentación | `presentacion` | Menús, lectura de datos y mensajes en consola |
| Lógica de negocio | `negocio` | Reglas, validaciones y coordinación de préstamos y devoluciones |
| Acceso a datos | `datos` | Consultas, inserciones y actualizaciones en PostgreSQL |
| Modelo | `modelo` | Clases `Socio`, `Libro` y `Prestamo` |
| Excepciones | `excepciones` | Errores propios que se informan sin cerrar el programa |

## Estructura del proyecto

```
BibliotecaApp/
├── pom.xml
├── README.md
├── sql/
│   ├── 01_crear_bd.sql
│   ├── 02_tablas.sql
│   └── 03_datos.sql
└── src/main/
    ├── java/com/IngSoftware/BibliotecaApp/
    │   ├── Main.java
    │   ├── datos/
    │   ├── excepciones/
    │   ├── modelo/
    │   ├── negocio/
    │   └── presentacion/
    └── resources/
        └── db.properties   (no se sube a Git)
```