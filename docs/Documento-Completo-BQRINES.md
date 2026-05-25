# BQRINES

**Autores:**
Valeria Caro Gallego
Juan Sebastián Mejía
Daniel Alexander Montoya

**Profesora:**
**Alexandra Guerrero**

Institución Universitaria Pascual Bravo
Facultad de Ingeniería — Ingeniería de Software
Medellín - Antioquia 2026

---

> ## 📋 Leyenda de cambios
>
> Este documento es la versión actualizada del análisis de requisitos, contrastada con el sistema
> ya implementado. Los marcadores indican qué cambió respecto a la versión original:
>
> | Marcador | Significado |
> |----------|-------------|
> | 🔄 **\[MODIFICADO\]** | Contenido que existía en el documento original pero fue ajustado en la implementación |
> | 🆕 **\[NUEVO\]** | Sección, caso de uso o requisito completamente nuevo, no contemplado en el documento original |
>
> El texto sin marcador es el contenido original sin cambios.

---

## 1. Contexto del sistema

### 1.1. Descripción del problema empresarial

BQRINES es una empresa dedicada a la gestión y venta de vehículos y repuestos que actualmente opera bajo procesos completamente manuales. La administración de sus registros se lleva a cabo mediante hojas de cálculo, lo que representa una solución frágil en términos de seguridad, confiabilidad y escalabilidad. Este enfoque limita la capacidad de la empresa para consultar y visualizar información de manera ágil e intuitiva, dificultando la toma de decisiones oportuna por parte de sus colaboradores.

La necesidad principal de BQRINES es contar con un sistema que centralice y automatice la gestión de sus operaciones comerciales, particularmente el registro y control de vehículos, repuestos, usuarios y ventas. El sistema debe permitir llevar un inventario actualizado en tiempo real, de manera que cuando una unidad o repuesto se agote, el sistema notifica automáticamente la necesidad de reabastecimiento o inactive temporalmente dicho ítem en la base de datos. Así mismo, se requiere que el asesor comercial tenga la capacidad de registrar y gestionar los repuestos disponibles dentro de la plataforma.

Se espera que la implementación de esta solución permita a la empresa pasar de una operación manual y dispersa a un modelo digitalizado, trazable y auditado, con un historial completo de cada gestión realizada. El impacto esperado se traduce en mayor seguridad sobre los datos, mejor control del inventario, y una toma de decisiones más rápida y fundamentada para los diferentes roles dentro de la organización.

---

### 1.2. Actores Principales

| Actor | Descripción | Tipo |
| :---- | :---- | :---- |
| Gerente | Responsable de la gestión estratégica, registro de inventario y toma de decisiones administrativas. | Interno |
| Asesor Comercial | Encargado de registrar usuarios, asociarlos a vehículos y gestionar el proceso comercial de venta. También gestiona los repuestos solicitados en casos de servicio y genera la factura de cierre. | Interno |
| Servicio Técnico | 🔄 **[MODIFICADO]** Gestiona casos de servicio técnico: registra clientes y sus vehículos, crea casos, solicita repuestos (incluyendo los sin stock), registra diagnóstico, notas técnicas y precio de mano de obra. No tiene acceso al módulo de ventas directo. | Interno |

> 🔄 **[MODIFICADO]** La descripción original de Servicio Técnico indicaba "ejecuta la venta de repuestos". En la implementación este rol no accede al módulo de ventas; su función es gestionar los casos de servicio técnico de forma completa hasta marcarlos como LISTO, momento en el que el Asesor Comercial genera la factura.

---

### 1.3. Restricciones relevantes

| Tipo de Restricción | Descripción | Impacto arquitectónico |
| ----- | ----- | ----- |
| **Técnica** | 🔄 **[MODIFICADO]** El sistema se desarrolló como **aplicación web** (Spring Boot MVC) accesible desde el navegador en la red interna de la empresa, en lugar de una aplicación de escritorio instalable. Esta decisión permite acceso multi-equipo sin instalación y simplifica el proceso de actualización. | La organización opera el sistema desde cualquier equipo con navegador en la red interna. No requiere instalación por equipo. |
| **Técnica** | El sistema debe ser capaz de enviar notificaciones automáticas cuando el inventario de un vehículo o repuesto se agote. | Requiere alertas o servicio de notificación integrado en la lógica de negocio. |
| **Organizacional** | Los roles dentro del sistema (Gerente, Asesor Comercial, Servicio Técnico) deben estar claramente diferenciados con accesos y permisos distintos. | Exige autenticación y control de acceso basado en roles. |
| **Normativa** | Los registros de ventas deben conservarse y ser auditables para efectos contables y comerciales de la empresa. | Requiere que el sistema mantenga un historial inmutable de transacciones y permita la generación de reportes con trazabilidad. |

