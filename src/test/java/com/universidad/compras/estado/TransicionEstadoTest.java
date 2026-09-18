package com.universidad.compras.estado;

import com.universidad.compras.modelo.Solicitud;
import com.universidad.compras.notificacion.EventoCambioEstado;
import com.universidad.compras.notificacion.PublicadorCambiosEstado;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransicionEstadoTest {

    private Solicitud solicitudEn(String estado) {
        Solicitud s = new Solicitud("S-040", "ana@udes.edu.co", 1000000, "SOFTWARE", "CC-100");
        s.setEstado(estado);
        return s;
    }

    @Test
    void ejecutarUnaSolicitudAprobadaLaDejaEjecutada() {
        Solicitud s = new Solicitud("S-030", "luis@udes.edu.co", 3000000, "MATERIAL_OFICINA", "CC-200");
        s.setEstado("APROBADA");
        ContextoSolicitud contexto = new ContextoSolicitud(s);

        ResultadoTransicion r = contexto.ejecutar();

        assertTrue(r.exitosa());
        assertEquals("EJECUTADA", s.getEstado());
    }

    @Test
    void ejecutarUnaSolicitudPendienteSeRechazaSinCambiarElEstado() {
        Solicitud s = new Solicitud("S-031", "ana@udes.edu.co", 1000000, "SOFTWARE", "CC-100");
        ContextoSolicitud contexto = new ContextoSolicitud(s);

        ResultadoTransicion r = contexto.ejecutar();

        assertFalse(r.exitosa());
        assertEquals("PENDIENTE", s.getEstado());
    }

    @Test
    void unaSolicitudEjecutadaNoPuedeVolverAEjecutarse() {
        Solicitud s = new Solicitud("S-032", "ana@udes.edu.co", 1000000, "SOFTWARE", "CC-100");
        s.setEstado("EJECUTADA");
        ContextoSolicitud contexto = new ContextoSolicitud(s);

        ResultadoTransicion r = contexto.ejecutar();

        assertFalse(r.exitosa());
        assertEquals("Error: ya fue ejecutada", r.mensaje());
        assertEquals("EJECUTADA", s.getEstado());
    }

    @Test
    void desdeUnEstadoEnTramiteSePuedeAprobarRechazarOCancelar() {
        Solicitud pendiente = solicitudEn("PENDIENTE");
        new ContextoSolicitud(pendiente).aprobar();
        assertEquals("APROBADA", pendiente.getEstado());

        Solicitud enAprobacion = solicitudEn("EN_APROBACION");
        new ContextoSolicitud(enAprobacion).rechazar();
        assertEquals("RECHAZADA", enAprobacion.getEstado());

        Solicitud otra = solicitudEn("PENDIENTE");
        new ContextoSolicitud(otra).cancelar();
        assertEquals("CANCELADA", otra.getEstado());
    }

    @Test
    void unEstadoFinalRechazaTodasLasOperacionesSinCambiarse() {
        for (String estadoFinal : List.of("RECHAZADA", "CANCELADA")) {
            Solicitud s = solicitudEn(estadoFinal);
            ContextoSolicitud contexto = new ContextoSolicitud(s);

            assertFalse(contexto.aprobar().exitosa());
            assertFalse(contexto.rechazar().exitosa());
            assertFalse(contexto.ejecutar().exitosa());
            assertFalse(contexto.cancelar().exitosa());
            assertEquals(estadoFinal, s.getEstado());
        }
    }

    @Test
    void elFlujoCompletoRespetaLasReglasEnCadaPaso() {
        Solicitud s = solicitudEn("PENDIENTE");
        ContextoSolicitud contexto = new ContextoSolicitud(s);

        assertFalse(contexto.ejecutar().exitosa());   // aún no está aprobada
        assertTrue(contexto.aprobar().exitosa());
        assertFalse(contexto.aprobar().exitosa());    // ya fue aprobada
        assertTrue(contexto.ejecutar().exitosa());
        assertFalse(contexto.cancelar().exitosa());   // una ejecutada no se cancela directamente
        assertEquals("EJECUTADA", s.getEstado());
    }

    @Test
    void soloLasTransicionesValidasSePublicanALosSuscriptores() {
        PublicadorCambiosEstado publicador = new PublicadorCambiosEstado(List.of());
        List<EventoCambioEstado> recibidos = new ArrayList<>();
        publicador.suscribir(evento -> recibidos.add(evento));
        Solicitud s = solicitudEn("PENDIENTE");
        ContextoSolicitud contexto = new ContextoSolicitud(s, publicador);

        contexto.ejecutar();   // inválida: no debe notificar
        assertTrue(recibidos.isEmpty());

        contexto.aprobar();    // válida: notifica una vez
        assertEquals(1, recibidos.size());
        assertEquals("APROBADA", recibidos.get(0).estadoNuevo());
    }
}