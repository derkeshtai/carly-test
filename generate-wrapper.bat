@echo off
echo ============================================
echo   Generando Gradle Wrapper 8.0...
echo ============================================
echo.

REM Este script debe ejecutarse DESDE Android Studio Terminal
REM O asegurarse de que gradle este en el PATH

where gradle >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo ERROR: Gradle no encontrado en PATH
    echo.
    echo Opciones:
    echo 1. Usar Android Studio Terminal (recomendado)
    echo 2. O instalar Gradle manualmente desde: https://gradle.org/install/
    echo.
    pause
    exit /b 1
)

echo Gradle encontrado! Generando wrapper version 8.0...
gradle wrapper --gradle-version 8.0

if %ERRORLEVEL% EQU 0 (
    echo.
    echo ============================================
    echo   Wrapper generado exitosamente!
    echo ============================================
    echo.
    echo Ahora puedes usar:
    echo   gradlew.bat assembleDebug
    echo.
) else (
    echo.
    echo ERROR al generar wrapper
    echo.
)

pause
