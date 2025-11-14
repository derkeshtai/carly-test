# 🔒 Aislamiento del Proyecto Android

## ¿Qué se comparte y qué no?

### ✅ AISLADO (por proyecto)

```
carly-test/
├── app/                        ← Código del proyecto (aislado)
├── build/                      ← Archivos compilados (aislados)
├── .gradle/                    ← Caché de Gradle (aislado)
├── gradle/wrapper/             ← Versión de Gradle (aislado)
└── gradlew.bat                 ← Script local (aislado)
```

### ⚠️ COMPARTIDO (entre proyectos)

```
C:\Users\drago\
├── .gradle\                    ← Caché global de Gradle
│   ├── caches\                 ← Dependencias descargadas
│   └── wrapper\dists\          ← Versiones de Gradle
│
└── AppData\Local\Android\
    └── Sdk\                    ← Android SDK (necesario)
```

---

## 🎯 Niveles de Aislamiento

### Nivel 1: Aislamiento Normal (RECOMENDADO)

**Lo que ya tienes por defecto:**
- ✅ Gradle Wrapper por proyecto
- ✅ Dependencias versionadas
- ✅ Configuración independiente
- ⚠️ Caché compartido (ahorra espacio)

**No hace falta hacer nada especial.**

### Nivel 2: Aislamiento Completo (OPCIONAL)

**Si quieres aislar HASTA el caché:**

Usa el script `setup-isolated.bat` que creé:

```batch
setup-isolated.bat
```

Esto configura:
```
carly-test/
└── .gradle-local/              ← Caché LOCAL del proyecto
    ├── caches/                 ← Dependencias SOLO de este proyecto
    └── wrapper/                ← Gradle SOLO de este proyecto
```

**Ventajas:**
- ✅ Completamente independiente
- ✅ No afecta otros proyectos
- ✅ Fácil de limpiar/eliminar

**Desventajas:**
- ❌ Ocupa más espacio (duplica dependencias)
- ❌ Más lento (re-descarga todo)

### Nivel 3: Aislamiento Extremo (OVERKILL)

**Si quieres aislar HASTA Java:**

Necesitarías:
1. Portable JDK en el proyecto
2. Android SDK portable
3. Todo en carpetas locales

**NO recomendado porque:**
- Es complicado de configurar
- Android SDK pesa ~10-30 GB
- Poco práctico para desarrollo

---

## 🧹 Limpieza Total

Si en algún momento quieres **eliminar TODO** del proyecto:

```batch
clean-all.bat
```

Esto borra:
- ✅ Todos los builds
- ✅ Cachés locales
- ✅ Gradle wrapper
- ✅ Configuración de IDE
- ✅ Todo excepto el código fuente

Deja el proyecto como recién descargado.

---

## 🤔 ¿Qué nivel usar?

### Usa Nivel 1 (Normal) si:
- ✅ Quieres desarrollo ágil
- ✅ No te importa compartir caché entre proyectos
- ✅ Confías en que las dependencias estén bien versionadas
- **👉 RECOMENDADO para este proyecto**

### Usa Nivel 2 (Aislado) si:
- ⚠️ Trabajas con versiones MUY diferentes de Android en diferentes proyectos
- ⚠️ Has tenido problemas de corrupción de caché
- ⚠️ Quieres probar configuraciones experimentales sin afectar otros proyectos

### Usa Nivel 3 (Extremo) si:
- ❌ Estás en un ambiente ultra-restringido
- ❌ No puedes instalar nada en el sistema
- **NO recomendado para desarrollo normal**

---

## 📂 ¿Qué archivos son seguros de eliminar?

### Siempre seguros:
```
✅ build/                  (se regenera al compilar)
✅ .gradle/                (se regenera al sincronizar)
✅ .gradle-local/          (si usaste aislamiento)
✅ app/build/              (se regenera al compilar)
✅ .idea/                  (configuración de Android Studio)
✅ *.apk                   (archivos compilados)
```

### NUNCA eliminar:
```
❌ app/src/                (tu código fuente!)
❌ gradle/wrapper/gradle-wrapper.properties
❌ build.gradle
❌ settings.gradle
```

---

## 🛡️ Protección del Sistema

### Lo que este proyecto NO toca:

- ❌ Variables de entorno del sistema
- ❌ PATH global
- ❌ Registro de Windows
- ❌ Program Files
- ❌ Otros proyectos
- ❌ Configuraciones globales

### Lo único "global" que se usa:

- ✅ Android SDK (necesario, ya lo tienes)
- ✅ Java/JDK (incluido en Android Studio)
- ⚠️ Caché de Gradle en `~/.gradle/` (opcional aislarlo)

---

## 💡 Recomendación Final

**Para CarlyDean Library:**

1. **Usa el nivel normal** (sin modificaciones)
2. No ejecutes `setup-isolated.bat` a menos que tengas problemas
3. Si quieres limpiar todo: `clean-all.bat`
4. El proyecto NO afecta tu sistema global

**Es seguro trabajar normalmente.** Android Studio ya maneja el aislamiento de manera inteligente.

---

## 🚀 Siguiente Paso

Ahora que sabes que el proyecto no afectará tu sistema:

1. Abre el proyecto en Android Studio
2. Deja que sincronice normalmente
3. Compila con `Build → Make Project`
4. Reporta si funciona

¡No necesitas preocuparte por el PATH ni variables globales! 👍
