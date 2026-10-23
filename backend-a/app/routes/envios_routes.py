from flask import Blueprint
from app.services.envios_service import envios_service
from app.utils.response import success_response, error_response

envios_bp = Blueprint('envios', __name__)

@envios_bp.route('/resumen', methods=['GET'])
def get_resumen():
    """
    Obtener resumen analítico de envíos
    ---
    tags:
      - Envíos Analítica
    responses:
      200:
        description: KPI de envíos (totales, en tránsito)
    """
    resumen = envios_service.get_resumen()
    if "error" in resumen:
        return error_response(resumen["error"], 500)
    return success_response(resumen)
