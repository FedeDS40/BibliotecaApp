package com.IngSoftware.BibliotecaApp.presentacion;

import com.IngSoftware.BibliotecaApp.excepciones.AccesoDatosException;
import com.IngSoftware.BibliotecaApp.excepciones.DatosInvalidosException;
import com.IngSoftware.BibliotecaApp.excepciones.ReglaNegocioException;
import com.IngSoftware.BibliotecaApp.modelo.Socio;
import com.IngSoftware.BibliotecaApp.negocio.LibroService;
import com.IngSoftware.BibliotecaApp.negocio.PrestamoService;

// Menú del socio: consulta libros y disponibilidad, y sus propios préstamos.
// Recibe al socio que ingresó, así solo puede ver su información (RF-08).
public class MenuSocio {

    private final Socio socio;
    private final LibroService libroService = new LibroService();
    private final PrestamoService prestamoService = new PrestamoService();

    public MenuSocio(Socio socio) {
        this.socio = socio;
    }

    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            mostrarOpciones();
            int opcion = ConsolaUtil.leerEntero("Elija una opción: ");
            switch (opcion) {
                case 1 -> ejecutar(this::consultarLibros);
                case 2 -> ejecutar(this::misPrestamos);
                case 0 -> salir = true;
                default -> ConsolaUtil.mostrarError("Opción inválida. Elija un número del menú.");
            }
        }
    }

    private void mostrarOpciones() {
        ConsolaUtil.titulo("MENÚ SOCIO - " + socio.getNombreCompleto()
                + " (" + socio.getNumeroSocio() + ")");
        System.out.println(" 1. Consultar libros y disponibilidad");
        System.out.println(" 2. Mis préstamos");
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

    // CU-09
    private void consultarLibros() {
        ConsolaUtil.titulo("Consultar libros");
        System.out.println("1. Buscar por título o autor");
        System.out.println("2. Listar todos los libros");
        int opcion = ConsolaUtil.leerEntero("Elija una opción: ");

        if (opcion == 1) {
            String texto = ConsolaUtil.leerTexto("Texto a buscar: ");
            Vista.mostrarLibros(libroService.buscar(texto, true), libroService);
        } else if (opcion == 2) {
            Vista.mostrarLibros(libroService.listarTodos(true), libroService);
        } else {
            ConsolaUtil.mostrarError("Opción inválida.");
        }
    }

    // CU-13: solo los préstamos del socio que ingresó
    private void misPrestamos() {
        ConsolaUtil.titulo("Mis préstamos");
        Vista.mostrarPrestamos(prestamoService.listarPrestamosDeSocio(socio.getNumeroSocio()));
    }
}

