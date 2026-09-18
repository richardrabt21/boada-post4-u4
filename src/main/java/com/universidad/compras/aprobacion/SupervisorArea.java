package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

public class SupervisorArea extends NivelAprobacion {

    private static final double LIMITE = 2_000_000;

    @Override
    protected String getNombre() {
        return "Supervisor de Área";
    }

    @Override
    protected boolean estaDentroDeMiAutoridad(Solicitud solicitud) {
        return solicitud.getMonto() <= LIMITE;
    }
}