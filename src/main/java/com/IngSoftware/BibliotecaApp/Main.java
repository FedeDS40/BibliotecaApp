package com.IngSoftware.BibliotecaApp;

import com.IngSoftware.BibliotecaApp.datos.Conexion;
import java.sql.Connection;
import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try (Connection con = Conexion.obtenerConexion()) {
            System.out.println("Conexión exitosa a la base: " + con.getCatalog());
        } catch (SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
    }
}