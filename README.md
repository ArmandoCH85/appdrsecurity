# Dr Security (app Dr Security GPS)

Cliente **Kotlin Multiplatform** con **Compose** y módulo Android (`composeApp`). Incluye también targets iOS en el código; en Windows/Linux el build local se orienta principalmente a **Android**.

## Requisitos

| Componente | Versión / notas |
|------------|------------------|
| **JDK** | **17** (el proyecto usa `JVM_17` para Android) |
| **Android SDK** | **API 35** (compileSdk/targetSdk); **mínimo SDK 26** |
| **Git** | Para clonar el repositorio |

Opcional pero recomendable: **Android Studio** (última estable) con el Android SDK instalado desde el SDK Manager.

## Configuración del SDK (una sola vez)

En la raíz del proyecto, crea el archivo **`local.properties`** (no se sube al repositorio si está en `.gitignore`) con la ruta de tu Android SDK:

### Windows (ejemplo)

Puedes usar barras normales (a menudo más simple):

```properties
sdk.dir=C:/Users/TU_USUARIO/AppData/Local/Android/Sdk
```

O el estilo con escape clásico:

```properties
sdk.dir=C\:\\Users\\TU_USUARIO\\AppData\\Local\\Android\\Sdk
```

### Linux (ejemplo)

```properties
sdk.dir=/home/TU_USUARIO/Android/Sdk
```

Si usas Android Studio, la ruta aparece en **Settings → Languages & Frameworks → Android SDK → Android SDK Location**.

Alternativa por variable de entorno (Linux/macOS): muchas herramientas respetan **`ANDROID_HOME`** o **`ANDROID_SDK_ROOT`**; Android Gradle Plugin suele seguir necesitando **`local.properties`** para la ruta del SDK.

## Compilar el APK de depuración (debug)

Desde la **raíz del repositorio** (donde está `settings.gradle.kts`).

### Windows

```bat
gradlew.bat :composeApp:assembleDebug
```

El APK generado queda en:

`composeApp\build\outputs\apk\debug\composeApp-debug.apk`

### Linux

Este repositorio incluye **`gradlew.bat`** (Windows). En Linux/macOS hace falta el script **`gradlew`** (Unix). Si **no** está en el proyecto, genera el wrapper con **Gradle 8.12+** instalado en el sistema:

```bash
cd /ruta/al/proyecto
gradle wrapper --gradle-version 8.12
chmod +x gradlew
./gradlew :composeApp:assembleDebug
```

Si ya existe **`gradlew`** en la raíz:

```bash
chmod +x gradlew
./gradlew :composeApp:assembleDebug
```

Salida del APK:

`composeApp/build/outputs/apk/debug/composeApp-debug.apk`

## Otras tareas útiles

| Acción | Windows | Linux (con `./gradlew`) |
|--------|---------|-------------------------|
| Limpiar build | `gradlew.bat clean` | `./gradlew clean` |
| Compilar tests unitarios (Android) | `gradlew.bat :composeApp:testDebugUnitTest` | `./gradlew :composeApp:testDebugUnitTest` |
| Generar interfaces SQLDelight | `gradlew.bat :composeApp:generateSqlDelight` | `./gradlew :composeApp:generateSqlDelight` |

## Abrir en Android Studio

1. **File → Open** y selecciona la carpeta raíz del proyecto (no solo `composeApp`).
2. Espera a que sincronice Gradle.
3. Menú **Build → Make Project** o ejecuta la variante **debug** en un dispositivo/emulador.

## Problemas frecuentes

- **`SDK location not found`**: falta `local.properties` o `sdk.dir` incorrecto.
- **`JAVA_HOME` no es JDK 17**: instala JDK 17 y apunta `JAVA_HOME` a esa instalación.
- **Targets iOS deshabilitados en el log de Gradle**: es normal en entornos sin toolchain de Kotlin/Native para iOS; no impide compilar **Android**.

## Licencia y marca

Revisa la documentación interna del producto y los términos de uso del servicio GPS asociado a la app.
