# DESIGN SYSTEM SKILL — RoutineOS (Technical Premium Narrative)

Este documento codifica las reglas oficiales de diseño, arquitectura visual y tokens de componentes para **RoutineOS** basadas en las especificaciones de **Stitch V3** y la librería `core/designsystem`. 

Cualquier pantalla, componente o diálogo en la aplicación **debe** cumplir con estas directrices para garantizar una experiencia cohesiva, profesional y de alta fidelidad técnica.

---

## 1. Identidad Visual y Filosofía de Diseño

- **Narrativa:** *Technical Premium Narrative* — Minimalismo de alta densidad, estética de instrumentación (HUD), superficies oscuras mate y vidrio selectivo (*glassmorphism* por tonal layering).
- **Tema:** Modo Oscuro exclusivo (Dark HUD). No existen fondos blancos ni componentes estándar de Android sin estilizar.
- **Micro-interacciones:** Bordes hairline de 1px, insignias tipo píldora (*pills*) y tipografía técnica para datos.
- **Idioma:** **100% Español** (`Locale("es", "ES")`). Nunca mezclar inglés con español (ejemplo: prohibido "Thursday 29 de October" o "PLANNED"; usar "Jueves 29 de octubre" y "PROGRAMADA").

---

## 2. Paleta de Colores y Tokens Semánticos (`RoutineColors`)

Todos los colores deben consumirse exclusivamente a través de `RoutineTheme.colors`. Queda **estrictamente prohibido** utilizar `Color.White`, `Color.Black` o códigos hexadecimales directos dentro de los composables de UI.

| Token | Hex / Valor | Rol Funcional |
| :--- | :--- | :--- |
| `colors.background` | `#0A0D12` / `#0F141A` | Lienzo base de la aplicación. |
| `colors.surface1` | `#12161D` / `#171C22` | Contenedor principal (`RoutineCard`, sheets, barras). |
| `colors.surface2` | `#171C24` / `#1B2026` | Tarjetas secundarias, inputs, botones de superficie activa. |
| `colors.surface3` | `#252A31` / `#30353C` | Elementos deshabilitados, capas elevadas y hovers. |
| `colors.border` | `#232A34` / `#1E293B` | Bordes estructurales hairline (1.dp con alpha 0.35f – 0.5f). |
| `colors.primary` | `#5AF0B3` / `#00E599` | Verde esmeralda de señal: acento principal, botones primarios, progreso positivo. |
| `colors.secondary` | `#A4C8FF` | Azul cielo: tareas concretas, bloques de foco. |
| `colors.tertiary` | `#DBD1FF` | Lavanda suave: actividades espontáneas, extensiones. |
| `colors.warning` | `#FBBF24` | Ámbar: recordatorios puntuales, avisos temporales. |
| `colors.error` | `#FFB4AB` / `#F87171` | Rojo coral: deadlines vencidos, conflictos, acciones de eliminación. |
| `colors.onSurface` | `#DEE3EB` | Texto principal de máxima legibilidad. |
| `colors.onSurfaceVariant` | `#85948B` / `#94A3B8` | Etiquetas secundarias, metadatos, iconos inactivos. |

---

## 3. Sistema Tipográfico (`RoutineTypography`)

La tipografía sigue un estándar dual riguroso:

1. **Inter:** Familia sans-serif principal para jerarquía de interfaz, títulos y cuerpos de lectura.
2. **JetBrains Mono / Monospace:** Familia monoespaciada para telemetría, cronómetros, porcentajes, horas y duraciones.

| Token | Familia / Peso | Tamaño / Leading | Uso Obligatorio |
| :--- | :--- | :--- | :--- |
| `displayLarge` | Inter / 700 (Bold) | 28sp – 32sp | Título de la pantalla principal (Today, Planning, Stats, Cuenta). |
| `headlineMedium` | Inter / 600 (SemiBold) | 20sp | Títulos de tarjetas principales, cabeceras de diálogo. |
| `labelCaps` | Inter / 700 (Bold) | 11sp – 12sp (Tracking: 1.5sp–2sp) | Eyebrows / Kickers de sección en MAYÚSCULAS (ej: `RITMO SEMANAL`). |
| `bodyBase` | Inter / 400 (Regular) | 14sp – 16sp | Texto descriptivo, nombres de tareas y actividades. |
| `dataHero` | Monospace / 700 (Bold) | 28sp – 36sp | Números principales de impacto (ej: `76%`, `00:42:15`). |
| `dataLarge` | Monospace / 600 | 18sp – 20sp | Valores de tarjetas de resumen (días activos, contadores). |
| `dataMedium` | Monospace / 500 | 13sp – 14sp | Horas (`08:30 – 09:30`), duraciones (`45 min`), insignias numéricas. |
| `dataSmall` | Monospace / 400 | 11sp – 12sp | Metadatos secundarios en listas compactas. |

---

