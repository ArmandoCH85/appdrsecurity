# Guía completa de diseño UI — réplica visual fiel de la app móvil

## Propósito del documento
Esta guía define el sistema visual necesario para reconstruir la app con un resultado **lo más parecido posible** a las capturas de referencia entregadas.

El documento cubre:
- vista general del sistema visual
- paleta de colores global
- tipografía
- espaciado, radios, sombras y estilo de iconos
- especificación detallada por pestaña
  - **Mapa**
  - **Perfil**
  - **Reportes**
  - **Unidades**
- navegación inferior global
- design tokens base
- reglas de fidelidad visual para implementación

> **Importante**
> Los valores de color, tamaños y proporciones están **estimados desde screenshots**, no exportados desde el diseño fuente. Aun así, están definidos para lograr una réplica visual muy cercana.

---

# 1. Vista general del sistema visual

## 1.1 Carácter visual de la app
La app transmite una identidad visual:
- moderna
- limpia
- corporativa
- ligera
- funcional
- pensada para operación y monitoreo en móvil

La interfaz se construye sobre una lógica muy clara:
- **fondo general gris muy claro**
- **cards blancas elevadas**
- **esquinas redondeadas grandes**
- **azul celeste como color primario**
- **jerarquía tipográfica fuerte en títulos**
- **espaciado generoso**
- **uso de estados por color**

No se ve como una UI dura ni industrial. Se ve amable, ordenada y táctil.

---

## 1.2 Principios visuales que deben respetarse

### Fondo de pantalla
El fondo general **no debe ser blanco puro**. Debe ser gris muy claro para que las cards resalten.

**Color recomendado:** `#F3F3F3`

### Cards
Todas las superficies relevantes deben sentirse como tarjetas flotantes:
- fondo blanco
- sombra suave
- radio amplio
- padding cómodo

### Azul del sistema
El color principal del sistema es un azul celeste limpio.

**Color principal recomendado:** `#58A8E0`

Debe usarse en:
- iconos activos
- botones de acción
- estados activos de navegación
- tabs activas
- acentos de selección
- fondos suaves de iconografía

### Estados por color
La app organiza estados con un lenguaje simple:
- **azul:** acción, navegación, foco, selección
- **verde:** activo, conectado, live, éxito
- **naranja:** idle, advertencia, pausa
- **rojo:** stopped, alerta, error
- **morado:** seguridad, contraseña, cuenta

---

# 2. Paleta global de colores

## 2.1 Colores principales
| Token | Hex | Uso principal |
|---|---|---|
| Primary Blue | `#58A8E0` | color principal del sistema |
| Primary Blue Alt | `#50A8E0` | variante secundaria del azul |
| Primary Blue Light | `#EAF4FD` | fondos suaves de iconos, pills y botones |
| Primary Blue Soft | `#D9EDF9` | rellenos de apoyo |

## 2.2 Fondos y superficies
| Token | Hex | Uso principal |
|---|---|---|
| Background App | `#F3F3F3` | fondo global |
| Surface Card | `#FFFFFF` | cards principales |
| Surface Soft | `#F7F7F7` | interiores suaves |
| Surface Muted | `#EEEEEE` | tabs inactivas, zonas neutras |

## 2.3 Textos
| Token | Hex | Uso principal |
|---|---|---|
| Text Primary | `#222222` | títulos y texto principal |
| Text Secondary | `#7A7A7A` | subtítulos |
| Text Muted | `#A0A0A0` | placeholders y metadata |
| Text on Blue | `#FFFFFF` | texto sobre azul |

## 2.4 Bordes y divisores
| Token | Hex | Uso principal |
|---|---|---|
| Border Light | `#E6E6E6` | bordes suaves |
| Divider | `#ECECEC` | separadores internos |

