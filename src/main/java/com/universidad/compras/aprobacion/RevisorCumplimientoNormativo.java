package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

public class RevisorCumplimientoNormativo extends NivelAprobacion {

    @Override
    protected String getNombre() {
        return "Revisor de Cumplimiento Normativo";
    }

    @Override
    protected boolean estaDentroDeMiAutoridad(Solicitud solicitud) {
        return "INTERNACIONAL".equals(solicitud.getCategoria());
    }
}