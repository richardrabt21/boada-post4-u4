package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

public class GerenteArea extends NivelAprobacion {

    private static final double LIMITE = 10_000_000;

    @Override
    protected String getNombre() {
        return "Gerente de Área";
    }

    @Override
    protected boolean estaDentroDeMiAutoridad(Solicitud solicitud) {
        return solicitud.getMonto() <= LIMITE;
    }
}