## 2.5 Estados
| Token | Hex | Uso principal |
|---|---|---|
| Success Green | `#48A850` | connected, live, moving |
| Success Green Light | `#EAF7EC` | fondo suave verde |
| Warning Orange | `#F89818` | idle, warning, pause |
| Warning Orange Light | `#FFF3DD` | fondo suave naranja |
| Danger Red | `#EA5954` | stopped, alert |
| Danger Red Light | `#FDEAEA` | fondo suave rojo |
| Purple Accent | `#AE59D9` | password, security |
| Purple Light | `#F4EAFB` | fondo suave morado |

## 2.6 Neutros adicionales
| Token | Hex | Uso principal |
|---|---|---|
| Dark Chip | `#6D7178` | chip activa oscura |
| Dark Chip Count | `#8B9097` | contador en chip activa |
| Icon Gray | `#6F6F6F` | iconos neutros |
| Chevron Gray | `#B8B8B8` | chevrons y flechas |
| Meta Gray | `#969696` | metadata secundaria |
| Count Gray | `#4A4A4A` | texto funcional medio |

---

# 3. Tipografía

## 3.1 Familia tipográfica recomendada
La referencia visual se parece fuertemente a una interfaz Android basada en **Roboto**.

Usar:
- `Roboto`
- fallback: `Inter, system-ui, sans-serif`

## 3.2 Jerarquía tipográfica
| Uso | Tamaño | Peso | Color recomendado |
|---|---:|---:|---|
| Display Title | 32px | 800 | `#222222` |
| Screen Title | 22px | 700 | `#222222` |
| Section Title | 18px | 700 | `#222222` |
| Card Title | 16px | 700 | `#222222` |
| Body | 15px | 500 | `#222222` |
| Body Secondary | 14px | 400-500 | `#7A7A7A` |
| Caption | 13px | 400-500 | `#969696` |
| Micro / Nav Label | 12px | 500 | `#8E8E8E` |

## 3.3 Reglas tipográficas
- títulos principales en negro suave, nunca negro puro absoluto
- subtítulos siempre con menos peso y menor contraste
- contadores y metadata con gris medio
- texto sobre fondos de color fuerte en blanco
- no usar demasiadas variaciones de fuente, todo debe verse uniforme

---

# 4. Bordes, radios, sombras y espaciado

## 4.1 Radios
| Elemento | Valor recomendado |
|---|---:|
| Card grande | 24px |
| Card media | 20px |
| Input / search | 20px a 22px |
| Chip | 18px |
| Badge | 16px |
| Botón redondo | 54px a 56px |
| Label pequeña | 8px a 10px |

## 4.2 Sombras
### Sombra principal de cards
```css
box-shadow: 0 6px 18px rgba(0, 0, 0, 0.08);
```

### Sombra ligera
```css
box-shadow: 0 4px 12px rgba(0, 0, 0, 0.06);
```

### Regla
Las sombras siempre deben verse:
- suaves
- cortas
- elegantes
- casi imperceptibles

No usar sombras oscuras ni muy largas.

## 4.3 Espaciado base
| Regla | Valor recomendado |
|---|---:|
| Padding horizontal de pantalla | 20px a 24px |
| Padding interno de cards | 18px a 22px |
| Separación entre bloques | 20px a 28px |
| Separación entre filas | 14px a 18px |
| Gap icono-texto | 12px a 16px |
| Altura de fila tipo settings | 84px a 92px |
| Altura de chips | 42px a 48px |
| Altura bottom nav | 82px a 88px |

---

# 5. Estilo global de iconos

## 5.1 Características
Los iconos deben verse:
- simples
- limpios
- suaves
- con trazos medios
- amigables al tacto
- sin exceso de detalle

## 5.2 Colores de iconos
- acción principal: `#58A8E0`
- acción neutra: `#6F6F6F`
- seguridad: `#AE59D9`
- éxito: `#48A850`
- advertencia: `#F89818`

## 5.3 Fondos de iconos circulares
Los iconos de listas y módulos suelen vivir dentro de círculos de fondo suave:
- azul claro: `#EAF4FD`
- verde claro: `#EEF8EF`
- naranja claro: `#FFF5E8`
- morado claro: `#F4EAFB`