> 🔄 **[MODIFICADO]** La restricción original RF-12 especificaba "aplicación de escritorio instalable". La implementación optó por una arquitectura web por las siguientes razones: mayor facilidad de acceso desde múltiples equipos sin instalación, actualizaciones centralizadas en el servidor, y mejor integración con la base de datos PostgreSQL centralizada.

---

## 2. Identificación de requisitos

### 2.1. Casos de Uso (Análisis de Alto Nivel)

*Figura 1: Diagrama de Casos de Uso del Sistema BQRINES.*

---

| CU-01: Autentificar Usuario | |
| ----- | :---- |
| **Actor principal** | Gerente, Asesor Comercial, Servicio Técnico. |
| **Precondición** | El usuario debe estar registrado en la base de datos. |
| **Flujo Básico** | 1. El usuario ingresa su nombre de usuario y contraseña. 2. El sistema valida las credenciales contra la base de datos. 3. El sistema identifica el rol del usuario. 4. El sistema redirige al Dashboard correspondiente según el rol. |
| **Postcondición** | El usuario tiene acceso a las funciones permitidas por su rol. |

---

| CU-02: Gestionar Usuarios (Solo Gerente) | |
| ----- | :---- |
| **Actor principal** | Gerente. |
| **Precondición** | Gerente autenticado en el sistema. |
| **Flujo Básico** | 1. El Gerente accede al módulo de gestión de personal. 2. El Gerente crea o edita el perfil de un usuario. 3. El Gerente define los permisos RBAC correspondientes al rol asignado. 4. El sistema confirma y persiste los cambios en la base de datos. |
| **Postcondición** | El perfil del usuario queda creado o actualizado con los permisos RBAC asignados. |

---

| CU-03: Gestionar Vehículos y Repuestos (Inventario) | |
| ----- | :---- |
| **Actor principal** | Gerente, Asesor Comercial. |
| **Precondición** | Usuario con permisos de administración autenticado en el sistema. |
| **Flujo Básico** | 1. El Gerente accede al módulo de inventario. 2. El Gerente registra o edita un vehículo o repuesto con su ficha técnica. 3. El sistema ajusta el stock y configura los umbrales de alerta de agotamiento. 4. El sistema confirma el registro y actualiza el catálogo en tiempo real. |
| **Postcondición** | El ítem queda registrado en el inventario con su stock y ficha técnica actualizados. |

---

| CU-04: Registrar Venta y Salida de Almacén | |
| ----- | :---- |
| **Actor principal** | 🔄 **[MODIFICADO]** Asesor Comercial. *(El Servicio Técnico no participa en ventas directas; sus ventas de repuestos se procesan al cerrar un Caso de Servicio — ver CU-08.)* |
| **Precondición** | El usuario debe estar autenticado y el producto debe tener stock disponible (stock > 0). |
| **Flujo Básico** | 1. El Asesor selecciona el producto solicitado por el cliente. 2. El sistema verifica la disponibilidad inmediata del stock. 3. El Asesor puede agregar múltiples productos al carrito y cobros extra (mano de obra) sin descuento de stock. 4. El Asesor ingresa los datos del cliente, incluyendo nombre y número de documento (CC/NIT). 5. El sistema genera el comprobante de venta con todas las líneas. 6. El sistema descuenta el stock de cada unidad vendida automáticamente. |
| **Postcondición** | La venta queda registrada en el historial auditable con el email del asesor y el stock se actualiza en tiempo real. |

---

