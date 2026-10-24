package com.IngSoftware.BibliotecaApp.presentacion;

import com.IngSoftware.BibliotecaApp.excepciones.AccesoDatosException;
import com.IngSoftware.BibliotecaApp.excepciones.DatosInvalidosException;
import com.IngSoftware.BibliotecaApp.excepciones.ReglaNegocioException;
import com.IngSoftware.BibliotecaApp.modelo.Libro;
import com.IngSoftware.BibliotecaApp.modelo.Socio;
import com.IngSoftware.BibliotecaApp.negocio.LibroService;
import com.IngSoftware.BibliotecaApp.negocio.PrestamoService;
import com.IngSoftware.BibliotecaApp.negocio.SocioService;

// Menú del administrador: gestiona libros y consulta la información general
// de socios y préstamos. Solo muestra y lee datos; las reglas las aplican los servicios.
public class MenuAdministrador {

    private final SocioService socioService = new SocioService();
    private final LibroService libroService = new LibroService();
    private final PrestamoService prestamoService = new PrestamoService();

    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            mostrarOpciones();
            int opcion = ConsolaUtil.leerEntero("Elija una opción: ");
            switch (opcion) {
                case 1 -> ejecutar(this::registrarLibro);
                case 2 -> ejecutar(this::modificarLibro);
                case 3 -> ejecutar(this::consultarLibros);
                case 4 -> ejecutar(this::darDeBajaLibro);
                case 5 -> ejecutar(this::consultarSocios);
                case 6 -> ejecutar(this::historialSocio);
                case 7 -> ejecutar(this::listarVigentes);
                case 8 -> ejecutar(this::listarVencidos);
                case 0 -> salir = true;
                default -> ConsolaUtil.mostrarError("Opción inválida. Elija un número del menú.");
            }
        }
    }

    private void mostrarOpciones() {
        ConsolaUtil.titulo("MENÚ ADMINISTRADOR");
        System.out.println(" 1. Registrar libro");
        System.out.println(" 2. Modificar libro");
        System.out.println(" 3. Consultar libros");
        System.out.println(" 4. Dar de baja libro");
        System.out.println(" 5. Consultar socios");
        System.out.println(" 6. Historial de préstamos de un socio");
        System.out.println(" 7. Listar préstamos vigentes");
        System.out.println(" 8. Listar préstamos vencidos");
        System.out.println(" 0. Volver");
    }

    // Ejecuta una opción y muestra cualquier error sin cerrar el programa
    private void ejecutar(Runnable accion) {
        try {
            accion.run();
        } catch (ReglaNegocioException | DatosInvalidosException e) {
            ConsolaUtil.mostrarError(e.getMessage());
        } catch (AccesoDatosException e) {
            ConsolaUtil.mostrarError(e.getMessage()
                    + ". Verifique que la base de datos esté disponible.");
        }
        ConsolaUtil.pausa();
    }

    // CU-06
    private void registrarLibro() {
        ConsolaUtil.titulo("Registrar libro");
        String titulo = ConsolaUtil.leerTexto("Título: ");
        String autor = ConsolaUtil.leerTexto("Autor: ");
        String editorial = ConsolaUtil.leerTexto("Editorial (opcional, Enter para omitir): ");
        String anio = ConsolaUtil.leerTexto("Año de publicación (opcional, Enter para omitir): ");

        Libro libro = libroService.registrar(titulo, autor, editorial, anio);
        ConsolaUtil.mostrarExito("La operación fue registrada. Libro dado de alta:");
        Vista.mostrarLibro(libro);
    }

    // CU-07
    private void modificarLibro() {
        ConsolaUtil.titulo("Modificar libro");
        int id = ConsolaUtil.leerEntero("ID del libro a modificar: ");
        Libro actual = libroService.obtenerPorId(id);
        Vista.mostrarLibro(actual);

        System.out.println("\nEscriba los datos nuevos (Enter para dejar el actual).");
        String titulo = ConsolaUtil.leerTexto("Título [" + actual.getTitulo() + "]: ");
        String autor = ConsolaUtil.leerTexto("Autor [" + actual.getAutor() + "]: ");
        String editorial = ConsolaUtil.leerTexto("Editorial ["
                + (actual.getEditorial() == null ? "-" : actual.getEditorial()) + "]: ");
        String anio = ConsolaUtil.leerTexto("Año ["
                + (actual.getAnioPublicacion() == null ? "-" : actual.getAnioPublicacion()) + "]: ");

        if (titulo.isEmpty()) titulo = actual.getTitulo();
        if (autor.isEmpty()) autor = actual.getAutor();
        if (editorial.isEmpty() && actual.getEditorial() != null) editorial = actual.getEditorial();
        if (anio.isEmpty() && actual.getAnioPublicacion() != null) {
            anio = actual.getAnioPublicacion().toString();
        }

        Libro modificado = libroService.modificar(id, titulo, autor, editorial, anio);
        ConsolaUtil.mostrarExito("La operación fue registrada. Datos actualizados:");
        Vista.mostrarLibro(modificado);
    }

    // CU-09: el administrador ve también los libros dados de baja
    private void consultarLibros() {
        ConsolaUtil.titulo("Consultar libros");
        System.out.println("1. Buscar por título o autor");
        System.out.println("2. Listar todos los libros");
        int opcion = ConsolaUtil.leerEntero("Elija una opción: ");

        if (opcion == 1) {
            String texto = ConsolaUtil.leerTexto("Texto a buscar: ");
            Vista.mostrarLibros(libroService.buscar(texto, false), libroService);
        } else if (opcion == 2) {
            Vista.mostrarLibros(libroService.listarTodos(false), libroService);
        } else {
            ConsolaUtil.mostrarError("Opción inválida.");
        }
    }

    // CU-08
    private void darDeBajaLibro() {
        ConsolaUtil.titulo("Dar de baja libro");
        int id = ConsolaUtil.leerEntero("ID del libro a dar de baja: ");
        Libro libro = libroService.obtenerPorId(id);
        Vista.mostrarLibro(libro);

        String confirmacion = ConsolaUtil.leerTexto("\n¿Confirma la baja? (S/N): ");
        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("Operación cancelada. No se hicieron cambios.");
            return;
        }
        libroService.darDeBaja(id);
        ConsolaUtil.mostrarExito("La operación fue registrada. El libro quedó dado de baja.");
    }

    // CU-04
    private void consultarSocios() {
        ConsolaUtil.titulo("Consultar socios");
        String numero = ConsolaUtil.leerTexto("Número de socio (Enter para listar todos): ");
        if (numero.isEmpty()) {
            Vista.mostrarSocios(socioService.listarTodos());
        } else {
            Vista.mostrarSocio(socioService.obtenerPorNumero(numero));
        }
    }

    // CU-12
    private void historialSocio() {
        ConsolaUtil.titulo("Historial de préstamos de un socio");
        String numero = ConsolaUtil.leerTexto("Número de socio: ");
        Socio socio = socioService.obtenerPorNumero(numero);
        Vista.mostrarSocio(socio);
        System.out.println();
        Vista.mostrarPrestamos(prestamoService.listarPrestamosDeSocio(numero));
    }

    // CU-14
    private void listarVigentes() {
        ConsolaUtil.titulo("Préstamos vigentes");
        Vista.mostrarPrestamos(prestamoService.listarVigentes());
    }

    // CU-14
    private void listarVencidos() {
        ConsolaUtil.titulo("Préstamos vencidos");
        Vista.mostrarPrestamos(prestamoService.listarVencidos());
    }
}
