from flask import Blueprint
from app.services.clientes_service import clientes_service
from app.utils.response import success_response, error_response

clientes_bp = Blueprint('clientes', __name__)

@clientes_bp.route('/resumen', methods=['GET'])
def get_resumen():
    """
    Obtener resumen analítico de clientes
    ---
    tags:
      - Clientes Analítica
    responses:
      200:
        description: KPI de clientes
    """
    resumen = clientes_service.get_resumen()
    if "error" in resumen:
        return error_response(resumen["error"], 500)
    return success_response(resumen)
