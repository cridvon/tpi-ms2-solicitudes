# TPI Metodología de Sistemas II — Sistema de Gestión de Solicitudes Académicas

Trabajo Práctico Integrador: evolución profesional de un sistema existente (calidad, deuda técnica, refactoring y revisión técnica).
Tecnicatura Universitaria en Programación a Distancia (UTN) — Metodología de Sistemas II.

## Descripción
Módulo que permite registrar solicitudes de estudiantes, clasificarlas por tipo, prioridad y estado, asignar un responsable, buscarlas y generar un reporte.
El repositorio conserva **dos versiones del mismo sistema** para comparar:

| Tag | Contenido |
|---|---|
| `estado-inicial` | Versión base: funciona, pero con deuda técnica deliberada (13 code smells) |
| `etapa-diagnostico` | Diagnóstico documentado (`docs/01-diagnostico.md`) |
| `etapa-refactoring` | 4 refactorings aplicados, uno por commit |
| `etapa-pruebas` | Pruebas automatizadas y evidencias de verificación |
| `etapa-docs` | Code review, decisiones técnicas, uso de IA y README |
| `estado-refactorizado` | Estado final del código |

Para ver las diferencias: `git diff estado-inicial estado-refactorizado -- src/main`.

## Funcionalidades
- Registrar una solicitud (con validación de nombre, email, tipo, prioridad y descripción).
- Cambiar el estado respetando el flujo `PENDIENTE → EN_PROCESO → RESUELTA / RECHAZADA`.
- Asignar un responsable (Bedelía, Dirección de Carrera, Secretaría Académica).
- Buscar/filtrar por estado, tipo, responsable y prioridad (los filtros se pueden combinar).
- Generar un reporte con el plazo de atención de cada solicitud y un resumen por estado.
- Persistencia en `solicitudes.csv`.

## Tecnologías
Java 17 o superior (probado con Java 21), sin dependencias externas. Scripts `bash` para compilar, verificar y probar.

## Instalación y uso
Requisitos: JDK 17+ y `bash` (macOS, Linux o Git Bash en Windows).

```bash
./scripts/compilar.sh      # compila en ./out
./scripts/verificar.sh     # compara la ejecución con la versión inicial (golden master)
./scripts/probar.sh        # ejecuta las 27 pruebas automatizadas
java -Dfile.encoding=UTF-8 -cp out solicitudes.app.Main     # demostración
```
Para ejecutar la versión inicial: `git checkout estado-inicial` y usar `solicitudes.Main`.

## Estructura (versión refactorizada)
```
src/main/java/solicitudes/
  dominio/       Estado, Tipo, Prioridad, Responsable, Solicitud, Estudiante y excepciones
  servicio/      SolicitudService, NuevaSolicitud, ValidadorSolicitud, Filtros
  persistencia/  SolicitudRepository, RepositorioMemoria, RepositorioCsv
  reporte/       GeneradorReporte, ReporteTexto
  app/           Main (demostración)
src/test/java/   Pruebas (MiniTest, sin dependencias)
docs/            Diagnóstico, refactorings, pruebas, code review, decisiones, uso de IA
scripts/         compilar, verificar, probar
```

## Documentación
1. [`docs/01-diagnostico.md`](docs/01-diagnostico.md): calidad interna/externa, code smells y plan.
2. [`docs/02-refactorings.md`](docs/02-refactorings.md): refactorings con antes/después y métricas.
3. [`docs/03-pruebas.md`](docs/03-pruebas.md): estrategia, casos y evidencias.
4. [`docs/04-code-review.md`](docs/04-code-review.md): checklist, observaciones y criterios de aceptación técnica.
5. [`docs/05-decisiones-tecnicas.md`](docs/05-decisiones-tecnicas.md): decisiones de diseño (formato ADR).
6. [`docs/06-uso-de-ia.md`](docs/06-uso-de-ia.md): declaración y registro de uso de IA.
