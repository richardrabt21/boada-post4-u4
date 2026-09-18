# Post-contenido — Unidad 4: Patrones de Comportamiento en ComprasUDES

## Descripción

Repositorio del post-contenido de la Unidad 4 de Patrones de Diseño de Software. Un único proyecto Spring Boot (`compras-comportamiento`) que resuelve cuatro necesidades reales del backend de ComprasUDES, el sistema interno de solicitudes de compra corporativas: aprobación por niveles jerárquicos, ejecución reversible de solicitudes aprobadas, notificaciones ante cambios de estado y reglas de transición según el estado actual de la solicitud.

## Estructura del proyecto

```
src/main/java/com/universidad/compras/
├── ComprasApp.java
├── modelo/          Solicitud (dada)
├── aprobacion/      Necesidad 1: cadena de aprobación (Chain of Responsibility)
├── ejecucion/       Necesidad 2: comandos con undo e historial (Command)
├── notificacion/    Necesidad 3: publicador y suscriptores (Observer)
└── estado/          Necesidad 4: reglas de transición (State)
src/test/java/com/universidad/compras/
├── aprobacion/AprobacionNivelesTest.java
├── ejecucion/EjecucionSolicitudTest.java
├── notificacion/NotificacionEstadoTest.java
└── estado/TransicionEstadoTest.java
```

El código dado (`Solicitud`, `ServicioAprobacion`, `ResultadoAprobacion`, `ControladorSolicitudes`, `PresupuestoService`, `OrdenCompraService` y `ClientesNotificacion`) no fue modificado.

## Cómo ejecutar

```
$ mvn clean package
$ mvn spring-boot:run
$ mvn test
```

Con la aplicación levantada se puede probar el endpoint de aprobación, que además dispara las notificaciones por consola:

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

**Alternativa descartada: Command.** Command encapsula una acción como objeto para ejecutarla, deshacerla y guardarla en un historial. En la aprobación nadie deshace nada: lo que se necesita es que una petición avance hasta que alguien la resuelva, algo que Command no modela (no tiene noción de "delegar al siguiente si no me corresponde"). Un `if/switch` por monto tampoco sirve: cada nivel nuevo obligaría a modificar ese bloque y haría que el servicio o el controlador conocieran los niveles.

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

**Patrón aplicado: Observer.**

Cada vez que una solicitud cambia de estado deben reaccionar tres módulos independientes (correo al solicitante, tablero de contabilidad y log de auditoría), sin que el código que cambia el estado los conozca, y una cuarta reacción no debe obligar a modificarlo.

Cómo se resolvió:
- `PublicadorCambiosEstado` es el único punto que cambia el estado de una solicitud y avisa a sus suscriptores mediante un `EventoCambioEstado` (solicitud, estado anterior y estado nuevo). No conoce a ningún suscriptor concreto: Spring le inyecta todos los `ObservadorEstado` existentes.
- `NotificadorCorreo`, `ActualizadorDashboardContabilidad` y `RegistradorAuditoria` son los tres suscriptores; cada uno usa su método de `ClientesNotificacion` (clase dada, sin modificar).
- Una cuarta reacción se agrega creando una clase nueva que implemente `ObservadorEstado` (o registrándola con `suscribir`), sin tocar el publicador. La prueba `agregarUnCuartoSuscriptorDePruebaNoRequiereModificarElMecanismo` lo verifica con un colector.
- Conexión con las necesidades anteriores: `ServicioAprobacionEnCadena` (Necesidad 1), `EjecutorSolicitudes` (Necesidad 2) y `ContextoSolicitud` (Necesidad 4) cambian el estado a través del publicador. Los niveles de aprobación ya no llaman a `setEstado`.
- Un suscriptor que falla no impide el cambio de estado ni la notificación a los demás, y un "cambio" al mismo estado no se notifica.

**Alternativa descartada: State (la patrón de la Necesidad 4).** El comportamiento que cambia aquí no es el de la propia solicitud, sino el de módulos externos (correo, contabilidad, auditoría) que deben enterarse cuando la solicitud ya cambió. State modela lo que una solicitud puede hacer según su estado actual: ataría las reacciones a las clases internas de estado y no ofrece una lista de suscriptores a la que agregar una cuarta reacción sin modificar código existente.

### Necesidad 4 — Reglas de transición según el estado

**Patrón aplicado: State.**

