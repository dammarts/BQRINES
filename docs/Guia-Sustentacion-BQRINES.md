# Guía de Sustentación — BQRINES
### Pitch de 15 minutos · 3 presentadores

---

## Leyenda de roles

| Símbolo | Presentador |
|---------|-------------|
| 🔵 **P1** | Presentador 1 — Visión general y arquitectura |
| 🟢 **P2** | Presentador 2 — Módulos de inventario y ventas |
| 🟠 **P3** | Presentador 3 — Casos de servicio y funciones avanzadas |

---

## Estructura de tiempo

```
┌─────────────────────────────────────────────────────────────────┐
│  00:00 – 02:00  │ P1 │ ¿Qué es BQRINES? Problema y solución     │
│  02:00 – 04:30  │ P1 │ Arquitectura técnica y roles del sistema  │
│  04:30 – 08:00  │ P2 │ Inventario + Ventas + Reportes            │
│  08:00 – 12:30  │ P3 │ Casos de servicio (flujo completo)        │
│  12:30 – 14:00  │ P3 │ Seguridad, auditoría y decisiones técnicas│
│  14:00 – 15:00  │ P1 │ Cierre y conclusión                       │
└─────────────────────────────────────────────────────────────────┘
```

---

## 🔵 P1 — Bloque 1: ¿Qué es BQRINES? (0:00 – 2:00)

### Guión sugerido

> "BQRINES es un sistema de gestión para un taller de vehículos.
> El problema que resuelve es que antes todo se manejaba a mano o en hojas de cálculo:
> el inventario no sabía cuándo un repuesto se acababa, las ventas no quedaban registradas
> con trazabilidad, y no había forma de hacer seguimiento a un vehículo que entraba a servicio.
>
> Nosotros construimos una aplicación web que digitaliza todo ese proceso:
> inventario, ventas, facturación y un módulo completo de casos de servicio técnico."

### Diapositiva sugerida — El problema vs. la solución

```
ANTES (Problema)                    DESPUÉS (BQRINES)
──────────────────────────────────────────────────────
📋 Hojas de Excel o papel     →    💻 Sistema web centralizado
❌ Sin saber si hay stock      →    ✅ Alertas automáticas de stock bajo
❓ "¿Quién vendió esto?"       →    🔍 Registro de quién hizo cada venta
🔧 Sin seguimiento al taller   →    📁 Módulo de Casos de Servicio completo
📄 Facturas a mano             →    🧾 Factura generada automáticamente
```

---

## 🔵 P1 — Bloque 2: Arquitectura y roles (2:00 – 4:30)

### Guión sugerido

> "Técnicamente, BQRINES es una aplicación web construida con Spring Boot 3.3,
> usando el patrón MVC clásico. La interfaz se renderiza en el servidor con Thymeleaf,
> los datos persisten en PostgreSQL, y la seguridad la maneja Spring Security con BCrypt.
>
> No hay API REST — todo es HTML renderizado en el servidor, lo que simplifica
> el despliegue y la seguridad. Un solo `.jar` con `bootRun` y el sistema está corriendo."

### Diagrama de arquitectura

```
                     ┌─────────────────────────────────┐
                     │         NAVEGADOR WEB            │
                     │  (Bootstrap 5 + FontAwesome)     │
                     └────────────┬────────────────────┘
                                  │ HTTP (form POST / GET)
                     ┌────────────▼────────────────────┐
                     │       SPRING BOOT 3.3            │
                     │  ┌──────────────────────────┐   │
                     │  │     Controllers (MVC)    │   │
                     │  │  @PreAuthorize por rol   │   │
                     │  └───────────┬──────────────┘   │
                     │  ┌───────────▼──────────────┐   │
                     │  │        Services           │   │
                     │  │  @Transactional / lógica │   │
                     │  └───────────┬──────────────┘   │
                     │  ┌───────────▼──────────────┐   │
                     │  │    Repositories (JPA)    │   │
                     │  └───────────┬──────────────┘   │
                     └─────────────-┼──────────────────┘
                                    │
                     ┌──────────────▼──────────────────┐
                     │         PostgreSQL               │
                     │  11 tablas · auto DDL · soft     │
                     │  delete · audit_logs             │
                     └─────────────────────────────────┘
```

