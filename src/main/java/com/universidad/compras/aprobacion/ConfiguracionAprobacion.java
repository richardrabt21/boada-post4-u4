package com.universidad.compras.aprobacion;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracionAprobacion {

    @Bean
    public ServicioAprobacion servicioAprobacion() {
        NivelAprobacion cumplimiento = new RevisorCumplimientoNormativo();

        cumplimiento
                .setSiguiente(new SupervisorArea())
                .setSiguiente(new GerenteArea())
                .setSiguiente(new DirectorFinanciero());

        return new ServicioAprobacionEnCadena(cumplimiento);
    }
}