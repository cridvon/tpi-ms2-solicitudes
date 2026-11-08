package solicitudes.reporte;

import java.util.List;

import solicitudes.dominio.Estado;
import solicitudes.dominio.Estudiante;
import solicitudes.dominio.Solicitud;

/** Reporte en texto plano con el detalle de cada solicitud y un resumen por estado. */
public class ReporteTexto implements GeneradorReporte {

    @Override
    public String generar(List<Solicitud> solicitudes) {
        StringBuilder texto = new StringBuilder();
        texto.append("=== REPORTE DE SOLICITUDES ===\n");
        texto.append("Total: ").append(solicitudes.size()).append("\n");
        for (Solicitud solicitud : solicitudes) {
            agregarDetalle(texto, solicitud);
        }
        agregarResumenPorEstado(texto, solicitudes);
        return texto.toString();
    }

    private void agregarDetalle(StringBuilder texto, Solicitud solicitud) {
        Estudiante estudiante = solicitud.getEstudiante();
        texto.append("[").append(solicitud.getId()).append("] ")
                .append(solicitud.getTipo().name())
                .append(" | Prioridad: ").append(solicitud.getPrioridad().name())
                .append(" | Estado: ").append(solicitud.getEstado().name())
                .append(" | Resp: ").append(solicitud.getResponsable().nombre())
                .append(" | Plazo: ").append(solicitud.plazoDias()).append(" días\n");
        texto.append("    Estudiante: ").append(estudiante.getNombre())
                .append(" (").append(estudiante.getEmail()).append(") legajo ")
                .append(estudiante.getLegajo()).append("\n");
        texto.append("    Detalle: ").append(solicitud.getDescripcion()).append("\n");
    }

    private void agregarResumenPorEstado(StringBuilder texto, List<Solicitud> solicitudes) {
        texto.append("--- Resumen por estado ---\n");
        for (Estado estado : Estado.values()) {
            long cantidad = solicitudes.stream().filter(s -> s.getEstado() == estado).count();
            texto.append(estado.name()).append(": ").append(cantidad).append("\n");
        }
    }
}
