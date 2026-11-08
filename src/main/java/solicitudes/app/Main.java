package solicitudes.app;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import solicitudes.dominio.Estado;
import solicitudes.dominio.Prioridad;
import solicitudes.dominio.Responsable;
import solicitudes.dominio.Solicitud;
import solicitudes.dominio.SolicitudException;
import solicitudes.dominio.Tipo;
import solicitudes.persistencia.RepositorioCsv;
import solicitudes.reporte.ReporteTexto;
import solicitudes.servicio.Filtros;
import solicitudes.servicio.NuevaSolicitud;
import solicitudes.servicio.SolicitudService;

/** Demostración del sistema: ejecuta un escenario completo y muestra los resultados. */
public class Main {
    private static final Path ARCHIVO = Path.of("solicitudes.csv");

    private static void ejecutar(Runnable operacion, String mensajeExito) {
        try {
            operacion.run();
            System.out.println(mensajeExito);
        } catch (SolicitudException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void registrar(SolicitudService servicio, NuevaSolicitud datos) {
        try {
            Solicitud solicitud = servicio.registrar(datos);
            System.out.println("Solicitud registrada con id " + solicitud.getId());
        } catch (SolicitudException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void mostrarLista(String titulo, List<Solicitud> solicitudes) {
        List<Integer> ids = solicitudes.stream().map(Solicitud::getId).collect(Collectors.toList());
        System.out.println(titulo + ": " + ids);
    }

    public static void main(String[] args) {
        SolicitudService servicio = new SolicitudService(new RepositorioCsv(ARCHIVO));

        System.out.println("== 1. Registro de solicitudes ==");
        registrar(servicio, new NuevaSolicitud("Ana Gómez", "ana.gomez@mail.com", "20001", "CERTIFICADO", 1, "Certificado de alumno regular"));
        registrar(servicio, new NuevaSolicitud("Luis Pérez", "luis.perez@mail.com", "20002", "EQUIVALENCIA", 2, "Equivalencia de Matemática"));
        registrar(servicio, new NuevaSolicitud("Marta Ruiz", "marta.ruiz@mail.com", "20003", "BAJA", 3, "Baja de la materia Inglés"));
        registrar(servicio, new NuevaSolicitud("", "x@mail.com", "20004", "CONSULTA", 1, "Consulta general"));
        registrar(servicio, new NuevaSolicitud("Pedro Sosa", "sin-arroba", "20005", "CONSULTA", 1, "Consulta general"));
        registrar(servicio, new NuevaSolicitud("Pedro Sosa", "pedro@mail.com", "20005", "OTRO", 1, "Consulta general"));
        registrar(servicio, new NuevaSolicitud("Pedro Sosa", "pedro@mail.com", "20005", "CONSULTA", 7, "Consulta general"));
        registrar(servicio, new NuevaSolicitud("Pedro Sosa", "pedro@mail.com", "20005", "CONSULTA", 1, "  "));

        System.out.println("== 2. Cambios de estado ==");
        ejecutar(() -> servicio.cambiarEstado(1, "EN_PROCESO"), "Estado actualizado");
        ejecutar(() -> servicio.cambiarEstado(1, "RESUELTA"), "Estado actualizado");
        ejecutar(() -> servicio.cambiarEstado(1, "PENDIENTE"), "Estado actualizado");
        ejecutar(() -> servicio.cambiarEstado(99, "EN_PROCESO"), "Estado actualizado");
        ejecutar(() -> servicio.cambiarEstado(2, "INEXISTENTE"), "Estado actualizado");
        ejecutar(() -> servicio.cambiarEstado(3, "RECHAZADA"), "Estado actualizado");

        System.out.println("== 3. Asignación de responsable ==");
        ejecutar(() -> servicio.asignarResponsable(2, "Secretaría Académica"), "Responsable asignado");
        ejecutar(() -> servicio.asignarResponsable(1, "Bedelía"), "Responsable asignado");
        ejecutar(() -> servicio.asignarResponsable(2, "Nadie"), "Responsable asignado");
        ejecutar(() -> servicio.asignarResponsable(77, "Bedelía"), "Responsable asignado");

        System.out.println("== 4. Búsquedas ==");
        mostrarLista("Pendientes", servicio.buscar(Filtros.porEstado(Estado.PENDIENTE)));
        mostrarLista("Tipo BAJA", servicio.buscar(Filtros.porTipo(Tipo.BAJA)));
        mostrarLista("Responsable Secretaría Académica", servicio.buscar(Filtros.porResponsable(Responsable.SECRETARIA_ACADEMICA)));
        mostrarLista("Prioridad 1", servicio.buscar(Filtros.porPrioridad(Prioridad.ALTA)));

        System.out.println("== 5. Reporte ==");
        System.out.print(servicio.generarReporte(new ReporteTexto()));

        System.out.println("== 6. Persistencia ==");
        SolicitudService reiniciado = new SolicitudService(new RepositorioCsv(ARCHIVO));
        System.out.println("Solicitudes recuperadas del archivo: " + reiniciado.cantidad());
    }
}
