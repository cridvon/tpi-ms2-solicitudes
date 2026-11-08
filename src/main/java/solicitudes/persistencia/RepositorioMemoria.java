package solicitudes.persistencia;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import solicitudes.dominio.Solicitud;

/** Repositorio en memoria (útil para pruebas y como base del repositorio CSV). */
public class RepositorioMemoria implements SolicitudRepository {
    private final Map<Integer, Solicitud> solicitudes = new LinkedHashMap<>();

    @Override
    public int siguienteId() {
        return solicitudes.keySet().stream().mapToInt(Integer::intValue).max().orElse(0) + 1;
    }

    @Override
    public void guardar(Solicitud solicitud) {
        solicitudes.put(solicitud.getId(), solicitud);
    }

    @Override
    public Optional<Solicitud> buscarPorId(int id) {
        return Optional.ofNullable(solicitudes.get(id));
    }

    @Override
    public List<Solicitud> obtenerTodas() {
        return new ArrayList<>(solicitudes.values());
    }
}