| CU-05: Generar Reportes y Alertas | |
| ----- | :---- |
| **Actor principal** | Gerente. |
| **Precondición** | Existencia de datos transaccionales registrados en el sistema. |
| **Flujo Básico** | 1. El sistema monitorea el stock constantemente. 2. Si un ítem llega al nivel mínimo (≤ 3 unidades), el sistema muestra una alerta visual en el Dashboard y en la sección de Reportes. 3. El Gerente accede a `/reports` y filtra ventas por rango de fechas. 4. El sistema consolida la información y la presenta con detalle por línea de venta. 5. 🔄 **[MODIFICADO]** El Gerente puede exportar el reporte filtrado a **PDF** o **Excel (.xlsx)** con un click; los botones de exportación respetan el filtro de fechas activo. |
| **Postcondición** | El Gerente obtiene la información necesaria para la toma de decisiones estratégicas, en pantalla o en archivo descargable. |

---

> ### 🆕 [NUEVO] CU-06: Gestionar Clientes de Servicio
>
> | CU-06: Gestionar Clientes de Servicio | |
> | ----- | :---- |
> | **Actor principal** | Servicio Técnico, Gerente. |
> | **Precondición** | Usuario autenticado con rol SERVICIO_TECNICO o GERENTE. |
> | **Flujo Básico** | 1. El técnico accede al módulo de Clientes. 2. Busca al cliente por nombre o ingresa los datos (nombre, documento, teléfono, email). 3. El sistema registra al cliente. 4. El técnico puede editar los datos del cliente en cualquier momento. 5. El sistema lista todos los vehículos asociados al cliente. |
> | **Postcondición** | El cliente queda registrado y disponible para asociar vehículos y crear casos de servicio. |

---

> ### 🆕 [NUEVO] CU-07: Gestionar Vehículos de Servicio
>
> | CU-07: Gestionar Vehículos de Servicio | |
> | ----- | :---- |
> | **Actor principal** | Servicio Técnico, Gerente. |
> | **Precondición** | Cliente registrado en el sistema. |
> | **Flujo Básico** | 1. El técnico registra el vehículo del cliente indicando placa (identificador único), marca, modelo, año y color. 2. El sistema valida que la placa no esté duplicada. 3. La placa queda vinculada al cliente. 4. Para acceder al historial de un vehículo, basta con buscar por su placa. 5. Un mismo cliente puede tener múltiples vehículos registrados. |
> | **Postcondición** | El vehículo queda asociado al cliente y disponible para crear casos de servicio. |

---

> ### 🆕 [NUEVO] CU-08: Gestionar Casos de Servicio
>
> | CU-08: Gestionar Casos de Servicio | |
> | ----- | :---- |
> | **Actor principal** | Servicio Técnico (crea y actualiza), Asesor Comercial (gestiona repuestos y cierra), Gerente (visualiza). |
> | **Precondición** | Vehículo de servicio registrado. |
> | **Flujo Básico** | 1. El técnico busca el vehículo por placa y crea un Caso describiendo el problema. 2. El caso inicia en estado **RECIBIDO** y avanza por los estados: EN_DIAGNOSTICO → ESPERANDO_REPUESTOS → EN_REPARACION → **LISTO**. 3. El técnico agrega repuestos al caso desde el catálogo completo (incluyendo ítems sin stock disponible). 4. El técnico registra sus notas de diagnóstico, la descripción de la mano de obra y su precio. 5. El Asesor Comercial revisa los repuestos solicitados y los marca como DISPONIBLE o SIN_STOCK. 6. Cuando el caso está en estado LISTO, el Asesor puede cerrarlo: accede al formulario de factura con los repuestos aprobados y la mano de obra ya pre-cargados, puede agregar cobros adicionales y confirma. 7. El sistema genera una Venta, descuenta stock de los repuestos disponibles, cierra el caso con estado ENTREGADO y vincula la factura. 8. El Gerente puede ver todos los casos en cualquier momento con su estado, repuestos y notas. |
> | **Postcondición** | El caso queda cerrado, la factura generada y el stock de repuestos descontado. |

---

> ### 🆕 [NUEVO] CU-09: Cambiar Contraseña (Self-Service)
>
> | CU-09: Cambiar Contraseña | |
> | ----- | :---- |
> | **Actor principal** | Gerente, Asesor Comercial, Servicio Técnico. |
> | **Precondición** | Usuario autenticado en el sistema. |
> | **Flujo Básico** | 1. El usuario accede a la opción "Contraseña" en la barra de navegación. 2. Ingresa su contraseña actual y la nueva contraseña (con confirmación). 3. El sistema verifica que la contraseña actual sea correcta. 4. Si es válida, actualiza la contraseña con cifrado BCrypt. 5. Si no es válida, muestra un mensaje de error claro. 6. Adicionalmente, el GERENTE puede resetear la contraseña de cualquier usuario desde el módulo de gestión de usuarios. |
> | **Postcondición** | La contraseña queda actualizada. El sistema no implementa recuperación por email (sistema interno sin SMTP). |

