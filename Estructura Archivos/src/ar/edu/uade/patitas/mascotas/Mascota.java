package ar.edu.uade.patitas.mascotas;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.historia.Consulta;
import ar.edu.uade.patitas.historia.VacunaDefinicion;
import ar.edu.uade.patitas.personas.Duenio;
import ar.edu.uade.patitas.tiempo.Fecha;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class Mascota implements Serializable {
    protected String id;
    protected String nombre;
    protected String especie;
    protected String raza;
    protected Sexo sexo;
    protected Fecha fechaNacimiento;
    protected double pesoActual;
    private Duenio duenio;
    private List<Consulta> historiaClinica;
    private List<TransferenciaMascota> transferencias;
    private List<VacunaDefinicion> calendarioVacunacion;

    public Mascota(String id, String nombre, String especie, String raza, Sexo sexo,
                   Fecha fechaNacimiento, double pesoActual, Duenio duenio) {
        if (id == null || id.isBlank() || nombre == null || nombre.isBlank() || pesoActual <= 0 || duenio == null) {
            throw new IllegalArgumentException("Datos de mascota invalidos");
        }
        this.id = id;
        this.nombre = nombre;
        this.especie = especie;
        this.raza = raza;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.pesoActual = pesoActual;
        this.duenio = duenio;
        this.historiaClinica = new ArrayList<Consulta>();
        this.transferencias = new ArrayList<TransferenciaMascota>();
        this.calendarioVacunacion = new ArrayList<VacunaDefinicion>();
        duenio.agregarMascota(this);
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getEspecie() { return especie; }
    public String getRaza() { return raza; }
    public Sexo getSexo() { return sexo; }
    public Fecha getFechaNacimiento() { return fechaNacimiento; }
    public double getPesoActual() { return pesoActual; }
    public Duenio getDuenio() { return duenio; }

    public void actualizarPeso(double nuevoPeso) throws ReglaNegocioException {
        if (nuevoPeso <= 0) throw new ReglaNegocioException("El peso debe ser mayor que cero");
        pesoActual = nuevoPeso;
    }

    public int getEdadEnSemanas(Fecha fechaReferencia) {
        return fechaNacimiento.semanasHasta(fechaReferencia);
    }

    public void transferir(Duenio nuevoDuenio, Fecha fecha) throws ReglaNegocioException {
        if (nuevoDuenio == null) throw new ReglaNegocioException("El nuevo duenio es obligatorio");
        if (nuevoDuenio == duenio) throw new ReglaNegocioException("La mascota ya pertenece a ese duenio");
        Duenio anterior = duenio;
        anterior.quitarMascota(this);
        duenio = nuevoDuenio;
        nuevoDuenio.agregarMascota(this);
        transferencias.add(new TransferenciaMascota(fecha, anterior, nuevoDuenio));
    }

    public void agregarConsultaConfirmada(Consulta consulta) throws ReglaNegocioException {
        if (consulta == null || !consulta.estaConfirmada()) {
            throw new ReglaNegocioException("Solo se agregan consultas confirmadas a la historia clinica");
        }
        if (!historiaClinica.contains(consulta)) historiaClinica.add(consulta);
    }

    public List<Consulta> getHistoriaClinica() {
        return new ArrayList<Consulta>(historiaClinica);
    }

    public List<TransferenciaMascota> getTransferencias() {
        return new ArrayList<TransferenciaMascota>(transferencias);
    }

    public void agregarVacunaAlCalendario(VacunaDefinicion vacuna) {
        if (vacuna != null && !calendarioVacunacion.contains(vacuna)) calendarioVacunacion.add(vacuna);
    }

    public List<VacunaDefinicion> getCalendarioVacunacion() {
        return new ArrayList<VacunaDefinicion>(calendarioVacunacion);
    }

    public abstract Map<String, Object> getDatosEspecie();

    protected Map<String, Object> nuevoMapaDatos() {
        return new HashMap<String, Object>();
    }
}
