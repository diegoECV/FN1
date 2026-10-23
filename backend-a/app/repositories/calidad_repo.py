from app.database.mongo_db import mongo_db

class CalidadRepository:
    def get_calidad_resumen(self):
        db = mongo_db.get_db()
        if db is None: return {"error": "Sin conexión a MongoDB"}
        
        try:
            total = db.quality_inspections.count_documents({})
            aprobados = db.quality_inspections.count_documents({"result": "PASSED"})
            rechazados = db.quality_inspections.count_documents({"result": "REJECTED"})
            
            return {
                "total_inspecciones": total,
                "lotes_aprobados": aprobados,
                "lotes_rechazados": rechazados
            }
        except Exception as e:
            return {"error": f"Error consultando calidad en MongoDB: {str(e)}"}

    def get_calidad_distribucion(self):
        db = mongo_db.get_db()
        if db is None:
            return {"error": "Sin conexión a MongoDB"}
        
        try:
            total = db.quality_inspections.count_documents({})
            aprobados = db.quality_inspections.count_documents({"result": "PASSED"})
            rechazados = db.quality_inspections.count_documents({"result": "REJECTED"})
            pendientes = max(0, total - (aprobados + rechazados))
            
            t = total if total > 0 else 1
            return {
                "total_inspecciones": total,
                "tasa_aprobacion_pct": round((aprobados / t) * 100, 2) if total > 0 else 0.0,
                "tasa_rechazo_pct": round((rechazados / t) * 100, 2) if total > 0 else 0.0,
                "distribucion": [
                    {"estado": "Aprobados", "cantidad": aprobados, "porcentaje": round((aprobados / t) * 100, 2) if total > 0 else 0.0},
                    {"estado": "Rechazados", "cantidad": rechazados, "porcentaje": round((rechazados / t) * 100, 2) if total > 0 else 0.0},
                    {"estado": "Pendientes", "cantidad": pendientes, "porcentaje": round((pendientes / t) * 100, 2) if total > 0 else 0.0}
                ]
            }
        except Exception as e:
            return {"error": f"Error consultando distribución de calidad: {str(e)}"}

calidad_repo = CalidadRepository()
