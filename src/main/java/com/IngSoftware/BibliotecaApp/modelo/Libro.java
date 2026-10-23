package com.IngSoftware.BibliotecaApp.modelo;

public class Libro {

    private int id;
    private String titulo;
    private String autor;
    private String editorial;
    private Integer anioPublicacion; // puede ser null (es opcional)
    private boolean activo;

    // Para crear un libro nuevo (queda activo)
    public Libro(String titulo, String autor, String editorial, Integer anioPublicacion) {
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.activo = true;
    }

    // Para armar un libro leído desde la base de datos
    public Libro(int id, String titulo, String autor, String editorial,
                 Integer anioPublicacion, boolean activo) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.editorial = editorial;
        this.anioPublicacion = anioPublicacion;
        this.activo = activo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getAutor() { return autor; }
    public void setAutor(String autor) { this.autor = autor; }

    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }

    public Integer getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(Integer anio) { this.anioPublicacion = anio; }

    public boolean estaActivo() { return activo; }

    public void darDeBaja() { this.activo = false; }
}
