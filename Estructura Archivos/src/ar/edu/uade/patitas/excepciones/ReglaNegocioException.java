package ar.edu.uade.patitas.excepciones;

public class ReglaNegocioException extends Exception {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
