package ar.edu.uade.patitas.app;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.notificaciones.ServicioNotificaciones;
import ar.edu.uade.patitas.personas.Administrador;
import ar.edu.uade.patitas.personas.PersonalClinica;
import ar.edu.uade.patitas.personas.Recepcionista;
import ar.edu.uade.patitas.personas.Veterinario;
import ar.edu.uade.patitas.persistencia.RepositorioSistema;
import ar.edu.uade.patitas.persistencia.RepositorioUsuarios;
import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static SistemaPatitas sistema;
    private static RepositorioUsuarios repositorioUsuarios;
    private static ServicioNotificaciones notificaciones = new ServicioNotificaciones("data/notificaciones.log");

    public static void main(String[] args) {
        RepositorioSistema repositorio = new RepositorioSistema("data/patitas.dat");
        repositorioUsuarios = new RepositorioUsuarios("data/usuarios.txt");

        try {
            sistema = repositorio.cargar();
            DatosDemo.cargarSiEstaVacio(sistema);
            repositorioUsuarios.inicializarSiNoExiste();

            ejecutarCicloPrincipal();
        } catch (IOException e) {
            System.out.println("Error de archivo: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            System.out.println("No se pudo reconstruir la informacion guardada: " + e.getMessage());
        } catch (ReglaNegocioException e) {
            System.out.println("Error de negocio al iniciar: " + e.getMessage());
        } finally {
            if (sistema != null) {
                try {
                    repositorio.guardar(sistema);
                    System.out.println("Datos guardados.");
                } catch (IOException e) {
                    System.out.println("No se pudieron guardar los datos: " + e.getMessage());
                }
            }
            scanner.close();
        }
    }

    private static void ejecutarCicloPrincipal() throws IOException {
        boolean enEjecucion = true;
        while (enEjecucion) {
            PersonalClinica usuario = autenticar();
            if (usuario == null) {
                // Se superó el máximo de intentos o no se pudo autenticar
                enEjecucion = false;
                break;
            }

            MenuRol menu = crearMenu(usuario);
            boolean cerrarSesion = menu.ejecutar();
            if (!cerrarSesion) {
                // El usuario eligió guardar y salir
                enEjecucion = false;
            }
        }
    }

    private static PersonalClinica autenticar() throws IOException {
        int intentos = 0;
        final int MAX_INTENTOS = 3;

        System.out.println("\n=== Iniciar Sesion ===");
        while (intentos < MAX_INTENTOS) {
            String legajo = ConsolaHelper.leerTextoNoVacio(scanner, "Usuario (legajo): ");
            String contrasenia = ConsolaHelper.leerTexto(scanner, "Contrasenia: ");

            if (!repositorioUsuarios.validarCredenciales(legajo, contrasenia)) {
                intentos++;
                int restantes = MAX_INTENTOS - intentos;
                System.out.println("Credenciales incorrectas. Intentos restantes: " + restantes);
                if (intentos >= MAX_INTENTOS) {
                    System.out.println("Ha superado el maximo de intentos permitidos. Saliendo...");
                    return null;
                }
            } else {
                PersonalClinica personal = sistema.buscarPersonalPorLegajo(legajo);
                if (personal == null) {
                    System.out.println("Aviso: El usuario existe en el archivo pero no hay personal asociado en el sistema.");
                } else {
                    return personal;
                }
            }
        }
        return null;
    }

    private static MenuRol crearMenu(PersonalClinica usuario) {
        if (usuario instanceof Recepcionista) {
            return new MenuRecepcionista(scanner, sistema, usuario, repositorioUsuarios, notificaciones);
        } else if (usuario instanceof Veterinario) {
            return new MenuVeterinario(scanner, sistema, usuario, repositorioUsuarios, notificaciones);
        } else if (usuario instanceof Administrador) {
            return new MenuAdministrador(scanner, sistema, usuario, repositorioUsuarios, notificaciones);
        }
        throw new IllegalArgumentException("Rol de personal no soportado: " + usuario.getClass().getSimpleName());
    }
}
