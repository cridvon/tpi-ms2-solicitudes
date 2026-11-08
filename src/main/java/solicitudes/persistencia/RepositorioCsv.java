package solicitudes.persistencia;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import solicitudes.dominio.Estado;
import solicitudes.dominio.Estudiante;
import solicitudes.dominio.Prioridad;
import solicitudes.dominio.Responsable;
import solicitudes.dominio.Solicitud;
import solicitudes.dominio.Tipo;

/**
 * Repositorio que persiste en un archivo CSV (id;nombre;email;legajo;tipo;prioridad;estado;responsable;descripcion).
 * Mantiene el mismo formato que la versión inicial.
 */
public class RepositorioCsv extends RepositorioMemoria {
    private static final String SEPARADOR = ";";
    private final Path archivo;

    public RepositorioCsv(Path archivo) {
        this.archivo = archivo;
        cargar();
    }

    @Override
    public void guardar(Solicitud solicitud) {
        super.guardar(solicitud);
        escribir();
    }

    private void cargar() {
        if (!Files.exists(archivo)) {
            return;
        }
        try {
            for (String linea : Files.readAllLines(archivo, StandardCharsets.UTF_8)) {
                super.guardar(desdeLinea(linea));
            }
        } catch (IOException | RuntimeException e) {
            throw new PersistenciaException("No se pudo leer " + archivo, e);
        }
    }

    private void escribir() {
        List<String> lineas = new ArrayList<>();
        for (Solicitud solicitud : obtenerTodas()) {
            lineas.add(aLinea(solicitud));
        }
        try {
            Files.write(archivo, lineas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("No se pudo escribir " + archivo, e);
        }
    }

    private String aLinea(Solicitud s) {
        Estudiante e = s.getEstudiante();
        return String.join(SEPARADOR, String.valueOf(s.getId()), e.getNombre(), e.getEmail(), e.getLegajo(),
                s.getTipo().name(), String.valueOf(s.getPrioridad().nivel()), s.getEstado().name(),
                s.getResponsable().nombre(), s.getDescripcion());
    }

    private Solicitud desdeLinea(String linea) {
        String[] c = linea.split(SEPARADOR);
        return new Solicitud(Integer.parseInt(c[0]), new Estudiante(c[1], c[2], c[3]),
                Tipo.desdeTexto(c[4]), Prioridad.desdeNivel(Integer.parseInt(c[5])), c[8],
                Estado.desdeTexto(c[6]), Responsable.desdeNombre(c[7]));
    }
}
