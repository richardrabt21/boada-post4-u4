package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;
import com.universidad.compras.notificacion.EventoCambioEstado;
import com.universidad.compras.notificacion.PublicadorCambiosEstado;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AprobacionNivelesTest {

    private ServicioAprobacion crearServicio() {
        PublicadorCambiosEstado publicador = new PublicadorCambiosEstado(List.of());
        return new ConfiguracionAprobacion().servicioAprobacion(publicador);
    }

    @Test
    void solicitudDentroDeAutoridadDelSupervisorSeAprueba() {
        ServicioAprobacion servicio = crearServicio();
        Solicitud s = new Solicitud("S-001", "ana@udes.edu.co", 1500000, "MATERIAL_OFICINA", "CC-100");

        ResultadoAprobacion r = servicio.evaluar(s);

        assertTrue(r.isAprobada());
        assertEquals("Supervisor de Área", r.getNivelResolutor());
        assertEquals("APROBADA", s.getEstado());
    }

    @Test
    void solicitudQueSuperaAlSupervisorEscalaAlGerente() {
        ServicioAprobacion servicio = crearServicio();
        Solicitud s = new Solicitud("S-002", "luis@udes.edu.co", 6000000, "SOFTWARE", "CC-200");

        ResultadoAprobacion r = servicio.evaluar(s);

        assertTrue(r.isAprobada());
        assertEquals("Gerente de Área", r.getNivelResolutor());
    }

    @Test
    void solicitudInternacionalPasaPorCumplimientoAntesDelNivelPorMonto() {
        ServicioAprobacion servicio = crearServicio();
        Solicitud s = new Solicitud("S-003", "gerencia@udes.edu.co", 1000000, "INTERNACIONAL", "CC-300");

        ResultadoAprobacion r = servicio.evaluar(s);

        assertEquals("Revisor de Cumplimiento Normativo", r.getNivelResolutor());
    }

    @Test
    void laEvaluacionPublicaElCambioDeEstado() {
        PublicadorCambiosEstado publicador = new PublicadorCambiosEstado(List.of());
        List<EventoCambioEstado> recibidos = new ArrayList<>();
        publicador.suscribir(evento -> recibidos.add(evento));
        ServicioAprobacion servicio = new ConfiguracionAprobacion().servicioAprobacion(publicador);
        Solicitud s = new Solicitud("S-004", "ana@udes.edu.co", 1000000, "SOFTWARE", "CC-100");

        servicio.evaluar(s);

        assertEquals(1, recibidos.size());
        assertEquals("PENDIENTE", recibidos.get(0).estadoAnterior());
        assertEquals("APROBADA", recibidos.get(0).estadoNuevo());
    }
}