package com.IngSoftware.BibliotecaApp;

import com.IngSoftware.BibliotecaApp.datos.SocioDAO;
import com.IngSoftware.BibliotecaApp.modelo.Socio;

public class Main {
    public static void main(String[] args) {
        SocioDAO dao = new SocioDAO();
        for (Socio s : dao.listarTodos()) {
            System.out.println(s.getNumeroSocio() + " - " + s.getNombreCompleto()
                    + " - DNI " + s.getDni()
                    + (s.estaActivo() ? "" : " (INACTIVO)"));
        }
    }
}