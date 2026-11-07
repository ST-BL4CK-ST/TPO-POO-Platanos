package ar.edu.uade.patitas.stock;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.historia.Consulta;
import ar.edu.uade.patitas.tiempo.Fecha;
import java.io.Serializable;
import java.util.List;

public interface IServicioStock extends Serializable {
    void verificarStock(Consulta consulta, Fecha fecha) throws ReglaNegocioException;
    void descontarStock(Consulta consulta, Fecha fecha) throws ReglaNegocioException;
    List<Producto> getProductosBajoMinimo(Fecha fecha);
}
