package solicitudes;

import java.util.Objects;

/** Mini framework de pruebas sin dependencias externas (equivalente simplificado a JUnit). */
public final class MiniTest {
    private static int aprobadas = 0;
    private static int fallidas = 0;

    private MiniTest() {
    }

    public static void prueba(String id, String descripcion, Runnable cuerpo) {
        try {
            cuerpo.run();
            aprobadas++;
            System.out.println("  [OK]    " + id + " " + descripcion);
        } catch (Throwable t) {
            fallidas++;
            System.out.println("  [FALLA] " + id + " " + descripcion + " -> " + t);
        }
    }

    public static void assertEquals(Object esperado, Object actual) {
        if (!Objects.equals(esperado, actual)) {
            throw new AssertionError("esperado <" + esperado + "> pero fue <" + actual + ">");
        }
    }

    public static void assertTrue(boolean condicion) {
        if (!condicion) {
            throw new AssertionError("se esperaba true");
        }
    }

    public static void assertFalse(boolean condicion) {
        assertTrue(!condicion);
    }

    /** Verifica que se lance la excepción indicada y devuelve su mensaje. */
    public static String assertThrows(Class<? extends Throwable> tipo, Runnable cuerpo) {
        try {
            cuerpo.run();
        } catch (Throwable t) {
            if (tipo.isInstance(t)) {
                return t.getMessage();
            }
            throw new AssertionError("se esperaba " + tipo.getSimpleName() + " pero fue " + t);
        }
        throw new AssertionError("se esperaba " + tipo.getSimpleName() + " pero no hubo excepción");
    }

    public static int resumen() {
        System.out.println();
        System.out.println("Resultado: " + aprobadas + " aprobadas, " + fallidas + " fallidas, total " + (aprobadas + fallidas));
        return fallidas;
    }
}
