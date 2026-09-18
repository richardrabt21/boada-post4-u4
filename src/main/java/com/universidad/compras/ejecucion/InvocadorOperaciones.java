package com.universidad.compras.ejecucion;

import com.universidad.compras.modelo.Solicitud;

import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvocadorOperaciones {

    // Un historial por solicitud; el último elemento es la operación más reciente.
    private final Map<String, Deque<Comando>> historialPorSolicitud = new HashMap<>();

    // synchronized: en Spring este objeto es un singleton compartido entre peticiones.
    public synchronized void ejecutar(Solicitud solicitud, Comando comando) {
        comando.ejecutar(); // si falla, lanza excepción y no queda en el historial
        historialPorSolicitud
                .computeIfAbsent(solicitud.getId(), id -> new ArrayDeque<>())
                .addLast(comando);
    }

    public synchronized Comando deshacerUltimo(Solicitud solicitud) {
        Deque<Comando> historial = historialPorSolicitud.get(solicitud.getId());
        if (historial == null || historial.isEmpty()) {
            throw new IllegalStateException(
                    "No hay operaciones para deshacer en la solicitud " + solicitud.getId());
        }
        Comando comando = historial.peekLast();
        comando.deshacer();
        historial.removeLast();
        return comando;
    }

    // Deshace una operación concreta sin tocar las demás (deshacer "de forma independiente").
    public synchronized void deshacer(Solicitud solicitud, Comando comando) {
        Deque<Comando> historial = historialPorSolicitud.get(solicitud.getId());
        if (historial == null || !historial.contains(comando)) {
            throw new IllegalArgumentException(
                    "La operación no está en el historial de la solicitud " + solicitud.getId());
        }
        comando.deshacer();
        historial.remove(comando);
    }

    // Copia de solo lectura, en orden cronológico.
    public synchronized List<Comando> historial(Solicitud solicitud) {
        Deque<Comando> historial = historialPorSolicitud.get(solicitud.getId());
        if (historial == null) {
            return List.of();
        }
        return Collections.unmodifiableList(List.copyOf(historial));
    }
}