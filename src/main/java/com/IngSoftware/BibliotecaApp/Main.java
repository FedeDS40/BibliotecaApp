package com.IngSoftware.BibliotecaApp;

import com.IngSoftware.BibliotecaApp.excepciones.DatosInvalidosException;
import com.IngSoftware.BibliotecaApp.excepciones.ReglaNegocioException;
import com.IngSoftware.BibliotecaApp.modelo.Libro;
import com.IngSoftware.BibliotecaApp.negocio.LibroService;

public class Main {

    static LibroService service = new LibroService();

    public static void main(String[] args) {
        probar("1. Registrar libro con todos los datos (debe funcionar)", () ->
                service.registrar("Libro de prueba", "Autor Prueba", "Editorial X", "2020"));

        probar("2. Registrar sin editorial ni año (debe funcionar)", () ->
                service.registrar("Libro de prueba", "Autor Prueba", "", ""));

        probar("3. Registrar sin título (debe fallar)", () ->
                service.registrar("  ", "Autor Prueba", "", ""));

        probar("4. Registrar con año con letras (debe fallar)", () ->
                service.registrar("Libro de prueba", "Autor Prueba", "", "20ab"));

        probar("5. Buscar con texto vacío (debe fallar)", () ->
                service.buscar("", true));

        probar("6. Baja de Rayuela (id 1), que está prestado (debe fallar)", () ->
                service.darDeBaja(1));

        probar("7. Baja de Don Quijote (id 9), ya inactivo (debe fallar)", () ->
                service.darDeBaja(9));

        probar("8. Baja de un libro nuevo, sin préstamos (debe funcionar)", () -> {
            Libro nuevo = service.registrar("Libro de prueba", "Autor Prueba", "", "");
            service.darDeBaja(nuevo.getId());
        });

        probar("9. Modificar un libro que no existe (debe fallar)", () ->
                service.modificar(999, "Titulo", "Autor", "", ""));

        System.out.println("10. Disponibilidad:");
        System.out.println("   Rayuela (id 1), prestado -> disponible: "
                + service.estaDisponible(service.obtenerPorId(1)) + " (esperado: false)");
        System.out.println("   Cien años (id 5), libre  -> disponible: "
                + service.estaDisponible(service.obtenerPorId(5)) + " (esperado: true)");
        System.out.println("   Don Quijote (id 9), baja -> disponible: "
                + service.estaDisponible(service.obtenerPorId(9)) + " (esperado: false)");
    }

    // Ejecuta una prueba e informa el resultado sin que el programa se cierre
    static void probar(String titulo, Runnable accion) {
        System.out.println(titulo);
        try {
            accion.run();
            System.out.println("   -> OK, la operación se realizó.");
        } catch (ReglaNegocioException e) {
            System.out.println("   -> Regla de negocio: " + e.getMessage());
        } catch (DatosInvalidosException e) {
            System.out.println("   -> Datos inválidos: " + e.getMessage());
        }
    }
}