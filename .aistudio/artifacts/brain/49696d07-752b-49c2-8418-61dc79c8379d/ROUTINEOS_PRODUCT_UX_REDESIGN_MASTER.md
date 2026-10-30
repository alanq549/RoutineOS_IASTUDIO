# RoutineOS — Rediseño Integral de Producto y Experiencia de Usuario (UI/UX)
**Blueprint Estratégico de Producto, Arquitectura Visual y Flujos de Interacción**

---

## 1. Diagnóstico Crítico de la Interfaz Actual

### 1.1 Síntomas de Inconsistencia Detectados
1. **Confusión de Roles entre Planning y Today:**  
   Planning se diseñó originalmente renderizando componentes derivados de Today (`TodayTimelineUiModel`), provocando que elementos futuros mostraran checkboxes de compleción y estados de telemetría en tiempo real (`0% completado hoy`).
2. **Fragmentación y Entierro de Funciones:**  
   *Mis Actividades* (el catálogo reutilizable) y *Pendientes* quedaron relegados a un menú flotante (`FAB`) que abre modales superpuestos, aislando al usuario del canvas de planificación.
3. **Sobrecarga Decorativa vs. Jerarquía de Información:**  
   Uso excesivo de bordes de múltiples colores, degradados atmosféricos y líneas de spine que compiten visualmente. En una pantalla con 8 elementos, todo grita por atención con la misma intensidad.
4. **Falta de Diferenciación Geométrica:**  
   Un bloque de gimnasio de 2 horas, una tarea puntual de 15 minutos y un recordatorio de tomar una pastilla se representan con tarjetas rectangulares de peso visual casi idéntico.
5. **Filtración de Complejidad Interna:**  
   Pantallas con nomenclatura de ingeniería (`MetadataSchema`, `SISTEMA`, `Payload`, `Identidad del molde`) que obligan al usuario a pensar en tablas de base de datos en lugar de en su vida cotidiana.

---

## 2. Matriz de Funciones (Decisiones de Producto)

| Área / Función | Decisión | Justificación y Nueva Ubicación |
| :--- | :---: | :--- |
| **Today (Superficie Operativa)** | **Rediseñar** | Se convierte en una pantalla limpia orientada al presente: **¿Qué está ocurriendo ahora?**, **¿Qué sigue?**, registro rápido y confirmación de recordatorios. |
| **Planning (Canvas Semanal)** | **Rediseñar** | Unifica la semana en un canvas continuo. Los bloques comunican duración proporcional. Incorpora panel lateral/inferior de **Pendientes** y selector de **Rutinas Habituales** sin modales ciegos. |
| **Pendientes (Ex-Backlog)** | **Combinar** | No necesita una pantalla propia. Coexiste dentro de **Planning** como una bandeja retráctil (*Inbox de intenciones*) lista para ser ubicada en el calendario. |
| **Mis Actividades Habituales** | **Combinar** | Pasa de un modal aislado a una pestaña/cajón accesible dentro de Planning (*"Mis Moldes / Rutinas"*), permitiendo estampar una rutina en cualquier día. |
| **Tareas Puntuales** | **Rediseñar** | En Planning: son acciones a calendarizar (sin checkbox, con hora y duración). En Today: son acciones ejecutables (con checkbox directo y opción *"Mover a mañana"*). |
| **Recordatorios** | **Rediseñar** | Dejan de ser tarjetas gruesas. Se convierten en **marcadores lineales puntuales** a su hora exacta (o aviso del día) con 3 salidas directas en Today: `[ Confirmar ]`, `[ +15 min ]`, `[ Omitir ]`. |
| **Alarmas y Notificaciones** | **Integrar** | Capacidad invisible de entrega. No es una entidad en la UI; es una propiedad de los recordatorios, bloques y deadlines que despierta al dispositivo. |
| **Calendario Externo** | **Simplificar** | Se proyecta como **bloques de contexto ocupado** (*sombreados neutros*). No se editan; sirven para calcular huecos libres y evitar solapamientos. |
| **Stats (Historial y Análisis)** | **Rediseñar** | Centro de aprendizaje personal: balance de tiempo planeado vs. real, tasa de cumplimiento de deadlines y base modular para extensiones (Gym, Finanzas). |
| **Account / Configuración** | **Mover a Cabecera** | Se elimina de la barra de navegación principal. Pasa a ser un icono sutil de perfil en la esquina superior de la aplicación. |