---

# 6. Pestaña Mapa

## 6.1 Objetivo visual de la pantalla
Es la vista operativa en tiempo real. Debe transmitir:
- monitoreo
- ubicación
- estado instantáneo
- control rápido

La lógica visual es:
- mapa de fondo claro
- UI flotante blanca por encima
- información resumida en tarjetas y chips
- controles redondos accesibles

## 6.2 Estructura visual
1. mapa ocupando toda la pantalla útil
2. card superior flotante con título y acciones
3. fila de chips de estado debajo del header
4. marcadores y etiquetas sobre el mapa
5. controles flotantes laterales de mapa
6. bottom navigation fija

## 6.3 Fondo del mapa
Usar mapa claro tipo Google Maps:
- base gris muy clara
- trazos de calles suaves
- elementos urbanos en azul/gris tenue
- zonas verdes muy discretas
- sin modo oscuro
- la cartografía no debe competir con la UI

## 6.4 Card superior flotante
### Contenedor
- fondo: `#FFFFFF`
- radio: `24px`
- sombra: `0 6px 18px rgba(0,0,0,0.08)`
- padding: `20px`

### Título `Live Map`
- color: `#222222`
- tamaño: `22px`
- peso: `700`

### Icono izquierdo
- color: `#58A8E0`

### Estado `LIVE`
- punto verde: `#48A850`
- texto LIVE: `#48A850`
- texto del conteo secundario: `#7A7A7A`
- tamaño: `13px a 14px`
- peso: `600`

## 6.5 Botones redondos del header
### Buscar
- fondo: `#F5F8FB`
- borde: `1px solid #D9EAF5`
- icono: `#58A8E0`
- tamaño: `54px`
- forma: circular

### Centrar / localizar
- fondo: `#FAFAFA`
- borde: `1px solid #E6E6E6`
- icono: `#6F6F6F`
- tamaño: `54px`
- forma: circular

### Ajustes
- fondo: `#F5F8FB`
- borde: `1px solid #CFE5F3`
- icono: `#58A8E0`
- tamaño: `54px`
- forma: circular

## 6.6 Chips de filtro de estado
### Chip activa `All`
- fondo: `#6D7178`
- texto: `#FFFFFF`
- punto: `#FFFFFF`
- contador fondo: `#8B9097`
- contador texto: `#FFFFFF`
- radio: `18px`
- altura: `42px a 46px`

### Chips inactivas
- fondo: `#FFFFFF`
- texto: `#3F3F3F`
- contador fondo: `#F3F3F3`
- contador texto: `#6F6F6F`
- sombra ligera

### Estados de chips
- `Moving` -> punto verde `#48A850`
- `Stopped` -> punto rojo `#EA5954`
- `Idle` -> punto naranja `#F89818`

## 6.7 Marcadores y etiquetas
### Pin principal
- verde del pin: `#48A850`
- interior: `#FFFFFF`
- icono interior: `#222222`

### Tooltip `GPS MINI`
- fondo: `#FFFFFF`
- texto: `#2A2A2A`
- radio: `16px`
- sombra: `0 4px 12px rgba(0,0,0,0.08)`
- padding: `12px 16px`

### Label pequeña inferior
- fondo: `#FFFFFF`
- borde: `2px solid #58A8E0`
- texto: `#2A2A2A`
- radio: `8px`

### Marcadores secundarios grises
- fill aproximado: `#6E7B84`

## 6.8 Controles flotantes del mapa
### Botón foco / expandir
- fondo: `#FFFFFF`
- icono: `#6F6F6F`
- sombra ligera

### Control de zoom
- contenedor blanco vertical
- separador interno: `#ECECEC`
- símbolos `+` y `-` grises
- bordes suaves

## 6.9 Resultado esperado
La pestaña Mapa debe sentirse como una capa de UI premium y ligera sobre un mapa operativo, con excelente legibilidad y botones táctiles muy evidentes.

