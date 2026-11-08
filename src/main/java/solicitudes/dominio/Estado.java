package solicitudes.dominio;

/** Estado de una solicitud. Conoce las transiciones permitidas. */
public enum Estado {
    PENDIENTE,
    EN_PROCESO,
    RESUELTA,
    RECHAZADA;

    public boolean puedePasarA(Estado destino) {
        switch (this) {
            case PENDIENTE:
                return destino == EN_PROCESO || destino == RECHAZADA;
            case EN_PROCESO:
                return destino == RESUELTA || destino == RECHAZADA;
            default:
                return false;
        }
    }

    public boolean esFinal() {
        return this == RESUELTA || this == RECHAZADA;
    }

    public static Estado desdeTexto(String texto) {
        for (Estado estado : values()) {
            if (estado.name().equals(texto)) {
                return estado;
            }
        }
        throw new DatoInvalidoException("estado inválido");
    }
}
