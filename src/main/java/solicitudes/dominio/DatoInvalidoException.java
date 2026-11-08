package solicitudes.dominio;

/** Se lanza cuando un dato de entrada no cumple las reglas del dominio. */
public class DatoInvalidoException extends SolicitudException {
    public DatoInvalidoException(String mensaje) {
        super(mensaje);
    }
}
