package com.universidad.compras.estado;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

// Único lugar donde se registran los estados existentes.
// Agregar un estado nuevo = crear su clase y añadirla a esta lista.
final class RegistroEstados {

    private static final Map<String, EstadoSolicitud> ESTADOS = Stream.<EstadoSolicitud>of(
                    new EstadoPendiente(),
                    new EstadoEnAprobacion(),
                    new EstadoAprobada(),
                    new EstadoRechazada(),
                    new EstadoEjecutada(),
                    new EstadoCancelada())
            .collect(Collectors.toUnmodifiableMap(EstadoSolicitud::nombre, Function.identity()));

    private RegistroEstados() {
    }

    static EstadoSolicitud desde(String nombre) {
        EstadoSolicitud estado = ESTADOS.get(nombre);
        if (estado == null) {
            throw new IllegalArgumentException("Estado desconocido: " + nombre);
        }
        return estado;
    }
}