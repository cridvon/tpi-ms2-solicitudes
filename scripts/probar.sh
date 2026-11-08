#!/usr/bin/env bash
# Ejecuta las pruebas unitarias y de integración (sin dependencias externas).
set -e
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
"$ROOT/scripts/compilar.sh" > /dev/null
cd "$ROOT"
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp out solicitudes.EjecutarPruebas
