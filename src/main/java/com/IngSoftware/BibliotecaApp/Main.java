package com.IngSoftware.BibliotecaApp;

import com.IngSoftware.BibliotecaApp.excepciones.DatosInvalidosException;
import com.IngSoftware.BibliotecaApp.excepciones.ReglaNegocioException;
import com.IngSoftware.BibliotecaApp.negocio.SocioService;

public class Main {

    static SocioService service = new SocioService();

    public static void main(String[] args) {
        probar("1. Registrar socio nuevo (debe funcionar)", () ->
                service.registrar("S-006", "35666777", "Pedro", "López"));

        probar("2. Registrar el mismo número de socio (debe fallar)", () ->
                service.registrar("S-006", "35999888", "Ana", "Torres"));

        probar("3. Registrar con un DNI que ya existe (debe fallar)", () ->
                service.registrar("S-007", "30111222", "Ana", "Torres"));

        probar("4. Registrar con DNI con letras (debe fallar)", () ->
                service.registrar("S-007", "abc", "Ana", "Torres"));

        probar("5. Registrar sin nombre (debe fallar)", () ->
                service.registrar("S-007", "36111222", "  ", "Torres"));

        probar("6. Baja de Martín Gómez, con 3 préstamos (debe fallar)", () ->
                service.darDeBaja("S-002"));

        probar("7. Baja de Julián Pérez, ya inactivo (debe fallar)", () ->
                service.darDeBaja("S-004"));

        probar("8. Baja del socio nuevo S-006, sin préstamos (debe funcionar)", () ->
                service.darDeBaja("S-006"));

        probar("9. Buscar un socio que no existe (debe fallar)", () ->
                service.obtenerPorNumero("S-999"));
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