---

## 3. Nueva Arquitectura de Navegación

La barra de navegación principal se reduce a **3 pilares conceptuales absolutos**:

```text
               ┌─────────────────────────────────────────┐
               │              ROUTINEOS                  │
               │ [Perfil/Ajustes]        [Captura Rápida]│
               ├─────────────────────────────────────────┤
               │                                         │
               │               CONTENIDO                 │
               │                                         │
               ├─────────────────────────────────────────┤
               │   [ TODAY ]     [ PLANNING ]   [ STATS ]│
               └─────────────────────────────────────────┘
```

1. **TODAY (Presente):** El modo operativo. Lo que está pasando ahora, la siguiente actividad, el cronómetro de foco y los recordatorios del momento.
2. **PLANNING (Futuro):** El modo estratégico. Navegación semanal, cronograma horario de bloques, bandeja de **Pendientes** y biblioteca de **Actividades Habituales**.
3. **STATS (Pasado y Hábitos):** El modo reflexivo. Horas ejecutadas vs. planeadas, cumplimiento, desviaciones temporales y módulos personales (Físico, Pagos).

---

## 4. Flujos Clave de Experiencia

### Flujo 1: De la Intención al Calendario (Pendiente $\to$ Planning)
1. El usuario pulsa el botón universal de captura rápida `+`.
2. Escribe *"Llevar coche al taller"*.
3. Toca `[ PENDIENTE ]`. (Listo, la idea está a salvo sin forzar fecha).
4. El domingo, al abrir **Planning**, despliega la bandeja inferior **Pendientes (3)**.
5. Toca *"Llevar coche al taller"* $\to$ selecciona *"Jueves 16:00 (1 hora)"*.
6. El elemento se transforma limpiamente en un bloque de trabajo en el cronograma del jueves.

### Flujo 2: Del Compromiso Externo a la Rutina Personal
1. Google Calendar tiene registrada una *"Reunión de Equipo (14:00 – 15:00)"*.
2. RoutineOS muestra esa franja como un bloque sombreado en gris neutro: `Calendario Externo · Ocupado`.
3. Al intentar colocar una sesión de gimnasio a las 14:30, RoutineOS le avisa: *"Se solapa con tu reunión externa. ¿Quieres moverla a las 15:30?"*.
4. El usuario acepta en un toque; la vida personal se adapta al contexto sin duplicar el evento.

### Flujo 3: De la Alarma a la Ejecución en Today
1. Son las 17:45. Suena el aviso anticipado: *"Tomar medicamento en 15 min"*.
2. A las 18:00, en la pantalla de **Today**, el recordatorio aparece destacado en color ámbar.
3. El usuario tiene 3 botones inmediatos:
   - `[ Confirmar ]` $\to$ Desaparece con animación suave y queda registrado.
   - `[ +15 min ]` $\to$ Se aplaza 15 minutos sin abrir diálogos.
   - `[ Omitir ]` $\to$ Se descarta sin generar registros erróneos.

---

## 5. Política de Alarmas, Notificaciones y Posposición (Snooze Engine)

| Tipo de Elemento | Comportamiento de Alarma | Opciones de Posposición (Snooze) | Comportamiento post-reinicio |
| :--- | :--- | :--- | :--- |
| **Recordatorio puntual** | Alarma exacta + vibración a la hora configurada. | `+10 min`, `+15 min`, `+1 hora`, `Esta noche (21:00)`. | Reprogramada automáticamente por `RoutineNotificationReceiver` (`BOOT_COMPLETED`). |
| **Bloque de Actividad** | Notificación anticipada configurable (ej: 15 min antes). | `Silenciar`, `Abrir Today`. | Reprogramada si el bloque no ha iniciado. |
| **Deadline (Fecha límite)** | Aviso el día previo (20:00) y el día del vencimiento (09:00). | `Ver en Planning`, `Posponer recordatorio`. | Persiste en Room; las alarmas de umbral se recalculan. |

