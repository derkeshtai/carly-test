@echo off
REM ============================================
REM   Configuración AISLADA para Carly Test
REM   No modifica variables globales del sistema
REM ============================================

echo.
echo [INFO] Configurando entorno aislado...
echo.

REM 1. Usar Gradle Wrapper LOCAL (ya incluido en el proyecto)
REM    No usa gradle del sistema

REM 2. Configurar directorios locales de Gradle
set GRADLE_USER_HOME=%CD%\.gradle-local
echo [OK] Gradle usará: %GRADLE_USER_HOME%

REM 3. Configurar caché local de dependencias
echo [OK] Dependencias se descargarán localmente

REM 4. Verificar que Java está disponible
where java >nul 2>&1
if %ERRORLEVEL% NEQ 0 (
    echo [ERROR] Java no encontrado
    echo [INFO] Android Studio incluye Java, usa su Terminal
    pause
    exit /b 1
)

REM 5. Generar wrapper si no existe
if not exist gradlew.bat (
    echo [INFO] Generando Gradle Wrapper 8.0 local...
    gradle wrapper --gradle-version 8.0 --gradle-user-home %GRADLE_USER_HOME%
    echo [OK] Wrapper generado
)

echo.
echo ============================================
echo   Entorno aislado configurado!
echo ============================================
echo.
echo Directorios usados:
echo   - Gradle local: %GRADLE_USER_HOME%
echo   - SDK Android: %ANDROID_HOME% (compartido)
echo.
echo Para compilar:
echo   gradlew.bat assembleDebug
echo.
echo Para limpiar TODO (incluyendo cachés locales):
echo   rmdir /s /q .gradle-local
echo.

pause
