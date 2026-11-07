package ar.edu.uade.patitas.mascotas;

import ar.edu.uade.patitas.personas.Duenio;
import ar.edu.uade.patitas.tiempo.Fecha;
import java.util.Map;

public class Ave extends Mascota {
    private boolean puedeVolar;

    public Ave(String id, String nombre, String raza, Sexo sexo, Fecha fechaNacimiento,
               double pesoActual, Duenio duenio, boolean puedeVolar) {
        super(id, nombre, "Ave", raza, sexo, fechaNacimiento, pesoActual, duenio);
        this.puedeVolar = puedeVolar;
    }

    @Override
    public Map<String, Object> getDatosEspecie() {
        Map<String, Object> datos = nuevoMapaDatos();
        datos.put("puedeVolar", puedeVolar);
        return datos;
    }
}
