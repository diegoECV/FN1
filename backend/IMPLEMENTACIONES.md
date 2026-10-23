# 📋 Documentación de Implementaciones — Módulo de Seguridad y Usuarios

> Documento técnico personal del módulo de Seguridad. Aquí registro las tablas y colecciones que ya tengo funcionando, las que estoy desarrollando y las que pienso agregar más adelante para completar el sistema de una empresa agroexportadora.

---

## ✅ Implementaciones Completadas

### 🟢 `ORDERS` — Pedidos de Exportación (SQL Server)

Esta es mi tabla principal transaccional. La migré desde MongoDB a SQL Server porque los pedidos son datos críticos que necesitan consistencia relacional.

| Campo | Tipo | Descripción |
|---|---|---|
| `order_id` | INT (PK) | Identificador único autogenerado del pedido |
| `client_id` | VARCHAR | Referencia lógica al cliente en MongoDB (`clients`) |
| `order_code` | VARCHAR | Código legible del pedido, ej. `ORD-2026-001` |
| `incoterm` | CHAR(3) | Condición de entrega acordada: FOB, CIF, EXW |
| `status` | VARCHAR | Estado del pedido: PENDING, CONFIRMED, SHIPPED, DELIVERED |
| `order_date` | DATE | Fecha en que se creó el pedido |

---

### 🟢 `ORDER_DETAILS` — Detalle de Pedidos (SQL Server)

Esta tabla acompaña a `ORDERS`. Aquí guardo cada producto que va dentro de un pedido. Un pedido puede tener varios detalles (palta, arándanos, uvas, etc.).

| Campo | Tipo | Descripción |
|---|---|---|
| `order_detail_id` | INT (PK) | Identificador único del detalle |
| `order_id` | INT (FK) | Relación con la tabla ORDERS (en cascada) |
| `product_id` | VARCHAR | Referencia lógica al producto en MongoDB (`products`) |
| `batch_id` | VARCHAR | Referencia lógica al lote en MongoDB (`inventory_batches`) |
| `quantity_kg` | DECIMAL | Cantidad en kilogramos solicitada |
| `unit_price` | DECIMAL | Precio por kg en USD acordado con el cliente |

---

### 🟢 `AUDIT_LOGS` — Logs de Auditoría (SQL Server)

Esta es mi tabla de auditoría. Cada vez que alguien en el sistema hace algo importante (crear un usuario, modificar un pedido, eliminar un cliente), se guarda un registro aquí. Es clave para la trazabilidad y la seguridad del sistema.

| Campo | Tipo | Descripción |
|---|---|---|
| `id` | BIGINT (PK) | Identificador único del evento de auditoría |
| `user_id` | VARCHAR | ID del usuario que realizó la acción (referencia a MongoDB) |
| `username` | VARCHAR | Nombre del usuario en el momento de la acción (snapshot) |
| `action` | VARCHAR | Tipo de acción: CREATE, UPDATE, DELETE, LOGIN, LOGOUT |
| `resource` | VARCHAR | Entidad afectada: users, orders, clients, products |
| `resource_id` | VARCHAR | ID del recurso específico que fue modificado |
| `timestamp` | DATETIME2 | Fecha y hora exacta del evento |
| `changes_before` | NVARCHAR(MAX) | Estado del registro antes de la modificación (JSON) |
| `changes_after` | NVARCHAR(MAX) | Estado del registro después de la modificación (JSON) |
| `ip_address` | VARCHAR | Dirección IP desde donde se realizó la acción |

---

### 🟢 `PAYMENTS` — Registro de Pagos (SQL Server)

Registro cada transacción de pago por separado, ya que un pedido puede recibir múltiples pagos parciales.

