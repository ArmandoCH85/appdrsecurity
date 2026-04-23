# Mapa: zoom +/− móvil

## Problema
- La pantalla **Mapa** usaba `showControls` por defecto `false` → no había +/−.
- Con `showControls = true` los botones quedaban **bajo** `AppBottomBar` (orden de dibujo en `Box`).

## Solución
- `showControls = true` en el listado del mapa y vistas con barra de pestañas.
- Nuevo `controlsBottomExtraPadding: Dp` (p. ej. `80.dp`) para elevar el bloque y **insets** de `navigationBars`.
- Controles: iconos `Add` / `Remove` / `MyLocation`, 48dp, `MaterialTheme` superficie + `primary`, `contentDescription` en ES.

## Archivos
- `MapBridge.kt` (expect), `MapBridge.android.kt`, `MapBridge.ios.kt`, `App.kt` (call sites).
