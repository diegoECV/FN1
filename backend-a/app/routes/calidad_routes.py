from flask import Blueprint
from app.services.calidad_service import calidad_service
from app.utils.response import success_response, error_response

calidad_bp = Blueprint('calidad', __name__)

@calidad_bp.route('/resumen', methods=['GET'])
def get_resumen():
    """
    Obtener resumen analítico de calidad
    ---
    tags:
      - Calidad Analítica
    responses:
      200:
        description: KPI de control de calidad
    """
    resumen = calidad_service.get_resumen()
    if "error" in resumen:
        return error_response(resumen["error"], 500)
    return success_response(resumen)

@calidad_bp.route('/distribucion', methods=['GET'])
def get_distribucion():
    """
    Obtener distribución porcentual de inspecciones de calidad
    ---
    tags:
      - Calidad Analítica
    responses:
      200:
        description: Distribución de lotes aprobados, rechazados y pendientes con porcentajes
    """
    data = calidad_service.get_distribucion()
    if isinstance(data, dict) and "error" in data:
        return error_response(data["error"], 500)
    return success_response(data)

