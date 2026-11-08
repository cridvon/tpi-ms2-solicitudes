package solicitudes.reporte;

import java.util.List;

import solicitudes.dominio.Solicitud;

/** Estrategia para generar reportes: permite agregar formatos (HTML, CSV...) sin tocar el servicio. */
public interface GeneradorReporte {
    String generar(List<Solicitud> solicitudes);
}
