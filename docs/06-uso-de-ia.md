# Declaración de uso de inteligencia artificial

| Aspecto | Detalle |
|---|---|
| Herramienta | Claude (Anthropic) |
| Finalidad | Asistencia en el desarrollo y la documentación del trabajo (código, pruebas, diagnóstico e informe), incluyendo el formato uniforme de los documentos y la presentación, y consultas puntuales |
| Grado de intervención de la IA | Alta en la generación inicial de código y documentos; la revisión, ejecución y validación posteriores estuvieron a cargo del equipo |
| Intervención del equipo | Ambos integrantes intervinieron por igual: revisaron, ejecutaron y validaron el código y los documentos, y tomaron las decisiones del trabajo en conjunto |

## Registro de prompts relevantes (orden cronológico)

**Prompt 1 — Relevamiento de la consigna**
> TP MS2V2 — revisa ese archivo en la carpeta que te agregue y veamos que es posible realizar.

Resultado: la IA leyó la consigna (`TP MS2V2.docx`) y el modelo de presentación de la cátedra, y propuso qué se podía generar (sistema base, diagnóstico, refactor, pruebas, code review, informe, presentación y guion) y qué debía hacer el equipo (video con voz propia, subida a GitHub, capturas reales).

**Prompt 2 — Decisiones de alcance**
> 1. usemos caso sugerido
> 2. sigamos con java
> 3. solo con facundo.
> 4. no importan estos datos
> 5. hazlo como lo dijiste en ese orden.

Resultado: se eligió el caso "Sistema de Gestión de Solicitudes Académicas", lenguaje Java, equipo de dos integrantes y el orden de trabajo: sistema base → diagnóstico → refactoring → pruebas → code review → informe → presentación y guion.

## Evidencias complementarias
- Verificación automática del comportamiento: `docs/evidencias/`.
- Historial de commits con etiquetas por etapa: ver README.
- [COMPLETAR: enlace o anexo PDF con la conversación completa, si la cátedra lo solicita].
