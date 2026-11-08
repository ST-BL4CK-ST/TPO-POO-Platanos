package ar.edu.uade.patitas.app;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.facturacion.Factura;
import ar.edu.uade.patitas.historia.Consulta;
import ar.edu.uade.patitas.historia.NotaRectificatoria;
import ar.edu.uade.patitas.historia.Tratamiento;
import ar.edu.uade.patitas.historia.VacunaAplicada;
import ar.edu.uade.patitas.historia.VacunaDefinicion;
import ar.edu.uade.patitas.mascotas.Mascota;
import ar.edu.uade.patitas.notificaciones.ServicioNotificaciones;
import ar.edu.uade.patitas.personas.PersonalClinica;
import ar.edu.uade.patitas.personas.Veterinario;
import ar.edu.uade.patitas.persistencia.RepositorioUsuarios;
import ar.edu.uade.patitas.stock.Producto;
import ar.edu.uade.patitas.tiempo.Fecha;
import ar.edu.uade.patitas.turnos.EstadoTurno;
import ar.edu.uade.patitas.turnos.Turno;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuVeterinario extends MenuRol {

    public MenuVeterinario(Scanner scanner, SistemaPatitas sistema, PersonalClinica usuarioActivo,
                           RepositorioUsuarios repositorioUsuarios, ServicioNotificaciones notificaciones) {
        super(scanner, sistema, usuarioActivo, repositorioUsuarios, notificaciones);
    }

    @Override
    public void mostrarMenu() {
        mostrarEncabezado("Veterinario");
        System.out.println("1. Registrar y confirmar consulta");
        System.out.println("2. Consultar historia clinica");
        System.out.println("3. Registrar nota rectificatoria");
        System.out.println("4. Ver mis turnos");
        System.out.println("5. Cerrar sesion");
        System.out.println("0. Guardar y salir");
    }

    @Override
    public AccionMenu procesarOpcion(int opcion) throws ReglaNegocioException, IOException {
        switch (opcion) {
            case 1: registrarConsulta(); return AccionMenu.CONTINUAR;
            case 2: mostrarHistoriaClinica(); return AccionMenu.CONTINUAR;
            case 3: registrarNotaRectificatoria(); return AccionMenu.CONTINUAR;
            case 4: verMisTurnos(); return AccionMenu.CONTINUAR;
            case 5: return AccionMenu.CERRAR_SESION;
            case 0: return AccionMenu.SALIR;
            default:
                System.out.println("Opcion invalida.");
                return AccionMenu.CONTINUAR;
        }
    }

    private void exigirVeterinarioLogueado() throws ReglaNegocioException {
        if (!(usuarioActivo instanceof Veterinario)) {
            throw new ReglaNegocioException("Debe ingresar como Veterinario");
        }
    }

    private void registrarConsulta() throws ReglaNegocioException {
        exigirVeterinarioLogueado();
        Veterinario veterinario = (Veterinario) usuarioActivo;

        // Buscar en la agenda del veterinario logueado turnos en estado programado
        List<Turno> programados = new ArrayList<Turno>();
        for (Turno t : veterinario.getAgenda().getTurnosOrdenados()) {
            if (t.getEstado() == EstadoTurno.PROGRAMADO) {
                programados.add(t);
            }
        }

        // Si no hay ninguno, mostrar mensaje exacto y volver al menú sin pedir datos ni crear consulta
        if (programados.isEmpty()) {
            System.out.println("No hay ningun turno asignado");
            return;
        }

        System.out.println("Turnos programados asignados:");
        for (Turno t : programados) {
            System.out.println(t.getId() + " | " + t.getFechaHora() + " | Mascota: " +
                    t.getMascota().getNombre() + " | Atencion: " + t.getAtencion().getNombre());
        }

        // Pedir el ID solo entre los suyos
        Turno turno = null;
        while (turno == null) {
            String idTurno = ConsolaHelper.leerTextoNoVacio(scanner, "ID turno: ");
            for (Turno t : programados) {
                if (t.getId().equalsIgnoreCase(idTurno)) {
                    turno = t;
                    break;
                }
            }
            if (turno == null) {
                System.out.println("ID invalido. Debe seleccionar uno de sus turnos programados.");
            }
        }

        Consulta consulta = sistema.getControladorConsultas().crearConsulta(usuarioActivo, turno);
        String motivo = ConsolaHelper.leerTextoNoVacio(scanner, "Motivo: ");
        double pesoDelDia = ConsolaHelper.leerPeso(scanner, "Peso del dia: ");
        String diagnostico = ConsolaHelper.leerTextoNoVacio(scanner, "Diagnostico: ");
        String observaciones = ConsolaHelper.leerTexto(scanner, "Observaciones: ");

        sistema.getControladorConsultas().registrarDatosClinicos(usuarioActivo, consulta,
                motivo, pesoDelDia, diagnostico, observaciones);

        while (ConsolaHelper.leerSiNo(scanner, "Agregar tratamiento? s/n: ")) {
            Producto producto = exigirProducto();
            String dosis = ConsolaHelper.leerTextoNoVacio(scanner, "Dosis: ");
            String frecuencia = ConsolaHelper.leerTextoNoVacio(scanner, "Frecuencia: ");
            String duracion = ConsolaHelper.leerTextoNoVacio(scanner, "Duracion: ");
            int cantidad = ConsolaHelper.leerEnteroPositivo(scanner, "Cantidad a consumir: ");
            sistema.getControladorConsultas().agregarTratamiento(usuarioActivo, consulta, producto,
                    dosis, frecuencia, duracion, cantidad);
            System.out.println("Tratamiento agregado.");
        }

        while (ConsolaHelper.leerSiNo(scanner, "Aplicar vacuna? s/n: ")) {
            VacunaDefinicion vacuna = sistema.buscarVacuna(ConsolaHelper.leerTexto(scanner, "Nombre de vacuna: "));
            if (vacuna == null) throw new ReglaNegocioException("Vacuna no encontrada");
            sistema.getControladorConsultas().aplicarVacuna(usuarioActivo, consulta, vacuna);
            System.out.println("Vacuna agregada.");
        }

        Factura factura = sistema.getControladorConsultas().confirmarConsulta(usuarioActivo, consulta);
        System.out.println("Consulta confirmada. Factura pendiente: " + factura.getNumero() + " subtotal=$" + factura.getSubtotal());
    }

    private void mostrarHistoriaClinica() throws ReglaNegocioException {
        exigirVeterinarioLogueado();
        Mascota mascota = exigirMascota();
        List<Consulta> historia = mascota.getHistoriaClinica();
        if (historia.isEmpty()) {
            System.out.println("Historia clinica vacia.");
            return;
        }
        System.out.println("=== Historia Clinica de " + mascota.getNombre() + " (" + mascota.getEspecie() + ") ===");
        for (Consulta consulta : historia) {
            System.out.println("--------------------------------------------------");
            System.out.println("Fecha/Hora: " + consulta.getTurno().getFechaHora() +
                    " | Dr/a. " + consulta.getVeterinario().getNombreCompleto() +
                    " (MP: " + consulta.getVeterinario().getMatricula() + ")");
            System.out.println("Motivo: " + consulta.getMotivo() +
                    " | Diagnostico: " + consulta.getDiagnostico() +
                    " | Peso: " + consulta.getPesoDelDia() + " kg");
            if (consulta.getObservaciones() != null && !consulta.getObservaciones().isBlank()) {
                System.out.println("Observaciones: " + consulta.getObservaciones());
            }

            List<Tratamiento> tratamientos = consulta.getTratamientos();
            if (!tratamientos.isEmpty()) {
                System.out.println("  Tratamientos indicados:");
                for (Tratamiento t : tratamientos) {
                    System.out.println("    - " + t.getProducto().getNombre() +
                            ": " + t.getDosis() + " cada " + t.getFrecuencia() +
                            " por " + t.getDuracion() + " (cant: " + t.getCantidad() + ")");
                }
            }

            List<VacunaAplicada> vacunas = consulta.getVacunasAplicadas();
            if (!vacunas.isEmpty()) {
                System.out.println("  Vacunas aplicadas:");
                for (VacunaAplicada v : vacunas) {
                    System.out.println("    - " + v.getDefinicion().getNombre() +
                            " | Lote: " + (v.getLote() != null ? v.getLote() : "N/A") +
                            " | Aplicada: " + v.getFechaAplicacion() +
                            " | Proximo refuerzo: " + v.getFechaProximoRefuerzo());
                }
            }

            List<NotaRectificatoria> notas = consulta.getNotasRectificatorias();
            if (!notas.isEmpty()) {
                System.out.println("  Notas rectificatorias asociadas:");
                for (NotaRectificatoria n : notas) {
                    System.out.println("    * [" + n.getFecha() + " - Dr/a. " +
                            n.getVeterinarioFirmante().getNombreCompleto() + "]: " + n.getTexto());
                }
            }
        }
        System.out.println("--------------------------------------------------");
    }

    private void registrarNotaRectificatoria() throws ReglaNegocioException {
        exigirVeterinarioLogueado();
        Mascota mascota = exigirMascota();
        List<Consulta> historia = mascota.getHistoriaClinica();
        if (historia.isEmpty()) {
            System.out.println("No hay consultas confirmadas para esta mascota.");
            return;
        }
        System.out.println("Consultas confirmadas de " + mascota.getNombre() + ":");
        for (int i = 0; i < historia.size(); i++) {
            Consulta c = historia.get(i);
            System.out.println((i + 1) + ". " + c.getTurno().getFechaHora() + " | Dr/a. " +
                    c.getVeterinario().getNombreCompleto() + " | " + c.getDiagnostico());
        }
        int seleccion = ConsolaHelper.leerOpcion(scanner, "Seleccione consulta a rectificar (1-" + historia.size() + "): ", 1, historia.size());
        Consulta consulta = historia.get(seleccion - 1);

        String texto = ConsolaHelper.leerTextoNoVacio(scanner, "Texto rectificatorio: ");
        Fecha fecha = ConsolaHelper.leerFecha(scanner, "Fecha de la nota dd/mm/aaaa: ");
        sistema.getControladorConsultas().registrarNotaRectificatoria(usuarioActivo, consulta, texto, fecha);
        System.out.println("Nota rectificatoria registrada correctamente. El registro original permanece intacto.");
    }

    private void verMisTurnos() throws ReglaNegocioException {
        exigirVeterinarioLogueado();
        Veterinario veterinario = (Veterinario) usuarioActivo;
        List<Turno> misTurnos = veterinario.getAgenda().getTurnosOrdenados();
        if (misTurnos.isEmpty()) {
            System.out.println("No hay ningun turno asignado");
            return;
        }
        System.out.println("-- Mis Turnos (" + veterinario.getNombreCompleto() + ") --");
        for (Turno turno : misTurnos) {
            System.out.println(turno);
        }
    }
}
