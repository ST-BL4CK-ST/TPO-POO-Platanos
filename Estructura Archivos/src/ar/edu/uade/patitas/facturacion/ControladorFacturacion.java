package ar.edu.uade.patitas.facturacion;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.historia.Consulta;
import ar.edu.uade.patitas.historia.Tratamiento;
import ar.edu.uade.patitas.historia.VacunaAplicada;
import ar.edu.uade.patitas.personas.Administrador;
import ar.edu.uade.patitas.personas.PersonalClinica;
import ar.edu.uade.patitas.personas.Recepcionista;
import ar.edu.uade.patitas.tiempo.Fecha;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ControladorFacturacion implements Serializable {
    private ICalculadorDescuento calculador;
    private List<Factura> facturas;
    private List<NotaCredito> notasCredito;
    private int siguienteFactura;
    private int siguienteNota;

    public ControladorFacturacion(ICalculadorDescuento calculador) {
        this.calculador = calculador;
        this.facturas = new ArrayList<Factura>();
        this.notasCredito = new ArrayList<NotaCredito>();
        this.siguienteFactura = 1;
        this.siguienteNota = 1;
    }

    public Factura emitirFactura(Consulta consulta) {
        Factura factura = new Factura("F-" + siguienteFactura, consulta.getTurno().getFechaHora().getFecha(), consulta);
        siguienteFactura++;
        factura.agregarLinea(new LineaFactura(consulta.getTurno().getAtencion().getNombre(), 1,
                consulta.getTurno().getAtencion().getArancel()));
        for (Tratamiento tratamiento : consulta.getTratamientos()) {
            factura.agregarLinea(new LineaFactura(tratamiento.getProducto().getNombre(), tratamiento.getCantidad(),
                    tratamiento.getProducto().getPrecioUnitario()));
        }
        for (VacunaAplicada vacuna : consulta.getVacunasAplicadas()) {
            factura.agregarLinea(new LineaFactura(vacuna.getProducto().getNombre(), 1, vacuna.getProducto().getPrecioUnitario()));
        }
        facturas.add(factura);
        return factura;
    }

    public void registrarPago(PersonalClinica usuario, Factura factura, MedioPago medioPago, int cuotas)
            throws ReglaNegocioException {
        if (!(usuario instanceof Recepcionista)) throw new ReglaNegocioException("Solo Recepcionista registra pagos");
        if (factura.getEstado() != EstadoFactura.PENDIENTE) throw new ReglaNegocioException("La factura no esta pendiente de pago");
        if (medioPago == MedioPago.CREDITO && cuotas <= 0) throw new ReglaNegocioException("Las cuotas deben ser mayores que cero");
        double subtotal = factura.getSubtotal();
        boolean plan = factura.getConsulta().getTurno().getMascota().getDuenio().tienePlanSalud();
        double descuento = calculador.calcularDescuento(subtotal, medioPago, plan);
        double base = subtotal - descuento;
        double recargo = calculador.calcularRecargo(base, medioPago);
        factura.registrarPago(medioPago, cuotas, descuento, recargo, base + recargo);
    }

    public NotaCredito anularFactura(PersonalClinica usuario, Factura factura, String motivo, Fecha fecha)
            throws ReglaNegocioException {
        if (!(usuario instanceof Administrador)) throw new ReglaNegocioException("Solo Administrador anula facturas");
        if (motivo == null || motivo.isBlank()) throw new ReglaNegocioException("Motivo obligatorio");
        if (factura.getEstado() != EstadoFactura.EMITIDA) throw new ReglaNegocioException("Solo se anulan facturas emitidas");
        factura.anular();
        NotaCredito nota = new NotaCredito("NC-" + siguienteNota, fecha, motivo, factura);
        siguienteNota++;
        notasCredito.add(nota);
        return nota;
    }

    public List<Factura> getFacturas() {
        List<Factura> copia = new ArrayList<Factura>();
        for (Factura factura : facturas) copia.add(factura);
        return copia;
    }

    public Factura buscarFactura(String numero) {
        for (Factura factura : facturas) if (factura.getNumero().equals(numero)) return factura;
        return null;
    }
}
