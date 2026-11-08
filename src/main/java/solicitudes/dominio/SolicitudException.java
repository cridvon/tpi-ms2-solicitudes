package solicitudes.dominio;

/** Raíz de las excepciones del dominio de solicitudes. */
public abstract class SolicitudException extends RuntimeException {
    protected SolicitudException(String mensaje) {
        super(mensaje);
    }
}
