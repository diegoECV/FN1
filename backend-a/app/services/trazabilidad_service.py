from app.repositories.trazabilidad_repo import trazabilidad_repo

class TrazabilidadService:
    def get_resumen(self):
        return trazabilidad_repo.get_trazabilidad_resumen()

trazabilidad_service = TrazabilidadService()
