package com.IngSoftware.BibliotecaApp.presentacion;

import com.IngSoftware.BibliotecaApp.excepciones.AccesoDatosException;
import com.IngSoftware.BibliotecaApp.excepciones.DatosInvalidosException;
import com.IngSoftware.BibliotecaApp.excepciones.ReglaNegocioException;
import com.IngSoftware.BibliotecaApp.modelo.Socio;
import com.IngSoftware.BibliotecaApp.negocio.SocioService;

// Pantalla de inicio: permite elegir el perfil (CU-01).
// Para ingresar como socio se debe indicar un socio existente.
public class MenuPrincipal {

    private final SocioService socioService = new SocioService();

    public void mostrar() {
        boolean salir = false;
        while (!salir) {
            ConsolaUtil.titulo("SISTEMA DE GESTIÓN DE BIBLIOTECA");
            System.out.println(" 1. Ingresar como Socio");
            System.out.println(" 2. Ingresar como Bibliotecario");
            System.out.println(" 3. Ingresar como Administrador");
            System.out.println(" 0. Salir");
            int opcion = ConsolaUtil.leerEntero("Elija un perfil: ");

            switch (opcion) {
                case 1 -> ingresarComoSocio();
                case 2 -> new MenuBibliotecario().mostrar();
                case 3 -> new MenuAdministrador().mostrar();
                case 0 -> salir = true;
                default -> ConsolaUtil.mostrarError("Opción inválida. Elija un número del menú.");
            }
        }
        System.out.println("\nHasta luego.");
    }

    // El socio debe existir; si no, se informa y se vuelve al inicio
    private void ingresarComoSocio() {
        try {
            String numero = ConsolaUtil.leerTexto("Número de socio: ");
            Socio socio = socioService.obtenerPorNumero(numero);
            new MenuSocio(socio).mostrar();
        } catch (ReglaNegocioException | DatosInvalidosException e) {
            ConsolaUtil.mostrarError(e.getMessage());
        } catch (AccesoDatosException e) {
            ConsolaUtil.mostrarError(e.getMessage()
                    + ". Verifique que la base de datos esté disponible.");
        }
    }
}
