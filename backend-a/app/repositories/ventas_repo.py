from app.database.sql_db import get_sql_connection
from app.models.ventas_model import VentasResumenModel

class VentasRepository:
    
    def get_ventas_resumen(self) -> VentasResumenModel:
        conn = get_sql_connection()
        if not conn:
            return None # Manejado en el servicio
        
        try:
            cursor = conn.cursor()
            
            # Consultamos métricas básicas de órdenes
            cursor.execute("""
                SELECT 
                    COUNT(order_id) as total_ordenes,
                    SUM(CASE WHEN status = 'PAID' THEN 1 ELSE 0 END) as ordenes_pagadas,
                    SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) as ordenes_pendientes
                FROM ORDERS
            """)
            row = cursor.fetchone()
            
            # Calculamos el revenue total desde las facturas pagadas
            cursor.execute("""
                SELECT SUM(total) as revenue_usd
                FROM INVOICES
                WHERE status = 'PAID'
            """)
            revenue_row = cursor.fetchone()
            
            total_ord = row[0] if row and row[0] is not None else 0
            ord_pagadas = row[1] if row and row[1] is not None else 0
            ord_pendientes = row[2] if row and row[2] is not None else 0
            rev_val = float(revenue_row[0]) if revenue_row and revenue_row[0] is not None else 0.0

            return VentasResumenModel(
                total_ordenes=total_ord,
                ordenes_pagadas=ord_pagadas,
                ordenes_pendientes=ord_pendientes,
                revenue_usd=rev_val
            )
            
        finally:
            conn.close()

    def get_ventas_mensual(self):
        conn = get_sql_connection()
        if not conn:
            return {"error": "Sin conexión a SQL Server"}
        
        try:
            cursor = conn.cursor()
            cursor.execute("""
                SELECT 
                    CONVERT(VARCHAR(7), order_date, 120) as periodo,
                    COUNT(order_id) as total_ordenes,
                    SUM(CASE WHEN status = 'PAID' THEN 1 ELSE 0 END) as ordenes_pagadas,
                    SUM(CASE WHEN status = 'PENDING' THEN 1 ELSE 0 END) as ordenes_pendientes
                FROM ORDERS
                WHERE order_date IS NOT NULL
                GROUP BY CONVERT(VARCHAR(7), order_date, 120)
                ORDER BY periodo ASC
            """)
            rows = cursor.fetchall()
            
            # Consultar facturación agrupada por mes
            cursor.execute("""
                SELECT 
                    CONVERT(VARCHAR(7), issue_date, 120) as periodo,
                    SUM(total) as facturacion_usd
                FROM INVOICES
                WHERE issue_date IS NOT NULL
                GROUP BY CONVERT(VARCHAR(7), issue_date, 120)
            """)
            inv_dict = {r[0]: float(r[1]) if r[1] is not None else 0.0 for r in cursor.fetchall()}
            
            mensual = []
            for r in rows:
                p = r[0]
                mensual.append({
                    "periodo": p,
                    "total_ordenes": r[1] if r[1] is not None else 0,
                    "ordenes_pagadas": r[2] if r[2] is not None else 0,
                    "ordenes_pendientes": r[3] if r[3] is not None else 0,
                    "facturacion_usd": inv_dict.get(p, 0.0)
                })
            return mensual
        finally:
            conn.close()

ventas_repo = VentasRepository()

