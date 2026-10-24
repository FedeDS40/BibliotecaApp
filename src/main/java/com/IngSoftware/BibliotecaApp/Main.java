package com.IngSoftware.BibliotecaApp;

import com.IngSoftware.BibliotecaApp.datos.LibroDAO;
import com.IngSoftware.BibliotecaApp.modelo.Libro;

public class Main {
    public static void main(String[] args) {
        LibroDAO dao = new LibroDAO();

        System.out.println("--- Búsqueda: borges ---");
        for (Libro l : dao.buscarPorTituloOAutor("borges", true)) {
            System.out.println(l.getId() + " - " + l.getTitulo() + " (" + l.getAutor() + ")");
        }

        System.out.println("--- Búsqueda: cien años ---");
        for (Libro l : dao.buscarPorTituloOAutor("cien años", true)) {
            System.out.println(l.getId() + " - " + l.getTitulo() + " (" + l.getAutor() + ")");
        }

        System.out.println("--- Todos los libros (activos e inactivos) ---");
        for (Libro l : dao.listarTodos(false)) {
            System.out.println(l.getId() + " - " + l.getTitulo()
                    + (l.estaActivo() ? "" : " (INACTIVO)"));
        }
    }
}