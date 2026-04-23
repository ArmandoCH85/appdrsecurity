# Propuesta: Dashboard en pestaña Reportes

## Intención

Reducir sensación de vacío en la pantalla principal de **Reportes**, añadiendo un **resumen informativo compacto** encima de la selección de tipo de reporte, sin sustituir los accesos actuales a Recorrido e Historial.

## Alcance

- Solo la pestaña **Reportes** (pantalla de elección de tipo), no el flujo completo de generación en `ReportView`.
- Comportamiento y contenido de la nueva región de resumen; principios de layout y accesibilidad móvil.

## Capacidades (para specs)

### New Capabilities

- **`reports-home`** — Comportamiento de la pantalla raíz de Reportes: jerarquía visual, región de dashboard/resumen, accesos existentes a Recorrido e Historial, estados de datos y accesibilidad.

## Enfoque (alto nivel)

- Priorizar **scroll vertical** y anclar contenido útil arriba; evitar centrar el bloque interactivo dejando ~40–50 % de pantalla en blanco.
- El dashboard **resume** información contextual (p. ej. flota/unidades, disponibilidad del catálogo de reportes, señal de alertas) usando datos que la app ya pueda exponer a esta pantalla; métricas opcionales si hay fuente fiable.

## Riesgo y reversión

- Bajo riesgo visual; reversión: ocultar o simplificar la región de resumen y restaurar layout anterior.
