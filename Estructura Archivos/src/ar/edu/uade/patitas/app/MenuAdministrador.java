package ar.edu.uade.patitas.app;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.facturacion.Factura;
import ar.edu.uade.patitas.notificaciones.ServicioNotificaciones;
import ar.edu.uade.patitas.personas.Administrador;
import ar.edu.uade.patitas.personas.PersonalClinica;
import ar.edu.uade.patitas.personas.Recepcionista;
import ar.edu.uade.patitas.personas.Veterinario;
import ar.edu.uade.patitas.persistencia.RepositorioUsuarios;
import ar.edu.uade.patitas.stock.Lote;
import ar.edu.uade.patitas.stock.Producto;
import ar.edu.uade.patitas.tiempo.DiaSemana;
import ar.edu.uade.patitas.tiempo.Fecha;
import ar.edu.uade.patitas.tiempo.Hora;
import ar.edu.uade.patitas.tiempo.TramoHorario;
import ar.edu.uade.patitas.turnos.Turno;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuAdministrador extends MenuRol {

    public MenuAdministrador(Scanner scanner, SistemaPatitas sistema, PersonalClinica usuarioActivo,
                             RepositorioUsuarios repositorioUsuarios, ServicioNotificaciones notificaciones) {
        super(scanner, sistema, usuarioActivo, repositorioUsuarios, notificaciones);
    }

    @Override
    public void mostrarMenu() {
        mostrarEncabezado("Administrador");
        System.out.println("1. Registrar producto o lote");
        System.out.println("2. Consultar stock bajo");
        System.out.println("3. Emitir recordatorios de vacunas");
        System.out.println("4. Anular factura");
        System.out.println("5. Asignar guardia");
        System.out.println("6. Listar turnos de todos los veterinarios");
        System.out.println("7. Gestionar usuarios");
        System.out.println("8. Cerrar sesion");
        System.out.println("0. Guardar y salir");
    }

    @Override
    public AccionMenu procesarOpcion(int opcion) throws ReglaNegocioException, IOException {
        switch (opcion) {
            case 1: gestionarStock(); return AccionMenu.CONTINUAR;
            case 2: mostrarStockBajo(); return AccionMenu.CONTINUAR;
            case 3: emitirRecordatorios(); return AccionMenu.CONTINUAR;
            case 4: anularFactura(); return AccionMenu.CONTINUAR;
            case 5: asignarGuardia(); return AccionMenu.CONTINUAR;
            case 6: listarTurnos(); return AccionMenu.CONTINUAR;
            case 7: gestionarUsuarios(); return AccionMenu.CONTINUAR;
            case 8: return AccionMenu.CERRAR_SESION;
            case 0: return AccionMenu.SALIR;
            default:
                System.out.println("Opcion invalida.");
                return AccionMenu.CONTINUAR;
        }
    }

    private void exigirAdministrador() throws ReglaNegocioException {
        if (!(usuarioActivo instanceof Administrador)) {
            throw new ReglaNegocioException("Debe ingresar como Administrador");
        }
    }

    private void gestionarStock() throws ReglaNegocioException {
        exigirAdministrador();
        int op = ConsolaHelper.leerOpcion(scanner, "1=Nuevo producto 2=Agregar lote: ", 1, 2);
        if (op == 1) {
            String codigo = ConsolaHelper.leerTextoNoVacio(scanner, "Codigo: ");
            String nombre = ConsolaHelper.leerTextoNoVacio(scanner, "Nombre: ");
            int stockMinimo = ConsolaHelper.leerEnteroPositivo(scanner, "Stock minimo: ");
            String proveedor = ConsolaHelper.leerTextoNoVacio(scanner, "Proveedor: ");
            double precio = ConsolaHelper.leerDoublePositivo(scanner, "Precio unitario: ");
            Producto producto = new Producto(codigo, nombre, stockMinimo, proveedor, precio);
            sistema.getServicioStock().agregarProducto(producto);
            System.out.println("Producto registrado.");
        } else if (op == 2) {
            Producto producto = exigirProducto();
            String codigoLote = ConsolaHelper.leerTextoNoVacio(scanner, "Codigo lote: ");
            int cantidad = ConsolaHelper.leerEnteroPositivo(scanner, "Cantidad: ");
            Fecha vencimiento = ConsolaHelper.leerFecha(scanner, "Vencimiento dd/mm/aaaa: ");
            Fecha ingreso = ConsolaHelper.leerFecha(scanner, "Fecha de ingreso dd/mm/aaaa: ");
            Lote lote = new Lote(codigoLote, cantidad, vencimiento);
            producto.agregarLote(lote, ingreso);
            System.out.println("Lote registrado.");
        }
    }

    private void mostrarStockBajo() throws ReglaNegocioException {
        exigirAdministrador();
        Fecha fecha = ConsolaHelper.leerFecha(scanner, "Fecha de consulta dd/mm/aaaa: ");
        List<Producto> bajos = sistema.getServicioStock().getProductosBajoMinimo(fecha);
        if (bajos.isEmpty()) {
            System.out.println("No hay productos bajo minimo.");
            return;
        }
        for (Producto producto : bajos) {
            System.out.println(producto + " | stock disponible=" + producto.getStockTotal(fecha));
        }
    }

    private void emitirRecordatorios() throws ReglaNegocioException {
        exigirAdministrador();
        Fecha fecha = ConsolaHelper.leerFecha(scanner, "Fecha de referencia dd/mm/aaaa: ");
        List<String> mensajes = sistema.getServicioRecordatorios().emitirRecordatorios(sistema.getMascotas(), fecha);
        if (mensajes.isEmpty()) {
            System.out.println("No hay recordatorios pendientes.");
            return;
        }
        for (String mensaje : mensajes) {
            notificaciones.registrarEvento(mensaje);
        }
    }

    private void anularFactura() throws ReglaNegocioException {
        exigirAdministrador();
        Factura factura = exigirFactura();
        String motivo = ConsolaHelper.leerTextoNoVacio(scanner, "Motivo: ");
        Fecha fecha = ConsolaHelper.leerFecha(scanner, "Fecha dd/mm/aaaa: ");
        sistema.getControladorFacturacion().anularFactura(usuarioActivo, factura, motivo, fecha);
        System.out.println("Factura anulada mediante nota de credito.");
    }

    private void asignarGuardia() throws ReglaNegocioException {
        exigirAdministrador();
        Veterinario veterinario = exigirVeterinario();
        Fecha fechaGuardia = ConsolaHelper.leerFecha(scanner, "Fecha guardia dd/mm/aaaa: ");
        sistema.getControladorTurnos().asignarGuardia(usuarioActivo, fechaGuardia, veterinario);
        System.out.println("Guardia asignada.");
    }

    private void listarTurnos() {
        for (PersonalClinica persona : sistema.getPersonal()) {
            if (persona instanceof Veterinario) {
                Veterinario veterinario = (Veterinario) persona;
                System.out.println("-- " + veterinario.getNombreCompleto() + " --");
                for (Turno turno : veterinario.getAgenda().getTurnosOrdenados()) {
                    System.out.println(turno);
                }
            }
        }
    }

    private void gestionarUsuarios() throws ReglaNegocioException, IOException {
        exigirAdministrador();
        int subOpcion = -1;
        while (subOpcion != 0) {
            System.out.println("\n--- Gestion de Usuarios ---");
            System.out.println("1. Crear usuario");
            System.out.println("2. Modificar usuario");
            System.out.println("3. Listar usuarios");
            System.out.println("0. Volver");
            subOpcion = ConsolaHelper.leerEntero(scanner, "Opcion: ");
            switch (subOpcion) {
                case 1: crearUsuario(); break;
                case 2: modificarUsuario(); break;
                case 3: listarUsuarios(); break;
                case 0: break;
                default: System.out.println("Opcion invalida.");
            }
        }
    }

    private void crearUsuario() throws ReglaNegocioException, IOException {
        System.out.println("\n--- Alta de Nuevo Usuario ---");
        String nombre = ConsolaHelper.leerTextoNoVacio(scanner, "Nombre: ");
        String apellido = ConsolaHelper.leerTextoNoVacio(scanner, "Apellido: ");
        String documento = ConsolaHelper.leerTextoNoVacio(scanner, "Documento: ");
        String telefono = ConsolaHelper.leerTextoNoVacio(scanner, "Telefono: ");
        String legajo = ConsolaHelper.leerTextoNoVacio(scanner, "Legajo: ");

        if (repositorioUsuarios.existeUsuario(legajo)) {
            throw new ReglaNegocioException("El legajo ya existe en usuarios.txt");
        }

        int tipo = ConsolaHelper.leerOpcion(scanner, "Tipo (1=Recepcionista 2=Veterinario 3=Administrador): ", 1, 3);
        PersonalClinica nuevoPersonal;
        if (tipo == 1) {
            nuevoPersonal = new Recepcionista(nombre, apellido, documento, telefono, legajo);
        } else if (tipo == 2) {
            String matricula = ConsolaHelper.leerTextoNoVacio(scanner, "Matricula: ");
            String especialidad = ConsolaHelper.leerTextoNoVacio(scanner, "Especialidad: ");
            Veterinario vet = new Veterinario(nombre, apellido, documento, telefono, legajo, matricula, especialidad);
            // Horario por defecto: lunes a sabado de 9 a 19
            TramoHorario tramo = new TramoHorario(new Hora(9, 0), new Hora(19, 0));
            vet.configurarHorario(DiaSemana.LUNES, tramo);
            vet.configurarHorario(DiaSemana.MARTES, tramo);
            vet.configurarHorario(DiaSemana.MIERCOLES, tramo);
            vet.configurarHorario(DiaSemana.JUEVES, tramo);
            vet.configurarHorario(DiaSemana.VIERNES, tramo);
            vet.configurarHorario(DiaSemana.SABADO, tramo);
            nuevoPersonal = vet;
        } else {
            nuevoPersonal = new Administrador(nombre, apellido, documento, telefono, legajo);
        }

        String contrasenia = null;
        while (contrasenia == null) {
            String entrada = ConsolaHelper.leerTexto(scanner, "Contrasenia: ");
            try {
                RepositorioUsuarios.validarContrasenia(entrada);
                contrasenia = entrada;
            } catch (IllegalArgumentException e) {
                System.out.println("Contrasenia invalida: " + e.getMessage());
            }
        }

        // Operacion atomica: registra en sistema (valida duplicados) y luego en TXT
        sistema.registrarPersonal(nuevoPersonal);
        try {
            repositorioUsuarios.registrarUsuario(legajo, contrasenia);
            System.out.println("Usuario " + legajo + " registrado exitosamente.");
        } catch (IOException e) {
            // Revertir alta en sistema para no dejar alta a medias
            sistema.eliminarPersonal(nuevoPersonal);
            throw e;
        } catch (RuntimeException e) {
            sistema.eliminarPersonal(nuevoPersonal);
            throw e;
        }
    }

    private void modificarUsuario() throws ReglaNegocioException, IOException {
        System.out.println("\n--- Modificar Usuario ---");
        String legajo = ConsolaHelper.leerTextoNoVacio(scanner, "Legajo a modificar: ");
        if (!repositorioUsuarios.existeUsuario(legajo)) {
            System.out.println("El usuario no existe.");
            return;
        }

        String nuevaContrasenia = null;
        while (nuevaContrasenia == null) {
            String entrada = ConsolaHelper.leerTexto(scanner, "Nueva contrasenia: ");
            try {
                RepositorioUsuarios.validarContrasenia(entrada);
                nuevaContrasenia = entrada;
            } catch (IllegalArgumentException e) {
                System.out.println("Contrasenia invalida: " + e.getMessage());
            }
        }
        repositorioUsuarios.cambiarContrasenia(legajo, nuevaContrasenia);

        PersonalClinica personal = sistema.buscarPersonalPorLegajo(legajo);
        if (personal != null) {
            String nuevoTelefono = ConsolaHelper.leerTextoNoVacio(scanner, "Nuevo telefono de contacto: ");
            personal.setTelefono(nuevoTelefono);
        }
        System.out.println("Usuario " + legajo + " modificado exitosamente.");
    }

    private void listarUsuarios() throws IOException {
        System.out.println("\n--- Lista de Usuarios ---");
        Map<String, String> usuarios = repositorioUsuarios.cargarUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios registrados.");
            return;
        }
        for (String legajo : usuarios.keySet()) {
            PersonalClinica p = sistema.buscarPersonalPorLegajo(legajo);
            if (p != null) {
                System.out.println(p.getLegajo() + " | " + p.getNombreCompleto() + " | " +
                        p.getClass().getSimpleName() + " | Tel: " + p.getTelefono() + " | Doc: " + p.getDocumento());
            } else {
                System.out.println(legajo + " | (Sin personal asociado en el sistema)");
            }
        }
    }
}
