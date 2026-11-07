package ar.edu.uade.patitas.app;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.facturacion.Factura;
import ar.edu.uade.patitas.facturacion.MedioPago;
import ar.edu.uade.patitas.mascotas.Ave;
import ar.edu.uade.patitas.mascotas.Conejo;
import ar.edu.uade.patitas.mascotas.Gato;
import ar.edu.uade.patitas.mascotas.Mascota;
import ar.edu.uade.patitas.mascotas.Perro;
import ar.edu.uade.patitas.mascotas.Sexo;
import ar.edu.uade.patitas.mascotas.TamanoPerro;
import ar.edu.uade.patitas.mascotas.TipoPelaje;
import ar.edu.uade.patitas.notificaciones.ServicioNotificaciones;
import ar.edu.uade.patitas.personas.Duenio;
import ar.edu.uade.patitas.personas.PersonalClinica;
import ar.edu.uade.patitas.personas.Recepcionista;
import ar.edu.uade.patitas.personas.Veterinario;
import ar.edu.uade.patitas.persistencia.RepositorioUsuarios;
import ar.edu.uade.patitas.tiempo.Fecha;
import ar.edu.uade.patitas.tiempo.FechaHora;
import ar.edu.uade.patitas.turnos.Atencion;
import ar.edu.uade.patitas.turnos.ConsultaGeneral;
import ar.edu.uade.patitas.turnos.Turno;
import java.io.IOException;
import java.util.Scanner;

public class MenuRecepcionista extends MenuRol {

    public MenuRecepcionista(Scanner scanner, SistemaPatitas sistema, PersonalClinica usuarioActivo,
                             RepositorioUsuarios repositorioUsuarios, ServicioNotificaciones notificaciones) {
        super(scanner, sistema, usuarioActivo, repositorioUsuarios, notificaciones);
    }

    @Override
    public void mostrarMenu() {
        mostrarEncabezado("Recepcionista");
        System.out.println("1. Registrar duenio");
        System.out.println("2. Registrar mascota");
        System.out.println("3. Transferir mascota");
        System.out.println("4. Asignar turno");
        System.out.println("5. Reprogramar / cancelar / ausente");
        System.out.println("6. Turno de urgencia");
        System.out.println("7. Cobrar factura pendiente");
        System.out.println("8. Cobrar sena pendiente");
        System.out.println("9. Listar turnos");
        System.out.println("10. Cerrar sesion");
        System.out.println("0. Guardar y salir");
    }

    @Override
    public AccionMenu procesarOpcion(int opcion) throws ReglaNegocioException, IOException {
        switch (opcion) {
            case 1: registrarDuenio(); return AccionMenu.CONTINUAR;
            case 2: registrarMascota(); return AccionMenu.CONTINUAR;
            case 3: transferirMascota(); return AccionMenu.CONTINUAR;
            case 4: asignarTurno(); return AccionMenu.CONTINUAR;
            case 5: gestionarTurno(); return AccionMenu.CONTINUAR;
            case 6: asignarTurnoUrgencia(); return AccionMenu.CONTINUAR;
            case 7: cobrarFactura(); return AccionMenu.CONTINUAR;
            case 8: cobrarSenaPendiente(); return AccionMenu.CONTINUAR;
            case 9: listarTurnos(); return AccionMenu.CONTINUAR;
            case 10: return AccionMenu.CERRAR_SESION;
            case 0: return AccionMenu.SALIR;
            default:
                System.out.println("Opcion invalida.");
                return AccionMenu.CONTINUAR;
        }
    }

    private void exigirRecepcionista() throws ReglaNegocioException {
        if (!(usuarioActivo instanceof Recepcionista)) {
            throw new ReglaNegocioException("Debe ingresar como Recepcionista");
        }
    }