---

## 6. Nueva Dirección Visual y Sistema de Color Semántico

El color deja de utilizarse como elemento meramente decorativo. Cada matiz tiene un significado funcional inequívoco:

* **Base (Fondo y Superficies):**
  - Fondo: Grafito oscuro mate (`#0B0F14`).
  - Superficie 1 (Timeline y Contenedores): `#111721`.
  - Superficie 2 (Tarjetas de bloque): `#161F2C`.
  - Bordes estructurales: `#1E293B` (delgados, 1dp, baja opacidad).
* **Color Semántico por Rol:**
  - **Esmeralda / Menta (`#34D399`):** Acciones activas, actividades completadas, botón de iniciar, progreso positivo.
  - **Índigo / Lavanda (`#818CF8`):** Tareas concretas programadas y bloques de trabajo focalizado.
  - **Ámbar cálido (`#FBBF24`):** Recordatorios puntuales, avisos, alertas de tiempo cercano.
  - **Rojo coral (`#F87171`):** Deadlines vencidos, conflictos temporales directos.
  - **Gris pizarra (`#64748B`):** Compromisos externos de calendario (zonas ocupadas neutras).

---

## 7. Wireframes Textuales de las Pantallas Principales

### Pantalla 1: TODAY (Centro Operativo)
```text
┌────────────────────────────────────────────────────────┐
│ [Perfil]                     TODAY             [  +  ] │
│                      MARTES, 27 DE OCTUBRE             │
├────────────────────────────────────────────────────────┤
│ ┌────────────────────────────────────────────────────┐ │
│ │ EN CURSO · 00:42:15                                │ │
│ │ Preparar Presentación Trimestral                   │ │
│ │ 10:00 – 11:30 (1h 30m)           [ +15m ] [Pausar] │ │
│ │ [ ✓ COMPLETAR ]                                    │ │
│ └────────────────────────────────────────────────────┘ │
│                                                        │
│ SIGUIENTE                                              │
│ 12:30  Almuerzo y caminata (45 min)                    │
│                                                        │
│ RECORDATORIOS PUNTUALES DE HOY                         │
│ ┌────────────────────────────────────────────────────┐ │
│ │ 🔔 15:00  Tomar vitamina D                         │ │
│ │ [ Confirmar ]    [ +15 min ]             [ Omitir ]│ │
│ └────────────────────────────────────────────────────┘ │
│                                                        │
│ RESTO DEL DÍA                                          │
│ 16:00  Gimnasio: Tren superior (1h 15m) · 3 pasos     │
│ 18:00  Deadline: Enviar reporte mensual ⚠️             │
└────────────────────────────────────────────────────────┘
```

### Pantalla 2: PLANNING (Canvas Semanal con Bandejas)
```text
┌────────────────────────────────────────────────────────┐
│ [Perfil]                   PLANNING            [  +  ] │
│                ←  26 OCT – 01 NOV  →  [Hoy]            │
│    L        M        X        J        V        S      │
│  [26]     [27*]    [28]     [29]     [30]     [31]     │
├────────────────────────────────────────────────────────┤
│ CRONOGRAMA · MIÉRCOLES 28 OCTUBRE (3h 15m planificadas)│
│                                                        │
│ 08:30 – 09:30   Gym: Pierna · 1h 00m                   │
│                 ├── Sentadilla (25m)                   │
│                 └── Prensa y gemelos (35m)             │
│                                                        │
│ 10:00 – 11:00   Calendario Externo (Ocupado: Reunión)  │
│                                                        │
│ 11:30 – 12:15   Revisar presupuesto · 45 min           │
│                 [ Mover ]           [ Cambiar horario ]│
│                                                        │
│ 16:00           🔔 Llamar a proveedor                  │
│                                                        │
├────────────────────────────────────────────────────────┤
│ [ 📥 PENDIENTES (4) ]         [ ⚡ MIS RUTINAS (6) ]    │
└────────────────────────────────────────────────────────┘
```

