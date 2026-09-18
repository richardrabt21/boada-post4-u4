package com.universidad.compras.notificacion;

@FunctionalInterface
public interface ObservadorEstado {
    void alCambiarEstado(EventoCambioEstado evento);
}