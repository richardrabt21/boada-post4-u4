package com.universidad.compras.estado;

public class EstadoPendiente extends EstadoEnTramite {

    @Override
    public String nombre() {
        return "PENDIENTE";
    }
}