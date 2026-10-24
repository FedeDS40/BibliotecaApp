package com.IngSoftware.BibliotecaApp.presentacion;

import com.IngSoftware.BibliotecaApp.modelo.Libro;
import com.IngSoftware.BibliotecaApp.modelo.Prestamo;
import com.IngSoftware.BibliotecaApp.modelo.Socio;
import com.IngSoftware.BibliotecaApp.negocio.LibroService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

// Muestra datos y comprobantes en la consola. No toma decisiones ni usa la base de datos.
public class Vista {

    // Acorta un texto largo para que la tabla no se desarme
    private static String cortar(String texto, int max) {
        if (texto == null || texto.isEmpty()) {
            return "-";
        }
        return texto.length() <= max ? texto : texto.substring(0, max - 3) + "...";
    }

    public static void mostrarSocio(Socio s) {
        System.out.println("  Número de socio : " + s.getNumeroSocio());
        System.out.println("  DNI             : " + s.getDni());
        System.out.println("  Nombre          : " + s.getNombreCompleto());
        System.out.println("  Estado          : " + (s.estaActivo() ? "Activo" : "Dado de baja"));
    }

    public static void mostrarSocios(List<Socio> socios) {
        if (socios.isEmpty()) {
            System.out.println("No hay socios registrados.");
            return;
        }
        System.out.printf("%-8s %-10s %-30s %s%n", "Número", "DNI", "Nombre", "Estado");
        for (Socio s : socios) {
            System.out.printf("%-8s %-10s %-30s %s%n", s.getNumeroSocio(), s.getDni(),
                    cortar(s.getNombreCompleto(), 30), s.estaActivo() ? "Activo" : "Dado de baja");
        }
        System.out.println("Total: " + socios.size());
    }

    // Muestra los libros con su disponibilidad (RF-04)
    public static void mostrarLibros(List<Libro> libros, LibroService libroService) {
        if (libros.isEmpty()) {
            System.out.println("No se encontraron libros.");
            return;
        }
        System.out.printf("%-4s %-30s %-24s %-14s %-5s %s%n",
                "ID", "Título", "Autor", "Editorial", "Año", "Estado");
        for (Libro l : libros) {
            String estado;
            if (!l.estaActivo()) {
                estado = "Dado de baja";
            } else if (libroService.estaDisponible(l)) {
                estado = "Disponible";
            } else {
                estado = "Prestado";
            }
            String anio = l.getAnioPublicacion() == null ? "-" : l.getAnioPublicacion().toString();
            System.out.printf("%-4d %-30s %-24s %-14s %-5s %s%n", l.getId(),
                    cortar(l.getTitulo(), 30), cortar(l.getAutor(), 24),
                    cortar(l.getEditorial(), 14), anio, estado);
        }
        System.out.println("Total: " + libros.size());
    }

    public static void mostrarLibro(Libro l) {
        System.out.println("  ID        : " + l.getId());
        System.out.println("  Título    : " + l.getTitulo());
        System.out.println("  Autor     : " + l.getAutor());
        System.out.println("  Editorial : " + (l.getEditorial() == null ? "-" : l.getEditorial()));
        System.out.println("  Año       : " + (l.getAnioPublicacion() == null ? "-" : l.getAnioPublicacion()));
        System.out.println("  Estado    : " + (l.estaActivo() ? "Activo" : "Dado de baja"));
    }

    // Lista de préstamos con su estado: vigente, vencido o devuelto
    public static void mostrarPrestamos(List<Prestamo> prestamos) {
        if (prestamos.isEmpty()) {
            System.out.println("No hay préstamos para mostrar.");
            return;
        }
        LocalDate hoy = LocalDate.now();
        System.out.printf("%-4s %-22s %-26s %-10s %-10s %s%n",
                "ID", "Socio", "Libro", "Préstamo", "Vence", "Estado");
        for (Prestamo p : prestamos) {
            String estado;
            if (p.estaDevuelto()) {
                estado = "Devuelto " + p.getFechaDevolucion();
            } else if (p.estaVencido(hoy)) {
                estado = "VENCIDO (" + p.diasDeAtraso(hoy) + " días)";
            } else {
                estado = "Vigente";
            }
            System.out.printf("%-4d %-22s %-26s %-10s %-10s %s%n", p.getId(),
                    cortar(p.getSocio().getNombreCompleto(), 22),
                    cortar(p.getLibro().getTitulo(), 26),
                    p.getFechaPrestamo(), p.getFechaVencimiento(), estado);
        }
        System.out.println("Total: " + prestamos.size());
    }

    // Comprobante que se muestra al registrar un préstamo
    public static void comprobantePrestamo(Prestamo p) {
        System.out.println();
        System.out.println("==========================================");
        System.out.println("          COMPROBANTE DE PRÉSTAMO");
        System.out.println("==========================================");
        System.out.println(" Préstamo número : " + p.getId());
        System.out.println(" Socio           : " + p.getSocio().getNumeroSocio()
                + " - " + p.getSocio().getNombreCompleto());
        System.out.println(" Libro           : (" + p.getLibro().getId() + ") "
                + p.getLibro().getTitulo() + " - " + p.getLibro().getAutor());
        System.out.println(" Fecha préstamo  : " + p.getFechaPrestamo());
        System.out.println(" Vence el        : " + p.getFechaVencimiento()
                + " (" + Prestamo.PLAZO_DIAS + " días corridos)");
        System.out.println("==========================================");
    }

    // Comprobante que se muestra al registrar una devolución
    public static void comprobanteDevolucion(Prestamo p) {
        System.out.println();
        System.out.println("==========================================");
        System.out.println("         COMPROBANTE DE DEVOLUCIÓN");
        System.out.println("==========================================");
        System.out.println(" Préstamo número : " + p.getId());
        System.out.println(" Socio           : " + p.getSocio().getNumeroSocio()
                + " - " + p.getSocio().getNombreCompleto());
        System.out.println(" Libro           : (" + p.getLibro().getId() + ") "
                + p.getLibro().getTitulo() + " - " + p.getLibro().getAutor());
        System.out.println(" Fecha préstamo  : " + p.getFechaPrestamo());
        System.out.println(" Vencía el       : " + p.getFechaVencimiento());
        System.out.println(" Devuelto el     : " + p.getFechaDevolucion());
        if (p.getFechaDevolucion().isAfter(p.getFechaVencimiento())) {
            long atraso = ChronoUnit.DAYS.between(p.getFechaVencimiento(), p.getFechaDevolucion());
            System.out.println(" Observación     : devuelto con " + atraso + " día(s) de atraso");
        } else {
            System.out.println(" Observación     : devuelto en término");
        }
        System.out.println("==========================================");
    }
}