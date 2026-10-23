# 1. Análisis del enunciado

**Propuesta elegida:** B — Clínica veterinaria «Patitas»
**Materia:** Paradigma Orientado a Objetos (UADE) — TPO, Etapa 1

---

## 1.1 Contexto

Una clínica veterinaria con varios profesionales quiere informatizar la atención de pacientes, los turnos y el control de insumos. El sistema debe cubrir: registro de dueños y mascotas, gestión de turnos, consultas con historia clínica, control de stock, facturación y recordatorios de vacunas.

---

## 1.2 Actores

| Actor | Descripción | Responsabilidades principales |
|---|---|---|
| **Recepcionista** | Personal de recepción. | Registra dueños y mascotas, transfiere mascotas, asigna / reprograma / cancela turnos, registra urgencias y ausencias, emite facturas y registra cobros. **No** puede registrar diagnósticos ni tratamientos (RN-HC-03). |
| **Veterinario** | Profesional con matrícula, especialidad y horario de atención por día. | Registra consultas, diagnósticos y tratamientos, aplica vacunas, firma notas rectificatorias y confirma la consulta. Puede estar de guardia. |
| **Dueño de la mascota** | Cliente, responsable de una o más mascotas; puede estar adherido a un plan de salud animal. | Solicita turnos y cancelaciones (a través de recepción), paga las facturas y recibe notificaciones (recordatorios de vacunas, reprogramaciones). Actor externo sin acceso directo al sistema (ver S-06). |
| **Administrador** | Personal de gestión de la clínica. | Gestiona veterinarios, horarios y guardias, catálogo de vacunas, productos y lotes; consulta turnos por veterinario, stock bajo y vacunas próximas a vencer; anula facturas mediante nota de crédito. |

> De todo el personal se conoce nombre, apellido, documento y teléfono. Los recordatorios y alertas los genera el propio sistema (no es un actor humano; ver S-11).

---

## 1.3 Requerimientos funcionales

**CU mín.** = caso de uso mínimo del enunciado: (1) registrar dueños y mascotas · (2) turnos · (3) consultas e historia clínica · (4) stock · (5) facturación · (6) recordatorios de vacunas. Los marcados con «—» se derivan del enunciado de modelado.

### Dueños y mascotas

| ID | Requerimiento | CU mín. | Actor |
|---|---|---|---|
| RF-01 | Registrar dueños (nombre, apellido, documento, teléfono, correo electrónico). | 1 | Recepcionista |
| RF-02 | Adherir o dar de baja a un dueño del plan de salud animal. | — | Recepcionista |
| RF-03 | Registrar mascotas asociadas a un dueño, con datos comunes (nombre, especie, raza, sexo, fecha de nacimiento, peso actual) y datos propios de la especie: perro (chip, tamaño), gato (interior/exterior), ave (puede volar), conejo (ver S-01). | 1 | Recepcionista |
| RF-04 | Transferir una mascota a otro dueño conservando su historia clínica. | 1 | Recepcionista |

### Turnos

| ID | Requerimiento | CU mín. | Actor |
|---|---|---|---|
| RF-05 | Asignar un turno (mascota, veterinario, fecha y hora, tipo de atención) validando horario del profesional y superposición. | 2 | Recepcionista |
| RF-06 | Reprogramar un turno. | 2 | Recepcionista |
| RF-07 | Cancelar un turno, aplicando cargo si corresponde. | 2 | Recepcionista |
| RF-08 | Registrar ausencias, contabilizar las consecutivas y exigir seña al llegar a tres. | 2 | Recepcionista |
| RF-09 | Asignar un turno de urgencia al profesional de guardia, desplazando y reprogramando el turno siguiente si hace falta, y notificando al dueño. | 2 | Recepcionista |
| RF-10 | Consultar los turnos de cada veterinario. | — | Administrador / Veterinario |
| RF-11 | Registrar veterinarios con su matrícula, especialidad, horario por día y guardias. | — | Administrador |

### Consultas e historia clínica

| ID | Requerimiento | CU mín. | Actor |
|---|---|---|---|
| RF-12 | Registrar una consulta (motivo, peso del día, diagnóstico, observaciones) vinculada a un turno. | 3 | Veterinario |
| RF-13 | Indicar tratamientos en una consulta (medicamento, dosis, frecuencia, duración), exigiendo peso registrado. | 3 | Veterinario |
| RF-14 | Aplicar vacunas en una consulta (lote y fecha), validando edad mínima y calculando el próximo refuerzo. | 3 | Veterinario |
| RF-15 | Confirmar la consulta: descuenta stock, marca el turno como atendido, incorpora la consulta a la historia clínica y dispara la facturación. | 3 | Veterinario |
| RF-16 | Consultar la historia clínica de una mascota (conjunto de consultas). | 3 | Veterinario |
| RF-17 | Registrar una nota rectificatoria firmada sobre un registro de la historia clínica. | 3 | Veterinario |

