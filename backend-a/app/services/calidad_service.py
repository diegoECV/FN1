from app.repositories.calidad_repo import calidad_repo

class CalidadService:
    def get_resumen(self):
        return calidad_repo.get_calidad_resumen()

    def get_distribucion(self):
        return calidad_repo.get_calidad_distribucion()

calidad_service = CalidadService()
