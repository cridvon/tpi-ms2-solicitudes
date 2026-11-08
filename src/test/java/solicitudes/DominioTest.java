package solicitudes;

import static solicitudes.MiniTest.*;

import solicitudes.dominio.*;

public class DominioTest {
    private static Solicitud nueva(Tipo tipo, Prioridad prioridad) {
        return new Solicitud(1, new Estudiante("Ana", "ana@mail.com", "1"), tipo, prioridad, "detalle");
    }

    public static void ejecutar() {
        System.out.println("Dominio");
        prueba("CP-01", "PENDIENTE puede pasar a EN_PROCESO y RECHAZADA", () -> {
            assertTrue(Estado.PENDIENTE.puedePasarA(Estado.EN_PROCESO));
            assertTrue(Estado.PENDIENTE.puedePasarA(Estado.RECHAZADA));
        });
        prueba("CP-02", "PENDIENTE no puede pasar directo a RESUELTA", () ->
                assertFalse(Estado.PENDIENTE.puedePasarA(Estado.RESUELTA)));
        prueba("CP-03", "los estados finales no admiten ninguna transición", () -> {
            for (Estado destino : Estado.values()) {
                assertFalse(Estado.RESUELTA.puedePasarA(destino));
                assertFalse(Estado.RECHAZADA.puedePasarA(destino));
            }
        });
        prueba("CP-04", "Estado.desdeTexto rechaza un texto desconocido", () ->
                assertEquals("estado inválido", assertThrows(DatoInvalidoException.class, () -> Estado.desdeTexto("pendiente"))));
        prueba("CP-05", "los plazos dependen del tipo y de la prioridad", () -> {
            assertEquals(3, Tipo.CERTIFICADO.plazoDias(Prioridad.ALTA));
            assertEquals(30, Tipo.EQUIVALENCIA.plazoDias(Prioridad.BAJA));
            assertEquals(5, Tipo.CONSULTA.plazoDias(Prioridad.MEDIA));
        });
        prueba("CP-06", "cada tipo define su responsable inicial", () -> {
            assertEquals(Responsable.BEDELIA, Tipo.CERTIFICADO.responsableInicial());
            assertEquals(Responsable.SIN_ASIGNAR, Tipo.CONSULTA.responsableInicial());
        });
        prueba("CP-07", "una solicitud nueva nace PENDIENTE con el responsable de su tipo", () -> {
            Solicitud s = nueva(Tipo.BAJA, Prioridad.BAJA);
            assertEquals(Estado.PENDIENTE, s.getEstado());
            assertEquals(Responsable.SECRETARIA_ACADEMICA, s.getResponsable());
        });
        prueba("CP-08", "no se puede asignar responsable a una solicitud cerrada", () -> {
            Solicitud s = nueva(Tipo.CERTIFICADO, Prioridad.ALTA);
            s.cambiarEstado(Estado.RECHAZADA);
            assertEquals("la solicitud está cerrada", assertThrows(ReglaDeNegocioException.class, () -> s.asignarA(Responsable.BEDELIA)));
        });
        prueba("CP-09", "Prioridad.desdeNivel rechaza valores fuera de 1-3", () -> {
            assertThrows(DatoInvalidoException.class, () -> Prioridad.desdeNivel(0));
            assertThrows(DatoInvalidoException.class, () -> Prioridad.desdeNivel(4));
        });
    }
}
