# Refactorings aplicados (antes / después)

Cada refactoring es un commit independiente y se verificó con `scripts/verificar.sh` (ver `docs/evidencias/verificacion_por_commit.txt`).

## R1. Replace Type Code with Enum + Encapsulate Field + Move Method (commit `592b1f1`)
**Smells:** obsesión por primitivos, valores mágicos, campos públicos, envidia de atributos, condicionales por tipo/estado, nombres poco expresivos.

Antes (`SolicitudManager`, versión inicial):
```java
if (s.estado.equals("PENDIENTE")) {
    if (!(nuevo.equals("EN_PROCESO") || nuevo.equals("RECHAZADA"))) { return -3; }
} else if (s.estado.equals("EN_PROCESO")) {
    if (!(nuevo.equals("RESUELTA") || nuevo.equals("RECHAZADA"))) { return -3; }
} else { return -3; }
s.estado = nuevo;
```
Después (`Estado` y `Solicitud`):
```java
public boolean puedePasarA(Estado destino) {
    switch (this) {
        case PENDIENTE:  return destino == EN_PROCESO || destino == RECHAZADA;
        case EN_PROCESO: return destino == RESUELTA || destino == RECHAZADA;
        default:         return false;
    }
}
public void cambiarEstado(Estado nuevo) {
    if (!estado.puedePasarA(nuevo)) { throw new ReglaDeNegocioException("transición de estado no permitida"); }
    estado = nuevo;
}
```
Beneficio: la regla vive junto al dato que gobierna (cohesión), el compilador impide estados inexistentes y los plazos (12 `if/else` anidados) pasan a `Tipo.plazoDias(Prioridad)`.

## R2. Introduce Parameter Object + Extract Class + Extract Method (commit `048e111`)
**Smells:** lista larga de parámetros, método largo, clase gigante.

Antes: `registrar(String nombre, String email, String legajo, String tipo, int prioridad, String descripcion)` con 40 líneas de validación y construcción.
Después:
```java
public record NuevaSolicitud(String nombre, String email, String legajo, String tipo, int prioridad, String descripcion) {}

public static void validar(NuevaSolicitud datos) {          // ValidadorSolicitud
    if (estaVacio(datos.nombre())) { throw new DatoInvalidoException("el nombre es obligatorio"); }
    if (!esEmailValido(datos.email())) { throw new DatoInvalidoException("email inválido"); }
    ...
}
```
Beneficio: una llamada se lee como un dato (`new NuevaSolicitud(...)`) y cada regla de validación tiene nombre propio.

## R3. Extract Class + Parameterize Method (commit `e032241`)
**Smells:** método largo (`reporte()` de 69 líneas), código duplicado (cuatro `buscarPor*`), clase gigante, viola OCP.

Antes: cuatro métodos que repetían el mismo bucle cambiando solo la condición, y un `reporte()` monolítico.
Después:
```java
public interface GeneradorReporte { String generar(List<Solicitud> solicitudes); }   // nuevos formatos sin tocar el servicio

public static Predicate<Solicitud> porEstado(Estado estado) { return s -> s.getEstado() == estado; }
public List<Solicitud> buscar(Predicate<Solicitud> criterio) { ... }
```
Beneficio: una sola implementación de búsqueda, criterios combinables (`porTipo(BAJA).and(porEstado(PENDIENTE))`) y reporte dividido en métodos de ≤ 13 líneas.

## R4. Extract Interface + Replace Error Code with Exception + reorganización en paquetes (commit `c299a1e`)
**Smells:** códigos de error, catch vacío, acoplamiento a infraestructura (viola DIP), clase gigante, defecto latente `tipo == null`.

Antes:
```java
void guardar() {
    try { PrintWriter w = new PrintWriter("solicitudes.csv"); ... w.close(); }
    catch (Exception e) { }                       // el error se pierde
}
public int cambiarEstado(int id, String nuevo) { ... return -3; }   // ¿qué significa -3?
```
Después:
```java
public interface SolicitudRepository { int siguienteId(); void guardar(Solicitud s); Optional<Solicitud> buscarPorId(int id); List<Solicitud> obtenerTodas(); }

public SolicitudService(SolicitudRepository repositorio) { this.repositorio = repositorio; }   // DIP

throw new PersistenciaException("No se pudo escribir " + archivo, e);                          // el error se informa
```
Beneficio: el servicio se prueba con `RepositorioMemoria` (sin disco), los errores no se pierden y cada excepción dice qué ocurrió.

## Resultado en métricas

| Métrica | Versión inicial | Versión refactorizada |
|---|---|---|
| Clase más grande | `SolicitudManager`: 276 líneas, 6 responsabilidades | `Main` (guion de demostración): 86 líneas; en el código de negocio, `Solicitud`: 75 líneas |
| Método más largo | `reporte()`: 69 líneas | 13 líneas en el código de negocio (`Main.main` es un guion lineal de 40 líneas) |
| Archivos de código | 5 | 21 (paquetes `dominio`, `servicio`, `persistencia`, `reporte`, `app`) |
| Errores | códigos `-1…-5` y `catch` vacío | excepciones con mensaje |
| Pruebas automatizadas | 0 (no se podía probar sin disco) | 27 (`docs/evidencias/resultado_pruebas.txt`) |
| Pasos para agregar un tipo de solicitud | modificar 3 métodos | 1 línea en el enum `Tipo` |

## Mejora de comportamiento declarada
Los refactorings no cambian el comportamiento externo salvo en un caso límite: `registrar` con `tipo == null`. Antes lanzaba `NullPointerException`; ahora responde `ERROR: tipo de solicitud inválido` (corrección de un defecto latente, prueba CP-13). Además, un `solicitudes.csv` corrupto ahora se informa (`PersistenciaException`) en lugar de ignorarse.
