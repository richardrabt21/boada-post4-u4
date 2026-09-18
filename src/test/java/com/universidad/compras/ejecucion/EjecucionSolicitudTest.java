package com.universidad.compras.ejecucion;

import com.universidad.compras.modelo.Solicitud;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EjecucionSolicitudTest {

    private EjecutorSolicitudes crearEjecutor() {
        return new EjecutorSolicitudes(new PresupuestoService(), new OrdenCompraService());
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
}