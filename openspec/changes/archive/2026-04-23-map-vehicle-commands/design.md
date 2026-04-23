# Design: Comandos en mapa (ficha de unidad)

## Technical Approach

Consolidar el flujo **get_device_commands → mapeo Stop/Start/Secure → send_gprs_command | send_sms_command** ya presente en `KtorDrSecurityApi` y `AppController`, cumpliendo la spec: **catálogo acoplado a la unidad seleccionada**, feedback explícito si no hay comando o faltan parámetros, y sin nuevos componentes de UI. La `baseUrl` y `authGet` / `authPost` con `lang` + `user_api_hash` siguen el patrón existente de sesión.

## Architecture Decisions

| Decisión | Elegida | Alternativas | Razonamiento |
|----------|---------|--------------|--------------|
| Catálogo al cambiar de unidad | Poner `commands` en `UiState.Empty` en `selectDevice` y volver a `loadCommands` | Guardar `deviceId` en el estado de comandos; solo invalidar con TTL | Mínima invasión; evita mapear acciones con la lista de **otro** dispositivo si el usuario toca un chip antes de que termine el GET. |
| Errores de negocio (sin template / parámetros) | `QuickCommandException` con mensaje → `activeMessage` | `error()` + `runCatching` y mensaje genérico en `handleFailure` | El backend ya se reflejaba; el fallo de producto (no aplica comando) no era visible para el usuario. |
| `send_command_data` / `sent_commands` | Fuera de v1 (spec “PUEDE”) | Pre-cargar plantillas vía `send_command_data` | `get_device_commands` es suficiente para quick actions; el tab Comandos ya lista el catálogo completo. |
| Cuerpo GPRS | `submitForm` con `device_id`, `type`, `message`, `auto_send_when_online` | JSON body | Es el patrón actual en `KtorDrSecurityApi` y alinea con el uso de form de la app. |
| `auto_send_when_online` | `CommandRequest` default `true` | Toggles en mapa (nuevo UI) | Excluido por producto: sin nuevos controles. |

## Data Flow

```
Map tap / lista → openDetail / selectDevice
  → selectedDeviceId + commands=Empty
  → loadCommands(deviceId) → get_device_commands
  → state.commands = Success(list)

DeviceQuickSheet: CommandChip onClick("engine_stop"|…)
  → sendQuickCommand(type)
  → (si no Success) getDeviceCommands fresh
  → resolveQuickCommandTemplate + buildQuickCommandMessage
  → CommandsRepository.sendCommand → Gprs o Sms
```

## File Changes

| Archivo | Acción | Descripción |
|---------|--------|-------------|
| `AppController.kt` | Modificar | `selectDevice`: reset `commands`; `sendQuickCommand`: mensajes explícitos y `QuickCommandException`. |
| `KtorDrSecurityApi.kt` | (Sin cambio obligatorio) | Endpoints y auth ya alineados con la spec. |
| `design.md` | Creado | Este documento. |

## Interfaces / Contracts

- **GET** `get_device_commands?device_id&lang&user_api_hash` → `List<CommandTemplate>` (ya modelado).
- **POST** `send_gprs_command` (form) / `send_sms_command` (form) — sin cambio de esquema.
- **Quick path**: `message` JSON par claves de `attributes` o `""` si no hay cuerpo (ver `buildQuickCommandMessage`).

## Testing Strategy

| Capa | Qué | Cómo |
|------|-----|------|
| Unit | Mapeo `resolveQuickCommandTemplate` / `buildQuickCommandMessage` | Tests existentes en `AlertsPollingPolicyTest` y afines; añadir caso de ausencia de template si aplica. |
| Manual / integración | Tap en mapa + envío en entorno con hash válido | Contra `ApiEnvironment.baseUrl` real. |

## Migration / Rollout

No aplica: solo cambio de lógica de estado y mensajes; sin migración de datos ni feature flags.

## Open Questions

- [ ] Ajustar el texto “Comando enviado” si el producto pide dejar claro cola **GPRS** offline (p. ej. un toast distinto cuando el API publica cola) — requiere contrato de respuesta analizado.
- [ ] (Opcional) Deshabilitar visualmente los chips mientras `isSendingCommand` o `commands` aún en carga, sin añadir controles nuevos (solo `enabled`).

---

*Presupuesto: &lt; 800 palabras.*
