# Design: Dashboard en pestaña Reportes

## Enfoque técnico

Sustituir el `Column` con `Arrangement.spacedBy(..., CenterVertically)` en `ReportsScreen` por contenido **alineado arriba** dentro de un **scroll vertical** (`verticalScroll` o `LazyColumn`), e insertar un composable **`ReportsHomeSummary`** antes del texto “Selecciona el tipo de reporte”. Los datos vienen de `AppUiState` ya cargado en login: **`devices`** y **`reports`**; se añade **`onRefreshReports`** (existente en `AppController`) para error de catálogo. Cumple [spec `reports-home`](specs/reports-home/spec.md).

## Decisiones de arquitectura

| Decisión | Opción elegida | Alternativa | Razón |
|----------|----------------|-------------|--------|
| Contenedor scroll | `LazyColumn` o `Column` + `verticalScroll` | Solo `Column` sin scroll | Spec exige scroll en viewport bajo; `LazyColumn` encaja con otras pantallas (p. ej. Perfil). |
| Ubicación UI | Composables privados en `App.kt` | Nuevo archivo `ReportsHome.kt` | El resto de tabs viven en `App.kt`; mantener consistencia salvo que el archivo crezca demasiado. |
| Resumen flota | Contar `DeviceSummary` con `isOnlineStatus(onlineStatus)` | Nuevo endpoint | Reutiliza reglas en `DeviceDisplayRules.kt`; sin API nueva. |
| Estado catálogo | Propagar `UiState<ReportCatalog>` a `ReportsScreen` | Duplicar fetch en pantalla | `loadReportCatalog` ya vive en `AppController`; una sola fuente de verdad. |
| Alertas opcionales | Pasar `UiState<List<AlertItem>>` y una fila compacta | Omitir | Spec “PUEDE”; mejora densidad sin obligar lógica nueva. |
| Último reporte | Omitir en v1 | Mostrar `reportFeedback` | Texto es volátil y contextual a `ReportView`; evitar ruido en home hasta acordar copy. |

## Flujo de datos

```
AppUiState (devices, reports [, alerts])
       │
       ▼
ReportsScreen ──► ReportsHomeSummary (texto + opcional fila alertas)
       │
       ├── ReportOptionCard → openHistory / openReportView
       └── onRefreshReports → AppController.refreshReports()
```

## Cambios de archivo

| Archivo | Acción |
|---------|--------|
| `composeApp/.../App.kt` | Ampliar firma de `ReportsScreen`; envolver cuerpo en scroll; añadir `ReportsHomeSummary` y helpers de copy para loading/error/success. |
| `composeApp/.../App.kt` (call site) | Pasar `state.reports`, `state.alerts` (opcional), `controller::refreshReports`. |

## Contratos de UI (parámetros nuevos)

- `reports: UiState<ReportCatalog>`
- `onRefreshReports: () -> Unit`
- `alerts: UiState<List<AlertItem>>` (opcional; si no se usa, no pasar)

Sin nuevos tipos de dominio: el resumen es presentación pura derivada de `UiState` existentes.

## Pruebas

| Capa | Qué | Cómo |
|------|-----|------|
| Lógica de conteo | Totales / en línea desde lista | Función pura testable en `commonTest` (extraer a `ReportsHomeSummaryKt` o `ReportHomeMetrics.kt` si se desea aislar). |
| UI | Layout y scroll | Manual / screenshot; proyecto sin suite UI Compose obligatoria. |

## Rollout

Sin feature flag ni migración. Revertir = restaurar firma antigua y quitar el bloque de resumen.

## Preguntas abiertas

- [ ] Copy final en español para estado “catálogo listo” (¿mostrar número de tipos de reporte?).
- [ ] ¿Fila de alertas solo con `Success` y recuento > 0, o siempre visible con “Sin alertas”?
