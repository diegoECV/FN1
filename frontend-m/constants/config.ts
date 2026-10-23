// =============================================================================
// CONFIGURACIÓN DE ENDPOINTS Y CONECTIVIDAD — FRONTEND MÓVIL (EXPO / REACT NATIVE)
// VISONS S.A.C. — Agroexport Management Platform
// =============================================================================
// Nota para emuladores y dispositivos móviles:
// - Android Emulator (Genymotion/AVD): usa 10.0.2.2 en lugar de localhost
// - iOS Simulator: usa localhost
// - Dispositivo físico en red Wi-Fi: usa la IP privada de tu PC (ej: 192.168.1.X)
// - AWS / Producción: usa la Elastic IP (EIP) con los NodePorts 30090 y 30500
// =============================================================================

import { Platform } from 'react-native';

const getLocalHost = (): string => {
  if (Platform.OS === 'android') {
    return '10.0.2.2';
  }
  return 'localhost';
};

const DEFAULT_HOST = getLocalHost();

export const API_CONFIG = {
  // Backend Transaccional (Spring Boot WebFlux - Java 25)
  // Puerto Local: 9090 | Puerto K8s NodePort: 30090
  CORE_BASE_URL: `http://${DEFAULT_HOST}:9090/api/v1`,

  // Backend Analítico (Python 3.12 Flask)
  // Puerto Local: 5000 | Puerto K8s NodePort: 30500
  // Todas las rutas en Flask cuelgan de /api/v1/analytics
  ANALYTICS_BASE_URL: `http://${DEFAULT_HOST}:5000/api/v1/analytics`,

  TIMEOUT: 15000,
};

// Endpoints del Backend Analítico (Alineados exactamente con las rutas de Flask)
export const BA_ENDPOINTS = {
  // Dashboard General Consolidado
  DASHBOARD_KPIS: '/dashboard/kpis',

  // Ventas y Facturación
  VENTAS_RESUMEN: '/ventas/resumen',
  VENTAS_MENSUAL: '/ventas/mensual',

  // Envíos y Despachos
  ENVIOS_RESUMEN: '/envios/resumen',

  // Calidad de Lotes
  CALIDAD_RESUMEN: '/calidad/resumen',
  CALIDAD_DISTRIBUCION: '/calidad/distribucion',

  // Inventario Físico
  INVENTARIO_RESUMEN: '/inventario/resumen',

  // Clientes y Proveedores (MongoDB)
  CLIENTES_RESUMEN: '/clientes/resumen',
  PROVEEDORES_RESUMEN: '/proveedores/resumen',

  // Trazabilidad y Auditoría
  TRAZABILIDAD_RESUMEN: '/trazabilidad/resumen',
  AUDIT_RESUMEN: '/audit/resumen',

  // Estudiantes / Grupo de Trabajo
  ESTUDIANTES_RESUMEN: '/estudiantes/resumen',
} as const;

// Endpoints del Backend Transaccional
export const BT_ENDPOINTS = {
  // Autenticación
  LOGIN: '/auth/login',
  REGISTER: '/auth/register',
  PROFILE: '/auth/me',

  // Pedidos
  ORDERS: '/orders',
  ORDER_DETAILS: '/order-details',

  // Auditoría
  AUDIT_LOGS: '/audit-logs',
} as const;
