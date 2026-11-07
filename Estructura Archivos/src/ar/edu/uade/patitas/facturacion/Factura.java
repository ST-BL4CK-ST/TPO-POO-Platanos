package ar.edu.uade.patitas.facturacion;

import ar.edu.uade.patitas.historia.Consulta;
import ar.edu.uade.patitas.tiempo.Fecha;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Factura implements Serializable {
    private String numero;
    private Fecha fecha;
    private Consulta consulta;
    private List<LineaFactura> lineas;
    private MedioPago medioPago;
    private EstadoFactura estado;
    private double descuento;
    private double recargo;
    private double total;
    private int cuotas;

    public Factura(String numero, Fecha fecha, Consulta consulta) {
        this.numero = numero;
        this.fecha = fecha;
        this.consulta = consulta;
        this.lineas = new ArrayList<LineaFactura>();
        this.estado = EstadoFactura.PENDIENTE;
    }

    public String getNumero() { return numero; }
    public Fecha getFecha() { return fecha; }
    public Consulta getConsulta() { return consulta; }
    public MedioPago getMedioPago() { return medioPago; }
    public EstadoFactura getEstado() { return estado; }
    public double getDescuento() { return descuento; }
    public double getRecargo() { return recargo; }
    public double getTotal() { return total; }
    public int getCuotas() { return cuotas; }

    public void agregarLinea(LineaFactura linea) {
        if (estado == EstadoFactura.PENDIENTE) lineas.add(linea);
    }

    public List<LineaFactura> getLineas() {
        List<LineaFactura> copia = new ArrayList<LineaFactura>();
        for (LineaFactura linea : lineas) copia.add(linea);
        return copia;
    }

    public double getSubtotal() {
        double subtotal = 0.0;
        for (LineaFactura linea : lineas) subtotal += linea.getSubtotal();
        return subtotal;
    }

    public void registrarPago(MedioPago medioPago, int cuotas, double descuento, double recargo, double total) {
        this.medioPago = medioPago;
        this.cuotas = cuotas;
        this.descuento = descuento;
        this.recargo = recargo;
        this.total = total;
        this.estado = EstadoFactura.EMITIDA;
    }

    public void anular() {
        if (estado == EstadoFactura.EMITIDA) estado = EstadoFactura.ANULADA;
    }
}
