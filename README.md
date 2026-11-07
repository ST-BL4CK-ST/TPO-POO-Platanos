# Clínica Veterinaria "Patitas"

Sistema en Java para informatizar la atención de la clínica veterinaria "Patitas".

> Trabajo Práctico Obligatorio de Paradigma Orientado a Objetos (UADE) — **Propuesta B: Clínica veterinaria**.

## Integrantes

| Nombre y Apellido | Legajo  | GitHub              |
|-------------------|---------|---------------------|
| Adrian Maldonado  | 1220625 | @ADRIANOBAANANO1    |
| Santiago Coco     | 1220810 | @ST-BL4CK-ST        |
| Milena Mendez     | 1214694 | @milemendez-17      |

## Datos de la cursada

- **Materia:** Paradigma Orientado a Objetos
- **Comisión:** _Miercoles Turno Tarde_
- **Docente:** _Leon Maria Angela_
- **Entrega Etapa 1:** 25/10/2026 (tag `etapa-1`)
- **Entrega Etapa 2:** 04/11/2026 (tag `etapa-2`)
- **Defensa:** 11/11/2026
---

# Etapa 2 — Implementación en Java

> Todo el código está en la carpeta `Estructura Archivos/`. Los comandos de abajo se ejecutan desde esa carpeta (`cd "Estructura Archivos"`).

## Descripción

Aplicación de consola en Java para una clínica veterinaria. Implementa registro de dueños y mascotas, transferencia de mascotas, turnos, urgencias, consultas e historia clínica, vacunas y recordatorios, stock por lotes, facturación, pagos y notas de crédito.

La implementación parte del diagrama final de la Etapa 1. Se mantuvieron los controladores GRASP, la jerarquía polimórfica de mascotas y tipos de atención, y las abstracciones `IServicioStock` e `ICalculadorDescuento` definidas por DIP.

## Requisitos

- Java 17 o superior.
- Ejecutar los comandos desde la carpeta raíz del proyecto.
- No requiere Maven, Gradle, JUnit ni librerías externas.

## Estructura

- `src/`: código fuente Java.
- `test/`: pruebas unitarias y funcionales ejecutables sin librerías externas.
- `docs/`: plan de pruebas, resultados, informe final y UML actualizado.
- `data/`: persistencia binaria y log de notificaciones.

## Compilación

```bash
mkdir -p out
javac -encoding UTF-8 -d out $(find src -name "*.java" | sort)
```

## Ejecución

```bash
java -cp out ar.edu.uade.patitas.app.Main
```

### Autenticación y Control de Acceso

El sistema cuenta con autenticación por archivo de texto plano ubicado en `data/usuarios.txt`.
Formato de cada registro: `usuario;contrasenia` (donde el usuario corresponde al legajo del personal clínico).
Si el archivo `data/usuarios.txt` no existe al iniciar la aplicación, se crea automáticamente con las credenciales demo.

**Usuarios y contraseñas de prueba:**

| Legajo | Contraseña | Rol | Nombre |
|---|---|---|---|
| `R001` | `1234` | Recepcionista | Ana Perez |
| `V001` | `1234` | Veterinario | Carla Diaz (MP: `MP100`) |
| `A001` | `1234` | Administrador | Luis Gomez |

**Mecanismo de Login:**
- Al arrancar la aplicación se solicitan usuario (legajo) y contraseña.
- Se permiten hasta **3 intentos** fallidos; si se superan, el sistema guarda el estado y se cierra de manera segura.
- Si el usuario existe en `data/usuarios.txt` pero no tiene personal asociado en el sistema, se advierte al operador.
- Cada rol accede a un menú específico y polimórfico (`MenuRecepcionista`, `MenuVeterinario`, `MenuAdministrador`) con numeración consecutiva.
- Todos los menús disponen de la opción **Cerrar sesión**, que regresa a la pantalla de login para permitir cambiar de usuario sin reiniciar la aplicación, y de la opción **Guardar y salir**.

### Gestión de Usuarios (Exclusivo Administrador)
El Administrador cuenta con la opción "Gestionar usuarios" que permite:
1. **Crear usuario**: Alta atómica de un nuevo `PersonalClinica` (Recepcionista, Veterinario o Administrador) con validación de legajo y documento no duplicados, configuración de horario de atención por defecto (lunes a sábado de 9 a 19 para veterinarios) y registro de contraseña en `data/usuarios.txt`.
2. **Modificar usuario**: Actualización de contraseña (validando que no contenga `;` ni esté vacía) y actualización de datos de contacto (teléfono).
3. **Listar usuarios**: Visualización de los usuarios registrados y su rol en la clínica.

