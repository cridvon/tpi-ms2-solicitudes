package solicitudes.persistencia;

import java.util.List;
import java.util.Optional;

import solicitudes.dominio.Solicitud;

/** Puerto de persistencia: el servicio depende de esta abstracción, no del almacenamiento concreto. */
public interface SolicitudRepository {
    int siguienteId();

    /** Inserta la solicitud o actualiza la existente. */
    void guardar(Solicitud solicitud);

    Optional<Solicitud> buscarPorId(int id);

    List<Solicitud> obtenerTodas();
}
