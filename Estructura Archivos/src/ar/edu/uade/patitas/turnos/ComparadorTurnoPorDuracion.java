package ar.edu.uade.patitas.turnos;

import java.util.Comparator;

public class ComparadorTurnoPorDuracion implements Comparator<Turno> {
    @Override
    public int compare(Turno a, Turno b) {
        return Integer.compare(a.getAtencion().getDuracionMinutos(), b.getAtencion().getDuracionMinutos());
    }
}
