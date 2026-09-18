package com.universidad.compras.ejecucion;

import com.universidad.compras.modelo.Solicitud;

public class GenerarOrdenCompraComando implements Comando {

    private final OrdenCompraService ordenCompraService;
    private final Solicitud solicitud;
    private final String proveedor;
    private String numeroOrden; // se conoce solo después de ejecutar

    public GenerarOrdenCompraComando(OrdenCompraService ordenCompraService, Solicitud solicitud, String proveedor) {
        this.ordenCompraService = ordenCompraService;
        this.solicitud = solicitud;
        this.proveedor = proveedor;
    }

    @Override
    public void ejecutar() {
        numeroOrden = ordenCompraService.generar(solicitud.getId(), proveedor);
    }

    @Override
    public void deshacer() {
        if (numeroOrden == null) {
            throw new IllegalStateException("La orden de compra aún no fue generada");
        }
        ordenCompraService.cancelar(numeroOrden);
    }

    @Override
    public String descripcion() {
        return "Generar orden de compra para " + proveedor
                + (numeroOrden != null ? " (" + numeroOrden + ")" : "");
    }

    public String getNumeroOrden() {
        return numeroOrden;
    }
}