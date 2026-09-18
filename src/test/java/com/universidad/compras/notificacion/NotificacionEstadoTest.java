package com.universidad.compras.notificacion;

import com.universidad.compras.modelo.Solicitud;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificacionEstadoTest {

    private PublicadorCambiosEstado crearPublicadorConLasTresReacciones() {
        return new PublicadorCambiosEstado(List.of(
                new NotificadorCorreo(),
                new ActualizadorDashboardContabilidad(),
                new RegistradorAuditoria()));
    }

    @Test
    void cambiarEstadoDisparaLasTresReaccionesSinLanzarExcepcion() {
        Solicitud s = new Solicitud("S-020", "ana@udes.edu.co", 2500000, "SOFTWARE", "CC-100");
        PublicadorCambiosEstado publicador = crearPublicadorConLasTresReacciones();

        PrintStream original = System.out;
        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        System.setOut(new PrintStream(salida, true, StandardCharsets.UTF_8));
        try {
            assertDoesNotThrow(() -> publicador.cambiarEstado(s, "APROBADA"));
        } finally {
            System.setOut(original);
        }

        String texto = salida.toString(StandardCharsets.UTF_8);
        assertEquals("APROBADA", s.getEstado());
        assertTrue(texto.contains("[EMAIL -> ana@udes.edu.co]"));
        assertTrue(texto.contains("[DASHBOARD] Solicitud S-020 -> APROBADA"));
        assertTrue(texto.contains("[AUDITORIA] S-020 -> APROBADA"));
    }

    @Test
    void agregarUnCuartoSuscriptorDePruebaNoRequiereModificarElMecanismo() {
        Solicitud s = new Solicitud("S-021", "luis@udes.edu.co", 1000000, "SOFTWARE", "CC-200");
        PublicadorCambiosEstado publicador = crearPublicadorConLasTresReacciones();
        List<EventoCambioEstado> recibidos = new ArrayList<>();
        publicador.suscribir(evento -> recibidos.add(evento));

        assertDoesNotThrow(() -> publicador.cambiarEstado(s, "RECHAZADA"));

        assertEquals(1, recibidos.size());
        assertEquals("PENDIENTE", recibidos.get(0).estadoAnterior());
        assertEquals("RECHAZADA", recibidos.get(0).estadoNuevo());
    }

    @Test
    void noNotificaCuandoElEstadoNoCambia() {
        Solicitud s = new Solicitud("S-022", "ana@udes.edu.co", 1000000, "SOFTWARE", "CC-100");
        PublicadorCambiosEstado publicador = new PublicadorCambiosEstado(List.of());
        List<EventoCambioEstado> recibidos = new ArrayList<>();
        publicador.suscribir(evento -> recibidos.add(evento));

        publicador.cambiarEstado(s, "PENDIENTE");

        assertTrue(recibidos.isEmpty());
    }

    @Test
    void unSuscriptorQueFallaNoImpideElCambioNiALosDemas() {
        Solicitud s = new Solicitud("S-023", "ana@udes.edu.co", 1000000, "SOFTWARE", "CC-100");
        PublicadorCambiosEstado publicador = new PublicadorCambiosEstado(List.of());
        List<EventoCambioEstado> recibidos = new ArrayList<>();
        publicador.suscribir(evento -> { throw new IllegalStateException("fallo simulado"); });
        publicador.suscribir(evento -> recibidos.add(evento));

        publicador.cambiarEstado(s, "APROBADA");

        assertEquals("APROBADA", s.getEstado());
        assertEquals(1, recibidos.size());
    }
}