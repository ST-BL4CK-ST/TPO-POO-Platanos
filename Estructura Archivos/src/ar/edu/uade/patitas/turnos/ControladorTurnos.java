package ar.edu.uade.patitas.turnos;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.mascotas.Mascota;
import ar.edu.uade.patitas.personas.Administrador;
import ar.edu.uade.patitas.personas.PersonalClinica;
import ar.edu.uade.patitas.personas.Recepcionista;
import ar.edu.uade.patitas.personas.Veterinario;
import ar.edu.uade.patitas.tiempo.Fecha;
import ar.edu.uade.patitas.tiempo.FechaHora;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ControladorTurnos implements Serializable {
    private Map<String, Veterinario> guardiasPorFecha;
    private Turno ultimoTurnoDesplazado;

    public ControladorTurnos() {
        guardiasPorFecha = new HashMap<String, Veterinario>();
    }

    public Turno asignarTurno(PersonalClinica usuario, Mascota mascota, Veterinario veterinario,
                              FechaHora fechaHora, Atencion atencion) throws ReglaNegocioException {
        return asignarTurno(usuario, mascota, veterinario, fechaHora, atencion, null);
    }

    public Turno asignarTurno(PersonalClinica usuario, Mascota mascota, Veterinario veterinario,
                              FechaHora fechaHora, Atencion atencion, FechaHora momentoSolicitud)
            throws ReglaNegocioException {
        validarRecepcion(usuario);
        if (momentoSolicitud != null && fechaHora.compareTo(momentoSolicitud) < 0) {
            throw new ReglaNegocioException("No se asignan turnos en el pasado");
        }
        if (mascota.getDuenio().requiereSena()) {
            throw new ReglaNegocioException("El duenio debe pagar una sena antes de pedir un nuevo turno");
        }
        return veterinario.agendarTurno(mascota, fechaHora, atencion);
    }

    public void reprogramarTurno(PersonalClinica usuario, Turno turno, FechaHora nuevaFechaHora)
            throws ReglaNegocioException {
        validarRecepcion(usuario);
        Veterinario vet = turno.getVeterinario();
        int duracion = turno.getAtencion().getDuracionMinutos();
        if (!vet.dentroDeHorario(nuevaFechaHora, duracion) || vet.getAgenda().haySuperposicion(nuevaFechaHora, duracion, turno)) {
            throw new ReglaNegocioException("El nuevo horario no esta disponible");
        }
        turno.reprogramar(nuevaFechaHora);
    }

    public void cancelarTurno(PersonalClinica usuario, Turno turno, FechaHora momentoCancelacion)
            throws ReglaNegocioException {
        validarRecepcion(usuario);
        turno.cancelar(momentoCancelacion);
    }

    public void marcarAusente(PersonalClinica usuario, Turno turno) throws ReglaNegocioException {
        validarRecepcion(usuario);
        turno.marcarAusente();
    }

    public void asignarGuardia(PersonalClinica usuario, Fecha fecha, Veterinario veterinario)
            throws ReglaNegocioException {
        if (!(usuario instanceof Administrador)) throw new ReglaNegocioException("Solo el administrador asigna guardias");
        guardiasPorFecha.put(fecha.toString(), veterinario);
    }

    public Turno asignarUrgencia(PersonalClinica usuario, Mascota mascota, FechaHora desde, Atencion atencionUrgencia)
            throws ReglaNegocioException {
        validarRecepcion(usuario);
        ultimoTurnoDesplazado = null;
        Veterinario guardia = guardiasPorFecha.get(desde.getFecha().toString());
        if (guardia == null) throw new ReglaNegocioException("No hay veterinario de guardia para la fecha");

        FechaHora candidato = desde;
        if (!guardia.dentroDeHorario(candidato, atencionUrgencia.getDuracionMinutos())) {
            candidato = guardia.getAgenda().getPrimerHuecoLibre(desde, atencionUrgencia.getDuracionMinutos());
            if (candidato == null) throw new ReglaNegocioException("No se encontro hueco disponible para la urgencia");
        }

        Turno ocupado = guardia.getAgenda().buscarTurnoSuperpuesto(candidato, atencionUrgencia.getDuracionMinutos());
        if (ocupado != null) {
            int duracionOcupado = ocupado.getAtencion().getDuracionMinutos();
            FechaHora buscarDesde = ocupado.getFechaHora().sumarMinutos(duracionOcupado);
            FechaHora nuevaFecha = guardia.getAgenda().getPrimerHuecoLibre(buscarDesde, duracionOcupado, ocupado);
            if (nuevaFecha == null) throw new ReglaNegocioException("La urgencia requeriria desplazar mas de un turno");
            ocupado.reprogramar(nuevaFecha);
            ultimoTurnoDesplazado = ocupado;
        }
        return guardia.getAgenda().agregarTurno(mascota, candidato, atencionUrgencia, true);
    }

    public Turno getUltimoTurnoDesplazado() {
        return ultimoTurnoDesplazado;
    }

    private void validarRecepcion(PersonalClinica usuario) throws ReglaNegocioException {
        if (!(usuario instanceof Recepcionista)) throw new ReglaNegocioException("Operacion permitida solo a Recepcionista");
    }
}
