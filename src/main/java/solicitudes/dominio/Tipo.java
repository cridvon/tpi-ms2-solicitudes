package solicitudes.dominio;

/** Tipo de solicitud: define el responsable inicial y los plazos de atención por prioridad. */
public enum Tipo {
    CERTIFICADO(Responsable.BEDELIA, 3, 5, 10),
    EQUIVALENCIA(Responsable.DIRECCION_CARRERA, 15, 20, 30),
    BAJA(Responsable.SECRETARIA_ACADEMICA, 5, 7, 10),
    CONSULTA(Responsable.SIN_ASIGNAR, 2, 5, 7);

    private final Responsable responsableInicial;
    private final int plazoAlta;
    private final int plazoMedia;
    private final int plazoBaja;

    Tipo(Responsable responsableInicial, int plazoAlta, int plazoMedia, int plazoBaja) {
        this.responsableInicial = responsableInicial;
        this.plazoAlta = plazoAlta;
        this.plazoMedia = plazoMedia;
        this.plazoBaja = plazoBaja;
    }

    public Responsable responsableInicial() {
        return responsableInicial;
    }

    public int plazoDias(Prioridad prioridad) {
        switch (prioridad) {
            case ALTA:
                return plazoAlta;
            case MEDIA:
                return plazoMedia;
            default:
                return plazoBaja;
        }
    }

    public static Tipo desdeTexto(String texto) {
        for (Tipo tipo : values()) {
            if (tipo.name().equals(texto)) {
                return tipo;
            }
        }
        throw new DatoInvalidoException("tipo de solicitud inválido");
    }
}
