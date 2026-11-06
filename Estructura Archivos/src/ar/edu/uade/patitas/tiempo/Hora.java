package ar.edu.uade.patitas.tiempo;

import java.io.Serializable;

public class Hora implements Comparable<Hora>, Serializable {
    private int hora;
    private int minuto;

    public Hora(int hora, int minuto) {
        if (hora < 0 || hora > 23 || minuto < 0 || minuto > 59) {
            throw new IllegalArgumentException("Hora invalida");
        }
        this.hora = hora;
        this.minuto = minuto;
    }

    public int getHora() { return hora; }
    public int getMinuto() { return minuto; }
    public int aMinutos() { return hora * 60 + minuto; }

    public static Hora desdeTexto(String texto) {
        String[] partes = texto.split(":");
        if (partes.length != 2) throw new IllegalArgumentException("Formato esperado: HH:mm");
        return new Hora(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]));
    }

    @Override
    public int compareTo(Hora otra) {
        return Integer.compare(aMinutos(), otra.aMinutos());
    }

    @Override
    public String toString() {
        return dosDigitos(hora) + ":" + dosDigitos(minuto);
    }

    private static String dosDigitos(int valor) {
        if (valor < 10) return "0" + valor;
        return Integer.toString(valor);
    }
}
