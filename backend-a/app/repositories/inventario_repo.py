from app.database.mongo_db import mongo_db
from app.database.sql_db import get_sql_connection

class InventarioRepository:
    def get_inventario_resumen(self):
        # Mongo data
        total_mongo = 0
        db = mongo_db.get_db()
        if db is not None:
            try:
                total_mongo = db.inventory_batches.count_documents({})
            except Exception as e:
                pass
        
        # SQL Server data
        total_sql = 0
        conn = get_sql_connection()
        if conn:
            try:
                cursor = conn.cursor()
                cursor.execute("SELECT COUNT(*) as total FROM BATCHES")
                row = cursor.fetchone()
                total_sql = row[0] if row and row[0] is not None else 0
            except Exception as e:
                pass
            finally:
                conn.close()
                
        return {
            "lotes_en_inventario_mongo": total_mongo,
            "lotes_produccion_sql": total_sql
        }

inventario_repo = InventarioRepository()
