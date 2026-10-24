package com.IngSoftware.BibliotecaApp;

import com.IngSoftware.BibliotecaApp.datos.PrestamoDAO;
import com.IngSoftware.BibliotecaApp.modelo.Prestamo;

import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        PrestamoDAO dao = new PrestamoDAO();
        LocalDate hoy = LocalDate.now();

        System.out.println("Préstamos sin devolver de Martín Gómez (esperado: 3): "
                + dao.contarSinDevolver(2));
        System.out.println("¿Carla Ruiz tiene vencidos? (esperado: true): "
                + dao.existeVencido(3, hoy));
        System.out.println("¿Lucía Fernández tiene vencidos? (esperado: false): "
                + dao.existeVencido(1, hoy));
        System.out.println("¿Rayuela está prestado? (esperado: true): "
                + dao.libroTienePrestamoSinDevolver(1));
        System.out.println("¿Cien años (id 5) está prestado? (esperado: false): "
                + dao.libroTienePrestamoSinDevolver(5));

        System.out.println("--- Préstamos sin devolver (esperado: 4) ---");
        for (Prestamo p : dao.listarSinDevolver()) {
            System.out.println("#" + p.getId() + " " + p.getSocio().getNombreCompleto()
                    + " - " + p.getLibro().getTitulo()
                    + " - vence " + p.getFechaVencimiento()
                    + (p.estaVencido(hoy) ? " (VENCIDO, " + p.diasDeAtraso(hoy) + " días)" : ""));
        }

        System.out.println("--- Historial de Sofía Díaz (esperado: 2) ---");
        for (Prestamo p : dao.buscarPorSocio(5)) {
            System.out.println("#" + p.getId() + " " + p.getLibro().getTitulo()
                    + " - devuelto " + p.getFechaDevolucion());
        }
    }
}