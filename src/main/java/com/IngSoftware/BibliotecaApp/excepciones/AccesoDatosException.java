package com.IngSoftware.BibliotecaApp.excepciones;

// Se usa cuando falla algo al hablar con la base de datos.
// Así el menú puede mostrar un mensaje en vez de cerrarse con un error.
public class AccesoDatosException extends RuntimeException {

    public AccesoDatosException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}