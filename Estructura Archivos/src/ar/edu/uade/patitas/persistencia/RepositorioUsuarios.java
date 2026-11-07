package ar.edu.uade.patitas.persistencia;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RepositorioUsuarios {
    private String rutaArchivo;

    public RepositorioUsuarios(String rutaArchivo) {
        if (rutaArchivo == null || rutaArchivo.isBlank()) {
            throw new IllegalArgumentException("La ruta del archivo de usuarios es obligatoria");
        }
        this.rutaArchivo = rutaArchivo;
    }

    public String getRutaArchivo() {
        return rutaArchivo;
    }

    public void inicializarSiNoExiste() throws IOException {
        File archivo = new File(rutaArchivo);
        if (archivo.exists()) {
            return;
        }
        File directorioPadre = archivo.getParentFile();
        if (directorioPadre != null && !directorioPadre.exists()) {
            directorioPadre.mkdirs();
        }

        BufferedWriter escritor = null;
        try {
            escritor = new BufferedWriter(new FileWriter(archivo));
            escritor.write("R001;1234");
            escritor.newLine();
            escritor.write("V001;1234");
            escritor.newLine();
            escritor.write("A001;1234");
            escritor.newLine();
        } catch (IOException e) {
            throw e;
        } finally {
            if (escritor != null) {
                try {
                    escritor.close();
                } catch (IOException ignored) {}
            }
        }
    }

    public Map<String, String> cargarUsuarios() throws IOException {
        File archivo = new File(rutaArchivo);
        if (!archivo.exists()) {
            inicializarSiNoExiste();
        }

        Map<String, String> usuarios = new LinkedHashMap<String, String>();
        BufferedReader lector = null;
        try {
            lector = new BufferedReader(new FileReader(archivo));
            String linea;
            while ((linea = lector.readLine()) != null) {
                String limpia = linea.trim();
                if (limpia.isEmpty()) {
                    continue;
                }
                String[] partes = limpia.split(";", -1);
                if (partes.length >= 2) {
                    String usuario = partes[0].trim();
                    String contrasenia = partes[1].trim();
                    if (!usuario.isEmpty()) {
                        usuarios.put(usuario, contrasenia);
                    }
                }
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (lector != null) {
                try {
                    lector.close();
                } catch (IOException ignored) {}
            }
        }
        return usuarios;
    }

    public boolean validarCredenciales(String legajo, String contrasenia) throws IOException {
        if (legajo == null || contrasenia == null) {
            return false;
        }
        Map<String, String> usuarios = cargarUsuarios();
        String passAlmacenada = usuarios.get(legajo.trim());
        if (passAlmacenada == null) {
            return false;
        }
        return passAlmacenada.equals(contrasenia.trim());
    }

    public boolean existeUsuario(String legajo) throws IOException {
        if (legajo == null) {
            return false;
        }
        Map<String, String> usuarios = cargarUsuarios();
        return usuarios.containsKey(legajo.trim());
    }

    public void registrarUsuario(String legajo, String contrasenia) throws IOException, ReglaNegocioException {
        if (legajo == null || legajo.isBlank()) {
            throw new IllegalArgumentException("El legajo es obligatorio");
        }
        validarContrasenia(contrasenia);
        String legajoLimpio = legajo.trim();
        if (existeUsuario(legajoLimpio)) {
            throw new ReglaNegocioException("El usuario ya existe en el archivo de usuarios");
        }

        BufferedWriter escritor = null;
        try {
            escritor = new BufferedWriter(new FileWriter(rutaArchivo, true));
            escritor.write(legajoLimpio + ";" + contrasenia.trim());
            escritor.newLine();
        } catch (IOException e) {
            throw e;
        } finally {
            if (escritor != null) {
                try {
                    escritor.close();
                } catch (IOException ignored) {}
            }
        }
    }

    public void cambiarContrasenia(String legajo, String nuevaContrasenia) throws IOException, ReglaNegocioException {
        if (legajo == null || legajo.isBlank()) {
            throw new IllegalArgumentException("El legajo es obligatorio");
        }
        validarContrasenia(nuevaContrasenia);
        String legajoLimpio = legajo.trim();

        Map<String, String> usuarios = cargarUsuarios();
        if (!usuarios.containsKey(legajoLimpio)) {
            throw new ReglaNegocioException("El usuario no existe");
        }
        usuarios.put(legajoLimpio, nuevaContrasenia.trim());
        guardarTodos(usuarios);
    }

    public void eliminarUsuario(String legajo) throws IOException {
        if (legajo == null) {
            return;
        }
        Map<String, String> usuarios = cargarUsuarios();
        if (usuarios.remove(legajo.trim()) != null) {
            guardarTodos(usuarios);
        }
    }

    public List<String> listarUsuarios() throws IOException {
        Map<String, String> usuarios = cargarUsuarios();
        return new ArrayList<String>(usuarios.keySet());
    }

    public static void validarContrasenia(String contrasenia) {
        if (contrasenia == null || contrasenia.isBlank()) {
            throw new IllegalArgumentException("La contrasenia no puede estar vacia");
        }
        if (contrasenia.contains(";")) {
            throw new IllegalArgumentException("La contrasenia no puede contener el caracter ';'");
        }
    }

    private void guardarTodos(Map<String, String> usuarios) throws IOException {
        BufferedWriter escritor = null;
        try {
            escritor = new BufferedWriter(new FileWriter(rutaArchivo, false));
            for (Map.Entry<String, String> entrada : usuarios.entrySet()) {
                escritor.write(entrada.getKey() + ";" + entrada.getValue());
                escritor.newLine();
            }
        } catch (IOException e) {
            throw e;
        } finally {
            if (escritor != null) {
                try {
                    escritor.close();
                } catch (IOException ignored) {}
            }
        }
    }
}
