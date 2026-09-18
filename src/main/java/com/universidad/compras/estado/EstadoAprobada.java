package com.universidad.compras.estado;

public class EstadoAprobada implements EstadoSolicitud {

    @Override
    public String nombre() {
        return "APROBADA";
    }

    @Override
    public ResultadoTransicion aprobar(ContextoSolicitud contexto) {
        return ResultadoTransicion.error("Error: la solicitud ya fue aprobada");
    }

    @Override
    public ResultadoTransicion ejecutar(ContextoSolicitud contexto) {
        contexto.transicionarA(new EstadoEjecutada());
        return ResultadoTransicion.exito("Ejecutada");
    }

    @Override
    public ResultadoTransicion cancelar(ContextoSolicitud contexto) {
        contexto.transicionarA(new EstadoCancelada());
        return ResultadoTransicion.exito("Cancelada");
    }
}