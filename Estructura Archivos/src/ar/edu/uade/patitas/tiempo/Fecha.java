package ar.edu.uade.patitas.tiempo;

import java.io.Serializable;

public class Fecha implements Comparable<Fecha>, Serializable {
    private int dia;
    private int mes;
    private int anio;

    public Fecha(int dia, int mes, int anio) {
        if (!esValida(dia, mes, anio)) {
            throw new IllegalArgumentException("Fecha invalida");
        }
        this.dia = dia;
        this.mes = mes;
        this.anio = anio;
    }

    public int getDia() { return dia; }
    public int getMes() { return mes; }
    public int getAnio() { return anio; }

    public static Fecha desdeTexto(String texto) {
        String[] partes = texto.split("/");
        if (partes.length != 3) {
            throw new IllegalArgumentException("Formato de fecha esperado: dd/mm/aaaa");
        }
        return new Fecha(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]), Integer.parseInt(partes[2]));
    }

    public Fecha sumarDias(int cantidad) {
        Fecha resultado = new Fecha(dia, mes, anio);
        int i;
        if (cantidad >= 0) {
            for (i = 0; i < cantidad; i++) resultado.avanzarUnDia();
        } else {
            for (i = 0; i > cantidad; i--) resultado.retrocederUnDia();
        }
        return resultado;
    }

    public int diasHasta(Fecha otra) {
        return otra.numeroDia() - numeroDia();
    }

    public int semanasHasta(Fecha otra) {
        return diasHasta(otra) / 7;
    }

    public DiaSemana getDiaSemana() {
        int base = numeroDiaDesde2000();
        int resto = base % 7;
        if (resto < 0) resto += 7;
        // 01/01/2000 fue sabado.
        switch (resto) {
            case 0: return DiaSemana.SABADO;
            case 1: return DiaSemana.DOMINGO;
            case 2: return DiaSemana.LUNES;
            case 3: return DiaSemana.MARTES;
            case 4: return DiaSemana.MIERCOLES;
            case 5: return DiaSemana.JUEVES;
            default: return DiaSemana.VIERNES;
        }
    }

    @Override
    public int compareTo(Fecha otra) {
        return Integer.compare(numeroDia(), otra.numeroDia());
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Fecha)) return false;
        Fecha otra = (Fecha) obj;
        return dia == otra.dia && mes == otra.mes && anio == otra.anio;
    }

    @Override
    public int hashCode() {
        return numeroDia();
    }

    @Override
    public String toString() {
        return dosDigitos(dia) + "/" + dosDigitos(mes) + "/" + anio;
    }

    private static String dosDigitos(int valor) {
        if (valor < 10) return "0" + valor;
        return Integer.toString(valor);
    }

    private void avanzarUnDia() {
        dia++;
        if (dia > diasDelMes(mes, anio)) {
            dia = 1;
            mes++;
            if (mes > 12) {
                mes = 1;
                anio++;
            }
        }
    }

    private void retrocederUnDia() {
        dia--;
        if (dia < 1) {
            mes--;
            if (mes < 1) {
                mes = 12;
                anio--;
            }
            dia = diasDelMes(mes, anio);
        }
    }

    private int numeroDiaDesde2000() {
        Fecha base = new Fecha(1, 1, 2000, true);
        return numeroDia() - base.numeroDia();
    }

    private Fecha(int dia, int mes, int anio, boolean interno) {
        this.dia = dia;
        this.mes = mes;
        this.anio = anio;
    }

    private int numeroDia() {
        int total = 0;
        int y;
        if (anio >= 1) {
            for (y = 1; y < anio; y++) total += esBisiesto(y) ? 366 : 365;
        }
        int m;
        for (m = 1; m < mes; m++) total += diasDelMes(m, anio);
        total += dia;
        return total;
    }

    private static boolean esValida(int dia, int mes, int anio) {
        if (anio < 1 || mes < 1 || mes > 12 || dia < 1) return false;
        return dia <= diasDelMes(mes, anio);
    }

    private static int diasDelMes(int mes, int anio) {
        switch (mes) {
            case 4:
            case 6:
            case 9:
            case 11: return 30;
            case 2: return esBisiesto(anio) ? 29 : 28;
            default: return 31;
        }
    }

    private static boolean esBisiesto(int anio) {
        if (anio % 400 == 0) return true;
        if (anio % 100 == 0) return false;
        return anio % 4 == 0;
    }
}
