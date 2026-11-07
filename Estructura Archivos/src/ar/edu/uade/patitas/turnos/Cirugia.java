package ar.edu.uade.patitas.turnos;
public class Cirugia extends Atencion {
    public Cirugia(double arancel) { super(arancel); }
    @Override public String getNombre() { return "Cirugia"; }
    @Override public int getDuracionMinutos() { return 120; }
}
