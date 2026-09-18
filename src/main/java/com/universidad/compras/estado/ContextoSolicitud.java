package com.universidad.compras.estado;

import com.universidad.compras.modelo.Solicitud;
import com.universidad.compras.notificacion.PublicadorCambiosEstado;

import java.util.List;

// La solicitud "con comportamiento": cada operación delega en el objeto que representa
// su estado actual. No hay if/else sobre getEstado() en ningún lugar.
public class ContextoSolicitud {

    private final Solicitud solicitud;
    private final PublicadorCambiosEstado publicador;

    public ContextoSolicitud(Solicitud solicitud) {
        this(solicitud, new PublicadorCambiosEstado(List.of()));
    }

    public ContextoSolicitud(Solicitud solicitud, PublicadorCambiosEstado publicador) {
        this.solicitud = solicitud;
        this.publicador = publicador;
    }

    public ResultadoTransicion aprobar() {
        return estadoActual().aprobar(this);
    }

    public ResultadoTransicion rechazar() {
        return estadoActual().rechazar(this);
    }

    public ResultadoTransicion ejecutar() {
        return estadoActual().ejecutar(this);
    }

    public ResultadoTransicion cancelar() {
        return estadoActual().cancelar(this);
    }

    public String getEstado() {
        return solicitud.getEstado();
    }

    // El estado siempre se lee de la solicitud, así nunca queda desincronizado
    // si otra parte del sistema la cambió.
    private EstadoSolicitud estadoActual() {
        return RegistroEstados.desde(solicitud.getEstado());
    }

    // Lo llaman los estados; el cambio se publica para que reaccionen los suscriptores (Necesidad 3).
    void transicionarA(EstadoSolicitud nuevoEstado) {
        publicador.cambiarEstado(solicitud, nuevoEstado.nombre());
    }
}