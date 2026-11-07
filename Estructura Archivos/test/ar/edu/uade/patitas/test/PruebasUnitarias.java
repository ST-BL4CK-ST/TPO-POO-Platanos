package ar.edu.uade.patitas.test;

import ar.edu.uade.patitas.app.ConsolaHelper;
import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.facturacion.CalculadorDescuento;
import ar.edu.uade.patitas.facturacion.ControladorFacturacion;
import ar.edu.uade.patitas.facturacion.Factura;
import ar.edu.uade.patitas.facturacion.MedioPago;
import ar.edu.uade.patitas.historia.Consulta;
import ar.edu.uade.patitas.historia.ControladorConsultas;
import ar.edu.uade.patitas.mascotas.Perro;
import ar.edu.uade.patitas.mascotas.Sexo;
import ar.edu.uade.patitas.mascotas.TamanoPerro;
import ar.edu.uade.patitas.personas.Duenio;
import ar.edu.uade.patitas.personas.Recepcionista;
import ar.edu.uade.patitas.personas.Veterinario;
import ar.edu.uade.patitas.persistencia.RepositorioUsuarios;
import ar.edu.uade.patitas.stock.Lote;
import ar.edu.uade.patitas.stock.Producto;
import ar.edu.uade.patitas.stock.ServicioStock;
import ar.edu.uade.patitas.tiempo.DiaSemana;
import ar.edu.uade.patitas.tiempo.Fecha;
import ar.edu.uade.patitas.tiempo.FechaHora;
import ar.edu.uade.patitas.tiempo.Hora;
import ar.edu.uade.patitas.tiempo.TramoHorario;
import ar.edu.uade.patitas.turnos.ConsultaGeneral;
import ar.edu.uade.patitas.turnos.ControladorTurnos;
import ar.edu.uade.patitas.turnos.EstadoTurno;
import ar.edu.uade.patitas.turnos.Turno;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class PruebasUnitarias {
    private static int correctas = 0;
    private static int fallidas = 0;

    public static void main(String[] args) {
        probarFechas();
        probarDescuentos();
        probarStockFIFO();
        probarComparatorPersonalizado();
        probarSuperposicionTurnos();
        probarConfirmacionConsulta();
        probarFaltaStockSinCambios();
        probarRepositorioUsuarios();
        probarConversionKgLb();
        probarReintentoLectura();
        System.out.println("RESULTADO: " + correctas + " correctas, " + fallidas + " fallidas");
    }

    private static void probarFechas() {
        Fecha fecha = new Fecha(28, 2, 2024).sumarDias(1);
        verificar("Fecha bisiesta suma 1 dia", fecha.equals(new Fecha(29, 2, 2024)));
        verificar("02/11/2026 es lunes", new Fecha(2, 11, 2026).getDiaSemana() == DiaSemana.LUNES);
        FechaHora hora = new FechaHora(new Fecha(2, 11, 2026), new Hora(23, 50)).sumarMinutos(20);
        verificar("Suma de minutos cambia de dia", hora.toString().equals("03/11/2026 00:10"));
    }

    private static void probarDescuentos() {
        CalculadorDescuento calculador = new CalculadorDescuento();
        verificarDouble("Plan + efectivo aplica 15%", 850.0, calculador.calcular(1000, MedioPago.EFECTIVO, true));
        verificarDouble("Credito con plan: 15% y luego 8%", 918.0, calculador.calcular(1000, MedioPago.CREDITO, true));
        verificarDouble("Debito sin plan", 1000.0, calculador.calcular(1000, MedioPago.DEBITO, false));
    }

    private static void probarStockFIFO() {
        try {
            Producto producto = new Producto("P1", "Medicamento", 1, "Prov", 100);
            Lote primero = new Lote("L1", 5, new Fecha(10, 11, 2026));
            Lote segundo = new Lote("L2", 5, new Fecha(20, 11, 2026));
            producto.agregarLote(segundo, new Fecha(1, 11, 2026));
            producto.agregarLote(primero, new Fecha(1, 11, 2026));
            producto.descontarFIFO(6, new Fecha(4, 11, 2026));
            verificar("FIFO agota primero el lote que vence antes", primero.getCantidad() == 0 && segundo.getCantidad() == 4);
        } catch (ReglaNegocioException e) {
            verificar("FIFO sin excepcion inesperada", false);
        }
    }

    private static void probarComparatorPersonalizado() {
        try {
            ServicioStock stock = new ServicioStock();
            Producto caro = new Producto("C", "Caro", 0, "P", 300);
            Producto barato = new Producto("B", "Barato", 0, "P", 100);
            stock.agregarProducto(caro);
            stock.agregarProducto(barato);
            verificar("Comparator ordena productos por precio", stock.getProductosOrdenadosPorPrecio().get(0) == barato);
        } catch (ReglaNegocioException e) {
            verificar("Comparator sin excepcion inesperada", false);
        }
    }

    private static void probarSuperposicionTurnos() {
        try {
            Escenario e = crearEscenario(5);
            ControladorTurnos controlador = new ControladorTurnos();
            FechaHora actual = new FechaHora(new Fecha(1, 11, 2026), new Hora(10, 0));
            controlador.asignarTurno(e.recepcionista, e.mascota, e.veterinario,
                    new FechaHora(new Fecha(2, 11, 2026), new Hora(10, 0)), new ConsultaGeneral(1000), actual);
            boolean rechazo = false;
            try {
                controlador.asignarTurno(e.recepcionista, e.mascota, e.veterinario,
                        new FechaHora(new Fecha(2, 11, 2026), new Hora(10, 15)), new ConsultaGeneral(1000), actual);
            } catch (ReglaNegocioException ex) {
                rechazo = true;
            }
            verificar("Agenda rechaza superposicion", rechazo);
        } catch (ReglaNegocioException e) {
            verificar("Preparacion superposicion", false);
        }
    }

    private static void probarConfirmacionConsulta() {
        try {
            Escenario e = crearEscenario(5);
            ControladorTurnos turnos = new ControladorTurnos();
            Turno turno = turnos.asignarTurno(e.recepcionista, e.mascota, e.veterinario,
                    new FechaHora(new Fecha(2, 11, 2026), new Hora(10, 0)), new ConsultaGeneral(1000),
                    new FechaHora(new Fecha(1, 11, 2026), new Hora(10, 0)));
            ControladorFacturacion facturacion = new ControladorFacturacion(new CalculadorDescuento());
            ControladorConsultas consultas = new ControladorConsultas(e.stock, facturacion);
            Consulta consulta = consultas.crearConsulta(e.veterinario, turno);
            consultas.registrarDatosClinicos(e.veterinario, consulta, "Control", 10, "Diagnostico", "Sin observaciones");
            consultas.agregarTratamiento(e.veterinario, consulta, e.producto, "1 comprimido", "cada 12 h", "3 dias", 2);
            Factura factura = consultas.confirmarConsulta(e.veterinario, consulta);
            verificar("Confirmar consulta marca turno atendido", turno.getEstado() == EstadoTurno.ATENDIDO);
            verificar("Confirmar consulta agrega historia", e.mascota.getHistoriaClinica().size() == 1);
            verificar("Confirmar consulta descuenta stock", e.producto.getStockTotal(new Fecha(2, 11, 2026)) == 3);
            verificar("Confirmar consulta genera factura", factura != null && factura.getSubtotal() == 1200.0);
        } catch (ReglaNegocioException e) {
            verificar("Confirmacion consulta sin excepcion inesperada", false);
        }
    }

    private static void probarFaltaStockSinCambios() {
        try {
            Escenario e = crearEscenario(1);
            ControladorTurnos turnos = new ControladorTurnos();
            Turno turno = turnos.asignarTurno(e.recepcionista, e.mascota, e.veterinario,
                    new FechaHora(new Fecha(2, 11, 2026), new Hora(11, 0)), new ConsultaGeneral(1000),
                    new FechaHora(new Fecha(1, 11, 2026), new Hora(10, 0)));
            ControladorConsultas consultas = new ControladorConsultas(e.stock,
                    new ControladorFacturacion(new CalculadorDescuento()));
            Consulta consulta = consultas.crearConsulta(e.veterinario, turno);
            consultas.registrarDatosClinicos(e.veterinario, consulta, "Control", 10, "Dx", "Obs");
            consultas.agregarTratamiento(e.veterinario, consulta, e.producto, "dosis", "frecuencia", "duracion", 2);
            boolean rechazo = false;
            try {
                consultas.confirmarConsulta(e.veterinario, consulta);
            } catch (ReglaNegocioException ex) {
                rechazo = true;
            }
            verificar("Falta stock rechaza confirmacion", rechazo);
            verificar("Falta stock conserva turno programado", turno.getEstado() == EstadoTurno.PROGRAMADO);
            verificar("Falta stock no agrega historia", e.mascota.getHistoriaClinica().isEmpty());
            verificar("Falta stock no descuenta unidades", e.producto.getStockTotal(new Fecha(2, 11, 2026)) == 1);
        } catch (ReglaNegocioException e) {
            verificar("Preparacion falta stock", false);
        }
    }

    private static Escenario crearEscenario(int stockInicial) throws ReglaNegocioException {
        Escenario e = new Escenario();
        e.duenio = new Duenio("Juan", "Perez", "1", "111", "a@b.com");
        e.mascota = new Perro("M1", "Toby", "Mestizo", Sexo.MACHO, new Fecha(1, 1, 2024), 10,
                e.duenio, "CH1", TamanoPerro.MEDIANO);
        e.recepcionista = new Recepcionista("Ana", "R", "2", "222", "R1");
        e.veterinario = new Veterinario("Vet", "Uno", "3", "333", "V1", "MP1", "General");
        e.veterinario.configurarHorario(DiaSemana.LUNES, new TramoHorario(new Hora(9, 0), new Hora(19, 0)));
        e.producto = new Producto("MED", "Medicamento", 0, "Prov", 100);
        e.producto.agregarLote(new Lote("LOT", stockInicial, new Fecha(30, 11, 2026)), new Fecha(1, 11, 2026));
        e.stock = new ServicioStock();
        e.stock.agregarProducto(e.producto);
        return e;
    }

    private static void verificar(String nombre, boolean condicion) {
        if (condicion) {
            correctas++;
            System.out.println("OK - " + nombre);
        } else {
            fallidas++;
            System.out.println("FALLO - " + nombre);
        }
    }

    private static void verificarDouble(String nombre, double esperado, double obtenido) {
        verificar(nombre, Double.compare(esperado, obtenido) == 0);
    }

    private static void probarRepositorioUsuarios() {
        String rutaTest = "data/test_usuarios.txt";
        File archivoTest = new File(rutaTest);
        if (archivoTest.exists()) {
            archivoTest.delete();
        }

        try {
            RepositorioUsuarios repo = new RepositorioUsuarios(rutaTest);

            // 1. Archivo inexistente crea usuarios demo
            verificar("Archivo inexistente no existe inicialmente", !archivoTest.exists());
            boolean authDemoInicial = repo.validarCredenciales("R001", "1234");
            verificar("Inicializa archivo inexistente y valida usuario demo", authDemoInicial && archivoTest.exists());

            // 2. Login correcto e incorrecto
            verificar("Login correcto V001", repo.validarCredenciales("V001", "1234"));
            verificar("Login incorrecto contrasenia erronea", !repo.validarCredenciales("R001", "passMal"));
            verificar("Login incorrecto usuario inexistente", !repo.validarCredenciales("NO_EXISTE", "1234"));

            // 3. Alta de usuario nuevo
            repo.registrarUsuario("V002", "claveSegura");
            verificar("Alta de usuario V002", repo.existeUsuario("V002"));
            verificar("Login de nuevo usuario V002", repo.validarCredenciales("V002", "claveSegura"));

            // 4. Alta duplicada rechazada
            boolean altaDuplicadaFallo = false;
            try {
                repo.registrarUsuario("V002", "otraClave");
            } catch (ReglaNegocioException e) {
                altaDuplicadaFallo = true;
            }
            verificar("Alta duplicada lanza ReglaNegocioException", altaDuplicadaFallo);

            // 5. Cambio de contrasenia
            repo.cambiarContrasenia("V002", "nuevaClave");
            verificar("Login con nueva contrasenia exitoso", repo.validarCredenciales("V002", "nuevaClave"));
            verificar("Login con contrasenia vieja falla", !repo.validarCredenciales("V002", "claveSegura"));

            // 6. Validacion de contrasenia sin punto y coma ni vacia
            boolean passConPuntoYComaRechazada = false;
            try {
                repo.registrarUsuario("X001", "clave;invalida");
            } catch (IllegalArgumentException e) {
                passConPuntoYComaRechazada = true;
            }
            verificar("Contrasenia con ';' rechazada", passConPuntoYComaRechazada);

        } catch (IOException | ReglaNegocioException e) {
            verificar("Excepcion inesperada en probarRepositorioUsuarios: " + e.getMessage(), false);
        } finally {
            if (archivoTest.exists()) {
                archivoTest.delete();
            }
        }
    }

    private static void probarConversionKgLb() {
        double unLb = ConsolaHelper.convertirLibrasAKilogramos(1.0);
        verificar("1 lb equivale a 0.45359237 kg", Math.abs(unLb - 0.45359237) < 0.0000001);

        double diezLb = ConsolaHelper.convertirLibrasAKilogramos(10.0);
        verificar("10 lb equivale a 4.5359237 kg", Math.abs(diezLb - 4.5359237) < 0.000001);

        boolean rechazaPesoCeroONegativo = false;
        try {
            ConsolaHelper.convertirLibrasAKilogramos(0.0);
        } catch (IllegalArgumentException e) {
            rechazaPesoCeroONegativo = true;
        }
        verificar("Conversion rechaza peso menor o igual a cero", rechazaPesoCeroONegativo);
    }

    private static void probarReintentoLectura() {
        // Simular Scanner con fecha invalida primero y luego valida
        Scanner scFecha = new Scanner("32/13/1564\n15/05/2024\n");
        Fecha fecha = ConsolaHelper.leerFecha(scFecha, "Fecha: ");
        verificar("Reintento de fecha rechaza fecha inexistente y acepta valida", fecha.equals(new Fecha(15, 5, 2024)));

        // Simular Scanner con entero no numerico, negativo y luego positivo
        Scanner scEntero = new Scanner("texto\n-3\n12\n");
        int entero = ConsolaHelper.leerEnteroPositivo(scEntero, "Entero: ");
        verificar("Reintento entero positivo rechaza texto y negativo", entero == 12);

        // Simular Scanner con opcion fuera de rango
        Scanner scOpcion = new Scanner("0\n9\n2\n");
        int opcion = ConsolaHelper.leerOpcion(scOpcion, "Opcion: ", 1, 3);
        verificar("Reintento de opcion rechaza valores fuera de rango", opcion == 2);

        // Simular Scanner con peso: rechaza negativo, luego ingresa 10 en libras (opcion 2)
        Scanner scPeso = new Scanner("-5\n10\n2\n");
        double pesoEnKg = ConsolaHelper.leerPeso(scPeso, "Peso: ");
        verificar("Reintento peso rechaza negativo y convierte libras a kg", Math.abs(pesoEnKg - 4.5359237) < 0.0001);
    }

    private static class Escenario {
        Duenio duenio;
        Perro mascota;
        Recepcionista recepcionista;
        Veterinario veterinario;
        Producto producto;
        ServicioStock stock;
    }
}
