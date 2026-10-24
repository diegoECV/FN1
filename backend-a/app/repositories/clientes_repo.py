from app.database.mongo_db import mongo_db

class ClientesRepository:
    def get_clientes_resumen(self):
        db = mongo_db.get_db()
        if db is None:
            return {"error": "Sin conexión a MongoDB"}
        
        try:
            total = db.clients.count_documents({})
            activos = db.clients.count_documents({"$or": [{"isActive": True}, {"isActive": {"$exists": False}}]})
            inactivos = db.clients.count_documents({"isActive": False})
            
            # Distribución por país
            paises_cursor = db.clients.aggregate([
                {"$group": {"_id": "$country", "cantidad": {"$sum": 1}}},
                {"$sort": {"cantidad": -1}}
            ])
            distribucion_paises = [
                {"pais": doc["_id"] or "Sin País", "cantidad": doc["cantidad"]}
                for doc in paises_cursor
            ]
            
            # Distribución por Incoterm preferido
            incoterm_cursor = db.clients.aggregate([
                {"$group": {"_id": "$preferredIncoterm", "cantidad": {"$sum": 1}}},
                {"$sort": {"cantidad": -1}}
            ])
            distribucion_incoterms = [
                {"incoterm": doc["_id"] or "N/A", "cantidad": doc["cantidad"]}
                for doc in incoterm_cursor
            ]
            
            # Crédito total y promedio
            credito_cursor = list(db.clients.aggregate([
                {"$group": {"_id": None, "total": {"$sum": "$creditLimit"}, "promedio": {"$avg": "$creditLimit"}}}
            ]))
            credito_total = float(credito_cursor[0]["total"]) if credito_cursor and credito_cursor[0].get("total") else 0.0
            credito_promedio = round(float(credito_cursor[0]["promedio"]), 2) if credito_cursor and credito_cursor[0].get("promedio") else 0.0

            # Listado analítico de clientes
            clientes_cursor = db.clients.find({}, {
                "_id": 1, "companyName": 1, "company_name": 1, "contactName": 1, 
                "country": 1, "creditLimit": 1, "preferredIncoterm": 1, "isActive": 1
            }).sort("creditLimit", -1)
            
            listado = []
            for c in clientes_cursor:
                listado.append({
                    "id": str(c["_id"]),
                    "empresa": c.get("companyName") or c.get("company_name") or "Sin Nombre",
                    "contacto": c.get("contactName") or "N/A",
                    "pais": c.get("country") or "N/A",
                    "limite_credito_usd": c.get("creditLimit") or 0,
                    "incoterm": c.get("preferredIncoterm") or "N/A",
                    "activo": c.get("isActive", True)
                })

            return {
                "total_clientes_registrados": total,
                "clientes_activos": activos,
                "clientes_inactivos": inactivos,
                "credito_total_autorizado_usd": credito_total,
                "credito_promedio_usd": credito_promedio,
                "distribucion_por_pais": distribucion_paises,
                "distribucion_por_incoterm": distribucion_incoterms,
                "top_clientes": listado
            }
        except Exception as e:
            return {"error": f"Error consultando clientes en MongoDB: {str(e)}"}

clientes_repo = ClientesRepository()
