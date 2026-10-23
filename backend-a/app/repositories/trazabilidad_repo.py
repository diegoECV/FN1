from app.database.mongo_db import mongo_db

class TrazabilidadRepository:
    def get_trazabilidad_resumen(self):
        db = mongo_db.get_db()
        if db is None: return {"error": "Sin conexión a MongoDB"}
        
        try:
            total = db.traceability_logs.count_documents({})
            return {
                "total_eventos_registrados": total
            }
        except Exception as e:
            return {"error": f"Error consultando trazabilidad en MongoDB: {str(e)}"}

trazabilidad_repo = TrazabilidadRepository()
