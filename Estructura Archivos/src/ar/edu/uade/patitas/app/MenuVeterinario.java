package ar.edu.uade.patitas.app;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.facturacion.Factura;
import ar.edu.uade.patitas.historia.Consulta;
import ar.edu.uade.patitas.historia.VacunaDefinicion;
import ar.edu.uade.patitas.mascotas.Mascota;
import ar.edu.uade.patitas.notificaciones.ServicioNotificaciones;
import ar.edu.uade.patitas.personas.PersonalClinica;
import ar.edu.uade.patitas.personas.Veterinario;
import ar.edu.uade.patitas.persistencia.RepositorioUsuarios;
import ar.edu.uade.patitas.stock.Producto;
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
        System.out.println("3. Ver mis turnos");
        System.out.println("4. Cerrar sesion");
        System.out.println("0. Guardar y salir");
    }

    @Override
    public AccionMenu procesarOpcion(int opcion) throws ReglaNegocioException, IOException {
        switch (opcion) {
            case 1: registrarConsulta(); return AccionMenu.CONTINUAR;
            case 2: mostrarHistoriaClinica(); return AccionMenu.CONTINUAR;
            case 3: verMisTurnos(); return AccionMenu.CONTINUAR;
            case 4: return AccionMenu.CERRAR_SESION;
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

        if (ConsolaHelper.leerSiNo(scanner, "Agregar tratamiento? s/n: ")) {
            Producto producto = exigirProducto();
            String dosis = ConsolaHelper.leerTextoNoVacio(scanner, "Dosis: ");
            String frecuencia = ConsolaHelper.leerTextoNoVacio(scanner, "Frecuencia: ");
            String duracion = ConsolaHelper.leerTextoNoVacio(scanner, "Duracion: ");
            int cantidad = ConsolaHelper.leerEnteroPositivo(scanner, "Cantidad a consumir: ");
            sistema.getControladorConsultas().agregarTratamiento(usuarioActivo, consulta, producto,
                    dosis, frecuencia, duracion, cantidad);
        }

        if (ConsolaHelper.leerSiNo(scanner, "Aplicar vacuna? s/n: ")) {
            VacunaDefinicion vacuna = sistema.buscarVacuna(ConsolaHelper.leerTexto(scanner, "Nombre de vacuna: "));
            if (vacuna == null) throw new ReglaNegocioException("Vacuna no encontrada");
            sistema.getControladorConsultas().aplicarVacuna(usuarioActivo, consulta, vacuna);
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
        for (Consulta consulta : historia) {
            System.out.println(consulta.getTurno().getFechaHora() + " | " + consulta.getDiagnostico() + " | peso=" + consulta.getPesoDelDia() + " kg");
        }
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
