package com.universidad.compras.ejecucion;

import com.universidad.compras.modelo.Solicitud;

public class ReservarPresupuestoComando implements Comando {

    private final PresupuestoService presupuestoService;
    private final Solicitud solicitud;

    public ReservarPresupuestoComando(PresupuestoService presupuestoService, Solicitud solicitud) {
        this.presupuestoService = presupuestoService;
        this.solicitud = solicitud;
    }

    @Override
    public void ejecutar() {
        boolean reservado = presupuestoService.reservar(solicitud.getCentroCosto(), solicitud.getMonto());
        if (!reservado) {
            throw new IllegalStateException(
                    "No se pudo reservar presupuesto en el centro de costo " + solicitud.getCentroCosto());
        }
    }

    @Override
    public void deshacer() {
        presupuestoService.liberar(solicitud.getCentroCosto(), solicitud.getMonto());
    }

    @Override
    public String descripcion() {
        return "Reservar presupuesto de $" + solicitud.getMonto() + " en " + solicitud.getCentroCosto();
    }
}