def serialize_trazabilidad_resumen(model) -> dict:
    return {
        "total_eventos_registrados": model.total_eventos_registrados
    }