---

# 7. Pestaña Perfil

## 7.1 Objetivo visual de la pantalla
Es una pantalla de configuración y cuenta. Debe comunicar:
- orden
- claridad
- seguridad
- estructura limpia

## 7.2 Estructura visual
1. header con título grande y subtítulo
2. icono grande de settings en esquina superior derecha
3. card de usuario / perfil
4. sección `Preferences`
5. card lista de preferencias
6. sección `Account & Security`
7. bottom navigation

## 7.3 Header
### Título `Settings`
- color: `#222222`
- tamaño: `32px`
- peso: `800`

### Subtítulo `Manage your preferences`
- color: `#7A7A7A`
- tamaño: `15px a 16px`
- peso: `400`

### Icono grande de ajustes
- círculo de fondo: `#EEF1F4`
- icono: `#58A8E0`
- tamaño del círculo: `74px a 82px`

## 7.4 Card de perfil
### Contenedor
- fondo: `#FFFFFF`
- radio: `24px`
- sombra: `0 6px 18px rgba(0,0,0,0.08)`
- padding: `20px`

### Avatar
- fondo del avatar: `#58A8E0`
- letra inicial: `#FFFFFF`
- punto de estado: `#48A850`
- borde del punto: `#FFFFFF`

### Indicador de carga / loader
- color del trazo: `#58A8E0`

## 7.5 Título de sección `Preferences`
- color: `#222222`
- tamaño: `18px`
- peso: `700`

## 7.6 Card de lista de ajustes
### Contenedor
- fondo: `#FFFFFF`
- radio: `24px`
- sombra: `0 4px 12px rgba(0,0,0,0.06)`

### Divisores
- color: `#ECECEC`

### Título de fila
- color: `#222222`
- tamaño: `16px`
- peso: `700`

### Subtítulo de fila
- color: `#7A7A7A`
- tamaño: `14px`
- peso: `400`

### Chevron
- color: `#B8B8B8`

## 7.7 Iconografía por opción
### Language
- icono: `#64B96A`
- fondo circular: `#EEF8EF`

### Push Notifications
- icono: `#F5B400`
- fondo circular: `#FFF5E8`

### Location Services
- icono: `#58A8E0`
- fondo circular: `#EAF4FD`

### Change Password
- icono: `#AE59D9`
- fondo circular: `#F4EAFB`

## 7.8 Toggle activo
- track: `#A9C9F2`
- thumb: `#58A8E0`
- estado: ON

### Regla visual del switch
Debe verse suave y corporativo, no brillante ni excesivamente material.

## 7.9 Resultado esperado
La pestaña Perfil debe sentirse espaciosa, muy limpia y ordenada, con foco en legibilidad y una jerarquía visual estable.

---

# 8. Pestaña Reportes

## 8.1 Objetivo visual de la pantalla
Es una vista de análisis y generación de reportes. Debe comunicar:
- control
- filtrado
- selección
- disponibilidad de reportes
- lectura rápida

## 8.2 Estructura visual
1. línea superior con contexto analítico
2. título grande de pantalla
3. dos botones circulares superiores
4. card de selección de periodo
5. selector de dispositivos
6. bloque de reportes disponibles
7. buscador
8. cards de reportes
9. bottom navigation

## 8.3 Header
### Texto superior
`Analytics & Insights (46 available)`
- color: `#6F6F6F`
- tamaño: `16px a 17px`
- peso: `400`

### Título `Reports`
- color: `#222222`
- tamaño: `32px`
- peso: `800`

## 8.4 Botones superiores derechos
### Botón circular outline
- fondo: transparente
- borde: `2px solid #58A8E0`
- icono: `#58A8E0`
- tamaño: `54px`
- forma: circular

### Botón circular filled
- fondo: `#58A8E0`
- icono: `#FFFFFF`
- tamaño: `54px`
- forma: circular

## 8.5 Card `Time Period`
### Contenedor
- fondo: `#FFFFFF`
- radio: `24px`
- sombra: `0 4px 12px rgba(0,0,0,0.06)`
- padding: `22px`

