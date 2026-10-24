package com.IngSoftware.BibliotecaApp;

import com.IngSoftware.BibliotecaApp.modelo.Socio;
import com.IngSoftware.BibliotecaApp.negocio.SocioService;
import com.IngSoftware.BibliotecaApp.presentacion.MenuSocio;

public class Main {
    public static void main(String[] args) {
        Socio socio = new SocioService().obtenerPorNumero("S-002");
        new MenuSocio(socio).mostrar();
    }
}