    private void registrarDuenio() throws ReglaNegocioException {
        exigirRecepcionista();
        String nombre = ConsolaHelper.leerTextoNoVacio(scanner, "Nombre: ");
        String apellido = ConsolaHelper.leerTextoNoVacio(scanner, "Apellido: ");
        String documento = ConsolaHelper.leerTextoNoVacio(scanner, "Documento: ");
        String telefono = ConsolaHelper.leerTextoNoVacio(scanner, "Telefono: ");
        String correo = ConsolaHelper.leerTextoNoVacio(scanner, "Correo: ");
        Duenio duenio = new Duenio(nombre, apellido, documento, telefono, correo);
        boolean tienePlan = ConsolaHelper.leerSiNo(scanner, "Plan de salud? (s/n): ");
        duenio.setPlanSaludAnimal(tienePlan);
        sistema.registrarDuenio(duenio);
        System.out.println("Duenio registrado.");
    }

    private void registrarMascota() throws ReglaNegocioException {
        exigirRecepcionista();
        Duenio duenio = exigirDuenio();
        String id = ConsolaHelper.leerTextoNoVacio(scanner, "ID mascota: ");
        String nombre = ConsolaHelper.leerTextoNoVacio(scanner, "Nombre: ");

        // Orden de carga: duenio, ID, nombre, especie, raza, sexo, fecha de nacimiento, peso, datos de especie
        int especie = ConsolaHelper.leerOpcion(scanner, "Especie 1=Perro 2=Gato 3=Ave 4=Conejo: ", 1, 4);
        String raza = ConsolaHelper.leerTextoNoVacio(scanner, "Raza: ");
        Sexo sexo = leerSexo();
        Fecha nacimiento = ConsolaHelper.leerFecha(scanner, "Fecha nacimiento dd/mm/aaaa: ");
        Fecha fechaRegistro = ConsolaHelper.leerFecha(scanner, "Fecha actual dd/mm/aaaa: ");
        if (nacimiento.compareTo(fechaRegistro) > 0) {
            throw new ReglaNegocioException("La fecha de nacimiento no puede ser futura");
        }

        // Peso con conversion y validacion > 0 (supuesto: se almacena en kg)
        double peso = ConsolaHelper.leerPeso(scanner, "Peso: ");

        Mascota mascota;
        if (especie == 1) {
            String chip = ConsolaHelper.leerTextoNoVacio(scanner, "Numero de chip: ");
            int tam = ConsolaHelper.leerOpcion(scanner, "Tamano 1=pequenio 2=mediano 3=grande: ", 1, 3);
            TamanoPerro tamano = tam == 1 ? TamanoPerro.PEQUENIO : (tam == 3 ? TamanoPerro.GRANDE : TamanoPerro.MEDIANO);
            mascota = new Perro(id, nombre, raza, sexo, nacimiento, peso, duenio, chip, tamano);
        } else if (especie == 2) {
            boolean interior = ConsolaHelper.leerSiNo(scanner, "Vive en interior? s/n: ");
            mascota = new Gato(id, nombre, raza, sexo, nacimiento, peso, duenio, interior);
        } else if (especie == 3) {
            boolean vuela = ConsolaHelper.leerSiNo(scanner, "Puede volar? s/n: ");
            mascota = new Ave(id, nombre, raza, sexo, nacimiento, peso, duenio, vuela);
        } else {
            int pelaje = ConsolaHelper.leerOpcion(scanner, "Pelaje 1=corto 2=largo: ", 1, 2);
            mascota = new Conejo(id, nombre, raza, sexo, nacimiento, peso, duenio,
                    pelaje == 2 ? TipoPelaje.LARGO : TipoPelaje.CORTO);
        }
        sistema.registrarMascota(mascota);
        System.out.println("Mascota registrada.");
    }

    private void transferirMascota() throws ReglaNegocioException {
        exigirRecepcionista();
        Mascota mascota = exigirMascota();
        Duenio nuevo = sistema.buscarDuenio(ConsolaHelper.leerTexto(scanner, "Documento nuevo duenio: "));
        if (nuevo == null) throw new ReglaNegocioException("Nuevo duenio inexistente");
        Fecha fechaTransferencia = ConsolaHelper.leerFecha(scanner, "Fecha transferencia dd/mm/aaaa: ");
        mascota.transferir(nuevo, fechaTransferencia);
        System.out.println("Transferencia registrada; historia clinica conservada.");
    }

