package ar.edu.uade.patitas.mascotas;

import ar.edu.uade.patitas.personas.Duenio;
import ar.edu.uade.patitas.tiempo.Fecha;
import java.util.Map;

public class Gato extends Mascota {
    private boolean esInterior;

    public Gato(String id, String nombre, String raza, Sexo sexo, Fecha fechaNacimiento,
                double pesoActual, Duenio duenio, boolean esInterior) {
        super(id, nombre, "Gato", raza, sexo, fechaNacimiento, pesoActual, duenio);
        this.esInterior = esInterior;
    }

    @Override
    public Map<String, Object> getDatosEspecie() {
        Map<String, Object> datos = nuevoMapaDatos();
        datos.put("esInterior", esInterior);
        return datos;
    }
}
