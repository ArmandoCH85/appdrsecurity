# Tasks: Comandos en mapa (vista unidad)

## Phase 1: Comportamiento y estado (core)

- [x] 1.1 `AppController.selectDevice`: reset de `commands` a `Empty` al cambiar de unidad para no reusar el catálogo de otra.
- [x] 1.2 `AppController.sendQuickCommand`: reemplazar `error()` genérico por excepción de dominio o ramas con `activeMessage` claro: sin comando; parámetros obligatorios sin quick path; mantener ruta de red/401 existente.
- [x] 1.3 Propagar `isSendingCommand` a `DeviceQuickSheet` / `CommandChip` con `enabled` + `clickable` (también se corrigió el chip para que dispare `onClick`).

## Phase 2: Red y repositorio (verificación)

- [x] 2.1 Revisar `KtorDrSecurityApi` para `get_device_commands`, `send_gprs_command`, `send_sms_command` (query `lang`/`user_api_hash` + cuerpos).
- [ ] 2.2 Probar con entorno real: una unidad GPRS y una con comando SMS, Stop/Start/Secure según catálogo.

## Phase 3: Pruebas

- [x] 3.1 `AppControllerQuickCommandTest`: catálogo vacío → mensaje “no admite”; catálogo con `immobilize` → un `sendGprsCommand`.
- [x] 3.2 Ejecutar `compileDebugUnitTest` / `testDebugUnitTest` (regresión en `AppController`).

## Phase 4: Cierre SDD (opcional)

- [x] 4.1 `sdd-verify` o checklist manual contra [spec](specs/map-vehicle-commands/spec.md) → [verify-report.md](verify-report.md).
- [x] 4.2 `sdd-archive`: spec en `openspec/specs/map-vehicle-commands/`, carpeta de cambio en `openspec/changes/archive/2026-04-23-map-vehicle-commands/`.
