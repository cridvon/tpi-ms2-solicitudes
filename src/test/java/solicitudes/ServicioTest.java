package solicitudes;

import static solicitudes.MiniTest.*;

import solicitudes.dominio.*;
import solicitudes.persistencia.RepositorioMemoria;
import solicitudes.reporte.ReporteTexto;
import solicitudes.servicio.Filtros;
import solicitudes.servicio.NuevaSolicitud;
import solicitudes.servicio.SolicitudService;

public class ServicioTest {
    private static SolicitudService servicio() {
        return new SolicitudService(new RepositorioMemoria());
    }

    private static NuevaSolicitud datos(String tipo, int prioridad) {
        return new NuevaSolicitud("Ana Gómez", "ana@mail.com", "20001", tipo, prioridad, "Detalle");
    }

    public static void ejecutar() {
        System.out.println("Servicio");
        prueba("CP-10", "registrar asigna ids correlativos", () -> {
            SolicitudService s = servicio();
            assertEquals(1, s.registrar(datos("BAJA", 1)).getId());
            assertEquals(2, s.registrar(datos("BAJA", 1)).getId());
        });
        prueba("CP-11", "registrar rechaza un nombre vacío", () ->
                assertEquals("el nombre es obligatorio", assertThrows(DatoInvalidoException.class,
                        () -> servicio().registrar(new NuevaSolicitud(" ", "a@b.com", "1", "BAJA", 1, "x")))));
        prueba("CP-12", "registrar rechaza un email sin arroba", () ->
                assertEquals("email inválido", assertThrows(DatoInvalidoException.class,
                        () -> servicio().registrar(new NuevaSolicitud("Ana", "ana.mail.com", "1", "BAJA", 1, "x")))));
        prueba("CP-13", "registrar con tipo null informa error (antes: NullPointerException)", () ->
                assertEquals("tipo de solicitud inválido", assertThrows(DatoInvalidoException.class,
                        () -> servicio().registrar(new NuevaSolicitud("Ana", "a@b.com", "1", null, 1, "x")))));
        prueba("CP-14", "con varios errores se informa el primero (orden de validación)", () ->
                assertEquals("el nombre es obligatorio", assertThrows(DatoInvalidoException.class,
                        () -> servicio().registrar(new NuevaSolicitud("", "malo", "1", "OTRO", 9, "")))));
        prueba("CP-15", "cambiar estado de una solicitud inexistente", () ->
                assertEquals("solicitud no encontrada", assertThrows(SolicitudNoEncontradaException.class,
                        () -> servicio().cambiarEstado(5, "EN_PROCESO"))));
        prueba("CP-16", "cambiar estado con una transición no permitida", () -> {
            SolicitudService s = servicio();
            s.registrar(datos("BAJA", 1));
            assertEquals("transición de estado no permitida", assertThrows(ReglaDeNegocioException.class,
                    () -> s.cambiarEstado(1, "RESUELTA")));
        });
        prueba("CP-17", "SIN ASIGNAR no se puede asignar manualmente", () -> {
            SolicitudService s = servicio();
            s.registrar(datos("BAJA", 1));
            assertEquals("responsable inválido", assertThrows(DatoInvalidoException.class,
                    () -> s.asignarResponsable(1, "SIN ASIGNAR")));
        });
        prueba("CP-18", "los filtros se pueden combinar", () -> {
            SolicitudService s = servicio();
            s.registrar(datos("BAJA", 1));
            s.registrar(datos("CERTIFICADO", 1));
            s.registrar(datos("BAJA", 3));
            s.cambiarEstado(3, "EN_PROCESO");
            assertEquals(2, s.buscar(Filtros.porTipo(Tipo.BAJA)).size());
            assertEquals(1, s.buscar(Filtros.porTipo(Tipo.BAJA).and(Filtros.porEstado(Estado.EN_PROCESO))).size());
        });
        prueba("CP-19", "el reporte vacío muestra total 0 y resumen en cero", () -> {
            String reporte = servicio().generarReporte(new ReporteTexto());
            assertTrue(reporte.contains("Total: 0"));
            assertTrue(reporte.contains("PENDIENTE: 0"));
        });
        prueba("CP-20", "el reporte incluye plazo, responsable y estudiante", () -> {
            SolicitudService s = servicio();
            s.registrar(datos("EQUIVALENCIA", 2));
            String reporte = s.generarReporte(new ReporteTexto());
            assertTrue(reporte.contains("[1] EQUIVALENCIA | Prioridad: MEDIA | Estado: PENDIENTE | Resp: Dirección de Carrera | Plazo: 20 días"));
            assertTrue(reporte.contains("Estudiante: Ana Gómez (ana@mail.com) legajo 20001"));
        });
    }
}
