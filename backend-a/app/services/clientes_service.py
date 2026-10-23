from app.repositories.clientes_repo import clientes_repo

class ClientesService:
    def get_resumen(self):
        return clientes_repo.get_clientes_resumen()

clientes_service = ClientesService()
