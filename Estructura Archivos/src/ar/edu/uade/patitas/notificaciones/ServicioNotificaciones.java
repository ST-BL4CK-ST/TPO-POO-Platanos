package ar.edu.uade.patitas.notificaciones;

import ar.edu.uade.patitas.personas.Duenio;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class ServicioNotificaciones {
    private String ruta;

    public ServicioNotificaciones(String ruta) {
        this.ruta = ruta;
    }

    public void notificar(Duenio duenio, String mensaje) {
        registrarEvento("Para " + duenio.getNombreCompleto() + ": " + mensaje);
    }

    public void registrarEvento(String texto) {
        System.out.println(texto);
        BufferedWriter escritor = null;
        try {
            escritor = new BufferedWriter(new FileWriter(ruta, true));
            escritor.write(texto);
            escritor.newLine();
        } catch (IOException e) {
            System.out.println("No se pudo registrar la notificacion: " + e.getMessage());
        } finally {
            if (escritor != null) {
                try {
                    escritor.close();
                } catch (IOException e) {
                    System.out.println("No se pudo cerrar el archivo de notificaciones: " + e.getMessage());
                }
            }
        }
    }
}
