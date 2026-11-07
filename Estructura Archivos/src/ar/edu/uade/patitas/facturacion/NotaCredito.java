package ar.edu.uade.patitas.facturacion;

import ar.edu.uade.patitas.tiempo.Fecha;
import java.io.Serializable;

public class NotaCredito implements Serializable {
    private String numero;
    private Fecha fecha;
    private String motivo;
    private Factura factura;

    public NotaCredito(String numero, Fecha fecha, String motivo, Factura factura) {
        this.numero = numero;
        this.fecha = fecha;
        this.motivo = motivo;
        this.factura = factura;
    }

    public String getNumero() { return numero; }
    public Fecha getFecha() { return fecha; }
    public String getMotivo() { return motivo; }
    public Factura getFactura() { return factura; }
}
