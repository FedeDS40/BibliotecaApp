package com.IngSoftware.BibliotecaApp.negocio;

import com.IngSoftware.BibliotecaApp.datos.PrestamoDAO;
import com.IngSoftware.BibliotecaApp.datos.SocioDAO;
import com.IngSoftware.BibliotecaApp.excepciones.DatosInvalidosException;
import com.IngSoftware.BibliotecaApp.excepciones.ReglaNegocioException;
import com.IngSoftware.BibliotecaApp.modelo.Socio;

import java.util.List;

public class SocioService {

    private final SocioDAO socioDAO = new SocioDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    // RF-01: registra un socio validando los datos y evitando duplicados
    public Socio registrar(String numeroSocio, String dni, String nombre, String apellido) {
        numeroSocio = limpiar(numeroSocio).toUpperCase();
        dni = limpiar(dni);
        nombre = limpiar(nombre);
        apellido = limpiar(apellido);

        validarDatos(numeroSocio, dni, nombre, apellido);

        // RN-11: el DNI y el número de socio no pueden repetirse
        if (socioDAO.existeNumeroSocio(numeroSocio)) {
            throw new ReglaNegocioException("Ya existe un socio con el número " + numeroSocio + ".");
        }
        if (socioDAO.existeDni(dni)) {
            throw new ReglaNegocioException("Ya existe un socio con el DNI " + dni + ".");
        }

        Socio socio = new Socio(numeroSocio, dni, nombre, apellido); // queda activo
        socioDAO.insertar(socio);
        return socio;
    }

    // RF-02: modifica DNI, nombre y apellido (el número de socio no se cambia)
    public Socio modificar(String numeroSocio, String dni, String nombre, String apellido) {
        Socio socio = obtenerPorNumero(numeroSocio);
        dni = limpiar(dni);
        nombre = limpiar(nombre);
        apellido = limpiar(apellido);

        validarDatos(socio.getNumeroSocio(), dni, nombre, apellido);

        if (!dni.equals(socio.getDni()) && socioDAO.existeDni(dni)) {
            throw new ReglaNegocioException("Ya existe un socio con el DNI " + dni + ".");
        }

        socio.setDni(dni);
        socio.setNombre(nombre);
        socio.setApellido(apellido);
        socioDAO.actualizar(socio);
        return socio;
    }

    // RF-02: devuelve el socio o avisa si no existe
    public Socio obtenerPorNumero(String numeroSocio) {
        numeroSocio = limpiar(numeroSocio).toUpperCase();
        if (numeroSocio.isEmpty()) {
            throw new DatosInvalidosException("Debe ingresar el número de socio.");
        }
        Socio socio = socioDAO.buscarPorNumero(numeroSocio);
        if (socio == null) {
            throw new ReglaNegocioException("No existe un socio con el número " + numeroSocio + ".");
        }
        return socio;
    }

    public List<Socio> listarTodos() {
        return socioDAO.listarTodos();
    }

    // RF-02, RN-09 y RN-10: baja lógica, solo si no tiene préstamos sin devolver
    public Socio darDeBaja(String numeroSocio) {
        Socio socio = obtenerPorNumero(numeroSocio);

        if (!socio.estaActivo()) {
            throw new ReglaNegocioException("El socio " + socio.getNumeroSocio()
                    + " ya está dado de baja.");
        }

        int pendientes = prestamoDAO.contarSinDevolver(socio.getId());
        if (pendientes > 0) {
            throw new ReglaNegocioException("No se puede dar de baja al socio: tiene "
                    + pendientes + " préstamo(s) sin devolver.");
        }

        socio.darDeBaja();
        socioDAO.actualizar(socio);
        return socio;
    }

    // Valida que los datos obligatorios estén completos y tengan formato correcto
    private void validarDatos(String numeroSocio, String dni, String nombre, String apellido) {
        if (numeroSocio.isEmpty()) {
            throw new DatosInvalidosException("El número de socio es obligatorio.");
        }
        if (numeroSocio.length() > 20) {
            throw new DatosInvalidosException("El número de socio no puede superar los 20 caracteres.");
        }
        if (dni.isEmpty()) {
            throw new DatosInvalidosException("El DNI es obligatorio.");
        }
        if (!dni.matches("\\d{7,8}")) {
            throw new DatosInvalidosException("El DNI debe tener 7 u 8 números, sin puntos ni letras.");
        }
        if (nombre.isEmpty()) {
            throw new DatosInvalidosException("El nombre es obligatorio.");
        }
        if (apellido.isEmpty()) {
            throw new DatosInvalidosException("El apellido es obligatorio.");
        }
        if (nombre.length() > 80 || apellido.length() > 80) {
            throw new DatosInvalidosException("El nombre y el apellido no pueden superar los 80 caracteres.");
        }
    }

    private String limpiar(String texto) {
        return texto == null ? "" : texto.trim();
    }
}