### Diagrama de roles (lo que puede hacer cada uno)

```
┌──────────────────────────────────────────────────────────────────┐
│                         3 ROLES DEL SISTEMA                      │
├──────────────────┬───────────────────────┬───────────────────────┤
│   GERENTE        │   ASESOR COMERCIAL    │  SERVICIO TÉCNICO     │
├──────────────────┼───────────────────────┼───────────────────────┤
│ ✅ Inventario    │ ✅ Ver inventario      │ ❌ Sin inventario      │
│    CRUD completo │    (solo lectura)      │                       │
├──────────────────┼───────────────────────┼───────────────────────┤
│ ✅ Ve TODAS      │ ✅ Sus propias ventas  │ ❌ Sin ventas          │
│    las ventas    │    (no las de otros)  │                       │
├──────────────────┼───────────────────────┼───────────────────────┤
│ ✅ Reportes PDF  │ ❌ Sin reportes        │ ❌ Sin reportes        │
│    y Excel       │                       │                       │
├──────────────────┼───────────────────────┼───────────────────────┤
│ ✅ Usuarios      │ ❌ Sin usuarios        │ ❌ Sin usuarios        │
│    CRUD          │                       │                       │
├──────────────────┼───────────────────────┼───────────────────────┤
│ ✅ Ver todos     │ ✅ Gestionar repuestos │ ✅ CREAR y actualizar  │
│    los casos     │    + CERRAR casos      │    casos              │
├──────────────────┼───────────────────────┼───────────────────────┤
│ ✅ Ver clientes  │ ❌ Sin clientes        │ ✅ CRUD de clientes    │
│    (solo lectura)│                       │    y vehículos        │
└──────────────────┴───────────────────────┴───────────────────────┘
```

### Punto técnico clave para mencionar

> "La seguridad no es solo visual — si un SERVICIO_TECNICO intenta entrar a `/inventory/vehicles`
> por URL directa, el sistema retorna HTTP 403. Cada endpoint tiene `@PreAuthorize` en el controlador,
> y el navbar oculta los enlaces con `sec:authorize` de Thymeleaf Security."

---

## 🟢 P2 — Bloque 3: Inventario + Ventas + Reportes (4:30 – 8:00)

### Guión sugerido

> "El núcleo original del sistema tiene dos grandes módulos: inventario y ventas.
> Voy a mostrarles cómo funcionan y qué decisiones técnicas tomamos."

### Sub-bloque: Inventario (4:30 – 6:00)

#### Diagrama — Ciclo de vida de un ítem de inventario

```
                    ┌─────────────────────┐
                    │   CREAR REPUESTO /  │
                    │   VEHÍCULO          │
                    └──────────┬──────────┘
                               │
                    ┌──────────▼──────────┐
                    │  @PrePersist:       │
                    │  createdAt = now()  │
                    │  syncActive()       │◄─── Si stock ≤ 0 → active=false
                    └──────────┬──────────┘
                               │
              ┌────────────────┼────────────────┐
              │                │                │
    ┌─────────▼──┐   ┌─────────▼──┐   ┌────────▼───────┐
    │ stock > 3  │   │ stock 1-3  │   │   stock = 0    │
    │  🟢 OK     │   │ 🟠 BAJO    │   │  🔴 SIN STOCK  │
    └────────────┘   └────────────┘   └────────────────┘
                              │                │
                    ┌─────────▼────────────────▼────┐
                    │   ALERTA en Dashboard          │
                    │   NotificationService (DB)     │
                    └───────────────────────────────┘

    ELIMINAR → Soft Delete: deleted_at = now()
               @SQLRestriction filtra automáticamente
               La fila NUNCA se borra físicamente
```

