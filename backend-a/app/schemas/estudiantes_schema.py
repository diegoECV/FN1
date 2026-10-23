from app.models.estudiantes_model import (
    DiegoIndicadoresModel,
    CamilaIndicadoresModel,
    LizethIndicadoresModel
)

def serialize_diego_indicadores(model: DiegoIndicadoresModel) -> dict:
    return {
        "estudiante": model.estudiante,
        "modulo": model.modulo,
        "indicadores": {
            "total_ordenes": model.total_ordenes,
            "facturacion_total_usd": model.facturacion_total_usd,
            "ordenes_pendientes_pago": model.ordenes_pendientes_pago,
            "ticket_promedio_usd": model.ticket_promedio_usd,
            "total_despachos_transito": model.total_despachos_transito,
            "ventas_historico_mensual": model.ventas_historico_mensual
        }
    }

def serialize_camila_indicadores(model: CamilaIndicadoresModel) -> dict:
    return {
        "estudiante": model.estudiante,
        "modulo": model.modulo,
        "indicadores": {
            "total_proveedores_activos": model.total_proveedores_activos,
            "proveedores_por_certificacion": model.proveedores_por_certificacion,
            "volumen_acopio_kg": model.volumen_acopio_kg,
            "total_eventos_trazabilidad": model.total_eventos_trazabilidad,
            "alertas_cadena_frio": model.alertas_cadena_frio,
            "cumplimiento_entregas_pct": model.cumplimiento_entregas_pct
        }
    }

def serialize_lizeth_indicadores(model: LizethIndicadoresModel) -> dict:
    return {
        "estudiante": model.estudiante,
        "modulo": model.modulo,
        "indicadores": {
            "total_inspecciones_calidad": model.total_inspecciones_calidad,
            "tasa_aprobacion_pct": model.tasa_aprobacion_pct,
            "tasa_rechazo_pct": model.tasa_rechazo_pct,
            "promedio_grados_brix": model.promedio_grados_brix,
            "stock_lotes_camara_frio": model.stock_lotes_camara_frio,
            "distribucion_por_calibre": model.distribucion_por_calibre
        }
    }
