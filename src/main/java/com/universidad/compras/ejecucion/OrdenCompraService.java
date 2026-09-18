package com.universidad.compras.ejecucion;

// Integración con el proveedor de órdenes de compra. No modificar.
public class OrdenCompraService {

    public String generar(String solicitudId, String proveedor) {
        String numeroOrden = "OC-" + solicitudId;
        System.out.println("Orden de compra " + numeroOrden + " generada para proveedor " + proveedor);
        return numeroOrden;
    }

    public void cancelar(String numeroOrden) {
        System.out.println("Orden de compra " + numeroOrden + " cancelada");
    }
}