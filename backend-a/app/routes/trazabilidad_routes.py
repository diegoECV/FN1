from flask import Blueprint
from app.services.trazabilidad_service import trazabilidad_service
from app.utils.response import success_response, error_response

trazabilidad_bp = Blueprint('trazabilidad', __name__)

@trazabilidad_bp.route('/resumen', methods=['GET'])
def get_resumen():
    """
    Obtener resumen analítico de trazabilidad
    ---
    tags:
      - Trazabilidad Analítica
    responses:
      200:
        description: KPI de trazabilidad y logs
    """
    resumen = trazabilidad_service.get_resumen()
    if "error" in resumen:
        return error_response(resumen["error"], 500)
    return success_response(resumen)