### Título interno
- color: `#222222`
- tamaño: `18px`
- peso: `700`

## 8.6 Tabs de periodo
### Activa
- fondo: `#58A8E0`
- texto: `#FFFFFF`
- radio: `16px`

### Inactiva
- fondo: `#EEEEEE`
- texto: `#666666`
- radio: `16px`

### Regla
No deben verse como tabs planas. Deben verse como botones grandes, blandos y táctiles.

## 8.7 Selector de dispositivos
- fondo: `#FFFFFF`
- radio: `20px`
- sombra: `0 4px 12px rgba(0,0,0,0.06)`
- texto principal: `#222222`
- icono: `#8DB7D8`
- chevron: `#8E8E8E`

## 8.8 Título `Available Reports (46)`
- color: `#222222`
- tamaño: `18px`
- peso: `700`

## 8.9 Search bar
- fondo: `#FFFFFF`
- borde: `1px solid #EFEFEF`
- radio: `22px`
- placeholder: `#C3C3C3`
- icono búsqueda: `#8E8E8E`

## 8.10 Card de reporte
### Contenedor
- fondo: `#FFFFFF`
- radio: `24px`
- sombra: `0 4px 12px rgba(0,0,0,0.06)`
- padding: `20px`

### Icono del reporte
- icono: `#63B56A`
- fondo circular: `#EEF8EF`

### Título del reporte
- color: `#222222`
- tamaño: `16px`
- peso: `700`

### Descripción
- color: `#6F6F6F`
- tamaño: `14px`
- peso: `400`

### Metadata
- color: `#969696`
- tamaño: `13px`
- peso: `400`

### CTA verde
- fondo: `#4CAF50`
- texto: `#FFFFFF`
- radio: `14px a 16px`
- peso: `700`

## 8.11 Resultado esperado
La pantalla debe verse muy ordenada, con cards limpias, filtros evidentes y llamados a la acción fáciles de reconocer.

---

# 9. Pestaña Unidades

## 9.1 Objetivo visual de la pantalla
Es una vista de operación de flota. Debe sentirse:
- escaneable
- operacional
- jerárquica
- compacta pero clara

## 9.2 Estructura visual
1. card superior flotante con resumen de vehículos
2. card de grupo o empresa
3. lista de cards individuales por unidad
4. bottom navigation

## 9.3 Card superior flotante
### Contenedor
- fondo: `#FFFFFF`
- radio: `24px`
- sombra: `0 6px 18px rgba(0,0,0,0.08)`
- padding: `20px`

### Título `Vehicles`
- color: `#222222`
- tamaño: `22px`
- peso: `700`

### Estado `CONNECTED`
- punto: `#48A850`
- texto: `#48A850`

### Conteo `37/38 vehicles`
- parte destacada: `#58A8E0`
- parte complementaria: `#4A4A4A`
- peso: `600`

### Botón buscar
- fondo: `#F5F8FB`
- borde: `1px solid #D9EAF5`
- icono: `#58A8E0`

### Botón refresh
- fondo: `#FAFAFA`
- borde: `1px solid #E6E6E6`
- icono: `#6F6F6F`

## 9.4 Card de grupo
Ejemplo: `QUIMICA SUIZA`

### Contenedor
- fondo: `#FFFFFF`
- radio: `24px`
- sombra: `0 4px 12px rgba(0,0,0,0.06)`
- padding: `18px a 20px`

### Nombre del grupo
- color: `#222222`
- tamaño: `18px`
- peso: `800`

### Conteo de vehículos
- color: `#58A8E0`
- tamaño: `15px`
- peso: `600`

### Indicador naranja de idle
- punto: `#F5B128`
- texto: `#6A6A6A`

### Botón `COLLAPSE`
- fondo: `#EAF4FD`
- texto: `#8BB7D8`
- radio: `14px`
- tamaño: `12px a 13px`
- peso: `700`

