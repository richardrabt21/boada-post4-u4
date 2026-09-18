package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

// Contrato ya usado por ControladorSolicitudes y por el módulo de reportes
// financieros (no incluido en este laboratorio). No modificar.
public interface ServicioAprobacion {
    ResultadoAprobacion evaluar(Solicitud solicitud);
}