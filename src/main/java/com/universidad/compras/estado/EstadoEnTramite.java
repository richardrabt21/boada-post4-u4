package com.universidad.compras.estado;

// Comportamiento común de las solicitudes que aún no han sido decididas.
public abstract class EstadoEnTramite implements EstadoSolicitud {

    @Override
    public ResultadoTransicion aprobar(ContextoSolicitud contexto) {
        contexto.transicionarA(new EstadoAprobada());
        return ResultadoTransicion.exito("Aprobada");
    }

    @Override
    public ResultadoTransicion rechazar(ContextoSolicitud contexto) {
        contexto.transicionarA(new EstadoRechazada());
        return ResultadoTransicion.exito("Rechazada");
    }

    @Override
    public ResultadoTransicion ejecutar(ContextoSolicitud contexto) {
        return ResultadoTransicion.error("Error: debe estar aprobada antes de ejecutarse");
    }

    @Override
    public ResultadoTransicion cancelar(ContextoSolicitud contexto) {
        contexto.transicionarA(new EstadoCancelada());
        return ResultadoTransicion.exito("Cancelada");
    }
}