# 📊 Backend Analítico - Visons

Este es el **Backend Analítico (`backend-a`)** del proyecto Visons. Está desarrollado en **Python (Flask)** y tiene como propósito principal extraer, calcular y exponer métricas de negocio, KPIs y resúmenes estadísticos cruzando datos entre nuestras bases de datos transaccionales (**SQL Server** y **MongoDB**).

A diferencia del backend principal (Java/Spring Boot) que se encarga de procesar las transacciones y reglas del negocio, este backend está optimizado para consultas de lectura pesadas y reportes analíticos.

---

## 🏗️ Arquitectura y Estructura

El proyecto sigue una arquitectura limpia basada en capas para mantener el código ordenado y modular:

```text
backend-a/
├── app/
│   ├── config/          # Carga de variables de entorno (.env)
│   ├── database/        # Conexiones a SQL Server (pyodbc) y MongoDB (pymongo)
│   ├── models/          # Clases (Dataclasses) para tipado estricto de las respuestas
│   ├── schemas/         # Serialización (convierte Modelos en JSON para las respuestas)
│   ├── repositories/    # Consultas directas a las BDs (Lógica de SQL y NoSQL)
│   ├── services/        # Lógica de negocio, cálculos y orquestación
│   ├── routes/          # Endpoints HTTP / Controladores de Flask
│   └── main.py          # Archivo principal: inicializa Flask, CORS y Swagger
├── .env.example         # Ejemplo de variables de entorno requeridas
├── requirements.txt     # Dependencias del proyecto
└── README.md            # Este archivo de documentación
```

---

## 🧩 Módulos Analíticos

La API está dividida en módulos que agrupan los 27 modelos de datos del ecosistema:

1. **Dashboard General** (`/dashboard/kpis`): Resumen ejecutivo consolidado que unifica en una sola llamada los KPIs comerciales, logísticos, de calidad, inventario, seguridad y maestros.
2. **Ventas**:
   - `/ventas/resumen`: KPIs de facturación, ingresos en USD, órdenes pagadas vs pendientes. (SQL Server)
   - `/ventas/mensual`: Serie temporal de ventas y facturación agrupada por año y mes (ideal para gráficos de evolución).
3. **Envíos** (`/envios/resumen`): Volumen de despachos, estatus de tránsito y declaraciones aduaneras. (SQL Server)
4. **Calidad**:
   - `/calidad/resumen`: Lotes inspeccionados, aprobados vs rechazados. (MongoDB)
   - `/calidad/distribucion`: Porcentajes y distribución de calidad (tasa de aprobación/rechazo).
5. **Inventario** (`/inventario/resumen`): Stock físico disponible. (Híbrido: SQL Server + MongoDB)
6. **Clientes** (`/clientes/resumen`): Total de clientes registrados. (MongoDB)
7. **Proveedores** (`/proveedores/resumen`): Padrón de proveedores y certificaciones. (MongoDB)
8. **Trazabilidad** (`/trazabilidad/resumen`): Logs de seguimiento integral de los productos. (MongoDB)
9. **Auditoría** (`/audit/resumen`): Métricas de auditoría, logins recientes y acciones agrupadas por recurso. (SQL Server)

---

## 🚀 Instalación y Configuración local

### 1. Requisitos Previos
* **Python 3.10+** (Se recomienda instalar las librerías del sistema: `python3-devel`, `unixODBC-devel` y compiladores para `pyodbc`).
* Las bases de datos (`database-p`) deben estar corriendo.

### 2. Entorno Virtual y Dependencias
Ejecuta los siguientes comandos en tu terminal desde la carpeta `backend-a`:

```bash
# Crear entorno virtual
python3 -m venv venv

# Activar entorno virtual
source venv/bin/activate

# Instalar dependencias
pip install -r requirements.txt
```

### 3. Variables de Entorno
Debes crear un archivo `.env` en la raíz del proyecto analítico (puedes usar `.env.example` como base).

```bash
cp .env.example .env
```

Asegúrate de configurar los parámetros correctos de SQL Server y MongoDB:
```env
MONGO_URI=mongodb://admin:adminpassword@192.168.49.1:27017/Visons?authSource=admin&directConnection=true
MONGO_DB_NAME=Visons

SQL_SERVER=192.168.49.1
SQL_PORT=1433
SQL_DATABASE=Visons
SQL_USER=sa
SQL_PASSWORD=Password123!

FLASK_PORT=5000
FLASK_DEBUG=True
```

---

## ▶️ Ejecución

### Entorno de Desarrollo (Local)
Para desarrollo y pruebas en tu laptop, inicia el servidor nativo:
```bash
# Asegúrate de estar en backend-a y con el venv activado
export PYTHONPATH=$PYTHONPATH:$(pwd)
python3 app/main.py
```

### Entorno de Producción (Gunicorn)
En producción **NUNCA** debes usar `python app/main.py`. Debes utilizar Gunicorn para habilitar el multi-hilo, lo que permitirá atender a múltiples clientes a la vez sin que se cuelgue la aplicación.

Ejecuta este comando para levantar el servidor con 4 procesos paralelos (Workers):
```bash
# Asegúrate de estar en backend-a y con el venv activado
export PYTHONPATH=$PYTHONPATH:$(pwd)
gunicorn -w 4 -b 0.0.0.0:5000 wsgi:app
```

El servidor arrancará en `http://localhost:5000`.

---

## 📖 Documentación de la API (Swagger)

Este proyecto incluye **Flasgger (Swagger UI)** integrado nativamente. Cuando el servidor esté corriendo, puedes visualizar, probar e interactuar con todos los endpoints desde una interfaz gráfica accediendo a:

👉 **[http://localhost:5000/apidocs/](http://localhost:5000/apidocs/)**
