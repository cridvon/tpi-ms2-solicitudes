#!/usr/bin/env bash
# Compila todo el proyecto (sin dependencias externas) en ./out
set -e
cd "$(dirname "$0")/.."
rm -rf out && mkdir -p out
find src/main -name '*.java' > out/fuentes.txt
if [ -d src/test ]; then find src/test -name '*.java' >> out/fuentes.txt; fi
javac -encoding UTF-8 -d out @out/fuentes.txt
echo "Compilación OK"
