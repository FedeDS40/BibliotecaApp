package com.IngSoftware.BibliotecaApp.excepciones;

// Se usa cuando faltan datos obligatorios o tienen un formato incorrecto.
// Ejemplo: registrar un socio sin nombre o con un DNI que no es numérico.
public class DatosInvalidosException extends RuntimeException {

    public DatosInvalidosException(String mensaje) {
        super(mensaje);
    }
}