// ============================================================
// config.ts — Configuración central de URLs y constantes
// Todas las URLs del backend se leen desde variables de entorno
// (.env). Nunca pongas IPs hardcodeadas en el código.
// ============================================================

// URL base del Backend 1 (Spring Boot — puerto 9090 en dev-2.0)
export const API_URL = process.env.EXPO_PUBLIC_API_URL ?? 'http://localhost:9090/api/v1';

// URL base del Backend 2 Analítico (Flask — puerto 5000)
export const BA_URL = process.env.EXPO_PUBLIC_BA_URL ?? 'http://localhost:5000/api/v1';

// Timeout en ms para todas las peticiones
export const REQUEST_TIMEOUT = Number(process.env.EXPO_PUBLIC_TIMEOUT ?? 10000);

// Endpoints del BE (Spring Boot)
export const ENDPOINTS = {
  // Auth
  LOGIN:   '/auth/login',
  LOGOUT:  '/auth/logout',
  REGISTER: '/auth/register',

  // Usuarios
  USERS:   '/users',
  USER_BY_ID: (id: string) => `/users/${id}`,
  USER_TOGGLE: (id: string) => `/users/${id}/toggle-status`,
  USER_APPROVE: (id: string) => `/users/${id}/approve`,

  // Clientes
  CLIENTS: '/clients',
  CLIENT_BY_ID: (id: string) => `/clients/${id}`,

  // Proveedores
  PROVIDERS: '/providers',
  PROVIDER_BY_ID: (id: string) => `/providers/${id}`,

  // Productos
  PRODUCTS: '/products',
  PRODUCT_BY_ID: (id: string) => `/products/${id}`,

  // Categorías
  CATEGORIES: '/categories',

  // Órdenes
  ORDERS: '/orders',
  ORDER_BY_ID: (id: string) => `/orders/${id}`,

  // Compras
  PURCHASES: '/purchases',

  // Inventario
  INVENTORY: '/inventory-batches',

  // Embarques
  SHIPMENTS: '/shipments',
} as const;

// Endpoints del BA (Flask Analítico — Corregidos según API real de Flask)
export const BA_ENDPOINTS = {
  HEALTH:        '/health',
  DASHBOARD:     '/analytics/dashboard/kpis',
  SALES:         '/analytics/ventas/resumen',
  SALES_MONTHLY: '/analytics/ventas/mensual',
  CLIENTS:       '/analytics/clientes/resumen',
  PROVIDERS:     '/analytics/proveedores/resumen',
  INVENTORY:     '/analytics/inventario/resumen',
  QUALITY:       '/analytics/calidad/resumen',
  SHIPMENTS:     '/analytics/envios/resumen',
  TRACEABILITY:  '/analytics/trazabilidad/resumen',
  AUDIT:         '/analytics/audit/resumen',
} as const;
