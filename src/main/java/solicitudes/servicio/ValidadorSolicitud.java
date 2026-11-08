package solicitudes.servicio;

import solicitudes.dominio.DatoInvalidoException;
import solicitudes.dominio.Prioridad;
import solicitudes.dominio.Tipo;

/** Valida los datos de una nueva solicitud y lanza DatoInvalidoException ante el primer error. */
public final class ValidadorSolicitud {

    private ValidadorSolicitud() {
    }

    public static void validar(NuevaSolicitud datos) {
        if (estaVacio(datos.nombre())) {
            throw new DatoInvalidoException("el nombre es obligatorio");
        }
        if (!esEmailValido(datos.email())) {
            throw new DatoInvalidoException("email inválido");
        }
        Tipo.desdeTexto(datos.tipo());
        Prioridad.desdeNivel(datos.prioridad());
        if (estaVacio(datos.descripcion())) {
            throw new DatoInvalidoException("la descripción es obligatoria");
        }
    }

    private static boolean estaVacio(String texto) {
        return texto == null || texto.trim().isEmpty();
    }

    private static boolean esEmailValido(String email) {
        return email != null && email.contains("@") && email.contains(".") && !email.startsWith("@");
    }
}
