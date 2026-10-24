package com.IngSoftware.BibliotecaApp;

import com.IngSoftware.BibliotecaApp.excepciones.DatosInvalidosException;
import com.IngSoftware.BibliotecaApp.excepciones.ReglaNegocioException;
import com.IngSoftware.BibliotecaApp.modelo.Prestamo;
import com.IngSoftware.BibliotecaApp.negocio.PrestamoService;

public class Main {

    static PrestamoService service = new PrestamoService();
    static Prestamo ultimo; // guarda el último préstamo creado por una prueba

    public static void main(String[] args) {
        probar("1. Socio inactivo S-004 pide el libro 5 (debe fallar, RN-01)", () ->
                service.registrarPrestamo("S-004", 5));

        probar("2. Socio S-003 con préstamo vencido pide el libro 5 (debe fallar, RN-06)", () ->
                service.registrarPrestamo("S-003", 5));

        probar("3. Socio S-002 con 3 préstamos pide el libro 5 (debe fallar, RN-03)", () ->
                service.registrarPrestamo("S-002", 5));

        probar("4. Socio S-001 pide Rayuela, que está prestado (debe fallar, RN-02)", () ->
                service.registrarPrestamo("S-001", 1));

        probar("5. Socio S-001 pide Don Quijote, inactivo (debe fallar, RN-02)", () ->
                service.registrarPrestamo("S-001", 9));

        probar("6. Socio S-001 pide un libro que no existe (debe fallar)", () ->
                service.registrarPrestamo("S-001", 999));

        probar("7. Socio que no existe pide el libro 5 (debe fallar)", () ->
                service.registrarPrestamo("S-999", 5));

        probar("8. Socio S-001 pide Cien años, id 5 (debe funcionar)", () -> {
            ultimo = service.registrarPrestamo("S-001", 5);
            System.out.println("   Préstamo #" + ultimo.getId()
                    + " - vence el " + ultimo.getFechaVencimiento() + " (7 días)");
        });

        probar("9. Socio S-005 pide Cien años otra vez, ya prestado (debe fallar)", () ->
                service.registrarPrestamo("S-005", 5));

        probar("10. Devolver un préstamo que no existe (debe fallar)", () ->
                service.registrarDevolucion(999));

        probar("11. Devolver el préstamo recién creado (debe funcionar)", () -> {
            Prestamo p = service.registrarDevolucion(ultimo.getId());
            System.out.println("   Devuelto el " + p.getFechaDevolucion());
        });

        probar("12. Devolver el mismo préstamo otra vez (debe fallar, RN-08)", () ->
                service.registrarDevolucion(ultimo.getId()));

        System.out.println("13. Listados:");
        System.out.println("   Vigentes: " + service.listarVigentes().size() + " (esperado: 3)");
        System.out.println("   Vencidos: " + service.listarVencidos().size() + " (esperado: 1)");
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