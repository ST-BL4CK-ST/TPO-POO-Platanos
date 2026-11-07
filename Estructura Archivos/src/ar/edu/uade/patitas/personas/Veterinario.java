package ar.edu.uade.patitas.personas;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.mascotas.Mascota;
import ar.edu.uade.patitas.tiempo.DiaSemana;
import ar.edu.uade.patitas.tiempo.FechaHora;
import ar.edu.uade.patitas.tiempo.TramoHorario;
import ar.edu.uade.patitas.turnos.Agenda;
import ar.edu.uade.patitas.turnos.Atencion;
import ar.edu.uade.patitas.turnos.Turno;
import java.util.HashMap;
import java.util.Map;

public class Veterinario extends PersonalClinica {
    private String matricula;
    private String especialidad;
    private Map<DiaSemana, TramoHorario> horarioAtencion;
    private Agenda agenda;

    public Veterinario(String nombre, String apellido, String documento, String telefono,
                       String legajo, String matricula, String especialidad) {
        super(nombre, apellido, documento, telefono, legajo);
        if (matricula == null || matricula.isBlank()) throw new IllegalArgumentException("Matricula obligatoria");
        this.matricula = matricula;
        this.especialidad = especialidad;
        this.horarioAtencion = new HashMap<DiaSemana, TramoHorario>();
        this.agenda = new Agenda(this);
    }

    public String getMatricula() { return matricula; }
    public String getEspecialidad() { return especialidad; }
    public Agenda getAgenda() { return agenda; }

    public void configurarHorario(DiaSemana dia, TramoHorario tramo) {
        horarioAtencion.put(dia, tramo);
    }

    public boolean dentroDeHorario(FechaHora fechaHora, int duracion) {
        TramoHorario tramo = horarioAtencion.get(fechaHora.getFecha().getDiaSemana());
        return tramo != null && tramo.contiene(fechaHora, duracion);
    }

    public boolean estaDisponible(FechaHora fechaHora, Atencion atencion) {
        return dentroDeHorario(fechaHora, atencion.getDuracionMinutos()) &&
                !agenda.haySuperposicion(fechaHora, atencion.getDuracionMinutos(), null);
    }

    public Turno agendarTurno(Mascota mascota, FechaHora fechaHora, Atencion atencion) throws ReglaNegocioException {
        return agenda.agregarTurno(mascota, fechaHora, atencion, false);
    }
}
