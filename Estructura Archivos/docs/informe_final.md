# Informe final — Etapa 2 — Veterinaria Patitas

## 1. Alcance implementado

La Etapa 2 implementa en Java el diseño consolidado en la Etapa 1 y cubre los casos de uso mínimos de la propuesta B: registro de dueños y mascotas, turnos, consultas e historia clínica, stock, facturación y recordatorios de vacunas. También se incorporaron transferencia de mascota, ausencias y seña, urgencias, notas rectificatorias y notas de crédito.

La aplicación utiliza interfaz por consola, persistencia en archivo binario, manejo de excepciones de negocio, colecciones, herencia, clases abstractas, interfaces, polimorfismo, `Comparable`, `Comparator`, SOLID y GRASP.

## 2. Correspondencia con el diseño

Se conservaron las decisiones centrales de la Etapa 1:

- `Persona` y `PersonalClinica` como raíces abstractas de la jerarquía de personas.
- `Mascota` abstracta con `Perro`, `Gato`, `Ave` y `Conejo`.
- `Atencion` abstracta con `ConsultaGeneral`, `Vacunacion` y `Cirugia`.
- `Agenda` como experta en superposiciones y creadora de `Turno`.
- `ControladorTurnos`, `ControladorConsultas` y `ControladorFacturacion` como controladores GRASP.
- `IServicioStock` e `ICalculadorDescuento` como abstracciones para DIP.
- `ServicioStock` y `CalculadorDescuento` como implementaciones concretas.

## 3. Cambios respecto del diagrama final de Etapa 1

### C2-01 — Fechas y horas propias

**Cambio:** `LocalDate`, `LocalTime` y `LocalDateTime` se reemplazan por `Fecha`, `Hora` y `FechaHora`.

**Motivo:** la referencia de contenidos solo menciona `LocalDate` conceptualmente y no desarrolla su API. Para mantener el código dentro de las construcciones disponibles se implementaron las operaciones necesarias con primitivas, condicionales, ciclos y `Comparable`.

**Impacto:** las reglas de antigüedad, vencimiento, refuerzos, horarios y superposición permanecen disponibles sin depender de una API no cubierta.

### C2-02 — Estado `PENDIENTE` de factura

**Cambio:** `EstadoFactura` incorpora `PENDIENTE`.

**Motivo:** al confirmar la consulta se crea la factura, pero el pago lo registra posteriormente recepción. `EMITIDA` se asigna al completar el cobro.

### C2-03 — Datos necesarios para stock y facturación

**Cambio:** `Producto` agrega `precioUnitario`; `Lote` agrega `codigo`; `Tratamiento` agrega `cantidad`; `VacunaDefinicion` referencia el `Producto` que representa la dosis.

**Motivo:** son datos necesarios para descontar cantidades, registrar el lote de vacuna y construir las líneas de factura. El supuesto S-17 de Etapa 1 ya establecía que los productos tendrían valores de referencia.

### C2-04 — Contrato de stock ampliado

**Cambio:** `IServicioStock` pasa de operar solo con tratamientos a verificar y descontar una `Consulta` completa junto con la fecha de consumo.

**Motivo:** una consulta puede consumir tratamientos y vacunas. Verificar el conjunto completo antes del primer descuento evita una confirmación parcial cuando varios consumos requieren el mismo producto.

### C2-05 — Notificaciones

**Cambio:** se agrega `ServicioNotificaciones`.

**Motivo:** aplica Fabricación Pura GRASP para separar escritura de logs y salida de consola de las entidades del dominio. Se utiliza para notificaciones simuladas y registro en archivo.

### C2-06 — Guardia y desplazamiento

**Cambio:** `ControladorTurnos` mantiene guardias por fecha y recuerda el último turno desplazado.

**Motivo:** implementar la regla de urgencia sin desplazamientos en cadena y permitir que la presentación notifique al dueño afectado.

### C2-07 — Autenticación en archivo TXT plano y gestión de usuarios

**Cambio:** se incorpora `RepositorioUsuarios` en el paquete `persistencia` para gestionar `data/usuarios.txt` con `BufferedReader`/`BufferedWriter` y `try-catch-finally`. Se agrega opción de gestión de usuarios en el menú del Administrador.

**Motivo:** independizar la autenticación del código de `Main`, garantizar persistencia en texto plano de credenciales (`legajo;contrasenia`) y permitir al Administrador crear, modificar y listar usuarios de forma atómica respetando las validaciones del sistema.

### C2-08 — Menú polimórfico desacoplado por rol

