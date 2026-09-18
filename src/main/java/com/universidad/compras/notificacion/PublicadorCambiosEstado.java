package com.universidad.compras.notificacion;

import com.universidad.compras.modelo.Solicitud;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

// Único punto que cambia el estado de una solicitud y avisa a los suscriptores.
// No conoce a ningún suscriptor concreto: Spring le inyecta todos los que existan.
@Component
public class PublicadorCambiosEstado {

    private final List<ObservadorEstado> observadores;

    public PublicadorCambiosEstado(List<ObservadorEstado> observadores) {
        this.observadores = new CopyOnWriteArrayList<>(observadores);
    }

    public void suscribir(ObservadorEstado observador) {
        observadores.add(observador);
    }

    public void desuscribir(ObservadorEstado observador) {
        observadores.remove(observador);
    }

    public void cambiarEstado(Solicitud solicitud, String nuevoEstado) {
        String estadoAnterior = solicitud.getEstado();
        if (Objects.equals(estadoAnterior, nuevoEstado)) {
            return; // no hubo cambio real, no se notifica
        }
        solicitud.setEstado(nuevoEstado);

        EventoCambioEstado evento = new EventoCambioEstado(solicitud, estadoAnterior, nuevoEstado);
        for (ObservadorEstado observador : observadores) {
            try {
                observador.alCambiarEstado(evento);
            } catch (RuntimeException e) {
                // Un suscriptor que falla no debe impedir el cambio ni a los demás suscriptores.
                System.err.println("Suscriptor " + observador.getClass().getSimpleName()
                        + " falló: " + e.getMessage());
            }
        }
    }
}