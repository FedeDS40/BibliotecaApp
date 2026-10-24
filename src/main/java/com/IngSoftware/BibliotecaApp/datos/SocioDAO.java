package com.IngSoftware.BibliotecaApp.datos;

import com.IngSoftware.BibliotecaApp.excepciones.AccesoDatosException;
import com.IngSoftware.BibliotecaApp.modelo.Socio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SocioDAO {

    // Guarda un socio nuevo y le asigna el id que le dio la base
    public void insertar(Socio socio) {
        String sql = "INSERT INTO socio (numero_socio, dni, nombre, apellido, activo) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, socio.getNumeroSocio());
            ps.setString(2, socio.getDni());
            ps.setString(3, socio.getNombre());
            ps.setString(4, socio.getApellido());
            ps.setBoolean(5, socio.estaActivo());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    socio.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo guardar el socio", e);
        }
    }

    // Modifica los datos de un socio existente (también sirve para la baja lógica)
    public void actualizar(Socio socio) {
        String sql = "UPDATE socio SET dni = ?, nombre = ?, apellido = ?, activo = ? WHERE id = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, socio.getDni());
            ps.setString(2, socio.getNombre());
            ps.setString(3, socio.getApellido());
            ps.setBoolean(4, socio.estaActivo());
            ps.setInt(5, socio.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo actualizar el socio", e);
        }
    }

    // Devuelve el socio, o null si no existe
    public Socio buscarPorId(int id) {
        String sql = "SELECT * FROM socio WHERE id = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? armarSocio(rs) : null;
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo buscar el socio", e);
        }
    }

    // Devuelve el socio, o null si no existe
    public Socio buscarPorNumero(String numeroSocio) {
        String sql = "SELECT * FROM socio WHERE numero_socio = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, numeroSocio);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? armarSocio(rs) : null;
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo buscar el socio", e);
        }
    }

    // RN-11: sirve para evitar DNI repetidos
    public boolean existeDni(String dni) {
        return existe("SELECT 1 FROM socio WHERE dni = ?", dni);
    }

    // RN-11: sirve para evitar números de socio repetidos
    public boolean existeNumeroSocio(String numeroSocio) {
        return existe("SELECT 1 FROM socio WHERE numero_socio = ?", numeroSocio);
    }

    public List<Socio> listarTodos() {
        String sql = "SELECT * FROM socio ORDER BY apellido, nombre";
        List<Socio> socios = new ArrayList<>();
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                socios.add(armarSocio(rs));
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo listar los socios", e);
        }
        return socios;
    }

    private boolean existe(String sql, String valor) {
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, valor);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo consultar la base de datos", e);
        }
    }

    // Convierte una fila de la tabla en un objeto Socio
    private Socio armarSocio(ResultSet rs) throws SQLException {
        return new Socio(
                rs.getInt("id"),
                rs.getString("numero_socio"),
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getBoolean("activo"));
    }
}