package com.universidad.compras.estado;

// Estado final: no acepta ninguna operación (todas se rechazan por defecto).
public class EstadoCancelada implements EstadoSolicitud {

    @Override
    public String nombre() {
        return "CANCELADA";
    }
}