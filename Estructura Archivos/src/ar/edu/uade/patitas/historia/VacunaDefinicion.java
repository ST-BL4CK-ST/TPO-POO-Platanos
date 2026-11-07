package ar.edu.uade.patitas.historia;

import ar.edu.uade.patitas.stock.Producto;
import ar.edu.uade.patitas.tiempo.Fecha;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class VacunaDefinicion implements Serializable {
    private String nombre;
    private List<String> especiesAplicables;
    private int edadMinimaSemanas;
    private int intervaloDiasRefuerzo;
    private Producto producto;

    public VacunaDefinicion(String nombre, int edadMinimaSemanas, int intervaloDiasRefuerzo, Producto producto) {
        if (nombre == null || nombre.isBlank() || edadMinimaSemanas < 0 || intervaloDiasRefuerzo <= 0 || producto == null) {
            throw new IllegalArgumentException("Datos de vacuna invalidos");
        }
        this.nombre = nombre;
        this.edadMinimaSemanas = edadMinimaSemanas;
        this.intervaloDiasRefuerzo = intervaloDiasRefuerzo;
        this.producto = producto;
        this.especiesAplicables = new ArrayList<String>();
    }

    public String getNombre() { return nombre; }
    public int getEdadMinimaSemanas() { return edadMinimaSemanas; }
    public int getIntervaloDiasRefuerzo() { return intervaloDiasRefuerzo; }
    public Producto getProducto() { return producto; }

    public void agregarEspecie(String especie) {
        if (especie != null && !especiesAplicables.contains(especie)) especiesAplicables.add(especie);
    }

    public boolean aplicaA(String especie) {
        return especiesAplicables.contains(especie);
    }

    public Fecha calcularFechaRefuerzo(Fecha fechaAplicacion) {
        return fechaAplicacion.sumarDias(intervaloDiasRefuerzo);
    }
}
