package solicitudes;

import static solicitudes.MiniTest.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import solicitudes.dominio.Estado;
import solicitudes.dominio.Solicitud;
import solicitudes.persistencia.PersistenciaException;
import solicitudes.persistencia.RepositorioCsv;
import solicitudes.servicio.NuevaSolicitud;
import solicitudes.servicio.SolicitudService;

public class PersistenciaTest {
    private static Path temporal() {
        try {
            Path dir = Files.createTempDirectory("solicitudes-test");
            return dir.resolve("solicitudes.csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void ejecutar() {
        System.out.println("Persistencia");
        prueba("CP-21", "guardar y recargar conserva las solicitudes", () -> {
            Path archivo = temporal();
            SolicitudService s = new SolicitudService(new RepositorioCsv(archivo));
            s.registrar(new NuevaSolicitud("Ana Gómez", "ana@mail.com", "20001", "CERTIFICADO", 1, "Detalle"));
            s.cambiarEstado(1, "EN_PROCESO");
            SolicitudService recargado = new SolicitudService(new RepositorioCsv(archivo));
            assertEquals(1, recargado.cantidad());
            Solicitud cargada = new RepositorioCsv(archivo).buscarPorId(1).get();
            assertEquals(Estado.EN_PROCESO, cargada.getEstado());
            assertEquals("Ana Gómez", cargada.getEstudiante().getNombre());
        });
        prueba("CP-22", "lee el CSV generado por la versión inicial (compatibilidad)", () -> {
            RepositorioCsv repo = new RepositorioCsv(Path.of("docs/evidencias/solicitudes_esperado.csv"));
            assertEquals(3, repo.obtenerTodas().size());
            assertEquals(Estado.RECHAZADA, repo.buscarPorId(3).get().getEstado());
        });
        prueba("CP-23", "un archivo corrupto se informa (no se silencia el error)", () -> {
            Path archivo = temporal();
            try {
                Files.write(archivo, "esto;no;es;un;csv".getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            assertThrows(PersistenciaException.class, () -> new RepositorioCsv(archivo));
        });
        prueba("CP-24", "los ids continúan correlativos después de recargar", () -> {
            Path archivo = temporal();
            SolicitudService s = new SolicitudService(new RepositorioCsv(archivo));
            s.registrar(new NuevaSolicitud("Ana", "a@b.com", "1", "BAJA", 1, "x"));
            s.registrar(new NuevaSolicitud("Luis", "l@b.com", "2", "BAJA", 1, "y"));
            SolicitudService recargado = new SolicitudService(new RepositorioCsv(archivo));
            assertEquals(3, recargado.registrar(new NuevaSolicitud("Eva", "e@b.com", "3", "BAJA", 1, "z")).getId());
        });
        prueba("CP-25", "al guardar no queda un archivo temporal olvidado", () -> {
            Path archivo = temporal();
            SolicitudService s = new SolicitudService(new RepositorioCsv(archivo));
            s.registrar(new NuevaSolicitud("Ana", "a@b.com", "1", "BAJA", 1, "x"));
            assertTrue(Files.exists(archivo));
            assertFalse(Files.exists(archivo.resolveSibling("solicitudes.csv.tmp")));
        });
    }
}
