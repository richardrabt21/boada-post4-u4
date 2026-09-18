package com.universidad.compras.aprobacion;

import com.universidad.compras.modelo.Solicitud;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// Ya en producción — no modificar. Depende únicamente de ServicioAprobacion.
@RestController
@RequestMapping("/api/solicitudes")
public class ControladorSolicitudes {

    private final ServicioAprobacion servicioAprobacion;

    public ControladorSolicitudes(ServicioAprobacion servicioAprobacion) {
        this.servicioAprobacion = servicioAprobacion;
    }

    @PostMapping("/evaluar")
    public ResponseEntity<ResultadoAprobacion> evaluar(@RequestBody Solicitud solicitud) {
        ResultadoAprobacion resultado = servicioAprobacion.evaluar(solicitud);
        return resultado.isAprobada()
                ? ResponseEntity.ok(resultado)
                : ResponseEntity.status(422).body(resultado);
    }
}