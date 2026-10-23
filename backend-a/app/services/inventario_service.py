from app.repositories.inventario_repo import inventario_repo

class InventarioService:
    def get_resumen(self):
        return inventario_repo.get_inventario_resumen()

inventario_service = InventarioService()
