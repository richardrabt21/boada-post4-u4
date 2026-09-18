package com.universidad.compras.notificacion;

// Clientes de bajo nivel ya probados — usarlos, no reimplementar su lógica.
// No modificar esta clase.
public final class ClientesNotificacion {

    private ClientesNotificacion() {}

    public static void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        System.out.println("[EMAIL -> " + destinatario + "] " + asunto + ": " + cuerpo);
    }

    public static void actualizarDashboardContabilidad(String solicitudId, String estado, double monto) {
        System.out.println("[DASHBOARD] Solicitud " + solicitudId + " -> " + estado + " ($" + monto + ")");
    }

    public static void registrarAuditoria(String solicitudId, String estado, String detalle) {
        System.out.println("[AUDITORIA] " + solicitudId + " -> " + estado + " (" + detalle + ")");
    }
}