---

> ### 🆕 [NUEVO] CU-10: Gestionar Solicitudes Internas
>
> | CU-10: Gestionar Solicitudes Internas | |
> | ----- | :---- |
> | **Actor principal** | Servicio Técnico (crea), Gerente (gestiona). |
> | **Precondición** | Usuario autenticado con rol correspondiente. |
> | **Flujo Básico** | 1. El técnico crea una solicitud interna con título y descripción. 2. La solicitud inicia en estado ABIERTA. 3. El Gerente ve todas las solicitudes y puede cambiar el estado (ABIERTA / EN_PROCESO / CERRADA) y agregar observaciones. 4. El técnico puede ver el estado actualizado y las notas del Gerente en sus solicitudes. |
> | **Postcondición** | La solicitud queda registrada como canal de comunicación interno entre el técnico y la gerencia. |

---

### 2.2. Story Map y Casos de Uso

- **CU-01 (Seguridad):** Como Gerente, quiero gestionar perfiles para controlar el acceso a la información.
- **CU-03 (Inventario):** Como Gerente, quiero registrar vehículos para mantener el catálogo actualizado.
- **CU-04 (Ventas):** Como Asesor, quiero registrar ventas con múltiples productos para formalizar transacciones y actualizar stock.
- **CU-05 (Reportes):** Como Gerente, quiero exportar reportes a PDF y Excel para tomar decisiones con información consolidada.
- **CU-08 (Servicio Técnico):** 🆕 Como Técnico, quiero gestionar casos de servicio para registrar el trabajo realizado y coordinar repuestos con el asesor.

---

### 2.3. Tabla de Requisitos Funcionales

| ID | Requisito Funcional | CU | Impacto Arquitectónico |
| :---- | :---- | :---- | :---- |
| RF-01 | Autenticación de usuarios | CU-01 | Alto (Módulo de seguridad) |
| RF-02 | Administrar usuarios (CRUD) | CU-01 | Medio (Persistencia de perfiles) |
| RF-03 | Control por roles (RBAC) | CU-01 | Alto (Validación en servidor) |
| RF-04 | Registro de vehículos de inventario. 🔄 **[MODIFICADO]** Incluye campos `placa` (único, auto-mayúsculas) y `color`. Detección de duplicados por placa. Borrado lógico (soft-delete con `deletedAt`). Timestamps `createdAt`/`updatedAt`. | CU-03 | Medio (Capa de datos) |
| RF-05 | Registro de repuestos. 🔄 **[MODIFICADO]** Incluye campo `referencia` única. Detección de duplicados. Borrado lógico (soft-delete). Timestamps `createdAt`/`updatedAt`. | CU-03 | Medio (Capa de datos) |
| RF-06 | Consulta de inventario en tiempo real con badges de estado: OK (> 3 uds.), bajo (1–3 uds.), agotado (0 uds.) | CU-05 | Alto (Consistencia de datos) |
| RF-07 | Registro de ventas. 🔄 **[MODIFICADO]** Soporta múltiples productos en un carrito; cobros extra (ej. mano de obra) sin descuento de stock; campo `buyerDocument` (CC/NIT del comprador); campo `registeredBy` (email del asesor como trazabilidad). | CU-04 | Alto (Transaccionalidad) |
| RF-08 | Descuento automático de stock al confirmar venta | CU-04 | Alto (Reglas de negocio) |
| RF-09 | Desactivación automática por stock 0 | CU-03 | Alto (Automatización lógica) |
| RF-10 | Alertas visuales en dashboard y reportes cuando stock ≤ 3 unidades | CU-05 | Alto (Servicio de notificación) |
| RF-11 | Historial auditable de acciones. 🔄 **[MODIFICADO]** Implementado como tabla `audit_logs` con: tipo de entidad, ID, descripción, acción (CREAR/EDITAR/ELIMINAR), diff de campos cambiados (ej. "Stock: 5 → 2 \| Precio: $8.000 → $7.500"), email del ejecutor o "sistema" para descuentos por venta. Vista en `/history` solo para GERENTE. | CU-10 | Alto (Log inmutable) |
| RF-12 | 🔄 **[MODIFICADO]** Aplicación **web** (Spring Boot MVC) accesible desde navegador en red interna, en lugar de escritorio instalable. Decisión arquitectónica adoptada para facilitar acceso multi-equipo sin instalación. | — | Alto (Arquitectura distribuida) |
| 🆕 RF-13 | Gestión de clientes de servicio (CRUD): nombre, documento, teléfono, email. Un cliente puede tener múltiples vehículos de servicio. | CU-06 | Medio (Capa de datos) |
| 🆕 RF-14 | Gestión de vehículos de servicio identificados por placa única. Vinculación a cliente. Búsqueda por placa. | CU-07 | Medio (Capa de datos) |
| 🆕 RF-15 | Creación y seguimiento de casos de servicio con 6 estados: RECIBIDO → EN_DIAGNOSTICO → ESPERANDO_REPUESTOS → EN_REPARACION → LISTO → ENTREGADO. | CU-08 | Alto (Flujo de negocio) |
| 🆕 RF-16 | Solicitud de repuestos dentro de casos, incluyendo ítems con stock 0. El asesor marca cada repuesto como DISPONIBLE o SIN_STOCK. | CU-08 | Medio (Integración inventario) |
| 🆕 RF-17 | Cobro de mano de obra con precio y descripción definidos por el técnico; pre-cargado en la factura de cierre del caso. El asesor puede editar y agregar cargos adicionales antes de confirmar. | CU-08 | Alto (Transaccionalidad) |
| 🆕 RF-18 | Cambio de contraseña self-service para todos los roles. Reset por Gerente desde gestión de usuarios. Sin recuperación por email (sistema interno). | CU-09 | Bajo (Seguridad de acceso) |
| 🆕 RF-19 | Exportación de reportes de ventas a PDF y Excel respetando el filtro de fechas activo. | CU-05 | Medio (Generación de documentos) |
| 🆕 RF-20 | Solicitudes internas de SERVICIO_TECNICO al GERENTE con estados ABIERTA/EN_PROCESO/CERRADA y observaciones de respuesta. | CU-10 | Bajo (Comunicación interna) |

