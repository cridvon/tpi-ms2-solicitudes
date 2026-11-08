package solicitudes.dominio;

/** Se lanza cuando se opera sobre una solicitud que no existe. */
public class SolicitudNoEncontradaException extends SolicitudException {
    public SolicitudNoEncontradaException(int id) {
        super("solicitud no encontrada");
    }
}
