package ar.edu.uade.patitas.historia;

import ar.edu.uade.patitas.personas.Veterinario;
import ar.edu.uade.patitas.tiempo.Fecha;
import java.io.Serializable;

public class NotaRectificatoria implements Serializable {
    private String texto;
    private Fecha fecha;
    private Veterinario veterinarioFirmante;

    public NotaRectificatoria(String texto, Fecha fecha, Veterinario veterinarioFirmante) {
        if (texto == null || texto.isBlank()) throw new IllegalArgumentException("Texto de nota obligatorio");
        this.texto = texto;
        this.fecha = fecha;
        this.veterinarioFirmante = veterinarioFirmante;
    }

    public String getTexto() { return texto; }
    public Fecha getFecha() { return fecha; }
    public Veterinario getVeterinarioFirmante() { return veterinarioFirmante; }
}
