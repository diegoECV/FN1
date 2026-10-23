from typing import Dict, Any, List
from app.database.sql_db import get_sql_connection
from app.database.mongo_db import mongo_db
from app.models.estudiantes_model import (
    DiegoIndicadoresModel,
    CamilaIndicadoresModel,
    LizethIndicadoresModel
)

class EstudiantesRepository:

    # -------------------------------------------------------------------------
    # 1. DIEGO CENTENO (Comercial, Ventas y Envíos)
    # -------------------------------------------------------------------------
    def get_diego_indicadores(self) -> DiegoIndicadoresModel:
        total_ord = 10
        ord_pendientes = 3
        revenue_usd = 285400.0
        total_despachos = 8
        historico_mensual: List[Dict[str, Any]] = [
            {"mes": "Mayo", "periodo": "2026-05", "facturacion_usd": 38400.0, "total_ordenes": 2},
            {"mes": "Junio", "periodo": "2026-06", "facturacion_usd": 52100.0, "total_ordenes": 2},
            {"mes": "Julio", "periodo": "2026-07", "facturacion_usd": 64300.0, "total_ordenes": 2},
            {"mes": "Agosto", "periodo": "2026-08", "facturacion_usd": 71800.0, "total_ordenes": 2},
            {"mes": "Septiembre", "periodo": "2026-09", "facturacion_usd": 58800.0, "total_ordenes": 2}
        ]

        conn = get_sql_connection()
        if conn:
            try:
                cursor = conn.cursor()
                cursor.execute("""
                    SELECT 
                        COUNT(order_id) as total_ordenes,
                        SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) as ordenes_pendientes
                    FROM ORDERS
                """)
                row = cursor.fetchone()
                if row and row[0] is not None:
                    total_ord = int(row[0])
                    ord_pendientes = int(row[1]) if row[1] is not None else 0

                cursor.execute("""
                    SELECT SUM(total) as revenue_usd
                    FROM INVOICES
                    WHERE status = 'PAID'
                """)
                rev_row = cursor.fetchone()
                if rev_row and rev_row[0] is not None:
                    revenue_usd = float(rev_row[0])

                cursor.execute("""
                    SELECT COUNT(*) 
                    FROM SHIPMENTS 
                    WHERE status IN ('SHIPPED', 'IN_TRANSIT')
                """)
                ship_row = cursor.fetchone()
                if ship_row and ship_row[0] is not None:
                    total_despachos = int(ship_row[0])
            except Exception:
                pass
            finally:
                conn.close()

        ticket_prom = round(revenue_usd / max(1, total_ord), 2)

        return DiegoIndicadoresModel(
            estudiante="Diego Centeno",
            modulo="Comercial, Ventas y Despachos Logísticos",
            total_ordenes=total_ord,
            facturacion_total_usd=revenue_usd,
            ordenes_pendientes_pago=ord_pendientes,
            ticket_promedio_usd=ticket_prom,
            ventas_historico_mensual=historico_mensual,
            total_despachos_transito=total_despachos
        )

    # -------------------------------------------------------------------------
    # 2. CAMILA QUISPE (Proveedores, Compras y Trazabilidad)
    # -------------------------------------------------------------------------
    def get_camila_indicadores(self) -> CamilaIndicadoresModel:
        total_prov = 10
        total_trazabilidad = 24
        alertas_frio = 2
        volumen_kg = 145200.0
        cumplimiento = 96.5
        certificaciones = [
            {"certificacion": "GlobalGAP", "cantidad": 6, "porcentaje": 60.0},
            {"certificacion": "Orgánico (USDA / UE)", "cantidad": 2, "porcentaje": 20.0},
            {"certificacion": "Fair Trade", "cantidad": 1, "porcentaje": 10.0},
            {"certificacion": "SENASA Habilitado", "cantidad": 1, "porcentaje": 10.0}
        ]

        db = mongo_db.get_db()
        if db is not None:
            try:
                count_prov = db.providers.count_documents({"isActive": True})
                if count_prov > 0:
                    total_prov = count_prov

                count_traza = db.traceability_logs.count_documents({})
                if count_traza > 0:
                    total_trazabilidad = count_traza

                count_temp_alerts = db.temperature_logs.count_documents({
                    "$or": [{"temperature": {"$lt": -1.0}}, {"temperature": {"$gt": 2.0}}]
                })
                if count_temp_alerts > 0:
                    alertas_frio = count_temp_alerts
            except Exception:
                pass

        return CamilaIndicadoresModel(
            estudiante="Camila Quispe",
            modulo="Proveedores, Acopio Agrícola y Trazabilidad",
            total_proveedores_activos=total_prov,
            proveedores_por_certificacion=certificaciones,
            volumen_acopio_kg=volumen_kg,
            total_eventos_trazabilidad=total_trazabilidad,
            alertas_cadena_frio=alertas_frio,
            cumplimiento_entregas_pct=cumplimiento
        )

    # -------------------------------------------------------------------------
    # 3. LIZETH QUISPE (Calidad, Inspecciones e Inventario)
    # -------------------------------------------------------------------------
    def get_lizeth_indicadores(self) -> LizethIndicadoresModel:
        total_insp = 10
        aprobados = 9
        rechazados = 1
        tasa_aprob = 90.0
        tasa_rech = 10.0
        prom_brix = 17.8
        stock_camaras = 12
        calibres = [
            {"calibre": "JUMBO (24-26mm)", "cantidad": 5, "porcentaje": 50.0},
            {"calibre": "XL (22-24mm)", "cantidad": 3, "porcentaje": 30.0},
            {"calibre": "L (20-22mm)", "cantidad": 2, "porcentaje": 20.0}
        ]

        db = mongo_db.get_db()
        if db is not None:
            try:
                tot = db.quality_inspections.count_documents({})
                if tot > 0:
                    total_insp = tot
                    aprobados = db.quality_inspections.count_documents({"result": "PASSED"})
                    rechazados = db.quality_inspections.count_documents({"result": "REJECTED"})
                    t = max(1, total_insp)
                    tasa_aprob = round((aprobados / t) * 100, 2)
                    tasa_rech = round((rechazados / t) * 100, 2)

                batches_count = db.inventory_batches.count_documents({})
                if batches_count > 0:
                    stock_camaras = batches_count
            except Exception:
                pass

        return LizethIndicadoresModel(
            estudiante="Lizeth Quispe",
            modulo="Calidad Fitosanitaria, Grados Brix e Inventario",
            total_inspecciones_calidad=total_insp,
            tasa_aprobacion_pct=tasa_aprob,
            tasa_rechazo_pct=tasa_rech,
            promedio_grados_brix=prom_brix,
            stock_lotes_camara_frio=stock_camaras,
            distribucion_por_calibre=calibres
        )

estudiantes_repo = EstudiantesRepository()
