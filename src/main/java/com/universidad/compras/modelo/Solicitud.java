package com.universidad.compras.modelo;

// Entidad compartida por las Necesidades 1 y 2 — no forma parte de la
// decisión de diseño de esta unidad, se provee tal cual.
public class Solicitud {

    private final String id;
    private final String solicitanteEmail;
    private final double monto;
    private final String categoria;      // ej. "MATERIAL_OFICINA", "SOFTWARE", "INTERNACIONAL"
    private final String centroCosto;
    private String estado;               // PENDIENTE, EN_APROBACION, APROBADA, RECHAZADA, EJECUTADA, CANCELADA
    private String nivelResolutor;       // quién tomó la última decisión

    public Solicitud(String id, String solicitanteEmail, double monto, String categoria, String centroCosto) {
        this.id = id;
        this.solicitanteEmail = solicitanteEmail;
        this.monto = monto;
        this.categoria = categoria;
        this.centroCosto = centroCosto;
        this.estado = "PENDIENTE";
    }

    public String getId() { return id; }
    public String getSolicitanteEmail() { return solicitanteEmail; }
    public double getMonto() { return monto; }
    public String getCategoria() { return categoria; }
    public String getCentroCosto() { return centroCosto; }
    public String getEstado() { return estado; }
    public String getNivelResolutor() { return nivelResolutor; }

    public void setEstado(String estado) { this.estado = estado; }
    public void setNivelResolutor(String nivel) { this.nivelResolutor = nivel; }

    @Override
    public String toString() {
        return "Solicitud{id='" + id + "', estado='" + estado + "', monto=" + monto + "}";
    }
}