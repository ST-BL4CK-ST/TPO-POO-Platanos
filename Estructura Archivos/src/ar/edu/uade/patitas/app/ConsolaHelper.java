package ar.edu.uade.patitas.app;

import ar.edu.uade.patitas.tiempo.Fecha;
import ar.edu.uade.patitas.tiempo.FechaHora;
import ar.edu.uade.patitas.tiempo.Hora;
import java.util.Scanner;

/**
 * Helper para lectura de datos por consola con bucles de reintento ante datos invalidos.
 *
 * Supuesto del sistema:
 * Todos los pesos en el dominio se almacenan y procesan en kilogramos (kg).
 * La conversion desde libras (lb) utiliza la relacion estandar: 1 lb = 0.45359237 kg.
 */
public class ConsolaHelper {
    public static final double FACTOR_LIBRAS_A_KG = 0.45359237;

    public static String leerTexto(Scanner scanner, String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    public static String leerTextoNoVacio(Scanner scanner, String mensaje) {
        while (true) {
            String valor = leerTexto(scanner, mensaje);
            if (!valor.isEmpty()) {
                return valor;
            }
            System.out.println("El dato no puede estar vacio. Ingrese nuevamente.");
        }
    }

    public static int leerEntero(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim();
            try {
                return Integer.parseInt(linea);
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero entero valido.");
            }
        }
    }

    public static int leerEnteroPositivo(Scanner scanner, String mensaje) {
        while (true) {
            int valor = leerEntero(scanner, mensaje);
            if (valor > 0) {
                return valor;
            }
            System.out.println("La cantidad debe ser mayor a cero.");
        }
    }

    public static int leerOpcion(Scanner scanner, String mensaje, int min, int max) {
        while (true) {
            int valor = leerEntero(scanner, mensaje);
            if (valor >= min && valor <= max) {
                return valor;
            }
            System.out.println("Opcion invalida. Debe ser entre " + min + " y " + max + ".");
        }
    }

    public static double leerDouble(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim();
            try {
                return Double.parseDouble(linea.replace(',', '.'));
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero decimal valido.");
            }
        }
    }

    public static double leerDoublePositivo(Scanner scanner, String mensaje) {
        while (true) {
            double valor = leerDouble(scanner, mensaje);
            if (valor > 0) {
                return valor;
            }
            System.out.println("El valor debe ser mayor a cero.");
        }
    }

    public static double leerPeso(Scanner scanner, String mensaje) {
        double valor = leerDoublePositivo(scanner, mensaje);
        int unidad = leerOpcion(scanner, "Unidad (1=kg 2=lb): ", 1, 2);
        if (unidad == 2) {
            return convertirLibrasAKilogramos(valor);
        }
        return valor;
    }

    public static double convertirLibrasAKilogramos(double libras) {
        if (libras <= 0) {
            throw new IllegalArgumentException("El peso debe ser mayor a cero");
        }
        return libras * FACTOR_LIBRAS_A_KG;
    }

    public static boolean leerSiNo(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim().toLowerCase();
            if (linea.equals("s") || linea.equals("si")) {
                return true;
            }
            if (linea.equals("n") || linea.equals("no")) {
                return false;
            }
            System.out.println("Respuesta invalida. Ingrese s o n.");
        }
    }

    public static Fecha leerFecha(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim();
            try {
                return Fecha.desdeTexto(linea);
            } catch (IllegalArgumentException | ArithmeticException e) {
                System.out.println("Fecha invalida, use dd/mm/aaaa (" + e.getMessage() + ")");
            }
        }
    }

    public static Hora leerHora(Scanner scanner, String mensaje) {
        while (true) {
            System.out.print(mensaje);
            String linea = scanner.nextLine().trim();
            try {
                return Hora.desdeTexto(linea);
            } catch (IllegalArgumentException e) {
                System.out.println("Hora invalida, use HH:mm (" + e.getMessage() + ")");
            }
        }
    }

    public static FechaHora leerFechaHora(Scanner scanner) {
        Fecha fecha = leerFecha(scanner, "Fecha dd/mm/aaaa: ");
        Hora hora = leerHora(scanner, "Hora HH:mm: ");
        return new FechaHora(fecha, hora);
    }

    public static FechaHora leerFechaHoraConEtiqueta(Scanner scanner, String etiqueta) {
        System.out.println(etiqueta + ":");
        return leerFechaHora(scanner);
    }
}