#### Puntos clave para mencionar

- **Soft delete**: `@SQLRestriction("deleted_at IS NULL")` — todos los queries auto-excluyen eliminados. No se necesita filtro manual en ningún lugar.
- **Auto-desactivación**: `@PreUpdate` en la entidad — si stock llega a 0 en cualquier operación, se desactiva solo, sin lógica en el servicio.
- **Detección de duplicados**: placa única (vehículos), referencia única (repuestos) — validado en el controlador antes de guardar.

---

### Sub-bloque: Ventas (6:00 – 7:00)

#### Diagrama — Flujo de una venta

```
ASESOR COMERCIAL en /sells/new

  Panel izquierdo              Panel derecho
  ┌──────────────────┐         ┌──────────────────────────┐
  │  Buscar producto │         │  CARRITO (en memoria JS) │
  │  [Vehículo]      │──add──► │  Ítem 1: Filtro aceite   │
  │  [Repuesto]      │         │  Ítem 2: Llanta 205/65   │
  │  [Cargo extra]   │         │  Ítem 3: Mano de obra    │
  └──────────────────┘         └──────────┬───────────────┘
                                          │ submit (hidden arrays)
                               ┌──────────▼───────────────┐
                               │  SellController.save()   │
                               │  @RequestParam List<>    │
                               └──────────┬───────────────┘
                                          │
                               ┌──────────▼───────────────┐
                               │  SellService             │
                               │  @Transactional          │
                               │  - Descuenta stock        │
                               │  - Skips extras (type)   │
                               │  - Suma total             │
                               │  - InsufficientStock? ➜  │
                               │    ROLLBACK completo     │
                               └──────────┬───────────────┘
                                          │
                               ┌──────────▼───────────────┐
                               │  /sells/voucher/{id}     │
                               │  Comprobante imprimible  │
                               └──────────────────────────┘
```

#### Punto técnico clave

> "Si en una venta de 5 ítems el ítem número 4 no tiene stock suficiente,
> la transacción entera hace rollback. Los primeros 3 ítems no se descuentan.
> Eso lo garantiza `@Transactional` en `SellService`."

---

### Sub-bloque: Reportes (7:00 – 8:00)

```
GERENTE en /reports

  ┌─────────────────────────────────────────────┐
  │  Filtro por fechas: [desde] – [hasta]       │
  │  ──────────────────────────────────────────  │
  │  Tabla de ventas con items inline           │
  │  Total por venta · Quién la registró        │
  │                                             │
  │  [ ⬇ Exportar PDF ]  [ ⬇ Exportar Excel ]  │
  └─────────────────────────────────────────────┘
          │                       │
  OpenPDF 1.3.30          Apache POI 5.2.5
  (com.github.librepdf)   (poi-ooxml)

  Los botones actualizan su href en tiempo real
  con JS cada vez que cambia el filtro de fechas
```

---

## 🟠 P3 — Bloque 4: Módulo de Casos de Servicio (8:00 – 12:30)

### Guión sugerido

> "Este es el módulo más complejo del sistema y el que más valor le agrega al taller.
> Antes, cuando un cliente traía un vehículo, no había forma de rastrear qué repuestos
> se habían pedido, si estaban disponibles, o cuánto cobrar al final.
> Nosotros implementamos un flujo completo de 3 actores."

### Diagrama — El flujo completo de un caso

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

