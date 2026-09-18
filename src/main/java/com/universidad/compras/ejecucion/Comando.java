package com.universidad.compras.ejecucion;

public interface Comando {
    void ejecutar();
    void deshacer();
    String descripcion();
}