# Archive report: `map-vehicle-commands`

**Fecha:** 2026-04-23  
**Modo:** OpenSpec (sync a `openspec/specs/` + carpeta movida a `changes/archive/`)

## Specs sincronizados

| Dominio | Acción |
|---------|--------|
| `map-vehicle-commands` | Creado `openspec/specs/map-vehicle-commands/spec.md` a partir del delta (spec completa; no existía spec principal previa). |

## Contenido archivado (auditoría)

- `proposal.md`
- `specs/map-vehicle-commands/spec.md`
- `design.md`
- `tasks.md` (í­tems principales completados; 2.2 prueba manual pendiente de entorno)
- `verify-report.md`
- `archive-report.md` (este archivo)

## Evidencia post-implementación

- `AppControllerQuickCommandTest`: catálogo vacío / éxito GPRS.
- `App.kt`: `CommandChip` con `clickable` + `enabled`; ficha mapa/dispositivos con `isSendingQuickCommand`.
- `AppController`: `selectDevice` limpia comandos; `QuickCommandException` en quick path.

## Nota

La tarea **2.2** (prueba manual GPRS/SMS en producción) sigue siendo responsabilidad operativa, no bloquea el archivo técnico.
