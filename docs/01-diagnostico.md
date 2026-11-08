# Diagnóstico de la versión inicial (`estado-inicial`)

## 1. Calidad externa vs. calidad interna

**Calidad externa (lo que ve el usuario).** El sistema funciona: registra solicitudes, valida los datos, cambia el estado respetando el flujo `PENDIENTE → EN_PROCESO → RESUELTA/RECHAZADA`, asigna responsables, filtra y genera un reporte, y conserva los datos entre ejecuciones (`solicitudes.csv`). La salida de la demostración (`docs/evidencias/salida_esperada.txt`) es correcta y consistente: los errores se informan con mensajes claros y el reporte calcula bien los plazos.

**Calidad interna (lo que ve quien mantiene el código).** La estructura es deficiente aunque nada de eso se note en la interfaz:

| Dimensión | Estado en la versión inicial | Consecuencia para la evolución |
|---|---|---|
| Legibilidad | Nombres como `l`, `sig`, `s`, `p/e/r/x`, métodos de hasta 68 líneas | Hay que leer todo el método para entender una regla |
| Mantenibilidad | Una sola clase (`SolicitudManager`, 276 líneas) concentra 6 responsabilidades | Cualquier cambio toca la misma clase y puede romper otra función |
| Extensibilidad | Agregar un tipo de solicitud obliga a editar 3 métodos distintos | Cada novedad del negocio cuesta más que la anterior |
| Testeabilidad | La ruta `solicitudes.csv` está fija en el constructor | No se puede probar la lógica sin escribir en disco |
| Robustez | Errores como números negativos y `catch` vacío | Las fallas pasan inadvertidas o se interpretan mal |

**Relación con la evolución futura.** Un pedido simple como "agregar el tipo `CERTIFICADO_ANALITICO` con plazos propios" requiere hoy modificar `registrar` (validación y responsable por defecto), `reporte` (plazos) y todas las comparaciones de `String`. Es el patrón típico de deuda técnica: el sistema *funciona*, pero cada cambio es más caro y riesgoso (costo del cambio creciente).

## 2. Deuda técnica y code smells

Referencias de ubicación: archivo `SolicitudManager.java` (SM) y `Main.java` salvo indicación.

