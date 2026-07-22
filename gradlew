#!/bin/sh
# Gradle wrapper — versão simplificada.
# Se o gradle-wrapper.jar não estiver presente, o Android Studio ou o
# GitHub Actions (passo "Garantir o Gradle wrapper") o recriam automaticamente.
DIR="$(cd "$(dirname "$0")" && pwd)"
if command -v gradle >/dev/null 2>&1; then
  exec gradle "$@"
else
  echo "Gradle não encontrado. Abra no Android Studio ou use o GitHub Actions."
  echo "Veja COMO_GERAR_O_APK.md."
  exit 1
fi
