package ar.edu.uade.patitas.mascotas;

import ar.edu.uade.patitas.personas.Duenio;
import ar.edu.uade.patitas.tiempo.Fecha;
import java.util.Map;

public class Conejo extends Mascota {
    private TipoPelaje tipoPelaje;

    public Conejo(String id, String nombre, String raza, Sexo sexo, Fecha fechaNacimiento,
                  double pesoActual, Duenio duenio, TipoPelaje tipoPelaje) {
        super(id, nombre, "Conejo", raza, sexo, fechaNacimiento, pesoActual, duenio);
        this.tipoPelaje = tipoPelaje;
    }

    @Override
    public Map<String, Object> getDatosEspecie() {
        Map<String, Object> datos = nuevoMapaDatos();
        datos.put("tipoPelaje", tipoPelaje);
        return datos;
    }
}
