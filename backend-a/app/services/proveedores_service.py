from app.repositories.proveedores_repo import proveedores_repo

class ProveedoresService:
    def get_resumen(self):
        return proveedores_repo.get_proveedores_resumen()

proveedores_service = ProveedoresService()
