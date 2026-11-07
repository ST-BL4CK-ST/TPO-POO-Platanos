package ar.edu.uade.patitas.personas;

public abstract class PersonalClinica extends Persona {
    protected String legajo;

    public PersonalClinica(String nombre, String apellido, String documento, String telefono, String legajo) {
        super(nombre, apellido, documento, telefono);
        if (legajo == null || legajo.isBlank()) throw new IllegalArgumentException("Legajo obligatorio");
        this.legajo = legajo;
    }

    public String getLegajo() { return legajo; }
}