## 9.5 Card individual de vehículo
### Contenedor
- fondo: `#FFFFFF`
- radio: `22px`
- sombra: `0 4px 12px rgba(0,0,0,0.06)`
- overflow: hidden

### Franja lateral izquierda
- color: `#F89818`
- ancho: `6px a 8px`

### Badge de estado `Idle`
- fondo: `#FFF3DD`
- texto: `#D89A10`
- punto: `#F89818`
- radio: `16px`

### Código / placa
- color: `#222222`
- tamaño: `18px a 20px`
- peso: `800`

### Línea de métricas
#### Velocidad
- color: `#4A4A4A`
- tamaño: `14px`
- peso: `500`

#### Tiempo de marcha / play
- color: `#2FAE68`

#### Tiempo de pausa / pause
- color: `#E1A823`

## 9.6 Chip de encendido
- fondo: `#F7F7F7`
- borde: `1px solid #D7D7D7`
- texto general: `#6A6A6A`
- valor ON/OFF: `#4A4A4A`
- radio: `16px`

## 9.7 Bloque inferior de dirección
### Contenedor
- fondo: `#FAFAFA`
- borde superior: `1px solid #F0F0F0`
- padding: `16px`

### Icono ubicación
- fondo circular: `#EAF4FD`
- icono: `#58A8E0`

### Texto dirección
- color: `#5F5F5F`
- tamaño: `14px`
- peso: `400`
- interlineado amplio

### Chevron derecha
- color: `#B8B8B8`

## 9.8 Resultado esperado
La pestaña Unidades debe permitir lectura rápida de muchas tarjetas sin perder claridad visual ni sensación de orden.

---

# 10. Navegación inferior global

## 10.1 Estilo general
- fondo: `#F3F3F3`
- icono activo: `#58A8E0`
- texto activo: `#58A8E0`
- icono inactivo: `#8E8E8E`
- texto inactivo: `#8E8E8E`
- label size: `12px`
- peso: `500`

## 10.2 Reglas visuales
La navegación inferior debe ser:
- minimalista
- clara
- plana
- sin sombras fuertes
- sin fondos activos exagerados

El estado activo se marca solo por color azul y ligera prioridad visual.

---

# 11. Especificación de componentes reutilizables

## 11.1 Card principal
- fondo: `#FFFFFF`
- radio: `24px`
- sombra: `0 6px 18px rgba(0,0,0,0.08)`
- padding: `20px`

## 11.2 Card secundaria
- fondo: `#FFFFFF`
- radio: `20px`
- sombra: `0 4px 12px rgba(0,0,0,0.06)`
- padding: `18px`

## 11.3 Botón circular primario
- tamaño: `54px`
- fondo: `#F5F8FB` o `#58A8E0`
- icono: azul o blanco según el caso
- radio: 50%

## 11.4 Chip de estado inactiva
- fondo: `#FFFFFF`
- texto: `#3F3F3F`
- contador suave: `#F3F3F3`
- radio: `18px`
- sombra ligera

## 11.5 Chip de estado activa
- fondo: `#6D7178`
- texto: `#FFFFFF`
- contador: `#8B9097`
- radio: `18px`

## 11.6 Input / search
- fondo: `#FFFFFF`
- borde: `1px solid #EFEFEF`
- radio: `22px`
- placeholder: `#C3C3C3`
- icono: `#8E8E8E`

## 11.7 Badge de estado warning
- fondo: `#FFF3DD`
- texto: `#D89A10`
- punto: `#F89818`
- radio: `16px`

## 11.8 Toggle activo
- track: `#A9C9F2`
- thumb: `#58A8E0`

---

# 12. Reglas de fidelidad visual

## 12.1 Cosas que sí deben mantenerse
- fondo general gris muy claro
- cards blancas redondeadas y elevadas
- azul celeste como color principal
- textos negros suaves, no negros duros
- sombras discretas
- mucho espacio en blanco
- iconos simples y limpios
- navegación inferior minimalista
- jerarquía visual muy clara por bloques

