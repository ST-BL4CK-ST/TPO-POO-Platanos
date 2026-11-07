package ar.edu.uade.patitas.historia;

import ar.edu.uade.patitas.mascotas.Mascota;
import ar.edu.uade.patitas.tiempo.Fecha;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServicioRecordatoriosVacunas implements Serializable {
    public List<String> emitirRecordatorios(List<Mascota> mascotas, Fecha fechaReferencia) {
        List<String> mensajes = new ArrayList<String>();
        for (Mascota mascota : mascotas) {
            for (Consulta consulta : mascota.getHistoriaClinica()) {
                for (VacunaAplicada vacuna : consulta.getVacunasAplicadas()) {
                    if (vacuna.requiereRecordatorio(fechaReferencia)) {
                        mensajes.add("Recordatorio para " + mascota.getDuenio().getNombreCompleto() +
                                ": " + mascota.getNombre() + " debe reforzar " + vacuna.getDefinicion().getNombre() +
                                " el " + vacuna.getFechaProximoRefuerzo());
                    } else if (vacuna.estaVencida(fechaReferencia)) {
                        mensajes.add("VACUNA VENCIDA: " + mascota.getNombre() + " - " + vacuna.getDefinicion().getNombre());
                    }
                }
            }
        }
        return mensajes;
    }
}
