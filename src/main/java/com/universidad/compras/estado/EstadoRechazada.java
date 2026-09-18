package com.universidad.compras.estado;

// Estado final: no acepta ninguna operación (todas se rechazan por defecto).
public class EstadoRechazada implements EstadoSolicitud {

    @Override
    public String nombre() {
        return "RECHAZADA";
    }
}