package com.universidad.compras.ejecucion;

// Módulo financiero — ya usado por otros procesos de ComprasUDES. No modificar.
public class PresupuestoService {

    public boolean reservar(String centroCosto, double monto) {
        System.out.println("Reservando $" + monto + " del centro de costo " + centroCosto);
        return true;
    }

    public void liberar(String centroCosto, double monto) {
        System.out.println("Liberando $" + monto + " del centro de costo " + centroCosto);
    }
}