---

## 8. Extensibilidad Modular sin Sobreingeniería (Gym Tracking y Finanzas)

Para integrar las ideas futuras de **seguimiento corporal en el Gym** o **gestión de gastos y fechas de pago**, no necesitamos crear nuevas bases de datos ni reinventar la rueda:

### Módulo Físico / Gym (Evolución de las métricas de ejecución)
* **Cómo encaja en el modelo:**  
  Hoy `ActivityDefinition` ya soporta `MetadataSchema` y `ActivityExecution` ya almacena valores reales.
* **UX sin sobreingeniería:**  
  En lugar de un submódulo aislado, al completar una rutina etiquetada como *"Gimnasio"*, Today despliega una tarjeta limpia:  
  *«¿Qué pesos moviste hoy?»* o selector visual del cuerpo (músculos trabajados). En **Stats**, aparece la pestaña *"Progreso Físico"* mostrando la frecuencia de grupos musculares entrenados durante el mes.

### Módulo Financiero / Pagos (Evolución de Deadlines y Recordatorios)
* **Cómo encaja en el modelo:**  
  Un pago de tarjeta o alquiler no es un concepto nuevo: **es un compromiso con Deadline y Alarma**.
* **UX sin sobreingeniería:**  
  El usuario crea un recordatorio: *"Pagar Tarjeta Visa · $2,500 antes del día 15"*.
  - En **Planning**: Se visualiza como un Deadline financiero.
  - En **Today**: Suena la alarma de pago 2 días antes y el día del vencimiento.
  - En **Stats**: Una tarjeta agregada calcula: *«Compromisos financieros del mes: $12,400 · Pagados a tiempo: 100%»*.
  El usuario obtiene control de sus finanzas sin tener que aprender una app de contabilidad bancaria.

---

## 9. Matriz de Impacto en el Dominio y Reglas Inmutables

1. **Invariante `DailyInstance`:** Sigue siendo la única unidad de proyección de la agenda. No se crean tablas duplicadas para eventos nuevos.
2. **Invariante `Deadline`:** Se mantiene ortogonal y enlazado polimórficamente a instancias, rutinas o pendientes.
3. **Invariante `BacklogItem`:** Permanece como almacenamiento de intenciones abiertas (`OPEN`) y resueltas (`RESOLVED`).
4. **Offline First:** Toda la sincronización de alarmas, ordenamiento y persistencia se ejecuta en SQLite/Room bajo Version 10.

---

## 10. Plan de Implementación Priorizado por Fases

* **Fase 1: Consolidación de Navegación y Shell Global**
  - Simplificar `AppNavHost` y `AppRoutes` a 3 destinos: `Today`, `Planning`, `Stats`.
  - Mover la configuración a la cabecera superior y unificar el botón de captura rápida global.
* **Fase 2: Rediseño del Canvas de Planning**
  - Integrar la bandeja de Pendientes y el cajón de Actividades Habituales directamente en la parte inferior de Planning.
  - Renderizar bloques con altura y badge proporcional a la duración real (`1h 30m`).
* **Fase 3: Rediseño del Centro de Mando Today**
  - Reemplazar la lista plana de tareas por la tarjeta activa en curso (hero card con cronómetro) y la lista de hitos inmediatos.
  - Botones directos de 1 toque para recordatorios (`Confirmar`, `+15 min`, `Omitir`).
* **Fase 4: Consolidación de Stats y Módulos Extensibles**
  - Gráficos limpios de Horas Planificadas vs. Ejecutadas.
  - Secciones preparadas para métricas de hábitos físicos y control de compromisos de pago.