## 4. Formas, Radios y Geometría (`RoutineShapes` & `RoutineDimensions`)

- **Cards y Contenedores Grandes:** `RoutineTheme.shapes.medium` (`16.dp`).
- **Botones Interactivos y Chips Cuadrados:** `RoutineTheme.shapes.small` (`8.dp`).
- **Modales y BottomSheets:** `RoutineTheme.shapes.large` (`24.dp` esquinas superiores).
- **Insignias, Badges y Filtros:** `RoutineTheme.shapes.pill` (`999.dp` radio completo).
- **Touch Target Mínimo:** Todos los elementos interactivos deben cumplir con un área mínima de **48.dp × 48.dp**.
- **Líneas Hairline:** `1.dp` dibujadas con `Modifier.border(...)` o `drawBehind { drawLine(...) }`.

---

## 5. Anatomía Estándar de Pantallas y Componentes

### 5.1 Cabeceras de Pantalla (Screen Headers)
Toda pantalla principal (Today, Planning, Stats, Account) debe tener un header coherente:
```kotlin
Box(
    modifier = Modifier
        .fillMaxWidth()
        .background(RoutineTheme.colors.background)
        .drawBehind {
            drawLine(
                color = RoutineTheme.colors.border.copy(alpha = 0.3f),
                start = Offset(0f, size.height),
                end = Offset(size.width, size.height),
                strokeWidth = 1.dp.toPx()
            )
        }
        .statusBarsPadding()
        .padding(horizontal = RoutineTheme.spacing.marginMobile)
        .padding(top = RoutineTheme.spacing.md, bottom = 14.dp)
) {
    // 1. Eyebrow en labelCaps (Verde Esmeralda o SurfaceVariant)
    // 2. Título principal en displayLarge (onSurface)
    // 3. Acción lateral (Icono de perfil o botón de regreso en pastilla)
}
```

### 5.2 Tarjetas de Contenedor (`RoutineCard`)
Las tarjetas nunca deben tener sombras de elevación opacas; utilizan *tonal layering*:
- Fondo: `RoutineTheme.colors.surface1`
- Borde: `1.dp` en `RoutineTheme.colors.border.copy(alpha = 0.35f)`
- Radio: `RoutineTheme.shapes.medium` (`16.dp`)
- Padding interno estándar: `RoutineTheme.spacing.lg` (`16.dp` a `20.dp`)

### 5.3 Badges e Indicadores de Estado (Pill Badges)
```kotlin
Box(
    modifier = Modifier
        .background(color.copy(alpha = 0.12f), RoutineTheme.shapes.pill)
        .border(1.dp, color.copy(alpha = 0.35f), RoutineTheme.shapes.pill)
        .padding(horizontal = 8.dp, vertical = 3.dp)
) {
    Text(
        text = label.uppercase(),
        style = RoutineTheme.typography.dataMedium.copy(fontSize = 10.sp, fontWeight = FontWeight.Bold),
        color = color
    )
}
```

---

## 6. Estado de Alineación por Módulo

| Módulo | Estado Visual | Reglas Específicas de Cumplimiento |
| :--- | :---: | :--- |
| **Today** | **ALINEADO** | Timeline continuo, tarjeta activa de foco, recordatorios puntuales con 3 salidas directas (`Confirmar`, `+15 min`, `Omitir`). |
| **Planning** | **ALINEADO** | Selector semanal con día actual destacado, bloques horarios proporcionales, dock retráctil para `Pendientes` e `Biblioteca de Rutinas`. |
| **Stats** | **EN PROCESO** | Selector de periodo en pastilla (`Día`, `Semana`, `Mes`, `Año`), 4 tarjetas de telemetría HUD, Ritmo Semanal con 7 columnas interactivas, desglose por día y adherencia por áreas sin componentes desalineados. |
| **Rutinas / Catálogo** | **EN PROCESO** | Mismo diseño en pantalla completa y en BottomSheet. Botón destacado `+ RUTINA`, chips de áreas de vida, tarjeta con chips de pasos/minutos y menú `⋮` con opción `Eliminar`. |
| **Account** | **ALINEADO** | Botón de regreso en píldora, avatar circular, bloques de configuración agrupados en `RoutineCard` con divisores hairline. |

---

## 7. Protocolo de Verificación Pre-Entrega

Antes de dar por finalizado cualquier cambio visual:
1. **Inspección de Hardcodes:** Verificar con `grep` que no existan `Color(0x...)`, `Color.White`, `Color.Black` ni radios crudos como `RoundedCornerShape(24.dp)` en archivos de UI.
2. **Inspección de Idioma:** Verificar que no existan cadenas o formateadores en inglés (ej: `Locale("es", "ES")` obligatorio en todos los `DateTimeFormatter`).
3. **Validación de Compilación:** Ejecutar `compile_applet`.
4. **Validación de Tests:** Ejecutar `gradle :app:testDebugUnitTest`.
