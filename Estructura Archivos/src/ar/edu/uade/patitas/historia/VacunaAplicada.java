package ar.edu.uade.patitas.historia;

import ar.edu.uade.patitas.stock.Producto;
import ar.edu.uade.patitas.tiempo.Fecha;
import java.io.Serializable;

public class VacunaAplicada implements Serializable {
    private VacunaDefinicion definicion;
    private Producto producto;
    private String lote;
    private Fecha fechaAplicacion;
    private Fecha fechaProximoRefuerzo;

    public VacunaAplicada(VacunaDefinicion definicion, Fecha fechaAplicacion) {
        this.definicion = definicion;
        this.producto = definicion.getProducto();
        this.fechaAplicacion = fechaAplicacion;
        this.fechaProximoRefuerzo = definicion.calcularFechaRefuerzo(fechaAplicacion);
    }

    public VacunaDefinicion getDefinicion() { return definicion; }
    public Producto getProducto() { return producto; }
    public String getLote() { return lote; }
    public Fecha getFechaAplicacion() { return fechaAplicacion; }
    public Fecha getFechaProximoRefuerzo() { return fechaProximoRefuerzo; }

    public void registrarLoteConsumido(String lote) {
        if (this.lote == null) this.lote = lote;
    }

    public boolean requiereRecordatorio(Fecha fechaReferencia) {
        int dias = fechaReferencia.diasHasta(fechaProximoRefuerzo);
        return dias >= 0 && dias <= 7;
    }

    public boolean estaVencida(Fecha fechaReferencia) {
        return fechaProximoRefuerzo.diasHasta(fechaReferencia) > 30;
    }
}
