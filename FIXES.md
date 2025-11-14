# 🔧 Soluciones a Errores Iniciales

## ❌ Error 1: `./gradlew no se reconoce`

### Causa
El proyecto no incluía los archivos del Gradle Wrapper.

### ✅ Solución A: Generar desde Android Studio (MÁS FÁCIL)

1. Abre el proyecto en Android Studio
2. Abre la Terminal integrada (View → Tool Windows → Terminal)
3. Ejecuta:
   ```bash
   gradle wrapper --gradle-version 8.1
   ```
4. Espera a que termine (descargará archivos)
5. Ya puedes usar `gradlew.bat`

### ✅ Solución B: Usar script automático

1. Ejecuta el archivo `generate-wrapper.bat` que incluí
2. Sigue las instrucciones

### ✅ Solución C: No usar gradlew (temporal)

Si nada funciona, compila directo desde Android Studio:
- Build → Make Project
- Build → Build Bundle(s) / APK(s) → Build APK(s)

---

---

## ❌ Error 3: Resource mipmap/ic_launcher not found

### Causa
El proyecto minimalista no incluía los iconos de la aplicación (ic_launcher).

### ✅ Solución (YA APLICADA)

He agregado iconos adaptativos vectoriales que funcionan en todos los dispositivos:

**Archivos creados:**
- `res/mipmap-anydpi-v26/ic_launcher.xml`
- `res/mipmap-anydpi-v26/ic_launcher_round.xml`
- `res/drawable/ic_launcher_foreground.xml`
- `res/values/colors.xml`

**Ícono:** Un libro simple en fondo morado (#667EEA).

Esto es solo para el proyecto de prueba. Para la app real usaremos un ícono profesional.

---

## ❌ Error 2: Repository 'Google' was added by build file

### Causa
Gradle 6.8+ cambió la forma de declarar repositorios. Hay conflicto entre `settings.gradle` y `build.gradle`.

### ✅ Solución (YA APLICADA)

He corregido el `build.gradle` raíz para usar la forma moderna.

**Antes (incorrecto):**
```gradle
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

**Después (correcto):**
```gradle
// Repositorios están en settings.gradle
// No hay bloque allprojects aquí
```

Los repositorios ahora están SOLO en `settings.gradle`:
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
```

---

## 🆕 Proyecto Actualizado

He corregido ambos problemas. Descarga la nueva versión:

### Cambios realizados:
1. ✅ Agregado `gradle/wrapper/gradle-wrapper.properties`
2. ✅ Agregado `gradlew.bat` para Windows
3. ✅ Corregido `build.gradle` (eliminado `allprojects`)
4. ✅ Agregado script `generate-wrapper.bat` para generar wrapper

---

## 📝 Pasos Corregidos

### Paso 1: Sincronizar proyecto en Android Studio

1. Abre Android Studio
2. File → Open → Selecciona carpeta `carly-test`
3. Espera a que sincronice Gradle
4. **Debería decir:** "Gradle sync finished in X s"
5. **NO debe haber errores** en Build Output

### Paso 2: Generar Gradle Wrapper

**Opción A - Desde Android Studio Terminal:**
```bash
gradle wrapper --gradle-version 8.1
```

**Opción B - Usando el script:**
- Ejecuta `generate-wrapper.bat`

### Paso 3: Compilar APK

**Opción A - Con gradlew:**
```bash
gradlew.bat assembleDebug
```

**Opción B - Desde Android Studio:**
- Build → Build Bundle(s) / APK(s) → Build APK(s)

### Paso 4: Verificar APK generado

Busca el archivo aquí:
```
app\build\outputs\apk\debug\app-debug.apk
```

---

## 🐛 Si persisten problemas

### Error: "SDK location not found"

**Solución:**
1. Crea archivo `local.properties` en la raíz del proyecto
2. Agrega (ajusta la ruta a tu instalación):
   ```properties
   sdk.dir=C\:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
   ```

Para encontrar tu SDK:
- Android Studio → File → Project Structure → SDK Location

### Error: "Gradle version mismatch"

**Solución:**
En `gradle/wrapper/gradle-wrapper.properties`, verifica:
```properties
distributionUrl=https\://services.gradle.org/distributions/gradle-8.1-bin.zip
```

### Error: "Could not download gradle-wrapper.jar"

**Solución:**
El JAR del wrapper no se puede descargar automáticamente en algunos entornos.

**Método 1 - Desde Android Studio:**
```bash
# En Android Studio Terminal:
gradle wrapper
```

**Método 2 - Compilar sin wrapper:**
- Usa Build → Make Project desde el IDE
- No uses comandos gradlew

---

## ✅ Checklist Final

Antes de continuar a la Fase 3, verifica:

- [ ] Proyecto abre en Android Studio sin errores
- [ ] Gradle sync exitoso
- [ ] Build ejecuta sin errores
- [ ] APK se genera correctamente
- [ ] App se instala y ejecuta en emulador/dispositivo

---

## 📞 Siguiente Paso

Una vez que todo compile y funcione:

1. Reporta si todo está OK ✅
2. Si hay errores, copia el mensaje completo
3. Continuaremos con Fase 3: Agregar funcionalidad JSON

---

## 💡 Tip para evitar problemas

**Usa SIEMPRE la Terminal de Android Studio** para comandos de Gradle.
No uses PowerShell o CMD directamente, porque Android Studio configura automáticamente:
- JAVA_HOME
- ANDROID_HOME  
- Gradle en PATH
- Variables de entorno correctas
