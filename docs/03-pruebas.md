# Pruebas y verificación del comportamiento

## Estrategia
1. **Prueba de caracterización (golden master).** Antes de refactorizar se fijó el comportamiento de la versión inicial: la salida completa de la demostración (`docs/evidencias/salida_esperada.txt`) y el archivo generado (`docs/evidencias/solicitudes_esperado.csv`). `scripts/verificar.sh` ejecuta la versión actual y compara ambos con `diff`. Se ejecutó **después de cada refactoring**.
2. **Pruebas automatizadas (27 casos: 24 de la etapa de pruebas y 3 agregadas a partir del code review).** Posibles recién después del refactor, porque el diseño original no se podía probar sin tocar el disco. Se ejecutan con `scripts/probar.sh`. Se usó un mini framework propio (`MiniTest`) para no depender de librerías externas.

## Evidencias
| Evidencia | Archivo |
|---|---|
| Salida de la versión inicial (referencia) | `docs/evidencias/salida_esperada.txt` |
| Verificación en cada commit (9 de 9 commits previos coinciden) | `docs/evidencias/verificacion_por_commit.txt` |
| Resultado de las pruebas automatizadas (27/27) | `docs/evidencias/resultado_pruebas.txt` |
| Prueba de sensibilidad: un cambio de comportamiento se detecta | `docs/evidencias/prueba_de_sensibilidad.txt` |

## Casos de prueba
| ID | Caso | Resultado esperado |
|---|---|---|
| CP-01 | PENDIENTE → EN_PROCESO / RECHAZADA | permitido |
| CP-02 | PENDIENTE → RESUELTA | no permitido |
| CP-03 | Estados finales hacia cualquier estado | no permitido |
| CP-04 | `Estado.desdeTexto("pendiente")` | `DatoInvalidoException` |
| CP-05 | Plazos por tipo y prioridad (3, 30, 5 días) | valores de la tabla |
| CP-06 | Responsable inicial por tipo | Bedelía / SIN ASIGNAR |
| CP-07 | Solicitud nueva | PENDIENTE y responsable de su tipo |
| CP-08 | Asignar responsable a solicitud cerrada | `ReglaDeNegocioException` |
| CP-09 | Prioridad 0 y 4 | `DatoInvalidoException` |
| CP-10 | Ids correlativos | 1, 2 |
| CP-11 | Nombre vacío | "el nombre es obligatorio" |
| CP-12 | Email sin arroba | "email inválido" |
| CP-13 | Tipo `null` | "tipo de solicitud inválido" (antes: `NullPointerException`) |
| CP-14 | Varios errores a la vez | se informa el primero (mismo orden que la versión inicial) |
| CP-15 | Cambiar estado de id inexistente | `SolicitudNoEncontradaException` |
| CP-15b | La excepción de inexistente conserva el id | `getId()` = id consultado |
| CP-15c | `registrar(null)` | error claro (`NullPointerException` con mensaje) |
| CP-16 | Transición no permitida | `ReglaDeNegocioException` |
| CP-17 | Asignar "SIN ASIGNAR" | "responsable inválido" |
| CP-18 | Filtros combinados | 2 de tipo BAJA, 1 de BAJA en proceso |
| CP-19 | Reporte vacío | "Total: 0" y resumen en cero |
| CP-20 | Reporte con una solicitud | línea con plazo, responsable y estudiante |
| CP-21 | Guardar y recargar | datos conservados |
| CP-22 | Leer el CSV de la versión inicial | compatible |
| CP-23 | Archivo corrupto | `PersistenciaException` |
| CP-24 | Ids tras recargar | continúan correlativos |
| CP-25 | Escritura del CSV | no queda archivo temporal |

## Cómo se verificó que el refactoring no alteró el comportamiento
- Salida idéntica (`diff` vacío) en los 9 commits previos a este registro.
- Archivo `solicitudes.csv` idéntico, lo que garantiza compatibilidad de datos entre versiones.
- Las únicas diferencias deliberadas están declaradas en `docs/02-refactorings.md` (caso `tipo == null` y archivo corrupto).

## Limitaciones
- La demostración no cubre todos los caminos (por ejemplo, descripciones con `;`).
- No hay pruebas de rendimiento ni de concurrencia: el sistema es monousuario.
