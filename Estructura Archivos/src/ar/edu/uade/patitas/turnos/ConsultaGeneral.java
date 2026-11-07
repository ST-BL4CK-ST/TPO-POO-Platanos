package ar.edu.uade.patitas.turnos;
public class ConsultaGeneral extends Atencion {
    public ConsultaGeneral(double arancel) { super(arancel); }
    @Override public String getNombre() { return "Consulta general"; }
    @Override public int getDuracionMinutos() { return 30; }
}
