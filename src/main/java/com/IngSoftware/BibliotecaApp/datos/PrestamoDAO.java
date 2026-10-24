package com.IngSoftware.BibliotecaApp.datos;

import com.IngSoftware.BibliotecaApp.excepciones.AccesoDatosException;
import com.IngSoftware.BibliotecaApp.modelo.Libro;
import com.IngSoftware.BibliotecaApp.modelo.Prestamo;
import com.IngSoftware.BibliotecaApp.modelo.Socio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO {

    // Trae el préstamo junto con los datos de su socio y de su libro
    private static final String SELECT_BASE =
            "SELECT p.id, p.fecha_prestamo, p.fecha_vencimiento, p.fecha_devolucion, "
          + "s.id AS s_id, s.numero_socio, s.dni, s.nombre, s.apellido, s.activo AS s_activo, "
          + "l.id AS l_id, l.titulo, l.autor, l.editorial, l.anio_publicacion, l.activo AS l_activo "
          + "FROM prestamo p "
          + "JOIN socio s ON s.id = p.id_socio "
          + "JOIN libro l ON l.id = p.id_libro ";

    // Guarda un préstamo nuevo y le asigna el id que le dio la base
    public void insertar(Prestamo prestamo) {
        String sql = "INSERT INTO prestamo (id_socio, id_libro, fecha_prestamo, fecha_vencimiento) "
                   + "VALUES (?, ?, ?, ?)";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, prestamo.getSocio().getId());
            ps.setInt(2, prestamo.getLibro().getId());
            ps.setObject(3, prestamo.getFechaPrestamo());
            ps.setObject(4, prestamo.getFechaVencimiento());
            ps.executeUpdate();
            try (ResultSet claves = ps.getGeneratedKeys()) {
                if (claves.next()) {
                    prestamo.setId(claves.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo guardar el préstamo", e);
        }
    }

    // Registra la devolución. Devuelve false si el préstamo no existe
    // o si ya estaba devuelto (RN-08: se registra una sola vez).
    public boolean actualizarDevolucion(int idPrestamo, LocalDate fecha) {
        String sql = "UPDATE prestamo SET fecha_devolucion = ? "
                   + "WHERE id = ? AND fecha_devolucion IS NULL";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setObject(1, fecha);
            ps.setInt(2, idPrestamo);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo registrar la devolución", e);
        }
    }

    // Devuelve el préstamo, o null si no existe
    public Prestamo buscarPorId(int id) {
        String sql = SELECT_BASE + "WHERE p.id = ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? armarPrestamo(rs) : null;
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo buscar el préstamo", e);
        }
    }

    // RN-03: cuántos préstamos sin devolver tiene un socio
    public int contarSinDevolver(int idSocio) {
        String sql = "SELECT COUNT(*) FROM prestamo WHERE id_socio = ? AND fecha_devolucion IS NULL";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSocio);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo contar los préstamos", e);
        }
    }

    // RN-06 y RN-07: ¿el socio tiene algún préstamo vencido sin devolver?
    public boolean existeVencido(int idSocio, LocalDate hoy) {
        String sql = "SELECT 1 FROM prestamo "
                   + "WHERE id_socio = ? AND fecha_devolucion IS NULL AND fecha_vencimiento < ?";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idSocio);
            ps.setObject(2, hoy);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo consultar los vencimientos", e);
        }
    }

    // RN-02 y RN-09: ¿el libro tiene un préstamo sin devolver?
    // Un libro está disponible cuando esto da false.
    public boolean libroTienePrestamoSinDevolver(int idLibro) {
        String sql = "SELECT 1 FROM prestamo WHERE id_libro = ? AND fecha_devolucion IS NULL";
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idLibro);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo consultar la disponibilidad", e);
        }
    }

    // Todos los préstamos sin devolver (vigentes y vencidos).
    // La capa de negocio los separa con Prestamo.estaVencido().
    public List<Prestamo> listarSinDevolver() {
        String sql = SELECT_BASE + "WHERE p.fecha_devolucion IS NULL ORDER BY p.fecha_vencimiento, p.id";
        return consultarLista(sql, null);
    }

    // Historial completo de un socio, del más reciente al más antiguo
    public List<Prestamo> buscarPorSocio(int idSocio) {
        String sql = SELECT_BASE + "WHERE p.id_socio = ? ORDER BY p.fecha_prestamo DESC, p.id DESC";
        return consultarLista(sql, idSocio);
    }

    // Préstamos sin devolver de un socio
    public List<Prestamo> buscarSinDevolverPorSocio(int idSocio) {
        String sql = SELECT_BASE + "WHERE p.id_socio = ? AND p.fecha_devolucion IS NULL "
                   + "ORDER BY p.fecha_vencimiento, p.id";
        return consultarLista(sql, idSocio);
    }

    private List<Prestamo> consultarLista(String sql, Integer parametro) {
        List<Prestamo> prestamos = new ArrayList<>();
        try (Connection con = Conexion.obtenerConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (parametro != null) {
                ps.setInt(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    prestamos.add(armarPrestamo(rs));
                }
            }
        } catch (SQLException e) {
            throw new AccesoDatosException("No se pudo listar los préstamos", e);
        }
        return prestamos;
    }

    // Convierte una fila (préstamo + socio + libro) en un objeto Prestamo
    private Prestamo armarPrestamo(ResultSet rs) throws SQLException {
        Socio socio = new Socio(
                rs.getInt("s_id"),
                rs.getString("numero_socio"),
                rs.getString("dni"),
                rs.getString("nombre"),
                rs.getString("apellido"),
                rs.getBoolean("s_activo"));

        int anio = rs.getInt("anio_publicacion");
        Integer anioPublicacion = rs.wasNull() ? null : anio;
        Libro libro = new Libro(
                rs.getInt("l_id"),
                rs.getString("titulo"),
                rs.getString("autor"),
                rs.getString("editorial"),
                anioPublicacion,
                rs.getBoolean("l_activo"));

        return new Prestamo(
                rs.getInt("id"),
                socio,
                libro,
                rs.getObject("fecha_prestamo", LocalDate.class),
                rs.getObject("fecha_vencimiento", LocalDate.class),
                rs.getObject("fecha_devolucion", LocalDate.class));
    }
}