    private void asignarTurno() throws ReglaNegocioException {
        exigirRecepcionista();
        Mascota mascota = exigirMascota();
        Veterinario veterinario = exigirVeterinario();
        FechaHora fechaHora = ConsolaHelper.leerFechaHora(scanner);
        Atencion atencion = leerAtencion();
        FechaHora momentoSolicitud = ConsolaHelper.leerFechaHoraConEtiqueta(scanner, "Momento actual de la solicitud");
        Turno turno = sistema.getControladorTurnos().asignarTurno(usuarioActivo, mascota, veterinario, fechaHora, atencion, momentoSolicitud);
        System.out.println("Turno asignado: " + turno);
    }

    private void gestionarTurno() throws ReglaNegocioException {
        exigirRecepcionista();
        Turno turno = exigirTurno();
        int op = ConsolaHelper.leerOpcion(scanner, "1=Reprogramar 2=Cancelar 3=Marcar ausente: ", 1, 3);
        if (op == 1) {
            sistema.getControladorTurnos().reprogramarTurno(usuarioActivo, turno, ConsolaHelper.leerFechaHora(scanner));
            System.out.println("Turno reprogramado.");
        } else if (op == 2) {
            FechaHora momento = ConsolaHelper.leerFechaHoraConEtiqueta(scanner, "Momento de cancelacion");
            sistema.getControladorTurnos().cancelarTurno(usuarioActivo, turno, momento);
            System.out.println("Turno cancelado. Cargo: $" + turno.getCargoCancelacion());
        } else if (op == 3) {
            sistema.getControladorTurnos().marcarAusente(usuarioActivo, turno);
            System.out.println("Ausencia registrada. Ausencias consecutivas: " + turno.getMascota().getDuenio().getAusenciasConsecutivas());
        }
    }

    private void asignarTurnoUrgencia() throws ReglaNegocioException {
        exigirRecepcionista();
        Mascota mascota = exigirMascota();
        FechaHora fechaHora = ConsolaHelper.leerFechaHora(scanner);
        Turno turno = sistema.getControladorTurnos().asignarUrgencia(usuarioActivo, mascota, fechaHora, new ConsultaGeneral(20000));
        System.out.println("Urgencia asignada: " + turno);
        Turno desplazado = sistema.getControladorTurnos().getUltimoTurnoDesplazado();
        if (desplazado != null) {
            notificaciones.notificar(desplazado.getMascota().getDuenio(),
                    "Su turno fue reprogramado por una urgencia para " + desplazado.getFechaHora());
        }
    }

    private void cobrarFactura() throws ReglaNegocioException {
        exigirRecepcionista();
        Factura factura = exigirFactura();
        int medio = ConsolaHelper.leerOpcion(scanner, "Medio 1=Efectivo 2=Debito 3=Credito: ", 1, 3);
        MedioPago medioPago = medio == 1 ? MedioPago.EFECTIVO : (medio == 3 ? MedioPago.CREDITO : MedioPago.DEBITO);
        int cuotas = medioPago == MedioPago.CREDITO ? ConsolaHelper.leerEnteroPositivo(scanner, "Cuotas: ") : 1;
        sistema.getControladorFacturacion().registrarPago(usuarioActivo, factura, medioPago, cuotas);
        System.out.println("Pago registrado. Descuento=$" + factura.getDescuento() + " recargo=$" + factura.getRecargo() + " total=$" + factura.getTotal());
    }

    private void cobrarSenaPendiente() throws ReglaNegocioException {
        exigirRecepcionista();
        Duenio duenio = exigirDuenio();
        if (!duenio.requiereSena()) {
            System.out.println("El duenio " + duenio.getNombreCompleto() + " no adeuda sena.");
            return;
        }
        sistema.getControladorTurnos().registrarPagoSena(usuarioActivo, duenio);
        System.out.println("Cobro de sena registrado correctamente para " + duenio.getNombreCompleto() +
                ". Ya puede solicitar turnos nuevamente.");
    }

    private void listarTurnos() {
        for (PersonalClinica persona : sistema.getPersonal()) {
            if (persona instanceof Veterinario) {
                Veterinario veterinario = (Veterinario) persona;
                System.out.println("-- " + veterinario.getNombreCompleto() + " --");
                for (Turno turno : veterinario.getAgenda().getTurnosOrdenados()) {
                    System.out.println(turno);
                }
            }
        }
    }
}