| Campo | Tipo | Descripción |
|---|---|---|
| `payment_id` | INT (PK) | ID único del pago |
| `order_id` | INT (FK) | Pedido al que corresponde este pago |
| `amount` | DECIMAL | Monto de este pago específico en USD |
| `payment_date` | DATETIME2 | Fecha en que se recibió el pago |
| `payment_method` | VARCHAR | Método: WIRE_TRANSFER, LETTER_OF_CREDIT, ADVANCE |
| `reference_code` | VARCHAR | Código de la transferencia bancaria o referencia |
| `notes` | NVARCHAR(MAX) | Comentarios adicionales del área de finanzas |

> **¿Por qué en SQL Server?** Porque los pagos son transacciones financieras y necesito consistencia total. Si algo falla a mitad de la operación, no quiero datos a medias.

---

### 🟢 `INVOICES` — Facturas de Exportación (SQL Server)

Cuando un pedido está confirmado, el área comercial genera una factura. Guardo esas facturas vinculadas a los pedidos para que el área contable pueda hacer el seguimiento.

| Campo | Tipo | Descripción |
|---|---|---|
| `invoice_id` | INT (PK) | ID único de la factura |
| `order_id` | INT (FK) | Pedido que origina esta factura |
| `invoice_number` | VARCHAR | Número correlativo de factura, ej. `FAC-2026-0042` |
| `issue_date` | DATETIME2 | Fecha de emisión de la factura |
| `due_date` | DATETIME2 | Fecha límite de pago |
| `currency` | VARCHAR | Moneda: USD, EUR |
| `subtotal` | DECIMAL | Monto antes de impuestos |
| `taxes` | DECIMAL | IGV u otros impuestos aplicables |
| `total` | DECIMAL | Total final a cobrar |
| `status` | VARCHAR | Estado: DRAFT, ISSUED, PAID, OVERDUE |

---

### 🟢 `SHIPMENTS` — Embarques (SQL Server)

Mi tabla estrella para logística. En agroexportación, el embarque es el evento más importante del proceso. Aquí registro cada contenedor que sale del Perú hacia el destino.

| Campo | Tipo | Descripción |
|---|---|---|
| `shipment_id` | INT (PK) | ID único del embarque |
| `order_id` | INT (FK) | Pedido que se está embarcando |
| `bl_number` | VARCHAR | Número de Bill of Lading (documento del barco) |
| `vessel_name` | VARCHAR | Nombre del buque, ej. MSC BARCELONA |
| `container_number` | VARCHAR | Número del contenedor, ej. MSCU1234567 |
| `departure_port` | VARCHAR | Puerto de salida: Callao, Paita, Matarani |
| `arrival_port` | VARCHAR | Puerto de llegada: Rotterdam, Los Ángeles, Shanghai |
| `departure_date` | DATETIME2 | Fecha de zarpe del buque |
| `estimated_arrival` | DATETIME2 | Fecha estimada de llegada (ETA) |
| `status` | VARCHAR | Estado: BOOKING, IN_TRANSIT, ARRIVED, DELIVERED |
| `temperature_setpoint` | DECIMAL | Temperatura del contenedor en °C (cadena de frío) |

---

### 🟢 `SHIPMENT_ITEMS` — Ítems del Embarque (SQL Server)

Detalle de los productos y su peso dentro de cada contenedor embarcado.

| Campo | Tipo | Descripción |
|---|---|---|
| `item_id` | INT (PK) | ID único del ítem |
| `shipment_id` | INT (FK) | Embarque al que pertenece |
| `product_id` | VARCHAR | Referencia lógica al producto en MongoDB |
| `quantity_pallets` | INT | Cantidad de pallets |
| `quantity_boxes` | INT | Cantidad de cajas |
| `net_weight_kg` | DECIMAL | Peso neto en kg |
| `gross_weight_kg` | DECIMAL | Peso bruto en kg |

---

### 🟢 `CERTIFICATES` — Certificados Fitosanitarios (SQL Server)

Cada lote que exporto necesita certificados legales. SENASA emite el certificado fitosanitario, y hay otros como el certificado orgánico o de residuos de pesticidas. Si un certificado vence, no puedo exportar.

