package com.universidad.compras.ejecucion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracionEjecucion {

    @Bean
    public PresupuestoService presupuestoService() {
        return new PresupuestoService();
    }

    @Bean
    public OrdenCompraService ordenCompraService() {
        return new OrdenCompraService();
    }

    @Bean
    public EjecutorSolicitudes ejecutorSolicitudes(PresupuestoService presupuestoService,
                                                   OrdenCompraService ordenCompraService) {
        return new EjecutorSolicitudes(presupuestoService, ordenCompraService);
    }
}