Las reglas sobre qué operaciones son válidas (aprobar, rechazar, ejecutar, cancelar) estaban repetidas como `if/else` sobre `getEstado()` en varios métodos, de modo que cada estado nuevo obligaba a modificar varios lugares a la vez.

Cómo se resolvió:
- `EstadoSolicitud` define las cuatro operaciones y, por defecto, todas se rechazan. Cada estado (`EstadoPendiente`, `EstadoEnAprobacion`, `EstadoAprobada`, `EstadoEjecutada`, `EstadoRechazada`, `EstadoCancelada`) sobrescribe únicamente las que permite y decide a qué estado transiciona.
- `ContextoSolicitud` representa la solicitud con comportamiento: cada operación delega en el objeto de su estado actual, que se lee siempre de la `Solicitud` para evitar desincronizaciones. Una operación inválida devuelve un `ResultadoTransicion` con error y no cambia el estado; una válida cambia el estado a través del publicador de la Necesidad 3.
- `RegistroEstados` es el único lugar donde se registran los estados. Agregar `EN_ESPERA_PROVEEDOR` sería crear su clase y añadirla a ese registro, sin tocar `if/else` porque ya no existen.

| Estado actual | aprobar | rechazar | ejecutar | cancelar |
|---|---|---|---|---|
| PENDIENTE / EN_APROBACION | APROBADA | RECHAZADA | error | CANCELADA |
| APROBADA | error | error | EJECUTADA | CANCELADA |
| EJECUTADA | error | error | error | error |
| RECHAZADA / CANCELADA | error | error | error | error |

**Alternativa descartada: Strategy.** Strategy se parece estructuralmente (un contexto que delega en una interfaz con varias implementaciones), pero su intención es distinta: un cliente externo elige e inyecta explícitamente el comportamiento activo, como el carrito de compras que activa la estrategia de descuento que desea, y las estrategias son independientes entre sí y no transicionan. Aquí no hay ningún cliente que elija: qué operaciones son válidas lo determina el estado en que se encuentra la propia solicitud en ese instante, y cada operación válida además provoca una transición a otro estado. Con Strategy los `if/else` reaparecerían en el código cliente que decide qué estrategia usar.

Limitación conocida: `ContextoSolicitud` aplica las reglas de transición, pero la cadena de la Necesidad 1 y el ejecutor de la Necesidad 2 cambian el estado directamente a través del publicador, sin consultar esas reglas.

### Reflexión — otros tres patrones (no rubricada)

1. **Recorrer las solicitudes de un centro de costo sin exponer cómo se almacenan:** encajaría **Iterator**, que da acceso secuencial a los elementos sin revelar si están en una lista, un mapa u otra estructura.
2. **Tres comprobantes con el mismo esqueleto (encabezado, cuerpo, pie) y distinto cuerpo:** encajaría **Template Method**: una clase base define el esqueleto de impresión y cada tipo de comprobante solo implementa cómo llena el cuerpo.
3. **Guardar y restaurar instantáneas completas de una solicitud sin que el código que las guarda conozca su interior:** se acercaría **Memento**. Se diferencia del undo de la Necesidad 2 en que Command deshace una operación ejecutando su inversa (liberar presupuesto, cancelar orden), mientras que Memento guarda una foto del estado y la restaura sin saber qué operaciones ocurrieron; sirve cuando no existe una operación inversa, aunque no revierte efectos en sistemas externos.

## Herramientas utilizadas

- Java 17 (compilación), Spring Boot 3.2, Apache Maven, JUnit 5
- Visual Studio Code, Git, GitHub

## Conclusiones

Lo más difícil fue distinguir patrones con estructura parecida: State y Strategy comparten un contexto que delega en una interfaz, y Observer y State parecen resolver el mismo problema de "reaccionar al estado". Lo que despejó cada duda fue preguntar quién decide y qué cambia: si el comportamiento lo elige un cliente externo o depende del estado del propio objeto, y si quienes reaccionan son módulos ajenos o la propia solicitud. Chain of Responsibility y Command también se confunden porque ambos aparecen en el ciclo de la solicitud, pero uno encamina una petición hasta que alguien la resuelve y el otro encapsula operaciones reversibles con historial. Centralizar el cambio de estado en un único publicador hizo que las notificaciones funcionaran para las cuatro necesidades sin repetir código. Como mejora, integraría `ContextoSolicitud` con la cadena de aprobación y con el ejecutor para que también respeten las reglas de transición.