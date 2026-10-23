from flask import Blueprint
from app.services.inventario_service import inventario_service
from app.utils.response import success_response, error_response

inventario_bp = Blueprint('inventario', __name__)

@inventario_bp.route('/resumen', methods=['GET'])
def get_resumen():
    """
    Obtener resumen analítico de inventario
    ---
    tags:
      - Inventario Analítica
    responses:
      200:
        description: KPI de inventario (SQL y Mongo)
    """
    resumen = inventario_service.get_resumen()
    if "error" in resumen:
        return error_response(resumen["error"], 500)
    return success_response(resumen)
