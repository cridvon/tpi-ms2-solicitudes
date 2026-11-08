package solicitudes.servicio;

/** Parameter Object: agrupa los datos necesarios para registrar una solicitud. */
public record NuevaSolicitud(String nombre, String email, String legajo,
                             String tipo, int prioridad, String descripcion) {
}
