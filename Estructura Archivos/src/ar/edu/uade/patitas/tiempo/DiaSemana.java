package ar.edu.uade.patitas.tiempo;

public enum DiaSemana {
    LUNES, MARTES, MIERCOLES, JUEVES, VIERNES, SABADO, DOMINGO;

    public DiaSemana siguiente() {
        switch (this) {
            case LUNES: return MARTES;
            case MARTES: return MIERCOLES;
            case MIERCOLES: return JUEVES;
            case JUEVES: return VIERNES;
            case VIERNES: return SABADO;
            case SABADO: return DOMINGO;
            default: return LUNES;
        }
    }
}
