package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

public class DirectorFinanciero extends NivelAprobacion {

    @Override
    protected String getNombre() {
        return "Director Financiero";
    }

    @Override
    protected boolean estaDentroDeMiAutoridad(Solicitud solicitud) {
        return true; // sin límite superior
    }
}