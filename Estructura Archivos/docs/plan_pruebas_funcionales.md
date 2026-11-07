# Plan de pruebas funcionales — Veterinaria Patitas

## Criterio

Se validan los casos de uso principales y las reglas de negocio más sensibles. Las pruebas funcionales automatizadas se ejecutan con `PruebasFuncionales.java`; los resultados guardados corresponden a una ejecución real posterior a la compilación.

| ID | Caso / regla | Datos de entrada principales | Resultado esperado | Resultado obtenido |
|---|---|---|---|---|
| PF-01 | Registrar dueño y mascota | Dueño doc. `20`; perro `M1` | Ambos quedan registrados y asociados | Correcto |
| PF-02 | Asignar turno | 02/11/2026 10:00, veterinario con horario 9–19 | Turno `PROGRAMADO` | Correcto |
| PF-03 | Evitar superposición | Segundo turno 10:15 de 30 min | Se rechaza por superposición | Correcto |
| PF-04 | Cancelación tardía | Arancel $1000; cancelación 1 h antes | Estado `CANCELADO`; cargo $200 | Correcto |
| PF-05 | Confirmar consulta | Peso, diagnóstico, tratamiento y vacuna | Turno `ATENDIDO`; consulta confirmada en historia | Correcto |
| PF-06 | Descontar stock al confirmar | 5 unidades medicamento; consume 2; vacuna 3, consume 1 | Quedan 3 y 2 unidades respectivamente | Correcto |
| PF-07 | Pago con plan + crédito | Subtotal $1400; plan 15%; crédito 8% | Total $1285,20 | Correcto |
| PF-08 | Anular factura | Factura emitida + motivo | Factura `ANULADA` y nota de crédito | Correcto |
| PF-09 | Recordatorio de vacuna | Refuerzo a 7 días; fecha de consulta dentro de ventana | Se genera recordatorio | Correcto |
| PF-10 | Transferir mascota | Nuevo dueño registrado | Cambia responsable; historia se conserva | Correcto |
| PF-11 | Urgencia | Guardia con turno a las 14:00; urgencia 14:00 | Urgencia ocupa 14:00 y turno se mueve una vez a 14:30 | Correcto |
| PF-12 | Login con archivo TXT | Legajo `R001` y pass `1234` vs claves inválidas | Acceso concedido con credenciales correctas, denegado con erróneas | Correcto |
| PF-13 | Menú polimórfico por rol | Recepcionista, Veterinario y Administrador | Cada rol recibe e inicializa su menú específico y aislado | Correcto |
| PF-14 | Validación de fecha con reintento | `31/02/2026` seguido de `15/08/2026` | Rechaza fecha inexistente y acepta la válida sin abortar | Correcto |
| PF-15 | Ingreso de peso en libras | `20.0` lb (opción 2) | Conversión exacta a kg (`9.0718474 kg`) almacenada en dominio | Correcto |
| PF-16 | Veterinario sin turnos asignados | Veterinario sin turnos programados en agenda | Detecta ausencia de turnos e impide iniciar consulta | Correcto |
| PF-17 | Alta de usuario por Administrador | Nuevo recepcionista en memoria y en `usuarios.txt` | Registrado en sistema y autenticable en archivo de forma atómica | Correcto |

## Pruebas unitarias complementarias

Se ejecutaron 35 verificaciones de lógica aislada: fechas bisiestas, día de semana, suma de minutos, descuentos, FIFO, superposición, confirmación, stock, faltantes, autenticación en TXT, creación por defecto, cambio de contraseña, rechazo de duplicados, validación de caracteres prohibidos, conversión lb/kg y reintentos de lectura en consola. Resultado: **35 correctas y 0 fallidas**.

## Evidencia

- `resultados_pruebas_unitarias.txt`
- `resultados_pruebas_funcionales.txt`
- `smoke_consola.txt`