ACTOR: SERVICIO TÉCNICO

  1. Va a /clients → busca por placa del vehículo
     ┌────────────────────────────────────────────────┐
     │  ¿La placa existe?                             │
     │  SI → muestra cliente + historial              │
     │  NO → formulario para registrar cliente nuevo  │
     └────────────────────────────────────────────────┘

  2. Clic en "Crear Caso" → llena descripción del problema
     → Caso creado con estado: [RECIBIDO]

  3. En el caso, el técnico puede:
     ┌────────────────────────────────────────────────┐
     │  + Agregar repuestos solicitados               │
     │    (incluye los que tienen stock = 0)          │
     │  + Escribir notas técnicas                     │
     │  + Setear mano de obra: descripción + precio   │
     │  + Cambiar estado del caso                     │
     └────────────────────────────────────────────────┘

  4. Cuando termina → cambia estado a [LISTO]

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

ACTOR: ASESOR COMERCIAL

  5. Ve en /cases el caso con estado LISTO
     → Botón "Cerrar" aparece

  6. En el detalle del caso:
     → Marca cada repuesto: [DISPONIBLE] o [SIN_STOCK]

  7. Clic "Cerrar caso y generar factura"
     ┌────────────────────────────────────────────────┐
     │  Formulario pre-cargado con:                   │
     │  · Repuestos DISPONIBLES                       │
     │  · Cargo de mano de obra (del técnico)         │
     │  · Posibilidad de agregar cargos extra          │
     └────────────────────────────────────────────────┘

  8. Al confirmar (POST /cases/{id}/close):
     ┌────────────────────────────────────────────────┐
     │  SellService.registerSell()                    │
     │    → Descuenta stock de repuestos              │
     │    → Registra la venta en BD                   │
     │                                                │
     │  ServiceCaseService.close()                    │
     │    → Vincula sellId al caso                    │
     │    → Estado: [ENTREGADO]                       │
     │    → Items DISPONIBLE → ENTREGADO (auto)       │
     └────────────────────────────────────────────────┘
     → Redirige a /sells/voucher/{id}
       (mismo comprobante que una venta directa)

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

ACTOR: GERENTE

  → Ve todos los casos en cualquier momento
  → Acceso completo al detalle
  → No puede crear ni cerrar casos (solo visualiza)

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

### Diagrama — Estados de un caso

```
                        RECIBIDO
                           │
                    ┌──────▼──────┐
                    │EN_DIAGNOSTICO│
                    └──────┬──────┘
                           │
                   ┌───────▼────────┐
                   │ESPERANDO_      │
                   │REPUESTOS       │
                   └───────┬────────┘
                           │
                    ┌──────▼──────┐
                    │EN_REPARACION│
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │    LISTO    │◄── Técnico marca "listo"
                    └──────┬──────┘    Asesor ve botón "Cerrar"
                           │
                    ┌──────▼──────┐
                    │  ENTREGADO  │◄── Solo asesor puede cerrar
                    └─────────────┘    Genera factura automáticamente
```

### Diagrama — Estados de un repuesto dentro del caso

```
    Técnico agrega repuesto
            │
       [SOLICITADO]
            │
     Asesor lo revisa
       ┌────┴────┐
       │         │
  [DISPONIBLE] [SIN_STOCK]
       │
  Al cerrar caso:
  auto → [ENTREGADO]
```

### Puntos técnicos clave para mencionar

> "Algo importante: el técnico puede solicitar repuestos con stock = 0.
> InventoryService.findAllSpares() devuelve TODOS los repuestos activos,
> incluyendo los sin stock, porque el técnico necesita documentar lo que pidió
> aunque no esté disponible aún. El asesor después lo marca como SIN_STOCK."

> "Al cerrar el caso, el asesor en realidad está creando una Venta normal del sistema —
> reutilizamos exactamente la misma infraestructura de SellService que usa la venta directa.
> El caso queda vinculado a esa venta por el campo sellId."

---

## 🟠 P3 — Bloque 5: Seguridad, Auditoría y Decisiones (12:30 – 14:00)

### Guión sugerido

> "Para cerrar, les cuento tres aspectos técnicos que consideramos importantes
> desde el diseño: la seguridad real (no solo visual), el historial auditable,
> y una decisión arquitectónica que cambió el enfoque del proyecto."

### Seguridad — 2 capas

