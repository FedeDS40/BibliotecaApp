package com.IngSoftware.BibliotecaApp.modelo;

public class Socio {

    private int id;
    private String numeroSocio;
    private String dni;
    private String nombre;
    private String apellido;
    private boolean activo;

    // Para crear un socio nuevo (queda activo)
    public Socio(String numeroSocio, String dni, String nombre, String apellido) {
        this.numeroSocio = numeroSocio;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.activo = true;
    }

    // Para armar un socio leído desde la base de datos
    public Socio(int id, String numeroSocio, String dni, String nombre,
                 String apellido, boolean activo) {
        this.id = id;
        this.numeroSocio = numeroSocio;
        this.dni = dni;
        this.nombre = nombre;
        this.apellido = apellido;
        this.activo = activo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumeroSocio() { return numeroSocio; }

    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public boolean estaActivo() { return activo; }

    public void darDeBaja() { this.activo = false; }
}