### Vacunas

| ID | Requerimiento | CU mín. | Actor |
|---|---|---|---|
| RF-18 | Mantener el catálogo de vacunas (nombre, especies, edad mínima, intervalo de refuerzo). | — | Administrador |
| RF-19 | Emitir recordatorios 7 días antes del vencimiento del refuerzo y determinar vacunas vencidas (30 días sin aplicar). | 6 | Sistema → Dueño |
| RF-20 | Consultar vacunas próximas a vencer. | 6 | Administrador |

### Stock de insumos

| ID | Requerimiento | CU mín. | Actor |
|---|---|---|---|
| RF-21 | Registrar productos (código, nombre, stock mínimo, proveedor) y la recepción de lotes (cantidad y fecha de vencimiento). | 4 | Administrador |
| RF-22 | Descontar stock tomando primero el lote que vence antes; rechazar insumos sin stock o lotes vencidos. | 4 | Sistema |
| RF-23 | Generar una alerta de reposición cuando el stock llega al mínimo. | 4 | Sistema |
| RF-24 | Consultar productos con stock bajo. | 4 | Administrador |

### Facturación

| ID | Requerimiento | CU mín. | Actor |
|---|---|---|---|
| RF-25 | Emitir una factura por consulta, con número, fecha y líneas (consulta e insumos con cantidad y precio). | 5 | Recepcionista |
| RF-26 | Registrar el pago en efectivo, débito o crédito en cuotas, calculando descuentos y recargos. | 5 | Recepcionista |
| RF-27 | Anular una factura emitida mediante una nota de crédito (la factura nunca se modifica). | 5 | Administrador |

### Transversal

| ID | Requerimiento | CU mín. | Actor |
|---|---|---|---|
| RF-28 | Controlar permisos por rol (por ejemplo, solo el veterinario registra diagnósticos y tratamientos). | — | Sistema |

---

## 1.4 Reglas de negocio

### Turnos

| ID | Regla |
|---|---|
| RN-TUR-01 | La duración depende del tipo de atención: consulta general 30 min, vacunación 15 min, cirugía 120 min. |
| RN-TUR-02 | Cada profesional atiende dentro de su propio horario (ej.: lunes a sábado de 9 a 19) y no puede tener dos turnos superpuestos. |
| RN-TUR-03 | Un turno se cancela sin cargo hasta 2 horas antes. Pasado ese plazo se cobra el 20 % de la consulta. |
| RN-TUR-04 | Quien falta sin avisar queda como ausente. Con tres ausencias seguidas debe pagar una seña para pedir nuevos turnos. |
| RN-TUR-05 | Una urgencia toma el primer horario del profesional de guardia. Si está ocupado, desplaza al turno siguiente, que se reprograma y se notifica al dueño. |

### Mascotas e historia clínica

| ID | Regla |
|---|---|
| RN-HC-01 | Toda mascota tiene un dueño responsable; un dueño puede tener varias mascotas. Si la mascota se transfiere, conserva su historia clínica. |
| RN-HC-02 | Cada especie (perro, gato, ave, conejo) tiene datos propios y su propio calendario de vacunación. |
| RN-HC-03 | Solo un veterinario puede registrar diagnósticos y tratamientos; recepción no puede. |
| RN-HC-04 | La historia clínica solo admite nuevos registros: nada se borra ni se modifica. Un error se corrige con una nota rectificatoria firmada por el profesional. |
| RN-HC-05 | No se puede indicar un medicamento si en esa consulta no se registró el peso del animal. |

### Vacunas

| ID | Regla |
|---|---|
| RN-VAC-01 | Cada vacuna aplicada se registra con su lote y fecha; el sistema calcula el próximo refuerzo según la especie. |
| RN-VAC-02 | Se emite un recordatorio 7 días antes del vencimiento del refuerzo. Pasados 30 días sin aplicarlo, la vacuna se considera vencida. |
| RN-VAC-03 | No se puede aplicar una vacuna a un animal menor a la edad mínima definida para esa vacuna. |

### Stock de insumos

| ID | Regla |
|---|---|
| RN-STK-01 | Cada insumo tiene un stock mínimo; al llegar a ese valor se genera una alerta de reposición. |
| RN-STK-02 | No se puede aplicar ni vender un insumo sin stock. El stock se descuenta al confirmar la consulta, no al indicar el tratamiento. |
| RN-STK-03 | Se consume primero el lote con vencimiento más próximo; un lote vencido no puede usarse. |

