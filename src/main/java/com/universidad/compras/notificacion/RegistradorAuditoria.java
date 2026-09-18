package com.universidad.compras.notificacion;

import org.springframework.stereotype.Component;

@Component
public class RegistradorAuditoria implements ObservadorEstado {

    @Override
    public void alCambiarEstado(EventoCambioEstado evento) {
        ClientesNotificacion.registrarAuditoria(
                evento.solicitud().getId(),
                evento.estadoNuevo(),
                "Estado anterior: " + evento.estadoAnterior());
    }
}