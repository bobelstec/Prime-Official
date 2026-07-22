@echo off
where gradle >nul 2>nul
if %ERRORLEVEL%==0 (
  gradle %*
) else (
  echo Gradle nao encontrado. Abra no Android Studio ou use o GitHub Actions.
  echo Veja COMO_GERAR_O_APK.md.
  exit /b 1
)
