#!/usr/bin/env bash
# Verificación de comportamiento externo (prueba de caracterización / "golden master").
# Ejecuta la demostración en una carpeta temporal y compara la salida y el archivo
# solicitudes.csv contra las referencias generadas con la versión inicial.
set -e
ROOT="$(cd "$(dirname "$0")/.." && pwd)"
"$ROOT/scripts/compilar.sh" > /dev/null
MAIN=$(grep -rl "static void main" "$ROOT/src/main" | sed "s|$ROOT/src/main/java/||; s|\.java$||; s|/|.|g")
TMP=$(mktemp -d)
cd "$TMP"
java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -cp "$ROOT/out" "$MAIN" > salida_actual.txt
if diff -u "$ROOT/docs/evidencias/salida_esperada.txt" salida_actual.txt \
   && diff -u "$ROOT/docs/evidencias/solicitudes_esperado.csv" solicitudes.csv; then
  echo "VERIFICACIÓN OK: el comportamiento externo coincide con el de la versión inicial."
else
  echo "VERIFICACIÓN FALLÓ: el comportamiento externo cambió." >&2
  exit 1
fi
