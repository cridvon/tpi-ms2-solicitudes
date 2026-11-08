package solicitudes.dominio;

/** Prioridad de una solicitud (1 = alta, 3 = baja). */
public enum Prioridad {
    ALTA(1),
    MEDIA(2),
    BAJA(3);

    private final int nivel;

    Prioridad(int nivel) {
        this.nivel = nivel;
    }

    public int nivel() {
        return nivel;
    }

    public static Prioridad desdeNivel(int nivel) {
        for (Prioridad prioridad : values()) {
            if (prioridad.nivel == nivel) {
                return prioridad;
            }
        }
        throw new DatoInvalidoException("prioridad inválida (1-3)");
    }
}
