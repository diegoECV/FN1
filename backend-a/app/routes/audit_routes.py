from flask import Blueprint
from app.services.audit_service import audit_service
from app.utils.response import success_response, error_response
from app.config.cache import cache

audit_bp = Blueprint('audit_bp', __name__)

@audit_bp.route('/resumen', methods=['GET'])
@cache.cached(timeout=60) # Cache por 60 segundos
def get_audit_resumen():
    """
    Obtener resumen analítico de logs de auditoría
    ---
    tags:
      - Auditoría Analítica
    responses:
      200:
        description: KPI y métricas de uso del sistema y seguridad
        schema:
          type: object
          properties:
            success:
              type: boolean
            message:
              type: string
            data:
              type: object
      500:
        description: Error interno o de conexión
    """
    data, error = audit_service.get_resumen()
    if error:
        return error_response(error)
    return success_response(data=data)
