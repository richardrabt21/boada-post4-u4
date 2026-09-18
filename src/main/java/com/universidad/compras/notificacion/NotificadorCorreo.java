package com.universidad.compras.notificacion;

import org.springframework.stereotype.Component;

@Component
public class NotificadorCorreo implements ObservadorEstado {

    @Override
    public void alCambiarEstado(EventoCambioEstado evento) {
        ClientesNotificacion.enviarCorreo(
                evento.solicitud().getSolicitanteEmail(),
                "Solicitud " + evento.solicitud().getId() + ": " + evento.estadoNuevo(),
                "Su solicitud pasó de " + evento.estadoAnterior() + " a " + evento.estadoNuevo() + ".");
    }
}