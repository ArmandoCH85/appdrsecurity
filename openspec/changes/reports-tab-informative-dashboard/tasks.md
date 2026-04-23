# Tasks: Dashboard en pestaña Reportes

## Phase 1: Layout y parámetros

- [x] 1.1 `App.kt`: ampliar `ReportsScreen` con `reports: UiState<ReportCatalog>`, `onRefreshReports: () -> Unit`, y `alerts: UiState<List<AlertItem>>` (o omitir alertas si se deja para fase 2).
- [x] 1.2 Call site `RootTab.Reports`: pasar `state.reports`, `controller::refreshReports`, y `state.alerts` si aplica.
- [x] 1.3 Reemplazar el `Column` interno con `weight(1f)` + `CenterVertically` por `LazyColumn` (o `Column` + `verticalScroll`) con `Arrangement.spacedBy` **sin** centrar verticalmente todo el bloque; padding superior pequeño y contenido desde arriba.

## Phase 2: Resumen (dashboard ligero)

- [x] 2.1 Añadir composable `ReportsHomeSummary`: fila o tarjeta para **unidades** (loading / error / lista vacía / “N unidades · M en línea” usando `isOnlineStatus`).
- [x] 2.2 Mismo bloque o adyacente: **catálogo** `UiState<ReportCatalog>` — loading (indicador o texto), success (mensaje “listo” o conteo de `types`), error (mensaje existente + `TextButton` “Reintentar” → `onRefreshReports`).
- [x] 2.3 Opcional: una línea para **alertas** (`Success` → recuento; `Loading` → texto breve; `Error` → omitir o mensaje corto).
- [x] 2.4 `semantics` / `contentDescription` en bloques del resumen para TalkBack/VoiceOver.

## Phase 3: Verificación

- [ ] 3.1 Probar en emulador: pestaña Reportes con catálogo OK, catálogo en error + reintento, lista de dispositivos vacía, muchas unidades, fuente grande (scroll).
- [ ] 3.2 (Opcional) Extraer función pura `fleetSummary(devices: List<DeviceSummary>): String` y test en `commonTest`.

## Phase 4: Cierre SDD

- [ ] 4.1 `sdd-verify` o checklist manual contra [spec](specs/reports-home/spec.md).
- [ ] 4.2 Tras merge: `sdd-archive` cuando corresponda.
