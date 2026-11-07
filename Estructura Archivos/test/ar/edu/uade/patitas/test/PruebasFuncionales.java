package ar.edu.uade.patitas.test;

import ar.edu.uade.patitas.app.ConsolaHelper;
import ar.edu.uade.patitas.app.MenuAdministrador;
import ar.edu.uade.patitas.app.MenuRecepcionista;
import ar.edu.uade.patitas.app.MenuVeterinario;
import ar.edu.uade.patitas.app.SistemaPatitas;
import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.facturacion.EstadoFactura;
import ar.edu.uade.patitas.facturacion.Factura;
import ar.edu.uade.patitas.facturacion.MedioPago;
import ar.edu.uade.patitas.historia.Consulta;
import ar.edu.uade.patitas.historia.VacunaDefinicion;
import ar.edu.uade.patitas.mascotas.Perro;
import ar.edu.uade.patitas.mascotas.Sexo;
import ar.edu.uade.patitas.mascotas.TamanoPerro;
import ar.edu.uade.patitas.notificaciones.ServicioNotificaciones;
import ar.edu.uade.patitas.personas.Administrador;
import ar.edu.uade.patitas.personas.Duenio;
import ar.edu.uade.patitas.personas.Recepcionista;
import ar.edu.uade.patitas.personas.Veterinario;
import ar.edu.uade.patitas.persistencia.RepositorioUsuarios;
import ar.edu.uade.patitas.stock.Lote;
import ar.edu.uade.patitas.stock.Producto;
import ar.edu.uade.patitas.tiempo.DiaSemana;
import ar.edu.uade.patitas.tiempo.Fecha;
import ar.edu.uade.patitas.tiempo.FechaHora;
import ar.edu.uade.patitas.tiempo.Hora;
import ar.edu.uade.patitas.tiempo.TramoHorario;
import ar.edu.uade.patitas.turnos.ConsultaGeneral;
import ar.edu.uade.patitas.turnos.EstadoTurno;
import ar.edu.uade.patitas.turnos.Turno;
import java.io.File;
import java.util.List;
import java.util.Scanner;

public class PruebasFuncionales {
    private static int ok = 0;
    private static int error = 0;

