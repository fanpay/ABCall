# ABCall - Aplicación Android

## Índice

- [ABCall - Aplicación Android](#abcall---aplicación-android)
  - [Índice](#índice)
  - [Requisitos](#requisitos)
  - [Configuración del Entorno](#configuración-del-entorno)
  - [Ejecución de la Aplicación](#ejecución-de-la-aplicación)
  - [Pruebas](#pruebas)
  - [Despliegue](#despliegue)
  - [Versiones Generadas](#versiones-generadas)
  - [Workflows relacionados](#workflows-relacionados)
  - [Autores](#autores)

## Requisitos

- [Android Studio](https://developer.android.com/studio)
- JDK 17
- [Gradle](https://gradle.org/)
- Conexión a Internet

## Configuración del Entorno

1. **Clonar el repositorio:**

    ```bash
    git clone https://github.com/fanpay/ABCall.git
    cd ABCall/mobile
    ```

2. **Configurar Android Studio:**

    - Abrir Android Studio.
    - Seleccionar "Open an existing Android Studio project".
    - Navegar a la carpeta `mobile` del repositorio clonado y abrirla.

3. **Configurar el SDK de Android:**

    - En Android Studio, ir a `File > Project Structure > SDK Location`.
    - Asegurarse de que el SDK de Android esté configurado correctamente.

4. **Instalar dependencias:**

    Android Studio debería detectar automáticamente las dependencias y comenzar a descargarlas. Si no es así, puedes forzar la sincronización del proyecto con Gradle usando `File > Sync Project with Gradle Files`.

## Ejecución de la Aplicación

1. **Configurar un dispositivo:**

    - Conectar un dispositivo físico con depuración USB habilitada, o
    - Configurar un emulador de Android desde `AVD Manager` en Android Studio.

2. **Ejecutar la aplicación:**

    - Seleccionar el dispositivo o emulador en la barra de herramientas de Android Studio.
    - Hacer clic en el botón de `Run` (icono de play) o usar el atajo `Shift + F10`.

## Pruebas

1. **Ejecutar pruebas unitarias:**

    ```bash
    ./gradlew testDebugUnitTest
    ```

2. **Generar reporte de cobertura con JaCoCo:**

    ```bash
    ./gradlew jacocoTestReport
    ```

    Los reportes se generarán en `mobile/app/build/reports/jacoco/testDebugUnitTest`.

## Despliegue

Para generar un APK de la aplicación:

1. **Construir el APK:**

    ```bash
    ./gradlew assembleDebug
    ```

    El APK generado se encontrará en [debug](http://_vscodecontentref_/0).

## Versiones Generadas

* [Drive](https://uniandes-my.sharepoint.com/:f:/g/personal/f_payan_uniandes_edu_co/EpGM6aTLQWtNp0HKyADlAfoB9whB-bQ1jBGM3CI5RB37fQ?e=1Ge50H)

## Workflows relacionados
* [Manual - MB - Ejecución de pruebas unitarias](https://github.com/fanpay/ABCall/actions/workflows/mb_manual_unit_testing.yml)
* [Pruebas e integración de ramas de funcionalidad - Mobile - Automatizado](https://github.com/fanpay/ABCall/actions/workflows/mb_integration.yml)


## Autores

Fabián Andrés Payan Meneses - f.payan@uniandes.edu.co <br/>
Esneider Velandia Gonzalez - e.velandia2164@uniandes.edu.co