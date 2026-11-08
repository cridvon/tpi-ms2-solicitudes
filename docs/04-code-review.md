# Code review profesional

**Alcance:** versión refactorizada (`estado-refactorizado`), todo `src/main` y `src/test`.
**Método:** checklist técnico (sección 1), aplicado archivo por archivo; las observaciones se registran con severidad, feedback y acción (sección 2).
**Feedback:** se redacta sobre el código (no sobre las personas), con evidencia y una propuesta concreta.

## 1. Checklist técnico

Estado: ✔ cumple · ◐ cumple con observación · ✘ no cumple · ➜ corregido durante la revisión

| # | Categoría | Pregunta de la checklist | Estado | Referencia |
|---|---|---|---|---|
| 1 | Lógica y funcionalidad | ¿La versión refactorizada produce la misma salida que la inicial? | ✔ | `verificar.sh` en los 6 commits |
| 2 | Lógica y funcionalidad | ¿Se cubren los casos borde (nulos, ids inexistentes, transiciones inválidas)? | ➜ | OBS-02 |
| 3 | Lógica y funcionalidad | ¿El orden de validación se mantiene respecto de la versión inicial? | ✔ | CP-14 |
| 4 | Legibilidad | ¿Los nombres expresan la intención (sin `l`, `s`, `p/e/r/x`)? | ✔ | todo el código |
| 5 | Legibilidad | ¿Las funciones son cortas y hacen una sola cosa? | ◐ | OBS-05 (`Main.main`) |
| 6 | Legibilidad | ¿Hay comentarios útiles y no comentarios que expliquen código confuso? | ✔ | Javadoc en las clases |
| 7 | Diseño | ¿Cada clase tiene una única responsabilidad (SRP)? | ✔ | `SolicitudService` 65 líneas |
| 8 | Diseño | ¿Se puede extender sin modificar (OCP)? | ✔ | nuevo `Tipo` = 1 línea; nuevo reporte = 1 clase |
| 9 | Diseño | ¿Se depende de abstracciones (DIP)? | ✔ | `SolicitudRepository`, `GeneradorReporte` |
| 10 | Diseño | ¿Se evita código duplicado (DRY)? | ◐ | OBS-04 |
| 11 | Diseño | ¿Hay bajo acoplamiento y límites claros entre paquetes? | ✔ | `dominio` no depende de nadie |
| 12 | Errores | ¿Se usan excepciones con mensaje y sin `catch` vacío? | ✔ | `PersistenciaException` |
| 13 | Seguridad / datos | ¿Se validan las entradas y se evita corromper el almacenamiento? | ◐ ➜ | OBS-01, OBS-03 |
| 14 | Rendimiento | ¿Hay recorridos o consultas innecesarias? | ◐ | OBS-06 |
| 15 | Pruebas | ¿Hay pruebas para los casos relevantes y son repetibles? | ✔ | 27 casos, `probar.sh` |
| 16 | Documentación | ¿README, decisiones y evidencias están actualizados? | ✔ | `/docs` |

## 2. Observaciones y feedback

| ID | Archivo | Observación | Severidad | Feedback propuesto | Acción |
|---|---|---|---|---|---|
| OBS-01 | `RepositorioCsv` | Un `;` dentro de nombre, legajo o descripción rompe el formato del CSV (heredado de la versión inicial). | Media | Sugerencia: escapar el separador o validar los campos. Se difiere porque cambiaría el comportamiento externo y requiere definir la regla con la cátedra/usuario. | **Pendiente** (mejora futura) |
| OBS-02 | `SolicitudNoEncontradaException` | El constructor recibía el `id` pero lo descartaba: se perdía contexto útil para diagnosticar. | Baja | Guardar el id y exponerlo con `getId()`. | **Corregido** (CP-15b) |
| OBS-03 | `RepositorioCsv.escribir` | La escritura directa del archivo puede dejarlo incompleto si el proceso se interrumpe. | Media | Escribir en un archivo temporal y reemplazar el original. | **Corregido** (CP-25) |
| OBS-04 | `Main` | `registrar` y `ejecutar` repiten el mismo `try/catch` de `SolicitudException`. | Baja | Unificar con un único método genérico. Se deja: es código de demostración y no de negocio. | Aceptada |
| OBS-05 | `Main.main` | 40 líneas: es un guion lineal de demostración, no lógica. | Baja | Dividir en métodos por escenario si el guion crece. | Aceptada |
| OBS-06 | `RepositorioMemoria.siguienteId` | Recorre todos los ids en cada alta (O(n)). | Baja | Mantener un contador. Para el volumen del sistema no es relevante. | Aceptada |
| OBS-07 | `ValidadorSolicitud.validar` | `registrar(null)` lanzaba un `NullPointerException` sin explicación. | Baja | `Objects.requireNonNull` con mensaje. | **Corregido** (CP-15c) |
| OBS-08 | `RepositorioMemoria.obtenerTodas` | Devuelve una copia de la lista, pero los objetos `Solicitud` son mutables: quien los reciba podría cambiar un estado sin pasar por el servicio (y sin persistir). | Baja | Aceptable en esta escala; a futuro, exponer vistas de solo lectura. | Aceptada |

### Aspectos positivos registrados
- Las reglas de negocio (transiciones, plazos, responsable por defecto) están donde corresponde y se pueden probar de forma aislada.
- La prueba de caracterización con comparación de salida da confianza real para refactorizar.
- La compatibilidad de datos con la versión inicial se verifica (CP-22).

## 3. Correcciones realizadas
OBS-02, OBS-03 y OBS-07 se corrigieron en el commit "Code review: correcciones…", con pruebas nuevas (CP-15b, CP-15c, CP-25). Tras las correcciones: 27/27 pruebas aprobadas y `verificar.sh` sin diferencias.

## 4. Criterios de aceptación técnica (TAC)

| Criterio | Cumple |
|---|---|
| Compila sin errores ni advertencias relevantes | ✔ |
| El comportamiento externo coincide con la versión inicial | ✔ |
| Todas las pruebas automatizadas pasan (27/27) | ✔ |
| Sin `catch` vacíos ni códigos de error numéricos | ✔ |
| Sin referencias a infraestructura concreta en la lógica de negocio | ✔ |
| Observaciones de severidad media/alta resueltas o justificadas | ✔ (OBS-03 corregida, OBS-01 justificada y registrada como mejora futura) |
| Documentación y evidencias actualizadas | ✔ |

**Resultado de la revisión: aprobado**, con una mejora pendiente registrada (OBS-01).
