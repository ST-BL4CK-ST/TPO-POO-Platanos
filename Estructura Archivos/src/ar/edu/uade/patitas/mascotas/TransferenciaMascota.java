package ar.edu.uade.patitas.mascotas;

import ar.edu.uade.patitas.personas.Duenio;
import ar.edu.uade.patitas.tiempo.Fecha;
import java.io.Serializable;

public class TransferenciaMascota implements Serializable {
    private Fecha fecha;
    private Duenio duenioAnterior;
    private Duenio duenioNuevo;

    public TransferenciaMascota(Fecha fecha, Duenio duenioAnterior, Duenio duenioNuevo) {
        this.fecha = fecha;
        this.duenioAnterior = duenioAnterior;
        this.duenioNuevo = duenioNuevo;
    }

    public Fecha getFecha() { return fecha; }
    public Duenio getDuenioAnterior() { return duenioAnterior; }
    public Duenio getDuenioNuevo() { return duenioNuevo; }
}
