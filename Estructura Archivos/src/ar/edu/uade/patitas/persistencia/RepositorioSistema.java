package ar.edu.uade.patitas.persistencia;

import ar.edu.uade.patitas.app.SistemaPatitas;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class RepositorioSistema {
    private String ruta;

    public RepositorioSistema(String ruta) {
        this.ruta = ruta;
    }

    public void guardar(SistemaPatitas sistema) throws IOException {
        ObjectOutputStream salida = null;
        try {
            salida = new ObjectOutputStream(new FileOutputStream(ruta));
            salida.writeObject(sistema);
        } catch (IOException e) {
            throw e;
        } finally {
            if (salida != null) salida.close();
        }
    }

    public SistemaPatitas cargar() throws IOException, ClassNotFoundException {
        File archivo = new File(ruta);
        if (!archivo.exists()) return new SistemaPatitas();
        ObjectInputStream entrada = null;
        try {
            entrada = new ObjectInputStream(new FileInputStream(archivo));
            return (SistemaPatitas) entrada.readObject();
        } catch (IOException e) {
            throw e;
        } catch (ClassNotFoundException e) {
            throw e;
        } finally {
            if (entrada != null) entrada.close();
        }
    }
}
