from flask import Blueprint
from app.services.estudiantes_service import estudiantes_service
from app.utils.response import success_response, error_response

estudiantes_bp = Blueprint('estudiantes', __name__)

@estudiantes_bp.route('/diego', methods=['GET'])
def get_diego_indicadores():
    """
    6 Indicadores Funcionales - Diego Centeno (Comercial, Ventas y Envíos)
    ---
    tags:
      - 🎓 Rúbrica Innovación Tecnológica (Indicadores por Estudiante)
    description: |
      Retorna los 6 indicadores funcionales correspondientes a Diego Centeno:
      1. total_ordenes: Volumen total de órdenes B2B registradas.
      2. facturacion_total_usd: Ingresos acumulados en USD por facturas cobradas.
      3. ordenes_pendientes_pago: Pedidos pendientes de pago en cobranza.
      4. ticket_promedio_usd: Facturación promedio por orden de exportación.
      5. total_despachos_transito: Contenedores marítimos en tránsito internacional.
      6. ventas_historico_mensual: Serie de facturación mensual y volumen comercial.
    responses:
      200:
        description: 6 indicadores funcionales calculados exitosamente
    """
    try:
        data = estudiantes_service.get_diego()
        return success_response(data)
    except Exception as e:
        return error_response(f"Error calculando indicadores de Diego: {str(e)}", 500)


@estudiantes_bp.route('/camila', methods=['GET'])
def get_camila_indicadores():
    """
    6 Indicadores Funcionales - Camila Quispe (Proveedores, Acopio y Trazabilidad)
    ---
    tags:
      - 🎓 Rúbrica Innovación Tecnológica (Indicadores por Estudiante)
    description: |
      Retorna los 6 indicadores funcionales correspondientes a Camila Quispe:
      1. total_proveedores_activos: Padrón de proveedores agrícolas habilitados.
      2. proveedores_por_certificacion: Distribución fitosanitaria (GlobalGAP, Orgánico, SENASA).
      3. volumen_acopio_kg: Kilos totales de uva cosechada recepcionados en planta.
      4. total_eventos_trazabilidad: Logs de seguimiento de origen a destino en MongoDB.
      5. alertas_cadena_frio: Desviaciones térmicas detectadas en contenedores de frío.
      6. cumplimiento_entregas_pct: Tasa de cumplimiento de entregas a tiempo por productores.
    responses:
      200:
        description: 6 indicadores funcionales calculados exitosamente
    """
    try:
        data = estudiantes_service.get_camila()
        return success_response(data)
    except Exception as e:
        return error_response(f"Error calculando indicadores de Camila: {str(e)}", 500)


@estudiantes_bp.route('/lizeth', methods=['GET'])
def get_lizeth_indicadores():
    """
    6 Indicadores Funcionales - Lizeth Quispe (Calidad, Grados Brix e Inventario)
    ---
    tags:
      - 🎓 Rúbrica Innovación Tecnológica (Indicadores por Estudiante)
    description: |
      Retorna los 6 indicadores funcionales correspondientes a Lizeth Quispe:
      1. total_inspecciones_calidad: Total de controles de calidad ejecutados en lotes.
      2. tasa_aprobacion_pct: Porcentaje de fruta calificada apta para exportación.
      3. tasa_rechazo_pct: Porcentaje de fruta rechazada por defectos fitosanitarios.
      4. promedio_grados_brix: Promedio de dulzor (°Bx) de la uva de exportación.
      5. stock_lotes_camara_frio: Lotes disponibles en cámara refrigerada de conservación.
      6. distribucion_por_calibre: Distribución de la fruta por calibre (Jumbo, XL, L).
    responses:
      200:
        description: 6 indicadores funcionales calculados exitosamente
    """
    try:
        data = estudiantes_service.get_lizeth()
        return success_response(data)
    except Exception as e:
        return error_response(f"Error calculando indicadores de Lizeth: {str(e)}", 500)


@estudiantes_bp.route('/todos', methods=['GET'])
def get_todos_indicadores():
    """
    Consolidado Oficial de los 18 Indicadores Funcionales (Diego, Camila y Lizeth)
    ---
    tags:
      - 🎓 Rúbrica Innovación Tecnológica (Indicadores por Estudiante)
    description: |
      Consolidado de los 18 indicadores funcionales (6 por cada uno de los 3 estudiantes)
      para la sustentación y validación directa ante el jurado evaluador.
    responses:
      200:
        description: Consolidado de los 18 indicadores de los 3 estudiantes
    """
    try:
        data = estudiantes_service.get_todos()
        return success_response(data)
    except Exception as e:
        return error_response(f"Error generando consolidado de indicadores: {str(e)}", 500)
