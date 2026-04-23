# Especificación: `map-vehicle-commands`

**Tipo:** Spec completa (no había `openspec/specs` previa para este dominio).

## Propósito

Comportamiento de los **tres botones** Stop / Start / Secure en el **mapa (detalle de unidad)**, reutilizando la UI existente, alineado con el API: catálogo **por dispositivo/protocolo**, envío **GPRS o SMS**, `lang` + `user_api_hash` en **todas** las peticiones.

## ADDED Requirements

### Requirement: Catálogo por unidad (get_device_commands)

| Regla | Detalle |
|-------|---------|
| Carga / cambio de unidad | Comandos **válidos** solo para el `device_id` activo en mapa. |
| Errores | No simular “éxito”; mensaje o estado si falla red o sesión. |

**Scenario: Cambio de unidad** — DADO otra unidad en mapa, CUANDO se muestra su ficha, ENTONCES el catálogo usado en los botones **DEBE** ser el de **esa** unidad.

**Scenario: Fallo** — DADO error de API o sesión, CUANDO el usuario toca un comando, ENTONCES **NO** “comando enviado” y **SÍ** feedback claro (reintento o login).

### Requirement: Tres acciones fijas hacia el catálogo

El sistema **DEBE** mapear Stop / Start / Secure a un **comando del catálogo** del dispositivo; si no hay mapeo, **no** enviar. Si el comando pide atributos obligatorios no cubribles por el path rápido, **no** enviar parcial; **no** dejar un estado de envío colgado.

**Scenario: Match** — DADO catálogo con comando compatible, CUANDO toca la acción, ENTONCES se arma `type` + `message` y se rutea a GPRS o SMS según el template.

**Scenario: Sin match** — DADO catálogo sin variante, CUANDO toca la acción, ENTONCES mensaje p. ej. “Esta unidad no admite este comando” y sin POST.

**Scenario: Faltan parámetros** — DADO requisitos sin default/elegibles en quick path, CUANDO toca la acción, ENTONCES informar; sin payload incompleto.

### Requirement: Envío (send_gprs_command / send_sms_command)

| Conexión | Comportamiento |
|----------|------------------|
| GPRS | `POST` con `device_id`, `type`, `message`, `auto_send_when_online` según contrato. |
| SMS | `POST` con `message` y `devices` (ids). |
| Auth | `lang` y `user_api_hash` en la petición. |

**Scenario: OK** — DADO envío exitoso (p. ej. `status: 1`), ENTONCES confirmación al usuario.

**Scenario: Offline / cola** — DADO GPRS con dispositivo sin enlace, CUANDO el API encola o difiere, ENTONCES no afirmar recepción inmediata en el vehículo; texto coherente con la política de `auto_send_when_online`.

### Requirement: Otros endpoints (PUEDE)

`send_command_data` y `sent_commands` **PUEDEN** usarse de soporte; **no** se exigen **nuevos** elementos de UI; el mínimo **NO** requiere historial en pantalla.

**Scenario: Mínimo** — SIN flujo a `sent_commands` en UI, un envío exitoso con feedback sigue siendo aceptable.

## REMOVED

Ninguno.

**Siguiente:** `sdd-design` (mapeo de red, estados); luego `sdd-tasks`.
