def serialize_envios_resumen(model) -> dict:
    return {
        "total_envios": model.total_envios,
        "envios_en_transito": model.envios_en_transito
    }
