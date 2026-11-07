package ar.edu.uade.patitas.stock;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.historia.Consulta;
import ar.edu.uade.patitas.historia.Tratamiento;
import ar.edu.uade.patitas.historia.VacunaAplicada;
import ar.edu.uade.patitas.tiempo.Fecha;
import java.util.ArrayList;
import java.util.List;

public class ServicioStock implements IServicioStock {
    private List<Producto> productos;

    public ServicioStock() {
        productos = new ArrayList<Producto>();
    }

    public void agregarProducto(Producto producto) throws ReglaNegocioException {
        if (buscarProducto(producto.getCodigo()) != null) throw new ReglaNegocioException("Codigo de producto duplicado");
        productos.add(producto);
    }

    public Producto buscarProducto(String codigo) {
        for (Producto producto : productos) if (producto.getCodigo().equals(codigo)) return producto;
        return null;
    }

    public List<Producto> getProductos() {
        List<Producto> copia = new ArrayList<Producto>();
        for (Producto producto : productos) copia.add(producto);
        return copia;
    }

    public List<Producto> getProductosOrdenadosPorPrecio() {
        List<Producto> copia = getProductos();
        copia.sort(new ComparadorProductoPorPrecio());
        return copia;
    }

    @Override
    public void verificarStock(Consulta consulta, Fecha fecha) throws ReglaNegocioException {
        List<Producto> revisados = new ArrayList<Producto>();
        for (Tratamiento tratamiento : consulta.getTratamientos()) {
            Producto producto = tratamiento.getProducto();
            if (!revisados.contains(producto)) {
                int requerido = cantidadRequerida(producto, consulta);
                if (producto.getStockTotal(fecha) < requerido) {
                    throw new ReglaNegocioException("Stock insuficiente de " + producto.getNombre());
                }
                revisados.add(producto);
            }
        }
        for (VacunaAplicada vacuna : consulta.getVacunasAplicadas()) {
            Producto producto = vacuna.getProducto();
            if (!revisados.contains(producto)) {
                int requerido = cantidadRequerida(producto, consulta);
                if (producto.getStockTotal(fecha) < requerido) {
                    throw new ReglaNegocioException("Stock insuficiente de " + producto.getNombre());
                }
                revisados.add(producto);
            }
        }
    }

    @Override
    public void descontarStock(Consulta consulta, Fecha fecha) throws ReglaNegocioException {
        verificarStock(consulta, fecha);
        for (Tratamiento tratamiento : consulta.getTratamientos()) {
            tratamiento.getProducto().descontarFIFO(tratamiento.getCantidad(), fecha);
        }
        for (VacunaAplicada vacuna : consulta.getVacunasAplicadas()) {
            String loteUsado = vacuna.getProducto().descontarFIFO(1, fecha);
            vacuna.registrarLoteConsumido(loteUsado);
        }
    }

    private int cantidadRequerida(Producto producto, Consulta consulta) {
        int cantidad = 0;
        for (Tratamiento tratamiento : consulta.getTratamientos()) {
            if (tratamiento.getProducto() == producto) cantidad += tratamiento.getCantidad();
        }
        for (VacunaAplicada vacuna : consulta.getVacunasAplicadas()) {
            if (vacuna.getProducto() == producto) cantidad++;
        }
        return cantidad;
    }

    @Override
    public List<Producto> getProductosBajoMinimo(Fecha fecha) {
        List<Producto> resultado = new ArrayList<Producto>();
        for (Producto producto : productos) if (producto.requiereReposicion(fecha)) resultado.add(producto);
        return resultado;
    }
}
