package com.universidad.compras.estado;

public class EstadoEjecutada implements EstadoSolicitud {

    @Override
    public String nombre() {
        return "EJECUTADA";
    }

    @Override
    public ResultadoTransicion ejecutar(ContextoSolicitud contexto) {
        return ResultadoTransicion.error("Error: ya fue ejecutada");
    }
}