from flask import Blueprint
from app.services.dashboard_service import dashboard_service
from app.utils.response import success_response, error_response
from app.config.cache import cache

dashboard_bp = Blueprint('dashboard', __name__)

@dashboard_bp.route('/kpis', methods=['GET'])
@cache.cached(timeout=60) # Cache en memoria por 60 segundos
def get_dashboard_kpis():
    """
    Obtener resumen ejecutivo consolidado para el Dashboard General
    ---
    tags:
      - Dashboard General
    responses:
      200:
        description: KPIs consolidados de comercial, logística, calidad, inventario y seguridad
    """
    try:
        data = dashboard_service.get_kpis_consolidados()
        return success_response(data)
    except Exception as e:
        return error_response(f"Error generando dashboard: {str(e)}", 500)
