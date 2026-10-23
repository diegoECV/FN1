from flask import Blueprint
from app.services.ventas_service import ventas_service
from app.utils.response import success_response, error_response
from app.config.cache import cache

ventas_bp = Blueprint('ventas', __name__)

@ventas_bp.route('/resumen', methods=['GET'])
@cache.cached(timeout=300) # Mantiene el resultado en memoria por 5 minutos
def get_resumen():
    """
    Obtener resumen analítico de ventas
    ---
    tags:
      - Ventas Analítica
    responses:
      200:
        description: KPI de ventas, órdenes y facturación total
    """
    resumen = ventas_service.get_resumen()
    
    if "error" in resumen:
        return error_response(resumen["error"], 500)
        
    return success_response(resumen)

@ventas_bp.route('/mensual', methods=['GET'])
@cache.cached(timeout=300)
def get_mensual():
    """
    Obtener evolución mensual de ventas y facturación
    ---
    tags:
      - Ventas Analítica
    responses:
      200:
        description: Serie temporal de ventas agrupadas por año y mes
    """
    data = ventas_service.get_mensual()
    if isinstance(data, dict) and "error" in data:
        return error_response(data["error"], 500)
    return success_response(data)

