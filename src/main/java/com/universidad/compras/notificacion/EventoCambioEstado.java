package com.universidad.compras.notificacion;

import com.universidad.compras.modelo.Solicitud;

public record EventoCambioEstado(Solicitud solicitud, String estadoAnterior, String estadoNuevo) {
}