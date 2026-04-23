# Tasks: map-vehicle-heading-marker

- [x] **1** — Bitmap ≥44×44 dp; glifo 32/38 dp centrado (normal/selected)
- [x] **2** — Borde blanco vía `Paint` STROKE + FILL (sin escala de rim)
- [x] **3** — `Path` alineado con `ic_map_heading_24` (misma geometría)
- [ ] **4** — (Opcional) Afinar ancla `Marker` si se valida GPS ≠ centro de icono

**Estado implementación (2026-04-23):** 1–3 en `MapBridge.android.kt`.
