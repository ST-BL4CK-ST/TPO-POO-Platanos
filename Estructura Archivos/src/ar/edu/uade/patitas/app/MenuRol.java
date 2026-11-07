package ar.edu.uade.patitas.app;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.facturacion.Factura;
import ar.edu.uade.patitas.mascotas.Mascota;
import ar.edu.uade.patitas.mascotas.Sexo;
import ar.edu.uade.patitas.notificaciones.ServicioNotificaciones;
import ar.edu.uade.patitas.personas.Duenio;
import ar.edu.uade.patitas.personas.PersonalClinica;
import ar.edu.uade.patitas.personas.Veterinario;
import ar.edu.uade.patitas.persistencia.RepositorioUsuarios;
import ar.edu.uade.patitas.stock.Producto;
import ar.edu.uade.patitas.turnos.Atencion;
import ar.edu.uade.patitas.turnos.Cirugia;
import ar.edu.uade.patitas.turnos.ConsultaGeneral;
import ar.edu.uade.patitas.turnos.Turno;
import ar.edu.uade.patitas.turnos.Vacunacion;
import java.io.IOException;
import java.util.Scanner;

public abstract class MenuRol {
    protected Scanner scanner;
    protected SistemaPatitas sistema;
    protected PersonalClinica usuarioActivo;
    protected RepositorioUsuarios repositorioUsuarios;
    protected ServicioNotificaciones notificaciones;

    public MenuRol(Scanner scanner, SistemaPatitas sistema, PersonalClinica usuarioActivo,
                   RepositorioUsuarios repositorioUsuarios, ServicioNotificaciones notificaciones) {
        this.scanner = scanner;
        this.sistema = sistema;
        this.usuarioActivo = usuarioActivo;
        this.repositorioUsuarios = repositorioUsuarios;
        this.notificaciones = notificaciones;
    }

    public abstract void mostrarMenu();

    public abstract AccionMenu procesarOpcion(int opcion) throws ReglaNegocioException, IOException;

    public boolean ejecutar() {
        while (true) {
            mostrarMenu();
            int opcion = ConsolaHelper.leerEntero(scanner, "Opcion: ");
            try {
                AccionMenu accion = procesarOpcion(opcion);
                if (accion == AccionMenu.CERRAR_SESION) {
                    System.out.println("Sesion cerrada.");
                    return true;
                }
                if (accion == AccionMenu.SALIR) {
                    return false;
                }
            } catch (ReglaNegocioException e) {
                System.out.println("Regla de negocio: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                System.out.println("Dato invalido: " + e.getMessage());
            } catch (IOException e) {
                System.out.println("Error de archivo: " + e.getMessage());
            }
        }
    }

    protected void mostrarEncabezado(String nombreRol) {
        String usuario = usuarioActivo == null ? "sin usuario" :
                usuarioActivo.getNombreCompleto() + " (" + usuarioActivo.getLegajo() + " - " + nombreRol + ")";
        System.out.println("\n=== Veterinaria Patitas | " + usuario + " ===");
    }

    protected Mascota exigirMascota() throws ReglaNegocioException {
        Mascota mascota = sistema.buscarMascota(ConsolaHelper.leerTexto(scanner, "ID mascota: "));
        if (mascota == null) throw new ReglaNegocioException("Mascota no encontrada");
        return mascota;
    }

    protected Veterinario exigirVeterinario() throws ReglaNegocioException {
        Veterinario veterinario = sistema.buscarVeterinario(ConsolaHelper.leerTexto(scanner, "Matricula veterinario: "));
        if (veterinario == null) throw new ReglaNegocioException("Veterinario no encontrado");
        return veterinario;
    }

    protected Turno exigirTurno() throws ReglaNegocioException {
        Turno turno = sistema.buscarTurno(ConsolaHelper.leerTexto(scanner, "ID turno: "));
        if (turno == null) throw new ReglaNegocioException("Turno no encontrado");
        return turno;
    }

    protected Producto exigirProducto() throws ReglaNegocioException {
        Producto producto = sistema.getServicioStock().buscarProducto(ConsolaHelper.leerTexto(scanner, "Codigo producto: "));
        if (producto == null) throw new ReglaNegocioException("Producto no encontrado");
        return producto;
    }

    protected Factura exigirFactura() throws ReglaNegocioException {
        Factura factura = sistema.getControladorFacturacion().buscarFactura(ConsolaHelper.leerTexto(scanner, "Numero factura: "));
        if (factura == null) throw new ReglaNegocioException("Factura no encontrada");
        return factura;
    }

    protected Duenio exigirDuenio() throws ReglaNegocioException {
        Duenio duenio = sistema.buscarDuenio(ConsolaHelper.leerTexto(scanner, "Documento del duenio: "));
        if (duenio == null) throw new ReglaNegocioException("Duenio inexistente");
        return duenio;
    }

    protected Sexo leerSexo() {
        int valor = ConsolaHelper.leerOpcion(scanner, "Sexo 1=Macho 2=Hembra: ", 1, 2);
        return valor == 2 ? Sexo.HEMBRA : Sexo.MACHO;
    }

    protected Atencion leerAtencion() {
        int tipo = ConsolaHelper.leerOpcion(scanner, "Atencion 1=Consulta general 2=Vacunacion 3=Cirugia: ", 1, 3);
        double arancel = ConsolaHelper.leerDoublePositivo(scanner, "Arancel: ");
        if (tipo == 2) return new Vacunacion(arancel);
        if (tipo == 3) return new Cirugia(arancel);
        return new ConsultaGeneral(arancel);
    }
}