    public static void main(String[] args) {
        try {
            SistemaPatitas s = new SistemaPatitas();
            Recepcionista r = new Recepcionista("R", "Uno", "10", "111", "R1");
            Administrador a = new Administrador("A", "Uno", "11", "222", "A1");
            Veterinario v = new Veterinario("V", "Uno", "12", "333", "V1", "MP1", "General");
            v.configurarHorario(DiaSemana.LUNES, new TramoHorario(new Hora(9,0), new Hora(19,0)));
            s.registrarPersonal(r); s.registrarPersonal(a); s.registrarPersonal(v);

            Duenio d = new Duenio("D", "Uno", "20", "444", "d@correo.com");
            d.setPlanSaludAnimal(true);
            s.registrarDuenio(d);
            Perro m = new Perro("M1", "Toby", "Mestizo", Sexo.MACHO, new Fecha(1,1,2024), 10, d, "C1", TamanoPerro.MEDIANO);
            s.registrarMascota(m);
            caso("PF-01 alta duenio y mascota", s.buscarDuenio("20") == d && s.buscarMascota("M1") == m);

            Producto med = new Producto("P1", "Medicamento", 1, "Prov", 100);
            med.agregarLote(new Lote("L1", 5, new Fecha(30,11,2026)), new Fecha(1,11,2026));
            s.getServicioStock().agregarProducto(med);
            Producto vacProd = new Producto("P2", "Vacuna X", 1, "Prov", 200);
            vacProd.agregarLote(new Lote("LV1", 3, new Fecha(30,12,2026)), new Fecha(1,11,2026));
            s.getServicioStock().agregarProducto(vacProd);
            VacunaDefinicion vacuna = new VacunaDefinicion("Vacuna X", 10, 7, vacProd);
            vacuna.agregarEspecie("Perro"); s.registrarVacuna(vacuna);

            FechaHora ahora = new FechaHora(new Fecha(1,11,2026), new Hora(10,0));
            Turno t = s.getControladorTurnos().asignarTurno(r, m, v,
                    new FechaHora(new Fecha(2,11,2026), new Hora(10,0)), new ConsultaGeneral(1000), ahora);
            caso("PF-02 asignar turno valido", t.getEstado() == EstadoTurno.PROGRAMADO);

            boolean superpuesto = false;
            try {
                s.getControladorTurnos().asignarTurno(r, m, v,
                        new FechaHora(new Fecha(2,11,2026), new Hora(10,15)), new ConsultaGeneral(1000), ahora);
            } catch (ReglaNegocioException e) { superpuesto = true; }
            caso("PF-03 rechazar superposicion", superpuesto);

            Turno tarde = s.getControladorTurnos().asignarTurno(r, m, v,
                    new FechaHora(new Fecha(2,11,2026), new Hora(12,0)), new ConsultaGeneral(1000), ahora);
            s.getControladorTurnos().cancelarTurno(r, tarde,
                    new FechaHora(new Fecha(2,11,2026), new Hora(11,0)));
            caso("PF-04 cancelacion tardia cobra 20%", tarde.getEstado() == EstadoTurno.CANCELADO && tarde.getCargoCancelacion() == 200.0);

            Consulta c = s.getControladorConsultas().crearConsulta(v, t);
            s.getControladorConsultas().registrarDatosClinicos(v, c, "Control", 11, "Dx", "Obs");
            s.getControladorConsultas().agregarTratamiento(v, c, med, "1", "12h", "2 dias", 2);
            s.getControladorConsultas().aplicarVacuna(v, c, vacuna);
            Factura f = s.getControladorConsultas().confirmarConsulta(v, c);
            caso("PF-05 confirmar consulta e historia", c.estaConfirmada() && m.getHistoriaClinica().size() == 1 && t.getEstado() == EstadoTurno.ATENDIDO);
            caso("PF-06 stock se descuenta al confirmar", med.getStockTotal(new Fecha(2,11,2026)) == 3 && vacProd.getStockTotal(new Fecha(2,11,2026)) == 2);

            s.getControladorFacturacion().registrarPago(r, f, MedioPago.CREDITO, 3);
            caso("PF-07 plan 15% + credito 8%", f.getEstado() == EstadoFactura.EMITIDA && f.getTotal() == 1285.2);

            s.getControladorFacturacion().anularFactura(a, f, "Error de facturacion", new Fecha(3,11,2026));
            caso("PF-08 anular con nota de credito", f.getEstado() == EstadoFactura.ANULADA);

            List<String> mensajes = s.getServicioRecordatorios().emitirRecordatorios(s.getMascotas(), new Fecha(5,11,2026));
            caso("PF-09 recordatorio dentro de 7 dias", !mensajes.isEmpty());

            Duenio nuevo = new Duenio("D", "Dos", "21", "555", "d2@correo.com");
            s.registrarDuenio(nuevo);
            m.transferir(nuevo, new Fecha(4,11,2026));
            caso("PF-10 transferencia conserva historia", m.getDuenio() == nuevo && m.getHistoriaClinica().size() == 1);

            Turno existente = s.getControladorTurnos().asignarTurno(r, m, v,
                    new FechaHora(new Fecha(2,11,2026), new Hora(14,0)), new ConsultaGeneral(1000), ahora);
            s.getControladorTurnos().asignarGuardia(a, new Fecha(2,11,2026), v);
            Turno urgencia = s.getControladorTurnos().asignarUrgencia(r, m,
                    new FechaHora(new Fecha(2,11,2026), new Hora(14,0)), new ConsultaGeneral(1000));
            caso("PF-11 urgencia desplaza un turno", urgencia.esUrgencia() && existente.getFechaHora().toString().equals("02/11/2026 14:30"));

            // PF-12 Login valido e invalido
            String rutaUsuariosPf = "data/pf_usuarios.txt";
            File archivoUsuariosPf = new File(rutaUsuariosPf);
            if (archivoUsuariosPf.exists()) archivoUsuariosPf.delete();
            RepositorioUsuarios repoPf = new RepositorioUsuarios(rutaUsuariosPf);
            repoPf.inicializarSiNoExiste();
            boolean loginValido = repoPf.validarCredenciales("R001", "1234");
            boolean loginPassInvalida = repoPf.validarCredenciales("R001", "claveErronea");
            boolean loginUsuarioInvalido = repoPf.validarCredenciales("NO_EXISTE", "1234");
            caso("PF-12 login valido e invalido", loginValido && !loginPassInvalida && !loginUsuarioInvalido);

            // PF-13 Menu por rol
            Scanner scVacio = new Scanner("");
            ServicioNotificaciones notif = new ServicioNotificaciones("data/notificaciones.log");
            MenuRecepcionista mr = new MenuRecepcionista(scVacio, s, r, repoPf, notif);
            MenuVeterinario mv = new MenuVeterinario(scVacio, s, v, repoPf, notif);
            MenuAdministrador ma = new MenuAdministrador(scVacio, s, a, repoPf, notif);
            caso("PF-13 menu por rol", mr != null && mv != null && ma != null);

            // PF-14 Fecha invalida con reintento
            Scanner scFecha = new Scanner("31/02/2026\n15/08/2026\n");
            Fecha fechaReintentada = ConsolaHelper.leerFecha(scFecha, "Fecha: ");
            caso("PF-14 fecha invalida con reintento", fechaReintentada.equals(new Fecha(15, 8, 2026)));

            // PF-15 Ingreso y conversion de peso en lb
            Scanner scPeso = new Scanner("20.0\n2\n");
            double pesoConvertido = ConsolaHelper.leerPeso(scPeso, "Peso: ");
            caso("PF-15 peso en lb", Math.abs(pesoConvertido - (20.0 * 0.45359237)) < 0.0001);

            // PF-16 Veterinario sin turnos no opera consultas
            Veterinario vSinTurnos = new Veterinario("VetSin", "Turnos", "88", "888", "V99", "MP99", "General");
            s.registrarPersonal(vSinTurnos);
            boolean tieneProgramados = false;
            for (Turno tV : vSinTurnos.getAgenda().getTurnosOrdenados()) {
                if (tV.getEstado() == EstadoTurno.PROGRAMADO) {
                    tieneProgramados = true;
                    break;
                }
            }
            caso("PF-16 veterinario sin turnos", !tieneProgramados);

            // PF-17 Alta de usuario por Administrador
            Recepcionista rNuevo = new Recepcionista("Maria", "Lopez", "77", "777", "R002");
            s.registrarPersonal(rNuevo);
            repoPf.registrarUsuario("R002", "passR002");
            boolean rNuevoEnSistema = s.buscarPersonalPorLegajo("R002") != null;
            boolean rNuevoEnRepo = repoPf.validarCredenciales("R002", "passR002");
            caso("PF-17 alta de usuario por Administrador", rNuevoEnSistema && rNuevoEnRepo);

            if (archivoUsuariosPf.exists()) archivoUsuariosPf.delete();

        } catch (Exception e) {
            System.out.println("ERROR GENERAL: " + e.getMessage());
            error++;
        }
        System.out.println("RESULTADO FUNCIONAL: " + ok + " correctas, " + error + " fallidas");
    }

    private static void caso(String nombre, boolean condicion) {
        if (condicion) { ok++; System.out.println("OK - " + nombre); }
        else { error++; System.out.println("FALLO - " + nombre); }
    }
}
