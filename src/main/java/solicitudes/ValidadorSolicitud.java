package solicitudes;

import solicitudes.dominio.DatoInvalidoException;
import solicitudes.dominio.Prioridad;
import solicitudes.dominio.Tipo;

/** Valida los datos de una nueva solicitud. Devuelve 0 si son válidos o un código de error negativo. */
public class ValidadorSolicitud {
    public static final int OK = 0;
    public static final int NOMBRE_INVALIDO = -1;
    public static final int EMAIL_INVALIDO = -2;
    public static final int TIPO_INVALIDO = -3;
    public static final int PRIORIDAD_INVALIDA = -4;
    public static final int DESCRIPCION_INVALIDA = -5;

    public static int validar(NuevaSolicitud datos) {
        if (estaVacio(datos.nombre())) {
            return NOMBRE_INVALIDO;
        }
        if (!esEmailValido(datos.email())) {
            return EMAIL_INVALIDO;
        }
        if (!esTipoValido(datos.tipo())) {
            return TIPO_INVALIDO;
        }
        if (!esPrioridadValida(datos.prioridad())) {
            return PRIORIDAD_INVALIDA;
        }
        if (estaVacio(datos.descripcion())) {
            return DESCRIPCION_INVALIDA;
        }
        return OK;
    }

    private static boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private static boolean esEmailValido(String email) {
        return email != null && email.contains("@") && email.contains(".") && !email.startsWith("@");
    }

    private static boolean esTipoValido(String tipo) {
        try {
            Tipo.desdeTexto(tipo);
            return true;
        } catch (DatoInvalidoException e) {
            return false;
        }
    }

    private static boolean esPrioridadValida(int prioridad) {
        try {
            Prioridad.desdeNivel(prioridad);
            return true;
        } catch (DatoInvalidoException e) {
            return false;
        }
    }
}
