# Especificación: `reports-home`

**Tipo:** Spec completa (sin `openspec/specs/reports-home/spec.md` previa).

## Propósito

Pantalla principal de la pestaña **Reportes**: layout, **resumen compacto (dashboard ligero)** arriba, accesos **Recorrido** e **Historial**, estados de datos y accesibilidad móvil (objetivos táctiles, no solo color).

## Requirements

### Requirement: Layout y espacio

| Regla | Detalle |
|-------|---------|
| Scroll | Con viewport típico de teléfono, el cuerpo **DEBE** permitir **scroll vertical** si no cabe todo. |
| Vacío | **NO DEBE** quedar la zona de acciones como bloque centrado con **grandes franjas vacías** si hay datos o mensajes de estado. |
| Orden | El resumen **DEBE** ir **antes** del texto de elección de tipo y de las tarjetas Recorrido / Historial. |

#### Scenario: Apertura con datos

- **DADO** sesión iniciada y datos mínimos para el resumen
- **CUANDO** abre Reportes
- **ENTONCES** ve resumen, luego selector y las dos opciones, sin blanco como protagonista

#### Scenario: Viewport corto

- **DADO** pantalla baja o texto grande
- **CUANDO** hace scroll
- **ENTONCES** alcanza resumen y acciones sin quedar bloqueadas fuera de vista

### Requirement: Resumen mínimo

| Bloque | Obligatoriedad |
|--------|----------------|
| Unidades / flota | **DEBE** un indicador agregado (total, en línea, o mensaje “sin datos”). |
| Catálogo reportes | **DEBE** estado: cargando, listo o error, con texto claro. |

#### Scenario: Error de catálogo

- **DADO** fallo al cargar catálogo
- **CUANDO** entra a Reportes
- **ENTONCES** aviso o bloque de estado comunica el fallo y recuperación coherente con la app

### Requirement: Resumen opcional

**PUEDE** incluir alertas resumidas, ayuda breve o última generación de reporte **si** el estado es fiable; si no hay datos secundarios, **NO DEBE** forzar huecos grandes.

### Requirement: Recorrido e Historial

Mismos destinos y textos de tarjetas; solo cambia contexto visual y posición relativa al resumen.

#### Scenario: Sin regresión

- **DADO** usuario en Reportes
- **CUANDO** pulsa Recorrido o Historial
- **ENTONCES** misma navegación que antes del cambio

### Requirement: Accesibilidad

| Criterio | Detalle |
|----------|---------|
| Tacto | Controles **DEBEN** ~44×44 dp o hit target equivalente. |
| Color | **NO** solo color; texto o icono con etiqueta accesible. |
| Contraste | Textos legibles sobre fondo de la app. |

#### Scenario: Lector de pantalla

- **DADO** TalkBack / VoiceOver
- **CUANDO** foco en un bloque del resumen
- **ENTONCES** anuncia métrica o estado con nombre significativo

## Criterios de producto (no implementación)

Resumen **2–4** bloques como máximo; jerarquía tipo escaneo rápido: título → métricas breves → acciones.
