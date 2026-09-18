package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;
import com.universidad.compras.notificacion.PublicadorCambiosEstado;

public class ServicioAprobacionEnCadena implements ServicioAprobacion {

    private final NivelAprobacion primerNivel;
    private final PublicadorCambiosEstado publicador;

    public ServicioAprobacionEnCadena(NivelAprobacion primerNivel, PublicadorCambiosEstado publicador) {
        this.primerNivel = primerNivel;
        this.publicador = publicador;
    }

    @Override
    public ResultadoAprobacion evaluar(Solicitud solicitud) {
        ResultadoAprobacion resultado = primerNivel.evaluar(solicitud);
        if (resultado.getNivelResolutor() != null) {
            publicador.cambiarEstado(solicitud, resultado.isAprobada() ? "APROBADA" : "RECHAZADA");
        }
        return resultado;
    }
}