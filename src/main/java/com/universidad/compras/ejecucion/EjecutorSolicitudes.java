package com.universidad.compras.ejecucion;

import com.universidad.compras.modelo.Solicitud;
import com.universidad.compras.notificacion.PublicadorCambiosEstado;

import java.util.List;

public class EjecutorSolicitudes {

    private final PresupuestoService presupuestoService;
    private final OrdenCompraService ordenCompraService;
    private final PublicadorCambiosEstado publicador;
    private final InvocadorOperaciones invocador = new InvocadorOperaciones();

    public EjecutorSolicitudes(PresupuestoService presupuestoService,
                               OrdenCompraService ordenCompraService,
                               PublicadorCambiosEstado publicador) {
        this.presupuestoService = presupuestoService;
        this.ordenCompraService = ordenCompraService;
        this.publicador = publicador;
    }

    public Comando reservarPresupuesto(Solicitud solicitud) {
        Comando comando = new ReservarPresupuestoComando(presupuestoService, solicitud);
        invocador.ejecutar(solicitud, comando);
        return comando;
    }

    public Comando generarOrdenCompra(Solicitud solicitud, String proveedor) {
        Comando comando = new GenerarOrdenCompraComando(ordenCompraService, solicitud, proveedor);
        invocador.ejecutar(solicitud, comando);
        return comando;
    }

    // Ejecuta las dos operaciones y, si ambas salen bien, deja la solicitud EJECUTADA.
    public void ejecutar(Solicitud solicitud, String proveedor) {
        reservarPresupuesto(solicitud);
        generarOrdenCompra(solicitud, proveedor);
        publicador.cambiarEstado(solicitud, "EJECUTADA");
    }

    public Comando deshacerUltimaOperacion(Solicitud solicitud) {
        Comando comando = invocador.deshacerUltimo(solicitud);
        reabrirSiEstabaEjecutada(solicitud);
        return comando;
    }

    public void deshacerOperacion(Solicitud solicitud, Comando comando) {
        invocador.deshacer(solicitud, comando);
        reabrirSiEstabaEjecutada(solicitud);
    }

    public List<Comando> historial(Solicitud solicitud) {
        return invocador.historial(solicitud);
    }

    // Si se deshace algo, la solicitud ya no está completamente ejecutada.
    private void reabrirSiEstabaEjecutada(Solicitud solicitud) {
        if ("EJECUTADA".equals(solicitud.getEstado())) {
            publicador.cambiarEstado(solicitud, "APROBADA");
        }
    }
}