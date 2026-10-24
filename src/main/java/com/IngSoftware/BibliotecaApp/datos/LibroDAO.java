package com.IngSoftware.BibliotecaApp.datos;

import com.IngSoftware.BibliotecaApp.excepciones.AccesoDatosException;
import com.IngSoftware.BibliotecaApp.modelo.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class LibroDAO {

    // Guarda un libro nuevo y le asigna el id que le dio la base
    public void insertar(Libro libro) {
        String sql = "INSERT INTO libro (titulo, autor, editorial, anio_publicacion, activo) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            asignarAnio(ps, 4, libro.getAnioPublicacion());
            ps.setBoolean(5, libro.estaActivo());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    libro.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo guardar el libro", e);
        }
    }

    // Modifica un libro existente (también sirve para la baja lógica)
    public void actualizar(Libro libro) {
        String sql = "UPDATE libro SET titulo = ?, autor = ?, editorial = ?, "
                   + "anio_publicacion = ?, activo = ? WHERE id = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getEditorial());
            asignarAnio(ps, 4, libro.getAnioPublicacion());
            ps.setBoolean(5, libro.estaActivo());
            ps.setInt(6, libro.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo actualizar el libro", e);
        }
    }

    // Devuelve el libro, o null si no existe
    public Libro buscarPorId(int id) {
        String sql = "SELECT * FROM libro WHERE id = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? armarLibro(rs) : null;
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo buscar el libro", e);
        }
    }

    // RF-04: busca por una parte del título o del autor, sin distinguir mayúsculas.
    // Si soloActivos es true, no devuelve los libros dados de baja.
    public List<Libro> buscarPorTituloOAutor(String texto, boolean soloActivos) {
        String sql = "SELECT * FROM libro WHERE (titulo ILIKE ? OR autor ILIKE ?)"
                   + (soloActivos ? " AND activo = TRUE" : "")
                   + " ORDER BY titulo, id";
        String patron = "%" + texto + "%";
        List<Libro> libros = new ArrayList<>();
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, patron);
            ps.setString(2, patron);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    libros.add(armarLibro(rs));
                }
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo buscar libros", e);
        }
        return libros;
    }

    public List<Libro> listarTodos(boolean soloActivos) {
        String sql = "SELECT * FROM libro"
                   + (soloActivos ? " WHERE activo = TRUE" : "")
                   + " ORDER BY titulo, id";
        List<Libro> libros = new ArrayList<>();
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                libros.add(armarLibro(rs));
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo listar los libros", e);
        }
        return libros;
    }

    // El año es opcional: si viene vacío se guarda NULL en la base
    private void asignarAnio(PreparedStatement ps, int posicion, Integer anio) throws SQLException {
        if (anio == null) {
            ps.setNull(posicion, Types.INTEGER);
        } else {
            ps.setInt(posicion, anio);
        }
    }

    // Convierte una fila de la tabla en un objeto Libro
    private Libro armarLibro(ResultSet rs) throws SQLException {
        int anio = rs.getInt("anio_publicacion");
        Integer anioPublicacion = rs.wasNull() ? null : anio;
        return new Libro(
                rs.getInt("id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("editorial"),
                anioPublicacion,
                rs.getBoolean("activo"));
    }
}