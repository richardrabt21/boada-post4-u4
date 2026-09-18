package com.universidad.compras.notificacion;

import org.springframework.stereotype.Component;

@Component
public class ActualizadorDashboardContabilidad implements ObservadorEstado {

    @Override
    public void alCambiarEstado(EventoCambioEstado evento) {
        ClientesNotificacion.actualizarDashboardContabilidad(
                evento.solicitud().getId(),
                evento.estadoNuevo(),
                evento.solicitud().getMonto());
    }
}