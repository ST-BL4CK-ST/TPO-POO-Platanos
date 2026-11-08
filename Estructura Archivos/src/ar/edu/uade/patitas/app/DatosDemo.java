package ar.edu.uade.patitas.app;

import ar.edu.uade.patitas.excepciones.ReglaNegocioException;
import ar.edu.uade.patitas.historia.VacunaDefinicion;
import ar.edu.uade.patitas.mascotas.Perro;
import ar.edu.uade.patitas.mascotas.Sexo;
import ar.edu.uade.patitas.mascotas.TamanoPerro;
import ar.edu.uade.patitas.personas.Administrador;
import ar.edu.uade.patitas.personas.Duenio;
import ar.edu.uade.patitas.personas.Recepcionista;
import ar.edu.uade.patitas.personas.Veterinario;
import ar.edu.uade.patitas.stock.Lote;
import ar.edu.uade.patitas.stock.Producto;
import ar.edu.uade.patitas.tiempo.DiaSemana;
import ar.edu.uade.patitas.tiempo.Fecha;
import ar.edu.uade.patitas.tiempo.Hora;
import ar.edu.uade.patitas.tiempo.TramoHorario;

public class DatosDemo {
    public static void cargarSiEstaVacio(SistemaPatitas sistema) throws ReglaNegocioException {
        if (!sistema.getDuenios().isEmpty()) return;

        Recepcionista recepcionista = new Recepcionista("Ana", "Perez", "30000001", "1111", "R001");
        Administrador administrador = new Administrador("Luis", "Gomez", "30000002", "2222", "A001");
        Veterinario veterinario = new Veterinario("Carla", "Diaz", "30000003", "3333", "V001", "MP100", "Clinica general");
        TramoHorario tramo = new TramoHorario(new Hora(9, 0), new Hora(19, 0));
        veterinario.configurarHorario(DiaSemana.LUNES, tramo);
        veterinario.configurarHorario(DiaSemana.MARTES, tramo);
        veterinario.configurarHorario(DiaSemana.MIERCOLES, tramo);
        veterinario.configurarHorario(DiaSemana.JUEVES, tramo);
        veterinario.configurarHorario(DiaSemana.VIERNES, tramo);
        veterinario.configurarHorario(DiaSemana.SABADO, tramo);
        sistema.registrarPersonal(recepcionista);
        sistema.registrarPersonal(administrador);
        sistema.registrarPersonal(veterinario);

        Duenio duenio = new Duenio("Martin", "Lopez", "40000001", "4444", "martin@correo.com");
        duenio.setPlanSaludAnimal(true);
        sistema.registrarDuenio(duenio);
        Perro perro = new Perro("M001", "Toby", "Mestizo", Sexo.MACHO, new Fecha(1, 1, 2024), 12.5,
                duenio, "CHIP-1", TamanoPerro.MEDIANO);
        sistema.registrarMascota(perro);

        Producto antibiotico = new Producto("P001", "Antibiotico", 3, "Proveedor A", 5000);
        antibiotico.agregarLote(new Lote("L-A1", 10, new Fecha(30, 11, 2026)), new Fecha(1, 11, 2026));
        sistema.getServicioStock().agregarProducto(antibiotico);

        Producto antirrabica = new Producto("P002", "Vacuna antirrabica", 2, "Proveedor B", 7000);
        antirrabica.agregarLote(new Lote("L-V1", 8, new Fecha(30, 12, 2026)), new Fecha(1, 11, 2026));
        sistema.getServicioStock().agregarProducto(antirrabica);
        VacunaDefinicion vacuna = new VacunaDefinicion("Antirrabica", 12, 365, antirrabica);
        vacuna.agregarEspecie("Perro");
        sistema.registrarVacuna(vacuna);
        perro.agregarVacunaAlCalendario(vacuna);

        Producto tripleFelina = new Producto("P003", "Vacuna triple felina", 2, "Proveedor B", 6500);
        tripleFelina.agregarLote(new Lote("L-TF1", 6, new Fecha(30, 12, 2026)), new Fecha(1, 11, 2026));
        sistema.getServicioStock().agregarProducto(tripleFelina);
        VacunaDefinicion vFelina = new VacunaDefinicion("Triple Felina", 8, 365, tripleFelina);
        vFelina.agregarEspecie("Gato");
        sistema.registrarVacuna(vFelina);

        Producto newcastle = new Producto("P004", "Vacuna Newcastle", 2, "Proveedor C", 4000);
        newcastle.agregarLote(new Lote("L-NC1", 10, new Fecha(30, 12, 2026)), new Fecha(1, 11, 2026));
        sistema.getServicioStock().agregarProducto(newcastle);
        VacunaDefinicion vNewcastle = new VacunaDefinicion("Newcastle", 4, 180, newcastle);
        vNewcastle.agregarEspecie("Ave");
        sistema.registrarVacuna(vNewcastle);

        Producto mixomatosis = new Producto("P005", "Vacuna Mixomatosis", 2, "Proveedor C", 4500);
        mixomatosis.agregarLote(new Lote("L-MX1", 5, new Fecha(30, 12, 2026)), new Fecha(1, 11, 2026));
        sistema.getServicioStock().agregarProducto(mixomatosis);
        VacunaDefinicion vMixomatosis = new VacunaDefinicion("Mixomatosis", 5, 180, mixomatosis);
        vMixomatosis.agregarEspecie("Conejo");
        sistema.registrarVacuna(vMixomatosis);
    }
}
