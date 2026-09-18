package com.universidad.compras.estado;

// Contrato de todo estado. Por defecto cada operación se rechaza;
// cada estado sobrescribe únicamente las operaciones que permite.
public interface EstadoSolicitud {

    String nombre();

    default ResultadoTransicion aprobar(ContextoSolicitud contexto) {
        return noPermitida("aprobar");
    }

    default ResultadoTransicion rechazar(ContextoSolicitud contexto) {
        return noPermitida("rechazar");
    }

    default ResultadoTransicion ejecutar(ContextoSolicitud contexto) {
        return noPermitida("ejecutar");
    }

    default ResultadoTransicion cancelar(ContextoSolicitud contexto) {
        return noPermitida("cancelar");
    }

    private ResultadoTransicion noPermitida(String operacion) {
        return ResultadoTransicion.error("Error: no se puede " + operacion + " una solicitud " + nombre());
    }
}