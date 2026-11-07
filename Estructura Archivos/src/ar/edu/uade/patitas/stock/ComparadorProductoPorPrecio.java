package ar.edu.uade.patitas.stock;

import java.util.Comparator;

public class ComparadorProductoPorPrecio implements Comparator<Producto> {
    @Override
    public int compare(Producto a, Producto b) {
        return Double.compare(a.getPrecioUnitario(), b.getPrecioUnitario());
    }
}
