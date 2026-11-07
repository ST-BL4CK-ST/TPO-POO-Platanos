package ar.edu.uade.patitas.turnos;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.mascotas.Mascota;
import ar.edu.uade.patitas.personas.Veterinario;
import ar.edu.uade.patitas.tiempo.FechaHora;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Agenda implements Serializable {
    private Veterinario veterinario;
    private List<Turno> turnos;

    public Agenda(Veterinario veterinario) {
        this.veterinario = veterinario;
        this.turnos = new ArrayList<Turno>();
    }

    public boolean haySuperposicion(FechaHora inicio, int duracion, Turno ignorar) {
        FechaHora fin = inicio.sumarMinutos(duracion);
        for (Turno turno : turnos) {
            if (turno != ignorar && turno.getEstado() == EstadoTurno.PROGRAMADO) {
                FechaHora otroInicio = turno.getFechaHora();
                FechaHora otroFin = otroInicio.sumarMinutos(turno.getAtencion().getDuracionMinutos());
                if (inicio.compareTo(otroFin) < 0 && fin.compareTo(otroInicio) > 0) return true;
            }
        }
        return false;
    }

    public Turno agregarTurno(Mascota mascota, FechaHora fechaHora, Atencion atencion, boolean urgencia)
            throws ReglaNegocioException {
        if (!veterinario.dentroDeHorario(fechaHora, atencion.getDuracionMinutos())) {
            throw new ReglaNegocioException("Horario fuera de la franja de atencion del veterinario");
        }
        if (haySuperposicion(fechaHora, atencion.getDuracionMinutos(), null)) {
            throw new ReglaNegocioException("Existe superposicion con otro turno");
        }
        String id = veterinario.getMatricula() + "-T" + (turnos.size() + 1);
        Turno turno = new Turno(id, fechaHora, atencion, urgencia, mascota, veterinario);
        turnos.add(turno);
        return turno;
    }

    public FechaHora getPrimerHuecoLibre(FechaHora desde, int duracion) {
        return getPrimerHuecoLibre(desde, duracion, null);
    }

    public FechaHora getPrimerHuecoLibre(FechaHora desde, int duracion, Turno ignorar) {
        FechaHora candidato = desde;
        int intentos = 0;
        while (intentos < 1000) {
            if (veterinario.dentroDeHorario(candidato, duracion) && !haySuperposicion(candidato, duracion, ignorar)) {
                return candidato;
            }
            candidato = candidato.sumarMinutos(15);
            intentos++;
        }
        return null;
    }

    public Turno buscarTurnoSuperpuesto(FechaHora inicio, int duracion) {
        FechaHora fin = inicio.sumarMinutos(duracion);
        for (Turno turno : turnos) {
            if (turno.getEstado() == EstadoTurno.PROGRAMADO) {
                FechaHora otroInicio = turno.getFechaHora();
                FechaHora otroFin = otroInicio.sumarMinutos(turno.getAtencion().getDuracionMinutos());
                if (inicio.compareTo(otroFin) < 0 && fin.compareTo(otroInicio) > 0) return turno;
            }
        }
        return null;
    }

    public List<Turno> getTurnos() {
        return new ArrayList<Turno>(turnos);
    }

    public List<Turno> getTurnosOrdenados() {
        List<Turno> copia = new ArrayList<Turno>(turnos);
        Collections.sort(copia);
        return copia;
    }

    public Turno buscarPorId(String id) {
        for (Turno turno : turnos) if (turno.getId().equals(id)) return turno;
        return null;
    }
}