```
CAPA 1: Interfaz (Thymeleaf Security)
  → sec:authorize oculta menús según rol
  → El usuario nunca ve enlaces a lo que no puede hacer

CAPA 2: Backend (@PreAuthorize)
  → Cada endpoint tiene su anotación
  → Si alguien intenta la URL directa: HTTP 403
  → Ejemplo:
      @GetMapping("/inventory/vehicles")
      @PreAuthorize("hasAnyRole('GERENTE','ASESOR_COMERCIAL')")
      ← SERVICIO_TECNICO → 403 Forbidden

  → En casos:
      /cases/new         → solo SERVICIO_TECNICO
      /cases/{id}/close  → solo ASESOR_COMERCIAL
      /cases/{id}        → todos, pero técnico solo ve los suyos
```

### Auditoría — Trazabilidad completa

```
  Cada CRUD en inventario genera una entrada en audit_logs:

  ┌────────────────────────────────────────────────────┐
  │  Fecha: 2026-05-20 14:32                           │
  │  Acción: EDITAR                                    │
  │  Ítem: Filtro de aceite (ID: 42)                   │
  │  Cambio: Stock: 10 → 8 | Precio: $12.000 → $14.000 │
  │  Realizado por: asesor@taller.com                   │
  └────────────────────────────────────────────────────┘

  ⚠ AuditLogService usa Propagation.REQUIRES_NEW
    → El log SE GUARDA aunque la transacción padre falle
    → Si una venta hace rollback, el intento queda registrado

  Las ventas que descuentan stock se registran con:
    performedBy = "sistema" (no el usuario)
```

### Decisión arquitectónica clave

```
  DOCUMENTO ORIGINAL decía:  "Aplicación de escritorio instalable"
                                           │
                                     CAMBIAMOS A:
                                           │
  IMPLEMENTADO:               Aplicación web (Spring Boot MVC)

  RAZONES:
  ✅ Un solo servidor → todos los roles acceden desde su PC
  ✅ Sin instalación en cada máquina
  ✅ Actualizaciones centralizadas (solo el servidor)
  ✅ Base de datos compartida sin sincronización manual
```

---

## 🔵 P1 — Cierre y conclusión (14:00 – 15:00)

### Guión sugerido

> "En resumen, BQRINES resuelve el problema de gestión del taller con:
> - Un control de inventario inteligente que se auto-gestiona
> - Un sistema de ventas multi-ítem con trazabilidad completa
> - Un módulo de casos de servicio que conecta al técnico, el asesor y el gerente
> - Reportes exportables en PDF y Excel
> - Seguridad real en 2 capas
> - Un historial auditable de todos los cambios
>
> Todo sobre una sola aplicación web que con un comando está corriendo."

### Números del proyecto

```
┌─────────────────────────────────────────────────────┐
│              BQRINES en números                      │
├─────────────────────────────────────────────────────┤
│  55  clases Java (controllers, services, models...) │
│  11  tablas en PostgreSQL                           │
│  3   roles con permisos distintos                   │
│  20  requisitos funcionales cubiertos               │
│  10  casos de uso implementados                     │
│  6   estados posibles de un caso de servicio        │
│  2   formatos de exportación (PDF y Excel)          │
│  0   instalación requerida en los clientes          │
└─────────────────────────────────────────────────────┘
```

---

## Preguntas frecuentes que pueden hacer (y cómo responder)

### ¿Por qué no usaron una API REST + frontend separado?

> "Decidimos usar server-side rendering con Thymeleaf porque el proyecto es un sistema
> interno de empresa, no una app pública. Simplifica el despliegue (un solo JAR),
> elimina la necesidad de gestionar autenticación en dos capas separadas, y reduce
> la superficie de ataque de seguridad."

### ¿Qué pasa si dos asesores venden el mismo ítem al mismo tiempo?

