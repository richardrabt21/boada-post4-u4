package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

public class ServicioAprobacionEnCadena implements ServicioAprobacion {

    private final NivelAprobacion primerNivel;

    public ServicioAprobacionEnCadena(NivelAprobacion primerNivel) {
        this.primerNivel = primerNivel;
    }

    @Override
    public ResultadoAprobacion evaluar(Solicitud solicitud) {
        return primerNivel.evaluar(solicitud);
    }
}