package ar.edu.uade.patitas.tiempo;

import java.io.Serializable;

public class FechaHora implements Comparable<FechaHora>, Serializable {
    private Fecha fecha;
    private Hora hora;

    public FechaHora(Fecha fecha, Hora hora) {
        if (fecha == null || hora == null) throw new IllegalArgumentException("Fecha y hora obligatorias");
        this.fecha = fecha;
        this.hora = hora;
    }

    public Fecha getFecha() { return fecha; }
    public Hora getHora() { return hora; }

    public static FechaHora desdeTexto(String fecha, String hora) {
        return new FechaHora(Fecha.desdeTexto(fecha), Hora.desdeTexto(hora));
    }

    public FechaHora sumarMinutos(int minutos) {
        int total = hora.aMinutos() + minutos;
        Fecha nuevaFecha = new Fecha(fecha.getDia(), fecha.getMes(), fecha.getAnio());
        while (total >= 1440) {
            total -= 1440;
            nuevaFecha = nuevaFecha.sumarDias(1);
        }
        while (total < 0) {
            total += 1440;
            nuevaFecha = nuevaFecha.sumarDias(-1);
        }
        return new FechaHora(nuevaFecha, new Hora(total / 60, total % 60));
    }

    public int minutosHasta(FechaHora otra) {
        return fecha.diasHasta(otra.fecha) * 1440 + (otra.hora.aMinutos() - hora.aMinutos());
    }

    @Override
    public int compareTo(FechaHora otra) {
        int compFecha = fecha.compareTo(otra.fecha);
        if (compFecha != 0) return compFecha;
        return hora.compareTo(otra.hora);
    }

    @Override
    public String toString() {
        return fecha.toString() + " " + hora.toString();
    }
}