## 12.2 Cosas que no deben introducirse
- gradientes
- glassmorphism
- neomorphism exagerado
- sombras pesadas
- bordes duros y oscuros
- color primario demasiado oscuro
- interfaces demasiado compactas
- elementos con saturación excesiva

## 12.3 Sensación visual correcta
El resultado debe parecer:
- moderno
- sobrio
- usable
- táctil
- ligero
- corporativo pero amable

---

# 13. Design tokens base

```json
{
  "color": {
    "primary": "#58A8E0",
    "primaryAlt": "#50A8E0",
    "primaryLight": "#EAF4FD",
    "primarySoft": "#D9EDF9",
    "background": "#F3F3F3",
    "surface": "#FFFFFF",
    "surfaceSoft": "#F7F7F7",
    "surfaceMuted": "#EEEEEE",
    "textPrimary": "#222222",
    "textSecondary": "#7A7A7A",
    "textMuted": "#A0A0A0",
    "textOnPrimary": "#FFFFFF",
    "borderLight": "#E6E6E6",
    "divider": "#ECECEC",
    "success": "#48A850",
    "successLight": "#EAF7EC",
    "warning": "#F89818",
    "warningLight": "#FFF3DD",
    "danger": "#EA5954",
    "dangerLight": "#FDEAEA",
    "purple": "#AE59D9",
    "purpleLight": "#F4EAFB",
    "iconGray": "#6F6F6F",
    "chevronGray": "#B8B8B8",
    "darkChip": "#6D7178",
    "darkChipCount": "#8B9097",
    "metaGray": "#969696"
  },
  "radius": {
    "cardLarge": 24,
    "cardMedium": 20,
    "input": 22,
    "chip": 18,
    "badge": 16,
    "buttonCircular": 54
  },
  "shadow": {
    "soft": "0 6px 18px rgba(0,0,0,0.08)",
    "light": "0 4px 12px rgba(0,0,0,0.06)"
  },
  "typography": {
    "fontFamily": "Roboto, Inter, system-ui, sans-serif",
    "displayTitle": { "size": 32, "weight": 800 },
    "screenTitle": { "size": 22, "weight": 700 },
    "sectionTitle": { "size": 18, "weight": 700 },
    "cardTitle": { "size": 16, "weight": 700 },
    "body": { "size": 15, "weight": 500 },
    "bodySecondary": { "size": 14, "weight": 400 },
    "caption": { "size": 13, "weight": 400 },
    "micro": { "size": 12, "weight": 500 }
  },
  "spacing": {
    "screenHorizontal": 24,
    "cardPadding": 20,
    "sectionGap": 24,
    "rowGap": 16,
    "iconTextGap": 12
  }
}
```

---

# 14. Prompt técnico resumido para implementación

Diseñar una app móvil Android visualmente casi idéntica a la referencia, usando fondo general `#F3F3F3`, cards blancas `#FFFFFF`, color primario `#58A8E0`, títulos `#222222`, subtítulos `#7A7A7A`, tipografía Roboto, radios grandes de `20px a 24px`, sombras suaves `0 6px 18px rgba(0,0,0,0.08)` y un sistema cromático por estados: verde para connected/live, naranja para idle, rojo para stopped y morado para seguridad. Mantener mucho espacio en blanco, iconografía simple, botones redondos, bottom navigation minimalista y componentes visualmente suaves. Replicar las cuatro pestañas: Mapa, Perfil, Reportes y Unidades respetando la jerarquía, estilo de cards, chips, tabs, badges, toggles y navegación observados en las capturas.

---

# 15. Nota final de uso
Esta guía debe usarse como base de réplica visual. Si se busca máxima fidelidad, conviene respetar primero:
1. colores
2. radios
3. pesos tipográficos
4. espaciado
5. jerarquía de bloques

Cambiar esos cinco puntos demasiado pronto hará que la app deje de parecerse a la referencia.
