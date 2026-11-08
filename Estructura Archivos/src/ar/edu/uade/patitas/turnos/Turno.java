package ar.edu.uade.patitas.turnos;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.mascotas.Mascota;
import ar.edu.uade.patitas.personas.Veterinario;
import ar.edu.uade.patitas.tiempo.FechaHora;
import java.io.Serializable;

public class Turno implements Comparable<Turno>, Serializable {
    private String id;
    private FechaHora fechaHora;
    private Atencion atencion;
    private EstadoTurno estado;
    private boolean urgencia;
    private Mascota mascota;
    private Veterinario veterinario;
    private double cargoCancelacion;

    public Turno(String id, FechaHora fechaHora, Atencion atencion, boolean urgencia,
                 Mascota mascota, Veterinario veterinario) {
        this.id = id;
        this.fechaHora = fechaHora;
        this.atencion = atencion;
        this.estado = EstadoTurno.PROGRAMADO;
        this.urgencia = urgencia;
        this.mascota = mascota;
        this.veterinario = veterinario;
    }

    public String getId() { return id; }
    public FechaHora getFechaHora() { return fechaHora; }
    public Atencion getAtencion() { return atencion; }
    public EstadoTurno getEstado() { return estado; }
    public boolean esUrgencia() { return urgencia; }
    public Mascota getMascota() { return mascota; }
    public Veterinario getVeterinario() { return veterinario; }
    public double getCargoCancelacion() { return cargoCancelacion; }

    public void reprogramar(FechaHora nuevaFecha) throws ReglaNegocioException {
        if (estado != EstadoTurno.PROGRAMADO) throw new ReglaNegocioException("Solo se reprograman turnos programados");
        fechaHora = nuevaFecha;
    }

    public double calcularCargoCancelacion(FechaHora momentoCancelacion) throws ReglaNegocioException {
        if (estado != EstadoTurno.PROGRAMADO) throw new ReglaNegocioException("El turno no esta programado");
        int minutos = momentoCancelacion.minutosHasta(fechaHora);
        if (minutos < 0) throw new ReglaNegocioException("No se puede cancelar un turno que ya paso");
        if (minutos < 120) return atencion.getArancel() * 0.20;
        return 0.0;
    }

    public void cancelar(FechaHora momentoCancelacion) throws ReglaNegocioException {
        if (estado != EstadoTurno.PROGRAMADO) throw new ReglaNegocioException("El turno no esta programado");
        int minutos = momentoCancelacion.minutosHasta(fechaHora);
        if (minutos < 0) throw new ReglaNegocioException("No se puede cancelar un turno que ya paso");
        if (minutos < 120) {
            cargoCancelacion = atencion.getArancel() * 0.20;
            mascota.getDuenio().agregarCargoPendiente(cargoCancelacion);
        }
        estado = EstadoTurno.CANCELADO;
    }

    public void marcarAusente() throws ReglaNegocioException {
        if (estado != EstadoTurno.PROGRAMADO) throw new ReglaNegocioException("El turno no esta programado");
        estado = EstadoTurno.AUSENTE;
        mascota.getDuenio().incrementarAusencia();
    }

    public void marcarAtendido() throws ReglaNegocioException {
        if (estado != EstadoTurno.PROGRAMADO) throw new ReglaNegocioException("El turno no esta programado");
        estado = EstadoTurno.ATENDIDO;
        mascota.getDuenio().resetAusencias();
    }

    @Override
    public int compareTo(Turno otro) {
        return fechaHora.compareTo(otro.fechaHora);
    }

    @Override
    public String toString() {
        return id + " | " + fechaHora + " | " + mascota.getNombre() + " | " +
                veterinario.getNombreCompleto() + " | " + atencion.getNombre() + " | " + estado;
    }
}
