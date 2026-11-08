package solicitudes.servicio;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import solicitudes.dominio.Estado;
import solicitudes.dominio.Estudiante;
import solicitudes.dominio.Prioridad;
import solicitudes.dominio.Responsable;
import solicitudes.dominio.Solicitud;
import solicitudes.dominio.SolicitudNoEncontradaException;
import solicitudes.dominio.Tipo;
import solicitudes.persistencia.SolicitudRepository;
import solicitudes.reporte.GeneradorReporte;

/** Casos de uso del sistema. Depende de abstracciones (repositorio y generador de reportes). */
public class SolicitudService {
    private final SolicitudRepository repositorio;

    public SolicitudService(SolicitudRepository repositorio) {
        this.repositorio = repositorio;
    }

    public Solicitud registrar(NuevaSolicitud datos) {
        ValidadorSolicitud.validar(datos);
        Solicitud solicitud = crearSolicitud(datos);
        repositorio.guardar(solicitud);
        return solicitud;
    }

    public void cambiarEstado(int id, String nuevoEstado) {
        Solicitud solicitud = obtener(id);
        solicitud.cambiarEstado(Estado.desdeTexto(nuevoEstado));
        repositorio.guardar(solicitud);
    }

    public void asignarResponsable(int id, String nombreResponsable) {
        Solicitud solicitud = obtener(id);
        solicitud.asignarA(Responsable.asignableDesdeNombre(nombreResponsable));
        repositorio.guardar(solicitud);
    }

    public List<Solicitud> buscar(Predicate<Solicitud> criterio) {
        return repositorio.obtenerTodas().stream().filter(criterio).collect(Collectors.toList());
    }

    public int cantidad() {
        return repositorio.obtenerTodas().size();
    }

    public String generarReporte(GeneradorReporte generador) {
        return generador.generar(repositorio.obtenerTodas());
    }

    private Solicitud obtener(int id) {
        return repositorio.buscarPorId(id).orElseThrow(() -> new SolicitudNoEncontradaException(id));
    }

    private Solicitud crearSolicitud(NuevaSolicitud datos) {
        Estudiante estudiante = new Estudiante(datos.nombre(), datos.email(), datos.legajo());
        return new Solicitud(repositorio.siguienteId(), estudiante, Tipo.desdeTexto(datos.tipo()),
                Prioridad.desdeNivel(datos.prioridad()), datos.descripcion());
    }
}