**Cambio:** se extraen los menús monolíticos de `Main` a la jerarquía polimórfica `MenuRol`, `MenuRecepcionista`, `MenuVeterinario` y `MenuAdministrador` en el paquete `app`.

**Motivo:** aplicar el principio de Responsabilidad Única (SRP) y Polimorfismo (GRASP), reduciendo la complejidad ciclomática de `Main` y asegurando que cada rol solo visualice y ejecute sus opciones autorizadas con numeración propia y consecutiva.

### C2-09 — Helpers de entrada robusta y conversión de unidades de peso

**Cambio:** se crea `ConsolaHelper` para centralizar lecturas con bucles de reintento ante entradas inválidas (fechas, horas, números, s/n) y conversión unificada de libras a kilogramos (`1 lb = 0.45359237 kg`).

**Motivo:** robustecer la interacción por consola sin abortar operaciones en curso y estandarizar el almacenamiento de pesos en kilogramos.

## 4. SOLID y GRASP visibles en el código

### SOLID

- **SRP:** `Agenda` gestiona turnos; `ServicioStock` inventario; `ControladorFacturacion` comprobantes; `ServicioNotificaciones` salida de notificaciones.
- **OCP:** nuevas especies o tipos de atención se agregan como nuevas subclases sin modificar el cliente estable.
- **DIP:** `ControladorConsultas` depende de `IServicioStock`; `ControladorFacturacion` depende de `ICalculadorDescuento`.

### GRASP

- **Experto:** `Agenda` conoce los turnos; `Veterinario` conoce horarios; `Producto` conoce lotes y stock.
- **Creador:** `Agenda` crea `Turno`; `ControladorFacturacion` crea `Factura` y `NotaCredito`; `ControladorConsultas` crea `Consulta` y `NotaRectificatoria`.
- **Controlador:** los tres controladores reciben los eventos del menú y coordinan el dominio.
- **Polimorfismo:** jerarquías `Mascota` y `Atencion` sobrescriben comportamiento específico.

## 5. Dificultades encontradas

1. **Fechas y horarios sin `java.time`:** se resolvió mediante clases propias con validación, comparación y aritmética básica.
2. **Consistencia de confirmación:** era necesario evitar descuentos parciales cuando una consulta usa varias veces el mismo producto; se agregó una verificación agregada previa.
3. **Urgencias:** se implementó el desplazamiento de un único turno, evitando cascadas.
4. **Diferencia entre factura creada y factura pagada:** se agregó el estado `PENDIENTE` para representar correctamente el flujo.

## 6. Distribución de tareas

La implementación se repartió por módulos. Los siguientes commits identifican los aportes principales de cada integrante.

| Integrante | Tareas realizadas | Commits y PR relacionados |
|---|---|---|
| Milena Luciana Mendez | Implementó las jerarquías de personas y mascotas, las consultas clínicas, los tratamientos y los recordatorios de vacunas. Integró el sistema, los menús por rol y los datos de demostración. | `89ae3d7`, `4aa4f7b` y `52e7eba` |
| Adrián Maldonado | Implementó los tipos de atención, la agenda de turnos y la facturación con descuentos y notas de crédito. Incorporó las pruebas automatizadas y la documentación de entrega. En la puesta a punto agregó el cobro de señas y cargos, amplió el menú veterinario y ajustó las reglas de vacunas, facturas y urgencias. | `354f798`, `04c439d` y `51676e6`. Ajustes en el PR #17. |
| Santiago Coco | Implementó los tipos de fecha y hora y la excepción del dominio. Desarrolló el inventario por lotes, la persistencia de datos y el servicio de notificaciones. | `a861730`, `edef104` y `6e72a5e` |

## 10. Reflexión del grupo

Al pasar del diagrama al código, tuvimos que precisar cuándo cambia el estado de una entidad y qué datos necesita cada operación. Por ejemplo, distinguir una factura pendiente de una emitida nos permitió separar la confirmación de la consulta del cobro en recepción. La verificación de stock también nos llevó a revisar la consulta completa antes de descontar unidades, para que un faltante no deje el inventario parcialmente modificado.

En la puesta a punto vimos que probar una regla con un solo ejemplo podía ocultar errores. La urgencia funcionaba con un turno de 30 minutos, pero al probar con una vacunación de 15 minutos apareció un problema de superposición. También fue importante probar qué pasa al rechazar una cancelación con cargo y comprobar que el turno siguiera programado. En un próximo trabajo definiríamos estos casos límite al diseñar cada flujo y actualizaríamos el diagrama y las pruebas en el mismo cambio que modifica la regla.