### Supuestos de Dominio: Unidad de Peso
- **Almacenamiento**: Todo peso dentro del dominio (mascotas y peso del día en consultas clínicas) se almacena y procesa exclusivamente en **kilogramos (kg)**.
- **Entrada por consola**: La consola permite ingresar el peso en kilogramos (`1=kg`) o en libras (`2=lb`). En caso de ingresar libras, el sistema aplica la conversión estándar: `1 lb = 0.45359237 kg`.
- Todas las salidas por pantalla e historia clínica indican explícitamente la unidad `"kg"`.

Datos de prueba principales:

- Dueño: documento `40000001`.
- Mascota: ID `M001`.
- Veterinario: matrícula `MP100`.
- Productos: `P001` (Antibiótico) y `P002` (Vacuna antirrábica).
- Vacuna: `Antirrabica`.

## Pruebas

Primero compilar el código principal y luego las pruebas:

```bash
mkdir -p out-test
javac -encoding UTF-8 -cp out -d out-test $(find test -name "*.java" | sort)
java -cp out:out-test ar.edu.uade.patitas.test.PruebasUnitarias
java -cp out:out-test ar.edu.uade.patitas.test.PruebasFuncionales
```

Resultados obtenidos en la puesta a punto incluida:

- Pruebas unitarias: **35 correctas, 0 fallidas**.
- Pruebas funcionales: **17 correctas, 0 fallidas**.
- Smoke test de consola: inicio de sesión por TXT, menú polimórfico por rol, consulta de turnos, alta de usuario y guardado: correcto.

Los resultados completos están en `docs/resultados_pruebas_unitarias.txt`, `docs/resultados_pruebas_funcionales.txt` y `docs/smoke_consola.txt`.

## Persistencia

El sistema usa serialización de objetos con `ObjectOutputStream` y `ObjectInputStream`. El archivo principal es `data/patitas.dat`. La información se carga al iniciar y se guarda al salir desde el menú.

Las notificaciones simuladas se muestran por consola y se agregan a `data/notificaciones.log` cuando corresponde.

## Decisiones de implementación respecto de la Etapa 1

La referencia de contenidos no desarrolla la API de `java.time`. Para evitar depender de métodos no cubiertos, se reemplazaron `LocalDate`, `LocalTime` y `LocalDateTime` por clases propias `Fecha`, `Hora` y `FechaHora`. Estas implementan comparación, suma de días/minutos y cálculo del día de la semana mediante lógica propia.

También se incorporaron cambios necesarios surgidos durante la codificación:

- `EstadoFactura` agrega `PENDIENTE`, porque la factura se crea al confirmar la consulta y queda emitida recién al registrar el pago.
- `Producto` agrega `precioUnitario`, requerido para construir las líneas de factura.
- `Lote` agrega `codigo`, necesario para identificar el lote consumido por una vacuna.
- `Tratamiento` agrega `cantidad`, para poder descontar y facturar unidades.
- `VacunaDefinicion` referencia al `Producto` de stock que representa la dosis.
- `IServicioStock` recibe una `Consulta` completa para verificar también vacunas y evitar descuentos parciales.
- Se agregó `ServicioNotificaciones` como fabricación pura para consola y archivo.

El detalle está en `docs/informe_final.md` y `docs/uml/diagrama_clases_etapa2.puml`.

## Reglas de negocio destacadas implementadas

- No se superponen turnos del mismo veterinario y se respetan sus horarios.
- Cancelación con menos de dos horas: cargo del 20% del arancel.
- Tres ausencias consecutivas activan la seña pendiente.
- La urgencia usa el veterinario de guardia y puede desplazar un único turno, sin cadena de desplazamientos.
- Solo el veterinario registra datos clínicos, tratamientos, vacunas y confirmaciones.
- No se indica tratamiento sin peso del día.
- La historia clínica confirmada no se modifica; las correcciones son notas rectificatorias.
- El stock se verifica antes de confirmar y se descuenta por lote con vencimiento más próximo, ignorando lotes vencidos.
- Si falta stock, no se cambia el turno, no se agrega historia y no se descuenta ninguna unidad.
- Facturación: efectivo 10%, plan 15% (se usa el mayor), crédito 8% de recargo luego del descuento.
- Una factura emitida se anula mediante nota de crédito.
- Recordatorios de vacunas y detección de vacunas vencidas según las reglas definidas.

