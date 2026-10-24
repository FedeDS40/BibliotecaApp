package com.IngSoftware.BibliotecaApp.presentacion;

import java.util.Scanner;

// Herramientas para leer datos de la consola y mostrar mensajes.
// Es la única clase que lee del teclado (capa de presentación).

public class ConsolaUtil {

    private static final Scanner scanner = new Scanner(System.in);

    // Muestra un mensaje y devuelve lo que escribió el usuario, sin espacios sobrantes
    public static String leerTexto(String mensaje) {
        System.out.print(mensaje);
        if (!scanner.hasNextLine()) {
            System.out.println();
            System.out.println("Entrada cerrada. Fin del programa.");
            System.exit(0);
        }
        return scanner.nextLine().trim();
    }

    // Repite la pregunta hasta que el usuario escriba un número entero
    public static int leerEntero(String mensaje) {
        while (true) {
            String texto = leerTexto(mensaje);
            try {
                return Integer.parseInt(texto);
            } catch (NumberFormatException e) {
                mostrarError("Debe ingresar un número entero.");
            }
        }
    }

    public static void titulo(String texto) {
        System.out.println();
        System.out.println("=== " + texto + " ===");
    }

    public static void mostrarError(String mensaje) {
        System.out.println("\n[ERROR] " + mensaje);
    }

    public static void mostrarExito(String mensaje) {
        System.out.println("\n[OK] " + mensaje);
    }

    public static void pausa() {
        leerTexto("\nPresione Enter para continuar...");
    }
}