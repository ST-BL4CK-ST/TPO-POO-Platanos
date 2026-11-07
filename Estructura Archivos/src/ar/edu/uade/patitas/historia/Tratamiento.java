package ar.edu.uade.patitas.historia;

import ar.edu.uade.patitas.stock.Producto;
import java.io.Serializable;

public class Tratamiento implements Serializable {
    private Producto producto;
    private String dosis;
    private String frecuencia;
    private String duracion;
    private int cantidad;

    public Tratamiento(Producto producto, String dosis, String frecuencia, String duracion, int cantidad) {
        if (producto == null || dosis == null || dosis.isBlank() || frecuencia == null || frecuencia.isBlank() ||
                duracion == null || duracion.isBlank() || cantidad <= 0) {
            throw new IllegalArgumentException("Datos de tratamiento invalidos");
        }
        this.producto = producto;
        this.dosis = dosis;
        this.frecuencia = frecuencia;
        this.duracion = duracion;
        this.cantidad = cantidad;
    }

    public Producto getProducto() { return producto; }
    public String getDosis() { return dosis; }
    public String getFrecuencia() { return frecuencia; }
    public String getDuracion() { return duracion; }
    public int getCantidad() { return cantidad; }
}
