from flask import Blueprint
from app.services.proveedores_service import proveedores_service
from app.utils.response import success_response, error_response

proveedores_bp = Blueprint('proveedores', __name__)

@proveedores_bp.route('/resumen', methods=['GET'])
def get_resumen():
    """
    Obtener resumen analítico de proveedores
    ---
    tags:
      - Proveedores Analítica
    responses:
      200:
        description: KPI de proveedores
    """
    resumen = proveedores_service.get_resumen()
    if "error" in resumen:
        return error_response(resumen["error"], 500)
    return success_response(resumen)
