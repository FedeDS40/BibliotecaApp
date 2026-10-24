package com.IngSoftware.BibliotecaApp.presentacion;

import com.IngSoftware.BibliotecaApp.excepciones.AccesoDatosException;
import com.IngSoftware.BibliotecaApp.excepciones.DatosInvalidosException;
import com.IngSoftware.BibliotecaApp.excepciones.ReglaNegocioException;
import com.IngSoftware.BibliotecaApp.modelo.Prestamo;
import com.IngSoftware.BibliotecaApp.modelo.Socio;
import com.IngSoftware.BibliotecaApp.negocio.LibroService;
import com.IngSoftware.BibliotecaApp.negocio.PrestamoService;
import com.IngSoftware.BibliotecaApp.negocio.SocioService;

// Menú del bibliotecario: gestiona socios, consulta libros y registra préstamos y devoluciones.
// Solo muestra y lee datos; las reglas las aplican los servicios.
public class MenuBibliotecario {

    private final SocioService socioService = new SocioService();
    private final LibroService libroService = new LibroService();
    private final PrestamoService prestamoService = new PrestamoService();

    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            mostrarOpciones();
            int opcion = ConsolaUtil.leerEntero("Elija una opción: ");
            switch (opcion) {
                case 1 -> ejecutar(this::registrarSocio);
                case 2 -> ejecutar(this::modificarSocio);
                case 3 -> ejecutar(this::consultarSocio);
                case 4 -> ejecutar(this::darDeBajaSocio);
                case 5 -> ejecutar(this::consultarLibros);
                case 6 -> ejecutar(this::registrarPrestamo);
                case 7 -> ejecutar(this::registrarDevolucion);
                case 8 -> ejecutar(this::historialSocio);
                case 9 -> ejecutar(this::listarVigentes);
                case 10 -> ejecutar(this::listarVencidos);
                case 0 -> salir = true;
                default -> ConsolaUtil.mostrarError("Opción inválida. Elija un número del menú.");
            }
        }
    }

    private void mostrarOpciones() {
        ConsolaUtil.titulo("MENÚ BIBLIOTECARIO");
        System.out.println(" 1. Registrar socio");
        System.out.println(" 2. Modificar socio");
        System.out.println(" 3. Consultar socio");
        System.out.println(" 4. Dar de baja socio");
        System.out.println(" 5. Consultar libros");
        System.out.println(" 6. Registrar préstamo");
        System.out.println(" 7. Registrar devolución");
        System.out.println(" 8. Historial de préstamos de un socio");
        System.out.println(" 9. Listar préstamos vigentes");
        System.out.println("10. Listar préstamos vencidos");
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

    // CU-02
    private void registrarSocio() {
        ConsolaUtil.titulo("Registrar socio");
        String numero = ConsolaUtil.leerTexto("Número de socio: ");
        String dni = ConsolaUtil.leerTexto("DNI: ");
        String nombre = ConsolaUtil.leerTexto("Nombre: ");
        String apellido = ConsolaUtil.leerTexto("Apellido: ");

        Socio socio = socioService.registrar(numero, dni, nombre, apellido);
        ConsolaUtil.mostrarExito("La operación fue registrada. Socio dado de alta:");
        Vista.mostrarSocio(socio);
    }

    // CU-03
    private void modificarSocio() {
        ConsolaUtil.titulo("Modificar socio");
        Socio actual = socioService.obtenerPorNumero(
                ConsolaUtil.leerTexto("Número de socio a modificar: "));
        Vista.mostrarSocio(actual);

        System.out.println("\nEscriba los datos nuevos (Enter para dejar el actual).");
        String dni = ConsolaUtil.leerTexto("DNI [" + actual.getDni() + "]: ");
        String nombre = ConsolaUtil.leerTexto("Nombre [" + actual.getNombre() + "]: ");
        String apellido = ConsolaUtil.leerTexto("Apellido [" + actual.getApellido() + "]: ");

        if (dni.isEmpty()) dni = actual.getDni();
        if (nombre.isEmpty()) nombre = actual.getNombre();
        if (apellido.isEmpty()) apellido = actual.getApellido();

        Socio modificado = socioService.modificar(actual.getNumeroSocio(), dni, nombre, apellido);
        ConsolaUtil.mostrarExito("La operación fue registrada. Datos actualizados:");
        Vista.mostrarSocio(modificado);
    }

    // CU-04
    private void consultarSocio() {
        ConsolaUtil.titulo("Consultar socio");
        String numero = ConsolaUtil.leerTexto("Número de socio (Enter para listar todos): ");
        if (numero.isEmpty()) {
            Vista.mostrarSocios(socioService.listarTodos());
        } else {
            Vista.mostrarSocio(socioService.obtenerPorNumero(numero));
        }
    }

    // CU-05
    private void darDeBajaSocio() {
        ConsolaUtil.titulo("Dar de baja socio");
        Socio socio = socioService.obtenerPorNumero(
                ConsolaUtil.leerTexto("Número de socio a dar de baja: "));
        Vista.mostrarSocio(socio);

        String confirmacion = ConsolaUtil.leerTexto("\n¿Confirma la baja? (S/N): ");
        if (!confirmacion.equalsIgnoreCase("S")) {
            System.out.println("Operación cancelada. No se hicieron cambios.");
            return;
        }
        socioService.darDeBaja(socio.getNumeroSocio());
        ConsolaUtil.mostrarExito("La operación fue registrada. El socio quedó dado de baja.");
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

    // CU-10
    private void registrarPrestamo() {
        ConsolaUtil.titulo("Registrar préstamo");
        String numeroSocio = ConsolaUtil.leerTexto("Número de socio: ");
        int idLibro = ConsolaUtil.leerEntero("ID del libro: ");

        Prestamo prestamo = prestamoService.registrarPrestamo(numeroSocio, idLibro);
        ConsolaUtil.mostrarExito("La operación fue registrada.");
        Vista.comprobantePrestamo(prestamo);
    }

    // CU-11
    private void registrarDevolucion() {
        ConsolaUtil.titulo("Registrar devolución");
        int idPrestamo = ConsolaUtil.leerEntero("Número de préstamo: ");

        Prestamo prestamo = prestamoService.registrarDevolucion(idPrestamo);
        ConsolaUtil.mostrarExito("La operación fue registrada.");
        Vista.comprobanteDevolucion(prestamo);
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