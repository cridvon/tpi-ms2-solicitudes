package solicitudes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;

import solicitudes.dominio.DatoInvalidoException;
import solicitudes.dominio.Estado;
import solicitudes.dominio.Estudiante;
import solicitudes.dominio.Prioridad;
import solicitudes.dominio.ReglaDeNegocioException;
import solicitudes.dominio.Responsable;
import solicitudes.dominio.Solicitud;
import solicitudes.dominio.Tipo;

public class SolicitudManager {
    private ArrayList<Solicitud> solicitudes = new ArrayList<Solicitud>();
    private int siguienteId = 1;

    public SolicitudManager() {
        cargar();
    }

    public int cantidad() {
        return solicitudes.size();
    }

    // devuelve el id (> 0) o un codigo de error (< 0), ver ValidadorSolicitud
    public int registrar(NuevaSolicitud datos) {
        int codigo = ValidadorSolicitud.validar(datos);
        if (codigo != ValidadorSolicitud.OK) {
            return codigo;
        }
        Solicitud solicitud = crearSolicitud(datos);
        solicitudes.add(solicitud);
        guardar();
        return solicitud.getId();
    }

    private Solicitud crearSolicitud(NuevaSolicitud datos) {
        Estudiante estudiante = new Estudiante(datos.nombre(), datos.email(), datos.legajo());
        return new Solicitud(siguienteId++, estudiante, Tipo.desdeTexto(datos.tipo()),
                Prioridad.desdeNivel(datos.prioridad()), datos.descripcion());
    }

    // 0 ok, -1 no existe, -2 estado invalido, -3 transicion no permitida
    public int cambiarEstado(int id, String nuevoEstado) {
        Solicitud solicitud = buscarPorId(id);
        if (solicitud == null) {
            return -1;
        }
        Estado estado;
        try {
            estado = Estado.desdeTexto(nuevoEstado);
        } catch (DatoInvalidoException e) {
            return -2;
        }
        try {
            solicitud.cambiarEstado(estado);
        } catch (ReglaDeNegocioException e) {
            return -3;
        }
        guardar();
        return 0;
    }

    // 0 ok, -1 no existe, -2 responsable invalido, -3 solicitud cerrada
    public int asignarResponsable(int id, String nombreResponsable) {
        Solicitud solicitud = buscarPorId(id);
        if (solicitud == null) {
            return -1;
        }
        Responsable responsable;
        try {
            responsable = Responsable.asignableDesdeNombre(nombreResponsable);
        } catch (DatoInvalidoException e) {
            return -2;
        }
        try {
            solicitud.asignarA(responsable);
        } catch (ReglaDeNegocioException e) {
            return -3;
        }
        guardar();
        return 0;
    }

    private Solicitud buscarPorId(int id) {
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getId() == id) {
                return solicitud;
            }
        }
        return null;
    }

    public ArrayList<Solicitud> buscarPorEstado(String estado) {
        ArrayList<Solicitud> resultado = new ArrayList<Solicitud>();
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getEstado().name().equals(estado)) {
                resultado.add(solicitud);
            }
        }
        return resultado;
    }

    public ArrayList<Solicitud> buscarPorTipo(String tipo) {
        ArrayList<Solicitud> resultado = new ArrayList<Solicitud>();
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getTipo().name().equals(tipo)) {
                resultado.add(solicitud);
            }
        }
        return resultado;
    }

    public ArrayList<Solicitud> buscarPorResponsable(String responsable) {
        ArrayList<Solicitud> resultado = new ArrayList<Solicitud>();
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getResponsable().nombre().equals(responsable)) {
                resultado.add(solicitud);
            }
        }
        return resultado;
    }

    public ArrayList<Solicitud> buscarPorPrioridad(int prioridad) {
        ArrayList<Solicitud> resultado = new ArrayList<Solicitud>();
        for (Solicitud solicitud : solicitudes) {
            if (solicitud.getPrioridad().nivel() == prioridad) {
                resultado.add(solicitud);
            }
        }
        return resultado;
    }

    public String reporte() {
        StringBuilder texto = new StringBuilder();
        texto.append("=== REPORTE DE SOLICITUDES ===\n");
        texto.append("Total: ").append(solicitudes.size()).append("\n");
        int[] porEstado = new int[Estado.values().length];
        for (Solicitud solicitud : solicitudes) {
            texto.append("[").append(solicitud.getId()).append("] ")
                    .append(solicitud.getTipo().name())
                    .append(" | Prioridad: ").append(solicitud.getPrioridad().name())
                    .append(" | Estado: ").append(solicitud.getEstado().name())
                    .append(" | Resp: ").append(solicitud.getResponsable().nombre())
                    .append(" | Plazo: ").append(solicitud.plazoDias()).append(" días\n");
            texto.append("    Estudiante: ").append(solicitud.getEstudiante().getNombre())
                    .append(" (").append(solicitud.getEstudiante().getEmail()).append(") legajo ")
                    .append(solicitud.getEstudiante().getLegajo()).append("\n");
            texto.append("    Detalle: ").append(solicitud.getDescripcion()).append("\n");
            porEstado[solicitud.getEstado().ordinal()]++;
        }
        texto.append("--- Resumen por estado ---\n");
        for (Estado estado : Estado.values()) {
            texto.append(estado.name()).append(": ").append(porEstado[estado.ordinal()]).append("\n");
        }
        return texto.toString();
    }

    void guardar() {
        try {
            PrintWriter w = new PrintWriter("solicitudes.csv");
            for (Solicitud solicitud : solicitudes) {
                Estudiante estudiante = solicitud.getEstudiante();
                w.println(solicitud.getId() + ";" + estudiante.getNombre() + ";" + estudiante.getEmail() + ";"
                        + estudiante.getLegajo() + ";" + solicitud.getTipo().name() + ";"
                        + solicitud.getPrioridad().nivel() + ";" + solicitud.getEstado().name() + ";"
                        + solicitud.getResponsable().nombre() + ";" + solicitud.getDescripcion());
            }
            w.close();
        } catch (Exception e) {
        }
    }

    void cargar() {
        File archivo = new File("solicitudes.csv");
        if (!archivo.exists()) {
            return;
        }
        try {
            BufferedReader lector = new BufferedReader(new FileReader(archivo));
            String linea = lector.readLine();
            while (linea != null) {
                String[] c = linea.split(";");
                int id = Integer.parseInt(c[0]);
                Solicitud solicitud = new Solicitud(id, new Estudiante(c[1], c[2], c[3]),
                        Tipo.desdeTexto(c[4]), Prioridad.desdeNivel(Integer.parseInt(c[5])), c[8],
                        Estado.desdeTexto(c[6]), Responsable.desdeNombre(c[7]));
                solicitudes.add(solicitud);
                if (id >= siguienteId) {
                    siguienteId = id + 1;
                }
                linea = lector.readLine();
            }
            lector.close();
        } catch (Exception e) {
            System.out.println("Error al cargar");
        }
    }
}
