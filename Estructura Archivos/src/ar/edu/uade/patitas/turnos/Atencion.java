package ar.edu.uade.patitas.turnos;

import java.io.Serializable;

public abstract class Atencion implements Serializable {
    private double arancel;

    public Atencion(double arancel) {
        if (arancel < 0) throw new IllegalArgumentException("Arancel invalido");
        this.arancel = arancel;
    }

    public double getArancel() { return arancel; }
    public abstract String getNombre();
    public abstract int getDuracionMinutos();
}