| N.º | Code smell / debilidad | Evidencia y ubicación | Impacto | Riesgo de mantenimiento | Prioridad |
|---|---|---|---|---|---|
| 1 | **Clase gigante (Large Class / God Class)** | `SolicitudManager` (276 líneas) valida, aplica reglas, busca, genera el reporte, persiste y asigna ids (SM, 9–276) | Viola SRP; cualquier cambio toca esta clase | Muy alto: cambios cruzados y conflictos de merge | **Alta** |
| 2 | **Método largo (Long Method)** | `reporte()` con ~68 líneas (SM, 162–230); `registrar()` ~40 líneas (SM, 23–62) | Difícil de leer y de probar por partes | Alto | **Alta** |
| 3 | **Lista larga de parámetros** | `registrar(nombre, email, legajo, tipo, prioridad, descripcion)` (SM, 23); parámetros del mismo tipo fáciles de intercambiar | Llamadas propensas a error | Medio | Media |
| 4 | **Obsesión por primitivos** | `tipo`, `estado` y `responsable` son `String`; `prioridad` es `int`; se comparan con literales (SM, 30, 76, 109) | Un error de tipeo ("PENDIENTE " o "pendiente") pasa el compilador | Alto | **Alta** |
| 5 | **Código duplicado** | Cuatro métodos `buscarPor*` idénticos salvo la condición (SM, 122–160); búsqueda por id repetida en `cambiarEstado` y `asignarResponsable` (SM, 66–71, 96–101) | Un cambio hay que replicarlo en varios lugares | Alto | **Alta** |
| 6 | **Valores mágicos** | Plazos `3, 5, 10, 15, 20, 30…` (SM, 178–210); estados y tipos como cadenas literales; prioridades `1, 2, 3` | No expresan intención; difícil cambiar una política | Medio | Media |
| 7 | **Códigos de error en lugar de excepciones** | `registrar`, `cambiarEstado` y `asignarResponsable` devuelven `-1…-5`; `Main` traduce cada código con `if/else` (Main, 7–45) | El llamador puede ignorar el error; el significado depende de comentarios | Alto | **Alta** |
| 8 | **Manejo de errores deficiente (catch vacío)** | `guardar()` captura `Exception` y no hace nada (SM, 240); `cargar()` solo imprime "Error al cargar" (SM, 272) | Si falla el disco se pierden datos sin aviso | Muy alto (integridad de datos) | **Alta** |
| 9 | **Envidia de atributos y campos públicos** | `SolicitudManager` lee y modifica `s.estado`, `s.est.nombre`; `Solicitud`, `Estudiante` y la lista `l` tienen campos públicos; `Main` accede a `m.l.size()` | Sin encapsulamiento: cualquiera rompe los invariantes | Alto | **Alta** |
| 10 | **Condicionales por tipo/estado (viola OCP)** | Cadenas `if/else` por tipo en `registrar` (SM, 50–58), por estado en `cambiarEstado` (SM, 78–88) y por tipo+prioridad en `reporte` (SM, 178–210) | Extender obliga a modificar código existente | Alto | **Alta** |
| 11 | **Acoplamiento a infraestructura (viola DIP)** | La ruta `"solicitudes.csv"` y `PrintWriter`/`FileReader` están dentro de la clase de negocio (SM, 232–275); `cargar()` se invoca desde el constructor | No se puede probar sin disco ni cambiar el almacenamiento | Alto | **Alta** |
| 12 | **Nombres poco expresivos** | `l`, `resps`, `sig`, `s`, `r`, `p/e/r/x`, `t`, `c` | Obliga a descifrar el código | Medio | Media |
| 13 | **Defectos latentes** | `tipo == null` produce `NullPointerException` (SM, 30); un `;` dentro de la descripción corrompe el CSV (SM, 236 y 253) | Fallas inesperadas en producción | Medio | Media (1.º se corrige; 2.º queda como mejora pendiente) |

### Priorización

1. **Primero (impacto alto y base del resto):** smells 4, 9, 10, 6 y 12 → modelar el dominio (enums, encapsulamiento, mover comportamiento).
2. **Después:** 3, 1 y 2 → objeto parámetro, extraer clases (validación, reporte) y métodos.
3. **Luego:** 5 → consolidar búsquedas con un único método parametrizado.
4. **Por último:** 7, 8, 11 y 13 → excepciones, repositorio e inversión de dependencias.

Se prioriza ese orden porque cada paso deja una base más segura para el siguiente y puede verificarse con la prueba de caracterización (`scripts/verificar.sh`).

## 3. Principios SOLID vulnerados

| Principio | Dónde se viola |
|---|---|
| **SRP** | `SolicitudManager` mezcla validación, reglas, búsqueda, reporte y persistencia |
| **OCP** | Nuevos tipos, estados o formatos de reporte obligan a modificar métodos existentes |
| **DIP** | La lógica de negocio depende del archivo concreto `solicitudes.csv` |
| LSP / ISP | No aplican de forma significativa: no hay herencia ni interfaces en la versión inicial (se incorporan interfaces en el refactor y se evalúa ISP allí) |

## 4. Plan de refactoring (relación smell → técnica)

| Refactoring | Smells que resuelve |
|---|---|
| R1. Replace Type Code with Enum + Encapsulate Field + Move Method | 4, 6, 9, 10, 12 |
| R2. Introduce Parameter Object + Extract Class (validador) + Extract Method | 2, 3 |
| R3. Extract Class (reporte) + Parameterize Method (búsquedas) | 1, 2, 5 |
| R4. Extract Interface (repositorio) + Replace Error Code with Exception + Rename | 1, 7, 8, 11, 13 |
