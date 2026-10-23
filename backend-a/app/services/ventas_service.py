from app.repositories.ventas_repo import ventas_repo
from app.schemas.ventas_schema import serialize_ventas_resumen

class VentasService:
    
    def get_resumen(self):
        model = ventas_repo.get_ventas_resumen()
        if not model:
            return {"error": "Sin conexión a BD"}
        return serialize_ventas_resumen(model)

    def get_mensual(self):
        return ventas_repo.get_ventas_mensual()

ventas_service = VentasService()
