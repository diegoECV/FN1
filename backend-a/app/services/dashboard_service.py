from app.services.ventas_service import ventas_service
from app.services.envios_service import envios_service
from app.services.calidad_service import calidad_service
from app.services.inventario_service import inventario_service
from app.services.clientes_service import clientes_service
from app.services.proveedores_service import proveedores_service
from app.services.audit_service import audit_service

class DashboardService:
    def get_kpis_consolidados(self):
        ventas = ventas_service.get_resumen()
        envios = envios_service.get_resumen()
        calidad = calidad_service.get_distribucion()
        inventario = inventario_service.get_resumen()
        clientes = clientes_service.get_resumen()
        proveedores = proveedores_service.get_resumen()
        audit_data, _ = audit_service.get_resumen()

        return {
            "comercial": {
                "total_ordenes": ventas.get("total_ordenes", 0) if isinstance(ventas, dict) else 0,
                "ordenes_pendientes": ventas.get("ordenes_pendientes", 0) if isinstance(ventas, dict) else 0,
                "facturacion_total_usd": ventas.get("revenue_usd", 0.0) if isinstance(ventas, dict) else 0.0
            },
            "logistica": {
                "total_envios": envios.get("total_envios", 0) if isinstance(envios, dict) else 0,
                "envios_en_transito": envios.get("envios_en_transito", 0) if isinstance(envios, dict) else 0
            },
            "calidad": {
                "total_inspecciones": calidad.get("total_inspecciones", 0) if isinstance(calidad, dict) else 0,
                "tasa_aprobacion_pct": calidad.get("tasa_aprobacion_pct", 0.0) if isinstance(calidad, dict) else 0.0
            },
            "inventario": {
                "lotes_en_inventario": inventario.get("lotes_en_inventario_mongo", 0) if isinstance(inventario, dict) else 0,
                "lotes_en_produccion": inventario.get("lotes_produccion_sql", 0) if isinstance(inventario, dict) else 0
            },
            "seguridad": {
                "total_auditorias": audit_data.get("total_logs", 0) if isinstance(audit_data, dict) else 0,
                "logins_recientes": audit_data.get("logins_recientes", 0) if isinstance(audit_data, dict) else 0
            },
            "maestros": {
                "total_clientes": clientes.get("total_clientes_registrados", 0) if isinstance(clientes, dict) else 0,
                "total_proveedores": proveedores.get("total_proveedores_activos", 0) if isinstance(proveedores, dict) else 0
            }
        }

dashboard_service = DashboardService()
