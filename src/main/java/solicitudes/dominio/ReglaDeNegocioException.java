package solicitudes.dominio;

/** Se lanza cuando una operación viola una regla de negocio (por ejemplo, una transición de estado no permitida). */
public class ReglaDeNegocioException extends SolicitudException {
    public ReglaDeNegocioException(String mensaje) {
        super(mensaje);
    }
}
