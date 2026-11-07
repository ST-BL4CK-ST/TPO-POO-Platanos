package ar.edu.uade.patitas.stock;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.tiempo.Fecha;
import java.io.Serializable;

public class Lote implements Comparable<Lote>, Serializable {
    private String codigo;
    private int cantidad;
    private Fecha fechaVencimiento;

    public Lote(String codigo, int cantidad, Fecha fechaVencimiento) {
        if (codigo == null || codigo.isBlank() || cantidad <= 0 || fechaVencimiento == null) {
            throw new IllegalArgumentException("Datos de lote invalidos");
        }
        this.codigo = codigo;
        this.cantidad = cantidad;
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getCodigo() { return codigo; }
    public int getCantidad() { return cantidad; }
    public Fecha getFechaVencimiento() { return fechaVencimiento; }

    public boolean estaVencido(Fecha fechaActual) {
        return fechaVencimiento.compareTo(fechaActual) < 0;
    }

    public void descontar(int cantidadADescontar) throws ReglaNegocioException {
        if (cantidadADescontar <= 0 || cantidadADescontar > cantidad) {
            throw new ReglaNegocioException("Cantidad invalida para descontar del lote");
        }
        cantidad -= cantidadADescontar;
    }

    @Override
    public int compareTo(Lote otro) {
        return fechaVencimiento.compareTo(otro.fechaVencimiento);
    }

    @Override
    public String toString() {
        return codigo + " | cant=" + cantidad + " | vence=" + fechaVencimiento;
    }
}
