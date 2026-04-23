# Propuesta: Comandos operativos en mapa (vista unidad)

## Resumen

Dar **comportamiento end-to-end** a los **tres botones de acción rápida** (Stop / Start / Secure) de la ficha de unidad en el **mapa**, alineado con el contrato de API de Dr Security GPS: el catálogo de comandos **depende del dispositivo y su protocolo**; el envío usa **GPRS o SMS** según lo declare cada comando; las peticiones requieren **sesión** (`lang`, `user_api_hash`).

## Capabilities (mapeo de especificaciones)

| Tipo | Capability | Especificación |
|------|------------|----------------|
| New | `map-vehicle-commands` | `openspec/changes/map-vehicle-commands/specs/map-vehicle-commands/spec.md` |

## Alcance

- **Incluido:** Comportamiento observable de la UI **ya presente** (ficha inferior con Stop / Start / Secure en vista mapa con unidad seleccionada); carga de comandos soportados; envío; feedback de error/éxito; criterio por “tipo de unidad” vía **respuesta** de `get_device_commands` (no nuevos botones en pantalla).
- **Excluido (salvo requisito explícito en spec):** Nuevas pantallas de historial, nuevos formularios de parámetros, almacenamiento de credenciales en código, ni `send_command_data` / `sent_commands` como UI obligatoria (pueden quedar como soporte o verificación en implementación o fases futuras según el spec).

## Criterio de aceptación (alto nivel)

- Con sesión válida, al pulsar un botón, el backend recibe un **POST** coherente (`send_gprs_command` o `send_sms_command` según el comando).
- Si la unidad **no ofrece** un comando mapeable, el usuario recibe explicación clara (no fallo silencioso).
- Requisitos mínimos de atributos del comando: si hace falta interacción que la UI actual no ofrece, el sistema **no** debe colgar: mensaje o estado explícito.

## Artefacto y modo SDD

- **Persistencia de spec:** `openspec` (delta en carpeta de cambio; dominio sin spec principal previa → spec **completa** bajo el cambio).
