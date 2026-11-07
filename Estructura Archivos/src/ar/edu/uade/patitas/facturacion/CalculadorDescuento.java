package ar.edu.uade.patitas.facturacion;

/**
 * Reglas de precio de la clinica (Etapa 2).
 * <ul>
 *   <li>Descuento: 10% si se paga en efectivo y 15% si el cliente tiene plan de salud.
 *       Los descuentos no se acumulan: se aplica el mayor.</li>
 *   <li>Recargo: 8% si se paga con credito, calculado sobre el monto ya descontado.</li>
 *   <li>Total = (subtotal - descuento) + recargo.</li>
 * </ul>
 */
public class CalculadorDescuento implements ICalculadorDescuento {
    @Override
    public double calcularDescuento(double subtotal, MedioPago medioPago, boolean tienePlanSalud) {
        double porcentaje = 0.0;
        if (medioPago == MedioPago.EFECTIVO) porcentaje = 0.10;
        if (tienePlanSalud && 0.15 > porcentaje) porcentaje = 0.15;
        return subtotal * porcentaje;
    }

    @Override
    public double calcularRecargo(double montoDescontado, MedioPago medioPago) {
        if (medioPago == MedioPago.CREDITO) return montoDescontado * 0.08;
        return 0.0;
    }

    @Override
    public double calcular(double subtotal, MedioPago medioPago, boolean tienePlanSalud) {
        double descuento = calcularDescuento(subtotal, medioPago, tienePlanSalud);
        double base = subtotal - descuento;
        return base + calcularRecargo(base, medioPago);
    }
}
