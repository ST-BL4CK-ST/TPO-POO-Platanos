package ar.edu.uade.patitas.historia;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.personas.Veterinario;
import ar.edu.uade.patitas.turnos.Turno;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Consulta implements Serializable {
    private Turno turno;
    private Veterinario veterinario;
    private String motivo;
    private double pesoDelDia;
    private String diagnostico;
    private String observaciones;
    private EstadoConsulta estado;
    private List<Tratamiento> tratamientos;
    private List<VacunaAplicada> vacunasAplicadas;
    private List<NotaRectificatoria> notasRectificatorias;

    public Consulta(Turno turno, Veterinario veterinario) {
        this.turno = turno;
        this.veterinario = veterinario;
        this.estado = EstadoConsulta.BORRADOR;
        this.tratamientos = new ArrayList<Tratamiento>();
        this.vacunasAplicadas = new ArrayList<VacunaAplicada>();
        this.notasRectificatorias = new ArrayList<NotaRectificatoria>();
    }

    public Turno getTurno() { return turno; }
    public Veterinario getVeterinario() { return veterinario; }
    public String getMotivo() { return motivo; }
    public double getPesoDelDia() { return pesoDelDia; }
    public String getDiagnostico() { return diagnostico; }
    public String getObservaciones() { return observaciones; }
    public EstadoConsulta getEstado() { return estado; }
    public boolean estaConfirmada() { return estado == EstadoConsulta.CONFIRMADA; }

    public void registrarDatos(String motivo, double pesoDelDia, String diagnostico, String observaciones)
            throws ReglaNegocioException {
        validarBorrador();
        if (motivo == null || motivo.isBlank() || pesoDelDia <= 0 || diagnostico == null || diagnostico.isBlank()) {
            throw new ReglaNegocioException("Motivo, peso y diagnostico son obligatorios");
        }
        this.motivo = motivo;
        this.pesoDelDia = pesoDelDia;
        this.diagnostico = diagnostico;
        this.observaciones = observaciones;
    }

    public void agregarTratamiento(Tratamiento tratamiento) throws ReglaNegocioException {
        validarBorrador();
        if (pesoDelDia <= 0) throw new ReglaNegocioException("Debe registrarse el peso antes de indicar medicamentos");
        tratamientos.add(tratamiento);
    }

    public void agregarVacuna(VacunaAplicada vacuna) throws ReglaNegocioException {
        validarBorrador();
        vacunasAplicadas.add(vacuna);
    }

    public List<Tratamiento> getTratamientos() {
        List<Tratamiento> copia = new ArrayList<Tratamiento>();
        for (Tratamiento tratamiento : tratamientos) copia.add(tratamiento);
        return copia;
    }

    public List<VacunaAplicada> getVacunasAplicadas() {
        List<VacunaAplicada> copia = new ArrayList<VacunaAplicada>();
        for (VacunaAplicada vacuna : vacunasAplicadas) copia.add(vacuna);
        return copia;
    }

    public List<NotaRectificatoria> getNotasRectificatorias() {
        List<NotaRectificatoria> copia = new ArrayList<NotaRectificatoria>();
        for (NotaRectificatoria nota : notasRectificatorias) copia.add(nota);
        return copia;
    }

    public void validarParaConfirmar() throws ReglaNegocioException {
        validarBorrador();
        if (motivo == null || diagnostico == null || pesoDelDia <= 0) {
            throw new ReglaNegocioException("La consulta no tiene todos los datos clinicos obligatorios");
        }
    }

    public void confirmar() throws ReglaNegocioException {
        validarParaConfirmar();
        estado = EstadoConsulta.CONFIRMADA;
    }

    public void agregarNotaRectificatoria(NotaRectificatoria nota) throws ReglaNegocioException {
        if (!estaConfirmada()) throw new ReglaNegocioException("Solo se rectifican consultas confirmadas");
        notasRectificatorias.add(nota);
    }

    private void validarBorrador() throws ReglaNegocioException {
        if (estado != EstadoConsulta.BORRADOR) throw new ReglaNegocioException("La consulta confirmada es inmutable");
    }
}
