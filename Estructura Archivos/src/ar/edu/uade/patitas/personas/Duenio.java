package ar.edu.uade.patitas.personas;

import ar.edu.uade.patitas.mascotas.Mascota;
import java.util.ArrayList;
import java.util.List;

public class Duenio extends Persona {
    private String correoElectronico;
    private boolean planSaludAnimal;
    private int ausenciasConsecutivas;
    private boolean senaPendiente;
    private List<Mascota> mascotas;

    public Duenio(String nombre, String apellido, String documento, String telefono, String correoElectronico) {
        super(nombre, apellido, documento, telefono);
        if (correoElectronico == null || correoElectronico.isBlank() || !correoValido(correoElectronico)) {
            throw new IllegalArgumentException("Correo electronico invalido");
        }
        this.correoElectronico = correoElectronico;
        this.mascotas = new ArrayList<Mascota>();
    }

    public String getCorreoElectronico() { return correoElectronico; }
    public boolean tienePlanSalud() { return planSaludAnimal; }
    public int getAusenciasConsecutivas() { return ausenciasConsecutivas; }
    public boolean requiereSena() { return senaPendiente; }

    public void setPlanSaludAnimal(boolean activo) { planSaludAnimal = activo; }

    public void incrementarAusencia() {
        ausenciasConsecutivas++;
        if (ausenciasConsecutivas >= 3) senaPendiente = true;
    }

    public void resetAusencias() {
        ausenciasConsecutivas = 0;
        senaPendiente = false;
    }

    public void registrarPagoSena() {
        senaPendiente = false;
    }

    public void agregarMascota(Mascota mascota) {
        if (mascota != null && !mascotas.contains(mascota)) mascotas.add(mascota);
    }

    public void quitarMascota(Mascota mascota) {
        mascotas.remove(mascota);
    }

    public List<Mascota> getMascotas() {
        return new ArrayList<Mascota>(mascotas);
    }

    private boolean correoValido(String correo) {
        boolean tieneArroba = false;
        boolean tienePunto = false;
        int i;
        for (i = 0; i < correo.length(); i++) {
            if (correo.charAt(i) == '@') tieneArroba = true;
            if (correo.charAt(i) == '.') tienePunto = true;
        }
        return tieneArroba && tienePunto;
    }
}
