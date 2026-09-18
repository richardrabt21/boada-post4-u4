# Post-contenido — Unidad 4: Patrones de Comportamiento en ComprasUDES

## Descripción

Repositorio del post-contenido de la Unidad 4 de Patrones de Diseño de Software. Un único proyecto Spring Boot (`compras-comportamiento`) que resuelve cuatro necesidades reales del backend de ComprasUDES, el sistema interno de solicitudes de compra corporativas: aprobación por niveles jerárquicos, ejecución reversible de solicitudes aprobadas, notificaciones ante cambios de estado y reglas de transición según el estado actual de la solicitud.

> Estado actual: Parte 1 completa (Necesidades 1 y 2). Las Necesidades 3 y 4 se agregan en la Parte 2.

## Estructura del proyecto

```
src/main/java/com/universidad/compras/
├── ComprasApp.java
├── modelo/          Solicitud (dada)
├── aprobacion/      Necesidad 1: cadena de aprobación
└── ejecucion/       Necesidad 2: comandos con undo e historial
src/test/java/com/universidad/compras/
├── aprobacion/AprobacionNivelesTest.java
└── ejecucion/EjecucionSolicitudTest.java
```

## Cómo ejecutar

```
$ mvn clean package
$ mvn spring-boot:run
$ mvn test
```

Con la aplicación levantada se puede probar el endpoint de aprobación:

```
POST http://localhost:8080/api/solicitudes/evaluar
{"id":"S-100","solicitanteEmail":"ana@udes.edu.co","monto":6000000,"categoria":"SOFTWARE","centroCosto":"CC-200"}
```

## Decisiones de diseño

### Necesidad 1 — Aprobación por niveles jerárquicos

**Patrón aplicado: Chain of Responsibility.**

El problema es que una solicitud debe recorrer una secuencia de decisores independientes (Supervisor de Área, Gerente de Área, Director Financiero y, para solicitudes INTERNACIONAL, el Revisor de Cumplimiento Normativo). Cada uno la resuelve si está dentro de su autoridad o la delega al siguiente, y la secuencia debe poder crecer o reordenarse sin tocar el código que dispara la evaluación.

Cómo se resolvió:
- `NivelAprobacion` es el eslabón base: guarda la referencia al siguiente nivel y decide en `evaluar` si resuelve la solicitud o la delega.
- `SupervisorArea`, `GerenteArea`, `DirectorFinanciero` y `RevisorCumplimientoNormativo` solo definen su nombre y su regla de autoridad. Ninguno conoce a los demás.
- `ServicioAprobacionEnCadena` implementa el contrato dado `ServicioAprobacion` y solo conoce el primer eslabón.
- `ConfiguracionAprobacion` es el único lugar que conoce el orden de los niveles. Agregar, quitar o reordenar un nivel solo modifica esta clase.
- El Revisor de Cumplimiento va primero y solo actúa si la categoría es `INTERNACIONAL`, por lo que ni el servicio ni `ControladorSolicitudes` (que no se modificó) necesitan saber qué categorías requieren un nivel adicional.
- Cada nivel que resuelve registra su nombre en `nivelResolutor`, tanto en la `Solicitud` como en el `ResultadoAprobacion`.

**Alternativa descartada: Command.** Command encapsula una acción como objeto para ejecutarla, deshacerla y guardarla en un historial. En la aprobación nadie deshace nada: lo que se necesita es que una petición avance hasta que alguien la resuelva, algo que Command no modela (no tiene noción de "delegar al siguiente si no me corresponde"). Un `if/switch` por monto tampoco sirve: cada nivel nuevo obligaría a modificar ese bloque y a `ControladorSolicitudes` o al servicio a conocer los niveles.

### Necesidad 2 — Ejecución reversible de solicitudes

**Patrón aplicado: Command.**

Reservar presupuesto y generar la orden de compra son operaciones discretas que un mismo actor (el equipo de Compras) ejecuta y puede deshacer de forma independiente, y el sistema debe conservar un historial ordenado de todas las operaciones de cada solicitud.

Cómo se resolvió:
- `Comando` define `ejecutar()`, `deshacer()` y `descripcion()`.
- `ReservarPresupuestoComando` envuelve a `PresupuestoService` (reservar / liberar) y `GenerarOrdenCompraComando` envuelve a `OrdenCompraService` (generar / cancelar, recordando el número de orden). Los servicios dados no se modificaron.
- `InvocadorOperaciones` ejecuta los comandos y mantiene un historial por solicitud (un `Deque` en orden cronológico). Permite deshacer la última operación o una operación concreta del historial, sin afectar a las demás.
- `EjecutorSolicitudes` es la puerta de entrada: deja la solicitud en `EJECUTADA` cuando ambas operaciones terminan bien y expone el historial completo, no solo la última operación.
- Decisión adicional: si se deshace una operación de una solicitud `EJECUTADA`, esta vuelve a `APROBADA`, porque deja de estar completamente ejecutada.

**Alternativa descartada: Chain of Responsibility.** Aquí no hay ningún decisor que evalúe condiciones para resolver o delegar una petición entrante: hay operaciones concretas que el equipo decide ejecutar y, eventualmente, revertir. La cadena tampoco ofrece deshacer ni historial. Y a la inversa, el problema de la Necesidad 1 no es de acciones reversibles sino de encaminar una petición por niveles reconfigurables, por lo que Command no lo resolvería.

### Necesidad 3 — Notificaciones ante cambio de estado

_Se completa en la Parte 2._

### Necesidad 4 — Reglas de transición según el estado

_Se completa en la Parte 2._

## Herramientas utilizadas

- Java 17 (compilación), Spring Boot 3.2, Apache Maven, JUnit 5
- Visual Studio Code, Git, GitHub