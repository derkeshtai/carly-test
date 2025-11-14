@echo off
REM ============================================
REM   Limpiar TODO el proyecto (reset completo)
REM ============================================

echo.
echo [ADVERTENCIA] Esto eliminará:
echo   - Todos los archivos compilados
echo   - Cachés de Gradle locales
echo   - Gradle Wrapper descargado
echo.
set /p confirm="¿Continuar? (S/N): "
if /i not "%confirm%"=="S" (
    echo Operación cancelada
    pause
    exit /b 0
)

echo.
echo [INFO] Limpiando proyecto...

REM Eliminar builds
if exist app\build (
    echo [OK] Eliminando app\build\...
    rmdir /s /q app\build
)

if exist build (
    echo [OK] Eliminando build\...
    rmdir /s /q build
)

REM Eliminar .gradle local
if exist .gradle (
    echo [OK] Eliminando .gradle\...
    rmdir /s /q .gradle
)

REM Eliminar .gradle-local (si se usó aislamiento)
if exist .gradle-local (
    echo [OK] Eliminando .gradle-local\...
    rmdir /s /q .gradle-local
)

REM Eliminar gradle wrapper descargado
if exist gradle\wrapper\gradle-wrapper.jar (
    echo [OK] Eliminando gradle-wrapper.jar...
    del /q gradle\wrapper\gradle-wrapper.jar
)

REM Eliminar .idea (configuración de Android Studio)
if exist .idea (
    echo [OK] Eliminando .idea\...
    rmdir /s /q .idea
)

REM Eliminar local.properties
if exist local.properties (
    echo [OK] Eliminando local.properties...
    del /q local.properties
)

echo.
echo ============================================
echo   Limpieza completa!
echo ============================================
echo.
echo El proyecto está limpio. Para recomenzar:
echo   1. Abre el proyecto en Android Studio
echo   2. Deja que sincronice
echo   3. Genera el wrapper si es necesario
echo.

pause
