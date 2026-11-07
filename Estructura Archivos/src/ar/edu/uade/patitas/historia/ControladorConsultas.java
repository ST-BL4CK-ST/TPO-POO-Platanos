package ar.edu.uade.patitas.historia;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.facturacion.ControladorFacturacion;
import ar.edu.uade.patitas.facturacion.Factura;
import ar.edu.uade.patitas.personas.PersonalClinica;
import ar.edu.uade.patitas.personas.Veterinario;
import ar.edu.uade.patitas.stock.IServicioStock;
import ar.edu.uade.patitas.stock.Producto;
import ar.edu.uade.patitas.tiempo.Fecha;
import ar.edu.uade.patitas.turnos.EstadoTurno;
import ar.edu.uade.patitas.turnos.Turno;
import java.io.Serializable;

public class ControladorConsultas implements Serializable {
    private IServicioStock servicioStock;
    private ControladorFacturacion controladorFacturacion;

    public ControladorConsultas(IServicioStock servicioStock, ControladorFacturacion controladorFacturacion) {
        this.servicioStock = servicioStock;
        this.controladorFacturacion = controladorFacturacion;
    }

    public Consulta crearConsulta(PersonalClinica usuario, Turno turno) throws ReglaNegocioException {
        Veterinario veterinario = validarVeterinario(usuario);
        if (turno.getEstado() != EstadoTurno.PROGRAMADO) throw new ReglaNegocioException("El turno no esta programado");
        if (turno.getVeterinario() != veterinario) throw new ReglaNegocioException("El turno corresponde a otro veterinario");
        return new Consulta(turno, veterinario);
    }

    public void registrarDatosClinicos(PersonalClinica usuario, Consulta consulta, String motivo,
                                       double peso, String diagnostico, String observaciones)
            throws ReglaNegocioException {
        validarVeterinario(usuario);
        consulta.registrarDatos(motivo, peso, diagnostico, observaciones);
        consulta.getTurno().getMascota().actualizarPeso(peso);
    }

    public void agregarTratamiento(PersonalClinica usuario, Consulta consulta, Producto producto,
                                   String dosis, String frecuencia, String duracion, int cantidad)
            throws ReglaNegocioException {
        validarVeterinario(usuario);
        if (consulta.getPesoDelDia() <= 0) throw new ReglaNegocioException("Debe registrarse el peso antes del tratamiento");
        consulta.agregarTratamiento(new Tratamiento(producto, dosis, frecuencia, duracion, cantidad));
    }

    public void aplicarVacuna(PersonalClinica usuario, Consulta consulta, VacunaDefinicion definicion)
            throws ReglaNegocioException {
        validarVeterinario(usuario);
        Fecha fechaAplicacion = consulta.getTurno().getFechaHora().getFecha();
        if (!definicion.aplicaA(consulta.getTurno().getMascota().getEspecie())) {
            throw new ReglaNegocioException("La vacuna no corresponde a la especie");
        }
        int edad = consulta.getTurno().getMascota().getEdadEnSemanas(fechaAplicacion);
        if (edad < definicion.getEdadMinimaSemanas()) throw new ReglaNegocioException("La mascota no alcanza la edad minima de vacunacion");
        consulta.agregarVacuna(new VacunaAplicada(definicion, fechaAplicacion));
    }

    public Factura confirmarConsulta(PersonalClinica usuario, Consulta consulta) throws ReglaNegocioException {
        validarVeterinario(usuario);
        Fecha fecha = consulta.getTurno().getFechaHora().getFecha();
        // Primero se valida y verifica todo. Si falla, no se realiza ningun cambio permanente.
        consulta.validarParaConfirmar();
        servicioStock.verificarStock(consulta, fecha);
        servicioStock.descontarStock(consulta, fecha);
        consulta.getTurno().marcarAtendido();
        consulta.confirmar();
        consulta.getTurno().getMascota().agregarConsultaConfirmada(consulta);
        return controladorFacturacion.emitirFactura(consulta);
    }

    public NotaRectificatoria registrarNotaRectificatoria(PersonalClinica usuario, Consulta consulta,
                                                           String texto, Fecha fecha)
            throws ReglaNegocioException {
        Veterinario veterinario = validarVeterinario(usuario);
        if (consulta.getVeterinario() != veterinario) {
            throw new ReglaNegocioException("La nota debe ser firmada por el veterinario autor de la consulta");
        }
        NotaRectificatoria nota = new NotaRectificatoria(texto, fecha, veterinario);
        consulta.agregarNotaRectificatoria(nota);
        return nota;
    }

    private Veterinario validarVeterinario(PersonalClinica usuario) throws ReglaNegocioException {
        if (!(usuario instanceof Veterinario)) throw new ReglaNegocioException("Operacion permitida solo a Veterinario");
        return (Veterinario) usuario;
    }
}
