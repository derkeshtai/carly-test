# 🧪 CarlyLibrary Test - Proyecto de Verificación

Proyecto minimalista para verificar que el entorno Android está correctamente configurado.

## 📋 Checklist de Verificación

### ✅ Paso 1: Abrir el Proyecto

1. Abre Android Studio
2. File → Open
3. Selecciona la carpeta `carly-test`
4. Espera a que Gradle sincronice (puede tardar 2-5 minutos la primera vez)

**¿Qué verificar?**
- ✅ No debe haber errores en la barra inferior (Build)
- ✅ Debe decir "Gradle sync finished" o similar
- ⚠️ **IGNORA estos warnings (son normales):**
  - "SDK processing... version 4 was encountered"
  - "We recommend using a newer Android Gradle plugin"
  - "Update Kotlin"
- ❌ Si hay errores ROJOS, anótalos y los revisamos

**Nota:** Los warnings en AMARILLO son normales y ya están suprimidos.

---

### ✅ Paso 2: Ejecutar Tests Unitarios

**Opción A: Desde Android Studio**
1. Click derecho en `app/src/test/java/com.carlydean.test`
2. Selecciona "Run 'Tests in com.carlydean.test'"

**Opción B: Desde Terminal**
```bash
cd carly-test
./gradlew test
```

**¿Qué verificar?**
- ✅ Deben pasar los 3 tests
- ✅ Mensaje: "BUILD SUCCESSFUL"
- ⚠️ Si falla algún test, copia el mensaje de error

---

### ✅ Paso 3: Compilar APK

**Desde Terminal:**
```bash
./gradlew assembleDebug
```

**¿Qué verificar?**
- ✅ Debe terminar con "BUILD SUCCESSFUL"
- ✅ Archivo creado en: `app/build/outputs/apk/debug/app-debug.apk`
- ✅ Tamaño aprox: 2-5 MB

---

### ✅ Paso 4: Ejecutar en Emulador

**Configurar Emulador (si no tienes uno):**
1. Tools → Device Manager
2. Create Device
3. Selecciona: Pixel 4 (o cualquier dispositivo)
4. System Image: Android 12.0 (API 31) - Recomendado
5. Next → Finish

**Ejecutar:**
1. Selecciona el emulador en la barra superior
2. Click en el botón verde ▶️ (Run)
3. Espera a que el emulador inicie (1-3 minutos primera vez)

**¿Qué verificar?**
- ✅ La app se abre
- ✅ Muestra información del dispositivo
- ✅ El botón "Probar Click" funciona
- ✅ El texto cambia al hacer click

---

### ✅ Paso 5: Ejecutar en Dispositivo Real

**Preparar dispositivo:**
1. En tu Android: Ajustes → Acerca del teléfono
2. Toca 7 veces en "Número de compilación"
3. Vuelve a Ajustes → Opciones de desarrollador
4. Activa "Depuración USB"
5. Conecta el cable USB a la PC

**Ejecutar:**
1. Android Studio detectará el dispositivo
2. Selecciónalo en la barra superior
3. Click en ▶️ (Run)
4. En el teléfono: Acepta "Permitir depuración USB"

**¿Qué verificar?**
- ✅ La app se instala en el teléfono
- ✅ Funciona igual que en el emulador
- ✅ La información del dispositivo es correcta

---

## 🐛 Problemas Comunes

### Error: "Gradle sync failed"

**Solución 1:** Limpiar caché
```bash
./gradlew clean
```

**Solución 2:** Invalidar cachés de Android Studio
- File → Invalidate Caches → Invalidate and Restart

**Solución 3:** Verificar Java
- File → Project Structure → SDK Location
- Verificar que JDK sea 17

---

### Error: "SDK not found"

1. Tools → SDK Manager
2. SDK Platforms → Instalar Android 12.0 (API 31)
3. SDK Tools → Verificar que estén instalados:
   - Android SDK Build-Tools 34.0.0
   - Android SDK Platform-Tools
   - Android Emulator

---

### Error en compilación: "Could not resolve..."

Problema de internet o proxy. Verificar:
- Conexión a internet
- Si usas proxy corporativo, configurarlo en `gradle.properties`:

```properties
systemProp.http.proxyHost=proxy.company.com
systemProp.http.proxyPort=8080
systemProp.https.proxyHost=proxy.company.com
systemProp.https.proxyPort=8080
```

---

### La app crashea al abrir

1. Ver Logcat en Android Studio (parte inferior)
2. Filtrar por "Error" o "Exception"
3. Copiar el stack trace completo

---

## 📊 Reporte de Verificación

Una vez completados todos los pasos, llena esto:

```
✅/❌ Paso 1 - Proyecto abierto sin errores: ___
✅/❌ Paso 2 - Tests unitarios pasados: ___
✅/❌ Paso 3 - APK compilado: ___
✅/❌ Paso 4 - Funciona en emulador: ___
✅/❌ Paso 5 - Funciona en dispositivo real: ___

Dispositivo real usado:
- Marca/Modelo: _______
- Android: _______
- API Level: _______

Errores encontrados (si los hay):
_________________________________
```

---

## 🎯 Siguiente Paso

Si todo funciona ✅, estamos listos para comenzar con las features reales:

**Fase 3: Agregar funcionalidades progresivamente**
1. ✅ Proyecto base funcionando
2. ⬜ Descargar y parsear JSON del catálogo
3. ⬜ Mostrar lista de libros
4. ⬜ Visor de PDFs básico
5. ⬜ Sistema de favoritos
6. ⬜ Descargas offline
7. ⬜ Otros lectores (EPUB, CBR, etc.)

Reporta los resultados y continuamos! 🚀
