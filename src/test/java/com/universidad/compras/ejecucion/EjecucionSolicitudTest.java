package com.universidad.compras.ejecucion;

import com.universidad.compras.modelo.Solicitud;
import com.universidad.compras.notificacion.EventoCambioEstado;
import com.universidad.compras.notificacion.PublicadorCambiosEstado;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EjecucionSolicitudTest {

    private EjecutorSolicitudes crearEjecutor(PublicadorCambiosEstado publicador) {
        return new EjecutorSolicitudes(new PresupuestoService(), new OrdenCompraService(), publicador);
    }

    private EjecutorSolicitudes crearEjecutor() {
        return crearEjecutor(new PublicadorCambiosEstado(List.of()));
    }

    @Test
    void ejecutarReservaPresupuestoYGeneraOrden() {
        Solicitud s = new Solicitud("S-010", "ana@udes.edu.co", 3000000, "SOFTWARE", "CC-100");
        s.setEstado("APROBADA");
        EjecutorSolicitudes ejecutor = crearEjecutor();

        ejecutor.ejecutar(s, "Proveedor Uno");

        assertEquals("EJECUTADA", s.getEstado());
    }

    @Test
    void deshacerSoloLaUltimaOperacionNoAfectaLaAnterior() {
        Solicitud s = new Solicitud("S-011", "luis@udes.edu.co", 4000000, "MATERIAL_OFICINA", "CC-200");
        EjecutorSolicitudes ejecutor = crearEjecutor();

        assertDoesNotThrow(() -> {
            ejecutor.reservarPresupuesto(s);
            ejecutor.generarOrdenCompra(s, "Proveedor Uno");
            ejecutor.deshacerUltimaOperacion(s);
        });

        List<Comando> historial = ejecutor.historial(s);
        assertEquals(1, historial.size());
        assertInstanceOf(ReservarPresupuestoComando.class, historial.get(0));
    }

    @Test
    void elHistorialConservaTodasLasOperacionesNoSoloLaUltima() {
        Solicitud s = new Solicitud("S-012", "ana@udes.edu.co", 2000000, "SOFTWARE", "CC-100");
        EjecutorSolicitudes ejecutor = crearEjecutor();

        assertDoesNotThrow(() -> {
            ejecutor.reservarPresupuesto(s);
            ejecutor.generarOrdenCompra(s, "Proveedor Uno");
        });

        assertEquals(2, ejecutor.historial(s).size());
    }

    @Test
    void sePuedeDeshacerLaReservaSinCancelarLaOrden() {
        Solicitud s = new Solicitud("S-013", "luis@udes.edu.co", 1500000, "SOFTWARE", "CC-200");
        EjecutorSolicitudes ejecutor = crearEjecutor();

        Comando reserva = ejecutor.reservarPresupuesto(s);
        ejecutor.generarOrdenCompra(s, "Proveedor Uno");

        ejecutor.deshacerOperacion(s, reserva);

        List<Comando> historial = ejecutor.historial(s);
        assertEquals(1, historial.size());
        assertInstanceOf(GenerarOrdenCompraComando.class, historial.get(0));
    }

    @Test
    void ejecutarYDeshacerPublicanLosCambiosDeEstado() {
        PublicadorCambiosEstado publicador = new PublicadorCambiosEstado(List.of());
        List<EventoCambioEstado> recibidos = new ArrayList<>();
        publicador.suscribir(evento -> recibidos.add(evento));
        EjecutorSolicitudes ejecutor = crearEjecutor(publicador);
        Solicitud s = new Solicitud("S-014", "ana@udes.edu.co", 3000000, "SOFTWARE", "CC-100");
        s.setEstado("APROBADA");

        ejecutor.ejecutar(s, "Proveedor Uno");
        ejecutor.deshacerUltimaOperacion(s);

        assertEquals(2, recibidos.size());
        assertEquals("EJECUTADA", recibidos.get(0).estadoNuevo());
        assertEquals("APROBADA", recibidos.get(1).estadoNuevo());
    }
}