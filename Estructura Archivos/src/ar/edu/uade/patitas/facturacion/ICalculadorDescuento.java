package ar.edu.uade.patitas.facturacion;

import java.io.Serializable;

public interface ICalculadorDescuento extends Serializable {
    double calcularDescuento(double subtotal, MedioPago medioPago, boolean tienePlanSalud);
    double calcularRecargo(double montoDescontado, MedioPago medioPago);
    double calcular(double subtotal, MedioPago medioPago, boolean tienePlanSalud);
}
