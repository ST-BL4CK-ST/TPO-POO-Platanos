package ar.edu.uade.patitas.app;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.facturacion.CalculadorDescuento;
import ar.edu.uade.patitas.facturacion.ControladorFacturacion;
import ar.edu.uade.patitas.historia.ControladorConsultas;
import ar.edu.uade.patitas.historia.ServicioRecordatoriosVacunas;
import ar.edu.uade.patitas.historia.VacunaDefinicion;
import ar.edu.uade.patitas.mascotas.Mascota;
import ar.edu.uade.patitas.personas.Duenio;
import ar.edu.uade.patitas.personas.PersonalClinica;
import ar.edu.uade.patitas.personas.Veterinario;
import ar.edu.uade.patitas.stock.Producto;
import ar.edu.uade.patitas.stock.ServicioStock;
import ar.edu.uade.patitas.turnos.ControladorTurnos;
import ar.edu.uade.patitas.turnos.Turno;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SistemaPatitas implements Serializable {
    private static final long serialVersionUID = -2476971881951143514L;
    private List<Duenio> duenios;
    private List<Mascota> mascotas;
    private List<PersonalClinica> personal;
    private List<VacunaDefinicion> vacunas;
    private ServicioStock servicioStock;
    private ControladorFacturacion controladorFacturacion;
    private ControladorTurnos controladorTurnos;
    private ControladorConsultas controladorConsultas;
    private ServicioRecordatoriosVacunas servicioRecordatorios;

    public SistemaPatitas() {
        duenios = new ArrayList<Duenio>();
        mascotas = new ArrayList<Mascota>();
        personal = new ArrayList<PersonalClinica>();
        vacunas = new ArrayList<VacunaDefinicion>();
        servicioStock = new ServicioStock();
        controladorFacturacion = new ControladorFacturacion(new CalculadorDescuento());
        controladorTurnos = new ControladorTurnos();
        controladorConsultas = new ControladorConsultas(servicioStock, controladorFacturacion);
        servicioRecordatorios = new ServicioRecordatoriosVacunas();
    }

    public void registrarDuenio(Duenio duenio) throws ReglaNegocioException {
        if (buscarDuenio(duenio.getDocumento()) != null) throw new ReglaNegocioException("Documento de duenio duplicado");
        duenios.add(duenio);
    }

    public void registrarMascota(Mascota mascota) throws ReglaNegocioException {
        if (buscarMascota(mascota.getId()) != null) throw new ReglaNegocioException("Identificador de mascota duplicado");
        mascotas.add(mascota);
    }

    public void registrarPersonal(PersonalClinica persona) throws ReglaNegocioException {
        for (PersonalClinica existente : personal) {
            if (existente.getDocumento().equals(persona.getDocumento())) throw new ReglaNegocioException("Documento de personal duplicado");
            if (existente.getLegajo().equals(persona.getLegajo())) throw new ReglaNegocioException("Legajo duplicado");
        }
        personal.add(persona);
    }

    public boolean eliminarPersonal(PersonalClinica persona) {
        return personal.remove(persona);
    }

    public void registrarVacuna(VacunaDefinicion vacuna) {
        vacunas.add(vacuna);
    }

    public Duenio buscarDuenio(String documento) {
        for (Duenio duenio : duenios) if (duenio.getDocumento().equals(documento)) return duenio;
        return null;
    }

    public Mascota buscarMascota(String id) {
        for (Mascota mascota : mascotas) if (mascota.getId().equals(id)) return mascota;
        return null;
    }

    public Veterinario buscarVeterinario(String matricula) {
        for (PersonalClinica persona : personal) {
            if (persona instanceof Veterinario) {
                Veterinario veterinario = (Veterinario) persona;
                if (veterinario.getMatricula().equals(matricula)) return veterinario;
            }
        }
        return null;
    }

    public PersonalClinica buscarPersonalPorLegajo(String legajo) {
        for (PersonalClinica persona : personal) if (persona.getLegajo().equals(legajo)) return persona;
        return null;
    }

    public Turno buscarTurno(String id) {
        for (PersonalClinica persona : personal) {
            if (persona instanceof Veterinario) {
                Turno turno = ((Veterinario) persona).getAgenda().buscarPorId(id);
                if (turno != null) return turno;
            }
        }
        return null;
    }

    public VacunaDefinicion buscarVacuna(String nombre) {
        for (VacunaDefinicion vacuna : vacunas) if (vacuna.getNombre().equals(nombre)) return vacuna;
        return null;
    }

    public List<Duenio> getDuenios() { return copiarDuenios(); }
    public List<Mascota> getMascotas() { return copiarMascotas(); }
    public List<PersonalClinica> getPersonal() { return copiarPersonal(); }
    public List<VacunaDefinicion> getVacunas() {
        List<VacunaDefinicion> copia = new ArrayList<VacunaDefinicion>();
        for (VacunaDefinicion vacuna : vacunas) copia.add(vacuna);
        return copia;
    }

    public ServicioStock getServicioStock() { return servicioStock; }
    public ControladorFacturacion getControladorFacturacion() { return controladorFacturacion; }
    public ControladorTurnos getControladorTurnos() { return controladorTurnos; }
    public ControladorConsultas getControladorConsultas() { return controladorConsultas; }
    public ServicioRecordatoriosVacunas getServicioRecordatorios() { return servicioRecordatorios; }

    private List<Duenio> copiarDuenios() {
        List<Duenio> copia = new ArrayList<Duenio>();
        for (Duenio duenio : duenios) copia.add(duenio);
        return copia;
    }

    private List<Mascota> copiarMascotas() {
        List<Mascota> copia = new ArrayList<Mascota>();
        for (Mascota mascota : mascotas) copia.add(mascota);
        return copia;
    }

    private List<PersonalClinica> copiarPersonal() {
        List<PersonalClinica> copia = new ArrayList<PersonalClinica>();
        for (PersonalClinica persona : personal) copia.add(persona);
        return copia;
    }
}
