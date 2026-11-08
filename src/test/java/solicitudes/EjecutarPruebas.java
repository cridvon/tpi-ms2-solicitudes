package solicitudes;

/** Ejecuta todas las pruebas. Devuelve código de salida distinto de cero si alguna falla. */
public class EjecutarPruebas {
    public static void main(String[] args) {
        DominioTest.ejecutar();
        ServicioTest.ejecutar();
        PersistenciaTest.ejecutar();
        System.exit(MiniTest.resumen() == 0 ? 0 : 1);
    }
}
