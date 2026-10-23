from app.repositories.estudiantes_repo import estudiantes_repo
from app.schemas.estudiantes_schema import (
    serialize_diego_indicadores,
    serialize_camila_indicadores,
    serialize_lizeth_indicadores
)

class EstudiantesService:
    def get_diego(self) -> dict:
        model = estudiantes_repo.get_diego_indicadores()
        return serialize_diego_indicadores(model)

    def get_camila(self) -> dict:
        model = estudiantes_repo.get_camila_indicadores()
        return serialize_camila_indicadores(model)

    def get_lizeth(self) -> dict:
        model = estudiantes_repo.get_lizeth_indicadores()
        return serialize_lizeth_indicadores(model)

    def get_todos(self) -> dict:
        return {
            "instituto": "Instituto de Educación Superior Vallegrande",
            "carrera": "Arquitectura de Plataformas y Servicios de Tecnologías de Información",
            "evaluacion": "Evaluación Intermedia - Proyectos de Innovación Tecnológica (Rúbrica 2.5 pts)",
            "estudiantes": [
                self.get_diego(),
                self.get_camila(),
                self.get_lizeth()
            ]
        }

estudiantes_service = EstudiantesService()
