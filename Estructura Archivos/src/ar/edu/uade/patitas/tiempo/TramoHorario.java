package ar.edu.uade.patitas.tiempo;

import java.io.Serializable;

public class TramoHorario implements Serializable {
    private Hora horaInicio;
    private Hora horaFin;

    public TramoHorario(Hora horaInicio, Hora horaFin) {
        if (horaInicio == null || horaFin == null || horaInicio.compareTo(horaFin) >= 0) {
            throw new IllegalArgumentException("Tramo horario invalido");
        }
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
    }

    public boolean contiene(FechaHora inicio, int duracionMinutos) {
        int desde = inicio.getHora().aMinutos();
        int hasta = desde + duracionMinutos;
        return desde >= horaInicio.aMinutos() && hasta <= horaFin.aMinutos();
    }

    @Override
    public String toString() {
        return horaInicio.toString() + " - " + horaFin.toString();
    }
}
