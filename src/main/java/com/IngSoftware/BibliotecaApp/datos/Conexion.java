package com.IngSoftware.BibliotecaApp.datos;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class Conexion {

    private static final String ARCHIVO_CONFIG = "db.properties";

    // Abre una conexión nueva a PostgreSQL usando los datos de db.properties
    public static Connection obtenerConexion() throws SQLException {
        Properties config = new Properties();

        try (InputStream in = Conexion.class.getClassLoader()
                .getResourceAsStream(ARCHIVO_CONFIG)) {
            if (in == null) {
                throw new SQLException("No se encontró el archivo " + ARCHIVO_CONFIG);
            }
            config.load(in);
        } catch (IOException e) {
            throw new SQLException("No se pudo leer el archivo " + ARCHIVO_CONFIG, e);
        }

        return DriverManager.getConnection(
                config.getProperty("db.url"),
                config.getProperty("db.user"),
                config.getProperty("db.password"));
    }
}
