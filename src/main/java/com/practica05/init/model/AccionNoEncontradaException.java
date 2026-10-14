package com.practica05.init.model;

public class AccionNoEncontradaException extends RuntimeException {

    public AccionNoEncontradaException(String simbolo) {
        super("No se han encontrado datos de precio para el símbolo de acción '" + simbolo + "'");
    }

}
