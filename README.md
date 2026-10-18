# Bitácora Electrónica — Gestión Técnica de Taller & Banco de Pruebas

Aplicación nativa para Android desarrollada en **Kotlin** con **Jetpack Compose** y **Material Design 3**, diseñada bajo la metodología de **Diseño Centrado en el Usuario (DCU)** para el registro offline de órdenes de reparación microelectrónica, control de inventario de repuestos y captura aislada de evidencia fotográfica.

## Datos Académicos
* **Institución:** Universidad Laica Eloy Alfaro de Manabí (ULEAM)
* **Carrera:** Tecnologías de la Información — Nivel 7
* **Asignatura:** Desarrollo de Aplicaciones Móviles
* **Docente:** Ing. Edgardo Panchana Flores

---

## Arquitectura del Sistema

El proyecto aplica la arquitectura oficial recomendada por Google estructurada en dos capas principales (sin *Domain Layer*):

1. **UI Layer (`ui/`):**
    * **UI Elements:** Pantallas y componentes reutilizables en Jetpack Compose con Material Design 3 (`SplashScreen`, `LoginScreen`, `RegisterScreen`, `ReparacionesScreen`, `NuevoRegistroScreen`, `DetalleReparacionScreen`, `InventarioScreen`, `ConfiguracionScreen`, `CameraCaptureScreen`).
    * **State Holders:** `ViewModel` y clases inmutables `UiState` expuestas mediante `StateFlow` bajo el patrón de Flujo Unidireccional de Datos (UDF).
2. **Data Layer (`data/`):**
    * **Repositories:** Contratos e implementaciones *Offline-First* (`OfflineReparacionRepository`, `OfflineComponenteRepository`, `OfflineSettingsRepository`) inyectados manualmente mediante `AppContainer`.
    * **Local Data Sources:**
        * **Room Database (SQLite):** Esquema relacional compuesto por **2 tablas maestras** (`reparaciones`, `componentes`) y **1 tabla transaccional** (`reparacion_componente`) para relaciones $N:M$ con deducción atómica de stock y datos semilla (`DatabaseCallback`).
        * **Preferences DataStore:** Persistencia ligera asíncrona para el tema oscuro en tiempo real, escala tipográfica del banco de trabajo y marca de tiempo de respaldos locales `.db`.
3. **Hardware Nativo (`utils/` & `ui/components/`):**
    * Integración con **CameraX** (`Preview`, `ImageCapture`) y `FileProvider` para almacenar fotografías de placas electrónicas en el directorio privado de la aplicación (`DIRECTORY_PICTURES`).

---

## Equipo de Desarrollo y Roles

* **Anthony (Líder / Navegación y Hardware Nativo):** Contenedor de inyección manual de dependencias (`AppContainer`), grafo de navegación tipado (`AppDestinations`, `AppNavigation`, `ModalNavigationDrawer`), integración nativa de CameraX (`CameraCaptureScreen`, `CameraPreview`, `CameraUtils`) y alta de registros (`NuevoRegistroScreen`).
* **Mario (Especialista UI/UX y Sistema de Diseño):** Sistema de diseño Material 3 (`BitacoraTheme` dinámico con escala de fuentes y modo oscuro), pantallas de acceso (`SplashScreen`, `LoginScreen`, `RegisterScreen`), tarjetas modulares (`RepairCardItem`, `ComponentCardItem`, `PhotoGalleryThumbnailRow`) y vistas interactivas.
* **Juan (Backend Local / Data Sources con Room):** Entidades relacionales (`ReparacionEntity`, `ComponenteEntity`, `ReparacionComponenteEntity`), modelo $N:M$ (`ReparacionWithComponentes`), DAOs reactivos con `Flow`, `DatabaseCallback` con datos semilla, exportación de respaldo SQLite (`DatabaseBackupHelper`) y pruebas instrumentales en Room.
* **Israel (Data Layer Repositories & State Holders):** Configuración de `Preferences DataStore` (`SettingsPreferencesDataSource`), contratos e implementaciones de repositorios, *State Holders* (`ReparacionesViewModel`, `InventarioViewModel`, `SettingsViewModel`, `DetalleReparacionViewModel`) y pruebas unitarias con repositorios *Fake*.

---

## Compilación y Ejecución

1. Clonar el repositorio y abrir el proyecto en **Android Studio**.
2. Sincronizar los archivos de Gradle (`./gradlew build`).
3. Ejecutar las pruebas unitarias locales de repositorios y *State Holders*:
   ```bash
   ./gradlew testDebugUnitTest