| Campo | Tipo | Descripción |
|---|---|---|
| `certificate_id` | INT (PK) | ID único del certificado |
| `shipment_id` | INT (FK) | Embarque al que pertenece este certificado |
| `batch_id` | VARCHAR | Lote de inventario que cubre (referencia MongoDB) |
| `type` | VARCHAR | Tipo: PHYTOSANITARY, ORGANIC, PESTICIDE_RESIDUE, ORIGIN |
| `issued_by` | VARCHAR | Entidad emisora: SENASA, USDA, Global G.A.P. |
| `issue_date` | DATETIME2 | Fecha de emisión |
| `expiry_date` | DATETIME2 | Fecha de vencimiento |
| `certificate_number` | VARCHAR | Código oficial del certificado |
| `file_url` | NVARCHAR(MAX) | URL del PDF escaneado guardado en la nube |

---

### 🟢 `CUSTOMS_DECLARATIONS` — Declaraciones Aduaneras (SQL Server)

Registro de las Declaraciones Aduaneras de Mercancías (DAM) para cada embarque que sale del país.

| Campo | Tipo | Descripción |
|---|---|---|
| `declaration_id` | INT (PK) | ID único de la declaración |
| `shipment_id` | INT (FK) | Embarque al que pertenece |
| `dam_number` | VARCHAR | Número oficial de la DAM |
| `fob_value` | DECIMAL | Valor FOB declarado en aduana (USD) |
| `customs_agency` | VARCHAR | Agencia de aduana responsable |
| `filing_date` | DATETIME2 | Fecha de presentación ante aduana |
| `status` | VARCHAR | Estado: PENDING, FILED, APPROVED, REJECTED |

---

### 🟢 Colecciones en MongoDB (Maestros y Operaciones)

Estas colecciones las mantengo en MongoDB porque son datos que no cambian muy seguido o generan volúmenes masivos de registros con estructuras flexibles.

| Colección | Descripción |
|---|---|
| `users` | Datos de acceso: email, contraseña encriptada, rol asignado |
| `clients` | Empresas importadoras con las que trabajamos (RUC, país, contacto) |
| `providers` | Proveedores de campo que nos venden la fruta |
| `products` | Catálogo de productos de exportación (palta, arándanos, uva, etc.) |
| `categories` | Categorías de productos con jerarquía flexible |
| `inventory_batches` | Lotes de inventario con trazabilidad de campo |
| `email_verifications` | Tokens temporales para verificar correos de nuevos usuarios |
| `revoked_tokens` | Tokens JWT invalidados para cerrar sesiones de forma segura |
| `rate_limits` | Control de intentos de login para prevenir ataques de fuerza bruta |
| `quality_inspections` | Inspecciones de calidad por lote y producto. Esquema dinámico según cultivo |
| `traceability_logs` | Registro de eventos del lote a lo largo de toda la cadena logística |
| `temperature_logs` | Lecturas de sensores IoT de temperatura y humedad en contenedores |
| `price_lists` | Listas de precios escalonadas por cliente, producto e incoterm |

---

## 🗺️ Visión General del Flujo

```
Proveedor (MongoDB) → Lote (inventory_batches) → Producto (products)
                                ↓
Cliente (MongoDB) → Pedido (ORDERS) → Detalle (ORDER_DETAILS)
                                ↓
                  Embarque (SHIPMENTS) → Ítems (SHIPMENT_ITEMS)
                                ↓
         Certificados (CERTIFICATES) + Aduanas (CUSTOMS_DECLARATIONS)
                                ↓
              Factura (INVOICES) → Pagos (PAYMENTS)
                                ↓
Calidad (quality_inspections) + Temperatura (temperature_logs) + Trazabilidad (traceability_logs)
                                ↓
                 Todo auditado en → AUDIT_LOGS
```
