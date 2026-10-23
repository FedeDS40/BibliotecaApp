package com.IngSoftware.BibliotecaApp.modelo;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Prestamo {

    // RN-05: el plazo de préstamo es de 7 días corridos
    public static final int PLAZO_DIAS = 7;

    private int id;
    private Socio socio;
    private Libro libro;
    private LocalDate fechaPrestamo;
    private LocalDate fechaVencimiento;
    private LocalDate fechaDevolucion; // null mientras no se devuelve

    // Para crear un préstamo nuevo: el vencimiento se calcula solo
    public Prestamo(Socio socio, Libro libro, LocalDate fechaPrestamo) {
        this.socio = socio;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaVencimiento = fechaPrestamo.plusDays(PLAZO_DIAS);
    }

    // Para armar un préstamo leído desde la base de datos
    public Prestamo(int id, Socio socio, Libro libro, LocalDate fechaPrestamo,
                    LocalDate fechaVencimiento, LocalDate fechaDevolucion) {
        this.id = id;
        this.socio = socio;
        this.libro = libro;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaVencimiento = fechaVencimiento;
        this.fechaDevolucion = fechaDevolucion;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Socio getSocio() { return socio; }
    public Libro getLibro() { return libro; }
    public LocalDate getFechaPrestamo() { return fechaPrestamo; }
    public LocalDate getFechaVencimiento() { return fechaVencimiento; }
    public LocalDate getFechaDevolucion() { return fechaDevolucion; }

    public boolean estaDevuelto() {
        return fechaDevolucion != null;
    }

    // RN-07: vencido = pasó la fecha de vencimiento y no fue devuelto
    public boolean estaVencido(LocalDate hoy) {
        return !estaDevuelto() && hoy.isAfter(fechaVencimiento);
    }

    public long diasDeAtraso(LocalDate hoy) {
        if (!estaVencido(hoy)) {
            return 0;
        }
        return ChronoUnit.DAYS.between(fechaVencimiento, hoy);
    }

    public void registrarDevolucion(LocalDate fecha) {
        this.fechaDevolucion = fecha;
    }
}
