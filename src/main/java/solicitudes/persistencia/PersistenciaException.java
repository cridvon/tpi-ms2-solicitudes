package solicitudes.persistencia;

/** Falla al leer o escribir el almacenamiento. Nunca se silencia: se informa al llamador. */
public class PersistenciaException extends RuntimeException {
    public PersistenciaException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
