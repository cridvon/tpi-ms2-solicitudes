package solicitudes.dominio;

/** Se lanza cuando se opera sobre una solicitud que no existe. */
public class SolicitudNoEncontradaException extends SolicitudException {
    private final int id;

    public SolicitudNoEncontradaException(int id) {
        super("solicitud no encontrada");
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
