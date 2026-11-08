package solicitudes.dominio;

/** Área responsable de atender una solicitud. */
public enum Responsable {
    BEDELIA("Bedelía"),
    DIRECCION_CARRERA("Dirección de Carrera"),
    SECRETARIA_ACADEMICA("Secretaría Académica"),
    SIN_ASIGNAR("SIN ASIGNAR");

    private final String nombre;

    Responsable(String nombre) {
        this.nombre = nombre;
    }

    public String nombre() {
        return nombre;
    }

    /** Busca cualquier responsable por su nombre visible (incluye SIN_ASIGNAR). */
    public static Responsable desdeNombre(String nombre) {
        for (Responsable responsable : values()) {
            if (responsable.nombre.equals(nombre)) {
                return responsable;
            }
        }
        throw new DatoInvalidoException("responsable inválido");
    }

    /** Busca un responsable que se pueda asignar manualmente (excluye SIN_ASIGNAR). */
    public static Responsable asignableDesdeNombre(String nombre) {
        Responsable responsable = desdeNombre(nombre);
        if (responsable == SIN_ASIGNAR) {
            throw new DatoInvalidoException("responsable inválido");
        }
        return responsable;
    }
}
