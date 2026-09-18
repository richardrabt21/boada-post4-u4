package com.universidad.compras.estado;

public record ResultadoTransicion(boolean exitosa, String mensaje) {

    public static ResultadoTransicion exito(String mensaje) {
        return new ResultadoTransicion(true, mensaje);
    }

    public static ResultadoTransicion error(String mensaje) {
        return new ResultadoTransicion(false, mensaje);
    }
}