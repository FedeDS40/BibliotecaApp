package com.IngSoftware.BibliotecaApp.excepciones;

// Se usa cuando una operación no cumple una regla de negocio.
// Ejemplo: prestar un libro a un socio inactivo (RN-01).
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}