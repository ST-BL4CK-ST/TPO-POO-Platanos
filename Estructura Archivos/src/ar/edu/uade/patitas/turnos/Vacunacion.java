package ar.edu.uade.patitas.turnos;
public class Vacunacion extends Atencion {
    public Vacunacion(double arancel) { super(arancel); }
    @Override public String getNombre() { return "Vacunacion"; }
    @Override public int getDuracionMinutos() { return 15; }
}
