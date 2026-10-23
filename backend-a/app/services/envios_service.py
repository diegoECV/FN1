from app.repositories.envios_repo import envios_repo

class EnviosService:
    def get_resumen(self):
        return envios_repo.get_envios_resumen()

envios_service = EnviosService()