### Facturación

| ID | Regla |
|---|---|
| RN-FAC-01 | La factura incluye la consulta y los insumos utilizados. |
| RN-FAC-02 | Efectivo: 10 % de descuento. Débito: sin recargo. Crédito en cuotas: 8 % de recargo. |
| RN-FAC-03 | Los clientes con plan de salud animal tienen 15 % de descuento. No se acumula con el de efectivo: se aplica el mayor. |
| RN-FAC-04 | Una factura emitida no se modifica. Si hay un error, se anula con una nota de crédito. |

---

## 1.5 Requisitos técnicos (no funcionales)

| Requisito | Detalle |
|---|---|
| Lenguaje | Java 17 o superior, organizado en paquetes. |
| Herencia y polimorfismo | Al menos una jerarquía con sobreescritura de métodos y uso de clases abstractas o interfaces. |
| Encapsulamiento | Atributos privados, constructores adecuados, modificadores de acceso correctos (incluido `protected` donde corresponda). |
| Colecciones | Colecciones apropiadas, con orden natural (`Comparable`) y personalizado (`Comparator`) en al menos un caso. |
| Excepciones | `try`/`catch`/`finally` y al menos una excepción propia lanzada con `throw`/`throws` para reglas de negocio. |
| Entrada y salida | Persistencia en archivos (lectura y escritura) que conserve la información entre ejecuciones. |
| Interfaz | Consola con menú o interfaz gráfica con manejo de eventos (esta última cuenta como mejora). |
| Patrones | 3 principios SOLID y 4 patrones GRASP visibles en el código. |
| Pruebas | Pruebas unitarias sobre la lógica de negocio y plan de pruebas funcionales. |
| Estilo | Nombres claros, métodos breves, comentarios donde la lógica no sea evidente. |

---

## 1.6 Supuestos

> **Estado: propuestos.** Cubren las ambigüedades detectadas en el enunciado y deben ser confirmados por el grupo antes de modelar. Los que más impactan el diseño de clases son S-02, S-05, S-09, S-13 y S-15.

