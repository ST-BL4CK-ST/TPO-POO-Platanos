package ar.edu.uade.patitas.personas;

import java.io.Serializable;

public abstract class Persona implements Serializable {
    private static final long serialVersionUID = 6501817907955177132L;
    protected String nombre;
    protected String apellido;
    protected String documento;
    protected String telefono;

    public Persona(String nombre, String apellido, String documento, String telefono) {
        if (nombre == null || nombre.isBlank() || apellido == null || apellido.isBlank() ||
                documento == null || documento.isBlank() || telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("Los datos personales son obligatorios");
        }
        this.nombre = nombre;
        this.apellido = apellido;
        this.documento = documento;
        this.telefono = telefono;
    }

    public String getNombre() { return nombre; }
    public String getApellido() { return apellido; }
    public String getDocumento() { return documento; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) {
        if (telefono == null || telefono.isBlank()) {
            throw new IllegalArgumentException("El telefono es obligatorio");
        }
        this.telefono = telefono;
    }
    public String getNombreCompleto() { return nombre + " " + apellido; }
}