---

### 2.4. Tabla de Requisitos No Funcionales

| ID | Requisito No Funcional | Métrica | Justificación |
| :---- | :---- | :---- | :---- |
| RNF-01 | 🔄 **[MODIFICADO]** Compatibilidad con navegadores modernos (Chrome, Firefox, Edge) en Windows 10/11 | 100% de funcionalidades accesibles desde navegador | Entorno web local en red interna |
| RNF-02 | Seguridad validada en backend | 0 accesos indebidos | No confiar solo en el cliente |
| RNF-03 | Cifrado de contraseñas | Hasheo 100% de credenciales con BCrypt | Protección física de equipos |
| RNF-04 | Auditoría completa | 100% trazabilidad de ventas e inventario | Obligación normativa |
| RNF-05 | Consistencia de inventario | 0 inconsistencias de stock | Alertas críticas de agotamiento |
| RNF-06 | Actualización de versiones | Proceso <= 5 minutos (redeploy en servidor) | Mantenibilidad del software |
| RNF-07 | Gestión de reconexión | Recuperación <= 30 seg | Dependencia de red interna |

---

## 3. Definición y priorización de atributos de calidad

- **Consistencia (Crítico):** Fundamental para evitar errores económicos y pérdidas de stock real frente al sistema.
- **Seguridad (Crítico):** Gestión de perfiles y protección de datos comerciales confidenciales.
- **Confiabilidad (Alto):** Asegura que las notificaciones de stock funcionen para evitar rupturas de cadena de suministro.

> 🆕 **[NUEVO]** **Trazabilidad (Alto):** El módulo de auditoría (`audit_logs`) garantiza que cada acción sobre el inventario quede registrada con usuario, timestamp y detalle del cambio, cumpliendo los requisitos normativos de la empresa.

---

## 4. Justificación de la Arquitectura Propuesta

Se ha seleccionado una **Arquitectura de N-Capas (N-Tier)** implementada como **aplicación web** con Spring Boot MVC. Esta decisión permite una separación de responsabilidades: la Capa de Lógica centraliza las reglas de negocio (validación de stock, alertas, casos de servicio), mientras que la Capa de Datos asegura transacciones **ACID** para mantener la consistencia.

