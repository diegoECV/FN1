def serialize_calidad_resumen(model) -> dict:
    return {
        "total_inspecciones": model.total_inspecciones,
        "lotes_aprobados": model.lotes_aprobados,
        "lotes_rechazados": model.lotes_rechazados
    }
