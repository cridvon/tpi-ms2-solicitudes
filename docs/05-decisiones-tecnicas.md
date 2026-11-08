# Decisiones técnicas (formato ADR)

## ADR-001 — Enums en lugar de `String`/`int` para tipo, estado, prioridad y responsable
- **Contexto:** la versión inicial comparaba cadenas literales; un error de tipeo pasaba el compilador y las reglas estaban repartidas en `if/else`.
- **Decisión:** modelar `Tipo`, `Estado`, `Prioridad` y `Responsable` como enums con comportamiento (transiciones, plazos, responsable inicial).
- **Consecuencias:** + el compilador detecta valores inválidos; + agregar un tipo es una línea; − hay que convertir texto a enum en el borde (`desdeTexto`).
- **Trade-off:** se gana seguridad y cohesión a cambio de algo más de código inicial.

## ADR-002 — Repositorio como interfaz (puertos y adaptadores livianos)
- **Contexto:** la lógica de negocio escribía directamente en `solicitudes.csv`.
- **Decisión:** `SolicitudRepository` con dos implementaciones: `RepositorioMemoria` y `RepositorioCsv`.
- **Consecuencias:** + el servicio se prueba sin disco (DIP); + se puede cambiar el almacenamiento (por ejemplo, a una base de datos) sin tocar el negocio; − una capa más de abstracción.
- **Alternativas descartadas:** arquitectura hexagonal completa o Clean Architecture: para un módulo de este tamaño serían sobreingeniería.

## ADR-003 — Excepciones no chequeadas con jerarquía propia
- **Contexto:** los códigos `-1…-5` no decían qué había pasado y podían ignorarse.
- **Decisión:** `SolicitudException` y subclases (`DatoInvalidoException`, `ReglaDeNegocioException`, `SolicitudNoEncontradaException`); `PersistenciaException` para fallas de almacenamiento.
- **Consecuencias:** + errores explícitos y con mensaje; + el borde (`Main`) decide cómo mostrarlos; − las excepciones no chequeadas no obligan a manejarlas en tiempo de compilación.

## ADR-004 — Predicados para búsquedas y estrategia para reportes
- **Contexto:** cuatro métodos de búsqueda duplicados y un reporte monolítico.
- **Decisión:** `buscar(Predicate<Solicitud>)` con `Filtros`, y la interfaz `GeneradorReporte` (patrón Strategy).
- **Consecuencias:** + criterios combinables y nuevos formatos de reporte sin modificar el servicio (OCP); − quien usa el servicio debe conocer `Filtros`.

## ADR-005 — Prueba de caracterización (golden master) antes de refactorizar
- **Contexto:** el sistema original no tenía pruebas y no era testeable de forma aislada.
- **Decisión:** fijar la salida de la demostración y el CSV generado como referencia y compararlos tras cada refactoring.
- **Consecuencias:** + seguridad para refactorizar; + se detectan cambios de comportamiento (ver prueba de sensibilidad); − solo cubre los caminos del escenario de demostración.

## ADR-006 — Mini framework de pruebas propio
- **Contexto:** el entorno de desarrollo no tenía acceso a Maven Central para descargar JUnit.
- **Decisión:** `MiniTest` (aserciones y ejecución) sin dependencias externas.
- **Consecuencias:** + el proyecto compila y se prueba solo con el JDK; − no ofrece el reporte, la integración con IDE ni las funciones avanzadas de JUnit. Migrar a JUnit 5 es directo (cambiar `prueba(...)` por `@Test`).
