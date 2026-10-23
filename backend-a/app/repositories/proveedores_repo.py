from app.database.mongo_db import mongo_db

class ProveedoresRepository:
    def get_proveedores_resumen(self):
        db = mongo_db.get_db()
        if db is None: return {"error": "Sin conexión a MongoDB"}
        
        try:
            total = db.providers.count_documents({})
            return {
                "total_proveedores_activos": total
            }
        except Exception as e:
            return {"error": f"Error consultando proveedores en MongoDB: {str(e)}"}

proveedores_repo = ProveedoresRepository()
