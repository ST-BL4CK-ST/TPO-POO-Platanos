package ar.edu.uade.patitas.stock;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.tiempo.Fecha;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Producto implements Comparable<Producto>, Serializable {
    private String codigo;
    private String nombre;
    private int stockMinimo;
    private String proveedor;
    private double precioUnitario;
    private List<Lote> lotes;
    private boolean alertaActiva;

    public Producto(String codigo, String nombre, int stockMinimo, String proveedor, double precioUnitario) {
        if (codigo == null || codigo.isBlank() || nombre == null || nombre.isBlank() || stockMinimo < 0 || precioUnitario < 0) {
            throw new IllegalArgumentException("Datos de producto invalidos");
        }
        this.codigo = codigo;
        this.nombre = nombre;
        this.stockMinimo = stockMinimo;
        this.proveedor = proveedor;
        this.precioUnitario = precioUnitario;
        this.lotes = new ArrayList<Lote>();
    }

    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public int getStockMinimo() { return stockMinimo; }
    public String getProveedor() { return proveedor; }
    public double getPrecioUnitario() { return precioUnitario; }
    public boolean isAlertaActiva() { return alertaActiva; }

    public void agregarLote(Lote lote, Fecha fechaIngreso) throws ReglaNegocioException {
        if (lote.estaVencido(fechaIngreso)) throw new ReglaNegocioException("No se puede ingresar un lote vencido");
        for (Lote existente : lotes) {
            if (existente.getCodigo().equals(lote.getCodigo())) throw new ReglaNegocioException("Codigo de lote duplicado");
        }
        lotes.add(lote);
        actualizarAlerta(fechaIngreso);
    }

    public int getStockTotal(Fecha fechaActual) {
        int total = 0;
        for (Lote lote : lotes) {
            if (!lote.estaVencido(fechaActual)) total += lote.getCantidad();
        }
        return total;
    }

    public boolean requiereReposicion(Fecha fechaActual) {
        return getStockTotal(fechaActual) <= stockMinimo;
    }

    public List<Lote> getLotesFIFO() {
        List<Lote> copia = new ArrayList<Lote>();
        for (Lote lote : lotes) copia.add(lote);
        Collections.sort(copia);
        return copia;
    }

    public String descontarFIFO(int cantidad, Fecha fechaActual) throws ReglaNegocioException {
        if (cantidad <= 0) throw new ReglaNegocioException("Cantidad a descontar invalida");
        if (getStockTotal(fechaActual) < cantidad) throw new ReglaNegocioException("Stock insuficiente de " + nombre);

        int restante = cantidad;
        String primerLoteUsado = null;
        List<Lote> ordenados = getLotesFIFO();
        for (Lote lote : ordenados) {
            if (!lote.estaVencido(fechaActual) && lote.getCantidad() > 0 && restante > 0) {
                if (primerLoteUsado == null) primerLoteUsado = lote.getCodigo();
                int aDescontar = restante;
                if (aDescontar > lote.getCantidad()) aDescontar = lote.getCantidad();
                lote.descontar(aDescontar);
                restante -= aDescontar;
            }
        }
        actualizarAlerta(fechaActual);
        return primerLoteUsado;
    }

    public void actualizarAlerta(Fecha fechaActual) {
        alertaActiva = requiereReposicion(fechaActual);
    }

    @Override
    public int compareTo(Producto otro) {
        return Integer.compare(stockMinimo, otro.stockMinimo);
    }

    @Override
    public String toString() {
        return codigo + " | " + nombre + " | minimo=" + stockMinimo + " | $" + precioUnitario;
    }
}
