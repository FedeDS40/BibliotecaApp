package com.IngSoftware.BibliotecaApp.negocio;

import com.IngSoftware.BibliotecaApp.datos.LibroDAO;
import com.IngSoftware.BibliotecaApp.datos.PrestamoDAO;
import com.IngSoftware.BibliotecaApp.excepciones.DatosInvalidosException;
import com.IngSoftware.BibliotecaApp.excepciones.ReglaNegocioException;
import com.IngSoftware.BibliotecaApp.modelo.Libro;

import java.util.List;

public class LibroService {

    private final LibroDAO libroDAO = new LibroDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    // RF-03: registra un ejemplar nuevo. La editorial y el año son opcionales.
    public Libro registrar(String titulo, String autor, String editorial, String anioTexto) {
        titulo = limpiar(titulo);
        autor = limpiar(autor);
        editorial = limpiar(editorial);

        validarDatos(titulo, autor, editorial);
        Integer anio = convertirAnio(anioTexto);

        Libro libro = new Libro(titulo, autor, editorial.isEmpty() ? null : editorial, anio);
        libroDAO.insertar(libro);
        return libro;
    }

    // RF-03: modifica los datos de un ejemplar existente
    public Libro modificar(int id, String titulo, String autor, String editorial, String anioTexto) {
        Libro libro = obtenerPorId(id);
        titulo = limpiar(titulo);
        autor = limpiar(autor);
        editorial = limpiar(editorial);

        validarDatos(titulo, autor, editorial);
        Integer anio = convertirAnio(anioTexto);

        libro.setTitulo(titulo);
        libro.setAutor(autor);
        libro.setEditorial(editorial.isEmpty() ? null : editorial);
        libro.setAnioPublicacion(anio);
        libroDAO.actualizar(libro);
        return libro;
    }

    // RF-03: devuelve el libro o avisa si no existe
    public Libro obtenerPorId(int id) {
        Libro libro = libroDAO.buscarPorId(id);
        if (libro == null) {
            throw new ReglaNegocioException("No existe un libro con el id " + id + ".");
        }
        return libro;
    }

    // RF-04: busca por una parte del título o del autor.
    // soloActivos = true para socios y bibliotecarios; false para el administrador.
    public List<Libro> buscar(String texto, boolean soloActivos) {
        texto = limpiar(texto);
        if (texto.isEmpty()) {
            throw new DatosInvalidosException("Debe ingresar un texto para buscar.");
        }
        return libroDAO.buscarPorTituloOAutor(texto, soloActivos);
    }

    public List<Libro> listarTodos(boolean soloActivos) {
        return libroDAO.listarTodos(soloActivos);
    }

    // RN-02: un libro está disponible si está activo y no tiene un préstamo sin devolver
    public boolean estaDisponible(Libro libro) {
        return libro.estaActivo()
                && !prestamoDAO.libroTienePrestamoSinDevolver(libro.getId());
    }

    // RF-03, RN-09 y RN-10: baja lógica, solo si no tiene préstamos sin devolver
    public Libro darDeBaja(int id) {
        Libro libro = obtenerPorId(id);

        if (!libro.estaActivo()) {
            throw new ReglaNegocioException("El libro " + id + " ya está dado de baja.");
        }
        if (prestamoDAO.libroTienePrestamoSinDevolver(id)) {
            throw new ReglaNegocioException(
                    "No se puede dar de baja el libro: tiene un préstamo sin devolver.");
        }

        libro.darDeBaja();
        libroDAO.actualizar(libro);
        return libro;
    }

    private void validarDatos(String titulo, String autor, String editorial) {
        if (titulo.isEmpty()) {
            throw new DatosInvalidosException("El título es obligatorio.");
        }
        if (autor.isEmpty()) {
            throw new DatosInvalidosException("El autor es obligatorio.");
        }
        if (titulo.length() > 150) {
            throw new DatosInvalidosException("El título no puede superar los 150 caracteres.");
        }
        if (autor.length() > 100 || editorial.length() > 100) {
            throw new DatosInvalidosException(
                    "El autor y la editorial no pueden superar los 100 caracteres.");
        }
    }

    // El año es opcional: vacío = null. Si se informa, debe ser un número válido.
    private Integer convertirAnio(String anioTexto) {
        String texto = limpiar(anioTexto);
        if (texto.isEmpty()) {
            return null;
        }
        if (!texto.matches("\\d{4}")) {
            throw new DatosInvalidosException("El año debe ser un número de 4 cifras.");
        }
        int anio = Integer.parseInt(texto);
        if (anio < 1000 || anio > 2100) {
            throw new DatosInvalidosException("El año debe estar entre 1000 y 2100.");
        }
        return anio;
    }

    private String limpiar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}