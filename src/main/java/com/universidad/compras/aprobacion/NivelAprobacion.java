package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;

public abstract class NivelAprobacion {

    private NivelAprobacion siguiente;

    // Devuelve el siguiente para poder encadenar: a.setSiguiente(b).setSiguiente(c)
    public NivelAprobacion setSiguiente(NivelAprobacion siguiente) {
        this.siguiente = siguiente;
        return siguiente;
    }

    public final ResultadoAprobacion evaluar(Solicitud solicitud) {
        if (estaDentroDeMiAutoridad(solicitud)) {
            return resolver(solicitud);
        }
        if (siguiente != null) {
            return siguiente.evaluar(solicitud);
        }
        return new ResultadoAprobacion(false, null,
                "Ningún nivel pudo resolver la solicitud " + solicitud.getId());
    }

    protected abstract String getNombre();

    protected abstract boolean estaDentroDeMiAutoridad(Solicitud solicitud);

    // Punto de extensión: por defecto un nivel aprueba lo que cae en su autoridad.
    protected boolean apruebaLaSolicitud(Solicitud solicitud) {
        return true;
    }

    // El nivel decide y registra quién resolvió. El cambio de estado NO se hace aquí:
    // lo realiza ServicioAprobacionEnCadena a través del publicador de cambios de estado.
    private ResultadoAprobacion resolver(Solicitud solicitud) {
        boolean aprobada = apruebaLaSolicitud(solicitud);
        solicitud.setNivelResolutor(getNombre());
        String detalle = (aprobada ? "Aprobada por " : "Rechazada por ") + getNombre();
        return new ResultadoAprobacion(aprobada, getNombre(), detalle);
    }
}