| ID | Tema | Ambigüedad detectada | Supuesto adoptado |
|---|---|---|---|
| S-01 | Datos del conejo | El enunciado define datos propios para perro, gato y ave, pero no para conejo (que sí figura en las reglas). | El conejo guarda su **tipo de pelaje** (corto / largo). |
| S-02 | Descuentos y recargos | No se aclara cómo se combinan plan de salud, medio de pago y recargo del crédito. | Orden de cálculo: (1) subtotal; (2) descuento = **el mayor** entre efectivo (10 %) y plan de salud (15 %); (3) recargo del 8 % si es crédito, aplicado sobre el monto ya descontado. Débito: sin recargo. |
| S-03 | Seña y ausencias | No se define el monto de la seña ni cuándo se reinicia el contador de ausencias. | Las ausencias se cuentan **por dueño** (cualquiera de sus mascotas). Con tres consecutivas, cada nuevo turno exige una seña de monto fijo parametrizable (ej.: 30 % de la consulta general), que se descuenta de la factura. El contador se reinicia cuando una mascota del dueño es atendida. |
| S-04 | Cancelación tardía | «20 % de la consulta» no aclara sobre qué precio ni cómo se cobra; no se dice qué pasa al reprogramar. | Es el 20 % del precio del tipo de atención del turno, registrado como cargo y cobrado aparte. Reprogramar a pedido del dueño con menos de 2 h de anticipación se trata como cancelación tardía; la reprogramación por urgencia, iniciada por la clínica, no tiene cargo. |
| S-05 | Urgencias y guardia | No se define quién es el «profesional de guardia», qué es el «primer horario» ni qué pasa si el turno desplazado choca con otro. | Cada día el administrador designa **un veterinario de guardia**. La urgencia ocupa el primer hueco libre a partir del momento del pedido, dentro de su horario, con duración de consulta general (30 min). Si está ocupado, se desplaza **solo el turno programado siguiente**, que se reubica en el primer hueco libre del mismo veterinario (puede ser otro día), sin desplazamientos en cadena. Se notifica al dueño. |
| S-06 | Dueño como actor | El dueño figura como actor, pero no se indica si usa el sistema. | Actor **externo sin acceso directo**: pide turnos, cancelaciones y pagos a través de recepción y recibe notificaciones. |
| S-07 | Horario del veterinario | «Horario para cada día de la semana» no aclara si admite varios tramos. | Un único tramo continuo por día (o día sin atención). Un turno debe entrar completo dentro del tramo. |
| S-08 | Especies | No se aclara si se admiten otras especies. | Solo perro, gato, ave y conejo; el diseño debe permitir agregar especies sin modificar las existentes. |
| S-09 | Calendario de vacunación | El catálogo tiene un intervalo de refuerzo por vacuna, pero la regla dice que el refuerzo depende de la especie. | El catálogo guarda una entrada por **(vacuna, especie)** con edad mínima e intervalo de refuerzo. El «calendario» de una especie es el conjunto de sus entradas. |
| S-10 | Fechas de refuerzo | No se precisa qué fecha es el «vencimiento del refuerzo». | `fechaRefuerzo = fechaAplicación + intervalo`. El recordatorio se emite desde `fechaRefuerzo − 7 días`. La vacuna se considera **vencida** si pasaron más de 30 días desde `fechaRefuerzo` sin aplicar el refuerzo. |
| S-11 | Notificaciones | No se dice cómo se avisa al dueño ni cuándo se generan los recordatorios. | Las notificaciones son **simuladas** (se muestran por consola y se registran en archivo; no hay envío real de mail o SMS). Los recordatorios se generan a demanda desde el menú y al iniciar el sistema. |
| S-12 | Edad mínima | No se define la unidad. | Se mide en **semanas**, calculada a la fecha de aplicación a partir de la fecha de nacimiento. |
| S-13 | Origen de medicamentos y vacunas | No se aclara si medicamentos y vacunas salen del stock de la clínica. | Todo medicamento indicado y toda vacuna aplicada se provee desde el stock de la clínica: cada dosis consume un producto (de ahí el lote que se registra al vacunar) y se factura como insumo. |
| S-14 | Stock y alertas | «Stock actual» no aclara si incluye lotes vencidos; «al llegar a ese valor» no indica el operador. | Stock disponible = suma de los lotes **no vencidos**. La alerta se genera cuando `stock ≤ mínimo`, una sola vez por cruce, hasta que se reponga. |
| S-15 | Confirmar la consulta | El enunciado habla de «confirmar la consulta» sin definir el momento ni sus efectos. | La consulta nace en **borrador** (editable y sin efectos). Al indicar un tratamiento solo se verifica disponibilidad (no se reserva). Al **confirmar**: se vuelve a verificar stock, se descuenta por lote más próximo a vencer, el turno pasa a *atendido*, la consulta entra a la historia clínica (ya inmutable) y se emite la factura. |
| S-16 | Roles y acceso | No se define quién ejecuta cada función ni cómo se identifica el usuario. | Recepcionista: dueños, mascotas, turnos, facturas y cobros. Veterinario: consultas, diagnósticos, tratamientos, vacunas, notas rectificatorias y confirmación. Administrador: veterinarios, horarios y guardias, catálogo de vacunas, productos y lotes, consultas y notas de crédito. No hay login: al iniciar se elige un usuario del personal y el sistema valida permisos según su rol. |
| S-17 | Facturación | No se aclara cuántas facturas se emiten, cómo se fijan los precios, si las cuotas afectan el recargo ni qué efecto tiene una nota de crédito. | Una factura por consulta confirmada. El precio de la consulta surge de una tarifa por tipo de atención y el de los insumos de cada producto (valores de referencia cargados en el sistema). El recargo del crédito es **fijo (8 %)** sin importar la cantidad de cuotas, que se registra. La nota de crédito anula la factura completa y **no revierte** stock ni historia clínica; si corresponde, se emite una factura nueva. |
| S-18 | Nota rectificatoria | No figura en el enunciado de modelado. | Se asocia a una consulta de la historia clínica y guarda fecha, texto y firma del veterinario autor del registro original. El registro original queda intacto. |
| S-19 | Transferencia de mascota | No se indica si se conserva el historial de dueños. | Se registra fecha, dueño anterior y dueño nuevo. La mascota tiene un único dueño vigente. |
| S-20 | Peso del animal | No se aclara si el peso del día actualiza el peso de la mascota. | El peso de la consulta es obligatorio (> 0) para indicar medicamentos y **actualiza** el peso actual de la mascota. |
| S-21 | Estados del turno | Solo se listan los estados, sin transiciones. | *Programado* → *atendido* \| *cancelado* \| *ausente*. Recepción marca *ausente* cuando pasó la hora sin atención. No se asignan turnos en el pasado. |
| S-22 | Identificación de personas | No se indica cómo se identifica a dueños y personal. | El **documento** es único por persona y se usa como identificador de búsqueda. |
