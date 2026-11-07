package ar.edu.uade.patitas.mascotas;

import ar.edu.uade.patitas.personas.Duenio;
import ar.edu.uade.patitas.tiempo.Fecha;
import java.util.Map;

public class Perro extends Mascota {
    private String numeroChip;
    private TamanoPerro tamano;

    public Perro(String id, String nombre, String raza, Sexo sexo, Fecha fechaNacimiento,
                 double pesoActual, Duenio duenio, String numeroChip, TamanoPerro tamano) {
        super(id, nombre, "Perro", raza, sexo, fechaNacimiento, pesoActual, duenio);
        this.numeroChip = numeroChip;
        this.tamano = tamano;
    }

    @Override
    public Map<String, Object> getDatosEspecie() {
        Map<String, Object> datos = nuevoMapaDatos();
        datos.put("numeroChip", numeroChip);
        datos.put("tamano", tamano);
        return datos;
    }
}
