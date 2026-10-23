from app.database.sql_db import get_sql_connection

class EnviosRepository:
    def get_envios_resumen(self):
        conn = get_sql_connection()
        if not conn: return {"error": "Sin conexión a SQL Server"}
        try:
            cursor = conn.cursor()
            cursor.execute("""
                SELECT 
                    COUNT(*) as total, 
                    SUM(CASE WHEN status='SHIPPED' THEN 1 ELSE 0 END) as enviados 
                FROM SHIPMENTS
            """)
            row = cursor.fetchone()
            total_env = row[0] if row and row[0] is not None else 0
            enviados = row[1] if row and row[1] is not None else 0
            return {
                "total_envios": total_env,
                "envios_en_transito": enviados
            }
        finally:
            conn.close()

envios_repo = EnviosRepository()