> "PostgreSQL y `@Transactional` garantizan serialización. Si dos transacciones
> intentan decrementar el mismo stock simultáneamente, una esperará a que la otra
> termine. Si el stock no alcanza después de la primera, la segunda lanza
> `InsufficientStockException` y hace rollback completo."

### ¿Cómo garantizan que el técnico no accede a ventas?

> "En dos niveles: el navbar no muestra el enlace (Thymeleaf Security `sec:authorize`),
> y si intenta la URL directa `/inventory/vehicles`, el controlador tiene
> `@PreAuthorize(\"hasAnyRole('GERENTE','ASESOR_COMERCIAL')\")` que retorna 403
> antes de ejecutar cualquier lógica."

### ¿Qué es el soft delete?

> "En lugar de borrar físicamente, ponemos `deleted_at = ahora()` en el registro.
> La anotación `@SQLRestriction(\"deleted_at IS NULL\")` en la entidad hace que
> TODOS los queries de JPA automáticamente excluyan esos registros.
> Esto nos da historial: podemos recuperar datos borrados si fuera necesario."

### ¿Por qué el técnico puede pedir repuestos sin stock?

> "Porque en un taller real, el técnico necesita documentar lo que requiere para el trabajo,
> aunque el asesor tenga que conseguirlo. El asesor luego marca cada repuesto como
> DISPONIBLE (lo tiene) o SIN_STOCK (hay que pedirlo). Solo los DISPONIBLES
> se incluyen en la factura final y descuentan stock."

---

## Flujo de demo sugerido (si tienen acceso en vivo)

```
1. Login como GERENTE → mostrar dashboard con estadísticas

2. Ir a Inventario → Vehículos → mostrar lista con colores de stock
   → Editar uno → cambiar stock a 0 → ver auto-desactivación

3. Login como SERVICIO_TECNICO
   → Clientes → Buscar placa "ABC123"
   → Crear caso → descripción del problema
   → En el caso: agregar repuesto, setear mano de obra "$50.000"
   → Cambiar estado a LISTO

4. Login como ASESOR_COMERCIAL
   → Cases → ver el caso con botón "Cerrar"
   → Marcar repuesto DISPONIBLE
   → Cerrar caso → ver factura pre-cargada con mano de obra
   → Confirmar → Comprobante generado

5. Login como GERENTE
   → /history → mostrar log del cambio de stock
   → /reports → filtrar por fecha → Exportar PDF
```

---

## Resumen visual del sistema completo

```
                        ┌─────────────────────────────────────┐
                        │            B Q R I N E S             │
                        │   Sistema de Gestión de Taller       │
                        └───────────────┬─────────────────────┘
                                        │
            ┌───────────────────────────┼──────────────────────────┐
            │                           │                          │
    ┌───────▼────────┐         ┌────────▼───────┐        ┌────────▼───────┐
    │   INVENTARIO   │         │     VENTAS     │        │    SERVICIO    │
    │                │         │                │        │    TÉCNICO     │
    │ · Vehículos    │         │ · Multi-ítem   │        │                │
    │ · Repuestos    │         │ · Carrito JS   │        │ · Clientes     │
    │ · Stock auto   │         │ · Comprobante  │        │ · Vehículos    │
    │ · Soft delete  │         │ · Trazabilidad │        │ · Casos        │
    │ · Alertas      │         │ · por asesor   │        │ · Repuestos    │
    └───────┬────────┘         └────────┬───────┘        │ · Mano obra    │
            │                          │                 └────────┬───────┘
            │                          │                          │
            └──────────────────────────┼──────────────────────────┘
                                       │
                           ┌───────────▼──────────────┐
                           │   FUNCIONES TRANSVERSALES │
                           │                           │
                           │  🔒 Seguridad (2 capas)  │
                           │  📋 Auditoría (diff-log)  │
                           │  📊 Reportes PDF/Excel    │
                           │  🔑 Cambio de contraseña  │
                           │  📬 Solicitudes internas  │
                           └───────────────────────────┘
```
