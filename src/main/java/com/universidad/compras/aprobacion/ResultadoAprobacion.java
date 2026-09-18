package com.universidad.compras.aprobacion;

public class ResultadoAprobacion {

    private final boolean aprobada;
    private final String nivelResolutor;
    private final String detalle;

    public ResultadoAprobacion(boolean aprobada, String nivelResolutor, String detalle) {
        this.aprobada = aprobada;
        this.nivelResolutor = nivelResolutor;
        this.detalle = detalle;
    }

    public boolean isAprobada() { return aprobada; }
    public String getNivelResolutor() { return nivelResolutor; }
    public String getDetalle() { return detalle; }
}