package com.universidad.compras.estado;

public class EstadoEnAprobacion extends EstadoEnTramite {

    @Override
    public String nombre() {
        return "EN_APROBACION";
    }
}