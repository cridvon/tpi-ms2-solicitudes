package solicitudes.servicio;

import java.util.function.Predicate;

import solicitudes.dominio.Estado;
import solicitudes.dominio.Prioridad;
import solicitudes.dominio.Responsable;
import solicitudes.dominio.Solicitud;
import solicitudes.dominio.Tipo;

/** Criterios de búsqueda reutilizables y combinables (por ejemplo, porEstado(...).and(porTipo(...))). */
public final class Filtros {

    private Filtros() {
    }

    public static Predicate<Solicitud> porEstado(Estado estado) {
        return s -> s.getEstado() == estado;
    }

    public static Predicate<Solicitud> porTipo(Tipo tipo) {
        return s -> s.getTipo() == tipo;
    }

    public static Predicate<Solicitud> porResponsable(Responsable responsable) {
        return s -> s.getResponsable() == responsable;
    }

    public static Predicate<Solicitud> porPrioridad(Prioridad prioridad) {
        return s -> s.getPrioridad() == prioridad;
    }
}
