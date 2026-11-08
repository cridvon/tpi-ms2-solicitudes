package solicitudes;

import java.util.ArrayList;

import solicitudes.dominio.Solicitud;

public class Main {

    static void mostrarRegistro(int r) {
        if (r > 0) {
            System.out.println("Solicitud registrada con id " + r);
        } else if (r == -1) {
            System.out.println("ERROR: el nombre es obligatorio");
        } else if (r == -2) {
            System.out.println("ERROR: email inválido");
        } else if (r == -3) {
            System.out.println("ERROR: tipo de solicitud inválido");
        } else if (r == -4) {
            System.out.println("ERROR: prioridad inválida (1-3)");
        } else if (r == -5) {
            System.out.println("ERROR: la descripción es obligatoria");
        }
    }

    static void mostrarCambio(int r) {
        if (r == 0) {
            System.out.println("Estado actualizado");
        } else if (r == -1) {
            System.out.println("ERROR: solicitud no encontrada");
        } else if (r == -2) {
            System.out.println("ERROR: estado inválido");
        } else if (r == -3) {
            System.out.println("ERROR: transición de estado no permitida");
        }
    }

    static void mostrarAsignacion(int r) {
        if (r == 0) {
            System.out.println("Responsable asignado");
        } else if (r == -1) {
            System.out.println("ERROR: solicitud no encontrada");
        } else if (r == -2) {
            System.out.println("ERROR: responsable inválido");
        } else if (r == -3) {
            System.out.println("ERROR: la solicitud está cerrada");
        }
    }

    static void mostrarLista(String titulo, ArrayList<Solicitud> lista) {
        ArrayList<Integer> ids = new ArrayList<Integer>();
        for (int i = 0; i < lista.size(); i++) {
            ids.add(lista.get(i).getId());
        }
        System.out.println(titulo + ": " + ids);
    }

    public static void main(String[] args) {
        SolicitudManager m = new SolicitudManager();

        System.out.println("== 1. Registro de solicitudes ==");
        mostrarRegistro(m.registrar("Ana Gómez", "ana.gomez@mail.com", "20001", "CERTIFICADO", 1, "Certificado de alumno regular"));
        mostrarRegistro(m.registrar("Luis Pérez", "luis.perez@mail.com", "20002", "EQUIVALENCIA", 2, "Equivalencia de Matemática"));
        mostrarRegistro(m.registrar("Marta Ruiz", "marta.ruiz@mail.com", "20003", "BAJA", 3, "Baja de la materia Inglés"));
        mostrarRegistro(m.registrar("", "x@mail.com", "20004", "CONSULTA", 1, "Consulta general"));
        mostrarRegistro(m.registrar("Pedro Sosa", "sin-arroba", "20005", "CONSULTA", 1, "Consulta general"));
        mostrarRegistro(m.registrar("Pedro Sosa", "pedro@mail.com", "20005", "OTRO", 1, "Consulta general"));
        mostrarRegistro(m.registrar("Pedro Sosa", "pedro@mail.com", "20005", "CONSULTA", 7, "Consulta general"));
        mostrarRegistro(m.registrar("Pedro Sosa", "pedro@mail.com", "20005", "CONSULTA", 1, "  "));

        System.out.println("== 2. Cambios de estado ==");
        mostrarCambio(m.cambiarEstado(1, "EN_PROCESO"));
        mostrarCambio(m.cambiarEstado(1, "RESUELTA"));
        mostrarCambio(m.cambiarEstado(1, "PENDIENTE"));
        mostrarCambio(m.cambiarEstado(99, "EN_PROCESO"));
        mostrarCambio(m.cambiarEstado(2, "INEXISTENTE"));
        mostrarCambio(m.cambiarEstado(3, "RECHAZADA"));

        System.out.println("== 3. Asignación de responsable ==");
        mostrarAsignacion(m.asignarResponsable(2, "Secretaría Académica"));
        mostrarAsignacion(m.asignarResponsable(1, "Bedelía"));
        mostrarAsignacion(m.asignarResponsable(2, "Nadie"));
        mostrarAsignacion(m.asignarResponsable(77, "Bedelía"));

        System.out.println("== 4. Búsquedas ==");
        mostrarLista("Pendientes", m.buscarPorEstado("PENDIENTE"));
        mostrarLista("Tipo BAJA", m.buscarPorTipo("BAJA"));
        mostrarLista("Responsable Secretaría Académica", m.buscarPorResponsable("Secretaría Académica"));
        mostrarLista("Prioridad 1", m.buscarPorPrioridad(1));

        System.out.println("== 5. Reporte ==");
        System.out.print(m.reporte());

        System.out.println("== 6. Persistencia ==");
        SolicitudManager m2 = new SolicitudManager();
        System.out.println("Solicitudes recuperadas del archivo: " + m2.cantidad());
    }
}