> 🔄 **[MODIFICADO]** El documento original especificaba una arquitectura de escritorio. La implementación adoptó Spring Boot MVC (aplicación web) por las siguientes razones técnicas y organizacionales:
> - **Acceso multi-equipo** sin instalación por dispositivo
> - **Actualizaciones centralizadas** en el servidor sin intervención en cada equipo
> - **Integración directa** con PostgreSQL en el servidor de la empresa
> - **Roles y permisos** gestionados por Spring Security en el servidor, sin riesgo de manipulación del cliente
>
> La aplicación opera en la red interna de BQRINES (localhost:8080 o IP del servidor), manteniendo el carácter de sistema centralizado descrito en el análisis original.

---

## 5. Identificación preliminar de riesgos

| ID | Riesgo | Prob. | Impacto | Mitigación |
| :---- | :---- | :---- | :---- | ----- |
| R-01 | Sobreventa por concurrencia | Media | Alto | Transacciones `@Transactional` con rollback automático; `InsufficientStockException` revierte la operación. |
| R-02 | Falla en notificaciones | Baja | Alto | Consulta directa a BD en cada solicitud; sin cola asíncrona que pueda fallar. |
| R-03 | Acceso no autorizado | Baja | Crítico | `@PreAuthorize` por método en todos los controladores; validación en Spring Security. |
| R-04 | Bajo rendimiento en búsqueda | Media | Medio | Indexación de tablas por placa y referencia; soft-delete con `@SQLRestriction` evita escaneos completos. |
| R-05 | Pérdida de datos técnicos | Baja | Alto | Soft-delete: ningún registro se elimina físicamente; backups de PostgreSQL recomendados. |
| 🆕 R-06 | Cierre de caso con stock insuficiente | Media | Alto | Al cerrar un caso, `SellService.registerSell()` verifica stock y lanza `InsufficientStockException` con rollback si no hay disponibilidad. |

---

## 6. Bibliografía y Uso de IA

**En cumplimiento con las normas éticas y académicas de la institución, se declara que para la elaboración de este documento de análisis se contó con el apoyo de herramientas de Inteligencia Artificial Generativa (Gemini y Claude, desarrollado por Anthropic).**

**Alcance del uso:**

- **Estructuración:** Apoyo en la organización del Story Map y la definición de la jerarquía de requisitos.
- **Redacción y Estilo:** Mejora de la coherencia en la descripción de los casos de uso y ajuste del formato de las Historias de Usuario al estándar "As a... I want... So that...".
- **Análisis Arquitectónico:** Guía conceptual para la justificación de la arquitectura de N-capas basada en los atributos de calidad.
- **Edición y complemento documental (Claude – Anthropic):** Integración de las tablas detalladas de casos de uso (Actor Principal, Precondición, Flujo Básico y Postcondición) al documento consolidado final.
- 🆕 **Actualización de requisitos implementados (Claude – Anthropic):** Contraste entre el documento original y el sistema implementado; integración de los nuevos casos de uso CU-06 a CU-10 y requisitos funcionales RF-13 a RF-20; marcado visual de cambios y adiciones.

**Los autores asumen la responsabilidad total sobre el contenido, validando que todas las decisiones de diseño y requisitos técnicos aquí expuestos corresponden a las necesidades reales del negocio BQRINES.**

### 6.2 Bibliografía

- Bass, L., Clements, P., & Kazman, R. (2021). *Software Architecture in Practice* (4th ed.). Addison-Wesley Professional. ISBN: 978-0136886099.
- Fowler, M. (2002). *Patterns of Enterprise Application Architecture*. Addison-Wesley Professional. ISBN: 978-0321127426.
- Richards, M., & Ford, N. (2020). *Fundamentals of Software Architecture: An Engineering Approach*. O'Reilly Media. ISBN: 978-1492043454.
- Sommerville, I. (2015). *Software Engineering* (10th ed.). Pearson.
- Newman, S. (2021). *Building Microservices: Designing Fine-Grained Systems* (2nd ed.). O'Reilly Media. ISBN: 978-1492050360.
- Anthropic. (2025). *Claude* (Versión Sonnet 4.6) [Herramienta de IA generativa]. https://www.anthropic.com
