from app.database.mongo_db import mongo_db

class ClientesRepository:
    def get_clientes_resumen(self):
        db = mongo_db.get_db()
        if db is None: return {"error": "Sin conexión a MongoDB"}
        
        try:
            total = db.clients.count_documents({})
            return {
                "total_clientes_registrados": total
            }
        except Exception as e:
            return {"error": f"Error consultando clientes en MongoDB: {str(e)}"}

clientes_repo = ClientesRepository()
