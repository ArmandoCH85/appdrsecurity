# Design: Icono de rumbo de vehículo (mapa Android / osmdroid)

## Technical Approach

Fijar un **glifo de rumbo** único (vector → bitmap con supersampling) coherente con `courseDegrees` y `icon_color` de Wox, optimizado para **lectura en tiles OSM (Mapnik)** y **pulgares en móvil**. El enfoque: separar claramente **tamaño visual** del **área de interacción**, fijar **ancla y pivote de rotación** con criterio de producto, y **limitar** iconos remotos para no romper la jerarquía visual. La implementación sigue en `MapBridge.android.kt` (sin cambiar el contrato de `DeviceLivePosition`).

## Architecture Decisions

| Decisión | Opción elegida | Alternativas | Razonamiento |
|----------|----------------|--------------|--------------|
| Forma del glifo | Dardo: triángulo isósceles **largo y estrecho** (tapa ~0.2–0.25 del ancho del viewBox) | Flecha con tallo (Material `arrow_upward`); círculo+sector (como GMaps) | Tallo genera asimetría tosca al rotar; sector exige lógica extra en canvas. Dardo = convención legible a baja resolución. |
| Tamaño en pantalla | **Glifo** ~32–40 dp según `selected`; **bitmap** ≥ **44×44 dp** con padding transparente | Un solo 36 dp sólido | WCAG/Apple HIG: blanco mín. ~44 pt para toque; el icono puede ser más pequeño **dentro** de la caja. |
| Borde/halo | Trazo de contraste: **1–1,5 dp** (equiv.) blanco o crema, opacidad ≥95 %, **sin** ensanchar mucho vía `scale` (evitar 1.05+) | Sombra `Paint` / halo por escala 1.1 | Escala baja inflaba un triángulo "grotesco"; trazo fijo mantiene lectura clara. |
| Ancla y rotación | Mantener `rotation = courseDegrees` y evaluar ancla **(0.5, 0.5)** vs **(0.5, ~0.62)** (centro ≈ peana del dardo) | Ancla baja tipo “chincheta” | (0.5,0.5) alinea con pivote geométrico. Si se desea “proa hacia rumbo” con el punto en la **base** del vehículo, bajar ancla y compensar; requiere validación con datos reales. |
| Color | Tokens ya existentes (`courseArrowFillColor`); **contrastar** con tile claro/oscuro; no confiar solo en relleno | Tinta plana pura | Mapas: un **borde** mejora contraste; evitar rellano sin borde. |
| Icono remoto (Wox) | Seguir `capBitmapForMap` (lado máx. ~32–40 dp); opcional: máscara/canto suave v2 | Tamaño nativo de PNG | PNG grande compite con el mapa; tamaño fijo = catálogo homogéneo. |

**Alternativas consideradas (descartadas para v1):** doble resolución con asset por densidad; renderizado Compose→Picture→Bitmap (mas pesado); cambiar proveedor de mapas.

## Data Flow

```
DeviceSummary (iconType, iconColor, course, mapIconId)
    → AppController.vehicleMapIconUrl (null si arrow)
    → DeviceLivePosition (iconUrl, courseDegrees, iconColor)
    → MapBridge: if iconUrl → BitmapDrawable; else if history → pin; else buildCourseArrowIcon + rotation
    → osmdroid Marker (position, icon, rotation, anchor)
```

## File Changes (previstas post-aprobación)

| Archivo | Acción | Descripción |
|---------|--------|-------------|
| `res/drawable/ic_map_heading_24.xml` | Modificar | Ajustar `pathData` a proporción objetivo; opcional: grupo + path de “keyline” si se pasa a stroke. |
| `MapBridge.android.kt` | Modificar | `rasterizeHeadingArrow`: caja 44+ dp, glifo escalado al centro; borde vía trazo/segunda pasada, no solo `scale`; constantes de dp para normal/selected. |
| (Opcional) `MapBridge.android.kt` | Modificar | Hit-test / `setHotspot` si osmdroid expone; si no, documentar límite. |
| `design.md` | Creado | Este documento. |

## Interfaces / Contracts

- `DeviceLivePosition.courseDegrees: Double?` — 0 = Norte, sentido agujas; sin cambios.
- `buildCourseArrowIcon` → `BitmapDrawable` cuyo ancho/alto = **caja táctil**; el dibujo queda con **insets** si el glifo < caja.
- `Marker.rotation` (grados) + `setAnchor(anchorU, anchorV)` — contrato osmdroid; ancla (0,0) esquina sup. izq.

## Testing Strategy

| Capa | Qué | Cómo |
|------|-----|------|
| Visual / manual | Legibilidad en zoom 10–17 sobre OSM | Dispositivo o emulador, varias densidades |
| Lógica | `courseArrowFillColor` + sin crash con `iconColor` nulo | Unitario existente o nuevo en `commonTest` si aplica |
| Regresión | `iconType=arrow` + URL null usa vector | E2E manual |

## Migration / Rollout

No migración. Rollout: deploy normal; Wox/PNG sin cambio de API.

## Open Questions

- [ ] ¿El punto de GPS en negocio representa **centro** del vehículo o **antena**? (afinado de ancla.)
- [ ] ¿Se requiere **estado** “con curso desconocido” (círculo o cruces) vía `course == null`?
- [ ] `setAnchor` con ancla baja: ¿validar con uno o dos dispositivos antes de fijar?

**Next step:** Tareas 1–3 **implementadas** en `MapBridge.android.kt` (caja 44/48dp, glifo 32/38dp, trazo+relleno vía `Path`); tarea 4 ancla opcional queda para validación de negocio.

**Implementado (patches):** `rasterizeHeadingArrow(boxDp, glyphDp, …)` + `headingArrowPath` alineado a `ic_map_heading_24`.
