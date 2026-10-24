package com.IngSoftware.BibliotecaApp.negocio;

import com.IngSoftware.BibliotecaApp.datos.PrestamoDAO;
import com.IngSoftware.BibliotecaApp.excepciones.ReglaNegocioException;
import com.IngSoftware.BibliotecaApp.modelo.Libro;
import com.IngSoftware.BibliotecaApp.modelo.Prestamo;
import com.IngSoftware.BibliotecaApp.modelo.Socio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoService {

    // RN-03: máximo de préstamos sin devolver por socio
    public static final int MAX_PRESTAMOS = 3;

    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    private final SocioService socioService = new SocioService();
    private final LibroService libroService = new LibroService();

    // CU-10: registra un préstamo aplicando todas las reglas
    public Prestamo registrarPrestamo(String numeroSocio, int idLibro) {
        LocalDate hoy = LocalDate.now(); // RN-12: fecha actual del sistema

        Socio socio = socioService.obtenerPorNumero(numeroSocio);
        validarSocioHabilitado(socio, hoy);

        Libro libro = libroService.obtenerPorId(idLibro);
        // RN-02: solo se prestan libros activos y disponibles
        if (!libro.estaActivo()) {
            throw new ReglaNegocioException("El libro " + idLibro
                    + " está dado de baja y no puede prestarse.");
        }
        if (!libroService.estaDisponible(libro)) {
            throw new ReglaNegocioException("El libro " + idLibro
                    + " no está disponible: tiene un préstamo sin devolver.");
        }

        // RN-04 y RN-05: un socio, un ejemplar, vencimiento a 7 días
        Prestamo prestamo = new Prestamo(socio, libro, hoy);
        prestamoDAO.insertar(prestamo);
        return prestamo;
    }

    // CU-11: registra la devolución de un préstamo
    public Prestamo registrarDevolucion(int idPrestamo) {
        Prestamo prestamo = prestamoDAO.buscarPorId(idPrestamo);
        if (prestamo == null) {
            throw new ReglaNegocioException("No existe un préstamo con el id " + idPrestamo + ".");
        }

        // RN-08: la devolución se registra una sola vez
        if (prestamo.estaDevuelto()) {
            throw new ReglaNegocioException("El préstamo " + idPrestamo
                    + " ya fue devuelto el " + prestamo.getFechaDevolucion() + ".");
        }

        LocalDate hoy = LocalDate.now(); // RN-12
        boolean registrada = prestamoDAO.actualizarDevolucion(idPrestamo, hoy);
        if (!registrada) {
            throw new ReglaNegocioException("El préstamo " + idPrestamo + " ya fue devuelto.");
        }

        prestamo.registrarDevolucion(hoy);
        return prestamo;
    }

    // CU-14: préstamos sin devolver cuyo vencimiento todavía no pasó
    public List<Prestamo> listarVigentes() {
        LocalDate hoy = LocalDate.now();
        List<Prestamo> vigentes = new ArrayList<>();
        for (Prestamo p : prestamoDAO.listarSinDevolver()) {
            if (!p.estaVencido(hoy)) {
                vigentes.add(p);
            }
        }
        return vigentes;
    }

    // CU-14 y RN-07: préstamos sin devolver cuyo vencimiento ya pasó
    public List<Prestamo> listarVencidos() {
        LocalDate hoy = LocalDate.now();
        List<Prestamo> vencidos = new ArrayList<>();
        for (Prestamo p : prestamoDAO.listarSinDevolver()) {
            if (p.estaVencido(hoy)) {
                vencidos.add(p);
            }
        }
        return vencidos;
    }

    // CU-12 y CU-13: historial completo de un socio
    public List<Prestamo> listarPrestamosDeSocio(String numeroSocio) {
        Socio socio = socioService.obtenerPorNumero(numeroSocio);
        return prestamoDAO.buscarPorSocio(socio.getId());
    }

    // CU-15: comprueba que el socio pueda recibir un préstamo
    private void validarSocioHabilitado(Socio socio, LocalDate hoy) {
        // RN-01: solo socios activos
        if (!socio.estaActivo()) {
            throw new ReglaNegocioException("El socio " + socio.getNumeroSocio()
                    + " está dado de baja y no puede recibir préstamos.");
        }

        // RN-06: con préstamos vencidos no se pueden pedir nuevos (CU-17 muestra el detalle)
        if (prestamoDAO.existeVencido(socio.getId(), hoy)) {
            throw new ReglaNegocioException("El socio tiene préstamos vencidos y debe "
                    + "devolverlos antes de pedir otro:\n" + detalleVencidos(socio, hoy));
        }

        // RN-03: máximo 3 préstamos sin devolver
        int sinDevolver = prestamoDAO.contarSinDevolver(socio.getId());
        if (sinDevolver >= MAX_PRESTAMOS) {
            throw new ReglaNegocioException("El socio ya tiene " + sinDevolver
                    + " préstamos sin devolver (máximo " + MAX_PRESTAMOS + ").");
        }
    }

    // CU-17: arma el texto con los préstamos vencidos del socio
    private String detalleVencidos(Socio socio, LocalDate hoy) {
        StringBuilder detalle = new StringBuilder();
        for (Prestamo p : prestamoDAO.buscarSinDevolverPorSocio(socio.getId())) {
            if (p.estaVencido(hoy)) {
                detalle.append("   - Préstamo #").append(p.getId())
                       .append(": ").append(p.getLibro().getTitulo())
                       .append(" (venció el ").append(p.getFechaVencimiento())
                       .append(", ").append(p.diasDeAtraso(hoy)).append(" días de atraso)\n");
            }
        }
        return detalle.toString().stripTrailing();
    }
}